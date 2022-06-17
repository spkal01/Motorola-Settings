package com.motorola.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.C1978R$array;
import com.android.settings.C1985R$id;
import com.android.settings.development.bluetooth.BaseBluetoothDialogPreference;

public class BluetoothLHDCArDialogPreference extends BaseBluetoothDialogPreference {
    /* access modifiers changed from: protected */
    public int getDefaultIndex() {
        return 0;
    }

    public BluetoothLHDCArDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothLHDCArDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothLHDCArDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothLHDCArDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    public int getRadioButtonGroupId() {
        return C1985R$id.bluetooth_audio_lhdc_ar_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_disable_ar));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_enable_ar));
        String[] stringArray = context.getResources().getStringArray(C1978R$array.bluetooth_enable_a2dp_codec_lhdc_ar_effect_titles);
        for (String add : stringArray) {
            this.mRadioButtonStrings.add(add);
        }
        String[] stringArray2 = context.getResources().getStringArray(C1978R$array.bluetooth_enable_a2dp_codec_lhdc_ar_effect_summaries);
        for (String add2 : stringArray2) {
            this.mSummaryStrings.add(add2);
        }
    }
}
