package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.data.BarLineScatterCandleData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanlu on 9/6/15.
 */
public class EasyData extends BarLineScatterCandleData<EasyDataSet> {

    public EasyData() {
        super();
    }

    public EasyData(List<String> xVals) {
        super(xVals);
    }

    public EasyData(String[] xVals) {
        super(xVals);
    }

    public EasyData(List<String> xVals, List<EasyDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public EasyData(String[] xVals, List<EasyDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public EasyData(List<String> xVals, EasyDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public EasyData(String[] xVals, EasyDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<EasyDataSet> toList(EasyDataSet dataSet) {
        List<EasyDataSet> sets = new ArrayList<EasyDataSet>();
        sets.add(dataSet);
        return sets;
    }
}