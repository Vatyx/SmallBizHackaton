package com.trinew.easytime.modules.stamps;

import com.trinew.easytime.models.ParseStamp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanlu on 9/2/15.
 */
public class StampCollection {
    private List<ParseStamp> stamps = new ArrayList<>();

    // the day the collection belongs to
    private int collectionDay;

    public StampCollection(int day) {
        collectionDay = day;
    }

    public List<ParseStamp> getStamps() {
        return stamps;
    }

    public int getCollectionDay() {
        return collectionDay;
    }

    public void addStamp(ParseStamp stamp) {
        stamps.add(stamp);
    }
}
