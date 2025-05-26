package me.guanming.carcarhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    public static final long FAKE_TIME_OFFSET = 883_612_800_000L; // 28 years in milliseconds
    public static final long FAKE_TIME_DELTA = 3_153_600_000L; // 1 year in milliseconds

    private static long _lastFakeTime = java.lang.System.currentTimeMillis() - FAKE_TIME_OFFSET;
    private static long calcTime(long originalTime) {
        long fakeTime = originalTime - FAKE_TIME_OFFSET;
        _lastFakeTime = originalTime - _lastFakeTime > FAKE_TIME_DELTA
            ? fakeTime  // 与上次计算结果相差太大（1年），则认为这是未修改的时间，因此使用修改后的时间
            : originalTime; // 相差不大，认为这是已经修改的时间，不做二次修改，直接返回
        return _lastFakeTime;
    }

    private static void modifySystemMillis(XC_MethodHook.MethodHookParam param) {
        long originalTime = (long)param.getResult();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) param.setResult(fakeTime);
        // 这个函数调用太频繁，可能会导致日志过多，因此注释掉
        // XposedBridge.log("(CarCarHook) System.currentTimeMillis: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyReturnedJavaCalendar(XC_MethodHook.MethodHookParam param) {
        java.util.Calendar calendar = (java.util.Calendar)param.getResult();
        long originalTime = calendar.getTimeInMillis();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) {
            calendar.setTimeInMillis(fakeTime);
            param.setResult(calendar);
        }
        XposedBridge.log("(CarCarHook) java.util.Calendar: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyReturnedAndroidCalendar(XC_MethodHook.MethodHookParam param) {
        android.icu.util.Calendar calendar = (android.icu.util.Calendar)param.getResult();
        long originalTime = calendar.getTimeInMillis();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) {
            calendar.setTimeInMillis(fakeTime);
            param.setResult(calendar);
        }
        XposedBridge.log("(CarCarHook) android.icu.util.Calendar: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyGregorianCalendarConstructor(XC_MethodHook.MethodHookParam param) {
        java.util.GregorianCalendar calendar = (java.util.GregorianCalendar)param.thisObject;
        long originalTime = calendar.getTimeInMillis();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) calendar.setTimeInMillis(fakeTime);
        XposedBridge.log("(CarCarHook) java.util.GregorianCalendar: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyTimeSetToNow(XC_MethodHook.MethodHookParam param) {
		android.text.format.Time time = (android.text.format.Time)param.thisObject;
        long originalTime = time.toMillis(true);
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) time.set(fakeTime);
        XposedBridge.log("(CarCarHook) android.text.format.Time.setToNow: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyDateConstructor(XC_MethodHook.MethodHookParam param) {
        java.util.Date date = (java.util.Date)param.thisObject;
        long originalTime = date.getTime();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) date.setTime(fakeTime);
        XposedBridge.log("(CarCarHook) java.util.Date: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyLocalDateTime(XC_MethodHook.MethodHookParam param, java.time.ZoneId zone) {
        java.time.LocalDateTime localDateTime = (java.time.LocalDateTime)param.getResult();
        long originalTime = localDateTime.atZone(zone).toInstant().toEpochMilli();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime)
            param.setResult(java.time.Instant.ofEpochMilli(fakeTime).atZone(zone).toLocalDateTime());
        XposedBridge.log("(CarCarHook) java.time.LocalDateTime.now: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyLocalDate(XC_MethodHook.MethodHookParam param, java.time.ZoneId zone) {
        java.time.LocalDate localDate = (java.time.LocalDate)param.getResult();
        long originalTime = localDate.atStartOfDay(zone).toInstant().toEpochMilli();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime)
            param.setResult(java.time.Instant.ofEpochMilli(fakeTime).atZone(zone).toLocalDate());
        XposedBridge.log("(CarCarHook) java.time.LocalDate.now: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyZonedDateTime(XC_MethodHook.MethodHookParam param, java.time.ZoneId zone) {
        java.time.ZonedDateTime zonedDateTime = (java.time.ZonedDateTime)param.getResult();
        long originalTime = zonedDateTime.toInstant().toEpochMilli();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime)
            param.setResult(java.time.Instant.ofEpochMilli(fakeTime).atZone(zone));
        XposedBridge.log("(CarCarHook) java.time.ZonedDateTime.now: " + originalTime + " -> " + fakeTime);
    }
    private static void modifyInstant(XC_MethodHook.MethodHookParam param) {
        java.time.Instant instant = (java.time.Instant)param.getResult();
        long originalTime = instant.toEpochMilli();
        long fakeTime = calcTime(originalTime);
        if (originalTime != fakeTime) {
            instant = java.time.Instant.ofEpochMilli(fakeTime);
            param.setResult(instant);
        }
        XposedBridge.log("(CarCarHook) java.time.Instant.now: " + originalTime + " -> " + fakeTime);
    }


    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("(CarCarHook) Loaded app: " + lpparam.packageName);

        try {
            XposedHelpers.findAndHookMethod(
                "kotlinx.datetime.Clock", lpparam.classLoader, "now",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("(CarCarHook) after Hooked kotlinx.datetime.Clock.now()");
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook kotlinx.datetime 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.lang.System", lpparam.classLoader, "currentTimeMillis",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifySystemMillis(param);
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook System.currentTimeMillis 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.Calendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            XposedBridge.log("Hook android.icu.util.Calendar.getInstance() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookConstructor(
                "java.util.GregorianCalendar", lpparam.classLoader,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyGregorianCalendarConstructor(param);
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook new java.util.GregorianCalendar() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.util.GregorianCalendar", lpparam.classLoader, "getInstance",
                java.util.TimeZone.class, java.util.Locale.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyReturnedJavaCalendar(param);
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook java.util.GregorianCalendar.getInstance() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "android.text.format.Time", lpparam.classLoader, "setToNow",
                new XC_MethodHook() {
    				@Override
        			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyTimeSetToNow(param);
        			}
        		}
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook Time.setToNow() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookConstructor(
                "java.util.Date", lpparam.classLoader,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyDateConstructor(param);
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook new java.util.Date() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.time.LocalDateTime", lpparam.classLoader, "now",
                new XC_MethodHook() {
    				@Override
        			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyLocalDateTime(param, java.time.ZoneId.systemDefault());
        			}
        		}
            );
            XposedHelpers.findAndHookMethod(
                "java.time.LocalDateTime", lpparam.classLoader, "now",
                java.time.ZoneId.class, new XC_MethodHook() {
    				@Override
        			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyLocalDateTime(param, (java.time.ZoneId)param.args[0]);
        			}
        		}
            );
            XposedHelpers.findAndHookMethod(
                "java.time.LocalDateTime", lpparam.classLoader, "now",
                java.time.Clock.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyLocalDateTime(param, ((java.time.Clock)param.args[0]).getZone());
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook LocalDateTime.now() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.time.LocalDate", lpparam.classLoader, "now",
                new XC_MethodHook() {
    				@Override
        			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyLocalDate(param, java.time.ZoneId.systemDefault());
        			}
        		}
            );
            XposedHelpers.findAndHookMethod(
                "java.time.LocalDate", lpparam.classLoader, "now",
                java.time.ZoneId.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyLocalDate(param, (java.time.ZoneId)param.args[0]);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.time.LocalDate", lpparam.classLoader, "now",
                java.time.Clock.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyLocalDate(param, ((java.time.Clock)param.args[0]).getZone());
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook LocalDate.now() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.time.ZonedDateTime", lpparam.classLoader, "now",
                new XC_MethodHook() {
    				@Override
        			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyZonedDateTime(param, java.time.ZoneId.systemDefault());
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.time.ZonedDateTime", lpparam.classLoader, "now",
                java.time.ZoneId.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyZonedDateTime(param, (java.time.ZoneId)param.args[0]);
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "java.time.ZonedDateTime", lpparam.classLoader, "now",
                java.time.Clock.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyZonedDateTime(param, ((java.time.Clock)param.args[0]).getZone());
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook ZonedDateTime.now() 失败: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod(
                "java.time.Instant", lpparam.classLoader, "now",
                new XC_MethodHook() {
    				@Override
        			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyInstant(param);
        			}
        		}
            );
            XposedHelpers.findAndHookMethod(
                "java.time.Instant", lpparam.classLoader, "now",
                java.time.Clock.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        modifyInstant(param);
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("Hook Instant.now() 失败: " + e.getMessage());
        }

        XposedBridge.log("(CarCarHook) Hook done.");
    }
}
