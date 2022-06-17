package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class BluetoothCodecDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    private final AbstractBluetoothPreferenceController.Callback mCallback;

    public String getPreferenceKey() {
        return "bluetooth_audio_codec_settings";
    }

    public BluetoothCodecDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore, AbstractBluetoothPreferenceController.Callback callback) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
        this.mCallback = callback;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((BaseBluetoothDialogPreference) this.mPreference).setCallback(this);
    }

    public List<Integer> getSelectableIndex() {
        BluetoothCodecConfig[] selectableConfigs;
        ArrayList arrayList = new ArrayList();
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        if (bluetoothA2dp == null) {
            return arrayList;
        }
        BluetoothDevice activeDevice = bluetoothA2dp.getActiveDevice();
        if (activeDevice == null) {
            Log.d("BtCodecCtr", "Unable to get selectable index. No Active Bluetooth device");
            return arrayList;
        } else if (bluetoothA2dp.isOptionalCodecsEnabled(activeDevice) == 1 && (selectableConfigs = getSelectableConfigs(activeDevice)) != null) {
            return getIndexFromConfig(selectableConfigs);
        } else {
            arrayList.add(Integer.valueOf(convertCfgToBtnIndex(0)));
            return arrayList;
        }
    }

    /* access modifiers changed from: protected */
    public void writeConfigurationValues(int i) {
        int i2 = 0;
        int i3 = 1000000;
        switch (i) {
            case 0:
                i2 = AbstractBluetoothDialogPreferenceController.getHighestCodec(getSelectableConfigs(this.mBluetoothA2dp.getActiveDevice()));
                break;
            case 1:
                break;
            case 2:
                i2 = 1;
                break;
            case 3:
                i2 = 2;
                break;
            case 4:
                i2 = 3;
                break;
            case 5:
                i2 = 4;
                break;
            case 6:
                i2 = 5;
                break;
            case 7:
                i2 = 6;
                break;
            case 8:
                i2 = 7;
                break;
            case 9:
                i2 = 8;
                break;
            case 10:
                i2 = 9;
                break;
            default:
                i3 = 0;
                break;
        }
        this.mBluetoothA2dpConfigStore.setCodecType(i2);
        this.mBluetoothA2dpConfigStore.setCodecPriority(i3);
        BluetoothCodecConfig selectableByCodecType = getSelectableByCodecType(i2);
        if (selectableByCodecType == null) {
            Log.d("BtCodecCtr", "Selectable config is null. Unable to reset");
        }
        this.mBluetoothA2dpConfigStore.setSampleRate(AbstractBluetoothDialogPreferenceController.getHighestSampleRate(selectableByCodecType));
        this.mBluetoothA2dpConfigStore.setBitsPerSample(AbstractBluetoothDialogPreferenceController.getHighestBitsPerSample(selectableByCodecType));
        this.mBluetoothA2dpConfigStore.setChannelMode(AbstractBluetoothDialogPreferenceController.getHighestChannelMode(selectableByCodecType));
    }

    /* access modifiers changed from: protected */
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtCodecCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getCodecType());
    }

    public void onIndexUpdated(int i) {
        super.onIndexUpdated(i);
        this.mCallback.onBluetoothCodecChanged();
    }

    private List<Integer> getIndexFromConfig(BluetoothCodecConfig[] bluetoothCodecConfigArr) {
        ArrayList arrayList = new ArrayList();
        for (BluetoothCodecConfig codecType : bluetoothCodecConfigArr) {
            arrayList.add(Integer.valueOf(convertCfgToBtnIndex(codecType.getCodecType())));
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public int convertCfgToBtnIndex(int i) {
        int defaultIndex = getDefaultIndex();
        switch (i) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            case 8:
                return 9;
            case 9:
                return 10;
            default:
                Log.e("BtCodecCtr", "Unsupported config:" + i);
                return defaultIndex;
        }
    }
}
