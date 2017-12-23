package com.bian.mydevsample.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author 边凌
 * date 2017/12/7 14:43
 * 类描述：
 */

public class Test implements Parcelable {

    public static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel source) {
            return new Test(source);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };
    private int tag;
    private String title;

    public Test() {
    }

    protected Test(Parcel in) {
        this.tag = in.readInt();
        this.title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tag);
        dest.writeString(this.title);
    }
}
