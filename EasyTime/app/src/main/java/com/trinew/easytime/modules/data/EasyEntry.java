package com.trinew.easytime.modules.data;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by jonathanlu on 9/2/15.
 */
public class EasyEntry extends Entry {

    private int flag;

    public EasyEntry(float val, int xIndex, int flag) {
        super(val, xIndex);

        this.flag = flag;
    }

    public EasyEntry copy() {
        EasyEntry copied = new EasyEntry(getVal(), getXIndex(), getFlag());
        return copied;
    }

    @Override
    public float getVal() {
        return super.getVal();
    }

    public int getFlag() { return flag; }
}