package de.hsbo.pollenwarner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hsbo.pollenwarner.services.LocationService;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver receiver;
    public static String regionData;
    public static String allData;
    public static String city;

    @Override
    public void onResume() {
        super.onResume();
        if(receiver == null){
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Receiver", "created");
                    regionData = (String) intent.getExtras().get("region_data");
                    city = (String) intent.getExtras().get("geocoder");
                    allData = (String) intent.getExtras().get("all_data");
                }
            };
            this.registerReceiver(receiver, new IntentFilter("pollenData"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigation();

        startService();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initNavigation() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void startService(){

        Intent locationServiceIntent = new Intent(MainActivity.this, LocationService.class);
        startForegroundService(locationServiceIntent);

    }

}