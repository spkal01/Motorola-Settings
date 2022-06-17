package com.motorola.settings.display.colors;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1978R$array;
import com.android.settings.C1986R$integer;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SeekBarPreference;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.settings.utils.DisplayUtils;

public class ColorTemperaturePreferenceController extends SliderPreferenceController {
    private static final String KEY = "color_temperature";
    private int mDefaultTemperature;
    private int[] mTemperatures;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public int getMin() {
        return 0;
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

    public ColorTemperaturePreferenceController(Context context) {
        super(context, KEY);
        this.mDefaultTemperature = context.getResources().getInteger(C1986R$integer.config_color_temperature_default);
        this.mTemperatures = context.getResources().getIntArray(C1978R$array.config_color_temperatures);
    }

    public int getAvailabilityStatus() {
        return (!ColorDisplayManager.isNightDisplayAvailable(this.mContext) || !DisplayUtils.isColorTemperatureModeAvailable(this.mContext)) ? 3 : 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SeekBarPreference seekBarPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (seekBarPreference != null) {
            seekBarPreference.setIconSpaceReserved(false);
            seekBarPreference.setMax(getMax());
            seekBarPreference.setMin(getMin());
            seekBarPreference.setContinuousUpdates(true);
            seekBarPreference.setProgress(getNormalizedPosition());
        }
    }

    public int getSliderPosition() {
        return getNormalizedPosition();
    }

    public boolean setSliderPosition(int i) {
        setColorTemperature(this.mTemperatures[i]);
        return true;
    }

    public int getMax() {
        return this.mTemperatures.length - 1;
    }

    private int getNormalizedPosition() {
        return getApproximatedPosition(getColorTemperature());
    }

    private int getApproximatedPosition(int i) {
        int i2 = 0;
        int i3 = this.mTemperatures[0] - i;
        int i4 = 0;
        while (true) {
            int[] iArr = this.mTemperatures;
            if (i2 >= iArr.length) {
                return i4;
            }
            int abs = Math.abs(iArr[i2] - i);
            if (abs < i3) {
                i4 = i2;
                i3 = abs;
            }
            i2++;
        }
    }

    private int getColorTemperature() {
        return MotorolaSettings.System.getInt(this.mContext.getContentResolver(), "global_color_temperature", this.mDefaultTemperature);
    }

    private void setColorTemperature(int i) {
        if (i == this.mDefaultTemperature) {
            MotorolaSettings.System.putString(this.mContext.getContentResolver(), "global_color_temperature", (String) null);
        } else {
            MotorolaSettings.System.putInt(this.mContext.getContentResolver(), "global_color_temperature", i);
        }
    }
}
