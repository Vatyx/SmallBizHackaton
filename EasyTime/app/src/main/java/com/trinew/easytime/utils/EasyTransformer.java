package com.trinew.easytime.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.trinew.easytime.modules.data.EasyData;

import java.util.List;

/**
 * Created by Jonathan on 9/4/2015.
 */
public class EasyTransformer extends Transformer {

    public EasyTransformer(ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
    }

    /**
     * Prepares the matrix that contains all offsets.
     */
    public void prepareMatrixOffset(boolean inverted) {

        mMatrixOffset.reset();

        // offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        if (!inverted)
            mMatrixOffset.postTranslate(mViewPortHandler.offsetLeft(),
                    mViewPortHandler.getChartHeight() - mViewPortHandler.offsetBottom());
        else {
            mMatrixOffset
                    .setTranslate(
                            -(mViewPortHandler.getChartWidth() - mViewPortHandler.offsetRight()),
                            mViewPortHandler.getChartHeight() - mViewPortHandler.offsetBottom());
            mMatrixOffset.postScale(-1.0f, 1.0f);
        }

        // mMatrixOffset.set(offset);

        // mMatrixOffset.reset();
        //
        // mMatrixOffset.postTranslate(mOffsetLeft, getHeight() -
        // mOffsetBottom);
    }

    public float[] generateTransformedValuesEasy(List<? extends Entry> entries,
                                                               int dataSet, EasyData bd, float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        int setCount = bd.getDataSetCount();
        float space = bd.getGroupSpace();

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);
            int i = e.getXIndex();

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + i * (setCount - 1) + dataSet + space * i
                    + space / 2f ;
            float y = e.getVal();

            valuePoints[j] = y * phaseY;
            valuePoints[j + 1] = x;
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }
}