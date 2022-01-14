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

import de.hsbo.pollenwarner.databinding.FragmentMapBinding;
import de.hsbo.pollenwarner.services.WebAppInterface;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private WebView webView;
    private View view;
    BroadcastReceiver receiver;
    WebAppInterface webAppInterface;



    @Override
    public void onResume() {
        super.onResume();
        if(receiver == null){
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Receiver", "created");
                    String data = (String) intent.getExtras().get("pollen_data");
                    webAppInterface.setPollenData(data);
                }
            };
            getActivity().registerReceiver(receiver, new IntentFilter("allPollenData"));
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


        binding = FragmentMapBinding.inflate(inflater, container, false);
        return view;
    }
}