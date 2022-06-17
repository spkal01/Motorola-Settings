package com.android.settings.dashboard;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.dashboard.DynamicLifecycleDataObserver;
import com.android.settingslib.drawer.DynamicLifecycle;
import com.android.settingslib.drawer.Tile;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DynamicDashboardTileController implements DynamicLifecycleDataObserver.Callback {
    private static final String TAG = "DynamicDashboardTileController";
    Context mContext;
    private final List<DynamicDataObserver> mDataObservers;
    DashboardFeatureProvider mFeatureProvider;
    FragmentActivity mFragActivity;
    DynamicLifecycle mLifecycle;
    Preference mPreference;
    private final List<DynamicDataObserver> mRegisteredObservers = new ArrayList();
    Tile mTile;

    DynamicDashboardTileController(FragmentActivity fragmentActivity, Preference preference, Tile tile, DashboardFeatureProvider dashboardFeatureProvider) {
        ArrayList arrayList = new ArrayList();
        this.mDataObservers = arrayList;
        this.mFragActivity = fragmentActivity;
        Context applicationContext = fragmentActivity.getApplicationContext();
        this.mContext = applicationContext;
        this.mPreference = preference;
        this.mTile = tile;
        this.mFeatureProvider = dashboardFeatureProvider;
        DynamicLifecycleDataObserver bindLifecycle = DynamicLifecycleDataObserver.bindLifecycle(applicationContext, tile, this);
        if (!(bindLifecycle == null || bindLifecycle.getUri() == null)) {
            arrayList.add(bindLifecycle);
        }
        this.mLifecycle = bindLifecycle;
    }

    public static boolean isAvailable(Tile tile, PackageManager packageManager, ContentResolver contentResolver) {
        if (tile.getMetaData() == null) {
            return true;
        }
        Bundle metaData = tile.getMetaData();
        if (metaData.containsKey("com.android.settings.required_system_features")) {
            for (String hasSystemFeature : metaData.getString("com.android.settings.required_system_features").split(",")) {
                if (!packageManager.hasSystemFeature(hasSystemFeature)) {
                    return false;
                }
            }
        }
        if (metaData.containsKey("com.android.settings.for_user") && "system_user_only".equals(metaData.getString("com.android.settings.for_user")) && UserHandle.myUserId() != 0) {
            return false;
        }
        if (metaData.containsKey("com.android.settings.required_boolean_system_property") && !SystemProperties.getBoolean(metaData.getString("com.android.settings.required_boolean_system_property"), false)) {
            return false;
        }
        if (!metaData.containsKey("com.android.settings.channel_id_allowlist") && !metaData.containsKey("com.android.settings.channel_id_blocklist")) {
            return true;
        }
        String string = MotorolaSettings.Global.getString(contentResolver, "channel_id");
        if (TextUtils.isEmpty(string) || "unknown".equals(string)) {
            return true;
        }
        List asList = Arrays.asList(metaData.getString("com.android.settings.channel_id_allowlist", "").split(","));
        List asList2 = Arrays.asList(metaData.getString("com.android.settings.channel_id_blocklist", "").split(","));
        if (!asList.contains(string) || asList2.contains(string)) {
            return false;
        }
        return true;
    }

    public void onBind(String str, Bundle bundle, int i, ContentResolver contentResolver) {
        List<DynamicDataObserver> bindPreferenceToTileAndGetObservers = this.mFeatureProvider.bindPreferenceToTileAndGetObservers(this.mFragActivity, bundle.getBoolean("shouldForceRoundedIcon"), bundle.getInt(":settings:source_metrics"), this.mPreference, this.mTile, str, i);
        if (bindPreferenceToTileAndGetObservers != null && !bindPreferenceToTileAndGetObservers.isEmpty()) {
            this.mDataObservers.addAll(bindPreferenceToTileAndGetObservers);
        }
        onStart(contentResolver, bundle);
    }

    public void onUnbind(String str, ContentResolver contentResolver) {
        Preference preference = this.mPreference;
        if (preference != null && TextUtils.equals(preference.getKey(), str)) {
            onStop(contentResolver);
            this.mPreference = null;
        }
    }

    public void updateState(String str, Bundle bundle, int i) {
        Preference preference = this.mPreference;
        if (preference != null && TextUtils.equals(preference.getKey(), str)) {
            this.mFeatureProvider.bindPreferenceToTileAndGetObservers(this.mFragActivity, bundle.getBoolean("shouldForceRoundedIcon"), bundle.getInt(":settings:source_metrics"), this.mPreference, this.mTile, str, i);
            this.mRegisteredObservers.forEach(DynamicDashboardTileController$$ExternalSyntheticLambda2.INSTANCE);
        }
    }

    public void onStart(ContentResolver contentResolver, Bundle bundle) {
        if (this.mPreference != null) {
            if (this.mRegisteredObservers.isEmpty() && !this.mDataObservers.isEmpty()) {
                registerDynamicDataObservers(this.mDataObservers, contentResolver);
            }
            DynamicLifecycle dynamicLifecycle = this.mLifecycle;
            if (dynamicLifecycle != null) {
                dynamicLifecycle.onStart(bundle);
            }
        }
    }

    public void onStop(ContentResolver contentResolver) {
        if (this.mPreference != null) {
            unregisterDynamicDataObservers(this.mDataObservers, contentResolver);
            DynamicLifecycle dynamicLifecycle = this.mLifecycle;
            if (dynamicLifecycle != null) {
                dynamicLifecycle.onStop();
            }
        }
    }

    private void registerDynamicDataObservers(List<DynamicDataObserver> list, ContentResolver contentResolver) {
        if (list != null && !list.isEmpty()) {
            list.forEach(new DynamicDashboardTileController$$ExternalSyntheticLambda0(this, contentResolver));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: registerDynamicDataObserver */
    public void lambda$registerDynamicDataObservers$1(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        String str = TAG;
        Log.d(str, "register observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        if (!this.mRegisteredObservers.contains(dynamicDataObserver)) {
            contentResolver.registerContentObserver(dynamicDataObserver.getUri(), false, dynamicDataObserver);
            this.mRegisteredObservers.add(dynamicDataObserver);
        }
    }

    private void unregisterDynamicDataObservers(List<DynamicDataObserver> list, ContentResolver contentResolver) {
        if (list != null && !list.isEmpty()) {
            list.forEach(new DynamicDashboardTileController$$ExternalSyntheticLambda1(this, contentResolver));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterDynamicDataObservers$2(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        String str = TAG;
        Log.d(str, "unregister observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        if (this.mRegisteredObservers.contains(dynamicDataObserver)) {
            this.mRegisteredObservers.remove(dynamicDataObserver);
            contentResolver.unregisterContentObserver(dynamicDataObserver);
        }
    }

    public void onAvailabilityStatusChanged(int i) {
        boolean z = true;
        boolean z2 = (i == 2 || i == 4 || i == 3) ? false : true;
        if (i == 5) {
            z = false;
        }
        this.mPreference.setVisible(z2);
        this.mPreference.setEnabled(z);
    }
}
