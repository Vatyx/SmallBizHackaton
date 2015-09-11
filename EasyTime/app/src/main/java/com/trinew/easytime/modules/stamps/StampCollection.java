package com.trinew.easytime.modules.stamps;

import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.utils.StampHelper;

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

    public int size() { return stamps.size(); }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void addStamp(ParseStamp stamp) {
        stamps.add(stamp);
    }

    // calculates duration spanned by collection stamps
    // we iterate through each stamp
    // when we hit a stamp with the IN flag we store the time of that stamp temporarily
    // when we hit a stamp with the OUT flag we calculate the difference between the time of
    // that stamp and the currently stored IN time and add it to the duration sum
    //
    // IN IN OUT
    // IN OUT IN
    // OUT IN OUT
    // IN OUT OUT
    public long getDuration() {
        return StampHelper.getStampListDuration(stamps);
    }
}