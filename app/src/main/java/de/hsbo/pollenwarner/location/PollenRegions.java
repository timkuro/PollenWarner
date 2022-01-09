package de.hsbo.pollenwarner.location;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PollenRegions {

    public static List<Polygon> getPollenRegions(Context context) throws IOException, JSONException {

        JSONObject regionsAsJson = PollenRegions.getRegionsAsJson(context);
        List<Polygon> regionsAsPolygons = PollenRegions.getRegionsAsPolygons(regionsAsJson);

        return regionsAsPolygons;
    }

    private static JSONObject getRegionsAsJson(Context context) {

        String jsonString = "";
        JSONObject jsonObject = new JSONObject();
        try {
            InputStream is = context.getAssets().open("gebiete.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
            jsonObject = new JSONObject(jsonString);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    private static List<Polygon> getRegionsAsPolygons(JSONObject regionsAsJson) {

        List<Polygon> regionsAsPolygons = new ArrayList<Polygon>();

        try {
            JSONArray array = (JSONArray) (regionsAsJson.get("features"));

            for (int i = 0; i < array.length(); i++) {

                JSONObject feature = (JSONObject) array.get(i);
                JSONObject properties = (JSONObject) feature.get("properties");
                String regionName = (String) properties.get("GEN");
                JSONObject geometry = (JSONObject) feature.get("geometry");
                JSONArray coordinates = (JSONArray) ((JSONArray) geometry.get("coordinates")).get(0);

                List<Point> points = new ArrayList<Point>();

                for (int k = 0; k < coordinates.length(); k++) {
                    JSONArray node = (JSONArray) coordinates.get(k);
                    if(!(node.get(0) instanceof JSONArray)){
                        double x = Double.parseDouble(String.valueOf(node.get(0)));
                        double y = Double.parseDouble(String.valueOf(node.get(1)));
                        points.add(new Point(x,y));
                    }
                    else {
                        for(int j = 0; j < node.length(); j++){
                            JSONArray nodeOfMultiPolygon = (JSONArray) node.get(j);
                            if(!(nodeOfMultiPolygon.get(0) instanceof JSONArray)){
                                double x = Double.parseDouble(String.valueOf(nodeOfMultiPolygon.get(0)));
                                double y = Double.parseDouble(String.valueOf(nodeOfMultiPolygon.get(1)));
                                points.add(new Point(x,y));
                            }
                        }
                    }

                }

                Polygon polygon = new Polygon(points, regionName);

                regionsAsPolygons.add(polygon);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return regionsAsPolygons;

    }

}
