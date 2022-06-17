package com.android.settings.connecteddevice;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class AddDevicePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private IntentFilter mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
    /* access modifiers changed from: private */
    public Preference mPreference;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AddDevicePreferenceController addDevicePreferenceController = AddDevicePreferenceController.this;
            addDevicePreferenceController.updateState(addDevicePreferenceController.mPreference);
        }
    };

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

    public AddDevicePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
            this.mPreference = findPreference;
            updateState(findPreference);
        }
    }

    public int getAvailabilityStatus() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth") ? 0 : 3;
    }

    public CharSequence getSummary() {
        if (isBluetoothEnabled()) {
            return "";
        }
        return this.mContext.getString(C1992R$string.connected_device_add_device_summary);
    }

    /* access modifiers changed from: protected */
    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }
}
