package com.moumou.locate.reminder;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MouMou on 28-12-16.
 */

public class POIReminder extends Reminder {

    private List<Integer> placeTypes;

    public POIReminder(int id, String Label, List<Integer> placeType) {
        super(id, Label);
        this.placeTypes = placeType;
    }

    public POIReminder(int id, String Label) {
        super(id, Label);
        this.placeTypes = new ArrayList<>();
    }

    public void addType(int type){
        placeTypes.add(type);
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }
}
