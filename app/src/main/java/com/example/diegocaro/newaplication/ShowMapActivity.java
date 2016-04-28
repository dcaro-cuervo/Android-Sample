package com.example.diegocaro.newaplication;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowMapActivity extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener, GoogleApiClient.OnConnectionFailedListener {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private Location mLastLocation;
    ArrayList<LatLng> markerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        //get the location to marker.
        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra(ShowLastPositionActivity.POSITION_MESSAGE);
        markerPoints = new ArrayList<LatLng>();
        markerPoints.add(getCoordinates(mLastLocation));
        addGoogleMap();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);

            drawPoints();
            drawDrivingRouteDirections();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    // A callback method, which is invoked on configuration is changed
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Adding the pointList arraylist to Bundle
        savedInstanceState.putParcelableArrayList("points", markerPoints);

        // Saving the bundle
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());

        LatLng foundCoordinate = getLocationFromAddress(place.getAddress().toString());
        if (foundCoordinate != null) {
            if (markerPoints.size()>1) {
                markerPoints.remove(1);
                googleMap.clear();
            }

            markerPoints.add(foundCoordinate);
            drawPoints();
            drawDrivingRouteDirections();

            //showDistance();
        }

    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Get latitude and longitud to marker in the map.
     * @param location
     */
    private LatLng getCoordinates(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        return latLng;
    }

    /**
     * Marker location in the map and center the camera
     */
    private void markerLocation(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        googleMap.addMarker(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    /**
     * draw marker on the map
     */
    private void drawPoints() {
        if (markerPoints != null) {
            for (int i = 0; i < markerPoints.size(); i++) {
                markerLocation(markerPoints.get(i));
            }
        }
    }

    /**
     * draw route under map between two directions.
     */
    private void drawDrivingRouteDirections() {
        if (markerPoints != null && markerPoints.size() > 1) {
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(markerPoints.get(0), markerPoints.get(markerPoints.size()-1));

            DownloadTask downloadTask = new DownloadTask();

            //Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    /**
     * show in UI the distance between two locations.
     */
    private void showDistance() {

        TextView textView = (TextView) findViewById(R.id.lblDistance);
        textView.setText(getDistance(markerPoints.get(0), markerPoints.get(1)));
    }

    /**
     * construct the url to call webService.
     * @param origin
     * @param destination
     * @return
     */
    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        //Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        //destination of route
        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;

        //Sensor enabled
        String sensor = "sensor=false";

        //building parameters to the web service
        String parameters = str_origin + "&" + str_destination + "&" + sensor;

        //output format
        String output = "json";

        //building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * get the distance between two coordinates
     */
    private String getDistance(LatLng origin, LatLng destination) {
        float[] result = new float[1];

        Location.distanceBetween(origin.latitude, origin.longitude, destination.latitude, destination.longitude, result);
        return Float.toString(result[0]);
    }

    /**
     * Build map and sync.
     */
    private void addGoogleMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Get coordinates from street address.
     * @param strAddress
     * @return coordinates
     */
    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();



            latLng = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return latLng;
    }

    /**
     * Update values when rotate the screen for example.
     * @param savedInstanceState
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Restoring the markers on configuration changes
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey("points")){
                markerPoints = savedInstanceState.getParcelableArrayList("points");
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<PolylineWithDistance>> {
        //parsing the data in non-ui thread
        @Override
        protected List<PolylineWithDistance> doInBackground(String... jsonData) {
            JSONObject jsonObject;
            List<PolylineWithDistance> routes = null;

            try {
                jsonObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                //starts parsing data
                routes = parser.parse(jsonObject);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return routes;
        }

        /**
         * Executes in UI thread, after the parsing process
         */
        @Override
        protected void onPostExecute (List<PolylineWithDistance> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;
            double distanceBetweenLocations = 0;

            //traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();

                //fetching i-th route
                PolylineWithDistance polylineWithDistance = result.get(i);
                List<HashMap<String,String>> path = polylineWithDistance.Polyline;
                distanceBetweenLocations = calculatePrice(polylineWithDistance.Distance);

                //fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                //adding all the points in the route to LineOptions
                polylineOptions.addAll(points);
                polylineOptions.width(20);
                polylineOptions.color(Color.BLUE);
            }

            //drawing polyline in the google map for the i-th route
            googleMap.addPolyline(polylineOptions);

            TextView lblDistance = (TextView) findViewById(R.id.lblDistance);
            lblDistance.setText("$ " + Double.toString(distanceBetweenLocations));
        }

        /**
         * Calculate price starting with down flag add distance per block.
         */
        private Double calculatePrice(int distance) {
            double downFlag = 12.5;
            double blockPrice = 100;
            double result;

            result = downFlag + (distance/blockPrice);

            return result;
        }
    }

    /**
     * Created by diego.caro on 16/04/2016.
     * Fetches data from url passed
     */
    public class DownloadTask extends AsyncTask<String, Void, String> {

        //downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            //for storing data from web service
            String data = "";

            try {
                //fetching the data from web service
                data = downloadUrl(url[0]);
            }
            catch (Exception ex) {
                Log.d("Background Task", ex.toString());
            }

            return data;
        }

        /**
         * Method to download json data from url
         * @param strUrl
         * @return json
         * @throws IOException
         */
        public String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream inputStream = null;
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(strUrl);

                //creating an http connection to communicate with url
                httpURLConnection = (HttpURLConnection) url.openConnection();

                //connecting to url
                httpURLConnection.connect();

                //reading data from url
                inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();
            }
            catch (Exception ex) {
                Log.d("Exception download url", ex.toString());
            }
            finally {
                inputStream.close();
                httpURLConnection.disconnect();
            }

            return data;
        }

        /**
         * Executes in UI thread, after the execution of doInBackground
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            //invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
}
