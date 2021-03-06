package com.moumou.locate.reminder;

import java.io.Serializable;

/**
 * Created by MouMou on 28-12-16.
 */

public abstract class Reminder implements Serializable{

    private int id;
    private String label;

    public Reminder(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public abstract String toNotificationString();

    public abstract String toDescriptionString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reminder reminder = (Reminder) o;

        return id == reminder.id && label.equals(reminder.label);
    }

}
