package com.motorola.settings.datausage;

import android.app.ActivityManager;
import android.content.Context;
import android.net.NetworkTemplate;
import android.text.format.Formatter;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.datausage.DataUsageUtils;
import com.motorola.android.provider.MotorolaSettings;
import java.util.Locale;

public class MotoDataUsageUtils {
    public static String formatDataUsageForVzw(Context context, long j, int i) {
        if (isShowingSizeInGB(context)) {
            return formatDataUsageForVzw(context, j, (i & 8) != 0 ? 1073741824 : 1000000000, context.getString(17040350));
        }
        return formatDataUsageForVzw(context, j, (i & 8) != 0 ? 1048576 : 1000000, context.getString(17040719));
    }

    private static String formatDataUsageForVzw(Context context, long j, long j2, String str) {
        float f = ((float) j) / ((float) j2);
        if (f == 0.0f || f >= 0.01f) {
            return context.getString(C1992R$string.data_usage_file_size, new Object[]{String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(f)}), str});
        }
        return context.getString(C1992R$string.data_usage_less_than_min, new Object[]{String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(0.01f)}), str});
    }

    public static Formatter.BytesResult formatBytes(Context context, long j, int i) {
        float f;
        String str;
        if (!isFeature4232VzwDataUnitsEnabled(context)) {
            return Formatter.formatBytes(context.getResources(), j, i);
        }
        if (isShowingSizeInGB(context)) {
            f = ((float) j) / ((float) ((i & 8) != 0 ? 1073741824 : 1000000000));
            str = context.getString(17040350);
        } else {
            f = ((float) j) / ((float) ((i & 8) != 0 ? 1048576 : 1000000));
            str = context.getString(17040719);
        }
        return new Formatter.BytesResult(String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(f)}), str, 0);
    }

    public static boolean isShowingSizeInGB(Context context) {
        if (MotorolaSettings.Secure.getIntForUser(context.getContentResolver(), "data_unit_display_gb", 1, ActivityManager.getCurrentUser()) == 1) {
            return true;
        }
        return false;
    }

    public static void setShowSizeInGB(Context context, boolean z) {
        MotorolaSettings.Secure.putInt(context.getContentResolver(), "data_unit_display_gb", z ? 1 : 0);
    }

    public static boolean isFeature4232VzwDataUnitsEnabled(Context context) {
        return context.getResources().getBoolean(C1980R$bool.ftr_4232_data_usage);
    }

    public static boolean isFeature6206VzwDataUsageDialogEnabled(Context context) {
        return context.getResources().getBoolean(C1980R$bool.ftr_6206_data_usage_dialog);
    }

    public static NetworkTemplate getRefTemplate(Context context, NetworkTemplate networkTemplate) {
        int defaultSubscriptionId;
        if (!useCellularPolicyForWifi(context, networkTemplate) || (defaultSubscriptionId = DataUsageUtils.getDefaultSubscriptionId(context)) == -1) {
            return null;
        }
        return DataUsageUtils.getDefaultTemplate(context, defaultSubscriptionId);
    }

    public static boolean useCellularPolicyForWifi(Context context, NetworkTemplate networkTemplate) {
        return context != null && isFeature4232VzwDataUnitsEnabled(context) && networkTemplate != null && (networkTemplate.getMatchRule() == 7 || networkTemplate.getMatchRule() == 4);
    }
}
