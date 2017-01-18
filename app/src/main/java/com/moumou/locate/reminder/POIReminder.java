package com.moumou.locate.reminder;

import com.moumou.locate.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class POIReminder extends Reminder implements Serializable {

    private List<Integer> placeTypes;
    private int matchedType;

    public POIReminder(int id, String Label, List<Integer> placeType) {
        super(id, Label);
        this.placeTypes = placeType;
    }

    public POIReminder(int id, String Label) {
        super(id, Label);
        this.placeTypes = new ArrayList<>();
    }

    @Override
    public String toNotificationString() {
        return Constants.POI_TYPES_ARRAY[matchedType];
    }

    public void addType(int type) {
        placeTypes.add(type);
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public String getPlaceTypesString() {
        StringBuilder sb = new StringBuilder();
        if (placeTypes.size() > 0) {
            sb.append(Constants.POI_TYPES_ARRAY[placeTypes.get(0)]);
            for (int i = 1; i < placeTypes.size(); i++) {
                sb.append(", ");
                sb.append(Constants.POI_TYPES_ARRAY[placeTypes.get(i)]);
            }
        }

        return sb.toString();
    }

    public void setMatchedType(int i) {
        this.matchedType = i;
    }
}
