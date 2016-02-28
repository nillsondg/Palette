package com.example.dmitry.palette;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dmitry on 28.02.2016.
 */
public class Photo implements Parcelable {
    private long id;
    private String data;

    public Photo(long id, String data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public Photo(Parcel in) {
        this.id = in.readLong();
        this.data = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {

        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
