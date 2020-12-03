package com.example.anchovyfeeder.realmdb;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;

public class PhotoObject extends RealmObject {
    private Date DATE;
    private String PHOTO_URI;

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public void setDATE(Calendar cal) {
        this.DATE = cal.getTime();
    }

    public void setDATENow() {
        Calendar cal = Calendar.getInstance();
        this.DATE = cal.getTime();
    }

    public String getDateFormat(SimpleDateFormat format) {
        return format.format(DATE);
    }

    public int getDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DATE);
        return cal.get(Calendar.DATE);
    }

    public Uri getPHOTO_URIToUri() {
        return Uri.parse("file://" + PHOTO_URI);
    }

    public String getPHOTO_URI() {
        return PHOTO_URI;
    }

    public void setPHOTO_URI(String PHOTO_URI) {
        this.PHOTO_URI = PHOTO_URI;
    }

    public void setPHOTO_URI(Uri PHOTO_URI) {
        this.PHOTO_URI = PHOTO_URI.toString();
    }
}
