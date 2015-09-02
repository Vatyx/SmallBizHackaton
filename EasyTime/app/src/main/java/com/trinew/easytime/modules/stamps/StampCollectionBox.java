package com.trinew.easytime.modules.stamps;

import android.util.Log;

import com.trinew.easytime.models.ParseStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jonathanlu on 9/2/15.
 *
 * Helper stamp collection class. Stamps are organized into collections based on
 * the day that they were created.
 */
public class StampCollectionBox {

    private List<StampCollection> collectionsList = new ArrayList<>();

    private int minHour;
    private int maxHour;

    public StampCollectionBox(List<ParseStamp> stamps) {
        // here initialize the collection lists
        Calendar stampCalendar = Calendar.getInstance();
        int numDays = stampCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);

        int i = 0;
        int j = 0;

        // time to fill the lists
        int currMinHour = 0;
        int currMaxHour = 24;
        for (i = 0; i < stamps.size(); i++) {
            if(stamps.get(i) == null) {
                Log.e("StampCollectionBox", "Got a null stamp!");
                continue;
            }

            ParseStamp stamp = stamps.get(i);
            Date stampTime = stamp.getCreatedAt();
            stampCalendar.setTime(stampTime);
            int stampDay = stampCalendar.get(Calendar.DAY_OF_YEAR);
            int stampHour = stampCalendar.get(Calendar.HOUR_OF_DAY);

            if(stampHour > currMinHour)
                currMinHour = stampHour;

            if(stampHour < currMaxHour)
                currMaxHour = stampHour;

            // search for the appropriate stamp collection if available otherwise create it
            StampCollection stampCollection = null;
            for(j = 0; j < collectionsList.size(); j++) {
                if(collectionsList.get(j).getCollectionDay() == stampDay) {
                    stampCollection = collectionsList.get(j);
                    break;
                }
            }
            if(j == collectionsList.size()) {
                stampCollection = new StampCollection(stampDay);
                collectionsList.add(stampCollection);
            }

            stampCollection.addStamp(stamps.get(i));
        }
    }

    // get a list of valid stamp collections
    public List<StampCollection> getStampCollections() {
        return collectionsList;
    }

    public int getMinHour() {
        return minHour;
    }

    public int getMaxHour() {
        return maxHour;
    }
}
