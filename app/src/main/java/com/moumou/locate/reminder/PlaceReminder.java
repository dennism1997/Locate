package com.moumou.locate.reminder;

import com.google.android.gms.location.places.Place;

/**
 * Created by MouMou on 28-12-16.
 */

public class PlaceReminder extends Reminder {

    private Place place;

    public PlaceReminder(int id, String Label, Place place) {
        super(id, Label);
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
