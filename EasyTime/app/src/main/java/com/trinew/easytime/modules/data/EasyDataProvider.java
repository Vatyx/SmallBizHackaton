package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider;
import com.trinew.easytime.views.charts.EasyChart;

/**
 * Created by jonathanlu on 9/6/15.
 */
public interface EasyDataProvider extends BarLineScatterCandleBubbleDataProvider {

    public EasyData getEasyData();

    /**
     * Sets a custom FillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     *
     * @param formatter
     */
    public void setFillFormatter(EasyChart.EasyFillFormatter formatter);

    /**
     * Returns the FillFormatter that handles the position of the filled-line.
     *
     * @return
     */
    public EasyChart.EasyFillFormatter getFillFormatter();
}