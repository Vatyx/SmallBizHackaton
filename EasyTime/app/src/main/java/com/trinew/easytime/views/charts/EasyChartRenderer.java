package com.trinew.easytime.views.charts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.trinew.easytime.modules.data.EasyBuffer;
import com.trinew.easytime.modules.data.EasyData;
import com.trinew.easytime.modules.data.EasyDataProvider;
import com.trinew.easytime.modules.data.EasyDataSet;
import com.trinew.easytime.modules.data.EasyEntry;

import java.util.List;

/**
 * Created by Jonathan on 9/3/2015.
 */
public class EasyChartRenderer extends DataRenderer {

    protected EasyDataProvider mChart;

    /** the rect object that is used for drawing the bars */
    protected RectF mBarRect = new RectF();

    protected EasyBuffer[] mBarBuffers;

    protected Paint mShadowPaint;

    public EasyChartRenderer(EasyDataProvider chart, ChartAnimator animator,
                                      ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);

        this.mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mValuePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void initBuffers() {

        EasyData easyData = mChart.getEasyData();
        mBarBuffers = new EasyBuffer[easyData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            EasyDataSet set = easyData.getDataSetByIndex(i);
            mBarBuffers[i] = new EasyBuffer(set.getValueCount() * 4 * set.getStackSize(),
                    easyData.getGroupSpace(),
                    easyData.getDataSetCount(), set.isStacked());
        }
    }

    protected void drawDataSet(Canvas c, EasyDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        List<EasyEntry> entries = dataSet.getYVals();

        // initialize the buffer
        EasyBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setBarSpace(dataSet.getBarSpace());
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3]))
                break;

            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
                continue;

            if (mChart.isDrawBarShadowEnabled()) {
                c.drawRect(mViewPortHandler.contentLeft(), buffer.buffer[j + 1],
                        mViewPortHandler.contentRight(),
                        buffer.buffer[j + 3], mShadowPaint);
            }

            // Set the color for the currently drawn value. If the index
            // is
            // out of bounds, reuse colors.
            mRenderPaint.setColor(dataSet.getColor(j / 4));
            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
        }
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (passesCheck()) {

            List<EasyDataSet> dataSets = mChart.getEasyData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(5f);
            float posOffset = 0f;
            float negOffset = 0f;
            final boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mChart.getEasyData().getDataSetCount(); i++) {

                EasyDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);
                final float halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f;

                ValueFormatter formatter = dataSet.getValueFormatter();

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                List<EasyEntry> entries = dataSet.getYVals();

                float[] valuePoints = getTransformedValues(trans, entries, i);

                for (int j = 0; j < (valuePoints.length - 1) * mAnimator.getPhaseX(); j += 2) {

                    EasyEntry e = entries.get(j / 2);

                    float[] vals = e.getStampVals();

                    // we still draw stacked bars, but there is one
                    // non-stacked
                    // in between

                    float[] transformed = new float[vals.length * 2];

                    float posY = 0f;

                    for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                        float value = vals[idx];
                        float y;

                        posY += value;
                        y = posY;

                        transformed[k] = y * mAnimator.getPhaseY();
                    }

                    trans.pointValuesToPixel(transformed);

                    for (int k = 0; k < transformed.length; k += 2) {

                        float val = vals[k / 2];
                        String valueText = formatter.getFormattedValue(val);

                        // calculate the correct offset depending on the draw position of the value
                        float valueTextWidth = Utils.calcTextWidth(mValuePaint, valueText);
                        posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                        negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth;
                            negOffset = -negOffset - valueTextWidth;
                        }

                        float x = transformed[k]
                                + (val >= 0 ? posOffset : negOffset);
                        float y = valuePoints[j + 1];

                        if (!mViewPortHandler.isInBoundsTop(y))
                            break;

                        if (!mViewPortHandler.isInBoundsX(x))
                            continue;

                        if (!mViewPortHandler.isInBoundsBottom(y))
                            continue;

                        drawValue(c, valueText, x, y + halfTextHeight);
                    }
                }
            }
        }
    }

    protected void prepareBarHighlight(float x, float y1, float y2, float barspaceHalf,
                                       Transformer trans) {

        float top = x - 0.5f + barspaceHalf;
        float bottom = x + 0.5f - barspaceHalf;
        float left = y1;
        float right = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixelHorizontal(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public float[] getTransformedValues(Transformer trans, List<EasyEntry> entries,
                                        int dataSetIndex) {
        return trans.generateTransformedValuesHorizontalBarChart(entries, dataSetIndex,
                mChart.getEasyData(), mAnimator.getPhaseY());
    }

    protected boolean passesCheck() {
        return mChart.getEasyData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }
}