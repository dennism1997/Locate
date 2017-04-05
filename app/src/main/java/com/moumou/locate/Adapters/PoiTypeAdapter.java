package com.moumou.locate.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.moumou.locate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MouMou on 28-12-16.
 */

public class PoiTypeAdapter extends ArrayAdapter<String> {

    private boolean[] checked;

    public PoiTypeAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        checked = new boolean[objects.length];
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.poi_types_list_item, parent, false);
        }

        TextView label = (TextView) convertView.findViewById(R.id.poi_type_name);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.poi_type_checkbox);

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(),
                                                   "font/Roboto-Regular.ttf");


        label.setText(getItem(position).replaceAll("_", " "));
        label.setTypeface(roboto);
        checkBox.setChecked(false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(!checkBox.isChecked());
                checked[position] = checkBox.isChecked();
            }
        });

        return convertView;
    }

    public List<Integer> getCheckedItems() {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                result.add(i);
            }
        }

        return result;
    }
}
