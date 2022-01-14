package de.hsbo.pollenwarner.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hsbo.pollenwarner.MainActivity;
import de.hsbo.pollenwarner.location.PollenRegions;
import de.hsbo.pollenwarner.location.Polygon;
import de.hsbo.pollenwarner.webrequest.Requestor;

public class LocationService<WebService> extends Service {


    private static final String TAG = "restService";
    private String CHANNEL_ID_1 = "rest Notification";


    //notification variables
    private Intent intentNotification;
    private PendingIntent pendingIntent;
    private NotificationCompat.Builder builder_1;
    private NotificationCompat.Builder builder_2;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;

    public LocationManager locationManager;
    public MyLocationListener listener;

    // minDistance GPS
    private static int minDistance = 0;

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
        // erstellt Notification Struktur
        intentNotification = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);

        builder_1 = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setContentIntent(pendingIntent)
                .setContentTitle("PollenWarner läuft im Hintergrund")
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.btn_dialog);



        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        CharSequence name = "MAIN";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        notificationChannel = new NotificationChannel(CHANNEL_ID_1, name, importance);

        notificationManager.createNotificationChannel(notificationChannel);


        Log.d(TAG, "Service created");
    }

    public void onDestroy() {
        super.onDestroy();

        // beim zerstören wird auchNotification zerstört
        notificationManager.cancel(0);

        locationManager.removeUpdates(listener);

        Log.e(TAG, "Service destroyed");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service running");
        startForeground(1, builder_1.build());
        try {
            regions = PollenRegions.getPollenRegions(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listener = new MyLocationListener(this, pendingIntent, notificationManager);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_STICKY;
        }


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, minDistance, (LocationListener) listener);

        Requestor requestor = new Requestor(this);
        requestor.requestPollenData(listener);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestor.requestPollenData(listener);
            }
        }, calendar.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

        return START_STICKY;
    }

    public static List<Polygon> getRegions() {
        return regions;
    }

}
