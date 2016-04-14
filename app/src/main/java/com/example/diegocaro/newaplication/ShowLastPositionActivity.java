package com.example.diegocaro.newaplication;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class ShowLastPositionActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks  {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String POSITION_MESSAGE = "com.example.diegocaro.newaplication.POSITION_MESSAGE";
    public GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Button btnShowLastLocation, btnShowMap;
    private TextView lblLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_last_position);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnShowLastLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowMap = (Button) findViewById(R.id.btnDisplayMap);
        lblLocation = (TextView) findViewById(R.id.lblLocation);
        final Context context = this;
        btnShowLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLocation();
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowMapActivity.class);
                intent.putExtra(POSITION_MESSAGE, mLastLocation);
                startActivity(intent);
            }
        });

        // Create an instance of GoogleAPIClient to getLastLocation.
        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }


    /**
     *  Este metodo se ejecuta luego del onMapReady
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private void displayLocation() {
        //get last Location.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            TextView lblLatitud = (TextView) findViewById(R.id.mLatitudeText);
            TextView lblLongitud = (TextView) findViewById(R.id.mLongitudeText);
            lblLatitud.setText(Double.toString(mLastLocation.getLatitude()));
            lblLongitud.setText(Double.toString(mLastLocation.getLongitude()));
        }
        else {
            lblLocation.setText("( Couldn't get the location. Make sure location is enabled.)");
        }
    }
}
