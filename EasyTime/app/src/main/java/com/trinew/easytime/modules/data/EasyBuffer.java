package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.buffer.AbstractBuffer;

import java.util.List;

/**
 * Created by Jonathan on 9/3/2015.
 */
public class EasyBuffer extends AbstractBuffer<EasyEntry> {

    protected float mGroupSpace = 0f;
    protected int mDataSetIndex = 0;
    protected int mDataSetCount = 1;

    // hold flags for each data point
    public final int[] flagBuffer;

    public EasyBuffer(int size, float groupspace, int dataSetCount) {
        super(size);

        flagBuffer = new int[size];

        //super((size < 4) ? 4 : size);
        this.mGroupSpace = groupspace;
        this.mDataSetCount = dataSetCount;
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    public void moveTo(float x, float y, int flag) {

        if (index != 0)
            return;

        flagBuffer[index] = flag;
        buffer[index++] = x;
        buffer[index++] = y;

        // in case just one entry, this is overwritten when lineTo is called
        buffer[index] = x;
        buffer[index + 1] = y;
    }

    public void lineTo(float x, float y, int flag) {

        if (index == 2) {
            buffer[index++] = x;
            buffer[index++] = y;
        } else {

            float prevX = buffer[index - 2];
            float prevY = buffer[index - 1];
            buffer[index++] = prevX;
            buffer[index++] = prevY;
            buffer[index++] = x;
            buffer[index++] = y;
        }
    }

    @Override
    public void feed(List<EasyEntry> entries) {

        int size = (int) Math.ceil((mTo - mFrom) * phaseX + mFrom);
        int from = mFrom + 1;

        int dataSetOffset = (mDataSetCount - 1);
        float groupSpaceHalf = mGroupSpace / 2f;

        for (int i = from; i < size; i++) {

            EasyEntry e = entries.get(i);

            // calculate the x-position, depending on datasetcount
            float y = e.getXIndex() + e.getXIndex() * dataSetOffset + mDataSetIndex
                    + mGroupSpace * e.getXIndex() + groupSpaceHalf;
            float[] vals = e.getStampValues();
            int[] flags = e.getStampFlags();

            // fill the stack
            for (int k = 0; k < vals.length; k++) {
                float x1 = vals[k] * phaseY;
                int f1 = flags[k];

                moveTo(x1, y);

                if(k + 1 < vals.length) {

                    float x2 = vals[k + 1] * phaseY;
                    int f2 = flags[k + 1];

                    lineTo(x2, y);
                }
            }
        }

        reset();
    }
}