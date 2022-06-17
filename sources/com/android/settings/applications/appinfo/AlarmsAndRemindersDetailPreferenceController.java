package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.ApplicationsState;

public class AlarmsAndRemindersDetailPreferenceController extends AppInfoPreferenceControllerBase {
    private String mPackageName;

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

    public AlarmsAndRemindersDetailPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return isCandidate() ? 0 : 2;
    }

    public void updateState(Preference preference) {
        preference.setSummary(getPreferenceSummary());
    }

    /* access modifiers changed from: protected */
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AlarmsAndRemindersDetails.class;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getPreferenceSummary() {
        return AlarmsAndRemindersDetails.getSummary(this.mContext, this.mParent.getAppEntry());
    }

    /* access modifiers changed from: package-private */
    public boolean isCandidate() {
        PackageInfo packageInfo = this.mParent.getPackageInfo();
        if (packageInfo == null) {
            return false;
        }
        return new AppStateAlarmsAndRemindersBridge(this.mContext, (ApplicationsState) null, (AppStateBaseBridge.Callback) null).createPermissionState(this.mPackageName, packageInfo.applicationInfo.uid).shouldBeVisible();
    }

    /* access modifiers changed from: package-private */
    public void setPackageName(String str) {
        this.mPackageName = str;
    }
}
