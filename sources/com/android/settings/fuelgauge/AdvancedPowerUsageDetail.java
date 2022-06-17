package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.appinfo.AppButtonsPreferenceController;
import com.android.settings.applications.appinfo.ButtonActionDialogFragment;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import com.motorola.settings.utils.CloneAppUtils;
import java.util.ArrayList;
import java.util.List;

public class AdvancedPowerUsageDetail extends DashboardFragment implements ButtonActionDialogFragment.AppButtonsDialogListener, BatteryTipPreferenceController.BatteryTipListener, RadioButtonPreference.OnClickListener {
    boolean enableTriState = true;
    private AppButtonsPreferenceController mAppButtonsPreferenceController;
    ApplicationsState.AppEntry mAppEntry;
    private BackgroundActivityPreferenceController mBackgroundActivityPreferenceController;
    Preference mBackgroundPreference;
    BatteryOptimizeUtils mBatteryOptimizeUtils;
    BatteryUtils mBatteryUtils;
    FooterPreference mFooterPreference;
    Preference mForegroundPreference;
    LayoutPreference mHeaderPreference;
    RadioButtonPreference mOptimizePreference;
    RadioButtonPreference mRestrictedPreference;
    ApplicationsState mState;
    RadioButtonPreference mUnrestrictedPreference;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "AdvancedPowerDetail";
    }

    public int getMetricsCategory() {
        return 53;
    }

    private static final class LaunchBatteryDetailPageArgs {
        /* access modifiers changed from: private */
        public String mAppLabel;
        /* access modifiers changed from: private */
        public long mBackgroundTimeMs;
        /* access modifiers changed from: private */
        public int mConsumedPower;
        /* access modifiers changed from: private */
        public long mForegroundTimeMs;
        /* access modifiers changed from: private */
        public int mIconId;
        /* access modifiers changed from: private */
        public boolean mIsUserEntry;
        /* access modifiers changed from: private */
        public String mPackageName;
        /* access modifiers changed from: private */
        public String mSlotInformation;
        /* access modifiers changed from: private */
        public int mUid;
        /* access modifiers changed from: private */
        public String mUsagePercent;

        private LaunchBatteryDetailPageArgs() {
        }
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, BatteryDiffEntry batteryDiffEntry, String str, boolean z, String str2) {
        BatteryHistEntry batteryHistEntry = batteryDiffEntry.mBatteryHistEntry;
        LaunchBatteryDetailPageArgs launchBatteryDetailPageArgs = new LaunchBatteryDetailPageArgs();
        String unused = launchBatteryDetailPageArgs.mUsagePercent = str;
        String unused2 = launchBatteryDetailPageArgs.mPackageName = batteryDiffEntry.getPackageName();
        String unused3 = launchBatteryDetailPageArgs.mAppLabel = batteryDiffEntry.getAppLabel();
        String unused4 = launchBatteryDetailPageArgs.mSlotInformation = str2;
        int unused5 = launchBatteryDetailPageArgs.mUid = (int) batteryHistEntry.mUid;
        int unused6 = launchBatteryDetailPageArgs.mIconId = batteryDiffEntry.getAppIconId();
        int unused7 = launchBatteryDetailPageArgs.mConsumedPower = (int) batteryDiffEntry.mConsumePower;
        long j = 0;
        long unused8 = launchBatteryDetailPageArgs.mForegroundTimeMs = z ? batteryDiffEntry.mForegroundUsageTimeInMs : 0;
        if (z) {
            j = batteryDiffEntry.mBackgroundUsageTimeInMs;
        }
        long unused9 = launchBatteryDetailPageArgs.mBackgroundTimeMs = j;
        boolean unused10 = launchBatteryDetailPageArgs.mIsUserEntry = batteryHistEntry.isUserEntry();
        startBatteryDetailPage(activity, instrumentedPreferenceFragment, launchBatteryDetailPageArgs);
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, BatteryEntry batteryEntry, String str, boolean z) {
        LaunchBatteryDetailPageArgs launchBatteryDetailPageArgs = new LaunchBatteryDetailPageArgs();
        String unused = launchBatteryDetailPageArgs.mUsagePercent = str;
        String unused2 = launchBatteryDetailPageArgs.mPackageName = batteryEntry.getDefaultPackageName();
        String unused3 = launchBatteryDetailPageArgs.mAppLabel = batteryEntry.getLabel();
        int unused4 = launchBatteryDetailPageArgs.mUid = batteryEntry.getUid();
        int unused5 = launchBatteryDetailPageArgs.mIconId = batteryEntry.iconId;
        int unused6 = launchBatteryDetailPageArgs.mConsumedPower = (int) batteryEntry.getConsumedPower();
        long j = 0;
        long unused7 = launchBatteryDetailPageArgs.mForegroundTimeMs = z ? batteryEntry.getTimeInForegroundMs() : 0;
        if (z) {
            j = batteryEntry.getTimeInBackgroundMs();
        }
        long unused8 = launchBatteryDetailPageArgs.mBackgroundTimeMs = j;
        boolean unused9 = launchBatteryDetailPageArgs.mIsUserEntry = batteryEntry.isUserEntry();
        startBatteryDetailPage(activity, instrumentedPreferenceFragment, launchBatteryDetailPageArgs);
    }

    private static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, LaunchBatteryDetailPageArgs launchBatteryDetailPageArgs) {
        int i;
        Bundle bundle = new Bundle();
        if (launchBatteryDetailPageArgs.mPackageName == null) {
            bundle.putString("extra_label", launchBatteryDetailPageArgs.mAppLabel);
            bundle.putInt("extra_icon_id", launchBatteryDetailPageArgs.mIconId);
            bundle.putString("extra_package_name", (String) null);
        } else {
            bundle.putString("extra_package_name", launchBatteryDetailPageArgs.mPackageName);
        }
        bundle.putInt("extra_uid", launchBatteryDetailPageArgs.mUid);
        bundle.putLong("extra_background_time", launchBatteryDetailPageArgs.mBackgroundTimeMs);
        bundle.putLong("extra_foreground_time", launchBatteryDetailPageArgs.mForegroundTimeMs);
        bundle.putString("extra_slot_time", launchBatteryDetailPageArgs.mSlotInformation);
        bundle.putString("extra_power_usage_percent", launchBatteryDetailPageArgs.mUsagePercent);
        bundle.putInt("extra_power_usage_amount", launchBatteryDetailPageArgs.mConsumedPower);
        if (launchBatteryDetailPageArgs.mIsUserEntry) {
            i = ActivityManager.getCurrentUser();
        } else {
            i = UserHandle.getUserId(launchBatteryDetailPageArgs.mUid);
        }
        new SubSettingLauncher(activity).setDestination(AdvancedPowerUsageDetail.class.getName()).setTitleRes(C1992R$string.battery_details_title).setArguments(bundle).setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory()).setUserHandle(new UserHandle(i)).launch();
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, String str) {
        Bundle bundle = new Bundle(3);
        PackageManager packageManager = activity.getPackageManager();
        bundle.putString("extra_package_name", str);
        bundle.putString("extra_power_usage_percent", Utils.formatPercentage(0));
        try {
            bundle.putInt("extra_uid", packageManager.getPackageUid(str, 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("AdvancedPowerDetail", "Cannot find package: " + str, e);
        }
        new SubSettingLauncher(activity).setDestination(AdvancedPowerUsageDetail.class.getName()).setTitleRes(C1992R$string.battery_details_title).setArguments(bundle).setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory()).launch();
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, String str, int i) {
        Bundle bundle = new Bundle(3);
        activity.getPackageManager();
        bundle.putString("extra_package_name", str);
        bundle.putString("extra_power_usage_percent", Utils.formatPercentage(0));
        bundle.putInt("extra_uid", i);
        new SubSettingLauncher(activity).setDestination(AdvancedPowerUsageDetail.class.getName()).setTitleRes(C1992R$string.battery_details_title).setArguments(bundle).setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory()).launch();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mState = ApplicationsState.getInstance(getActivity().getApplication());
        this.mBatteryUtils = BatteryUtils.getInstance(getContext());
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String string = getArguments().getString("extra_package_name");
        if (this.enableTriState) {
            onCreateForTriState(string);
        } else {
            this.mForegroundPreference = findPreference("app_usage_foreground");
            this.mBackgroundPreference = findPreference("app_usage_background");
        }
        this.mHeaderPreference = (LayoutPreference) findPreference("header_view");
        if (string != null) {
            int i = getArguments().getInt("extra_uid", -1);
            if (CloneAppUtils.isCloneAppByUid(i)) {
                this.mAppEntry = this.mState.getEntry(string, UserHandle.getUserId(i));
            } else {
                this.mAppEntry = this.mState.getEntry(string, UserHandle.myUserId());
            }
        }
    }

    public void onResume() {
        super.onResume();
        initHeader();
        if (this.enableTriState) {
            initPreferenceForTriState(getContext());
            FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), 1889, this.mBatteryOptimizeUtils.getPackageName());
            return;
        }
        initPreference(getContext());
    }

    /* access modifiers changed from: package-private */
    public void initHeader() {
        View findViewById = this.mHeaderPreference.findViewById(C1985R$id.entity_header);
        FragmentActivity activity = getActivity();
        Bundle arguments = getArguments();
        EntityHeaderController buttonActions = EntityHeaderController.newInstance(activity, this, findViewById).setRecyclerView(getListView(), getSettingsLifecycle()).setButtonActions(0, 0);
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null) {
            buttonActions.setLabel((CharSequence) arguments.getString("extra_label"));
            if (arguments.getInt("extra_icon_id", 0) == 0) {
                buttonActions.setIcon(activity.getPackageManager().getDefaultActivityIcon());
            } else {
                buttonActions.setIcon(activity.getDrawable(arguments.getInt("extra_icon_id")));
            }
        } else {
            this.mState.ensureIcon(appEntry);
            buttonActions.setLabel(this.mAppEntry);
            buttonActions.setIcon(this.mAppEntry);
            buttonActions.setIsInstantApp(AppUtils.isInstant(this.mAppEntry.info));
        }
        if (this.enableTriState) {
            buttonActions.setSummary(getAppActiveTime(arguments.getLong("extra_foreground_time"), arguments.getLong("extra_background_time"), arguments.getString("extra_slot_time", (String) null)));
        }
        buttonActions.done((Activity) activity, true);
    }

    /* access modifiers changed from: package-private */
    public void initPreference(Context context) {
        Bundle arguments = getArguments();
        long j = arguments.getLong("extra_foreground_time");
        long j2 = arguments.getLong("extra_background_time");
        this.mForegroundPreference.setSummary(TextUtils.expandTemplate(getText(C1992R$string.battery_used_for), new CharSequence[]{StringUtil.formatElapsedTime(context, (double) j, false, false)}));
        this.mBackgroundPreference.setSummary(TextUtils.expandTemplate(getText(C1992R$string.battery_active_for), new CharSequence[]{StringUtil.formatElapsedTime(context, (double) j2, false, false)}));
    }

    /* access modifiers changed from: package-private */
    public void initPreferenceForTriState(Context context) {
        String str;
        if (!this.mBatteryOptimizeUtils.isValidPackageName()) {
            String string = context.getString(C1992R$string.manager_battery_usage_optimized_only);
            str = context.getString(C1992R$string.manager_battery_usage_footer_limited, new Object[]{string});
        } else if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            String string2 = context.getString(C1992R$string.manager_battery_usage_unrestricted_only);
            str = context.getString(C1992R$string.manager_battery_usage_footer_limited, new Object[]{string2});
        } else {
            str = context.getString(C1992R$string.manager_battery_usage_footer);
        }
        this.mFooterPreference.setTitle((CharSequence) str);
        this.mFooterPreference.setLearnMoreAction(new AdvancedPowerUsageDetail$$ExternalSyntheticLambda0(this, context));
        this.mFooterPreference.setLearnMoreContentDescription(context.getString(C1992R$string.manager_battery_usage_link_a11y));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initPreferenceForTriState$0(Context context, View view) {
        startActivityForResult(HelpUtils.getHelpIntent(context, context.getString(C1992R$string.help_url_app_usage_settings), ""), 0);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return this.enableTriState ? C1994R$xml.power_usage_detail : C1994R$xml.power_usage_detail_legacy;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        Bundle arguments = getArguments();
        int i = arguments.getInt("extra_uid", 0);
        String string = arguments.getString("extra_package_name");
        AppButtonsPreferenceController appButtonsPreferenceController = new AppButtonsPreferenceController((SettingsActivity) getActivity(), this, getSettingsLifecycle(), string, this.mState, 0, 1);
        this.mAppButtonsPreferenceController = appButtonsPreferenceController;
        arrayList.add(appButtonsPreferenceController);
        if (this.enableTriState) {
            arrayList.add(new UnrestrictedPreferenceController(context, i, string));
            arrayList.add(new OptimizedPreferenceController(context, i, string));
            arrayList.add(new RestrictedPreferenceController(context, i, string));
        } else {
            BackgroundActivityPreferenceController backgroundActivityPreferenceController = new BackgroundActivityPreferenceController(context, this, i, string);
            this.mBackgroundActivityPreferenceController = backgroundActivityPreferenceController;
            arrayList.add(backgroundActivityPreferenceController);
            arrayList.add(new BatteryOptimizationPreferenceController((SettingsActivity) getActivity(), this, string));
        }
        return arrayList;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        AppButtonsPreferenceController appButtonsPreferenceController = this.mAppButtonsPreferenceController;
        if (appButtonsPreferenceController != null) {
            appButtonsPreferenceController.handleActivityResult(i, i2, intent);
        }
    }

    public void handleDialogClick(int i) {
        AppButtonsPreferenceController appButtonsPreferenceController = this.mAppButtonsPreferenceController;
        if (appButtonsPreferenceController != null) {
            appButtonsPreferenceController.handleDialogClick(i);
        }
    }

    public void onBatteryTipHandled(BatteryTip batteryTip) {
        BackgroundActivityPreferenceController backgroundActivityPreferenceController = this.mBackgroundActivityPreferenceController;
        backgroundActivityPreferenceController.updateSummary(findPreference(backgroundActivityPreferenceController.getPreferenceKey()));
    }

    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        int i;
        String key = radioButtonPreference.getKey();
        updatePreferenceState(this.mUnrestrictedPreference, key);
        updatePreferenceState(this.mOptimizePreference, key);
        updatePreferenceState(this.mRestrictedPreference, key);
        if (key.equals(this.mUnrestrictedPreference.getKey())) {
            i = 1776;
        } else if (key.equals(this.mOptimizePreference.getKey())) {
            i = 1777;
        } else {
            i = key.equals(this.mRestrictedPreference.getKey()) ? 1778 : 0;
        }
        if (i != 0) {
            FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), i, (Pair<Integer, Object>[]) new Pair[]{new Pair(1, this.mBatteryOptimizeUtils.getPackageName()), new Pair(3, getArguments().getString("extra_power_usage_percent"))});
        }
    }

    private void updatePreferenceState(RadioButtonPreference radioButtonPreference, String str) {
        radioButtonPreference.setChecked(str.equals(radioButtonPreference.getKey()));
    }

    private void onCreateForTriState(String str) {
        this.mUnrestrictedPreference = (RadioButtonPreference) findPreference("unrestricted_pref");
        this.mOptimizePreference = (RadioButtonPreference) findPreference("optimized_pref");
        this.mRestrictedPreference = (RadioButtonPreference) findPreference("restricted_pref");
        this.mFooterPreference = (FooterPreference) findPreference("app_usage_footer_preference");
        this.mUnrestrictedPreference.setOnClickListener(this);
        this.mOptimizePreference.setOnClickListener(this);
        this.mRestrictedPreference.setOnClickListener(this);
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(getContext(), getArguments().getInt("extra_uid"), str);
    }

    private CharSequence getAppActiveTime(long j, long j2, String str) {
        long j3 = j + j2;
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(getContext()).getPowerUsageFeatureProvider(getContext());
        if (j3 == 0) {
            return getText(powerUsageFeatureProvider.isChartGraphEnabled(getContext()) ? C1992R$string.battery_not_usage_24hr : C1992R$string.battery_not_usage);
        } else if (str != null) {
            return getAppActiveSummaryWithSlotTime(j, j2, j3, str);
        } else {
            if (powerUsageFeatureProvider.isChartGraphEnabled(getContext())) {
                return getAppPast24HrActiveSummary(j, j2, j3);
            }
            return getAppFullChargeActiveSummary(j, j2, j3);
        }
    }

    private CharSequence getAppFullChargeActiveSummary(long j, long j2, long j3) {
        int i;
        if (j != 0 || j2 == 0) {
            if (j3 < 60000) {
                return getText(C1992R$string.battery_total_usage_less_minute);
            }
            if (j2 < 60000) {
                if (j2 == 0) {
                    i = C1992R$string.battery_total_usage;
                } else {
                    i = C1992R$string.battery_total_usage_and_bg_less_minute_usage;
                }
                return TextUtils.expandTemplate(getText(i), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j3, false, false)});
            }
            return TextUtils.expandTemplate(getText(C1992R$string.battery_total_and_bg_usage), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j3, false, false), StringUtil.formatElapsedTime(getContext(), (double) j2, false, false)});
        } else if (j2 < 60000) {
            return getText(C1992R$string.battery_bg_usage_less_minute);
        } else {
            return TextUtils.expandTemplate(getText(C1992R$string.battery_bg_usage), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j2, false, false)});
        }
    }

    private CharSequence getAppPast24HrActiveSummary(long j, long j2, long j3) {
        int i;
        if (j != 0 || j2 == 0) {
            if (j3 < 60000) {
                return getText(C1992R$string.battery_total_usage_less_minute_24hr);
            }
            if (j2 < 60000) {
                if (j2 == 0) {
                    i = C1992R$string.battery_total_usage_24hr;
                } else {
                    i = C1992R$string.battery_total_usage_and_bg_less_minute_usage_24hr;
                }
                return TextUtils.expandTemplate(getText(i), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j3, false, false)});
            }
            return TextUtils.expandTemplate(getText(C1992R$string.battery_total_and_bg_usage_24hr), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j3, false, false), StringUtil.formatElapsedTime(getContext(), (double) j2, false, false)});
        } else if (j2 < 60000) {
            return getText(C1992R$string.battery_bg_usage_less_minute_24hr);
        } else {
            return TextUtils.expandTemplate(getText(C1992R$string.battery_bg_usage_24hr), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j2, false, false)});
        }
    }

    private CharSequence getAppActiveSummaryWithSlotTime(long j, long j2, long j3, String str) {
        int i;
        if (j != 0 || j2 == 0) {
            if (j3 < 60000) {
                return TextUtils.expandTemplate(getText(C1992R$string.battery_total_usage_less_minute_with_period), new CharSequence[]{str});
            } else if (j2 < 60000) {
                if (j2 == 0) {
                    i = C1992R$string.battery_total_usage_with_period;
                } else {
                    i = C1992R$string.battery_total_usage_and_bg_less_minute_usage_with_period;
                }
                return TextUtils.expandTemplate(getText(i), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j3, false, false), str});
            } else {
                return TextUtils.expandTemplate(getText(C1992R$string.battery_total_and_bg_usage_with_period), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j3, false, false), StringUtil.formatElapsedTime(getContext(), (double) j2, false, false), str});
            }
        } else if (j2 < 60000) {
            return TextUtils.expandTemplate(getText(C1992R$string.battery_bg_usage_less_minute_with_period), new CharSequence[]{str});
        } else {
            return TextUtils.expandTemplate(getText(C1992R$string.battery_bg_usage_with_period), new CharSequence[]{StringUtil.formatElapsedTime(getContext(), (double) j2, false, false), str});
        }
    }
}
