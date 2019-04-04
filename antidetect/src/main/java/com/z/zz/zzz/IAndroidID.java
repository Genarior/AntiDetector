package com.z.zz.zzz;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public abstract class IAndroidID {

    protected static Context context;

    public abstract String getAndroidID();

    static IAndroidID newInstance(Context ctx) {
        context = ctx;
        final int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < Build.VERSION_CODES.CUPCAKE) {
            return new SystemAndroidID();
        } else {
            return new SecureAndroidID();
        }
    }

    private static class SystemAndroidID extends IAndroidID {

        @SuppressWarnings("deprecation")
        @Override
        public String getAndroidID() {
            return Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);
        }

    }

    private static class SecureAndroidID extends IAndroidID {

        @Override
        public String getAndroidID() {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }

    }
}
