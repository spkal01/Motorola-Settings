package com.motorola.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.widget.RadioButtonPreference;
import com.motorola.android.provider.MotorolaSettings;

public class FODAnimationRadioPreferenceController extends BasePreferenceController implements LifecycleObserver {
    public static final String KEY_STYLE_1 = "style_1";
    public static final String KEY_STYLE_2 = "style_2";
    public static final String KEY_STYLE_3 = "style_3";
    private final ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            super.onChange(z);
            FODAnimationRadioPreferenceController fODAnimationRadioPreferenceController = FODAnimationRadioPreferenceController.this;
            fODAnimationRadioPreferenceController.updateState(fODAnimationRadioPreferenceController.mPreference);
        }
    };
    private final RadioButtonPreference.OnClickListener mOnClickListener = new FODAnimationRadioPreferenceController$$ExternalSyntheticLambda0(this);
    /* access modifiers changed from: private */
    public RadioButtonPreference mPreference;
    private int mValue;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 0;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mPreference;
        if (radioButtonPreference2 != null && !radioButtonPreference2.isChecked()) {
            FingerprintUtils.setFingerprintAnimationStyle(this.mContext, this.mValue);
        }
    }

    public FODAnimationRadioPreferenceController(Context context, Lifecycle lifecycle, String str, int i) {
        super(context, str);
        this.mValue = i;
        lifecycle.addObserver(this);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = radioButtonPreference;
        if (radioButtonPreference != null) {
            radioButtonPreference.setOnClickListener(this.mOnClickListener);
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(FingerprintUtils.getFingerprintAnimationStyle(this.mContext) == this.mValue);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("fod_animation_style"), true, this.mContentObserver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }
}
