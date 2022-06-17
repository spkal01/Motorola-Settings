package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import com.android.settings.C1978R$array;
import com.google.common.primitives.Ints;

public final class MagnificationCapabilities {
    public static String getSummary(Context context, int i) {
        String[] stringArray = context.getResources().getStringArray(C1978R$array.magnification_mode_summaries);
        int indexOf = Ints.indexOf(context.getResources().getIntArray(C1978R$array.magnification_mode_values), i);
        if (indexOf == -1) {
            indexOf = 0;
        }
        return stringArray[indexOf];
    }

    public static void setCapabilities(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        Settings.Secure.putIntForUser(contentResolver, "accessibility_magnification_capability", i, contentResolver.getUserId());
    }

    public static int getCapabilities(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return Settings.Secure.getIntForUser(contentResolver, "accessibility_magnification_capability", 1, contentResolver.getUserId());
    }
}
