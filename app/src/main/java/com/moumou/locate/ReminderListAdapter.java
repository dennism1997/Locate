package com.moumou.locate;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moumou.locate.reminder.LocationReminder;
import com.moumou.locate.reminder.POIReminder;
import com.moumou.locate.reminder.Reminder;

import java.util.List;

/**
 * Created by MouMou on 29-12-16.
 */

public class ReminderListAdapter extends ArrayAdapter<Reminder> {

    //todo make this
    public ReminderListAdapter(Context context, int resource, List<Reminder> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.reminder_list_item, parent, false);
        }
        Reminder r = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.reminder_title);
        TextView description = (TextView) convertView.findViewById(R.id.reminder_desc);

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(),
                                                   "font/Roboto-Regular.ttf");
        title.setTypeface(roboto);
        description.setTypeface(roboto);

        title.setText(r.getLabel());

        if (r instanceof POIReminder) {
            POIReminder pr = (POIReminder) r;
            description.setText(pr.getPlaceTypesString());
        } else if (r instanceof LocationReminder) {
            LocationReminder lr = (LocationReminder) r;
            description.setText(lr.getPlaceName() + ", " + lr.getPlaceAddress());
        }
        return convertView;
    }
}
