package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.RestrictedAppPreference;
import java.util.List;
import java.util.Map;

public class LocationInjectedServicesPreferenceController extends LocationInjectedServiceBasePreferenceController {
    private static final String TAG = "LocationPrefCtrl";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationInjectedServicesPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public void injectLocationServices(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        for (Map.Entry next : getLocationServices().entrySet()) {
            for (Preference preference : (List) next.getValue()) {
                if (preference instanceof RestrictedAppPreference) {
                    ((RestrictedAppPreference) preference).checkRestrictionAndSetDisabled();
                }
            }
            if (((Integer) next.getKey()).intValue() == UserHandle.myUserId() && preferenceCategory != null) {
                LocationSettings.addPreferencesSorted((List) next.getValue(), preferenceCategory);
            }
        }
    }
}
