package com.trinew.easytime.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

@ParseClassName("Stamp")
public class ParseStamp extends ParseObject {

    // key identifiers
    public static final String STAMP_KEY_FLAG = "flag";
    public static final String STAMP_KEY_LOG_DATE = "logDate";
    public static final String STAMP_KEY_COMMENT = "comment";
    public static final String STAMP_KEY_LOCATION = "location";

    // flag constants
    public static final int FLAG_CHECK_IN = 0;
    public static final int FLAG_CHECK_OUT = 1;

    public Date getLogDate() { return getDate(STAMP_KEY_LOG_DATE); }
    public void setLogDate(Date logDate) { put(STAMP_KEY_LOG_DATE, logDate); }

    public int getFlag() {
        return getInt(STAMP_KEY_FLAG);
    }
    public void setFlag(int flag) {
        put(STAMP_KEY_FLAG, flag);
    }

    public String getComment() {
        return getString(STAMP_KEY_COMMENT);
    }
    public void setComment(String comment) {
        put(STAMP_KEY_COMMENT, comment);
    }

    public ParseGeoPoint getLocation() { return getParseGeoPoint(STAMP_KEY_LOCATION); }
    public void setLocation(ParseGeoPoint location) { put(STAMP_KEY_LOCATION, location); }

    // returns long day time value from log date
    public long getLogTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getLogDate());
        long hourOfDayMillis = (long) (calendar.get(Calendar.HOUR_OF_DAY) * 3600000);
        long minuteOfHourMillis = (long) (calendar.get(Calendar.MINUTE) * 60000);
        return hourOfDayMillis + minuteOfHourMillis;
    }
}