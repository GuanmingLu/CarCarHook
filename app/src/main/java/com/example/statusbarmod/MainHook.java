package me.guanming.carcarhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    private long getTimestamp(long originalTime) {
        return originalTime - 883_612_800_000L; // 28 years in milliseconds
    }
    private long getTimestamp() {
        return getTimestamp(System.currentTimeMillis());
    }

    void modifyReturnedJavaCalendar(MethodHookParam param) {
        java.util.Calendar calendar = (java.util.Calendar)param.getResult();
        calendar.add(java.util.Calendar.YEAR, -28);
        param.setResult(calendar);
    }
    void modifyReturnedAndroidCalendar(MethodHookParam param) {
        android.icu.util.Calendar calendar = (android.icu.util.Calendar)param.getResult();
        calendar.add(android.icu.util.Calendar.YEAR, -28);
        param.setResult(calendar);
    }

    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("(CarCarHook) Loaded app: " + lpparam.packageName);

        try {
            XposedHelpers.findAndHookMethod(
                "java.lang.System", lpparam.classLoader, "currentTimeMillis",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(getTimestamp((long)param.getResult()));
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook System.currentTimeMillis 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook java.util.Calendar.getInstance() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "android.icu.util.Calendar", lpparam.classLoader, "getInstance",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedAndroidCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "android.icu.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedAndroidCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "android.icu.util.Calendar", lpparam.classLoader, "getInstance",
                android.icu.util.TimeZone.class, java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedAndroidCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "android.icu.util.Calendar", lpparam.classLoader, "getInstance",
                android.icu.util.TimeZone.class, android.icu.util.ULocale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedAndroidCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "android.icu.util.Calendar", lpparam.classLoader, "getInstance",
                android.icu.util.TimeZone.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedAndroidCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "android.icu.util.Calendar", lpparam.classLoader, "getInstance",
                android.icu.util.ULocale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedAndroidCalendar(param);
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook android.icu.util.Calendar.getInstance() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookConstructor(
                "java.util.GregorianCalendar", lpparam.classLoader,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        java.util.GregorianCalendar calendar = (java.util.GregorianCalendar)param.thisObject;
                        calendar.add(java.util.Calendar.YEAR, -28);
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook new java.util.GregorianCalendar() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ModifyReturnedJavaCalendar(param);
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook java.util.GregorianCalendar.getInstance() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "android.text.format.Time", lpparam.classLoader, "setToNow",
                new XC_MethodHook() {
    				@Override
        			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    	    			((android.text.format.Time)param.thisObject).set(getTimestamp());
    	    			param.setResult(null);
        			}
        		}
            );
        } catch (Exception e) {
            XposedBridge.log("Hook Time.setToNow() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookConstructor(
                "java.util.Date", lpparam.classLoader,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        java.util.Date date = (java.util.Date)param.thisObject;
                        date.setTime(getTimestamp());
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Hook new java.util.Date() 失败: " + e.getMessage());
        }

        XposedBridge.log("(CarCarHook) Hook done.");
    }
}
