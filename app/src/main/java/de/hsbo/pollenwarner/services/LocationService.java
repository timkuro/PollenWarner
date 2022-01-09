package de.hsbo.pollenwarner.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.content.AsyncTaskLoader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hsbo.pollenwarner.MainActivity;
import de.hsbo.pollenwarner.location.MyLocationListener;
import de.hsbo.pollenwarner.location.PollenRegions;
import de.hsbo.pollenwarner.location.Polygon;
import de.hsbo.pollenwarner.webrequest.Requestor;

public class LocationService<WebService> extends Service {

    private static final String TAG = "restService";
    private String CHANNEL_ID_1 = "rest Notification";
    private String CHANNEL_ID_2 = "Neue Landesgrenze erreicht";

    //notification variables
    private Intent intentNotification;
    private PendingIntent pendingIntent;
    private NotificationCompat.Builder builder_1;
    private NotificationCompat.Builder builder_2;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;

    public LocationManager locationManager;
    public MyLocationListener listener;

    // locations
    private static  Location actualLocation = null;
    private static Location newLocation = null;

    // Landesgebiete
    private static  Location actualArea = null;
    private static Location newArea = null;
    private static JSONArray featuresDArr;

    public static String actualData;

    // minDistance GPS
    private static int minDistance = 0;

    private Context ctx;

    private static List<Polygon> regions;

    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate();
        ctx = this;
        // erstellt Notification Struktur
        intentNotification = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);

        builder_1 = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setContentIntent(pendingIntent)
                .setContentTitle("PollenWarner läuft im Hintergrund")
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.btn_dialog);

        builder_2 = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setContentIntent(pendingIntent)
                .setContentTitle("Hausarbeit")
                .setContentText("Neue Landesgrenze erreicht")
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.btn_dialog);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        CharSequence name = "MAIN";
        String description = "Main Notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        notificationChannel = new NotificationChannel(CHANNEL_ID_1, name, importance);

        notificationManager.createNotificationChannel(notificationChannel);


        Log.e(TAG, "Service created");
    }

    public void onDestroy() {
        super.onDestroy();

        // beim zerstören wird auchNotification zerstört
        notificationManager.cancel(0);

        locationManager.removeUpdates(listener);

        Log.e(TAG, "Service destroyed");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service running");

        try {
            regions = PollenRegions.getPollenRegions(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_STICKY;
        }
        startForeground(1, builder_1.build());
        //notificationManager.notify(2, builder_2.build());

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, minDistance, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, minDistance, listener);

        Requestor requestor = new Requestor(this);
        requestor.requestPollenData(listener);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                requestor.requestPollenData(listener);
            }
        }, calendar.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

        return START_STICKY;
    }

    public static List<Polygon> getRegions() {
        return regions;
    }



}
