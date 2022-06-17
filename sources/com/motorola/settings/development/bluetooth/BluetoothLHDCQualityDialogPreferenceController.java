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

public class BluetoothLHDCQualityDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_lhdc_playback_quality";
    }

    public BluetoothLHDCQualityDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
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
        Log.e("BtLHDC_QualityCtl", "writeConfigurationValues  = " + i);
        if (i > 8) {
            i = getDefaultIndex();
        }
        this.mBluetoothA2dpConfigStore.setCodecSpecific1Value((long) (i | 32768));
    }

    /* access modifiers changed from: protected */
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtLHDC_QualityCtl", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex((int) bluetoothCodecConfig.getCodecSpecific1());
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

    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 9; i++) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    public void onHDAudioEnabled(boolean z) {
        this.mPreference.setEnabled(false);
    }

    /* access modifiers changed from: package-private */
    public int convertCfgToBtnIndex(int i) {
        Log.e("BtLHDC_QualityCtl", "convertCfgToBtnIndex  = " + i);
        if ((49152 & i) == 32768) {
            return i & 255;
        }
        return getDefaultIndex();
    }
}
