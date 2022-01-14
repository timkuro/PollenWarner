package de.hsbo.pollenwarner.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import de.hsbo.pollenwarner.MapFragment;

public class WebAppInterface {
    Context mContext;
    String pollenData;

    public void setPollenData(String pollenData) {
        this.pollenData = pollenData;
    }

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public String getPollenData(String regionId) throws JSONException {

        JSONArray allData = new JSONArray(this.pollenData);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<allData.length(); i++){
            JSONObject regionI = (JSONObject) allData.get(i);
            int id = (int) regionI.get("id");
            if(Integer.parseInt(regionId) == id){
                sb.append("<b>" + regionI.get("name") + "</b></br>");
                JSONObject pollenData = (JSONObject) regionI.get("pollenData");
                Iterator<String> keys = pollenData.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    if (pollenData.get(key) instanceof JSONObject) {
                        JSONObject values = (JSONObject) pollenData.get(key);
                        String value = (String) values.get("today");
                        sb.append(key + ": " + value + "</br>");
                    }
                }
            }
        }
        String result = sb.toString();
        return result;
    }

    @SuppressLint("WrongConstant")
    @JavascriptInterface
    public void getTestData(){
        Toast.makeText(mContext, this.pollenData, Toast.LENGTH_SHORT).show();;
    }
}