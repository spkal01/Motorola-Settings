package com.android.settings.deviceinfo;

import android.content.Context;
import com.android.settings.C1980R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class ManualPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public String getPreferenceKey() {
        return "manual";
    }

    public ManualPreferenceController(Context context) {
        super(context);
    }

    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_manual);
    }
}
