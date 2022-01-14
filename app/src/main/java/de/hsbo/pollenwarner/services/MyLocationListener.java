package de.hsbo.pollenwarner.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hsbo.pollenwarner.location.Point;
import de.hsbo.pollenwarner.location.Polygon;
import de.hsbo.pollenwarner.services.LocationService;
import de.hsbo.pollenwarner.webrequest.JsonInterpreter;

public class MyLocationListener implements LocationListener {

    private JSONObject pollenData;
    Context context;
    String oldRegion;
    String currentRegion = "";
    int currentRegionId;
    private String CHANNEL_ID_1 = "rest Notification";
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    private JSONObject dataOfCurrentRegion;
    private JSONArray dataOfAllRegions;

    public MyLocationListener(Context context, PendingIntent pendingIntent, NotificationManager notificationManager) {
        this.context = context;
        this.pendingIntent = pendingIntent;
        this.notificationManager = notificationManager;
    }

    @Override
    public void onLocationChanged(Location location) {

        String city = null;
        try {
            city = getCity(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Polygon> regions = LocationService.getRegions();

        for (Polygon region: regions) {
            Boolean pip = region.PointInPolygon(new Point(location.getLongitude(),location.getLatitude()));

            if (pip){
                currentRegion = region.getRegion();
                currentRegionId = region.getId();
                if(oldRegion == null){
                    oldRegion = currentRegion;
                }
                else{
                    checkIfRegionChanged();
                }
            }
        }
        Log.e("Current Location:", "Lat: " + location.getLatitude() + ", " +
                "Lon: " + location.getLongitude() + ", " +
                "City: " + city + ", " +
                "Pollenregion: " + currentRegion
        );

        if (pollenData != null){
//            JSONObject data = JsonInterpreter.getDataOfRegion(currentRegion, pollenData);
            dataOfAllRegions = JsonInterpreter.getDataOfAllRegions(pollenData);
            dataOfCurrentRegion = null;
            try {
                for(int i=0; i< dataOfAllRegions.length(); i++){
                    JSONObject dataOfRegion = (JSONObject) dataOfAllRegions.get(i);
                    if(dataOfRegion.get("id").equals(currentRegionId)){
                        dataOfCurrentRegion = dataOfRegion;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dataOfCurrentRegion != null){
                Log.d("Check Pollendata", dataOfCurrentRegion.toString());

                Intent intent = new Intent("pollenData");
                intent.putExtra("region_data", dataOfCurrentRegion.toString());
                intent.putExtra("geocoder", city);
                intent.putExtra("all_data", dataOfAllRegions.toString());

                this.context.sendBroadcast(intent);
            }


        }
    }

    private void checkIfRegionChanged() {
        if(currentRegion.equals(oldRegion) == false){
            sendNotification();
        }
    }

    public void setPollenData(JSONObject pollenData) {
        this.pollenData = pollenData;
    }

    public JSONObject getDataOfCurrentRegion() {
        return dataOfCurrentRegion;
    }

    public JSONArray getDataOfAllRegions() {
        return dataOfAllRegions;
    }

    private String getCity(Location location) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.context, Locale.getDefault());

        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String city = addresses.get(0).getLocality();

        return city;
    }

    public void sendNotification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, CHANNEL_ID_1)
                .setContentIntent(pendingIntent)
                .setContentTitle("PollenWarner läuft im Hintergrund")
                .setContentText("Aktuelle Region: " + this.currentRegion)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.btn_dialog);

        Log.d("NotificationManager", "Region has changed");
        this.notificationManager.notify(1, builder.build());
    }

}