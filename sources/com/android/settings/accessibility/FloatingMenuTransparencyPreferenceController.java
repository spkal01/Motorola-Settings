package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class FloatingMenuTransparencyPreferenceController extends SliderPreferenceController implements LifecycleObserver, OnResume, OnPause {
    static final float DEFAULT_TRANSPARENCY = 0.45f;
    private static final int FADE_ENABLED = 1;
    static final float MAXIMUM_TRANSPARENCY = 1.0f;
    private static final float MAX_PROGRESS = 90.0f;
    private static final float MIN_PROGRESS = 0.0f;
    static final float PRECISION = 100.0f;
    final ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            FloatingMenuTransparencyPreferenceController.this.updateAvailabilityStatus();
        }
    };
    private final ContentResolver mContentResolver;
    SeekBarPreference mPreference;

    private float convertTransparencyIntToFloat(int i) {
        return ((float) i) / PRECISION;
    }

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public int getMax() {
        return 90;
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

    public FloatingMenuTransparencyPreferenceController(Context context, String str) {
        super(context, str);
        this.mContentResolver = context.getContentResolver();
    }

    public int getAvailabilityStatus() {
        return AccessibilityUtil.isFloatingMenuEnabled(this.mContext) ? 0 : 5;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SeekBarPreference seekBarPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = seekBarPreference;
        seekBarPreference.setContinuousUpdates(true);
        this.mPreference.setMax(getMax());
        this.mPreference.setMin(getMin());
        this.mPreference.setHapticFeedbackMode(2);
        updateState(this.mPreference);
    }

    public void onResume() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_button_mode"), false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_floating_menu_fade_enabled"), false, this.mContentObserver);
    }

    public void onPause() {
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
    }

    public int getSliderPosition() {
        return convertTransparencyFloatToInt(getTransparency());
    }

    public boolean setSliderPosition(int i) {
        return Settings.Secure.putFloat(this.mContentResolver, "accessibility_floating_menu_opacity", MAXIMUM_TRANSPARENCY - convertTransparencyIntToFloat(i));
    }

    /* access modifiers changed from: private */
    public void updateAvailabilityStatus() {
        boolean z = true;
        boolean z2 = Settings.Secure.getInt(this.mContentResolver, "accessibility_floating_menu_fade_enabled", 1) == 1;
        SeekBarPreference seekBarPreference = this.mPreference;
        if (!AccessibilityUtil.isFloatingMenuEnabled(this.mContext) || !z2) {
            z = false;
        }
        seekBarPreference.setEnabled(z);
    }

    private int convertTransparencyFloatToInt(float f) {
        return Math.round(f * PRECISION);
    }

    private float getTransparency() {
        float f = MAXIMUM_TRANSPARENCY - Settings.Secure.getFloat(this.mContentResolver, "accessibility_floating_menu_opacity", DEFAULT_TRANSPARENCY);
        return (f < MIN_PROGRESS || f > 0.9f) ? DEFAULT_TRANSPARENCY : f;
    }
}
