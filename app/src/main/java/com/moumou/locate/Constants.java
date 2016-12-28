package com.moumou.locate;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by MouMou on 28-12-16.
 */

public class Constants {

    private static int activitycounter = 0;

    public static final int RC_LOCATION = 9002;
    public static final int RC_NEW_LOC = 9003;
    public static final int RC_NEW_PLACE = 9004;

    public static final String TAG_LAT = "lat";
    public static final String TAG_LONG = "long";
    public static final String NEW_LAT = "newlat";
    public static final String NEW_LONG = "newlong";
    public static final String NEW_PLACE = "newplace";
    public static final String NEW_LABEL = "newlabel";
    public static final String NEW_TYPES_ARRAY = "newpoi";

    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static int getActivitycounter() {
        activitycounter++;
        return activitycounter - 1;
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    public static final String[] POI_TYPES_ARRAY = new String[]{"accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall", "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "electrician", "electronics_store", "embassy", "fire_station", "florist", "funeral_home", "furniture_store", "gas_station", "gym", "hair_care", "hardware_store", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store", "laundry", "lawyer", "library", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery", "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park", "parking", "pet_store", "pharmacy", "physiotherapist", "plumber", "police", "post_office", "real_estate_agency", "restaurant", "roofing_contractor", "rv_park", "school", "shoe_store", "shopping_mall", "spa", "stadium", "storage", "store", "subway_station", "synagogue", "taxi_stand", "train_station", "transit_station", "travel_agency", "university", "veterinary_care", "zoo"};
    public static final List<String> POI_TYPES_LIST = Arrays.asList(POI_TYPES_ARRAY);
}