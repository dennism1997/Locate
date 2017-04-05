package com.moumou.locate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moumou.locate.reminder.Reminder;

import java.util.List;

/**
 * Created by MouMou on 29-12-16.
 */

public class ReminderListAdapter extends ArrayAdapter<Reminder> {

    private Bitmap[] images;
    private int[] height;
    private int[] width;

    public ReminderListAdapter(Context context, int resource, List<Reminder> objects) {
        super(context, resource, objects);
        //        images = new Bitmap[objects.size()];
        //        height = new int[objects.size()];
        //        width = new int[objects.size()];
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.reminder_list_item, parent, false);
        }
        Reminder r = getItem(position);
        ImageView imageView = null;

        TextView title = (TextView) convertView.findViewById(R.id.reminder_title);
        TextView description = (TextView) convertView.findViewById(R.id.reminder_desc);
        TextView distance = (TextView) convertView.findViewById(R.id.reminder_dist);

        imageView = (ImageView) convertView.findViewById(R.id.place_image);

        width[position] = imageView.getWidth();
        width[position] = imageView.getHeight();

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(),
                                                   "font/Roboto-Regular.ttf");
        title.setTypeface(roboto);
        description.setTypeface(roboto);

        title.setText(r != null ? r.getLabel().replace('_', ' ') : "Null Reminder");
        imageView.setImageResource(R.drawable.default_wifi);

        description.setText(r != null ? r.toDescriptionString() : "Null Reminder");

        //        if (r instanceof LocationReminder) {
        //            LocationReminder lr = (LocationReminder) r;
        //            //imageView.setImageBitmap();
        //        }
        //
        //        if(images[position] != null && imageView != null) {
        //            imageView.setImageBitmap(images[position]);
        //        }

        return convertView;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    //    public void setBitmap(int pos, Bitmap bitmap) {
    //        images[pos] = bitmap;
    //        notifyDataSetChanged();
    //    }
    //
    //    public int getHeight(int pos) {
    //        return height[pos];
    //    }
    //
    //    public int getWidth(int pos) {
    //        return width[pos];
    //    }
}
