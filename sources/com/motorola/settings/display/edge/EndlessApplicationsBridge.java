package com.motorola.settings.display.edge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C1985R$id;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.motorola.settings.widget.IManageApplicationsMenuHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import motorola.core_services.misc.MotoExtendManager;
import motorola.core_services.misc.WaterfallManager;

public class EndlessApplicationsBridge extends AppStateBaseBridge implements IManageApplicationsMenuHandler {
    /* access modifiers changed from: private */
    public static final Set<String> CONFIGURABLE_APPS = new HashSet();
    public static final ApplicationsState.AppFilter FILTER_APP_ENDLESS_CONFIGURABLE = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_PERSONAL, new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return EndlessApplicationsBridge.CONFIGURABLE_APPS.contains(appEntry.info.packageName);
        }
    });
    public static final ApplicationsState.AppFilter FILTER_APP_ENDLESS_DISABLED = new EndlessAbstractAppFilter() {
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return !this.waterfallApps.contains(appEntry.info.packageName);
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_ENDLESS_ENABLED = new EndlessAbstractAppFilter() {
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return this.waterfallApps.contains(appEntry.info.packageName);
        }
    };
    private static final String TAG = "EndlessApplicationsBridge";
    private final Context mContext;
    private final MotoExtendManager mMotoExtendManager;

    /* access modifiers changed from: protected */
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
    }

    public EndlessApplicationsBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mContext = context;
        this.mMotoExtendManager = MotoExtendManager.getInstance(context);
    }

    /* access modifiers changed from: protected */
    public void loadAllExtraInfo() {
        Set<String> set = CONFIGURABLE_APPS;
        set.clear();
        set.addAll(WaterfallManager.getConfigurablePackages(this.mContext));
        buildUsageStatsComparator(this.mContext);
    }

    public boolean isSwitchEnabled(ApplicationsState.AppEntry appEntry) {
        ApplicationInfo applicationInfo;
        if (appEntry == null || (applicationInfo = appEntry.info) == null) {
            return false;
        }
        return WaterfallManager.canChangeWaterfallMode(this.mContext, applicationInfo.packageName);
    }

    public boolean isSwitchChecked(ApplicationsState.AppEntry appEntry) {
        return isWaterfallEnabled(appEntry);
    }

    public CompoundButton.OnCheckedChangeListener getSwitchOnCheckedListener(ApplicationsState.AppEntry appEntry) {
        if (appEntry == null) {
            return null;
        }
        return new EndlessApplicationsBridge$$ExternalSyntheticLambda0(this, appEntry);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getSwitchOnCheckedListener$0(ApplicationsState.AppEntry appEntry, CompoundButton compoundButton, boolean z) {
        setWaterfallEnabled(appEntry, z);
    }

    /* JADX WARNING: type inference failed for: r4v3, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onOptionsItemSelected(int r4) {
        /*
            r3 = this;
            int r0 = com.android.settings.C1985R$id.turn_on
            r1 = 0
            if (r4 == r0) goto L_0x0009
            int r2 = com.android.settings.C1985R$id.turn_off
            if (r4 != r2) goto L_0x0027
        L_0x0009:
            java.lang.String[] r2 = new java.lang.String[r1]
            if (r4 != r0) goto L_0x0016
            java.util.Set<java.lang.String> r4 = CONFIGURABLE_APPS
            java.lang.Object[] r4 = r4.toArray(r2)
            r2 = r4
            java.lang.String[] r2 = (java.lang.String[]) r2
        L_0x0016:
            motorola.core_services.misc.MotoExtendManager r3 = r3.mMotoExtendManager     // Catch:{ Exception -> 0x001d }
            boolean r3 = r3.setWaterfallExpandedPackages(r2)     // Catch:{ Exception -> 0x001d }
            return r3
        L_0x001d:
            r3 = move-exception
            java.lang.String r4 = TAG
            java.lang.String r0 = r3.getMessage()
            android.util.Log.e(r4, r0, r3)
        L_0x0027:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.display.edge.EndlessApplicationsBridge.onOptionsItemSelected(int):boolean");
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(C1985R$id.turn_on).setVisible(true);
        menu.findItem(C1985R$id.turn_off).setVisible(true);
        menu.findItem(C1985R$id.show_system).setVisible(false);
        menu.findItem(C1985R$id.hide_system).setVisible(false);
        return true;
    }

    private boolean setWaterfallEnabled(ApplicationsState.AppEntry appEntry, boolean z) {
        ApplicationInfo applicationInfo;
        if (!(appEntry == null || (applicationInfo = appEntry.info) == null || TextUtils.isEmpty(applicationInfo.packageName))) {
            String str = appEntry.info.packageName;
            if (!z) {
                return this.mMotoExtendManager.removeWaterfallExpandedPackage(str);
            }
            try {
                return this.mMotoExtendManager.addWaterfallExpandedPackage(str);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return false;
    }

    private boolean isWaterfallEnabled(ApplicationsState.AppEntry appEntry) {
        ApplicationInfo applicationInfo;
        if (!(appEntry == null || (applicationInfo = appEntry.info) == null || TextUtils.isEmpty(applicationInfo.packageName))) {
            try {
                return !WaterfallManager.isWaterfallRestricted(this.mContext, appEntry.info.packageName);
            } catch (IllegalArgumentException unused) {
            }
        }
        return false;
    }

    static abstract class EndlessAbstractAppFilter implements ApplicationsState.AppFilter {
        final Set<String> waterfallApps = new HashSet();

        public void init() {
        }

        EndlessAbstractAppFilter() {
        }

        public void init(Context context) {
            String[] waterfallExpandedPackages = MotoExtendManager.getInstance(context).getWaterfallExpandedPackages();
            this.waterfallApps.clear();
            if (!ArrayUtils.isEmpty(waterfallExpandedPackages)) {
                this.waterfallApps.addAll(Arrays.asList(waterfallExpandedPackages));
            }
        }
    }
}
