package com.trinew.easytime.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Stamp")
public class ParseStamp extends ParseObject {

    // key identifiers
    public static final String STAMP_KEY_FLAG = "flag";
    public static final String STAMP_KEY_COMMENT = "comment";
    public static final String STAMP_KEY_LOCATION = "location";

    // flag constants
    public static final int FLAG_CHECK_IN = 0;
    public static final int FLAG_CHECK_OUT = 1;

    public String getFlag() {
        return getString(STAMP_KEY_FLAG);
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
}