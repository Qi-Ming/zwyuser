package com.baowu.zwyuser.bean;
import android.os.Parcel;
import android.os.Parcelable;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name = "设备报警信息")
public class BearingBushAlarmInfo implements Parcelable {

    @SmartColumn(id = 1, name = "间隙A")
    private double data1;

    @SmartColumn(id = 2, name = "间隙B")
    private double data2;

    @SmartColumn(id = 3, name = "位移C")
    private double data3;

    @SmartColumn(id = 4, name = "位移D")
    private double data4;

    @SmartColumn(id = 5, name = "负荷侧顶升量")
    private double data5;

    @SmartColumn(id = 6, name = "非负荷侧顶升量")
    private double data6;

    @SmartColumn(id = 7, name = "时间")
    private String alarmDate;

    public BearingBushAlarmInfo() {

    }

    protected BearingBushAlarmInfo(Parcel in) {
        data1 = in.readDouble();
        data2 = in.readDouble();
        data3 = in.readDouble();
        data4 = in.readDouble();
        data5 = in.readDouble();
        data6 = in.readDouble();
        alarmDate = in.readString();
    }

    public static final Creator<BearingBushAlarmInfo> CREATOR = new Creator<BearingBushAlarmInfo>() {
        @Override
        public BearingBushAlarmInfo createFromParcel(Parcel in) {
            return new BearingBushAlarmInfo(in);
        }

        @Override
        public BearingBushAlarmInfo[] newArray(int size) {
            return new BearingBushAlarmInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(data1);
        dest.writeDouble(data2);
        dest.writeDouble(data3);
        dest.writeDouble(data4);
        dest.writeDouble(data5);
        dest.writeDouble(data6);
        dest.writeString(alarmDate);
    }

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

    public double getData3() {
        return data3;
    }

    public void setData3(double data3) {
        this.data3 = data3;
    }

    public double getData4() {
        return data4;
    }

    public void setData4(double data4) {
        this.data4 = data4;
    }

    public double getData5() {
        return data5;
    }

    public void setData5(double data5) {
        this.data5 = data5;
    }

    public double getData6() {
        return data6;
    }

    public void setData6(double data6) {
        this.data6 = data6;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }
}
