package com.example.anchovyfeeder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmListItem {
    private Boolean use;
    private Calendar time;
    private String name;

    public void setUse(Boolean value) {
        this.use = value;
    }

    public Boolean getUse() {
        return this.use;
    }

    public void setTime(int hh, int mm, int ss) {
        time = Calendar.getInstance();
        time.set(Calendar.HOUR, hh);
        time.set(Calendar.MINUTE, mm);
        time.set(Calendar.SECOND, ss);
    }

    public String getTimeToString(String format) {
        SimpleDateFormat form = new SimpleDateFormat(format);
        String formattedTime = form.format(time.getTime());
        return formattedTime;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }
}
