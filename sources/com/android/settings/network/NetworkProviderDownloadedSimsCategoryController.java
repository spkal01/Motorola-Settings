package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;

public class NetworkProviderDownloadedSimsCategoryController extends PreferenceCategoryController implements LifecycleObserver {
    private static final String KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM = "provider_model_downloaded_sim_category";
    private NetworkProviderDownloadedSimListController mNetworkProviderDownloadedSimListController;

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

    public NetworkProviderDownloadedSimsCategoryController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle) {
        this.mNetworkProviderDownloadedSimListController = createDownloadedSimListController(lifecycle);
    }

    /* access modifiers changed from: protected */
    public NetworkProviderDownloadedSimListController createDownloadedSimListController(Lifecycle lifecycle) {
        return new NetworkProviderDownloadedSimListController(this.mContext, lifecycle);
    }

    public int getAvailabilityStatus() {
        NetworkProviderDownloadedSimListController networkProviderDownloadedSimListController = this.mNetworkProviderDownloadedSimListController;
        return (networkProviderDownloadedSimListController == null || !networkProviderDownloadedSimListController.isAvailable()) ? 2 : 0;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM)).setVisible(isAvailable());
        this.mNetworkProviderDownloadedSimListController.displayPreference(preferenceScreen);
    }
}
