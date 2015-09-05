package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.data.Entry;
import com.trinew.easytime.models.ParseStamp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jonathanlu on 9/2/15.
 */
public class EasyEntry extends Entry {

    private List<ParseStamp> mStamps;

    private float minValue;
    private float maxValue;

    public EasyEntry(List<ParseStamp> stamps, int xIndex) {
        super(0f, xIndex);
        this.mStamps = stamps;
        calcMinMax();
    }

    public EasyEntry(List<ParseStamp> stamps, int xIndex, String label) {
        super(0f, xIndex, label);
        this.mStamps = stamps;
        calcMinMax();
    }

    public EasyEntry copy() {
        EasyEntry copied = new EasyEntry(mStamps, getXIndex());
        return copied;
    }

    public List<ParseStamp> getStamps() {
        return mStamps;
    }

    public float[] getStampValues() {
        float[] stampVals = new float[mStamps.size()];
        for(int i = 0; i < stampVals.length; i++) {
            stampVals[i] = mStamps.get(i).getTimeValue();
        }
        return stampVals;
    }

    public int[] getStampFlags() {
        int[] stampFlags = new int[mStamps.size()];
        for(int i = 0; i < stampFlags.length; i++) {
            stampFlags[i] = mStamps.get(i).getFlag();
        }
        return stampFlags;
    }

    public void setStamps(List<ParseStamp> stamps) {
        calcMinMax();
        setVal(maxValue);
        mStamps = stamps;

    }

    /**
     * Returns the value of this BarEntry. If the entry is stacked, it returns the positive sum of all values.
     *
     * @return
     */
    @Override
    public float getVal() {
        return super.getVal();
    }

    public float getMaxValue() { return maxValue; }
    public float getMinValue() { return minValue; }

    private void calcMinMax() {
        minValue = 24f;
        maxValue = 0f;
        Calendar calcCalendar = Calendar.getInstance();
        for(int i = 0; i < mStamps.size(); i++) {
            Date stampDate = mStamps.get(i).getCreatedAt();
            calcCalendar.setTime(stampDate);
            int stampHour = calcCalendar.get(Calendar.HOUR_OF_DAY);
            int stampMinute = calcCalendar.get(Calendar.MINUTE);
            float stampTime = (float) stampHour + (float) stampMinute / 60f;
            minValue = Math.min(stampTime, minValue);
            maxValue = Math.max(stampTime, maxValue);
        }
    }
}