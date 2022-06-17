package com.android.settings.datausage;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.datausage.AppStateDataUsageBridge;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;

public class DataSaverSummary extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, DataSaverBackend.Listener, AppStateBaseBridge.Callback, ApplicationsState.Callbacks {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.data_saver) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return DataUsageUtils.hasMobileData(context) && DataUsageUtils.getDefaultSubscriptionId(context) != -1;
        }
    };
    private ApplicationsState mApplicationsState;
    private DataSaverBackend mDataSaverBackend;
    private AppStateDataUsageBridge mDataUsageBridge;
    private ApplicationsState.Session mSession;
    private SettingsMainSwitchBar mSwitchBar;
    private boolean mSwitching;
    private Preference mUnrestrictedAccess;

    public int getMetricsCategory() {
        return 348;
    }

    public void onAllSizesComputed() {
    }

    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    public void onDenylistStatusChanged(int i, boolean z) {
    }

    public void onLauncherInfoChanged() {
    }

    public void onLoadEntriesCompleted() {
    }

    public void onPackageIconChanged() {
    }

    public void onPackageListChanged() {
    }

    public void onPackageSizeChanged(String str) {
    }

    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    public void onRunningStateChanged(boolean z) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C1994R$xml.data_saver);
        this.mUnrestrictedAccess = findPreference("unrestricted_access");
        this.mApplicationsState = ApplicationsState.getInstance((Application) getContext().getApplicationContext());
        this.mDataSaverBackend = new DataSaverBackend(getContext());
        this.mDataUsageBridge = new AppStateDataUsageBridge(this.mApplicationsState, this, this.mDataSaverBackend);
        this.mSession = this.mApplicationsState.newSession(this, getSettingsLifecycle());
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsMainSwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(C1992R$string.data_saver_switch_title));
        this.mSwitchBar.show();
        this.mSwitchBar.addOnSwitchChangeListener(this);
    }

    public void onResume() {
        super.onResume();
        this.mDataSaverBackend.refreshAllowlist();
        this.mDataSaverBackend.refreshDenylist();
        this.mDataSaverBackend.addListener(this);
        this.mDataUsageBridge.resume();
    }

    public void onPause() {
        super.onPause();
        this.mDataSaverBackend.remListener(this);
        this.mDataUsageBridge.pause();
    }

    public void onSwitchChanged(Switch switchR, boolean z) {
        synchronized (this) {
            if (!this.mSwitching) {
                this.mSwitching = true;
                this.mDataSaverBackend.setDataSaverEnabled(z);
            }
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_url_data_saver;
    }

    public void onDataSaverChanged(boolean z) {
        synchronized (this) {
            this.mSwitchBar.setChecked(z);
            this.mSwitching = false;
        }
    }

    public void onExtraInfoUpdated() {
        Object obj;
        if (isAdded()) {
            ArrayList<ApplicationsState.AppEntry> allApps = this.mSession.getAllApps();
            int size = allApps.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                ApplicationsState.AppEntry appEntry = allApps.get(i2);
                if (ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER.filterApp(appEntry) && (obj = appEntry.extraInfo) != null && ((AppStateDataUsageBridge.DataUsageState) obj).isDataSaverAllowlisted) {
                    i++;
                }
            }
            this.mUnrestrictedAccess.setSummary((CharSequence) getResources().getQuantityString(C1990R$plurals.data_saver_unrestricted_summary, i, new Object[]{Integer.valueOf(i)}));
        }
    }
}
