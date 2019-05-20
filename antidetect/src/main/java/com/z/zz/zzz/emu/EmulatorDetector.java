package com.z.zz.zzz.emu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.z.zz.zzz.AntiDetector.TAG;

/**
 * Copyright 2016 Framgia, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by Pham Quy Hai on 5/16/16.
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public final class EmulatorDetector {

    private static final String[] GENY_FILES = {
            "/dev/socket/genyd",
            "/dev/socket/baseband_genyd"
    };

    //    private static final String[] PHONE_NUMBERS = {
//            "15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564",
//            "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576",
//            "15555215578", "15555215580", "15555215582", "15555215584"
//    };
//
//    private static final String[] DEVICE_IDS = {
//            "000000000000000",
//            "e21833235b6eef10",
//            "012345678912345"
//    };
//
//    private static final String[] IMSI_IDS = {
//            "310260000000000"
//    };
    private static final String[] QEMU_DRIVERS = {"goldfish"};
    private static final String[] PIPES = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };
    private static final String[] X86_FILES = {
            "ueventd.android_x86.rc",
            "x86.prop",
            "ueventd.ttVM_x86.rc",
            "init.ttVM_x86.rc",
            "fstab.ttVM_x86",
            "fstab.vbox86",
            "init.vbox86.rc",
            "ueventd.vbox86.rc"
    };
    private static final String[] ANDY_FILES = {
            "fstab.andy",
            "ueventd.andy.rc"
    };
    private static final String[] NOX_FILES = {
            "fstab.nox",
            "init.nox.rc",
            "ueventd.nox.rc"
    };
    private static final Property[] PROPERTIES = {
            new Property("init.svc.qemud", null),
            new Property("init.svc.qemu-props", null),
            new Property("qemu.hw.mainkeys", null),
            new Property("qemu.sf.fake_camera", null),
            new Property("qemu.sf.lcd_density", null),
            new Property("ro.bootloader", "unknown"),
            new Property("ro.bootmode", "unknown"),
            new Property("ro.hardware", "goldfish"),
            new Property("ro.kernel.android.qemud", null),
            new Property("ro.kernel.qemu.gles", null),
            new Property("ro.kernel.qemu", "1"),
            new Property("ro.product.device", "generic"),
            new Property("ro.product.model", "sdk"),
            new Property("ro.product.name", "sdk"),
            new Property("ro.serialno", null)
    };
    private static final String IP = "10.0.2.15";
    private static final int MIN_PROPERTIES_THRESHOLD = 0x5;
    private static EmulatorDetector mEmulatorDetector;
    private static boolean isDebug = false;
    private final Context mContext;
    //    private boolean isTelephony = false;
    private boolean isCheckPackage = true;
    private List<String> mListPackageName = new ArrayList<>();
    private static JSONObject jsonDump;

    private EmulatorDetector(Context pContext) {
        mContext = pContext;
        mListPackageName.add("com.google.android.launcher.layouts.genymotion");
        mListPackageName.add("com.bluestacks");
        mListPackageName.add("com.bignox.app");
    }

    public static EmulatorDetector with(Context pContext) {
        if (pContext == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        if (mEmulatorDetector == null)
            mEmulatorDetector = new EmulatorDetector(pContext.getApplicationContext());
        return mEmulatorDetector;
    }

    private static String dumpBuildInfo() {
        JSONObject jo = new JSONObject();
        try {
            putJsonSafed(jo, "PD", Build.PRODUCT);
            putJsonSafed(jo, "MA", Build.MANUFACTURER);
            putJsonSafed(jo, "BR", Build.BRAND);
            putJsonSafed(jo, "DE", Build.DEVICE);
            putJsonSafed(jo, "MO", Build.MODEL);
            putJsonSafed(jo, "HW", Build.HARDWARE);
            putJsonSafed(jo, "BL", Build.BOOTLOADER);
            putJsonSafed(jo, "FP", Build.FINGERPRINT);
            putJsonSafed(jo, "SE", Build.SERIAL);
        } catch (Exception e) {
        }

        return jo.toString();
    }

    public static String dump() {
        if (jsonDump == null) {
            return null;
        }
        return jsonDump.toString();
    }

    public static JSONObject putJsonSafed(@NonNull JSONObject json, @NonNull String name, @NonNull Object value) {
        try {
            return json.put(name, value);
        } catch (JSONException e) {
        }

        return json;
    }

    static void log(String str) {
        if (isDebug) {
            Log.d(TAG, "Emu ---> " + str);
        }
    }

    public boolean isDebug() {
        return isDebug;
    }

//    public boolean isCheckTelephony() {
//        return isTelephony;
//    }

    public EmulatorDetector setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

//    public EmulatorDetector setCheckTelephony(boolean telephony) {
//        this.isTelephony = telephony;
//        return this;
//    }

    public boolean isCheckPackage() {
        return isCheckPackage;
    }

    public EmulatorDetector setCheckPackage(boolean chkPackage) {
        this.isCheckPackage = chkPackage;
        return this;
    }

    public EmulatorDetector addPackageName(String pPackageName) {
        this.mListPackageName.add(pPackageName);
        return this;
    }

    public EmulatorDetector addPackageName(List<String> pListPackageName) {
        this.mListPackageName.addAll(pListPackageName);
        return this;
    }

//    public void detect(final OnEmulatorDetectorListener pOnEmulatorDetectorListener) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean isEmulator = detect();
//                log(">>> This System is Emulator: " + isEmulator);
//                if (pOnEmulatorDetectorListener != null) {
//                    pOnEmulatorDetectorListener.onResult(isEmulator);
//                }
//            }
//        }).start();
//    }

    public List<String> getPackageNameList() {
        return mListPackageName;
    }

    public boolean detect() {
        jsonDump = new JSONObject();

        boolean result;

        // Check Emu flag
        int flag = EmulatorFinder.findEmulatorFeatureFlag(mContext);
        result = flag != 0x0;
        log(">>> Find emulator feature flag: " + result);
        try {
            putJsonSafed(jsonDump, "emu_flag", formatBinaryString(flag));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check Basic
        if (!result) {
            result = checkBasic() || hasEmulatorBuild();
            log(">>> Check Basic: " + result);
        }

        // Check Advanced
        if (!result) {
            result = checkAdvanced();
            log(">>> Check Advanced: " + result);
        }

        // Check Package Name
        if (!result) {
            result = checkPackageName();
            log(">>> Check Package Name: " + result);
        }

        log(dumpBuildInfo());

        return result;
    }

    private String formatBinaryString(int flag) {
        StringBuilder builder = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        String binStr = Integer.toBinaryString(flag);
        log("<<< binStr: " + binStr);

        CharSequence cs = binStr.subSequence(0, binStr.length());
        int len = cs.length();
        int k = 0;
        for (int i = len; i > 0; i--) {
            stack.add(cs.charAt(i - 1));
            if ((len - i + 1) % 3 == 0) {
                stack.add(',');
                k++;
            }
            k++;
        }
        for (int i = 0; i < k; i++) {
            builder.append(stack.pop());
        }
        return builder.toString();
    }

    private boolean hasEmulatorBuild() {
        String BOARD = Build.BOARD; // The name of the underlying board, like "unknown".
        // This appears to occur often on real hardware... that's sad
        // String BOOTLOADER = android.os.Build.BOOTLOADER; // The system bootloader version number.
        String BRAND = Build.BRAND; // The brand (e.g., carrier) the software is customized for, if any.
        // "generic"
        String DEVICE = Build.DEVICE; // The name of the industrial design. "generic"
        String HARDWARE = Build.HARDWARE; // The name of the hardware (from the kernel command line or
        // /proc). "goldfish"
        String MODEL = Build.MODEL; // The end-user-visible name for the end product. "sdk"
        String PRODUCT = Build.PRODUCT; // The name of the overall product.
        if ((BOARD.compareTo("unknown") == 0) /* || (BOOTLOADER.compareTo("unknown") == 0) */
                || (BRAND.compareTo("generic") == 0) || (DEVICE.compareTo("generic") == 0)
                || (MODEL.compareTo("sdk") == 0) || (PRODUCT.compareTo("sdk") == 0)
                || (HARDWARE.compareTo("goldfish") == 0)) {
            putJsonSafed(jsonDump, "emu_bld", true);
            return true;
        }
        putJsonSafed(jsonDump, "emu_bld", false);
        return false;
    }

    private boolean checkBasic() {
        boolean result = Build.FINGERPRINT.startsWith("generic")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.toLowerCase().contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.equals("goldfish")
                || Build.HARDWARE.equals("vbox86")
                || Build.PRODUCT.equals("sdk")
                || Build.PRODUCT.equals("google_sdk")
                || Build.PRODUCT.equals("sdk_x86")
                || Build.PRODUCT.equals("vbox86p")
                || Build.BOARD.toLowerCase().contains("nox")
                || Build.BOOTLOADER.toLowerCase().contains("nox")
                || Build.HARDWARE.toLowerCase().contains("nox")
                || Build.PRODUCT.toLowerCase().contains("nox")
                || Build.SERIAL.toLowerCase().contains("nox");

        if (result) {
            putJsonSafed(jsonDump, "bas", result);
            return true;
        }
        result |= Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");
        if (result) {
            putJsonSafed(jsonDump, "bas", result);
            return true;
        }
        result |= "google_sdk".equals(Build.PRODUCT);
        putJsonSafed(jsonDump, "bas", result);
        return result;
    }

    private boolean checkAdvanced() {
        boolean result = /*checkTelephony()
                ||*/ checkFiles(GENY_FILES, "Geny")
                || checkFiles(ANDY_FILES, "Andy")
                || checkFiles(NOX_FILES, "Nox")
                || checkQEmuDrivers()
                || checkFiles(PIPES, "Pipes")
                || checkIp()
                || (checkQEmuProps() && checkFiles(X86_FILES, "X86"));
        putJsonSafed(jsonDump, "adv", result);
        return result;
    }

//    private boolean checkTelephony() {
//        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
//                == PackageManager.PERMISSION_GRANTED && this.isTelephony && isSupportTelePhony()) {
//            return checkPhoneNumber()
//                    || checkDeviceId()
//                    || checkImsi()
//                    || checkOperatorNameAndroid();
//        }
//        return false;
//    }
//
//    private boolean checkPhoneNumber() {
//        TelephonyManager telephonyManager =
//                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//
//        @SuppressLint("HardwareIds") String phoneNumber = telephonyManager.getLine1Number();
//
//        for (String number : PHONE_NUMBERS) {
//            if (number.equalsIgnoreCase(phoneNumber)) {
//                log(" check phone number is detected");
//                return true;
//            }
//
//        }
//        return false;
//    }
//
//    private boolean checkDeviceId() {
//        TelephonyManager telephonyManager =
//                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//
//        @SuppressLint("HardwareIds") String deviceId = telephonyManager.getDeviceId();
//
//        for (String known_deviceId : DEVICE_IDS) {
//            if (known_deviceId.equalsIgnoreCase(deviceId)) {
//                log("Check device id is detected");
//                return true;
//            }
//
//        }
//        return false;
//    }
//
//    private boolean checkImsi() {
//        TelephonyManager telephonyManager =
//                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//        @SuppressLint("HardwareIds") String imsi = telephonyManager.getSubscriberId();
//
//        for (String known_imsi : IMSI_IDS) {
//            if (known_imsi.equalsIgnoreCase(imsi)) {
//                log("Check imsi is detected");
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean checkOperatorNameAndroid() {
//        String operatorName = ((TelephonyManager)
//                mContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
//        if (operatorName.equalsIgnoreCase("android")) {
//            log("Check operator name android is detected");
//            return true;
//        }
//        return false;
//    }

    private boolean checkPackageName() {
        if (!isCheckPackage || mListPackageName.isEmpty()) {
            putJsonSafed(jsonDump, "pkg", false);
            return false;
        }
        final PackageManager packageManager = mContext.getPackageManager();
        for (final String pkgName : mListPackageName) {
            final Intent tryIntent = packageManager.getLaunchIntentForPackage(pkgName);
            if (tryIntent != null) {
                final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(tryIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!resolveInfos.isEmpty()) {
                    putJsonSafed(jsonDump, "pkg", true);
                    return true;
                }
            }
        }
        putJsonSafed(jsonDump, "pkg", false);
        return false;
    }

    private boolean checkQEmuDrivers() {
        for (File drivers_file : new File[]{new File("/proc/tty/drivers"), new File("/proc/cpuinfo")}) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                String driver_data = new String(data);
                for (String known_qemu_driver : QEMU_DRIVERS) {
                    if (driver_data.contains(known_qemu_driver)) {
                        log(">>> Check QEmuDrivers is detected");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkFiles(String[] targets, String type) {
        for (String pipe : targets) {
            File qemu_file = new File(pipe);
            if (qemu_file.exists()) {
                log(">>> Check " + type + " is detected");
                return true;
            }
        }
        return false;
    }

    private boolean checkQEmuProps() {
        int found_props = 0;

        for (Property property : PROPERTIES) {
            String property_value = getProp(mContext, property.name);
            if ((property.seek_value == null) && (property_value != null)) {
                found_props++;
            }
            if ((property.seek_value != null)
                    && (property_value.contains(property.seek_value))) {
                found_props++;
            }

        }

        if (found_props >= MIN_PROPERTIES_THRESHOLD) {
            log(">>> Check QEmuProps is detected");
            return true;
        }
        return false;
    }

    private boolean checkIp() {
        boolean ipDetected = false;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            String[] args = {"/system/bin/netcfg"};
            StringBuilder stringBuilder = new StringBuilder();
            try {
                ProcessBuilder builder = new ProcessBuilder(args);
                builder.directory(new File("/system/bin/"));
                builder.redirectErrorStream(true);
                Process process = builder.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    stringBuilder.append(new String(re));
                }
                in.close();
            } catch (Exception ex) {
            }

            String netData = stringBuilder.toString();

            if (!TextUtils.isEmpty(netData)) {
                log(">>> netcfg data -> " + netData);
                String[] array = netData.split("\n");

                for (String lan : array) {
                    if ((lan.contains("wlan0") || lan.contains("tunl0") || lan.contains("eth0"))
                            && lan.contains(IP)) {
                        ipDetected = true;
                        log(">>> Check IP is detected");
                        break;
                    }
                }

            }
        }
        return ipDetected;
    }

//    private boolean isSupportTelePhony() {
//        PackageManager packageManager = mContext.getPackageManager();
//        boolean isSupport = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
//        log("Supported TelePhony: " + isSupport);
//        return isSupport;
//    }

    private String getProp(Context context, String property) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemProperties = classLoader.loadClass("android.os.SystemProperties");

            Method get = systemProperties.getMethod("get", String.class);

            Object[] params = new Object[1];
            params[0] = property;

            return (String) get.invoke(systemProperties, params);
        } catch (Exception e) {
        }
        return null;
    }

    public interface OnEmulatorDetectorListener {
        void onResult(boolean isEmulator);
    }
}

