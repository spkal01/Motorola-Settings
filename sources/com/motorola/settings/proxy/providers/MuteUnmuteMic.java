package com.motorola.settings.proxy.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.settings.utils.SensorPrivacyManagerHelper;
import java.util.concurrent.Executor;

public class MuteUnmuteMic extends ContentProvider {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private Executor mCallbackExecutor;
    private SensorPrivacyManagerHelper mSensorPrivacyManagerHelper;

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        Context context = getContext();
        String callingPackage = getCallingPackage();
        if (!isPrivilegedApp(context.getPackageManager(), callingPackage)) {
            if (DEBUG) {
                Log.d("MuteUnmuteMic", callingPackage + " is not a privileged app");
            }
            return bundle2;
        }
        str.hashCode();
        if (str.equals("getMicSensorValue")) {
            boolean z = !this.mSensorPrivacyManagerHelper.isSensorBlocked(1);
            if (DEBUG) {
                Log.d("MuteUnmuteMic", "mic sensor values is " + z);
            }
            bundle2.putBoolean("result", z);
        } else if (str.equals("toggleMicSensor")) {
            SensorPrivacyManagerHelper sensorPrivacyManagerHelper = this.mSensorPrivacyManagerHelper;
            sensorPrivacyManagerHelper.setSensorBlocked(5, 1, !sensorPrivacyManagerHelper.isSensorBlocked(1));
            bundle2.putBoolean("result", true);
        }
        return bundle2;
    }

    private static boolean isPrivilegedApp(PackageManager packageManager, String str) {
        try {
            if ((packageManager.getApplicationInfo(str, 0).privateFlags & 8) != 0) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public boolean onCreate() {
        if (DEBUG) {
            Log.d("MuteUnmuteMic", "user id = " + UserHandle.myUserId());
        }
        this.mSensorPrivacyManagerHelper = SensorPrivacyManagerHelper.getInstance(getContext());
        Executor mainExecutor = getContext().getMainExecutor();
        this.mCallbackExecutor = mainExecutor;
        this.mSensorPrivacyManagerHelper.addSensorBlockedListener(1, new MuteUnmuteMic$$ExternalSyntheticLambda0(this), mainExecutor);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i, boolean z) {
        if (DEBUG) {
            Log.d("MuteUnmuteMic", "sensor value changed");
        }
        getContext().getContentResolver().notifyChange(Uri.parse("content://com.motorola.settings.proxy.providers.MuteUnmuteMic"), (ContentObserver) null);
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new UnsupportedOperationException();
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }
}
