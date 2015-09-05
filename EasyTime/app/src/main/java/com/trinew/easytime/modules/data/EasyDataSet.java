package com.trinew.easytime.modules.data;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarLineScatterCandleDataSet;
import com.github.mikephil.charting.data.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanlu on 9/2/15.
 */
public class EasyDataSet extends BarLineScatterCandleDataSet<EasyEntry> {

    /**
     * the alpha value used to draw the highlight indicator bar
     */
    private int mHighLightAlpha = 120;

    public EasyDataSet(List<EasyEntry> yVals, String label) {
        super(yVals, label);

        mHighLightColor = Color.rgb(0, 0, 0);
    }

    @Override
    public DataSet<EasyEntry> copy() {

        List<EasyEntry> yVals = new ArrayList<>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add((mYVals.get(i)).copy());
        }

        EasyDataSet copied = new EasyDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;
        copied.mHighLightAlpha = mHighLightAlpha;

        return copied;
    }

    @Override
    protected void calcMinMax(int start, int end) {
        final int yValCount = mYVals.size();

        if (yValCount == 0)
            return;

        int endValue;

        if (end == 0 || end >= yValCount)
            endValue = yValCount - 1;
        else
            endValue = end;

        mLastStart = start;
        mLastEnd = endValue;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {

            EasyEntry e = mYVals.get(i);

            if (e != null && e.getStamps() != null && !Float.isNaN(e.getVal())) {
                if (e.getVal() < mYMin)
                    mYMin = Math.min(e.getMinValue(), mYMin);

                if (e.getVal() > mYMax)
                    mYMax = Math.max(e.getMaxValue(), mYMax);
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = 0.f;
            mYMax = 0.f;
        }
    }

    /**
     * Set the alpha value (transparency) that is used for drawing the highlight
     * indicator bar. min = 0 (fully transparent), max = 255 (fully opaque)
     *
     * @param alpha
     */
    public void setHighLightAlpha(int alpha) {
        mHighLightAlpha = alpha;
    }

    /**
     * Returns the alpha value (transparency) that is used for drawing the
     * highlight indicator.
     *
     * @return
     */
    public int getHighLightAlpha() {
        return mHighLightAlpha;
    }
}