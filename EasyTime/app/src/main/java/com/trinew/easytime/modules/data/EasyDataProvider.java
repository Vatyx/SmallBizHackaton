package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider;

/**
 * Created by Jonathan on 9/3/2015.
 */
public interface EasyDataProvider extends BarLineScatterCandleBubbleDataProvider {
    EasyData getEasyData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isDrawHighlightArrowEnabled();
}