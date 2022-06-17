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

public class BluetoothLHDCArDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    public String getPreferenceKey() {
        return "bluetooth_enable_a2dp_codec_lhdc_ar_effect";
    }

    public BluetoothLHDCArDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
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
        int i2 = i != 0 ? 1275068418 : 1275068416;
        Log.i("LHDC_UI_AR", "write codecSpecific3Value = " + i2);
        this.mBluetoothA2dpConfigStore.setCodecSpecific3Value(i2);
    }

    /* access modifiers changed from: protected */
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtLHDC_ArCtl", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex((int) bluetoothCodecConfig.getCodecSpecific3());
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
        if (currentCodecConfig == null || currentCodecConfig.getCodecType() != 7) {
            preference.setEnabled(false);
            preference.setSummary((CharSequence) "");
            return;
        }
        preference.setEnabled(true);
    }

    /* access modifiers changed from: package-private */
    public int convertCfgToBtnIndex(int i) {
        Log.e("BtLHDC_ArCtl", "convertCfgToBtnIndex  = " + i);
        if ((-16777216 & i) != 1275068416) {
            Log.e("BtLHDC_ArCtl", "tag not matched, return AR OFF");
            return 0;
        } else if ((i & 2) != 0) {
            Log.e("BtLHDC_ArCtl", "return AR ON");
            return 1;
        } else {
            Log.e("BtLHDC_ArCtl", "return AR OFF");
            return 0;
        }
    }
}
