package com.example.anchovyfeeder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class AlarmListItem extends RealmObject {
    private Boolean IS_USE = true;
    private Date DATE;
    @Ignore
    private Calendar CALENDAR;
    private String ALARM_NAME = "";

    public void setTime(int hh, int mm, int ss) {
        CALENDAR = Calendar.getInstance();
        CALENDAR.set(Calendar.HOUR_OF_DAY, hh);
        CALENDAR.set(Calendar.MINUTE, mm);
        CALENDAR.set(Calendar.SECOND, ss);
        DATE = new Date(CALENDAR.getTimeInMillis());
    }

    public String getTimeToString(String format) {
        SimpleDateFormat form = new SimpleDateFormat(format);
        String formattedTime = form.format(CALENDAR.getTime());
        return formattedTime;
    }

    public Calendar getCalendar() {
        if (CALENDAR == null) {
            CALENDAR = Calendar.getInstance();
            CALENDAR.setTime(DATE);
        }
        return this.CALENDAR;
    }

    public int getHour() {
        if (CALENDAR == null) {
            CALENDAR = Calendar.getInstance();
            CALENDAR.setTime(DATE);
        }
        return CALENDAR.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {

        if (CALENDAR == null) {
            CALENDAR = Calendar.getInstance();
            CALENDAR.setTime(DATE);
        }
        return CALENDAR.get(Calendar.MINUTE);
    }

    public Boolean getUse() {
        return this.IS_USE;
    }

    public void setUse(Boolean value) {
        this.IS_USE = value;
    }

    public String getName() {
        return this.ALARM_NAME;
    }

    public void setName(String value) {
        this.ALARM_NAME = value;
    }

    public Date getDate() {
        return DATE;
    }

    public void setDate(Date date) {
        if (CALENDAR == null) {
            CALENDAR = Calendar.getInstance();
            CALENDAR.setTime(DATE);
        }
        this.DATE = date;
    }

}
