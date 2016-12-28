package com.moumou.locate;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MouMou on 28-12-16.
 */

public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location mCurrentLocation;

    private NotificationCompat.Builder mNotificationBuilder;
    NotificationManager mNotificationManager;

    private List<Place> mPlaceList;

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        setUp();
        mGoogleApiClient.connect();
    }

    private void setUp() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_add_white_24dp)
                    .setContentTitle("Locate!")
                    .setContentText("Hello World!");
        }
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                Log.d("LOCATION", "current location: " + mCurrentLocation.toString());
                writeLocation(mCurrentLocation);
            } else {
                Toast.makeText(this, "Cant get location", Toast.LENGTH_SHORT).show();
            }
            startLocationUpdates();
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

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 60000);
        mLocationRequest.setFastestInterval(5 * 60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(10);

        // Request location updates
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                                                     mLocationRequest,
                                                                     this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mPlaceList = getPlaces();
        writeLocation(location);
        Log.d("LOCATION", "[" + new Date() + "]" + location.toString());
    }

    private List<Place> getPlaces() {
        final List<Place> places = new ArrayList<>();
        try {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(
                    mGoogleApiClient,
                    null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                        if (placeLikelihood.getLikelihood() > 0.1) {
                            places.add(placeLikelihood.getPlace());
                            Log.i("PLACES",
                                  String.format("Place '%s' has likelihood: %g",
                                                placeLikelihood.getPlace().getName(),
                                                placeLikelihood.getLikelihood()));
                        }
                    }

                    likelyPlaces.release();
                }
            });

            return places;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeLocation(final Location location) {

        Log.d("IO", "writing location:" + location.toString());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Constants.putDouble(editor, Constants.TAG_LAT, location.getLatitude());
        Constants.putDouble(editor, Constants.TAG_LONG, location.getLongitude());
        editor.apply();
    }
}
