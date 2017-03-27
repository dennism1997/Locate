package com.moumou.locate.reminder;

import java.io.Serializable;

/**
 * Created by MouMou on 28-12-16.
 */

public class LocationReminder extends Reminder implements Serializable {

    private String placeId;
    private String placeName;
    private String placeAddress;
    private double latitude;
    private double longitude;

    public LocationReminder(int id, String label, String placeId, String placeName, String placeAddress, double latitude, double longitude) {
        super(id, label);
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String toNotificationString(){
        return placeName + ", " + placeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LocationReminder that = (LocationReminder) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (!placeId.equals(that.placeId)) return false;
        if (!placeName.equals(that.placeName)) return false;
        return placeAddress.equals(that.placeAddress);
    }

}
