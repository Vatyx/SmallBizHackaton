package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.buffer.AbstractBuffer;

import java.util.List;

/**
 * Created by jonathanlu on 9/6/15.
 */
public class EasyCircleBuffer extends AbstractBuffer<EasyEntry> {

    protected int mDataSetIndex = 0;

    public EasyCircleBuffer(int size) {
        super(size);
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    protected void addCircle(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(List<EasyEntry> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseY + mFrom);

        for (int i = mFrom; i < size; i++) {

            float y = mDataSetIndex * 2f;
            EasyEntry e = entries.get(i);
            addCircle(e.getVal() * phaseX, y);
        }

        reset();
    }
}