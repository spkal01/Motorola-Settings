package com.android.settings.aware;

import android.content.Context;
import androidx.fragment.app.Fragment;

public class AwareFeatureProviderImpl implements AwareFeatureProvider {
    public boolean isEnabled(Context context) {
        return false;
    }

    public boolean isSupported(Context context) {
        return false;
    }

    public void showRestrictionDialog(Fragment fragment) {
    }
}
