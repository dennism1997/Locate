package com.moumou.locate.reminder;

/**
 * Created by MouMou on 28-12-16.
 */

public abstract class Reminder {

    private int id;
    private String label;

    public Reminder(int id, String label) {
        this.id = id;
        this.label = label;
    }


}
