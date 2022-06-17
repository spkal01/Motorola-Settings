package com.motorola.settings.tether;

import android.content.Context;
import android.content.Intent;
import com.android.settings.slices.CustomSliceRegistry;

public class WifiHotspotSliceConflictHelper {
    public static boolean needsWarning(Context context) {
        return WifiHotspotConflictWarningDialog.needsWarning(context);
    }

    public static Intent getActivityIntent(Context context) {
        return new Intent(context, WifiHotspotConflictWarningDialogActivity.class).putExtra("key_provider_model_slice_uri", CustomSliceRegistry.PROVIDER_MODEL_SLICE_URI).addFlags(268435456).addFlags(65536);
    }
}
