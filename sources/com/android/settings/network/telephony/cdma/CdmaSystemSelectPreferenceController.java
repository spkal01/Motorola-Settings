package com.android.settings.network.telephony.cdma;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;
import com.motorola.settings.network.MotoMnsLog;

public class CdmaSystemSelectPreferenceController extends CdmaBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String LOG_TAG = "CdmaSystemSelectPreferenceController";

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

    public CdmaSystemSelectPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        boolean z = getAvailabilityStatus() == 0;
        listPreference.setVisible(z);
        listPreference.setOnPreferenceChangeListener(z ? this : null);
        int cdmaRoamingMode = this.mTelephonyManager.getCdmaRoamingMode();
        if (cdmaRoamingMode != -1) {
            if (cdmaRoamingMode == 0 || cdmaRoamingMode == 2) {
                listPreference.setValue(Integer.toString(cdmaRoamingMode));
            } else {
                resetCdmaRoamingModeToDefault();
            }
        }
        updateStateBasedOnNwMode(listPreference, MobileNetworkUtils.getNetworkTypeFromRaf((int) this.mTelephonyManager.getAllowedNetworkTypesForReason(0)));
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        try {
            if (parseInt == getCdmaRoamingMode()) {
                return false;
            }
            setPreferredCdmaSystem(parseInt);
            return true;
        } catch (IllegalStateException unused) {
            return false;
        }
    }

    private void resetCdmaRoamingModeToDefault() {
        ((ListPreference) this.mPreference).setValue(Integer.toString(2));
        Settings.Global.putInt(this.mContext.getContentResolver(), "roaming_settings", 2);
        this.mTelephonyManager.setCdmaRoamingMode(2);
    }

    private int getCdmaRoamingMode() {
        try {
            return Settings.Global.getInt(this.mContext.getContentResolver(), "roaming_settings");
        } catch (Exception unused) {
            return -1;
        }
    }

    /* access modifiers changed from: protected */
    public void setPreferredCdmaSystem(int i) {
        MotoMnsLog.logd(LOG_TAG, "set preferred CDMA to: " + i);
        ThreadUtils.postOnBackgroundThread((Runnable) new CdmaSystemSelectPreferenceController$$ExternalSyntheticLambda0(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setPreferredCdmaSystem$0(int i) {
        this.mTelephonyManager.setCdmaRoamingMode(i);
        MotoMnsLog.logd(LOG_TAG, "doInBackground - Cdma Roaming Mode: " + i);
        Settings.Global.putInt(this.mContext.getContentResolver(), "roaming_settings", i);
    }

    /* access modifiers changed from: protected */
    public void updateStateBasedOnNwMode(Preference preference, int i) {
        preference.setEnabled((i == 9 || i == 26) ? false : true);
    }
}
