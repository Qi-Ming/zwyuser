package com.baowu.zwyuser.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name = "设备报警信息")
public class BearingAlarmInfo implements Parcelable {
    @SmartColumn(id = 1, name = "位移A")
    private double data1;

    @SmartColumn(id = 2, name = "位移B")
    private double data2;

    @SmartColumn(id = 3, name = "时间")
    private String alarmDate;

    public BearingAlarmInfo() {}

    public double getData1() {
        return data1;
    }

    public void setData1(double data1) {
        this.data1 = data1;
    }

    public double getData2() {
        return data2;
    }

    public void setData2(double data2) {
        this.data2 = data2;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }

    protected BearingAlarmInfo(Parcel in) {
        data1 = in.readDouble();
        data2 = in.readDouble();
        alarmDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(data1);
        dest.writeDouble(data2);
        dest.writeString(alarmDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BearingAlarmInfo> CREATOR = new Creator<BearingAlarmInfo>() {
        @Override
        public BearingAlarmInfo createFromParcel(Parcel in) {
            return new BearingAlarmInfo(in);
        }

        @Override
        public BearingAlarmInfo[] newArray(int size) {
            return new BearingAlarmInfo[size];
        }
    };
}
