package de.hsbo.pollenwarner.location;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import de.hsbo.pollenwarner.services.LocationService;
import de.hsbo.pollenwarner.webrequest.JsonInterpreter;

public class MyLocationListener implements LocationListener {

    private static Location lastLocation = null;
    private static Location currentBestLocation = null;
    static final int TWO_MINUTES = 1000 * 60 * 2;
    private JSONObject pollenData;

    @Override
    public void onLocationChanged(Location location) {

        makeUseOfNewLocation(location);

        if(currentBestLocation == null){
            currentBestLocation = location;
        }
        Log.e("Current Location:", "Lat: " + location.getLatitude() + ", " +
                                            "Lon: " + location.getLongitude()
        );
        List<Polygon> regions = LocationService.getRegions();

        Log.e("Regions:", regions.toString());
        String currentRegion = "";
        for (Polygon region: regions) {
            String name = region.getRegion();
            Boolean pip = region.PointInPolygon(new Point(location.getLongitude(),location.getLatitude()));

            if (pip){
                currentRegion = region.getRegion();
            }
        }
        Log.e("Aktuelle Region:", currentRegion);

        if (pollenData != null){
            JSONObject data = JsonInterpreter.getDataOfRegion(currentRegion, pollenData);
            Log.e("Check Pollendata", data.toString());

        }
    }

    /**
     * This method modify the last know good location according to the arguments.
     *
     * @param location The possible new location.
     */
    void makeUseOfNewLocation(Location location) {
        if ( isBetterLocation(location, currentBestLocation) ) {
            lastLocation = currentBestLocation;
            currentBestLocation = location;
        }
    }

    /** Determines whether one location reading is better than the current location fix
     * @param location  The new location that you want to evaluate
     * @param currentBestLocation  The current location fix, to which you want to compare the new one.
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    public static Location getLocation(){
        return currentBestLocation;
    }

    public void setPollenData(JSONObject pollenData) {
        this.pollenData = pollenData;
    }
}