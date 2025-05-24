package me.guanming.carcarhook;

import android.graphics.Color;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.example.car_launcher"))    // 只对目标包名进行 Hook
            return;

        XposedBridge.log("(CarCarHook) Loaded app: " + lpparam.packageName);

        try {
            Class<?> systemClass = lpparam.classLoader.loadClass("java.lang.System");
            XposedHelpers.findAndHookMethod(systemClass, "currentTimeMillis", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    long originalTime = (long) param.getResult();
                    long modifiedTime = originalTime - 8_836_128_000_000L;
                    XposedBridge.log("(CarCarHook) A:" + String.valueOf(originalTime) + " -> " + String.valueOf(modifiedTime));
                    param.setResult(modifiedTime);
                }
            });
        } catch (ClassNotFoundException e) {
            XposedBridge.log("Hook System.currentTimeMillis 失败: " + e.getMessage());
        }

        try {
            Class<?> calendarClass = lpparam.classLoader.loadClass("java.util.Calendar");
            XposedHelpers.findAndHookMethod(calendarClass, "getInstance", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    java.util.Calendar calendar = (java.util.Calendar) param.getResult();
                    calendar.add(java.util.Calendar.YEAR, -28);
                    XposedBridge.log("(CarCarHook) B:" + String.valueOf(calendar.get(java.util.Calendar.YEAR)));
                    param.setResult(calendar);
                }
            });
        } catch (ClassNotFoundException e) {
            XposedBridge.log("Hook Calendar.getInstance() 失败: " + e.getMessage());
        }

        XposedBridge.log("(CarCarHook) Hook done.");
    }
}
