package com.trinew.easytime.utils;

import com.trinew.easytime.models.ParseStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jonathanlu on 9/10/15.
 */
public class StampHelper {

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
    public static long getStampListDuration(List<ParseStamp> stamps) {
        if(stamps.size() < 2) return 0;

        long duration = 0;

        int prevFlag = -1;

        // store the currently evaluated check in time
        // every time we hit a check out flag we add to the duration
        long currInTime = 0;
        for (ParseStamp stamp : stamps) {
            int stampFlag = stamp.getFlag();
            if((prevFlag == -1 || prevFlag == ParseStamp.FLAG_CHECK_OUT) && stampFlag == ParseStamp.FLAG_CHECK_IN) {
                prevFlag = stampFlag;
                currInTime = stamp.getLogTimeOfDay();
            } else if(prevFlag == ParseStamp.FLAG_CHECK_IN && stampFlag == ParseStamp.FLAG_CHECK_OUT) {
                prevFlag = stampFlag;
                duration += stamp.getLogTimeOfDay() - currInTime;
            }
        }

        return duration;
    }

    public static List<ParseStamp> getTodayStamps(List<ParseStamp> stamps) {
        List<ParseStamp> resultList = new ArrayList<>();
        Calendar todayCalendar = Calendar.getInstance();
        Calendar currCalendar = Calendar.getInstance();
        for(int i = 0; i < stamps.size(); i++) {
            currCalendar.setTime(stamps.get(i).getLogDate());
            if((todayCalendar.get(Calendar.YEAR) == currCalendar.get(Calendar.YEAR)) &&
                    (todayCalendar.get(Calendar.DAY_OF_YEAR) == currCalendar.get(Calendar.DAY_OF_YEAR))) {
                resultList.add(stamps.get(i));
            }
        }

        return resultList;
    }
}
