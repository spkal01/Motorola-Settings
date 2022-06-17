package com.android.settings.applications.manageapplications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.IUsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceFrameLayout;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.compat.IPlatformCompat;
import com.android.settings.C1980R$bool;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1988R$menu;
import com.android.settings.C1992R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.applications.AppStateMediaManagementAppsBridge;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.applications.AppStorageSettings;
import com.android.settings.applications.UsageAccessDetails;
import com.android.settings.applications.appinfo.AlarmsAndRemindersDetails;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.applications.appinfo.DrawOverlayDetails;
import com.android.settings.applications.appinfo.ExternalSourcesDetails;
import com.android.settings.applications.appinfo.ManageExternalStorageDetails;
import com.android.settings.applications.appinfo.MediaManagementAppsDetails;
import com.android.settings.applications.appinfo.WriteSettingsDetails;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.HighPowerDetail;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppNotificationSettings;
import com.android.settings.widget.LoadingViewController;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settings.wifi.ChangeWifiStateDetails;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.settingsspinner.SettingsSpinnerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.motorola.settings.display.edge.EndlessApplicationsBridge;
import com.motorola.settings.display.edge.SettingsBaseEndlessLayoutMixin;
import com.motorola.settings.display.fullscreen.FullscreenAppsBridge;
import com.motorola.settings.widget.HighlightablePreferenceAdapter;
import com.motorola.settings.widget.IManageApplicationsMenuHandler;
import com.motorola.settings.widget.ManageApplicationsHeaderView;
import com.motorola.settingslib.InstalledAppUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class ManageApplications extends InstrumentedFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    static final boolean DEBUG = Build.IS_DEBUGGABLE;
    static final String EXTRA_EXPAND_SEARCH_VIEW = "expand_search_view";
    public static final ApplicationsState.AppFilter FILTER_HIDE_AMX_UNLOCK_APP = new ApplicationsState.AppFilter() {
        private final String[] AMX_UNLOCK_APP_PKGNAMES = {"co.sitic.pp", "cv.sitic.tg"};

        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if ((appEntry.info.flags & 1) != 0) {
                for (String equals : this.AMX_UNLOCK_APP_PKGNAMES) {
                    if (equals.equals(appEntry.info.packageName)) {
                        return false;
                    }
                }
            }
            return true;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_HIDE_APP = new ApplicationsState.AppFilter() {
        private InstalledAppUtils mInstalledAppUtils;

        public void init() {
        }

        public void init(Context context) {
            this.mInstalledAppUtils = InstalledAppUtils.get(context);
        }

        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            InstalledAppUtils installedAppUtils;
            ApplicationInfo applicationInfo = appEntry.info;
            if ((applicationInfo.flags & 1) == 0 || (installedAppUtils = this.mInstalledAppUtils) == null || !installedAppUtils.isHiddenPackage(applicationInfo.packageName)) {
                return true;
            }
            return false;
        }
    };
    public static final Set<Integer> LIST_TYPES_WITH_INSTANT = new ArraySet(Arrays.asList(new Integer[]{0, 3}));
    public static final Set<Integer> LIST_TYPES_WITH_PINNED_HEADER = new ArraySet(Arrays.asList(new Integer[]{14, 15}));
    private AppBarLayout mAppBarLayout;
    private ApplicationsAdapter mApplications;
    private ApplicationsState mApplicationsState;
    private View mContentScrollView;
    /* access modifiers changed from: private */
    public String mCurrentPkgName;
    private int mCurrentUid;
    /* access modifiers changed from: private */
    public View mEmptyView;
    boolean mExpandSearch;
    /* access modifiers changed from: private */
    public AppFilterItem mFilter;
    FilterSpinnerAdapter mFilterAdapter;
    /* access modifiers changed from: private */
    public Spinner mFilterSpinner;
    /* access modifiers changed from: private */
    public int mFilterType;
    CharSequence mInvalidSizeStr;
    private boolean mIsPersonalOnly;
    private boolean mIsWorkOnly;
    public int mListType;
    /* access modifiers changed from: private */
    public View mLoadingContainer;
    /* access modifiers changed from: private */
    public NotificationBackend mNotificationBackend;
    private Menu mOptionsMenu;
    RecyclerView mRecyclerView;
    private ResetAppsHelper mResetAppsHelper;
    private View mRootView;
    private View mScrollableHeader;
    /* access modifiers changed from: private */
    public SearchView mSearchView;
    /* access modifiers changed from: private */
    public boolean mShowSystem;
    int mSortOrder = C1985R$id.sort_order_alpha;
    View mSpinnerHeader;
    private int mStorageType;
    /* access modifiers changed from: private */
    public IUsageStatsManager mUsageStatsManager;
    /* access modifiers changed from: private */
    public UserManager mUserManager;
    private String mVolumeUuid;
    private int mWorkUserId;

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
        this.mApplicationsState = ApplicationsState.getInstance(activity.getApplication());
        Intent intent = activity.getIntent();
        Bundle arguments = getArguments();
        int i = C1992R$string.all_apps;
        int intExtra = intent.getIntExtra(":settings:show_fragment_title_resid", i);
        String string = arguments != null ? arguments.getString("classname") : null;
        if (string == null) {
            string = intent.getComponent().getClassName();
        }
        if (string.equals(Settings.StorageUseActivity.class.getName())) {
            if (arguments == null || !arguments.containsKey("volumeUuid")) {
                this.mListType = 0;
            } else {
                this.mVolumeUuid = arguments.getString("volumeUuid");
                this.mStorageType = arguments.getInt("storageType", 0);
                this.mListType = 3;
            }
            this.mSortOrder = C1985R$id.sort_order_size;
        } else if (string.equals(Settings.UsageAccessSettingsActivity.class.getName())) {
            this.mListType = 4;
            intExtra = C1992R$string.usage_access;
        } else if (string.equals(Settings.HighPowerApplicationsActivity.class.getName())) {
            this.mListType = 5;
            this.mShowSystem = true;
            intExtra = C1992R$string.high_power_apps;
        } else if (string.equals(Settings.OverlaySettingsActivity.class.getName())) {
            this.mListType = 6;
            intExtra = C1992R$string.system_alert_window_settings;
            reportIfRestrictedSawIntent(intent);
        } else if (string.equals(Settings.WriteSettingsActivity.class.getName())) {
            this.mListType = 7;
            intExtra = C1992R$string.write_settings;
        } else if (string.equals(Settings.ManageExternalSourcesActivity.class.getName())) {
            this.mListType = 8;
            intExtra = C1992R$string.install_other_apps;
        } else if (string.equals(Settings.GamesStorageActivity.class.getName())) {
            this.mListType = 9;
            this.mSortOrder = C1985R$id.sort_order_size;
        } else if (string.equals(Settings.ChangeWifiStateActivity.class.getName())) {
            this.mListType = 10;
            intExtra = C1992R$string.change_wifi_state_title;
        } else if (string.equals(Settings.ManageExternalStorageActivity.class.getName())) {
            this.mListType = 11;
            intExtra = C1992R$string.manage_external_storage_title;
        } else if (string.equals(Settings.MediaManagementAppsActivity.class.getName())) {
            this.mListType = 13;
            intExtra = C1992R$string.media_management_apps_title;
        } else if (string.equals(Settings.AlarmsAndRemindersActivity.class.getName())) {
            this.mListType = 12;
            intExtra = C1992R$string.alarms_and_reminders_title;
        } else if (string.equals(Settings.NotificationAppListActivity.class.getName())) {
            this.mListType = 1;
            this.mUsageStatsManager = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
            this.mNotificationBackend = new NotificationBackend();
            this.mSortOrder = C1985R$id.sort_order_recent_notification;
            intExtra = C1992R$string.app_notifications_title;
        } else {
            if (intExtra != -1) {
                i = intExtra;
            }
            this.mListType = 0;
            intExtra = i;
        }
        if (string.equals(Settings.FullScreenSettingsActivity.class.getName())) {
            intExtra = C1992R$string.full_screen_applications_title;
            this.mListType = 14;
            this.mSortOrder = C1985R$id.sort_order_apps_list_recents;
        }
        if (string.equals(Settings.EndlessApplicationsActivity.class.getName())) {
            intExtra = C1992R$string.endless_applications_title;
            this.mListType = 15;
            this.mSortOrder = C1985R$id.sort_order_apps_list_recents;
        }
        AppFilterRegistry instance = AppFilterRegistry.getInstance();
        this.mFilter = instance.get(instance.getDefaultFilterType(this.mListType));
        this.mIsPersonalOnly = arguments != null && arguments.getInt("profile") == 1;
        this.mIsWorkOnly = arguments != null && arguments.getInt("profile") == 2;
        int i2 = arguments != null ? arguments.getInt("workId") : UserHandle.myUserId();
        this.mWorkUserId = i2;
        if (this.mIsWorkOnly && i2 == UserHandle.myUserId()) {
            this.mWorkUserId = Utils.getManagedProfileId(this.mUserManager, UserHandle.myUserId());
        }
        this.mExpandSearch = activity.getIntent().getBooleanExtra(EXTRA_EXPAND_SEARCH_VIEW, false);
        if (bundle != null) {
            this.mSortOrder = bundle.getInt("sortOrder", this.mSortOrder);
            this.mShowSystem = bundle.getBoolean("showSystem", this.mShowSystem);
            this.mFilterType = bundle.getInt("filterType", 4);
            this.mExpandSearch = bundle.getBoolean(EXTRA_EXPAND_SEARCH_VIEW);
        }
        this.mInvalidSizeStr = activity.getText(C1992R$string.invalid_size_value);
        this.mResetAppsHelper = new ResetAppsHelper(activity);
        if (intExtra > 0) {
            activity.setTitle(intExtra);
        }
        if (HighlightablePreferenceAdapter.isArgKeySystemPackage(getContext(), getArguments())) {
            this.mShowSystem = true;
        }
    }

    private void reportIfRestrictedSawIntent(Intent intent) {
        try {
            Uri data = intent.getData();
            if (data == null) {
                return;
            }
            if (TextUtils.equals("package", data.getScheme())) {
                int launchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivity().getActivityToken());
                if (launchedFromUid == -1) {
                    Log.w("ManageApplications", "Error obtaining calling uid");
                    return;
                }
                IPlatformCompat asInterface = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
                if (asInterface == null) {
                    Log.w("ManageApplications", "Error obtaining IPlatformCompat service");
                } else {
                    asInterface.reportChangeByUid(135920175, launchedFromUid);
                }
            }
        } catch (RemoteException e) {
            Log.w("ManageApplications", "Error reporting SAW intent restriction", e);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        SettingsBaseEndlessLayoutMixin.applyThemeToFragment(layoutInflater);
        if (this.mListType != 6 || Utils.isSystemAlertWindowEnabled(getContext())) {
            if (LIST_TYPES_WITH_PINNED_HEADER.contains(Integer.valueOf(this.mListType))) {
                this.mRootView = layoutInflater.inflate(C1987R$layout.extended_manage_applications_apps, (ViewGroup) null);
            } else {
                this.mRootView = layoutInflater.inflate(C1987R$layout.manage_applications_apps, (ViewGroup) null);
            }
            this.mLoadingContainer = this.mRootView.findViewById(C1985R$id.loading_container);
            this.mEmptyView = this.mRootView.findViewById(16908292);
            this.mRecyclerView = (RecyclerView) this.mRootView.findViewById(C1985R$id.apps_list);
            this.mContentScrollView = this.mRootView.findViewById(C1985R$id.content_scroll);
            ApplicationsAdapter applicationsAdapter = new ApplicationsAdapter(this.mApplicationsState, this, this.mFilter, bundle);
            this.mApplications = applicationsAdapter;
            if (bundle != null) {
                boolean unused = applicationsAdapter.mHasReceivedLoadEntries = bundle.getBoolean("hasEntries", false);
                boolean unused2 = this.mApplications.mHasReceivedBridgeCallback = bundle.getBoolean("hasBridge", false);
            }
            this.mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
            this.mRecyclerView.setAdapter(this.mApplications);
            if (viewGroup instanceof PreferenceFrameLayout) {
                this.mRootView.getLayoutParams().removeBorders = true;
            }
            createHeader();
            this.mResetAppsHelper.onRestoreInstanceState(bundle);
            this.mAppBarLayout = (AppBarLayout) getActivity().findViewById(C1985R$id.app_bar);
            disableToolBarScrollableBehavior();
            return this.mRootView;
        }
        this.mRootView = layoutInflater.inflate(C1987R$layout.manage_applications_apps_unsupported, (ViewGroup) null);
        setHasOptionsMenu(false);
        return this.mRootView;
    }

    /* access modifiers changed from: package-private */
    public void createHeader() {
        FragmentActivity activity = getActivity();
        FrameLayout frameLayout = (FrameLayout) this.mRootView.findViewById(C1985R$id.pinned_header);
        FrameLayout frameLayout2 = (FrameLayout) this.mRootView.findViewById(C1985R$id.scrollable_header);
        this.mScrollableHeader = frameLayout2;
        View inflate = activity.getLayoutInflater().inflate(C1987R$layout.manage_apps_filter_spinner, frameLayout, false);
        this.mSpinnerHeader = inflate;
        this.mFilterSpinner = (Spinner) inflate.findViewById(C1985R$id.filter_spinner);
        FilterSpinnerAdapter filterSpinnerAdapter = new FilterSpinnerAdapter(this);
        this.mFilterAdapter = filterSpinnerAdapter;
        this.mFilterSpinner.setAdapter(filterSpinnerAdapter);
        this.mFilterSpinner.setOnItemSelectedListener(this);
        frameLayout.addView(this.mSpinnerHeader, 0);
        this.mFilterAdapter.enableFilter(AppFilterRegistry.getInstance().getDefaultFilterType(this.mListType));
        if (this.mListType == 0 && UserManager.get(getActivity()).getUserProfiles().size() > 1 && !this.mIsWorkOnly && !this.mIsPersonalOnly) {
            this.mFilterAdapter.enableFilter(8);
            this.mFilterAdapter.enableFilter(9);
        }
        if (this.mListType == 1) {
            this.mFilterAdapter.enableFilter(2);
            this.mFilterAdapter.enableFilter(3);
            this.mFilterAdapter.enableFilter(16);
            this.mFilterAdapter.enableFilter(4);
        }
        if (this.mListType == 5) {
            this.mFilterAdapter.enableFilter(1);
        }
        if (this.mListType == 14) {
            this.mFilterAdapter.enableFilter(21);
            this.mFilterAdapter.enableFilter(22);
            this.mFilterAdapter.enableFilter(23);
            this.mFilterAdapter.disableFilter(4);
            if (frameLayout2 != null) {
                ManageApplicationsHeaderView manageApplicationsHeaderView = new ManageApplicationsHeaderView(getContext());
                manageApplicationsHeaderView.setHeaderTitle(C1992R$string.full_screen_applications_description);
                manageApplicationsHeaderView.setHeaderBackground(C1983R$drawable.full_screen_applications_header);
                frameLayout2.addView(manageApplicationsHeaderView);
            }
        }
        if (this.mListType == 15) {
            this.mFilterAdapter.enableFilter(21);
            this.mFilterAdapter.enableFilter(24);
            this.mFilterAdapter.enableFilter(25);
            this.mFilterAdapter.disableFilter(4);
            ManageApplicationsHeaderView manageApplicationsHeaderView2 = new ManageApplicationsHeaderView(getContext());
            manageApplicationsHeaderView2.setHeaderTitle(C1992R$string.endless_applications_description);
            manageApplicationsHeaderView2.setHeaderBackground(C1983R$drawable.endless_applications_header);
            frameLayout2.addView(manageApplicationsHeaderView2);
        }
        setCompositeFilter();
    }

    static ApplicationsState.AppFilter getCompositeFilter(int i, int i2, String str) {
        ApplicationsState.VolumeFilter volumeFilter = new ApplicationsState.VolumeFilter(str);
        if (i == 3) {
            return i2 == 0 ? new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_APPS_EXCEPT_GAMES, volumeFilter) : volumeFilter;
        }
        if (i == 9) {
            return new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_GAMES, volumeFilter);
        }
        if (i == 14) {
            return new ApplicationsState.CompoundFilter(FullscreenAppsBridge.FILTER_APPS_FULL_SCREEN_CONFIGURABLE, volumeFilter);
        }
        if (i == 15) {
            return new ApplicationsState.CompoundFilter(EndlessApplicationsBridge.FILTER_APP_ENDLESS_CONFIGURABLE, volumeFilter);
        }
        return null;
    }

    public int getMetricsCategory() {
        switch (this.mListType) {
            case 0:
                return 65;
            case 1:
                return 133;
            case 3:
                return 182;
            case 4:
                return 95;
            case 5:
                return 184;
            case 6:
            case 7:
                return 221;
            case 8:
                return 808;
            case 9:
                return 838;
            case 10:
                return 338;
            case 11:
                return 1822;
            case 12:
                return 1869;
            case 13:
                return 1874;
            case 14:
                return 3003;
            default:
                return 0;
        }
    }

    public void onStart() {
        super.onStart();
        updateView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.resume(this.mSortOrder);
            this.mApplications.updateLoading();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mResetAppsHelper.onSaveInstanceState(bundle);
        bundle.putInt("sortOrder", this.mSortOrder);
        bundle.putInt("filterType", this.mFilter.getFilterType());
        bundle.putBoolean("showSystem", this.mShowSystem);
        bundle.putBoolean("hasEntries", this.mApplications.mHasReceivedLoadEntries);
        bundle.putBoolean("hasBridge", this.mApplications.mHasReceivedBridgeCallback);
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            bundle.putBoolean(EXTRA_EXPAND_SEARCH_VIEW, !searchView.isIconified());
        }
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.onSaveInstanceState(bundle);
        }
    }

    public void onStop() {
        super.onStop();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.pause();
        }
        this.mResetAppsHelper.stop();
    }

    public void onDestroyView() {
        super.onDestroyView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.release();
        }
        this.mRootView = null;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        String str;
        if (i == 1 && (str = this.mCurrentPkgName) != null) {
            int i3 = this.mListType;
            if (i3 == 1) {
                this.mApplications.mExtraInfoBridge.forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
            } else if (i3 == 5 || i3 == 6 || i3 == 7) {
                this.mApplications.mExtraInfoBridge.forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
            } else {
                this.mApplicationsState.requestSize(str, UserHandle.getUserId(this.mCurrentUid));
            }
        }
    }

    private void setCompositeFilter() {
        int i;
        ApplicationsState.CompoundFilter compositeFilter = getCompositeFilter(this.mListType, this.mStorageType, this.mVolumeUuid);
        if (compositeFilter == null) {
            compositeFilter = this.mFilter.getFilter();
        }
        if (this.mIsWorkOnly) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_WORK);
        }
        if (this.mIsPersonalOnly) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_PERSONAL);
        }
        if (!(!InstalledAppUtils.get(getContext()).hasHiddenPackages() || (i = this.mListType) == 0 || i == 4)) {
            ApplicationsState.AppFilter appFilter = FILTER_HIDE_APP;
            appFilter.init(getContext());
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, appFilter);
        }
        if (this.mListType == 0 && Build.VERSION.DEVICE_INITIAL_SDK_INT <= 30 && getResources().getBoolean(C1980R$bool.config_hide_amx_unlock_app_enabled)) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, FILTER_HIDE_AMX_UNLOCK_APP);
        }
        if (Build.IS_PRC_PRODUCT && this.mListType == 14) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, AppStateBaseBridge.FILTER_APPS_LIST_REMOVE_CLONE_APPS);
        }
        this.mApplications.setCompositeFilter(new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED));
    }

    private void startApplicationDetailsActivity() {
        switch (this.mListType) {
            case 1:
                startAppInfoFragment(AppNotificationSettings.class, C1992R$string.notifications_title);
                return;
            case 3:
                startAppInfoFragment(AppStorageSettings.class, C1992R$string.storage_settings);
                return;
            case 4:
                startAppInfoFragment(UsageAccessDetails.class, C1992R$string.usage_access);
                return;
            case 5:
                HighPowerDetail.show(this, this.mCurrentUid, this.mCurrentPkgName, 1);
                return;
            case 6:
                startAppInfoFragment(DrawOverlayDetails.class, C1992R$string.overlay_settings);
                return;
            case 7:
                startAppInfoFragment(WriteSettingsDetails.class, C1992R$string.write_system_settings);
                return;
            case 8:
                startAppInfoFragment(ExternalSourcesDetails.class, C1992R$string.install_other_apps);
                return;
            case 9:
                startAppInfoFragment(AppStorageSettings.class, C1992R$string.game_storage_settings);
                return;
            case 10:
                startAppInfoFragment(ChangeWifiStateDetails.class, C1992R$string.change_wifi_state_title);
                return;
            case 11:
                startAppInfoFragment(ManageExternalStorageDetails.class, C1992R$string.manage_external_storage_title);
                return;
            case 12:
                startAppInfoFragment(AlarmsAndRemindersDetails.class, C1992R$string.alarms_and_reminders_label);
                return;
            case 13:
                startAppInfoFragment(MediaManagementAppsDetails.class, C1992R$string.media_management_apps_title);
                return;
            default:
                startAppInfoFragment(AppInfoDashboardFragment.class, C1992R$string.application_info_label);
                return;
        }
    }

    private void startAppInfoFragment(Class<?> cls, int i) {
        AppInfoBase.startAppInfoFragment(cls, i, this.mCurrentPkgName, this.mCurrentUid, this, 1, getMetricsCategory());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            HelpUtils.prepareHelpMenuItem((Activity) activity, menu, getHelpResource(), getClass().getName());
            this.mOptionsMenu = menu;
            menuInflater.inflate(C1988R$menu.manage_apps, menu);
            MenuItem findItem = menu.findItem(C1985R$id.search_app_list_menu);
            if (findItem != null) {
                findItem.setOnActionExpandListener(this);
                SearchView searchView = (SearchView) findItem.getActionView();
                this.mSearchView = searchView;
                searchView.setQueryHint(getText(C1992R$string.search_settings));
                this.mSearchView.setOnQueryTextListener(this);
                if (this.mExpandSearch) {
                    findItem.expandActionView();
                }
            }
            updateOptionsMenu();
        }
    }

    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, false);
        View view = this.mContentScrollView;
        if (view != null) {
            ViewCompat.setNestedScrollingEnabled(view, false);
        }
        View view2 = this.mScrollableHeader;
        if (view2 == null) {
            return true;
        }
        view2.setVisibility(8);
        return true;
    }

    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, true);
        View view = this.mContentScrollView;
        if (view != null) {
            ViewCompat.setNestedScrollingEnabled(view, true);
        }
        View view2 = this.mScrollableHeader;
        if (view2 != null) {
            view2.setVisibility(0);
        }
        return true;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenu();
    }

    public void onDestroyOptionsMenu() {
        this.mOptionsMenu = null;
    }

    /* access modifiers changed from: package-private */
    public int getHelpResource() {
        switch (this.mListType) {
            case 0:
                return C1992R$string.help_uri_apps;
            case 1:
                return C1992R$string.help_uri_notifications;
            case 3:
                return C1992R$string.help_uri_apps_storage;
            case 4:
                return C1992R$string.help_url_usage_access;
            case 5:
                return C1992R$string.help_uri_apps_high_power;
            case 6:
                return C1992R$string.help_uri_apps_overlay;
            case 7:
                return C1992R$string.help_uri_apps_write_settings;
            case 8:
                return C1992R$string.help_uri_apps_manage_sources;
            case 9:
                return C1992R$string.help_uri_apps_overlay;
            case 10:
                return C1992R$string.help_uri_apps_wifi_access;
            case 11:
                return C1992R$string.help_uri_manage_external_storage;
            case 12:
                return C1992R$string.help_uri_alarms_and_reminders;
            case 13:
                return C1992R$string.help_uri_media_management_apps;
            case 14:
                return C1992R$string.help_uri_full_screen_applications;
            default:
                return C1992R$string.help_uri_endless_appplications;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOptionsMenu() {
        Menu menu = this.mOptionsMenu;
        if (menu != null) {
            menu.findItem(C1985R$id.advanced).setVisible(false);
            Menu menu2 = this.mOptionsMenu;
            int i = C1985R$id.sort_order_alpha;
            boolean z = true;
            menu2.findItem(i).setVisible(this.mListType == 3 && this.mSortOrder != i);
            Menu menu3 = this.mOptionsMenu;
            int i2 = C1985R$id.sort_order_size;
            menu3.findItem(i2).setVisible(this.mListType == 3 && this.mSortOrder != i2);
            this.mOptionsMenu.findItem(C1985R$id.show_system).setVisible(!this.mShowSystem && this.mListType != 5);
            this.mOptionsMenu.findItem(C1985R$id.hide_system).setVisible(this.mShowSystem && this.mListType != 5);
            MenuItem findItem = this.mOptionsMenu.findItem(C1985R$id.reset_app_preferences);
            if (this.mListType != 0) {
                z = false;
            }
            findItem.setVisible(z);
            this.mOptionsMenu.findItem(C1985R$id.sort_order_recent_notification).setVisible(false);
            this.mOptionsMenu.findItem(C1985R$id.sort_order_frequent_notification).setVisible(false);
            MenuItem findItem2 = this.mOptionsMenu.findItem(11);
            if (findItem2 != null) {
                findItem2.setVisible(false);
            }
            if (this.mApplications.mExtraInfoBridge instanceof IManageApplicationsMenuHandler) {
                ((IManageApplicationsMenuHandler) this.mApplications.mExtraInfoBridge).onPrepareOptionsMenu(this.mOptionsMenu);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        int itemId2 = menuItem.getItemId();
        if ((this.mApplications.mExtraInfoBridge instanceof IManageApplicationsMenuHandler) && ((IManageApplicationsMenuHandler) this.mApplications.mExtraInfoBridge).onOptionsItemSelected(itemId2)) {
            this.mApplications.rebuild();
        }
        if (itemId2 == C1985R$id.sort_order_alpha || itemId2 == C1985R$id.sort_order_size) {
            ApplicationsAdapter applicationsAdapter = this.mApplications;
            if (applicationsAdapter != null) {
                applicationsAdapter.rebuild(itemId);
            }
        } else if (itemId2 == C1985R$id.show_system || itemId2 == C1985R$id.hide_system) {
            this.mShowSystem = !this.mShowSystem;
            this.mApplications.rebuild();
        } else if (itemId2 == C1985R$id.reset_app_preferences) {
            this.mResetAppsHelper.buildResetDialog();
            return true;
        } else if (itemId2 != C1985R$id.advanced) {
            return false;
        } else {
            if (this.mListType == 1) {
                new SubSettingLauncher(getContext()).setDestination(ConfigureNotificationSettings.class.getName()).setTitleRes(C1992R$string.configure_notification_settings).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
            } else {
                startActivityForResult(new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"), 2);
            }
            return true;
        }
        updateOptionsMenu();
        return true;
    }

    public void onClick(View view) {
        if (this.mApplications != null) {
            int childAdapterPosition = this.mRecyclerView.getChildAdapterPosition(view);
            if (childAdapterPosition == -1) {
                Log.w("ManageApplications", "Cannot find position for child, skipping onClick handling");
                return;
            }
            int i = this.mListType;
            if (i == 14 || i == 15) {
                ViewGroup viewGroup = new ApplicationViewHolder(view).mWidgetContainer;
                if (viewGroup != null) {
                    viewGroup.performClick();
                }
            } else if (this.mApplications.getApplicationCount() > childAdapterPosition) {
                ApplicationInfo applicationInfo = this.mApplications.getAppEntry(childAdapterPosition).info;
                this.mCurrentPkgName = applicationInfo.packageName;
                this.mCurrentUid = applicationInfo.uid;
                startApplicationDetailsActivity();
                ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, true);
            }
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mFilter = this.mFilterAdapter.getFilter(i);
        setCompositeFilter();
        this.mApplications.setFilter(this.mFilter);
        if (DEBUG) {
            Log.d("ManageApplications", "Selecting filter " + getContext().getText(this.mFilter.getTitle()));
        }
    }

    public boolean onQueryTextChange(String str) {
        this.mApplications.filterSearch(str);
        return false;
    }

    public void updateView() {
        updateOptionsMenu();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    public void setHasDisabled(boolean z) {
        if (this.mListType == 0) {
            this.mFilterAdapter.setFilterEnabled(5, z);
            this.mFilterAdapter.setFilterEnabled(7, z);
        }
    }

    public void setHasInstant(boolean z) {
        if (LIST_TYPES_WITH_INSTANT.contains(Integer.valueOf(this.mListType))) {
            this.mFilterAdapter.setFilterEnabled(6, z);
        }
    }

    private void disableToolBarScrollableBehavior() {
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        ((CoordinatorLayout.LayoutParams) this.mAppBarLayout.getLayoutParams()).setBehavior(behavior);
    }

    static class FilterSpinnerAdapter extends SettingsSpinnerAdapter<CharSequence> {
        private final Context mContext;
        private final ArrayList<AppFilterItem> mFilterOptions = new ArrayList<>();
        private final ManageApplications mManageApplications;

        public FilterSpinnerAdapter(ManageApplications manageApplications) {
            super(manageApplications.getContext());
            this.mContext = manageApplications.getContext();
            this.mManageApplications = manageApplications;
        }

        public AppFilterItem getFilter(int i) {
            return this.mFilterOptions.get(i);
        }

        public void setFilterEnabled(int i, boolean z) {
            if (z) {
                enableFilter(i);
            } else {
                disableFilter(i);
            }
        }

        public void enableFilter(int i) {
            int indexOf;
            AppFilterItem appFilterItem = AppFilterRegistry.getInstance().get(i);
            if (!this.mFilterOptions.contains(appFilterItem)) {
                boolean z = ManageApplications.DEBUG;
                if (z) {
                    Log.d("ManageApplications", "Enabling filter " + this.mContext.getText(appFilterItem.getTitle()));
                }
                this.mFilterOptions.add(appFilterItem);
                Collections.sort(this.mFilterOptions);
                updateFilterView(this.mFilterOptions.size() > 1);
                notifyDataSetChanged();
                if (this.mFilterOptions.size() == 1) {
                    if (z) {
                        Log.d("ManageApplications", "Auto selecting filter " + appFilterItem + " " + this.mContext.getText(appFilterItem.getTitle()));
                    }
                    this.mManageApplications.mFilterSpinner.setSelection(0);
                    this.mManageApplications.onItemSelected((AdapterView<?>) null, (View) null, 0, 0);
                }
                if (this.mFilterOptions.size() > 1 && (indexOf = this.mFilterOptions.indexOf(AppFilterRegistry.getInstance().get(this.mManageApplications.mFilterType))) != -1) {
                    this.mManageApplications.mFilterSpinner.setSelection(indexOf);
                    this.mManageApplications.onItemSelected((AdapterView<?>) null, (View) null, indexOf, 0);
                }
            }
        }

        public void disableFilter(int i) {
            AppFilterItem appFilterItem = AppFilterRegistry.getInstance().get(i);
            if (this.mFilterOptions.remove(appFilterItem)) {
                boolean z = ManageApplications.DEBUG;
                if (z) {
                    Log.d("ManageApplications", "Disabling filter " + appFilterItem + " " + this.mContext.getText(appFilterItem.getTitle()));
                }
                Collections.sort(this.mFilterOptions);
                boolean z2 = true;
                if (this.mFilterOptions.size() <= 1) {
                    z2 = false;
                }
                updateFilterView(z2);
                notifyDataSetChanged();
                if (this.mManageApplications.mFilter == appFilterItem && this.mFilterOptions.size() > 0) {
                    if (z) {
                        Log.d("ManageApplications", "Auto selecting filter " + this.mFilterOptions.get(0) + this.mContext.getText(this.mFilterOptions.get(0).getTitle()));
                    }
                    this.mManageApplications.mFilterSpinner.setSelection(0);
                    this.mManageApplications.onItemSelected((AdapterView<?>) null, (View) null, 0, 0);
                }
            }
        }

        public int getCount() {
            return this.mFilterOptions.size();
        }

        public CharSequence getItem(int i) {
            return this.mContext.getText(this.mFilterOptions.get(i).getTitle());
        }

        /* access modifiers changed from: package-private */
        public void updateFilterView(boolean z) {
            if (z) {
                this.mManageApplications.mSpinnerHeader.setVisibility(0);
            } else {
                this.mManageApplications.mSpinnerHeader.setVisibility(8);
            }
            if (ManageApplications.LIST_TYPES_WITH_PINNED_HEADER.contains(Integer.valueOf(this.mManageApplications.mListType))) {
                this.mManageApplications.mRecyclerView.setPadding(0, 0, 0, 0);
            }
        }
    }

    static class ApplicationsAdapter extends HighlightablePreferenceAdapter<ApplicationViewHolder> implements ApplicationsState.Callbacks, AppStateBaseBridge.Callback {
        private AppBarLayout mAppBarLayout;
        private AppFilterItem mAppFilter;
        private PowerAllowlistBackend mBackend;
        private ApplicationsState.AppFilter mCompositeFilter;
        private final Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<ApplicationsState.AppEntry> mEntries;
        /* access modifiers changed from: private */
        public final AppStateBaseBridge mExtraInfoBridge;
        /* access modifiers changed from: private */
        public boolean mHasReceivedBridgeCallback;
        /* access modifiers changed from: private */
        public boolean mHasReceivedLoadEntries;
        private final IconDrawableFactory mIconDrawableFactory;
        private int mLastIndex = -1;
        private int mLastSortMode = -1;
        private final LoadingViewController mLoadingViewController;
        private final ManageApplications mManageApplications;
        OnScrollListener mOnScrollListener;
        /* access modifiers changed from: private */
        public ArrayList<ApplicationsState.AppEntry> mOriginalEntries;
        private RecyclerView mRecyclerView;
        private boolean mResumed;
        private SearchFilter mSearchFilter;
        private final ApplicationsState.Session mSession;
        private final ApplicationsState mState;
        private int mWhichSize = 0;

        public int getItemViewType(int i) {
            return 0;
        }

        public void onPackageIconChanged() {
        }

        public ApplicationsAdapter(ApplicationsState applicationsState, ManageApplications manageApplications, AppFilterItem appFilterItem, Bundle bundle) {
            super(manageApplications.requireContext());
            setHasStableIds(true);
            this.mState = applicationsState;
            this.mSession = applicationsState.newSession(this);
            this.mManageApplications = manageApplications;
            this.mLoadingViewController = new LoadingViewController(manageApplications.mLoadingContainer, manageApplications.mRecyclerView, manageApplications.mEmptyView);
            FragmentActivity activity = manageApplications.getActivity();
            this.mContext = activity;
            this.mAppBarLayout = (AppBarLayout) manageApplications.getActivity().findViewById(C1985R$id.app_bar);
            this.mIconDrawableFactory = IconDrawableFactory.newInstance(activity);
            this.mAppFilter = appFilterItem;
            PowerAllowlistBackend instance = PowerAllowlistBackend.getInstance(activity);
            this.mBackend = instance;
            int i = manageApplications.mListType;
            if (i == 1) {
                this.mExtraInfoBridge = new AppStateNotificationBridge(activity, applicationsState, this, manageApplications.mUsageStatsManager, manageApplications.mUserManager, manageApplications.mNotificationBackend);
            } else if (i == 4) {
                this.mExtraInfoBridge = new AppStateUsageBridge(activity, applicationsState, this);
            } else if (i == 5) {
                instance.refreshList();
                this.mExtraInfoBridge = new AppStatePowerBridge(activity, applicationsState, this);
            } else if (i == 6) {
                this.mExtraInfoBridge = new AppStateOverlayBridge(activity, applicationsState, this);
            } else if (i == 7) {
                this.mExtraInfoBridge = new AppStateWriteSettingsBridge(activity, applicationsState, this);
            } else if (i == 8) {
                this.mExtraInfoBridge = new AppStateInstallAppsBridge(activity, applicationsState, this);
            } else if (i == 10) {
                this.mExtraInfoBridge = new AppStateChangeWifiStateBridge(activity, applicationsState, this);
            } else if (i == 11) {
                this.mExtraInfoBridge = new AppStateManageExternalStorageBridge(activity, applicationsState, this);
            } else if (i == 12) {
                this.mExtraInfoBridge = new AppStateAlarmsAndRemindersBridge(activity, applicationsState, this);
            } else if (i == 13) {
                this.mExtraInfoBridge = new AppStateMediaManagementAppsBridge(activity, applicationsState, this);
            } else if (i == 14) {
                this.mExtraInfoBridge = new FullscreenAppsBridge(activity, applicationsState, this);
            } else if (i == 15) {
                this.mExtraInfoBridge = new EndlessApplicationsBridge(activity, applicationsState, this);
            } else {
                this.mExtraInfoBridge = null;
            }
            if (bundle != null) {
                this.mLastIndex = bundle.getInt("state_last_scroll_index");
            }
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.mRecyclerView = recyclerView;
            OnScrollListener onScrollListener = new OnScrollListener(this);
            this.mOnScrollListener = onScrollListener;
            this.mRecyclerView.addOnScrollListener(onScrollListener);
        }

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
            this.mOnScrollListener = null;
            this.mRecyclerView = null;
        }

        public void setCompositeFilter(ApplicationsState.AppFilter appFilter) {
            this.mCompositeFilter = appFilter;
            rebuild();
        }

        public void setFilter(AppFilterItem appFilterItem) {
            this.mAppFilter = appFilterItem;
            int i = this.mManageApplications.mListType;
            if (i == 1) {
                if (3 == appFilterItem.getFilterType()) {
                    rebuild(C1985R$id.sort_order_frequent_notification);
                } else if (2 == appFilterItem.getFilterType()) {
                    rebuild(C1985R$id.sort_order_recent_notification);
                } else if (16 == appFilterItem.getFilterType()) {
                    rebuild(C1985R$id.sort_order_alpha);
                } else {
                    this.mLastSortMode = -1;
                    rebuild(C1985R$id.sort_order_alpha);
                }
            } else if (i == 14 || i == 15) {
                int filterType = appFilterItem.getFilterType();
                if (filterType != 16) {
                    switch (filterType) {
                        case 20:
                            rebuild(C1985R$id.sort_order_apps_list_recents);
                            return;
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                            break;
                        default:
                            rebuild();
                            return;
                    }
                }
                ManageApplications manageApplications = this.mManageApplications;
                int i2 = C1985R$id.sort_order_alpha;
                manageApplications.mSortOrder = i2;
                this.mLastSortMode = i2;
                rebuild();
            } else {
                rebuild();
            }
        }

        public void resume(int i) {
            if (ManageApplications.DEBUG) {
                Log.i("ManageApplications", "Resume!  mResumed=" + this.mResumed);
            }
            if (!this.mResumed) {
                this.mResumed = true;
                this.mSession.onResume();
                this.mLastSortMode = i;
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.resume();
                }
                rebuild();
                return;
            }
            rebuild(i);
        }

        public void pause() {
            if (this.mResumed) {
                this.mResumed = false;
                this.mSession.onPause();
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.pause();
                }
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            bundle.putInt("state_last_scroll_index", ((LinearLayoutManager) this.mManageApplications.mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        }

        public void release() {
            this.mSession.onDestroy();
            AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
            if (appStateBaseBridge != null) {
                appStateBaseBridge.release();
            }
        }

        public void rebuild(int i) {
            if (i != this.mLastSortMode) {
                this.mManageApplications.mSortOrder = i;
                this.mLastSortMode = i;
                rebuild();
            }
        }

        public ApplicationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            int i2 = this.mManageApplications.mListType;
            if (i2 == 1) {
                view = ApplicationViewHolder.newView(viewGroup, true);
            } else if (i2 == 14 || i2 == 15) {
                view = ApplicationViewHolder.newViewWithoutDivider(viewGroup, true);
            } else {
                view = ApplicationViewHolder.newView(viewGroup, false);
            }
            return new ApplicationViewHolder(view);
        }

        public void rebuild() {
            Comparator<ApplicationsState.AppEntry> comparator;
            ApplicationsState.CompoundFilter compoundFilter;
            boolean z = false;
            if (this.mHasReceivedLoadEntries && (this.mExtraInfoBridge == null || this.mHasReceivedBridgeCallback)) {
                if (Environment.isExternalStorageEmulated()) {
                    this.mWhichSize = 0;
                } else {
                    this.mWhichSize = 1;
                }
                ApplicationsState.CompoundFilter filter = this.mAppFilter.getFilter();
                ApplicationsState.AppFilter appFilter = this.mCompositeFilter;
                if (appFilter != null) {
                    filter = new ApplicationsState.CompoundFilter(filter, appFilter);
                }
                if (!this.mManageApplications.mShowSystem) {
                    if (ManageApplications.LIST_TYPES_WITH_INSTANT.contains(Integer.valueOf(this.mManageApplications.mListType))) {
                        compoundFilter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER_AND_INSTANT);
                    } else {
                        compoundFilter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER);
                    }
                    filter = compoundFilter;
                }
                int i = this.mLastSortMode;
                if (i == C1985R$id.sort_order_size) {
                    int i2 = this.mWhichSize;
                    if (i2 == 1) {
                        comparator = ApplicationsState.INTERNAL_SIZE_COMPARATOR;
                    } else if (i2 != 2) {
                        comparator = ApplicationsState.SIZE_COMPARATOR;
                    } else {
                        comparator = ApplicationsState.EXTERNAL_SIZE_COMPARATOR;
                    }
                } else if (i == C1985R$id.sort_order_recent_notification) {
                    comparator = AppStateNotificationBridge.RECENT_NOTIFICATION_COMPARATOR;
                } else if (i == C1985R$id.sort_order_frequent_notification) {
                    comparator = AppStateNotificationBridge.FREQUENCY_NOTIFICATION_COMPARATOR;
                } else if (i == C1985R$id.sort_order_apps_list_recents) {
                    comparator = AppStateBaseBridge.COMPARATOR_APPS_RECENT;
                } else {
                    comparator = ApplicationsState.ALPHA_COMPARATOR;
                }
                ThreadUtils.postOnBackgroundThread((Runnable) new ManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda0(this, new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_NOT_HIDE), comparator));
            } else if (ManageApplications.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Not rebuilding until all the app entries loaded. !mHasReceivedLoadEntries=");
                sb.append(!this.mHasReceivedLoadEntries);
                sb.append(" !mExtraInfoBridgeNull=");
                if (this.mExtraInfoBridge != null) {
                    z = true;
                }
                sb.append(z);
                sb.append(" !mHasReceivedBridgeCallback=");
                sb.append(!this.mHasReceivedBridgeCallback);
                Log.d("ManageApplications", sb.toString());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$rebuild$0(ApplicationsState.AppFilter appFilter, Comparator comparator) {
            this.mSession.rebuild(appFilter, comparator, false);
        }

        /* access modifiers changed from: package-private */
        public void filterSearch(String str) {
            if (this.mSearchFilter == null) {
                this.mSearchFilter = new SearchFilter();
            }
            if (this.mOriginalEntries == null) {
                Log.w("ManageApplications", "Apps haven't loaded completely yet, so nothing can be filtered");
            } else {
                this.mSearchFilter.filter(str);
            }
        }

        private static boolean packageNameEquals(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
            String str;
            String str2;
            if (packageItemInfo == null || packageItemInfo2 == null || (str = packageItemInfo.packageName) == null || (str2 = packageItemInfo2.packageName) == null) {
                return false;
            }
            return str.equals(str2);
        }

        private ArrayList<ApplicationsState.AppEntry> removeDuplicateIgnoringUser(ArrayList<ApplicationsState.AppEntry> arrayList) {
            int size = arrayList.size();
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>(size);
            ApplicationInfo applicationInfo = null;
            int i = 0;
            while (i < size) {
                ApplicationsState.AppEntry appEntry = arrayList.get(i);
                ApplicationInfo applicationInfo2 = appEntry.info;
                if (!packageNameEquals(applicationInfo, applicationInfo2)) {
                    arrayList2.add(appEntry);
                }
                i++;
                applicationInfo = applicationInfo2;
            }
            arrayList2.trimToSize();
            return arrayList2;
        }

        public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
            if (ManageApplications.DEBUG) {
                Log.d("ManageApplications", "onRebuildComplete size=" + arrayList.size());
            }
            int filterType = this.mAppFilter.getFilterType();
            if (filterType == 0 || filterType == 1) {
                arrayList = removeDuplicateIgnoringUser(arrayList);
            }
            this.mEntries = arrayList;
            this.mOriginalEntries = arrayList;
            notifyDataSetChanged();
            int i = 0;
            if (getItemCount() == 0) {
                this.mLoadingViewController.showEmpty(false);
            } else {
                this.mLoadingViewController.showContent(false);
                if (this.mManageApplications.mSearchView != null && this.mManageApplications.mSearchView.isVisibleToUser()) {
                    CharSequence query = this.mManageApplications.mSearchView.getQuery();
                    if (!TextUtils.isEmpty(query)) {
                        filterSearch(query.toString());
                    }
                }
            }
            if (this.mLastIndex != -1 && getItemCount() > this.mLastIndex) {
                this.mManageApplications.mRecyclerView.getLayoutManager().scrollToPosition(this.mLastIndex);
                this.mLastIndex = -1;
            }
            ManageApplications manageApplications = this.mManageApplications;
            if (manageApplications.mListType != 4) {
                manageApplications.setHasDisabled(this.mState.haveDisabledApps());
                this.mManageApplications.setHasInstant(this.mState.haveInstantApps());
                Bundle arguments = this.mManageApplications.getArguments();
                if (arguments != null) {
                    String string = arguments.getString(":settings:fragment_args_key");
                    ComponentName unflattenFromString = string != null ? ComponentName.unflattenFromString(string) : null;
                    while (unflattenFromString != null && i < arrayList.size()) {
                        if (arrayList.get(i).info.packageName.equals(unflattenFromString.getPackageName())) {
                            requestHighlight(i, this.mRecyclerView, this.mAppBarLayout);
                        }
                        i++;
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void updateLoading() {
            if (this.mHasReceivedLoadEntries && this.mSession.getAllApps().size() != 0) {
                this.mLoadingViewController.showContent(false);
            } else {
                this.mLoadingViewController.showLoadingViewDelayed();
            }
        }

        public void onExtraInfoUpdated() {
            this.mHasReceivedBridgeCallback = true;
            rebuild();
        }

        public void onRunningStateChanged(boolean z) {
            this.mManageApplications.getActivity().setProgressBarIndeterminateVisibility(z);
        }

        public void onPackageListChanged() {
            rebuild();
        }

        public void onLoadEntriesCompleted() {
            this.mHasReceivedLoadEntries = true;
            rebuild();
        }

        public void onPackageSizeChanged(String str) {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ApplicationInfo applicationInfo = this.mEntries.get(i).info;
                    if (applicationInfo != null || TextUtils.equals(str, applicationInfo.packageName)) {
                        if (TextUtils.equals(this.mManageApplications.mCurrentPkgName, applicationInfo.packageName)) {
                            rebuild();
                            return;
                        }
                        this.mOnScrollListener.postNotifyItemChange(i);
                    }
                }
            }
        }

        public void onLauncherInfoChanged() {
            if (!this.mManageApplications.mShowSystem) {
                rebuild();
            }
        }

        public void onAllSizesComputed() {
            if (this.mLastSortMode == C1985R$id.sort_order_size) {
                rebuild();
            }
        }

        public int getItemCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public int getApplicationCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }

        public ApplicationsState.AppEntry getAppEntry(int i) {
            return this.mEntries.get(i);
        }

        public long getItemId(int i) {
            if (i == this.mEntries.size()) {
                return -1;
            }
            return this.mEntries.get(i).f138id;
        }

        public boolean isEnabled(int i) {
            if (getItemViewType(i) == 1 || this.mManageApplications.mListType != 5) {
                return true;
            }
            ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            if (this.mBackend.isSysAllowlisted(appEntry.info.packageName) || this.mBackend.isDefaultActiveApp(appEntry.info.packageName)) {
                return false;
            }
            return true;
        }

        public void onBindViewHolder(ApplicationViewHolder applicationViewHolder, int i) {
            super.onBindViewHolder(applicationViewHolder, i);
            ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            synchronized (appEntry) {
                this.mState.ensureLabelDescription(appEntry);
                applicationViewHolder.setTitle(appEntry.label, appEntry.labelDescription);
                this.mState.ensureIcon(appEntry);
                applicationViewHolder.setIcon(appEntry.icon);
                updateSummary(applicationViewHolder, appEntry);
                updateSwitch(applicationViewHolder, appEntry);
                applicationViewHolder.updateDisableView(appEntry.info);
            }
            applicationViewHolder.setEnabled(isEnabled(i));
            applicationViewHolder.itemView.setOnClickListener(this.mManageApplications);
        }

        private void updateSummary(ApplicationViewHolder applicationViewHolder, ApplicationsState.AppEntry appEntry) {
            int i;
            ManageApplications manageApplications = this.mManageApplications;
            switch (manageApplications.mListType) {
                case 1:
                    Object obj = appEntry.extraInfo;
                    if (obj == null || !(obj instanceof AppStateNotificationBridge.NotificationsSentState)) {
                        applicationViewHolder.setSummary((CharSequence) null);
                        return;
                    } else {
                        applicationViewHolder.setSummary(AppStateNotificationBridge.getSummary(this.mContext, (AppStateNotificationBridge.NotificationsSentState) obj, this.mLastSortMode));
                        return;
                    }
                case 4:
                    Object obj2 = appEntry.extraInfo;
                    if (obj2 != null) {
                        if (new AppStateUsageBridge.UsageState((AppStateAppOpsBridge.PermissionState) obj2).isPermissible()) {
                            i = C1992R$string.app_permission_summary_allowed;
                        } else {
                            i = C1992R$string.app_permission_summary_not_allowed;
                        }
                        applicationViewHolder.setSummary(i);
                        return;
                    }
                    applicationViewHolder.setSummary((CharSequence) null);
                    return;
                case 5:
                    applicationViewHolder.setSummary(HighPowerDetail.getSummary(this.mContext, appEntry));
                    return;
                case 6:
                    applicationViewHolder.setSummary(DrawOverlayDetails.getSummary(this.mContext, appEntry));
                    return;
                case 7:
                    applicationViewHolder.setSummary(WriteSettingsDetails.getSummary(this.mContext, appEntry));
                    return;
                case 8:
                    applicationViewHolder.setSummary(ExternalSourcesDetails.getPreferenceSummary(this.mContext, appEntry));
                    return;
                case 10:
                    applicationViewHolder.setSummary(ChangeWifiStateDetails.getSummary(this.mContext, appEntry));
                    return;
                case 11:
                    applicationViewHolder.setSummary(ManageExternalStorageDetails.getSummary(this.mContext, appEntry));
                    return;
                case 12:
                    applicationViewHolder.setSummary(AlarmsAndRemindersDetails.getSummary(this.mContext, appEntry));
                    return;
                case 13:
                    applicationViewHolder.setSummary(MediaManagementAppsDetails.getSummary(this.mContext, appEntry));
                    return;
                case 14:
                case 15:
                    applicationViewHolder.mSummary.setVisibility(8);
                    return;
                default:
                    applicationViewHolder.updateSizeText(appEntry, manageApplications.mInvalidSizeStr, this.mWhichSize);
                    return;
            }
        }

        private void updateSwitch(ApplicationViewHolder applicationViewHolder, ApplicationsState.AppEntry appEntry) {
            int i = this.mManageApplications.mListType;
            if (i == 1) {
                applicationViewHolder.updateSwitch(((AppStateNotificationBridge) this.mExtraInfoBridge).getSwitchOnCheckedListener(appEntry), AppStateNotificationBridge.enableSwitch(appEntry), AppStateNotificationBridge.checkSwitch(appEntry));
                Object obj = appEntry.extraInfo;
                if (obj == null || !(obj instanceof AppStateNotificationBridge.NotificationsSentState)) {
                    applicationViewHolder.setSummary((CharSequence) null);
                } else {
                    applicationViewHolder.setSummary(AppStateNotificationBridge.getSummary(this.mContext, (AppStateNotificationBridge.NotificationsSentState) obj, this.mLastSortMode));
                }
            } else if (i == 14) {
                FullscreenAppsBridge fullscreenAppsBridge = (FullscreenAppsBridge) this.mExtraInfoBridge;
                applicationViewHolder.updateSwitch(fullscreenAppsBridge.getSwitchOnCheckedListener(appEntry), fullscreenAppsBridge.isSwitchEnabled(appEntry), fullscreenAppsBridge.isSwitchChecked(appEntry));
            } else if (i == 15) {
                EndlessApplicationsBridge endlessApplicationsBridge = (EndlessApplicationsBridge) this.mExtraInfoBridge;
                applicationViewHolder.updateSwitch(endlessApplicationsBridge.getSwitchOnCheckedListener(appEntry), endlessApplicationsBridge.isSwitchEnabled(appEntry), endlessApplicationsBridge.isSwitchChecked(appEntry));
            }
        }

        public static class OnScrollListener extends RecyclerView.OnScrollListener {
            private ApplicationsAdapter mAdapter;
            private boolean mDelayNotifyDataChange;
            private int mScrollState = 0;

            public OnScrollListener(ApplicationsAdapter applicationsAdapter) {
                this.mAdapter = applicationsAdapter;
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                this.mScrollState = i;
                if (i == 0 && this.mDelayNotifyDataChange) {
                    this.mDelayNotifyDataChange = false;
                    this.mAdapter.notifyDataSetChanged();
                }
            }

            public void postNotifyItemChange(int i) {
                if (this.mScrollState == 0) {
                    this.mAdapter.notifyItemChanged(i);
                } else {
                    this.mDelayNotifyDataChange = true;
                }
            }
        }

        private class SearchFilter extends Filter {
            private SearchFilter() {
            }

            /* access modifiers changed from: protected */
            public Filter.FilterResults performFiltering(CharSequence charSequence) {
                ArrayList arrayList;
                if (TextUtils.isEmpty(charSequence)) {
                    arrayList = ApplicationsAdapter.this.mOriginalEntries;
                } else {
                    ArrayList arrayList2 = new ArrayList();
                    Iterator it = ApplicationsAdapter.this.mOriginalEntries.iterator();
                    while (it.hasNext()) {
                        ApplicationsState.AppEntry appEntry = (ApplicationsState.AppEntry) it.next();
                        if (appEntry.label.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            arrayList2.add(appEntry);
                        }
                    }
                    arrayList = arrayList2;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = arrayList;
                filterResults.count = arrayList.size();
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ArrayList unused = ApplicationsAdapter.this.mEntries = (ArrayList) filterResults.values;
                ApplicationsAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
