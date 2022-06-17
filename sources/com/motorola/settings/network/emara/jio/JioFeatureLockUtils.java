package com.motorola.settings.network.emara.jio;

import android.content.Context;
import com.motorola.android.provider.MotorolaSettings;

public class JioFeatureLockUtils {
    public static boolean isSubsidyLocked(Context context) {
        return MotorolaSettings.Global.getInt(context.getContentResolver(), "jio_subsidy_locked", 0) == 2;
    }
}
