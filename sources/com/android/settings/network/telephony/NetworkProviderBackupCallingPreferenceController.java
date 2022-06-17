package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.List;

public class NetworkProviderBackupCallingPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final String KEY_PREFERENCE_CATEGORY = "provider_model_backup_calling_category";
    private static final String TAG = "NetProvBackupCallingCtrl";
    private NetworkProviderBackupCallingGroup mNetworkProviderBackupCallingGroup;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;
    private List<SubscriptionInfo> mSubscriptionList;

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

    public NetworkProviderBackupCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public NetworkProviderBackupCallingGroup createBackupCallingControllerForSub(Lifecycle lifecycle, List<SubscriptionInfo> list) {
        return new NetworkProviderBackupCallingGroup(this.mContext, lifecycle, list, KEY_PREFERENCE_CATEGORY);
    }

    public void init(Lifecycle lifecycle) {
        List<SubscriptionInfo> activeSubscriptionList = getActiveSubscriptionList();
        this.mSubscriptionList = activeSubscriptionList;
        this.mNetworkProviderBackupCallingGroup = createBackupCallingControllerForSub(lifecycle, activeSubscriptionList);
    }

    private List<SubscriptionInfo> getActiveSubscriptionList() {
        return SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
    }

    public int getAvailabilityStatus() {
        List<SubscriptionInfo> list;
        if (this.mNetworkProviderBackupCallingGroup == null || (list = this.mSubscriptionList) == null || list.size() < 2) {
            return 2;
        }
        return 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY);
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setVisible(isAvailable());
        this.mNetworkProviderBackupCallingGroup.displayPreference(preferenceScreen);
    }
}
