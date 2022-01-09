package de.hsbo.pollenwarner.webrequest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hsbo.pollenwarner.MainActivity;
import de.hsbo.pollenwarner.location.MyLocationListener;
import de.hsbo.pollenwarner.services.LocationService;

public class Requestor {

    private Context context;

    public Requestor() {
    }

    public Requestor(Context context) {
        this.context = context;
    }

    public void requestPollenData(MyLocationListener listener){
        Log.e("Hallo", "test");
        RequestQueue queue = Volley.newRequestQueue(this.context);

        String url = "https://opendata.dwd.de/climate_environment/health/alerts/s31fg.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Request Rest Service", "505");

                try {
                    JsonInterpreter jsonInterpreter = new JsonInterpreter(response);

                    listener.setPollenData(jsonInterpreter.getJson());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);
    }
}
