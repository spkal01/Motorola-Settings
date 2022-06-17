package com.android.settings.applications.appinfo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.BatteryUsageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.UidBatteryConsumer;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.fuelgauge.AdvancedPowerUsageDetail;
import com.android.settings.fuelgauge.BatteryChartPreferenceController;
import com.android.settings.fuelgauge.BatteryDiffEntry;
import com.android.settings.fuelgauge.BatteryEntry;
import com.android.settings.fuelgauge.BatteryUsageStatsLoader;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.motorola.settings.utils.CloneAppUtils;
import java.util.List;

public class AppBatteryPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_BATTERY = "battery";
    private static final String TAG = "AppBatteryPreferenceController";
    private boolean mBatteryDiffEntriesLoaded = false;
    BatteryDiffEntry mBatteryDiffEntry;
    private String mBatteryPercent;
    BatteryUsageStats mBatteryUsageStats;
    private boolean mBatteryUsageStatsLoaded = false;
    final BatteryUsageStatsLoaderCallbacks mBatteryUsageStatsLoaderCallbacks = new BatteryUsageStatsLoaderCallbacks();
    BatteryUtils mBatteryUtils;
    boolean mIsChartGraphEnabled;
    /* access modifiers changed from: private */
    public final String mPackageName;
    private final AppInfoDashboardFragment mParent;
    private Preference mPreference;
    private final int mUid;
    UidBatteryConsumer mUidBatteryConsumer;
    /* access modifiers changed from: private */
    public final int mUserId;

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

    public AppBatteryPreferenceController(Context context, AppInfoDashboardFragment appInfoDashboardFragment, String str, int i, Lifecycle lifecycle) {
        super(context, KEY_BATTERY);
        this.mParent = appInfoDashboardFragment;
        this.mBatteryUtils = BatteryUtils.getInstance(this.mContext);
        this.mPackageName = str;
        this.mUid = i;
        if (CloneAppUtils.isCloneAppByUid(i)) {
            this.mUserId = UserHandle.getUserId(i);
        } else {
            this.mUserId = this.mContext.getUserId();
        }
        refreshFeatureFlag(this.mContext);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_app_info_settings_battery) ? 0 : 2;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        findPreference.setEnabled(false);
        loadBatteryDiffEntries();
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_BATTERY.equals(preference.getKey())) {
            return false;
        }
        if (this.mBatteryDiffEntry != null) {
            Log.i(TAG, "BatteryDiffEntry not null, launch : " + this.mBatteryDiffEntry.getPackageName() + " | uid : " + this.mBatteryDiffEntry.mBatteryHistEntry.mUid + " with DiffEntry data");
            FragmentActivity activity = this.mParent.getActivity();
            AppInfoDashboardFragment appInfoDashboardFragment = this.mParent;
            BatteryDiffEntry batteryDiffEntry = this.mBatteryDiffEntry;
            AdvancedPowerUsageDetail.startBatteryDetailPage(activity, appInfoDashboardFragment, batteryDiffEntry, Utils.formatPercentage(batteryDiffEntry.getPercentOfTotal(), true), true, (String) null);
            return true;
        }
        if (isBatteryStatsAvailable()) {
            Context context = this.mContext;
            UidBatteryConsumer uidBatteryConsumer = this.mUidBatteryConsumer;
            BatteryEntry batteryEntry = new BatteryEntry(context, (Handler) null, (UserManager) this.mContext.getSystemService("user"), uidBatteryConsumer, false, uidBatteryConsumer.getUid(), (String[]) null, this.mPackageName);
            Log.i(TAG, "Battery consumer available, launch : " + batteryEntry.getDefaultPackageName() + " | uid : " + batteryEntry.getUid() + " with BatteryEntry data");
            AdvancedPowerUsageDetail.startBatteryDetailPage(this.mParent.getActivity(), this.mParent, batteryEntry, this.mIsChartGraphEnabled ? Utils.formatPercentage(0) : this.mBatteryPercent, !this.mIsChartGraphEnabled);
        } else {
            Log.i(TAG, "Launch : " + this.mPackageName + " with package name");
            if (CloneAppUtils.isCloneAppByUid(this.mUid)) {
                AdvancedPowerUsageDetail.startBatteryDetailPage(this.mParent.getActivity(), this.mParent, this.mPackageName, this.mUid);
            } else {
                AdvancedPowerUsageDetail.startBatteryDetailPage((Activity) this.mParent.getActivity(), (InstrumentedPreferenceFragment) this.mParent, this.mPackageName);
            }
        }
        return true;
    }

    public void onResume() {
        this.mParent.getLoaderManager().restartLoader(5, Bundle.EMPTY, this.mBatteryUsageStatsLoaderCallbacks);
    }

    public void onPause() {
        this.mParent.getLoaderManager().destroyLoader(5);
    }

    private void loadBatteryDiffEntries() {
        new AsyncTask<Void, Void, BatteryDiffEntry>() {
            /* access modifiers changed from: protected */
            public BatteryDiffEntry doInBackground(Void... voidArr) {
                List<BatteryDiffEntry> batteryLast24HrUsageData;
                if (AppBatteryPreferenceController.this.mPackageName == null || (batteryLast24HrUsageData = BatteryChartPreferenceController.getBatteryLast24HrUsageData(AppBatteryPreferenceController.this.mContext)) == null) {
                    return null;
                }
                return (BatteryDiffEntry) batteryLast24HrUsageData.stream().filter(AppBatteryPreferenceController$1$$ExternalSyntheticLambda2.INSTANCE).filter(new AppBatteryPreferenceController$1$$ExternalSyntheticLambda1(this)).filter(new AppBatteryPreferenceController$1$$ExternalSyntheticLambda0(this)).findFirst().orElse((Object) null);
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ boolean lambda$doInBackground$0(BatteryDiffEntry batteryDiffEntry) {
                return batteryDiffEntry.mBatteryHistEntry.mConsumerType == 1;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ boolean lambda$doInBackground$1(BatteryDiffEntry batteryDiffEntry) {
                return batteryDiffEntry.mBatteryHistEntry.mUserId == ((long) AppBatteryPreferenceController.this.mUserId);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ boolean lambda$doInBackground$2(BatteryDiffEntry batteryDiffEntry) {
                if (!AppBatteryPreferenceController.this.mPackageName.equals(batteryDiffEntry.getPackageName())) {
                    return false;
                }
                Log.i(AppBatteryPreferenceController.TAG, "Return target application: " + batteryDiffEntry.mBatteryHistEntry.mPackageName + " | uid: " + batteryDiffEntry.mBatteryHistEntry.mUid + " | userId: " + batteryDiffEntry.mBatteryHistEntry.mUserId);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(BatteryDiffEntry batteryDiffEntry) {
                AppBatteryPreferenceController appBatteryPreferenceController = AppBatteryPreferenceController.this;
                appBatteryPreferenceController.mBatteryDiffEntry = batteryDiffEntry;
                appBatteryPreferenceController.updateBatteryWithDiffEntry();
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: package-private */
    public void updateBatteryWithDiffEntry() {
        if (this.mIsChartGraphEnabled) {
            BatteryDiffEntry batteryDiffEntry = this.mBatteryDiffEntry;
            if (batteryDiffEntry == null || batteryDiffEntry.mConsumePower <= 0.0d) {
                this.mPreference.setSummary((CharSequence) this.mContext.getString(C1992R$string.no_battery_summary_24hr));
            } else {
                String formatPercentage = Utils.formatPercentage(batteryDiffEntry.getPercentOfTotal(), true);
                this.mBatteryPercent = formatPercentage;
                this.mPreference.setSummary((CharSequence) this.mContext.getString(C1992R$string.battery_summary_24hr, new Object[]{formatPercentage}));
            }
        }
        this.mBatteryDiffEntriesLoaded = true;
        this.mPreference.setEnabled(this.mBatteryUsageStatsLoaded);
    }

    /* access modifiers changed from: private */
    public void onLoadFinished() {
        PackageInfo packageInfo;
        if (this.mBatteryUsageStats != null && (packageInfo = this.mParent.getPackageInfo()) != null) {
            this.mUidBatteryConsumer = findTargetUidBatteryConsumer(this.mBatteryUsageStats, packageInfo.applicationInfo.uid);
            if (this.mParent.getActivity() != null) {
                updateBattery();
            }
        }
    }

    private void refreshFeatureFlag(Context context) {
        if (isWorkProfile(context)) {
            try {
                context = context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.OWNER);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "context.createPackageContextAsUser() fail: " + e);
            }
        }
        this.mIsChartGraphEnabled = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).isChartGraphEnabled(context);
    }

    private boolean isWorkProfile(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        return userManager.isManagedProfile() && !userManager.isSystemUser();
    }

    /* access modifiers changed from: package-private */
    public void updateBattery() {
        this.mBatteryUsageStatsLoaded = true;
        this.mPreference.setEnabled(this.mBatteryDiffEntriesLoaded);
        if (!this.mIsChartGraphEnabled) {
            if (isBatteryStatsAvailable()) {
                String formatPercentage = Utils.formatPercentage((int) this.mBatteryUtils.calculateBatteryPercent(this.mUidBatteryConsumer.getConsumedPower(), this.mBatteryUsageStats.getConsumedPower(), this.mBatteryUsageStats.getDischargePercentage()));
                this.mBatteryPercent = formatPercentage;
                this.mPreference.setSummary((CharSequence) this.mContext.getString(C1992R$string.battery_summary, new Object[]{formatPercentage}));
                return;
            }
            this.mPreference.setSummary((CharSequence) this.mContext.getString(C1992R$string.no_battery_summary));
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isBatteryStatsAvailable() {
        return this.mUidBatteryConsumer != null;
    }

    /* access modifiers changed from: package-private */
    public UidBatteryConsumer findTargetUidBatteryConsumer(BatteryUsageStats batteryUsageStats, int i) {
        List uidBatteryConsumers = batteryUsageStats.getUidBatteryConsumers();
        int size = uidBatteryConsumers.size();
        for (int i2 = 0; i2 < size; i2++) {
            UidBatteryConsumer uidBatteryConsumer = (UidBatteryConsumer) uidBatteryConsumers.get(i2);
            if (uidBatteryConsumer.getUid() == i) {
                return uidBatteryConsumer;
            }
        }
        return null;
    }

    private class BatteryUsageStatsLoaderCallbacks implements LoaderManager.LoaderCallbacks<BatteryUsageStats> {
        public void onLoaderReset(Loader<BatteryUsageStats> loader) {
        }

        private BatteryUsageStatsLoaderCallbacks() {
        }

        public Loader<BatteryUsageStats> onCreateLoader(int i, Bundle bundle) {
            return new BatteryUsageStatsLoader(AppBatteryPreferenceController.this.mContext, false);
        }

        public void onLoadFinished(Loader<BatteryUsageStats> loader, BatteryUsageStats batteryUsageStats) {
            AppBatteryPreferenceController appBatteryPreferenceController = AppBatteryPreferenceController.this;
            appBatteryPreferenceController.mBatteryUsageStats = batteryUsageStats;
            appBatteryPreferenceController.onLoadFinished();
        }
    }
}
