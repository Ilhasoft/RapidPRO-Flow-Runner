package in.ureport.flowrunner.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by John Cordeiro on 4/18/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class Flow implements Parcelable {

    private String uuid;

    private String name;

    public String getUuid() {
        return uuid;
    }

    public Flow setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Flow setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.name);
    }

    public Flow() {
    }

    protected Flow(Parcel in) {
        this.uuid = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Flow> CREATOR = new Creator<Flow>() {
        @Override
        public Flow createFromParcel(Parcel source) {
            return new Flow(source);
        }

        @Override
        public Flow[] newArray(int size) {
            return new Flow[size];
        }
    };
}
