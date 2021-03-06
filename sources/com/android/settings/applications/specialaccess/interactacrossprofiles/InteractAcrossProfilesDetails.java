package com.android.settings.applications.specialaccess.interactacrossprofiles;

import android.app.ActionBar;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.PermissionChecker;
import android.content.pm.CrossProfileApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppStoreUtil;
import com.android.settings.widget.CardPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.widget.LayoutPreference;

public class InteractAcrossProfilesDetails extends AppInfoBase implements Preference.OnPreferenceClickListener {
    private String mAppLabel;
    private Context mContext;
    private CrossProfileApps mCrossProfileApps;
    private LayoutPreference mHeader;
    private Intent mInstallAppIntent;
    private CardPreference mInstallBanner;
    private boolean mInstalledInPersonal;
    private boolean mInstalledInWork;
    /* access modifiers changed from: private */
    public boolean mIsPageLaunchedByApp;
    private PackageManager mPackageManager;
    private UserHandle mPersonalProfile;
    private RestrictedSwitchPreference mSwitchPref;
    private UserManager mUserManager;
    private UserHandle mWorkProfile;

    /* access modifiers changed from: protected */
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    public int getMetricsCategory() {
        return 1829;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mContext = context;
        this.mCrossProfileApps = (CrossProfileApps) context.getSystemService(CrossProfileApps.class);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mPackageManager = this.mContext.getPackageManager();
        UserHandle workProfile = InteractAcrossProfilesSettings.getWorkProfile(this.mUserManager);
        this.mWorkProfile = workProfile;
        this.mPersonalProfile = this.mUserManager.getProfileParent(workProfile);
        this.mInstalledInWork = isPackageInstalled(this.mPackageName, this.mWorkProfile.getIdentifier());
        this.mInstalledInPersonal = isPackageInstalled(this.mPackageName, this.mPersonalProfile.getIdentifier());
        this.mAppLabel = this.mPackageInfo.applicationInfo.loadLabel(this.mPackageManager).toString();
        this.mInstallAppIntent = AppStoreUtil.getAppStoreLink(this.mContext, this.mPackageName);
        addPreferencesFromResource(C1994R$xml.interact_across_profiles_permissions_details);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("interact_across_profiles_settings_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceClickListener(this);
        this.mHeader = (LayoutPreference) findPreference("interact_across_profiles_header");
        CardPreference cardPreference = (CardPreference) findPreference("install_app_banner");
        this.mInstallBanner = cardPreference;
        cardPreference.setOnPreferenceClickListener(this);
        this.mIsPageLaunchedByApp = launchedByApp();
        if (!refreshUi()) {
            setIntentAndFinish(true);
        }
        addAppTitleAndIcons(this.mPersonalProfile, this.mWorkProfile);
        styleActionBar();
        maybeShowExtraSummary();
        logPageLaunchMetrics();
    }

    private void maybeShowExtraSummary() {
        Preference findPreference = findPreference("interact_across_profiles_extra_summary");
        if (findPreference != null) {
            findPreference.setVisible(this.mIsPageLaunchedByApp);
        }
    }

    private void logPageLaunchMetrics() {
        if (!this.mCrossProfileApps.canConfigureInteractAcrossProfiles(this.mPackageName)) {
            logNonConfigurableAppMetrics();
        }
        if (this.mIsPageLaunchedByApp) {
            logEvent(162);
        } else {
            logEvent(163);
        }
    }

    private void logNonConfigurableAppMetrics() {
        if (!isCrossProfilePackageAllowlisted(this.mPackageName)) {
            logEvent(164);
            return;
        }
        if (this.mInstallBanner == null) {
            logEvent(167);
        }
        if (!this.mInstalledInPersonal) {
            logEvent(166);
        } else if (!this.mInstalledInWork) {
            logEvent(165);
        }
    }

    /* access modifiers changed from: private */
    public void logEvent(int i) {
        DevicePolicyEventLogger.createEvent(i).setStrings(new String[]{this.mPackageName}).setInt(UserHandle.myUserId()).setAdmin(RestrictedLockUtils.getProfileOrDeviceOwner(this.mContext, this.mWorkProfile).component).write();
    }

    private void addAppTitleAndIcons(UserHandle userHandle, UserHandle userHandle2) {
        TextView textView = (TextView) this.mHeader.findViewById(C1985R$id.entity_header_title);
        if (textView != null) {
            textView.setText(this.mPackageInfo.applicationInfo.loadLabel(this.mPackageManager).toString());
        }
        ImageView imageView = (ImageView) this.mHeader.findViewById(C1985R$id.entity_header_icon_personal);
        if (imageView != null) {
            Drawable mutate = IconDrawableFactory.newInstance(this.mContext).getBadgedIcon(this.mPackageInfo.applicationInfo, userHandle.getIdentifier()).mutate();
            if (!this.mInstalledInPersonal) {
                mutate.setColorFilter(createSuspendedColorMatrix());
            }
            imageView.setImageDrawable(mutate);
        }
        ImageView imageView2 = (ImageView) this.mHeader.findViewById(C1985R$id.entity_header_icon_work);
        if (imageView2 != null) {
            Drawable mutate2 = IconDrawableFactory.newInstance(this.mContext).getBadgedIcon(this.mPackageInfo.applicationInfo, userHandle2.getIdentifier()).mutate();
            if (!this.mInstalledInWork) {
                mutate2.setColorFilter(createSuspendedColorMatrix());
            }
            imageView2.setImageDrawable(mutate2);
        }
    }

    private void styleActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0.0f);
        }
    }

    private ColorMatrixColorFilter createSuspendedColorMatrix() {
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] array = colorMatrix.getArray();
        array[0] = 0.5f;
        array[6] = 0.5f;
        array[12] = 0.5f;
        float f = (float) 127;
        array[4] = f;
        array[9] = f;
        array[14] = f;
        ColorMatrix colorMatrix2 = new ColorMatrix();
        colorMatrix2.setSaturation(0.0f);
        colorMatrix2.preConcat(colorMatrix);
        return new ColorMatrixColorFilter(colorMatrix2);
    }

    public boolean onPreferenceClick(Preference preference) {
        if (!refreshUi()) {
            setIntentAndFinish(true);
        }
        if (preference == this.mSwitchPref) {
            handleSwitchPreferenceClick();
            return true;
        } else if (preference != this.mInstallBanner) {
            return false;
        } else {
            handleInstallBannerClick();
            return true;
        }
    }

    private void handleSwitchPreferenceClick() {
        if (isInteractAcrossProfilesEnabled()) {
            logEvent(172);
            enableInteractAcrossProfiles(false);
            refreshUi();
            return;
        }
        showConsentDialog();
    }

    private void showConsentDialog() {
        View inflate = getLayoutInflater().inflate(C1987R$layout.interact_across_profiles_consent_dialog, (ViewGroup) null);
        ((TextView) inflate.findViewById(C1985R$id.interact_across_profiles_consent_dialog_title)).setText(getString(C1992R$string.interact_across_profiles_consent_dialog_title, this.mAppLabel));
        ((TextView) inflate.findViewById(C1985R$id.app_data_summary)).setText(getString(C1992R$string.interact_across_profiles_consent_dialog_app_data_summary, this.mAppLabel));
        ((TextView) inflate.findViewById(C1985R$id.permissions_summary)).setText(getString(C1992R$string.interact_across_profiles_consent_dialog_permissions_summary, this.mAppLabel));
        new AlertDialog.Builder(getActivity()).setView(inflate).setPositiveButton(C1992R$string.allow, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                InteractAcrossProfilesDetails.this.logEvent(170);
                InteractAcrossProfilesDetails.this.enableInteractAcrossProfiles(true);
                InteractAcrossProfilesDetails.this.refreshUi();
                if (InteractAcrossProfilesDetails.this.mIsPageLaunchedByApp) {
                    InteractAcrossProfilesDetails.this.setIntentAndFinish(true);
                }
            }
        }).setNegativeButton(C1992R$string.deny, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                InteractAcrossProfilesDetails.this.logEvent(171);
                InteractAcrossProfilesDetails.this.refreshUi();
            }
        }).create().show();
    }

    private boolean isInteractAcrossProfilesEnabled() {
        return isInteractAcrossProfilesEnabled(this.mContext, this.mPackageName);
    }

    static boolean isInteractAcrossProfilesEnabled(Context context, String str) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        UserHandle workProfile = InteractAcrossProfilesSettings.getWorkProfile(userManager);
        if (workProfile == null) {
            return false;
        }
        UserHandle profileParent = userManager.getProfileParent(workProfile);
        if (!((CrossProfileApps) context.getSystemService(CrossProfileApps.class)).canConfigureInteractAcrossProfiles(str) || !isInteractAcrossProfilesEnabledInProfile(context, str, profileParent) || !isInteractAcrossProfilesEnabledInProfile(context, str, workProfile)) {
            return false;
        }
        return true;
    }

    private static boolean isInteractAcrossProfilesEnabledInProfile(Context context, String str, UserHandle userHandle) {
        try {
            if (PermissionChecker.checkPermissionForPreflight(context, "android.permission.INTERACT_ACROSS_PROFILES", -1, context.getPackageManager().getApplicationInfoAsUser(str, 0, userHandle).uid, str) == 0) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void enableInteractAcrossProfiles(boolean z) {
        this.mCrossProfileApps.setInteractAcrossProfilesAppOp(this.mPackageName, z ^ true ? 1 : 0);
    }

    private void handleInstallBannerClick() {
        if (this.mInstallAppIntent == null) {
            logEvent(169);
        } else if (!this.mInstalledInWork) {
            logEvent(168);
            this.mContext.startActivityAsUser(this.mInstallAppIntent, this.mWorkProfile);
        } else if (!this.mInstalledInPersonal) {
            logEvent(168);
            this.mContext.startActivityAsUser(this.mInstallAppIntent, this.mPersonalProfile);
        }
    }

    public static CharSequence getPreferenceSummary(Context context, String str) {
        int i;
        if (isInteractAcrossProfilesEnabled(context, str)) {
            i = C1992R$string.interact_across_profiles_summary_allowed;
        } else {
            i = C1992R$string.interact_across_profiles_summary_not_allowed;
        }
        return context.getString(i);
    }

    /* access modifiers changed from: protected */
    public boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return false;
        }
        if (!this.mCrossProfileApps.canUserAttemptToConfigureInteractAcrossProfiles(this.mPackageName)) {
            this.mSwitchPref.setEnabled(false);
            return false;
        } else if (!this.mCrossProfileApps.canConfigureInteractAcrossProfiles(this.mPackageName)) {
            return refreshUiForNonConfigurableApps();
        } else {
            refreshUiForConfigurableApps();
            return true;
        }
    }

    private boolean refreshUiForNonConfigurableApps() {
        this.mSwitchPref.setChecked(false);
        this.mSwitchPref.setTitle(C1992R$string.interact_across_profiles_switch_disabled);
        if (!isCrossProfilePackageAllowlisted(this.mPackageName)) {
            this.mInstallBanner.setVisible(false);
            this.mSwitchPref.setDisabledByAdmin(RestrictedLockUtils.getProfileOrDeviceOwner(this.mContext, this.mWorkProfile));
            return true;
        }
        this.mSwitchPref.setEnabled(false);
        boolean z = this.mInstalledInPersonal;
        if (!z && !this.mInstalledInWork) {
            return false;
        }
        if (!z) {
            this.mInstallBanner.setTitle((CharSequence) getString(C1992R$string.interact_across_profiles_install_personal_app_title, this.mAppLabel));
            if (this.mInstallAppIntent != null) {
                this.mInstallBanner.setSummary(C1992R$string.interact_across_profiles_install_app_summary);
            }
            this.mInstallBanner.setVisible(true);
            return true;
        } else if (this.mInstalledInWork) {
            return false;
        } else {
            this.mInstallBanner.setTitle((CharSequence) getString(C1992R$string.interact_across_profiles_install_work_app_title, this.mAppLabel));
            if (this.mInstallAppIntent != null) {
                this.mInstallBanner.setSummary(C1992R$string.interact_across_profiles_install_app_summary);
            }
            this.mInstallBanner.setVisible(true);
            return true;
        }
    }

    private boolean isCrossProfilePackageAllowlisted(String str) {
        return ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getAllCrossProfilePackages().contains(str);
    }

    private boolean isPackageInstalled(String str, int i) {
        try {
            if (this.mContext.createContextAsUser(UserHandle.of(i), 0).getPackageManager().getPackageInfo(str, 786432) != null) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private void refreshUiForConfigurableApps() {
        this.mInstallBanner.setVisible(false);
        this.mSwitchPref.setEnabled(true);
        if (isInteractAcrossProfilesEnabled()) {
            enableSwitchPref();
        } else {
            disableSwitchPref();
        }
    }

    private void enableSwitchPref() {
        this.mSwitchPref.setChecked(true);
        this.mSwitchPref.setTitle(C1992R$string.interact_across_profiles_switch_enabled);
        ImageView imageView = (ImageView) this.mHeader.findViewById(C1985R$id.entity_header_swap_horiz);
        if (imageView != null) {
            imageView.setImageDrawable(this.mContext.getDrawable(C1983R$drawable.ic_swap_horiz_blue));
        }
    }

    private void disableSwitchPref() {
        this.mSwitchPref.setChecked(false);
        this.mSwitchPref.setTitle(C1992R$string.interact_across_profiles_switch_disabled);
        ImageView imageView = (ImageView) this.mHeader.findViewById(C1985R$id.entity_header_swap_horiz);
        if (imageView != null) {
            imageView.setImageDrawable(this.mContext.getDrawable(C1983R$drawable.ic_swap_horiz_grey));
        }
    }

    private boolean launchedByApp() {
        Intent intent;
        Bundle bundleExtra = getIntent().getBundleExtra(":settings:show_fragment_args");
        if (bundleExtra == null || (intent = (Intent) bundleExtra.get("intent")) == null) {
            return false;
        }
        return "android.settings.MANAGE_CROSS_PROFILE_ACCESS".equals(intent.getAction());
    }
}
