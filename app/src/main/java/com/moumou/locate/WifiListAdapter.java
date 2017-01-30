package com.moumou.locate;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WifiListAdapter extends ArrayAdapter<String> {

    public WifiListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.add_wifirem_list_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.wifi_ssid);

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(),
                                                   "font/Roboto-Regular.ttf");
        textView.setTypeface(roboto);
        String text = getItem(position);
        textView.setText(text);


        return convertView;
    }
}
