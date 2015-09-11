package com.trinew.easytime.views.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.FillFormatter;
import com.trinew.easytime.modules.data.EasyData;
import com.trinew.easytime.modules.data.EasyDataProvider;
import com.trinew.easytime.modules.data.EasyDataSet;

public class EasyChart extends BarLineChartBase<EasyData> implements EasyDataProvider {

    private EasyFillFormatter mFillFormatter;

    public EasyChart(Context context) {
        super(context);
    }

    public EasyChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new EasyChartRenderer(this, mAnimator, mViewPortHandler);
        mFillFormatter = new EasyFillFormatter();
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        if (mDeltaX == 0 && mData.getYValCount() > 0)
            mDeltaX = 1;
    }

    @Override
    public void setFillFormatter(EasyFillFormatter formatter) {

        if (formatter == null)
            formatter = new EasyFillFormatter();
        else
            mFillFormatter = formatter;
    }

    @Override
    public EasyFillFormatter getFillFormatter() {
        return mFillFormatter;
    }

    @Override
    public EasyData getEasyData() {
        return mData;
    }

    public class EasyFillFormatter implements FillFormatter {

        @Override
        public float getFillLinePosition(LineDataSet dataSet, LineData data, float chartMaxY, float chartMinY) {
            return 0f;
        }

        public float getEasyFillLinePosition(EasyDataSet dataSet, EasyData data,
                                             float chartMaxY, float chartMinY) {

            float fillMin = 0f;

            if (dataSet.getYMax() > 0 && dataSet.getYMin() < 0) {
                fillMin = 0f;
            } else {

                if (!getAxis(dataSet.getAxisDependency()).isStartAtZeroEnabled()) {

                    float max, min;

                    if (data.getYMax() > 0)
                        max = 0f;
                    else
                        max = chartMaxY;
                    if (data.getYMin() < 0)
                        min = 0f;
                    else
                        min = chartMinY;

                    fillMin = dataSet.getYMin() >= 0 ? min : max;
                } else {
                    fillMin = 0f;
                }

            }

            return fillMin;
        }
    }
}