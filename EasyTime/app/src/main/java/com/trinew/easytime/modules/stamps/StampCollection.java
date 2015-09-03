package com.trinew.easytime.modules.stamps;

import com.trinew.easytime.models.ParseStamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jonathanlu on 9/2/15.
 */
public class StampCollection {
    private List<ParseStamp> stamps = new ArrayList<>();

    // the date representing the day the collection belongs to
    private Date collectionDate;

    public StampCollection(Date date) {
        collectionDate = date;
    }

    public List<ParseStamp> getStamps() {
        return stamps;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void addStamp(ParseStamp stamp) {
        stamps.add(stamp);
    }
}
