package com.motorola.settings.network.tmo;

import android.content.Context;
import android.content.IntentFilter;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.telephony.Enable2gPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.network.tmo.TmoEnabledNetworkDialogFragment;

public class Tmo2GToggleEnablerController extends Enable2gPreferenceController implements TmoEnabledNetworkDialogFragment.PreferredWarningListener {
    private static final String TAG = "Tmo2GToggleEnablerController";
    private FragmentManager mFragmentManager;
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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public Tmo2GToggleEnablerController(Context context, String str) {
        super(context, str);
    }

    public Enable2gPreferenceController init(int i, FragmentManager fragmentManager) {
        super.init(i);
        String str = TAG;
        MotoMnsLog.logd(str, "calling init : " + i);
        this.mFragmentManager = fragmentManager;
        return this;
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr7454Enabled(this.mContext, i) ? 0 : 2;
    }

    public boolean setChecked(boolean z) {
        if (!z) {
            showEnabledNetworkDialog();
        }
        return super.setChecked(z);
    }

    private void showEnabledNetworkDialog() {
        TmoEnabledNetworkDialogFragment newInstance = TmoEnabledNetworkDialogFragment.newInstance();
        newInstance.setCallback(this);
        newInstance.show(this.mFragmentManager, "tmo_dialog_tag");
    }

    public void onAction(boolean z) {
        String str = TAG;
        MotoMnsLog.logd(str, "onAction: " + z);
        if (!z) {
            setChecked(true);
            updateState(this.mPreference);
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }
}
