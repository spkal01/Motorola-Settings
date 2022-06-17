package com.motorola.checkin;

import android.content.ContentResolver;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class CheckinEventWrapper {
    private static String TAG = "CheckinEventWrapper";
    private static Constructor<?> sMethodConstructor;
    private static Method sMethodPublish;
    private static Method sMethodSetValue;
    private static boolean sSucessfullyInit;
    private Object mCheckinEvent;

    static {
        Class<String> cls = String.class;
        try {
            Class<?> cls2 = Class.forName("com.motorola.android.provider.CheckinEvent");
            Class<?> cls3 = Class.forName("com.motorola.data.event.api.Event");
            sMethodConstructor = cls2.getDeclaredConstructor(new Class[]{cls, cls, cls, Long.TYPE});
            sMethodSetValue = cls3.getDeclaredMethod("setValue", new Class[]{cls, cls});
            sMethodPublish = cls2.getDeclaredMethod("publish", new Class[]{Object.class});
            sSucessfullyInit = true;
        } catch (Throwable unused) {
            Log.w(TAG, "Reflection failed");
            sMethodSetValue = null;
            sMethodPublish = null;
            sMethodConstructor = null;
            sSucessfullyInit = false;
        }
    }

    public static boolean isInitialized() {
        return sSucessfullyInit;
    }

    public boolean setHeader(String str, String str2, String str3, long j) {
        if (sSucessfullyInit) {
            try {
                this.mCheckinEvent = sMethodConstructor.newInstance(new Object[]{str, str2, str3, Long.valueOf(j)});
                return true;
            } catch (Throwable unused) {
                Log.w(TAG, "Reflection failed");
            }
        }
        return false;
    }

    public void setValue(String str, String str2) {
        Object obj;
        if (sSucessfullyInit && (obj = this.mCheckinEvent) != null) {
            try {
                sMethodSetValue.invoke(obj, new Object[]{str, str2});
            } catch (Throwable unused) {
                Log.w(TAG, "Reflection failed");
            }
        }
    }

    public void publish(ContentResolver contentResolver) {
        Object obj;
        if (sSucessfullyInit && (obj = this.mCheckinEvent) != null) {
            try {
                sMethodPublish.invoke(obj, new Object[]{contentResolver});
            } catch (Throwable unused) {
                Log.w(TAG, "Reflection failed");
            }
        }
    }
}
