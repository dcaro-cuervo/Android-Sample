package com.example.diegocaro.newaplication;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMapActivity extends FragmentActivity implements OnMapReadyCallback {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra(ShowLastPositionActivity.POSITION_MESSAGE);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (mLastLocation != null && googleMap != null) {
            handleNewLocation(mLastLocation);
        }
    }

    /**
     * Marker location in the map and center the camera
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        googleMap.addMarker(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    private void addGoogleMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
        }
    }
}
