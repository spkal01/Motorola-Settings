package com.android.settings.security;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;

public class ConfirmSimDeletionPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String KEY_CONFIRM_SIM_DELETION = "confirm_sim_deletion";
    private boolean mAsync;
    private boolean mConfirmationDefaultOn;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private Preference mPreference;

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

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ConfirmSimDeletionPreferenceController(Context context, String str) {
        super(context, str);
        this.mConfirmationDefaultOn = context.getResources().getBoolean(C1980R$bool.config_sim_deletion_confirmation_default_on);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public ConfirmSimDeletionPreferenceController(Context context, boolean z) {
        this(context, KEY_CONFIRM_SIM_DELETION);
        this.mAsync = z;
    }

    public int getAvailabilityStatus() {
        if (!this.mAsync && !MobileNetworkUtils.showEuiccSettings(this.mContext)) {
            return 3;
        }
        return 0;
    }

    private boolean getGlobalState() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), KEY_CONFIRM_SIM_DELETION, this.mConfirmationDefaultOn ? 1 : 0) == 1;
    }

    public boolean isChecked() {
        return getGlobalState();
    }

    public boolean setChecked(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), KEY_CONFIRM_SIM_DELETION, z ? 1 : 0);
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!preference.getKey().equals(getPreferenceKey())) {
            return false;
        }
        if (!isChecked()) {
            this.mMetricsFeatureProvider.action(this.mContext, 1738, (Pair<Integer, Object>[]) new Pair[0]);
            setChecked(true);
            return true;
        }
        WifiDppUtils.showLockScreen(this.mContext, new ConfirmSimDeletionPreferenceController$$ExternalSyntheticLambda1(this, preference));
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceChange$0(Preference preference) {
        this.mMetricsFeatureProvider.action(this.mContext, 1739, (Pair<Integer, Object>[]) new Pair[0]);
        setChecked(false);
        ((TwoStatePreference) preference).setChecked(false);
    }

    public void updateState(Preference preference) {
        ThreadUtils.postOnBackgroundThread((Runnable) new ConfirmSimDeletionPreferenceController$$ExternalSyntheticLambda0(this));
        if (!((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isKeyguardSecure()) {
            preference.setEnabled(false);
            if (preference instanceof TwoStatePreference) {
                ((TwoStatePreference) preference).setChecked(false);
            }
            preference.setSummary(C1992R$string.disabled_because_no_backup_security);
            return;
        }
        preference.setEnabled(true);
        if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(getGlobalState());
        }
        preference.setSummary(C1992R$string.confirm_sim_deletion_description);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$2() {
        ThreadUtils.postOnMainThread(new ConfirmSimDeletionPreferenceController$$ExternalSyntheticLambda2(this, MobileNetworkUtils.showEuiccSettings(this.mContext)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(boolean z) {
        this.mPreference.setVisible(z);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(KEY_CONFIRM_SIM_DELETION);
        this.mPreference = findPreference;
        findPreference.setVisible(false);
    }
}
