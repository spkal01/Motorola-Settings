package com.motorola.settings.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.settings.C1992R$string;
import java.util.List;

public class DemoModeUtils {
    private static final Intent INTENT_LAUNCH_DEMO_MODE_MC_CONF_ACTIVITY = new Intent("com.motorola.settings.action.DEMO_MODE_MC_CONF_ACTIVITY");
    private static final String TAG = "DemoModeUtils";

    public static boolean isDemoModeEnabled(Context context) {
        return getDemoModeComponent(context) != null;
    }

    private static ComponentName getDemoModeComponent(Context context) {
        List<ResolveInfo> queryIntentActivitiesAsUser = context.getPackageManager().queryIntentActivitiesAsUser(INTENT_LAUNCH_DEMO_MODE_MC_CONF_ACTIVITY, 1, ActivityManager.getCurrentUser());
        if (queryIntentActivitiesAsUser == null) {
            return null;
        }
        for (ResolveInfo resolveInfo : queryIntentActivitiesAsUser) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if ("android.permission.MASTER_CLEAR".equals(activityInfo.permission)) {
                return new ComponentName(activityInfo.packageName, activityInfo.name);
            }
            String str = TAG;
            Log.w(str, "Skipping activity " + activityInfo.packageName + "/" + activityInfo.name + ": it does not have the required permission :" + "android.permission.MASTER_CLEAR");
        }
        return null;
    }

    public static void launchMasterClearConfirmation(Fragment fragment, int i) {
        ComponentName demoModeComponent = getDemoModeComponent(fragment.getActivity());
        if (demoModeComponent != null) {
            try {
                fragment.startActivityForResult(new Intent("com.motorola.settings.action.DEMO_MODE_MC_CONF_ACTIVITY").setComponent(demoModeComponent), i);
                return;
            } catch (Exception unused) {
            }
        }
        Toast.makeText(fragment.getActivity(), C1992R$string.demo_mode_error_starting_activity, 0).show();
    }
}
