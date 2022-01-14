package de.hsbo.pollenwarner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DashboardFragment extends Fragment {

    BroadcastReceiver receiver;
    private TableLayout tableLayout;
    private View view;
    MainActivity activity;
    String data;
    String city;

    @Override
    public void onResume() {
        super.onResume();

        city = activity.city;
        data = activity.regionData;
        if(city != null & data != null){
            updateTable(data);
            updateLocation(city);
        }
        else{
            if(receiver == null){
                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("Receiver", "created");
                        data = (String) intent.getExtras().get("region_data");
                        city = (String) intent.getExtras().get("geocoder");
                        updateTable(data);
                        updateLocation(city);

                    }
                };
                getActivity().registerReceiver(receiver, new IntentFilter("pollenData"));
            }
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tableLayout = view.findViewById(R.id.dashboard_table);
        TableRow header = (TableRow) getLayoutInflater().inflate(R.layout.dashboard_header, null);
        tableLayout.addView(header);

        activity = (MainActivity) getActivity();


//        updateLocation(city);
//        updateTable(data);

        return view;
    }

    private void updateTable(String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            JSONObject pollenData = (JSONObject)dataJson.get("pollenData");
            JSONArray pollenNames = pollenData.names();
            for(int i = 0; i<pollenNames.length(); i++){
                try {
                    String pollenName = (String) pollenNames.get(i);
                    TableRow row;

                    if(tableLayout.findViewById(i) == null){
                        row = (TableRow) getLayoutInflater().inflate(R.layout.dashboard_row, tableLayout, false);
                        row.setId(i);

                        TextView pollenNameView = row.findViewById(R.id.row_pollen);
                        TextView pollenLevelView = row.findViewById(R.id.row_level);

                        pollenNameView.setText(pollenName);
                        JSONObject currentPollenData = (JSONObject) pollenData.get(pollenName);
                        pollenLevelView.setText((String)currentPollenData.get("today"));

                        tableLayout.addView(row);
                    }
                    else{
                        row = tableLayout.findViewById(i);
                        TextView pollenNameView = row.findViewById(R.id.row_pollen);
                        TextView pollenLevelView = row.findViewById(R.id.row_level);

                        pollenNameView.setText(pollenName);
                        JSONObject currentPollenData = (JSONObject) pollenData.get(pollenName);
                        pollenLevelView.setText((String)currentPollenData.get("today"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Dashboard", "updated Pollen data");
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void updateLocation(String city){
        TextView textView = view.findViewById(R.id.pollendashboard_textview);
        textView.setText(city);
    }
}