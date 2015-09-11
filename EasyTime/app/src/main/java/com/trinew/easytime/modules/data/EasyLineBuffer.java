package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.buffer.AbstractBuffer;
import com.trinew.easytime.models.ParseStamp;

import java.util.List;

/**
 * Created by Jonathan on 9/3/2015.
 */
public class EasyLineBuffer extends AbstractBuffer<EasyEntry> {

    protected int mDataSetIndex = 0;

    public EasyLineBuffer(int size) {
        super((size < 4) ? 4 : size);
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    public void moveTo(float x, float y) {

        if (index != 0)
            return;

        buffer[index++] = x;
        buffer[index++] = y;

        // in case just one entry, this is overwritten when lineTo is called
        buffer[index] = x;
        buffer[index + 1] = y;
    }

    public void lineTo(float x, float y) {

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

        int size = (int) Math.ceil((mTo - mFrom) * phaseY + mFrom);
        int from = mFrom + 1;

        int prevFlag = -1;

        for (int i = from; i < size; i++) {

            EasyEntry e = entries.get(i);
            float val = e.getVal();
            int flag = e.getFlag();

            // calculate the x-position, depending on datasetcount
            float y = mDataSetIndex * 2f;//mDataSetIndex * dataSetOffset 0.8f * e.getXIndex() + 0.8f / 2;
            float x = val * phaseX;

            if((prevFlag == -1 || prevFlag == ParseStamp.FLAG_CHECK_OUT) && flag == ParseStamp.FLAG_CHECK_IN) {
                moveTo(x, y);
                prevFlag = flag;
            } else if(prevFlag == ParseStamp.FLAG_CHECK_IN && flag == ParseStamp.FLAG_CHECK_OUT) {
                lineTo(x, y);
                prevFlag = flag;
            }
        }

        reset();
    }
}