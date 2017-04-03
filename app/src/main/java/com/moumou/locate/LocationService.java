package com.moumou.locate;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
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
import com.moumou.locate.reminder.LocationReminder;
import com.moumou.locate.reminder.POIReminder;
import com.moumou.locate.reminder.Reminder;
import com.moumou.locate.reminder.WifiReminder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MouMou on 28-12-16.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    NotificationManager mNotificationManager;
    long minute = 60000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationCompat.Action.Builder mActionBuilder;
    private List<Place> mPlaceList;

    private List<Reminder> reminderList;

    @Override
    public void onCreate() {
        super.onCreate();

        setUp();
        mGoogleApiClient.connect();

        Log.d("SERVICE", "Starting Location Service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SERVICE", "Stopping Location Service");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void setUp() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_stat_location_on)
                    .setContentTitle("Locate!")
                    .setContentText("Hello World!");
            mNotificationBuilder.setVibrate(new long[]{1000, 1000});
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotificationBuilder.setSound(alarmSound);
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
        mLocationRequest = new LocationRequest().setInterval(4 * minute)
                //.setInterval(100)
                //.setFastestInterval(100)
                .setFastestInterval(10 * minute)
                .setSmallestDisplacement(50)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
        writeLocation(location);
        getPlaces();
        checkWifiReminder();
        Log.d("LOCATION", "[" + new Date() + "] new location:" + location.toString());
    }

    private void checkWifiReminder() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String SSID = wifiInfo.getSSID();
        if (reminderList != null) {
            for (Reminder r : reminderList) {
                if (r instanceof WifiReminder) {
                    WifiReminder wr = (WifiReminder) r;
                    if (SSID.equals(wr.getSSID())) {
                        makeNotification(wr);
                    }
                }
            }
        }
    }

    private synchronized boolean getPlaces() {
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
                                  String.format("Place '%s' has likelihood: %g with types: %s",
                                                placeLikelihood.getPlace().getName(),
                                                placeLikelihood.getLikelihood(),
                                                Constants.getPlaceTypesString(placeLikelihood.getPlace())));
                        }
                    }

                    mPlaceList = places;
                    Log.d("LOCATION", "Finished getting places from Google");
                    if (getFromStorage()) {
                        checkLocation();
                    }
                    likelyPlaces.release();
                }
            });

            return true;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
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

    private void checkLocation() {
        Log.d("LOC", "Checking Location");
        for (Reminder r : reminderList) {
            if (r instanceof POIReminder) {
                POIReminder pr = (POIReminder) r;
                for (Place p : mPlaceList) {
                    for (int j = 0; j < p.getPlaceTypes().size(); j++) {
                        int i = p.getPlaceTypes().get(j);
                        if (pr.getPlaceTypes().contains(i)) {
                            Log.d("LOC", "Matched location with " + p.getPlaceTypes().get(j));
                            pr.setMatchedType(i);
                            makeNotification(pr);
                            break;
                        }
                    }
                }
            } else if (r instanceof LocationReminder) {
                LocationReminder lr = (LocationReminder) r;
                Location l1 = new Location("");
                l1.setLatitude(lr.getLatitude());
                l1.setLongitude(lr.getLongitude());

                Location l2 = new Location("");
                l2.setLatitude(mCurrentLocation.getLatitude());
                l2.setLongitude(mCurrentLocation.getLongitude());

                if (l1.distanceTo(l2) < 50) {
                    Log.d("LOC", "Matched location with " + lr.getPlaceAddress());
                    makeNotification(lr);
                }
            }
        }
    }

    private synchronized boolean getFromStorage() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(Constants.REMINDER_FILE);
            ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            if (o instanceof List) {
                List list = (List) o;
                if (!list.isEmpty() && list.get(0) instanceof Reminder) {
                    Log.d("IO", "Read from storage:" + list.toString());
                    reminderList = (List<Reminder>) list;
                    return true;
                }
            }
            fis.close();
            ois.close();
            return false;
        } catch (FileNotFoundException e) {
            try {
                openFileOutput(Constants.REMINDER_FILE, MODE_PRIVATE);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException | IOException e) {
            Log.d("IO", "Couldn't read from storage");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void makeNotification(Reminder r) {
        mNotificationBuilder.setContentTitle(r.getLabel());
        mNotificationBuilder.setContentText(r.toNotificationString());

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                                                     0,
                                                     i,
                                                     PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder.setContentIntent(pi);

        PendingIntent reminderLaterAction = DelayNotificationActivity.getDismissIntent(r.getId(),
                                                                                       getApplicationContext());
        PendingIntent dismissAction = DismissNotificationActivity.getDismissIntent(r.getId(),
                                                                                   getApplicationContext());
        mNotificationBuilder.addAction(R.drawable.ic_alarm_black_24dp,
                                       getString(R.string.remind_later),
                                       reminderLaterAction);
        mNotificationBuilder.addAction(R.drawable.ic_cancel_black_24dp,
                                       getString(R.string.dismiss),
                                       dismissAction);

        //mActionBuilder = new NotificationCompat.Action.Builder(R.drawable.ic_alarm_black_24dp, getString(R.string.remind_later), );

        //NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        mNotificationManager.notify(r.getId(), mNotificationBuilder.build());
    }
}
