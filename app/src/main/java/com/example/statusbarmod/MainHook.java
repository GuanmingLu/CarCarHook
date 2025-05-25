package me.guanming.carcarhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("(CarCarHook) Loaded app: " + lpparam.packageName);

        try {
            XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass("java.lang.System", lpparam.classLoader), "currentTimeMillis",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        long originalTime = (long)param.getResult();
                        long modifiedTime = originalTime - 883_612_800_000L;
                        param.setResult(modifiedTime);
                    }
                }
            );
        } catch (ClassNotFoundException e) {
            XposedBridge.log("Hook System.currentTimeMillis 失败: " + e.getMessage());
        }

        XposedBridge.log("(CarCarHook) Hook done.");
    }
}
