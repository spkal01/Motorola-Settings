package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.gestures.TriplePowerKeySettingObserver;
import com.motorola.settings.gestures.TripleTapPowerEmergencyCallPrefController;

public class DoubleTapPowerPreferenceController extends GesturePreferenceController {
    static final int OFF = 1;

    /* renamed from: ON */
    static final int f101ON = 0;
    private static final String PREF_KEY_VIDEO = "gesture_double_tap_power_video";
    private final String SECURE_KEY = "camera_double_tap_power_gesture_disabled";
    private TriplePowerKeySettingObserver mSettingObserver = null;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    /* access modifiers changed from: protected */
    public String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public boolean isPublicSlice() {
        return true;
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DoubleTapPowerPreferenceController(Context context, String str) {
        super(context, str);
    }

    public static boolean isSuggestionComplete(Context context, SharedPreferences sharedPreferences) {
        if (!isGestureAvailable(context) || sharedPreferences.getBoolean("pref_double_tap_power_suggestion_complete", false)) {
            return true;
        }
        return false;
    }

    private static boolean isGestureAvailable(Context context) {
        if (context.getResources().getInteger(17694808) == 3) {
            return false;
        }
        return context.getResources().getBoolean(17891408);
    }

    public int getAvailabilityStatus() {
        return isGestureAvailable(this.mContext) ? 0 : 3;
    }

    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "gesture_double_tap_power");
    }

    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", 0) == 0;
    }

    public boolean setChecked(boolean z) {
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", z ^ true ? 1 : 0);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isGestureAvailable(this.mContext) && TripleTapPowerEmergencyCallPrefController.isGestureAvailable(this.mContext)) {
            this.mSettingObserver = new TriplePowerKeySettingObserver(preferenceScreen.findPreference(getPreferenceKey()), this);
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null) {
            return;
        }
        if (TripleTapPowerEmergencyCallPrefController.isTripleTapEmergencyCallEnabled(this.mContext)) {
            preference.setEnabled(false);
            preference.setSummary((CharSequence) this.mContext.getResources().getString(C1992R$string.double_press_power_setting_disabled));
            return;
        }
        preference.setEnabled(true);
    }

    public void onStart() {
        super.onStart();
        TriplePowerKeySettingObserver triplePowerKeySettingObserver = this.mSettingObserver;
        if (triplePowerKeySettingObserver != null) {
            triplePowerKeySettingObserver.register(this.mContext.getContentResolver());
        }
    }

    public void onStop() {
        super.onStop();
        TriplePowerKeySettingObserver triplePowerKeySettingObserver = this.mSettingObserver;
        if (triplePowerKeySettingObserver != null) {
            triplePowerKeySettingObserver.unregister(this.mContext.getContentResolver());
        }
    }
}
