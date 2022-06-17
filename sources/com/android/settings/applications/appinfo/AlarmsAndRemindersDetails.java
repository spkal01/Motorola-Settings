package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.Settings;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class AlarmsAndRemindersDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppStateAlarmsAndRemindersBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private AppStateAlarmsAndRemindersBridge.AlarmsAndRemindersState mPermissionState;
    private RestrictedSwitchPreference mSwitchPref;
    private volatile Boolean mUncommittedState;

    /* access modifiers changed from: protected */
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    public int getMetricsCategory() {
        return 1869;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        int i;
        AppStateAlarmsAndRemindersBridge appStateAlarmsAndRemindersBridge = new AppStateAlarmsAndRemindersBridge(context, (ApplicationsState) null, (AppStateBaseBridge.Callback) null);
        ApplicationInfo applicationInfo = appEntry.info;
        if (appStateAlarmsAndRemindersBridge.createPermissionState(applicationInfo.packageName, applicationInfo.uid).isAllowed()) {
            i = C1992R$string.app_permission_summary_allowed;
        } else {
            i = C1992R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateAlarmsAndRemindersBridge(activity, this.mState, (AppStateBaseBridge.Callback) null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService(AppOpsManager.class);
        if (bundle != null) {
            this.mUncommittedState = (Boolean) bundle.get("uncommitted_state");
            if (this.mUncommittedState != null && isAppSpecific()) {
                setResult(this.mUncommittedState.booleanValue() ? -1 : 0);
            }
        }
        addPreferencesFromResource(C1994R$xml.alarms_and_reminders);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("alarms_and_reminders_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceChangeListener(this);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mUncommittedState != null) {
            bundle.putObject("uncommitted_state", this.mUncommittedState);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int i = 0;
        if (preference != this.mSwitchPref) {
            return false;
        }
        this.mUncommittedState = (Boolean) obj;
        if (isAppSpecific()) {
            if (this.mUncommittedState.booleanValue()) {
                i = -1;
            }
            setResult(i);
        }
        refreshUi();
        return true;
    }

    private void setCanScheduleAlarms(boolean z) {
        this.mAppOpsManager.setUidMode("android:schedule_exact_alarm", this.mPackageInfo.applicationInfo.uid, z ? 0 : 2);
    }

    private void logPermissionChange(boolean z, String str) {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), 1752, getMetricsCategory(), str, z ? 1 : 0);
    }

    private boolean isAppSpecific() {
        return Settings.AlarmsAndRemindersAppActivity.class.getName().equals(getIntent().getComponent().getClassName());
    }

    public void onPause() {
        super.onPause();
        if (!getActivity().isChangingConfigurations() && this.mPermissionState != null && this.mUncommittedState != null && this.mUncommittedState.booleanValue() != this.mPermissionState.isAllowed()) {
            setCanScheduleAlarms(this.mUncommittedState.booleanValue());
            logPermissionChange(this.mUncommittedState.booleanValue(), this.mPackageName);
            this.mUncommittedState = null;
        }
    }

    /* access modifiers changed from: protected */
    public boolean refreshUi() {
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) {
            return false;
        }
        AppStateAlarmsAndRemindersBridge.AlarmsAndRemindersState createPermissionState = this.mAppBridge.createPermissionState(this.mPackageName, applicationInfo.uid);
        this.mPermissionState = createPermissionState;
        this.mSwitchPref.setEnabled(createPermissionState.shouldBeVisible());
        this.mSwitchPref.setChecked(this.mUncommittedState != null ? this.mUncommittedState.booleanValue() : this.mPermissionState.isAllowed());
        return true;
    }
}
