package com.github.fragmentstatepageradapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caolijie on 16/4/14.
 */
public class Item implements Parcelable {
    public long id;
    public String text;

    public Item(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.text);
    }

    public Item() {
    }

    protected Item(Parcel in) {
        this.id = in.readLong();
        this.text = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
