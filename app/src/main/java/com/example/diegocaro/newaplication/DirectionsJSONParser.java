package com.example.diegocaro.newaplication;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by diego.caro on 19/04/2016.
 */
public class DirectionsJSONParser {
    /**
     * Receives a JSONObject and return a list of lists containing latitudes and longitude
     */
    public List<PolylineWithDistance> parse(JSONObject jsonObject) {
        List<PolylineWithDistance> routes = new ArrayList<>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jsonObject.getJSONArray("routes");

            //traversing all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                Integer distanceBetweenLocations = 0;

                //traversing all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    //traversing all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyLine = "";
                        polyLine = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Integer distance = (Integer)((JSONObject)((JSONObject)jSteps.get(k)).get("distance")).get("value");
                        distanceBetweenLocations = distanceBetweenLocations + distance;

                        List<LatLng> list = decodePoly(polyLine);

                        //traversing all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("lat", Double.toString((list.get(l)).latitude));
                            hashMap.put("lng", Double.toString(list.get(l).longitude));

                            path.add(hashMap);
                        }
                    }

                    PolylineWithDistance polylineWithDistance = new PolylineWithDistance(distanceBetweenLocations, path);
                    routes.add(polylineWithDistance);
                }
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
