package com.moumou.locate.reminder;

import java.io.Serializable;

/**
 * Created by MouMou on 19-01-17.
 */

public class WifiReminder extends Reminder implements Serializable {

    private String SSID;

    public WifiReminder(int id, String label, String SSID) {
        super(id, label);
        this.SSID = SSID;
    }

    @Override
    public String toNotificationString() {
        return "Network: " + this.SSID;
    }

    @Override
    public String toDescriptionString() {
        return toNotificationString();
    }

    public String getSSID() {
        return SSID;
    }
}
