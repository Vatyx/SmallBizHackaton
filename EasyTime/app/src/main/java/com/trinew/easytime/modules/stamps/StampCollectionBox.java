package com.trinew.easytime.modules.stamps;

import android.util.Log;

import com.trinew.easytime.models.ParseStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by jonathanlu on 9/2/15.
 *
 * Helper stamp collection class. Stamps are organized into collections based on
 * the day that they were created. The collections are sorted based on their day.
 */
public class StampCollectionBox {

    private List<ParseStamp> mStamps = new ArrayList<>();
    private List<StampCollection> collectionsList = new ArrayList<>();

    private int minHour;
    private int maxHour;

    public StampCollectionBox(List<ParseStamp> stamps) {
        feed(stamps);
    }

    public void feed(List<ParseStamp> stamps) {
        mStamps = stamps;
        sortStamps();
    }

    // arranges the raw stamp list into sorted list of collections
    private void sortStamps() {
        // here initialize the collection lists
        Calendar stampCalendar = Calendar.getInstance();

        collectionsList.clear();

        int i = 0;
        int j = 0;

        // time to fill the lists
        minHour = 24;
        maxHour = 0;
        for (i = 0; i < mStamps.size(); i++) {
            if(mStamps.get(i) == null) {
                Log.e("StampCollectionBox", "Got a null stamp!");
                continue;
            }

            ParseStamp stamp = mStamps.get(i);
            Date stampDate = stamp.getLogDate();
            stampCalendar.setTime(stampDate);
            int stampHour = stampCalendar.get(Calendar.HOUR_OF_DAY);

            //Log.i("StampCollectionBox", "Stamp = " + stampDay + " : " + stampHour);

            if(stampHour < minHour)
                minHour = stampHour;

            if(stampHour > maxHour)
                maxHour = stampHour;

            // search for the appropriate stamp collection if available otherwise create it
            StampCollection stampCollection = null;
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(stampDate);

            for(j = 0; j < collectionsList.size(); j++) {
                cal1.setTime(collectionsList.get(j).getCollectionDate());
                if((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) && (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))) {
                    stampCollection = collectionsList.get(j);
                    break;
                }
            }
            if(j == collectionsList.size()) {
                stampCollection = new StampCollection(stampDate);
                collectionsList.add(stampCollection);
                Calendar temp = Calendar.getInstance();
                temp.setTime(stampDate);
                Log.i("StampCollectionBox", "Added year: " + temp.get(Calendar.YEAR));
            }

            stampCollection.addStamp(stamp);
        }

        Collections.sort(collectionsList, new Comparator<StampCollection>() {
            @Override
            public int compare(StampCollection t0, StampCollection t1) {
                return (int) (t1.getCollectionDate().getTime() - t0.getCollectionDate().getTime());
            }
        });
    }

    // get a list of valid stamp collections
    public List<StampCollection> getStampCollections() {
        return collectionsList;
    }

    public int getMaxSize() {
        int max = 0;
        for(int i = 0; i < collectionsList.size(); i++) {
            max = Math.max(collectionsList.get(i).size(), max);
        }
        return max;
    }

    public int getMinHour() {
        return minHour;
    }

    public int getMaxHour() {
        return maxHour;
    }
}
