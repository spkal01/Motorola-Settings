package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.AirplaneModeEnabler;
import com.android.settings.C1980R$bool;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.network.MotoMobileNetworkUtils;

public class AirplaneModePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop, OnDestroy, AirplaneModeEnabler.OnAirplaneModeChangedListener {
    private static final String EXIT_ECM_RESULT = "exit_ecm_result";
    public static final int REQUEST_CODE_CONFIRM_DIALOG = 2;
    public static final int REQUEST_CODE_EXIT_ECM = 1;
    public static final Uri SLICE_URI = new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("airplane_mode").build();
    private AirplaneModeEnabler mAirplaneModeEnabler;
    private SwitchPreference mAirplaneModePreference;
    private Fragment mFragment;

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

    public boolean isPublicSlice() {
        return true;
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AirplaneModePreferenceController(Context context, String str) {
        super(context, str);
        if (isAvailable(this.mContext)) {
            this.mAirplaneModeEnabler = new AirplaneModeEnabler(this.mContext, this);
        }
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    /* access modifiers changed from: package-private */
    public void setAirplaneModeEnabler(AirplaneModeEnabler airplaneModeEnabler) {
        this.mAirplaneModeEnabler = airplaneModeEnabler;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!"airplane_mode".equals(preference.getKey()) || !this.mAirplaneModeEnabler.isInEcmMode()) {
            return false;
        }
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            fragment.startActivityForResult(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null), 1);
        }
        return true;
    }

    public Uri getSliceUri() {
        return SLICE_URI;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mAirplaneModePreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public static boolean isAvailable(Context context) {
        return context.getResources().getBoolean(C1980R$bool.config_show_toggle_airplane) && !context.getPackageManager().hasSystemFeature("android.software.leanback");
    }

    public int getAvailabilityStatus() {
        return isAvailable(this.mContext) ? 0 : 3;
    }

    public void onStart() {
        if (isAvailable()) {
            this.mAirplaneModeEnabler.start();
        }
    }

    public void onStop() {
        if (isAvailable()) {
            this.mAirplaneModeEnabler.stop();
        }
    }

    public void onDestroy() {
        this.mAirplaneModeEnabler.close();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            this.mAirplaneModeEnabler.setAirplaneModeInECM(intent.getBooleanExtra(EXIT_ECM_RESULT, false), this.mAirplaneModePreference.isChecked());
        } else if (i == 2 && i2 == -1) {
            this.mAirplaneModeEnabler.setAirplaneMode(true);
        }
    }

    public boolean isChecked() {
        return this.mAirplaneModeEnabler.isAirplaneModeOn();
    }

    public boolean setChecked(boolean z) {
        Intent intentResolvedToSystemActivity;
        if (isChecked() == z) {
            return false;
        }
        if (!z || this.mFragment == null || (intentResolvedToSystemActivity = MotoMobileNetworkUtils.getIntentResolvedToSystemActivity(this.mContext, "com.motorola.settings.action.AIRPLANE_MODE_ON")) == null || Settings.Global.getInt(this.mContext.getContentResolver(), "mot_show_airplanemode_dialog", 1) != 1) {
            this.mAirplaneModeEnabler.setAirplaneMode(z);
            return true;
        }
        this.mFragment.startActivityForResult(intentResolvedToSystemActivity, 2);
        return false;
    }

    public void onAirplaneModeChanged(boolean z) {
        SwitchPreference switchPreference = this.mAirplaneModePreference;
        if (switchPreference != null) {
            switchPreference.setChecked(z);
        }
    }
}
