package com.baowu.zwyuser.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SelectorItem implements Parcelable {
    private String data;
    private int id;

    public SelectorItem(String data, int id) {
        this.data = data;
        this.id = id;
    }

    protected SelectorItem(Parcel in) {
        data = in.readString();
        id = in.readInt();
    }

    public static final Creator<SelectorItem> CREATOR = new Creator<SelectorItem>() {
        @Override
        public SelectorItem createFromParcel(Parcel in) {
            return new SelectorItem(in);
        }

        @Override
        public SelectorItem[] newArray(int size) {
            return new SelectorItem[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
        parcel.writeInt(id);
    }
}
