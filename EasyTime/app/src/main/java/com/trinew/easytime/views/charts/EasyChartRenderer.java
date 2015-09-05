package com.trinew.easytime.views.charts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
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
import com.trinew.easytime.utils.EasyTransformer;

import java.util.List;

/**
 * Created by Jonathan on 9/3/2015.
 */
public class EasyChartRenderer extends DataRenderer {

    protected EasyDataProvider mChart;

    /** the rect object that is used for drawing the bars */
    protected RectF mBarRect = new RectF();

    protected EasyBuffer[] mBuffers;

    public EasyChartRenderer(EasyDataProvider chart, ChartAnimator animator,
                                      ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);

        this.mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mValuePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void initBuffers() {

        EasyData easyData = mChart.getEasyData();
        mBuffers = new EasyBuffer[easyData.getDataSetCount()];

        for (int i = 0; i < mBuffers.length; i++) {
            EasyDataSet set = easyData.getDataSetByIndex(i);
            mBuffers[i] = new EasyBuffer(set.getValueCount() * 2,
                    easyData.getGroupSpace(),
                    easyData.getDataSetCount());
        }
    }

    @Override
    public void drawData(Canvas c) {

        EasyData easyData = mChart.getEasyData();

        for (int i = 0; i < easyData.getDataSetCount(); i++) {

            EasyDataSet set = easyData.getDataSetByIndex(i);

            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    protected void drawDataSet(Canvas c, EasyDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        List<EasyEntry> entries = dataSet.getYVals();

        // initialize the buffer
        EasyBuffer buffer = mBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);

        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3]))
                break;

            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
                continue;

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

    protected void drawValue(Canvas c, String value, float xPos, float yPos) {
        c.drawText(value, xPos, yPos, mValuePaint);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        int setCount = mChart.getEasyData().getDataSetCount();

        for (int i = 0; i < indices.length; i++) {

            Highlight h = indices[i];
            int index = h.getXIndex();

            int dataSetIndex = h.getDataSetIndex();
            EasyDataSet set = mChart.getEasyData().getDataSetByIndex(dataSetIndex);

            if (set == null || !set.isHighlightEnabled())
                continue;

            float barspaceHalf = set.getBarSpace() / 2f;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            // check outofbounds
            if (index >= 0
                    && index < (mChart.getXChartMax() * mAnimator.getPhaseX()) / setCount) {

                EasyEntry e = set.getEntryForXIndex(index);

                if (e == null || e.getXIndex() != index)
                    continue;

                float groupspace = mChart.getEasyData().getGroupSpace();
                boolean isStack = h.getStackIndex() < 0 ? false : true;

                // calculate the correct x-position
                float x = index * setCount + dataSetIndex + groupspace / 2f
                        + groupspace * index;

                final float y1;
                final float y2;

                if (isStack) {
                    y1 = h.getRange().from;
                    y2 = h.getRange().to * mAnimator.getPhaseY();
                } else {
                    y1 = e.getVal();
                    y2 = 0.f;
                }

                prepareBarHighlight(x, y1, y2, barspaceHalf, trans);

                c.drawRect(mBarRect, mHighlightPaint);

                if (mChart.isDrawHighlightArrowEnabled()) {

                    mHighlightPaint.setAlpha(255);

                    // distance between highlight arrow and bar
                    float offsetY = mAnimator.getPhaseY() * 0.07f;

                    float[] values = new float[9];
                    trans.getPixelToValueMatrix().getValues(values);
                    final float xToYRel = Math.abs(values[Matrix.MSCALE_Y] / values[Matrix.MSCALE_X]);

                    final float arrowWidth = set.getBarSpace() / 2.f;
                    final float arrowHeight = arrowWidth * xToYRel;

                    final float yArrow = y1 > -y2 ? y1 : y1;

                    Path arrow = new Path();
                    arrow.moveTo(x + 0.4f, yArrow + offsetY);
                    arrow.lineTo(x + 0.4f + arrowWidth, yArrow + offsetY - arrowHeight);
                    arrow.lineTo(x + 0.4f + arrowWidth, yArrow + offsetY + arrowHeight);

                    trans.pathValueToPixel(arrow);
                    c.drawPath(arrow, mHighlightPaint);
                }
            }
        }
    }

    // assumes trans is an instanceof EasyTransformer
    public float[] getTransformedValues(Transformer trans, List<EasyEntry> entries,
                                        int dataSetIndex) {
        EasyTransformer easyTrans = (EasyTransformer) trans;
        return easyTrans.generateTransformedValuesEasy(entries, dataSetIndex,
                mChart.getEasyData(), mAnimator.getPhaseY());
    }

    protected boolean passesCheck() {
        return mChart.getEasyData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }

    @Override
    public void drawExtras(Canvas c) { }
}