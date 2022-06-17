package com.motorola.extensions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.motorola.extensions.internal.DynamicPreferenceAttrHandler;
import com.motorola.extensions.internal.DynamicPreferenceInflater;
import com.motorola.extensions.internal.SystemUtils;
import java.util.ArrayList;
import java.util.List;

public class DynamicPreferences {
    private static final String TAG = "DynamicPreferences";

    public static void addOrOverridePreferences(PreferenceScreen preferenceScreen, ArrayList<Preference> arrayList, ArrayList<Preference> arrayList2, Intent intent) {
        List<ResolveInfo> queryIntentActivities;
        Bundle bundle;
        if (intent != null) {
            Context context = preferenceScreen.getContext();
            PackageManager packageManager = context.getPackageManager();
            if (SystemUtils.isSystemOrMotoApp(packageManager, context.getPackageName()) && (queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 128)) != null) {
                for (ResolveInfo next : queryIntentActivities) {
                    String str = next.activityInfo.packageName;
                    if (SystemUtils.isSystemOrMotoApp(packageManager, str) && (bundle = next.activityInfo.metaData) != null && bundle.containsKey("com.motorola.extensions.preference")) {
                        try {
                            new DynamicPreferenceInflater(context.createPackageContext(str, 0), next.activityInfo).inflate("com.motorola.extensions.preference", new DynamicPreferenceAttrHandler.Holder(preferenceScreen, arrayList, arrayList2));
                        } catch (PackageManager.NameNotFoundException e) {
                            String str2 = TAG;
                            Log.w(str2, "Could not find the application " + next.activityInfo.packageName, e);
                        } catch (Throwable th) {
                            String str3 = TAG;
                            Log.d(str3, "Error inflating dynamic preference from " + next.activityInfo.packageName, th);
                        }
                    }
                }
            }
        }
    }
}
