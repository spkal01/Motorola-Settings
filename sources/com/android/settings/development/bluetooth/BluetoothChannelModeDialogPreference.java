package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.C1978R$array;
import com.android.settings.C1985R$id;

public class BluetoothChannelModeDialogPreference extends BaseBluetoothDialogPreference {
    public BluetoothChannelModeDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothChannelModeDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothChannelModeDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothChannelModeDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    public int getRadioButtonGroupId() {
        return C1985R$id.bluetooth_audio_channel_mode_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_channel_mode_default));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_channel_mode_mono));
        this.mRadioButtonIds.add(Integer.valueOf(C1985R$id.bluetooth_audio_channel_mode_stereo));
        String[] stringArray = context.getResources().getStringArray(C1978R$array.bluetooth_a2dp_codec_channel_mode_titles);
        for (String add : stringArray) {
            this.mRadioButtonStrings.add(add);
        }
        String[] stringArray2 = context.getResources().getStringArray(C1978R$array.bluetooth_a2dp_codec_channel_mode_summaries);
        for (String add2 : stringArray2) {
            this.mSummaryStrings.add(add2);
        }
    }
}
