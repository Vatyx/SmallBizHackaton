package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.buffer.AbstractBuffer;

import java.util.List;

/**
 * Created by Jonathan on 9/3/2015.
 */
public class EasyBuffer extends AbstractBuffer<EasyEntry> {

    protected float mBarSpace = 0f;
    protected float mGroupSpace = 0f;
    protected int mDataSetIndex = 0;
    protected int mDataSetCount = 1;
    protected boolean mContainsStacks = false;
    protected boolean mInverted = false;

    public EasyBuffer(int size, float groupspace, int dataSetCount, boolean containsStacks) {
        super(size);
        this.mGroupSpace = groupspace;
        this.mDataSetCount = dataSetCount;
        this.mContainsStacks = containsStacks;
    }

    public void setBarSpace(float barspace) {
        this.mBarSpace = barspace;
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    public void setInverted(boolean inverted) {
        this.mInverted = inverted;
    }

    protected void addBar(float left, float top, float right, float bottom) {

        buffer[index++] = left;
        buffer[index++] = top;
        buffer[index++] = right;
        buffer[index++] = bottom;
    }

    @Override
    public void feed(List<EasyEntry> entries) {

        float size = entries.size() * phaseX;

        int dataSetOffset = (mDataSetCount - 1);
        float barSpaceHalf = mBarSpace / 2f;
        float groupSpaceHalf = mGroupSpace / 2f;
        float barWidth = 0.5f;

        for (int i = 0; i < size; i++) {

            EasyEntry e = entries.get(i);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + e.getXIndex() * dataSetOffset + mDataSetIndex
                    + mGroupSpace * e.getXIndex() + groupSpaceHalf;
            float y = e.getVal();
            float[] vals = e.getStampVals();

            float posY = 0f;
            float yStart = 0f;

            // fill the stack
            for (int k = 0; k < vals.length; k++) {

                float value = vals[k];

                y = posY;
                yStart = posY + value;
                posY = yStart;

                float bottom = x - barWidth + barSpaceHalf;
                float top = x + barWidth - barSpaceHalf;
                float left, right;
                if (mInverted) {
                    left = y >= yStart ? y : yStart;
                    right = y <= yStart ? y : yStart;
                } else {
                    right = y >= yStart ? y : yStart;
                    left = y <= yStart ? y : yStart;
                }

                // multiply the height of the rect with the phase
                right *= phaseY;
                left *= phaseY;

                addBar(left, top, right, bottom);
            }
        }

        reset();
    }
}