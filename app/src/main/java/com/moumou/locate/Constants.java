package com.moumou.locate;

import android.content.SharedPreferences;

import com.google.android.gms.location.places.Place;
import com.moumou.locate.reminder.Reminder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by MouMou on 28-12-16.
 */

public class Constants {

    public static final String REMINDER_FILE = "reminders";
    public static final String NEW_WIFI_REM = "newwifi";
    public static final String PREF_KEY_FIRST_START = "firststart";
    public static final int NO_WIFI = 8001;
    public static final int RC_INTRO = 9006;
    public static final int RC_LOCATION = 9002;
    public static final int RC_NEW_LOC = 9003;
    public static final int RC_NEW_PLACE = 9004;
    public static final int RC_NEW_WIFI = 9005;
    public static final int LOC_INTERVAL_LONG = 10 * 60 * 1000;
    public static final int LOC_INTERVAL_SHORT = 5 * 60 * 1000;
    public static final String TAG_LAT = "lat";
    public static final String TAG_LONG = "long";
    public static final String NEW_LAT = "newlat";
    public static final String NEW_LONG = "newlong";
    public static final String NEW_PLACE = "newplace";
    public static final String NEW_LABEL = "newlabel";
    public static final String NEW_TYPES_ARRAY = "newpoi";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String[] POI_TYPES_ARRAY = new String[]{"accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall", "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "electrician", "electronics_store", "embassy", "fire_station", "florist", "funeral_home", "furniture_store", "gas_station", "gym", "hair_care", "hardware_store", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store", "laundry", "lawyer", "library", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery", "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park", "parking", "pet_store", "pharmacy", "physiotherapist", "plumber", "police", "post_office", "real_estate_agency", "restaurant", "roofing_contractor", "rv_park", "school", "shoe_store", "shopping_mall", "spa", "stadium", "storage", "store", "subway_station", "synagogue", "taxi_stand", "train_station", "transit_station", "travel_agency", "university", "veterinary_care", "zoo"};
    public static final List<String> POI_TYPES_LIST = Arrays.asList(POI_TYPES_ARRAY);
    private static AtomicInteger ai = new AtomicInteger();

    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static int getReminderId() {
        return ai.get();
    }

    public static int getReminderId(List<Reminder> reminderList) {
        int max = 0;
        for (Reminder r : reminderList) {
            if (r.getId() > max) {
                max = r.getId();
            }
        }
        ai = new AtomicInteger(max);
        return getReminderId();
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next();
        }
        return ret;
    }

    public static String getPlaceTypesString(Place p) {
        System.out.println(p.getPlaceTypes().toString());
        StringBuilder sb = new StringBuilder();
        if (p.getPlaceTypes().size() > 0) {
            if (p.getPlaceTypes().get(0) < 90) {
                sb.append(Constants.POI_TYPES_ARRAY[p.getPlaceTypes().get(0)]);
            }
            for (int i = 1; i < p.getPlaceTypes().size(); i++) {
                if (p.getPlaceTypes().get(i) > 90) {
                    continue;
                }
                sb.append(", ");
                sb.append(Constants.POI_TYPES_ARRAY[p.getPlaceTypes().get(i)]);
            }
        }

        return sb.toString();
    }
}