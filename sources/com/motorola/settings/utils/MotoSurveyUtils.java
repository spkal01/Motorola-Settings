package com.motorola.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class MotoSurveyUtils {
    public static void sendSurveyIntent(Context context, String str) {
        if (surveyPackageExists(context)) {
            Intent intent = new Intent();
            intent.setPackage("com.motorola.survey");
            intent.setAction("com.motorola.internal.intent.action.FINGERPRINT_ENROLLMENT");
            intent.putExtra("type", str);
            context.sendBroadcast(intent);
        }
    }

    private static boolean surveyPackageExists(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.motorola.survey", 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("MotoSurveyUtils", "com.motorola.survey does not exist");
            return false;
        }
    }
}
