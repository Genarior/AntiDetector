package com.z.zz.zzz.emu;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import static com.z.zz.zzz.emu.EmulatorDetector.log;

final class EmulatorFinder {

    // 模拟器检测一个20项目, 依次为:
    // 1. 拨号盘
    // 2. 蓝牙
    // 3. GPS
    // 4. 多点触控
    // 5. 电池温度
    // 6. 电池电压
    // 7. 原始模拟器特征
    // 8. 海马模拟器特征
    // 9. 文卓爷模拟器特征
    // 10. 逍遥模拟器特征
    // 11. BlueStack模拟器特征
    // 12. 夜神模拟器特征
    // 13. 天天模拟器特征
    // 14. VBOX虚拟机特征
    // 15. Genymotion特征
    // 16. Qemu特征
    // 17. CPU信息
    // 18. 设备信息
    // 19. 出厂信息
    // 20. 网络运营商信息
    static int findEmulatorFeatureFlag(Context context) {
        int flag = 0x0;

        if (!checkResolveDailAction(context)) {
            flag |= (0x1);
        }
        if (!checkBluetoothHardware()) {
            flag |= (0x1 << 1);
        }
        if (!checkGPSHardware(context)) {
            flag |= (0x1 << 2);
        }
        if (!checkMultiTouch(context)) {
            flag |= (0x1 << 3);
        }
        if (!checkBatteryTemperature(context)) {
            flag |= (0x1 << 4);
        }
        if (!checkBatteryVoltage(context)) {
            flag |= (0x1 << 5);
        }
        if (!checkOriginSimulatorFeature()) {
            flag |= (0x1 << 6);
        }
        if (!checkHaimaSimulatorFeature()) {
            flag |= (0x1 << 7);
        }
        if (!checkWenzhuoSimulatorFeature()) {
            flag |= (0x1 << 8);
        }
        if (!checkXiaoyaoSimulatorFeature()) {
            flag |= (0x1 << 9);
        }
        if (!checkBlueStackSimulatorFeature()) {
            flag |= (0x1 << 10);
        }
        if (!checkYeshenSimulatorFeature()) {
            flag |= (0x1 << 11);
        }
        if (!checkTiantianSimulatorFeature()) {
            flag |= (0x1 << 12);
        }
        if (!checkVboxFeature()) {
            flag |= (0x1 << 13);
        }
        if (!checkGenymotionFeature()) {
            flag |= (0x1 << 14);
        }
        if (!checkQemuFeature()) {
            flag |= (0x1 << 15);
        }
        if (!checkCpuInfo()) {
            flag |= (0x1 << 16);
        }
        if (!checkDeviceInfo()) {
            flag |= (0x1 << 17);
        }
        if (!checkBuildProperty()) {
            flag |= (0x1 << 18);
        }
        if (!checkNetworkOperatorName(context)) {
            flag |= (0x1 << 19);
        }
        if (!checkNetworkOperatorName(context)) {
            flag |= (0x1 << 19);
        }

        log("findEmulatorFeatureFlag: " + flag);

        return flag;
    }

    // 1 检查项: 是否能跳转拨号盘
    private static boolean checkResolveDailAction(Context context) {
        try {
            String url = "tel:" + "12345678910";
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_DIAL);
            if (intent.resolveActivity(context.getPackageManager()) == null) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 2 检查项: 是否有蓝牙硬件
    private static boolean checkBluetoothHardware() {
        // 兼容64位ARM处理器
        try {
            if (!Utils.fileExist("/system/lib/libbluetooth_jni.so")
                    && !Utils.fileExist("/system/lib64/libbluetooth_jni.so")
                    && !Utils.fileExist("/system/lib/arm64/libbluetooth_jni.so")
                    && !Utils.fileExist("/system/vendor/lib64/libbluetooth_jni.so")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 3 检查项: 是否有GPS硬件
    private static boolean checkGPSHardware(Context context) {
        try {
            final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (mgr == null)
                return false;
            final List<String> providers = mgr.getAllProviders();
            if (providers == null)
                return false;
            boolean containGPS = providers.contains(LocationManager.GPS_PROVIDER);
            if (!containGPS) {
                return false;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return true;
    }

    // 4 检查项: 是否支持多点触控
    private static boolean checkMultiTouch(Context context) {
        try {
            boolean hasFeature = context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch");
            if (!hasFeature) {
                return false;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return true;
    }

    // 5 检查项: 电池温度
    private static boolean checkBatteryTemperature(Context context) {
        try {
            Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (batteryStatus == null) {
                return false;
            }
            int temp = batteryStatus.getIntExtra("temperature", -999);
            if (temp == -999) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 6 检查项: 电池电压
    private static boolean checkBatteryVoltage(Context context) {
        try {
            Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (batteryStatus == null) {
                return false;
            }
            int volt = batteryStatus.getIntExtra("voltage", -999);
            if (volt == -999) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 7 检查项: 源生模拟器特征文件
    private static boolean checkOriginSimulatorFeature() {
        try {
            if (Utils.fileExist("/system/bin/qemu_props")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 8 检查项: 海马模拟器特征文件
    private static boolean checkHaimaSimulatorFeature() {
        try {
            if (Utils.fileExist("/system/lib/libdroid4x.so")) {
                return false;
            }
            if (Utils.fileExist("/system/bin/droid4x-prop")) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("init.svc.droid4x"))) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 9 检查项: 文卓爷模拟器特征文件
    private static boolean checkWenzhuoSimulatorFeature() {
        try {
            if (Utils.fileExist("/system/bin/windroyed")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 10 逍遥模拟器特征文件
    private static boolean checkXiaoyaoSimulatorFeature() {
        try {
            if (Utils.fileExist("/system/bin/microvirt-prop")) {
                return false;
            }
            if (Utils.fileExist("/system/bin/microvirtd")) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("init.svc.microvirtd"))) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 11 BlueStack模拟器特征文件
    private static boolean checkBlueStackSimulatorFeature() {
        try {
            if (Utils.fileExist("/data/.bluestacks.prop")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 12 夜神模拟器特征文件
    private static boolean checkYeshenSimulatorFeature() {
        try {
            if (Utils.fileExist("/system/bin/nox-prop")) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("init.svc.noxd"))) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 13 天天模拟器特征文件
    private static boolean checkTiantianSimulatorFeature() {
        try {
            if (Utils.fileExist("/system/bin/ttVM-prop")) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("init.svc.ttVM_x86-setup"))) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 14 Vbox特征
    private static boolean checkVboxFeature() {
        try {
            if (!Utils.stringEmpty(Utils.systemGetProperty("init.svc.vbox86-setup"))) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("androVM.vbox_dpi"))) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("androVM.vbox_graph_mode"))) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 15 Genymotion特征
    private static boolean checkGenymotionFeature() {
        try {
            if (Utils.stringContains(Utils.systemGetProperty("ro.product.manufacturer"), "Genymotion")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 16 Qemu特征
    private static boolean checkQemuFeature() {
        try {
            if (!Utils.stringEmpty(Utils.systemGetProperty("init.svc.qemud"))) {
                return false;
            }
            if (!Utils.stringEmpty(Utils.systemGetProperty("ro.kernel.android.qemud"))) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 17 CPU信息
    private static boolean checkCpuInfo() {
        try {
            String cpu = getCPUInfoString();
            if (Utils.stringContains(cpu, "Genuine Intel(R)")) {
                return false;
            }
            if (Utils.stringContains(cpu, "Intel(R) Core(TM)")) {
                return false;
            }
            if (Utils.stringContains(cpu, "Intel(R) Pentium(R)")) {
                return false;
            }
            if (Utils.stringContains(cpu, "Intel(R) Xeon(R)")) {
                return false;
            }
            if (Utils.stringContains(cpu, "AMD")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 18 设备版本
    private static boolean checkDeviceInfo() {
        try {
            String device = getDeviceInfo();
            if (Utils.stringContains(device, "qemu")) {
                return false;
            }
            if (Utils.stringContains(device, "tencent")) {
                return false;
            }
            if (Utils.stringContains(device, "ttvm")) {
                return false;
            }
            if (Utils.stringContains(device, "Tiantian")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 19 Build属性
    private static boolean checkBuildProperty() {
        try {
            // fingerprint
            String FINGERPRINT = Build.FINGERPRINT;
            if (FINGERPRINT.startsWith("generic")) {
                return false;
            }
            if (FINGERPRINT.toLowerCase().contains("vbox")) {
                return false;
            }
            if (FINGERPRINT.toLowerCase().contains("test-keys")) {
                return false;
            }
            // model
            String model = Build.MODEL;
            if (model.contains("google_sdk")) {
                return false;
            }
            if (model.contains("Emulator")) {
                return false;
            }
            if (model.contains("Android SDK built for x86")) {
                return false;
            }
            // SERIAL
            String serial = Build.SERIAL;
            if (serial.equalsIgnoreCase("unknown")) {
                return false;
            }
            if (serial.equalsIgnoreCase("android")) {
                return false;
            }
            // MANUFACTURER
            String manufacturer = Build.MANUFACTURER;
            if (manufacturer.contains("Genymotion")) {
                return false;
            }
            // BRAND
            String brand = Build.BRAND;
            if (brand.startsWith("generic")
                    && brand.startsWith("generic")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    // 20 检查网络运营商名称
    private static boolean checkNetworkOperatorName(Context context) {
        try {
            String networkOP = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
            if (Utils.stringEquals(networkOP.toLowerCase(), "android")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    // ========================Helper==============================

    private static boolean isMultiTouchSupported(Context context) {
        boolean z = false;
        try {
            z = context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;

    }

    private static String getBatteryTemperature(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryStatus == null) {
            return null;
        }
        int temp = batteryStatus.getIntExtra("temperature", -1);
        if (temp > 0) {
            return Utils.tempToStr(((float) temp) / 10.0f, 1);
        }
        return null;
    }

    private static String getBatteryVoltage(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryStatus == null) {
            return null;
        }
        int volt = batteryStatus.getIntExtra("voltage", -1);
        if (volt > 0) {
            return String.valueOf(volt);
        }
        return null;
    }

    private static String getCPUInfoString() {
        String name = "unknown";
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String line = null;

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }

                String[] info = line.split(":\\s+", 2);
                if (info.length >= 2) {
                    String k = info[0].trim();
                    String v = info[1].trim();
                    if ("Hardware".equals(k)) {
                        name = v;
                    } else if ("model name".equals(k)) {
                        name = v;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private static String getDeviceInfo() {
        String result = "";
        ProcessBuilder cmd;

        try {
            String[] args = {"/system/bin/cat", "/proc/version"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[256];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            result = "exception";
        }

        return result.trim();
    }

    private static class Utils {

        static boolean fileExist(String filePath) {
            boolean flag = true;
            File file = new File(filePath);
            if (!file.exists()) {
                flag = false;
            }
            return flag;
        }

        static String systemGetProperty(String key) {
            String value = "";
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class);
                value = (String) (get.invoke(c, key, ""));
            } catch (Exception e) {
                e.printStackTrace();
                value = "";
            } finally {
                return value;
            }
        }

        static String tempToStr(float temp, int tempSetting) {
            if (temp <= 0.0f) {
                return "";
            }
            if (tempSetting == 2) {
                return String.format("%.1f°F", new Object[]{Float.valueOf(((9.0f * temp) + 160.0f) / 5.0f)});
            }
            return String.format("%.1f°C", new Object[]{Float.valueOf(temp)});
        }

        static boolean stringEquals(CharSequence a, CharSequence b) {
            if (a == b) return true;
            int length;
            if (a != null && b != null && (length = a.length()) == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) return false;
                    }
                    return true;
                }
            }
            return false;
        }

        static boolean stringContains(String main, String sub) {
            if (main == null || sub == null) {
                return false;
            }
            return main.indexOf(sub) != -1;
        }

        static boolean stringEmpty(CharSequence s) {
            return s == null || s.length() == 0;
        }
    }

}
