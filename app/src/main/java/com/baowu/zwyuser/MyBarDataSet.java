package com.baowu.zwyuser;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class MyBarDataSet extends BarDataSet {
    public enum Orientation {UP, DOWN};
    private int cnt = 0;
    private Orientation orient;
    private double warn = 0;
    private double limit = 1;

    public MyBarDataSet(List<BarEntry> yVals, String label, double limit) {
        super(yVals, label);
        this.cnt = 1;
        this.limit = limit;
    }

    public MyBarDataSet(List<BarEntry> yVals, String label, double warn, double limit, Orientation orient) {
        super(yVals, label);
        this.cnt = 2;
        this.orient = orient;
        this.warn = warn;
        this.limit = limit;
    }

    public MyBarDataSet(List<BarEntry> yVals, String label, Orientation orient) {
        super(yVals,label);
        this.orient = orient;
        this.cnt = 3;
    }

    public MyBarDataSet(List<BarEntry> yVals, String label,double limit, Orientation orient) {
        super(yVals, label);
        this.cnt = 4;
        this.limit = limit;
        this.orient = orient;
    }


    @Override
    public int getColor(int index) {
        if (index <= getEntryCount()) {
            if (cnt == 1) {
                if ((getEntryForIndex(index).getY() <= this.limit && this.limit >= 0) ||
                        (getEntryForIndex(index).getY() >= this.limit && this.limit <= 0))
                    return mColors.get(2);
                else
                    return mColors.get(0);
            }
            else if (cnt == 2) {
                if ((getEntryForIndex(index).getY() >= this.limit && orient == Orientation.UP) ||
                        (getEntryForIndex(index).getY() <= this.limit && orient == Orientation.DOWN))
                    return mColors.get(0);
                else if ((getEntryForIndex(index).getY() >= this.warn && orient == Orientation.UP) ||
                        (getEntryForIndex(index).getY() <= this.warn && orient == Orientation.DOWN))
                    return mColors.get(1);
                else
                    return mColors.get(2);
            } else if (cnt == 3) {
                return mColors.get(2);
            } else if (cnt == 4) {
                if ((getEntryForIndex(index).getY() > this.limit && orient == Orientation.UP) ||
                        (getEntryForIndex(index).getY() < this.limit && orient == Orientation.DOWN)) {
                    return mColors.get(0);
                } else {
                    return mColors.get(2);
                }
            }
        }
        return super.getColor(index);
    }
}
