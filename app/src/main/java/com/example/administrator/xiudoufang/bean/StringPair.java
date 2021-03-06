package com.example.administrator.xiudoufang.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class StringPair implements Parcelable {

    private String key;
    private String value;
    private boolean isShowLine;
    private boolean isShowNext;
    private boolean isToogleButton;

    public StringPair(String key) {
        this.key = key;
    }

    public StringPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public StringPair(String key, String value, boolean isShowLine) {
        this.key = key;
        this.value = value;
        this.isShowLine = isShowLine;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isShowLine() {
        return isShowLine;
    }

    public StringPair setShowLine(boolean showLine) {
        isShowLine = showLine;
        return this;
    }

    public boolean isShowNext() {
        return isShowNext;
    }

    public StringPair setShowNext(boolean showNext) {
        isShowNext = showNext;
        return this;
    }

    public boolean isToogleButton() {
        return isToogleButton;
    }

    public StringPair setToogleButton(boolean toogleButton) {
        isToogleButton = toogleButton;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringPair) {
            StringPair s2 = (StringPair) obj;
            return this.key.equals(s2.key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + key.hashCode();
        result = 37 * result + value.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
        dest.writeByte(this.isShowLine ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowNext ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isToogleButton ? (byte) 1 : (byte) 0);
    }

    protected StringPair(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
        this.isShowLine = in.readByte() != 0;
        this.isShowNext = in.readByte() != 0;
        this.isToogleButton = in.readByte() != 0;
    }

    public static final Creator<StringPair> CREATOR = new Creator<StringPair>() {
        @Override
        public StringPair createFromParcel(Parcel source) {
            return new StringPair(source);
        }

        @Override
        public StringPair[] newArray(int size) {
            return new StringPair[size];
        }
    };
}
