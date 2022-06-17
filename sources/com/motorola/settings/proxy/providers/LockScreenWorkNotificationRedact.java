package com.motorola.settings.proxy.providers;

import android.app.NotificationManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import com.android.settings.Utils;
import java.util.List;

public class LockScreenWorkNotificationRedact extends ContentProvider {
    public boolean onCreate() {
        return true;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        int managedProfileId;
        Bundle bundle2 = new Bundle();
        Context context = getContext();
        String callingPackage = getCallingPackage();
        if (isPrivilegedApp(context.getPackageManager(), callingPackage) && isEnabledNotificationListenerPackage(context, callingPackage)) {
            str.hashCode();
            if (str.equals("getAllowPrivateNotifications")) {
                int i = bundle != null ? bundle.getInt("android.intent.extra.USER_ID", -1) : -1;
                if (!(i == -1 || (managedProfileId = Utils.getManagedProfileId((UserManager) context.getSystemService(UserManager.class), UserHandle.myUserId())) == -10000 || i != managedProfileId)) {
                    ContentResolver contentResolver = context.getContentResolver();
                    boolean z = true;
                    if (Settings.Secure.getIntForUser(contentResolver, "lock_screen_allow_private_notifications", 1, i) == 0) {
                        z = false;
                    }
                    bundle2.putBoolean("result", z);
                }
            }
        }
        return bundle2;
    }

    private static boolean isEnabledNotificationListenerPackage(Context context, String str) {
        List enabledNotificationListenerPackages = ((NotificationManager) context.getSystemService(NotificationManager.class)).getEnabledNotificationListenerPackages();
        return enabledNotificationListenerPackages != null && enabledNotificationListenerPackages.contains(str);
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
