package com.motorola.settings.development.bluetooth;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C1992R$string;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class BluetoothAACWhiteListPreferenceController extends AbstractBluetoothPreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String[] aacVbrSupportedPlatform = {"lito", "kona", "lahaina", "taro"};

    public String getPreferenceKey() {
        return "bluetooth_aac_white_list_settings";
    }

    public BluetoothAACWhiteListPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    public boolean isAvailable() {
        boolean z;
        String str = SystemProperties.get("ro.hardware.soc.manufacturer", "");
        String str2 = SystemProperties.get("ro.board.platform", "");
        if (!str.equals("qcom")) {
            return false;
        }
        String[] strArr = aacVbrSupportedPlatform;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            String str3 = strArr[i];
            if (str3.equals(str2)) {
                Log.i("BtAACWhiteListCtr", "board platform is: " + str3);
                z = true;
                break;
            }
            i++;
        }
        if (z) {
            return false;
        }
        return true;
    }

    public void updateState(Preference preference) {
        boolean z = SystemProperties.getBoolean("persist.vendor.bt.a2dp.aac_whitelist", true);
        if (z) {
            ((SwitchPreference) this.mPreference).setSummary(C1992R$string.bt_aac_white_list_enabled_summary);
        } else {
            ((SwitchPreference) this.mPreference).setSummary(C1992R$string.bt_aac_white_list_disabled_summary);
        }
        ((SwitchPreference) this.mPreference).setChecked(z);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append(booleanValue ? " enables" : " disables");
        sb.append(" AAC whitelist.");
        Log.i("BtAACWhiteListCtr", sb.toString());
        if (booleanValue) {
            ((SwitchPreference) this.mPreference).setSummary(C1992R$string.bt_aac_white_list_enabled_summary);
        } else {
            ((SwitchPreference) this.mPreference).setSummary(C1992R$string.bt_aac_white_list_disabled_summary);
        }
        SystemProperties.set("persist.vendor.bt.a2dp.aac_whitelist", Boolean.toString(booleanValue));
        return true;
    }
}
