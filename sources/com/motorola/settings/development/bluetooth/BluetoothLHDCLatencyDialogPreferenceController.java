package com.motorola.settings.development.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController;
import com.android.settings.development.bluetooth.BaseBluetoothDialogPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class BluetoothLHDCLatencyDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_codec_lhdc_latency";
    }

    public BluetoothLHDCLatencyDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    public boolean isAvailable() {
        return SystemProperties.get("ro.hardware.soc.manufacturer", "").equals("mtk");
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((BaseBluetoothDialogPreference) this.mPreference).setCallback(this);
    }

    /* access modifiers changed from: protected */
    public void writeConfigurationValues(int i) {
        Log.e("BtLHDC_LatencyCtl", "writeConfigurationValues  = " + i);
        if (i > 1) {
            i = getDefaultIndex();
        }
        this.mBluetoothA2dpConfigStore.setCodecSpecific2Value(i | 49152);
    }

    /* access modifiers changed from: protected */
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtLHDC_LatencyCtl", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex((int) bluetoothCodecConfig.getCodecSpecific2());
    }

    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 2; i++) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    public void onHDAudioEnabled(boolean z) {
        this.mPreference.setEnabled(false);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig == null || !(currentCodecConfig.getCodecType() == 7 || currentCodecConfig.getCodecType() == 8)) {
            preference.setEnabled(false);
            preference.setSummary((CharSequence) "");
            return;
        }
        preference.setEnabled(true);
    }

    /* access modifiers changed from: package-private */
    public int convertCfgToBtnIndex(int i) {
        Log.e("BtLHDC_LatencyCtl", "convertCfgToBtnIndex  = " + i);
        if ((i & 49152) == 49152) {
            return i & 1;
        }
        return getDefaultIndex();
    }
}
