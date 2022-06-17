package com.motorola.settings.display.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.UserManager;
import com.android.settings.C1978R$array;
import com.android.settings.C1986R$integer;
import com.motorola.settings.utils.DisplayUtils;

public class EdgeUtils {
    public static int getDisplayGripSuppressionDistance(Context context) {
        return context.getSharedPreferences("edge_pref", 0).getInt("grip_suspension_distance", context.getResources().getInteger(C1986R$integer.config_waterfall_display_grip_suppression_distance));
    }

    public static void setDisplayGripSuppressionDistance(Context context, int i) {
        context.getSharedPreferences("edge_pref", 0).edit().putInt("grip_suspension_distance", i).apply();
    }

    public static int[] getSensitivityScales(Context context) {
        TypedArray obtainTypedArray = context.getResources().obtainTypedArray(C1978R$array.config_edgeSensitivityScales);
        int length = obtainTypedArray.length();
        int[] iArr = new int[length];
        for (int i = 0; i < length; i++) {
            iArr[i] = obtainTypedArray.getInt(i, 1);
        }
        obtainTypedArray.recycle();
        return iArr;
    }

    public static boolean isEdgeSensitivityAvailable(Context context) {
        return DisplayUtils.isWaterfallModeAvailable(context) && UserManager.get(context).isAdminUser();
    }
}
