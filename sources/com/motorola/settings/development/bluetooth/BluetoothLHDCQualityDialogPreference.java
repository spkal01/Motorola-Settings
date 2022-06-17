package com.motorola.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.C1978R$array;
import com.android.settings.C1985R$id;
import com.android.settings.development.bluetooth.BaseBluetoothDialogPreference;

public class BluetoothLHDCQualityDialogPreference extends BaseBluetoothDialogPreference {
    /* access modifiers changed from: protected */
    public int getDefaultIndex() {
        return 5;
    }

    public BluetoothLHDCQualityDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothLHDCQualityDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothLHDCQualityDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothLHDCQualityDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    public int getRadioButtonGroupId() {
        return C1985R$id.bluetooth_audio_lhdc_quality_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_64kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_96kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_128kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_192kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_256kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_400kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_500kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_900kbps));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_lhdc_quality_auto));
        String[] stringArray = context.getResources().getStringArray(C1978R$array.bluetooth_a2dp_codec_lhdc_playback_quality_titles);
        for (String add : stringArray) {
            this.mRadioButtonStrings.add(add);
        }
        String[] stringArray2 = context.getResources().getStringArray(C1978R$array.bluetooth_a2dp_codec_lhdc_playback_quality_summaries);
        for (String add2 : stringArray2) {
            this.mSummaryStrings.add(add2);
        }
    }
}
