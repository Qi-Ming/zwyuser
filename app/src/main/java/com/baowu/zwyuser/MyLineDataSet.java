package com.baowu.zwyuser;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class MyLineDataSet extends LineDataSet {
    public enum Orientation {UP, DOWN};
    private int cnt = 0;
    private Orientation orient;
    private double warn = 0;
    private double limit = 1;

    public MyLineDataSet(List<Entry> yVals, String label, double limit) {
        super(yVals, label);
        this.cnt = 1;
        this.limit = limit;
    }

    public MyLineDataSet(List<Entry> yVals, String label, double warn, double limit, Orientation orient) {
        super(yVals, label);
        this.cnt = 2;
        this.orient = orient;
        this.warn = warn;
        this.limit = limit;
    }

    public MyLineDataSet(List<Entry> yVals, String label, Orientation orient) {
        super(yVals,label);
        this.orient = orient;
        this.cnt = 3;
    }

    public MyLineDataSet(List<Entry> yVals, String label, double limit, Orientation orient) {
        super(yVals, label);
        this.cnt = 4;
        this.limit = limit;
        this.orient = orient;
    }
}
