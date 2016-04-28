package com.example.diegocaro.newaplication;

import java.util.HashMap;
import java.util.List;

/**
 * Created by diego.caro on 25/04/2016.
 */
public class PolylineWithDistance {

    Integer Distance;
    List<HashMap<String, String>> Polyline;

    public PolylineWithDistance(Integer distance, List<HashMap<String,String>> polyline) {
        Distance = distance;
        Polyline = polyline;
    }
}
