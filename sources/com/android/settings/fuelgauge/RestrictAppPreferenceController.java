package com.android.settings.fuelgauge;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.util.Pair;
import androidx.preference.Preference;
import com.android.settings.C1990R$plurals;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.BatteryTipUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public class RestrictAppPreferenceController extends BasePreferenceController {
    static final String KEY_RESTRICT_APP = "restricted_app";
    List<AppInfo> mAppInfos;
    private AppOpsManager mAppOpsManager;
    private InstrumentedPreferenceFragment mPreferenceFragment;
    private UserManager mUserManager;

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

    public RestrictAppPreferenceController(Context context) {
        super(context, KEY_RESTRICT_APP);
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        this.mUserManager = userManager;
        this.mAppInfos = BatteryTipUtils.getRestrictedAppsList(this.mAppOpsManager, userManager);
    }

    public RestrictAppPreferenceController(InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        this(instrumentedPreferenceFragment.getContext());
        this.mPreferenceFragment = instrumentedPreferenceFragment;
    }

    public int getAvailabilityStatus() {
        return this.mAppInfos.size() > 0 ? 0 : 2;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        List<AppInfo> restrictedAppsList = BatteryTipUtils.getRestrictedAppsList(this.mAppOpsManager, this.mUserManager);
        this.mAppInfos = restrictedAppsList;
        int size = restrictedAppsList.size();
        preference.setVisible(size > 0);
        preference.setSummary((CharSequence) this.mContext.getResources().getQuantityString(C1990R$plurals.restricted_app_summary, size, new Object[]{Integer.valueOf(size)}));
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!getPreferenceKey().equals(preference.getKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        RestrictedAppDetails.startRestrictedAppDetails(this.mPreferenceFragment, this.mAppInfos);
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1890, (Pair<Integer, Object>[]) new Pair[0]);
        return true;
    }
}
