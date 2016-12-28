package com.moumou.locate.reminder;

import android.location.Location;

import com.google.android.gms.location.places.Place;

/**
 * Created by MouMou on 28-12-16.
 */

public class LocationReminder extends Reminder {

    private Place place;

    public LocationReminder(int id, String label, Place place) {
        super(id, label);
        this.place = place;
    }

}
