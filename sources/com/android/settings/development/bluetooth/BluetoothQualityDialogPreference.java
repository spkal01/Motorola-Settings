package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.C1978R$array;
import com.android.settings.C1985R$id;

public class BluetoothQualityDialogPreference extends BaseBluetoothDialogPreference {
    /* access modifiers changed from: protected */
    public int getDefaultIndex() {
        return 3;
    }

    public BluetoothQualityDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothQualityDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothQualityDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothQualityDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    public int getRadioButtonGroupId() {
        return C1985R$id.bluetooth_audio_quality_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_quality_default));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_quality_optimized_quality));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_quality_optimized_connection));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_quality_best_effort));
        String[] stringArray = context.getResources().getStringArray(C1978R$array.bluetooth_a2dp_codec_ldac_playback_quality_titles);
        for (String add : stringArray) {
            this.mRadioButtonStrings.add(add);
        }
        String[] stringArray2 = context.getResources().getStringArray(C1978R$array.bluetooth_a2dp_codec_ldac_playback_quality_summaries);
        for (String add2 : stringArray2) {
            this.mSummaryStrings.add(add2);
        }
    }
}
