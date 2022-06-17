package com.motorola.settings.display.fullscreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.CompoundButton;
import com.android.settings.C1985R$id;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.motorola.settings.widget.IManageApplicationsMenuHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import motorola.core_services.activity.MotoActivityManager;

public class FullscreenAppsBridge extends AppStateBaseBridge implements IManageApplicationsMenuHandler {
    /* access modifiers changed from: private */
    public static Set<String> CONFIGURABLE_PACKAGES = new HashSet();
    public static final ApplicationsState.AppFilter FILTER_APPS_FULL_SCREEN_CONFIGURABLE = new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if (appEntry.isHomeApp) {
                return false;
            }
            return FullscreenAppsBridge.CONFIGURABLE_PACKAGES.contains(appEntry.info.packageName);
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APPS_FULL_SCREEN_DISABLED = new AbstractImmersiveModeFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return !isForcedImmersiveFullScreen(appEntry);
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APPS_FULL_SCREEN_ENABLED = new AbstractImmersiveModeFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return isForcedImmersiveFullScreen(appEntry);
        }
    };
    private final Context mContext;
    private final MotoActivityManager mMotoActivityManager;

    public boolean isSwitchEnabled(ApplicationsState.AppEntry appEntry) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
    }

    public FullscreenAppsBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mContext = context;
        this.mMotoActivityManager = MotoActivityManager.getInstance(context.getApplicationContext());
    }

    /* access modifiers changed from: protected */
    public void loadAllExtraInfo() {
        buildConfigurablePackageList();
        buildUsageStatsComparator(this.mContext);
    }

    public CompoundButton.OnCheckedChangeListener getSwitchOnCheckedListener(ApplicationsState.AppEntry appEntry) {
        if (appEntry == null) {
            return null;
        }
        return new FullscreenAppsBridge$$ExternalSyntheticLambda0(this, appEntry);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getSwitchOnCheckedListener$0(ApplicationsState.AppEntry appEntry, CompoundButton compoundButton, boolean z) {
        String str = appEntry.info.packageName;
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        if (z) {
            this.mMotoActivityManager.enableForcedImmersiveFullScreen(arrayList);
        } else {
            this.mMotoActivityManager.disableForcedImmersiveFullScreen(arrayList);
        }
    }

    public boolean onOptionsItemSelected(int i) {
        int i2 = C1985R$id.turn_on;
        boolean z = false;
        if (i != i2 && i != C1985R$id.turn_off) {
            return false;
        }
        if (i == i2) {
            z = true;
        }
        setImmersiveModePackages(z);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(C1985R$id.turn_on).setVisible(true);
        menu.findItem(C1985R$id.turn_off).setVisible(true);
        menu.findItem(C1985R$id.show_system).setVisible(false);
        menu.findItem(C1985R$id.hide_system).setVisible(false);
        return true;
    }

    public boolean isSwitchChecked(ApplicationsState.AppEntry appEntry) {
        ApplicationInfo applicationInfo;
        if (appEntry == null || (applicationInfo = appEntry.info) == null || TextUtils.isEmpty(applicationInfo.packageName)) {
            return false;
        }
        return this.mMotoActivityManager.isForcedImmersiveFullScreen(0, appEntry.info.packageName);
    }

    private void setImmersiveModePackages(boolean z) {
        ArrayList arrayList = new ArrayList(CONFIGURABLE_PACKAGES);
        if (z) {
            this.mMotoActivityManager.enableForcedImmersiveFullScreen(arrayList);
        } else {
            this.mMotoActivityManager.disableForcedImmersiveFullScreen(arrayList);
        }
    }

    private void buildConfigurablePackageList() {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentActivities(intent, 128)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null) {
                CONFIGURABLE_PACKAGES.add(activityInfo.packageName);
            }
        }
        List forceImmersiveFullScreenBlackList = this.mMotoActivityManager.getForceImmersiveFullScreenBlackList();
        if (forceImmersiveFullScreenBlackList != null) {
            CONFIGURABLE_PACKAGES.removeAll(forceImmersiveFullScreenBlackList);
        }
    }

    private static abstract class AbstractImmersiveModeFilter implements ApplicationsState.AppFilter {
        protected MotoActivityManager mMotoActivityManager;

        private AbstractImmersiveModeFilter() {
        }

        public void init(Context context) {
            this.mMotoActivityManager = MotoActivityManager.getInstance(context);
        }

        /* access modifiers changed from: protected */
        public final boolean isForcedImmersiveFullScreen(ApplicationsState.AppEntry appEntry) {
            ApplicationInfo applicationInfo;
            if (appEntry == null || (applicationInfo = appEntry.info) == null || TextUtils.isEmpty(applicationInfo.packageName)) {
                return false;
            }
            return this.mMotoActivityManager.isForcedImmersiveFullScreen(0, appEntry.info.packageName);
        }
    }
}
