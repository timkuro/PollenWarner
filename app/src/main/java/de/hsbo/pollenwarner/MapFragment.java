package de.hsbo.pollenwarner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TableLayout;

import org.json.JSONObject;

import de.hsbo.pollenwarner.databinding.FragmentMapBinding;
import de.hsbo.pollenwarner.services.WebAppInterface;

public class MapFragment extends Fragment {

    private WebView webView;
    private View view;
    BroadcastReceiver receiver;
    WebAppInterface webAppInterface;
    MainActivity activity;
    String data;



    @Override
    public void onResume() {
        super.onResume();
        data = activity.allData;
        if(data != null){
            webAppInterface.setPollenData(data);
        }
        else{
            if(receiver == null){
                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("Receiver", "created");
                        data = (String) intent.getExtras().get("all_data");
                        webAppInterface.setPollenData(data);
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
        view =  inflater.inflate(R.layout.fragment_map, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/PollenMap.html");
        webAppInterface = new WebAppInterface(getActivity());
        webView.addJavascriptInterface(webAppInterface, "Android");

        activity = (MainActivity) getActivity();
        return view;
    }
}