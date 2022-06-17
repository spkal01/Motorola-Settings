package com.android.settings.bluetooth;

import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1987R$layout;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class BluetoothDetailsCompanionAppsController extends BluetoothDetailsController {
    private CachedBluetoothDevice mCachedDevice;
    CompanionDeviceManager mCompanionDeviceManager;
    PackageManager mPackageManager;
    PreferenceCategory mProfilesContainer;

    public String getPreferenceKey() {
        return "device_companion_apps";
    }

    /* access modifiers changed from: protected */
    public void refresh() {
    }

    public BluetoothDetailsCompanionAppsController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mCachedDevice = cachedBluetoothDevice;
        this.mCompanionDeviceManager = (CompanionDeviceManager) context.getSystemService(CompanionDeviceManager.class);
        this.mPackageManager = context.getPackageManager();
        lifecycle.addObserver(this);
    }

    /* access modifiers changed from: protected */
    public void init(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mProfilesContainer = preferenceCategory;
        preferenceCategory.setLayoutResource(C1987R$layout.preference_companion_app);
    }
}
