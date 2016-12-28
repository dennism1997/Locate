package com.moumou.locate.reminder;

import android.location.Location;

/**
 * Created by MouMou on 28-12-16.
 */

public class LocationReminder extends Reminder {

    private Location location;

    public LocationReminder(int id, String label, Location location) {
        super(id, label);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
