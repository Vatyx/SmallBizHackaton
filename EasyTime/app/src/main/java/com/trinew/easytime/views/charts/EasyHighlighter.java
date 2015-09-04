package com.trinew.easytime.views.charts;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.trinew.easytime.modules.data.EasyDataProvider;
import com.trinew.easytime.modules.data.EasyDataSet;
import com.trinew.easytime.modules.data.EasyEntry;

/**
 * Created by Jonathan on 9/4/2015.
 */
public class EasyHighlighter extends ChartHighlighter<EasyDataProvider> {

    public EasyHighlighter(EasyDataProvider chart) {
        super(chart);
    }

    @Override
    public Highlight getHighlight(float x, float y) {

        Highlight h = super.getHighlight(x, y);

        if (h == null)
            return h;
        else {

            EasyDataSet set = mChart.getEasyData().getDataSetByIndex(h.getDataSetIndex());

            if (set.isStacked()) {

                // create an array of the touch-point
                float[] pts = new float[2];
                pts[0] = y;

                // take any transformer to determine the x-axis value
                mChart.getTransformer(set.getAxisDependency()).pixelsToValue(pts);

                return getStackedHighlight(h, set, h.getXIndex(), h.getDataSetIndex(), pts[0]);
            } else
                return h;
        }
    }

    @Override
    protected int getXIndex(float x) {

        if (!mChart.getEasyData().isGrouped()) {

            // create an array of the touch-point
            float[] pts = new float[2];
            pts[1] = x;

            // take any transformer to determine the x-axis value
            mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);

            return (int) Math.round(pts[1]);
        } else {

            float baseNoSpace = getBase(x);

            int setCount = mChart.getEasyData().getDataSetCount();
            int xIndex = (int) baseNoSpace / setCount;

            int valCount = mChart.getData().getXValCount();

            if (xIndex < 0)
                xIndex = 0;
            else if (xIndex >= valCount)
                xIndex = valCount - 1;

            return xIndex;
        }
    }

    @Override
    protected int getDataSetIndex(int xIndex, float x, float y) {

        if (!mChart.getEasyData().isGrouped()) {
            return 0;
        } else {

            float baseNoSpace = getBase(x);

            int setCount = mChart.getEasyData().getDataSetCount();
            int dataSetIndex = (int) baseNoSpace % setCount;

            if (dataSetIndex < 0)
                dataSetIndex = 0;
            else if (dataSetIndex >= setCount)
                dataSetIndex = setCount - 1;

            return dataSetIndex;
        }
    }

    protected Highlight getStackedHighlight(Highlight old, EasyDataSet set, int xIndex, int dataSetIndex, double yValue) {

        EasyEntry entry = set.getEntryForXIndex(xIndex);

        if (entry == null || entry.getStampVals() == null)
            return old;

        Range[] ranges = getRanges(entry);
        int stackIndex = getClosestStackIndex(ranges, (float) yValue);

        Highlight h = new Highlight(xIndex, dataSetIndex, stackIndex, ranges[stackIndex]);
        return h;
    }

    protected int getClosestStackIndex(Range[] ranges, float value) {

        if (ranges == null)
            return 0;

        int stackIndex = 0;

        for (Range range : ranges) {
            if (range.contains(value))
                return stackIndex;
            else
                stackIndex++;
        }

        int length = ranges.length - 1;

        return (value > ranges[length].to) ? length : 0;
        //
        // float[] vals = e.getVals();
        //
        // if (vals == null)
        // return -1;
        //
        // int index = 0;
        // float remainder = e.getNegativeSum();
        //
        // while (index < vals.length - 1 && value > vals[index] + remainder) {
        // remainder += vals[index];
        // index++;
        // }
        //
        // return index;
    }

    protected Range[] getRanges(EasyEntry entry) {

        float[] values = entry.getStampVals();

        if (values == null)
            return null;

        float posRemain = 0f;

        Range[] ranges = new Range[values.length];

        for (int i = 0; i < ranges.length; i++) {

            float value = values[i];

            ranges[i] = new Range(posRemain, posRemain + value);
            posRemain += value;
        }

        return ranges;
    }

    /**
     * Returns the base y-value to the corresponding x-touch value in pixels.
     *
     * @param y
     * @return
     */
    protected float getBase(float y) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[1] = y;

        // take any transformer to determine the x-axis value
        mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
        float yVal = pts[1];

        int setCount = mChart.getEasyData().getDataSetCount();

        // calculate how often the group-space appears
        int steps = (int) ((float) yVal / ((float) setCount + mChart.getEasyData().getGroupSpace()));

        float groupSpaceSum = mChart.getEasyData().getGroupSpace() * (float) steps;

        float baseNoSpace = (float) yVal - groupSpaceSum;
        return baseNoSpace;
    }
}