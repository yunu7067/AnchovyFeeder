package com.example.anchovyfeeder;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AlarmObject extends RealmObject {
    private String ALARM_TITLE;
    private Date ALARM_TIME;
    private Boolean IS_USING;

    public String getALARM_TITLE() {
        return ALARM_TITLE;
    }

    public void setALARM_TITLE(String ALARM_TITLE) {
        this.ALARM_TITLE = ALARM_TITLE;
    }

    public Date getALARM_TIME() {
        return ALARM_TIME;
    }

    public void setALARM_TIME(Date ALARM_TIME) {
        this.ALARM_TIME = ALARM_TIME;
    }

    public Boolean getIS_USING() {
        return IS_USING;
    }

    public void setIS_USING(Boolean IS_USING) {
        this.IS_USING = IS_USING;
    }
}
