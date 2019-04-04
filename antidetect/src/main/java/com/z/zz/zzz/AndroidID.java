package com.z.zz.zzz;


import android.content.Context;

public class AndroidID extends IAndroidID {
    private String androidId;

    public AndroidID(Context context) {
        androidId = newInstance(context).getAndroidID();
    }

    @Override
    public String getAndroidID() {
        return androidId;
    }

}
