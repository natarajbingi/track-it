package com.a.goldtrack.camera;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BytePojo implements Serializable {

    byte[] bytes;




    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


}
