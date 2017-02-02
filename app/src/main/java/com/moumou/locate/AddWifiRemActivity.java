package com.moumou.locate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddWifiRemActivity extends AppCompatActivity {

    private WifiListAdapter wifiListAdapter;
    private List<String> ssidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_rem);

        final ListView listView = (ListView) findViewById(R.id.wifi_listview);

        ssidList = new ArrayList<>();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        if (list == null) {
            Intent result = new Intent();
            setResult(Constants.NO_WIFI, result);
            finish();
            return;
        }

        Collections.sort(list, new Comparator<WifiConfiguration>() {
            @Override
            public int compare(WifiConfiguration o1, WifiConfiguration o2) {
                return o2.priority - o1.priority;
            }
        });

        for (WifiConfiguration wifiConfiguration : list) {
            ssidList.add(wifiConfiguration.SSID.substring(1, wifiConfiguration.SSID.length() - 1));
        }

        wifiListAdapter = new WifiListAdapter(this, R.layout.add_wifirem_list_item, ssidList);

        listView.setAdapter(wifiListAdapter);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent result = new Intent();
                result.putExtra(Constants.NEW_WIFI_REM, ssidList.get(position));
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        EditText editText = (EditText) findViewById(R.id.wifi_filter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                wifiListAdapter.getFilter().filter(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}
