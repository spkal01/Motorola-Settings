package com.android.settings.applications;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.apphibernation.AppHibernationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class HibernatedAppsPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final long DEFAULT_UNUSED_THRESHOLD_MS = TimeUnit.DAYS.toMillis(90);
    private static final String PROPERTY_HIBERNATION_UNUSED_THRESHOLD_MILLIS = "auto_revoke_unused_threshold_millis2";
    private static final String TAG = "HibernatedAppsPrefController";
    private final Executor mBackgroundExecutor;
    private boolean mLoadedUnusedCount;
    private boolean mLoadingUnusedApps;
    private final Executor mMainExecutor;
    private PreferenceScreen mScreen;
    private int mUnusedCount;

    private interface UnusedCountLoadedCallback {
        void onUnusedCountLoaded(int i);
    }

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

    public HibernatedAppsPreferenceController(Context context, String str) {
        this(context, str, Executors.newSingleThreadExecutor(), context.getMainExecutor());
    }

    HibernatedAppsPreferenceController(Context context, String str, Executor executor, Executor executor2) {
        super(context, str);
        this.mUnusedCount = 0;
        this.mBackgroundExecutor = executor;
        this.mMainExecutor = executor2;
    }

    public int getAvailabilityStatus() {
        return isHibernationEnabled() ? 0 : 2;
    }

    public CharSequence getSummary() {
        if (!this.mLoadedUnusedCount) {
            return this.mContext.getResources().getString(C1992R$string.summary_placeholder);
        }
        Resources resources = this.mContext.getResources();
        int i = C1990R$plurals.unused_apps_summary;
        int i2 = this.mUnusedCount;
        return resources.getQuantityString(i, i2, new Object[]{Integer.valueOf(i2)});
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updatePreference();
    }

    private void updatePreference() {
        if (this.mScreen != null && !this.mLoadingUnusedApps) {
            loadUnusedCount(new HibernatedAppsPreferenceController$$ExternalSyntheticLambda0(this));
            this.mLoadingUnusedApps = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreference$1(int i) {
        this.mUnusedCount = i;
        this.mLoadingUnusedApps = false;
        this.mLoadedUnusedCount = true;
        this.mMainExecutor.execute(new HibernatedAppsPreferenceController$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreference$0() {
        refreshSummary(this.mScreen.findPreference(this.mPreferenceKey));
    }

    private void loadUnusedCount(UnusedCountLoadedCallback unusedCountLoadedCallback) {
        this.mBackgroundExecutor.execute(new HibernatedAppsPreferenceController$$ExternalSyntheticLambda2(this, unusedCountLoadedCallback));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadUnusedCount$2(UnusedCountLoadedCallback unusedCountLoadedCallback) {
        unusedCountLoadedCallback.onUnusedCountLoaded(getUnusedCount());
    }

    private int getUnusedCount() {
        List list;
        String[] strArr;
        PackageManager packageManager = this.mContext.getPackageManager();
        List hibernatingPackagesForUser = ((AppHibernationManager) this.mContext.getSystemService(AppHibernationManager.class)).getHibernatingPackagesForUser();
        int size = hibernatingPackagesForUser.size();
        long currentTimeMillis = System.currentTimeMillis();
        long j = DeviceConfig.getLong("permissions", PROPERTY_HIBERNATION_UNUSED_THRESHOLD_MILLIS, DEFAULT_UNUSED_THRESHOLD_MS);
        List<UsageStats> queryUsageStats = ((UsageStatsManager) this.mContext.getSystemService(UsageStatsManager.class)).queryUsageStats(2, currentTimeMillis - j, currentTimeMillis);
        ArrayMap arrayMap = new ArrayMap();
        for (UsageStats next : queryUsageStats) {
            arrayMap.put(next.mPackageName, next);
        }
        int i = 0;
        for (PackageInfo next2 : packageManager.getInstalledPackages(4608)) {
            String str = next2.packageName;
            UsageStats usageStats = (UsageStats) arrayMap.get(str);
            boolean z = usageStats != null && (currentTimeMillis - usageStats.getLastTimeAnyComponentUsed() < j || currentTimeMillis - usageStats.getLastTimeVisible() < j);
            if (!hibernatingPackagesForUser.contains(str) && (strArr = next2.requestedPermissions) != null && !z) {
                int length = strArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    list = hibernatingPackagesForUser;
                    if ((packageManager.getPermissionFlags(strArr[i2], str, this.mContext.getUser()) & 131072) != 0) {
                        i++;
                        break;
                    }
                    i2++;
                    hibernatingPackagesForUser = list;
                }
                hibernatingPackagesForUser = list;
            }
            list = hibernatingPackagesForUser;
            hibernatingPackagesForUser = list;
        }
        return size + i;
    }

    private static boolean isHibernationEnabled() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_enabled", true);
    }
}
