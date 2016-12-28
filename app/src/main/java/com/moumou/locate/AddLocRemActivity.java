package com.moumou.locate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

public class AddLocRemActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng location;
    private LatLng selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loc_rem);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map);
        mapFragment.getMapAsync(this);

        Button cancelButton = (Button) findViewById(R.id.loc_cancel);
        Button okButton = (Button) findViewById(R.id.loc_ok);
        final EditText editText = (EditText) findViewById(R.id.loc_label);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() > 0) {
                    Intent result = new Intent();
                    result.putExtra(Constants.NEW_LAT, selected.latitude);
                    result.putExtra(Constants.NEW_LONG, selected.longitude);
                    result.putExtra(Constants.NEW_LABEL, editText.getText().toString().trim());
                    setResult(Activity.RESULT_OK, result);
                    finish();
                } else {
                    Toast.makeText(AddLocRemActivity.this, "You forgot to enter a label!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext());
        double lat = Constants.getDouble(sharedPreferences, Constants.TAG_LAT, 0);
        double lon = Constants.getDouble(sharedPreferences, Constants.TAG_LONG, 0);
        if (lat != 0 && lon != 0) {
            location = new LatLng(lat, lon);
        } else {
            location = new LatLng(52.379189, 4.899431);
        }
        selected = location;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at current location
        mMap.addMarker(new MarkerOptions().position(location).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("MAPS", "map clicked at:" + latLng.toString());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                selected = latLng;
            }
        });

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(poi.latLng).title(poi.name));
                selected = poi.latLng;
            }
        });
    }
}
