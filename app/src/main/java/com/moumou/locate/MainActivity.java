package com.moumou.locate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.moumou.locate.reminder.LocationReminder;
import com.moumou.locate.reminder.PlaceReminder;
import com.moumou.locate.reminder.Reminder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String email;

    private List<Reminder> reminderList;

    //FAB's
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminderList = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(this);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(this);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(this);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this,
                                               android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                                                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                 PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                                              new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                              Constants.RC_LOCATION);
            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                              Constants.RC_LOCATION);
        }

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext());

        email = sharedPreferences.getString("email", null);

        if (email == null) {
            startActivity(i);
        } else {
            Log.d("SHAREPREF", "email:" + email);

            Intent locationService = new Intent(this, LocationService.class);
            startService(locationService);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        closeFAB();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                //place reminder
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), Constants.RC_NEW_PLACE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                animateFAB();
                break;
            case R.id.fab2:
                //location reminder
                Intent i = new Intent(this, AddLocRemActivity.class);
                startActivityForResult(i, Constants.RC_NEW_LOC);
                animateFAB();
                break;
            case R.id.fab3:
                animateFAB();
                break;
        }
    }

    public void animateFAB() {

        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    private void closeFAB() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        double latitude;
        double longitude;
        String label;
        switch (requestCode) {
            case Constants.RC_NEW_LOC: {
                if (resultCode == Activity.RESULT_OK) {
                    latitude = data.getDoubleExtra(Constants.NEW_LAT, 400);
                    longitude = data.getDoubleExtra(Constants.NEW_LONG, 400);
                    label = data.getStringExtra(Constants.NEW_LABEL);
                    if (latitude != 400 && longitude != 400) {
                        Location l = new Location("");
                        l.setLatitude(latitude);
                        l.setLongitude(longitude);
                        reminderList.add(new LocationReminder(Constants.getActivitycounter(), label, l));
                    }
                }
                break;
            }
            case Constants.RC_NEW_PLACE: {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    reminderList.add(new PlaceReminder(Constants.getActivitycounter(), "bla", place));
                }
                break;
            }
        }
    }
}
