package de.hsbo.pollenwarner.webrequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonInterpreter {

    private JSONObject originJson;
    private JSONObject json;

    public JsonInterpreter(JSONObject originJson) throws JSONException {
        this.originJson = originJson;
        this.json = comprimizeJson();
    }

    public JSONObject getJson() {
        return json;
    }

    public JSONObject comprimizeJson() throws JSONException {

        JSONObject newJson = new JSONObject();
        newJson.put("state", this.originJson.get("last_update"));

        JSONArray regionsData = (JSONArray) originJson.get("content");

        JSONArray region = new JSONArray();

        for (int i = 0; i < regionsData.length(); i++) {


            JSONObject currentRegion = regionsData.getJSONObject(i);

            JSONObject newCurrentRegion = new JSONObject();

            newCurrentRegion.put("region_name", currentRegion.get("partregion_name"));
            newCurrentRegion.put("Pollen", currentRegion.get("Pollen"));

            region.put(i, newCurrentRegion);


        }
        newJson.put("content", region);
        return newJson;
    }

    public static JSONObject getDataOfRegion(String region, JSONObject pollenData){
        JSONObject pollenDataOfRegion = new JSONObject();
        try {

            JSONArray content = (JSONArray)pollenData.get("content");
            for(int i=0; i<content.length(); i++){
                JSONObject regionI = (JSONObject) content.get(i);
                String regionName = (String) regionI.get("region_name");
                if(regionName.equals(region)){
                    pollenDataOfRegion = (JSONObject) regionI.get("Pollen");
                    return pollenDataOfRegion;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pollenDataOfRegion;
    }
}
