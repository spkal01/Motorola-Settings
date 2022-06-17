package com.motorola.settings.network.tmo;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.network.MotoTelephonyTogglePreferenceController;
import java.util.concurrent.Executors;

public class TmoVoNRPreferenceController extends MotoTelephonyTogglePreferenceController implements LifecycleObserver {
    private static final String DISABLED = "0";
    private static final String ENABLED = "1";
    private static final String TAG = "TmoVoNRPreferenceController";
    private static final String VOICE_OVER_NR_ENABLE = "voNr_enabled";
    private static final String configEnableUri = "content://com.motorola.carrierconfig.provider.VoNRConfigProvider/voNr_enabled";
    private static final String configVisibilityUri = "content://com.motorola.carrierconfig.provider.VoNRConfigProvider/voNr_toggle_available";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private Preference mPreference;
    private boolean mVoNRState = true;
    private MotoExtTelephonyManager motTm;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TmoVoNRPreferenceController(Context context, String str) {
        super(context, str);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null) {
            allowedNetworkTypesListener.register(this.mContext, this.mSubId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null) {
            allowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
        }
    }

    public void init(int i, FragmentManager fragmentManager, Lifecycle lifecycle) {
        super.init(i, fragmentManager);
        this.motTm = new MotoExtTelephonyManager(this.mContext, this.mSubId);
        this.mVoNRState = getStatus();
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new TmoVoNRPreferenceController$$ExternalSyntheticLambda0(this));
        }
        lifecycle.addObserver(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        updateState(this.mPreference);
    }

    public int getAvailabilityStatus(int i) {
        return (!MotoTelephonyFeature.isFtr7199Enabled(this.mContext, i) || !getVisibility()) ? 2 : 0;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(MotoMobileNetworkUtils.carrierAllowed5g(this.mContext, this.mSubId));
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public boolean isChecked() {
        return this.mVoNRState;
    }

    public boolean setChecked(boolean z) {
        this.mVoNRState = z;
        updateValue();
        return true;
    }

    private boolean getVisibility() {
        return TmoConfigUtils.getConfigFromTMOConfig(this.mContext, configVisibilityUri, this.mSubId);
    }

    private boolean getStatus() {
        return TmoConfigUtils.getConfigFromTMOConfig(this.mContext, configEnableUri, this.mSubId);
    }

    private void updateValue() {
        Executors.newSingleThreadExecutor().execute(new TmoVoNRPreferenceController$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateValue$1() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VOICE_OVER_NR_ENABLE, Boolean.valueOf(this.mVoNRState));
        this.mContext.getContentResolver().update(Uri.parse(configEnableUri), contentValues, buildTmoConfigQueryArgs(this.mSubId));
    }

    private Bundle buildTmoConfigQueryArgs(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("subId", i);
        return bundle;
    }
}
