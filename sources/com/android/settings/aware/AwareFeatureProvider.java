package com.android.settings.aware;

import android.content.Context;
import androidx.fragment.app.Fragment;

public interface AwareFeatureProvider {
    boolean isEnabled(Context context);

    boolean isSupported(Context context);

    void showRestrictionDialog(Fragment fragment);
}
