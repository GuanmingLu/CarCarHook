package me.guanming.carcarhook;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.util.Date;
import java.util.Locale;

public class MainHook implements IXposedHookLoadPackage {
    private Date combine(Date date, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        return cal.getTime();
    }
    
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("(CarCarHook) Loaded app: " + lpparam.packageName);

        try {
            XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass("java.lang.System", lpparam.classLoader), "currentTimeMillis",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Date newTimestamp = combine(
                            new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2022-01-01"),
                            Calendar.getInstance().getTime()
                        );

                        XposedBridge.log("(CarCarHook) System.currentTimeMillis intercepted: " + newTimestamp);
                        param.setResult(newTimestamp.getTime());
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook System.currentTimeMillis 失败: " + e.getMessage());
        }

        XposedBridge.log("(CarCarHook) Hook done.");
    }
}
