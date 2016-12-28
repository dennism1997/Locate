package com.moumou.locate;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.internal.PlaceEntity;
import com.moumou.locate.reminder.PlaceReminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MouMou on 28-12-16.
 */

public class AddPlaceRemActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Place selected;
    private List<Place> nearbyPlaces;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nearbyPlaces = new ArrayList<>();

        setUp();
        //        Button okButton = (Button) findViewById(R.id.loc_ok);
        //        okButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Intent result = new Intent();
        //                PlaceReminder placeReminder = new PlaceReminder(Constants.getActivitycounter(), null);
        //                result.putExtra(Constants.NEW_PLACE, placeReminder);
        //                setResult(Activity.RESULT_OK, result);
        //                finish();
        //            }
        //        });

    }

    private void setUp() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
    }

    /*
 * Create a new location client, using the enclosing class to
 * handle callbacks.
 */
    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            nearbyPlaces.clear();
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(
                    mGoogleApiClient,
                    null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                        if (placeLikelihood.getLikelihood() > 0.1) {
                            nearbyPlaces.add(placeLikelihood.getPlace());
                            Log.i("PLACES",
                                  String.format("Place '%s' has likelihood: %g",
                                                placeLikelihood.getPlace().getName(),
                                                placeLikelihood.getLikelihood()));
                        }
                    }

                    likelyPlaces.release();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_SHORT).show();
    }
}
