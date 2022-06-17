package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.Settings;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.motorola.settings.utils.DemoModeUtils;

public class MainClear extends InstrumentedFragment implements ViewTreeObserver.OnGlobalLayoutListener {
    static final int CREDENTIAL_CONFIRM_REQUEST = 56;
    private static final boolean DEBUG;
    static final int KEYGUARD_REQUEST = 55;
    private View mContentView;
    CheckBox mEsimStorage;
    View mEsimStorageContainer;
    CheckBox mExternalStorage;
    private View mExternalStorageContainer;
    FooterButton mInitiateButton;
    protected final View.OnClickListener mInitiateListener = new View.OnClickListener() {
        public void onClick(View view) {
            Context context = view.getContext();
            if (Utils.isDemoUser(context)) {
                ComponentName deviceOwnerComponent = Utils.getDeviceOwnerComponent(context);
                if (deviceOwnerComponent != null) {
                    context.startActivity(new Intent().setPackage(deviceOwnerComponent.getPackageName()).setAction("android.intent.action.FACTORY_RESET"));
                }
            } else if (!MainClear.this.runKeyguardConfirmation(55)) {
                Intent accountConfirmationIntent = MainClear.this.getAccountConfirmationIntent();
                if (accountConfirmationIntent != null) {
                    MainClear.this.showAccountCredentialConfirmation(accountConfirmationIntent);
                } else {
                    MainClear.this.showFinalConfirmation();
                }
            }
        }
    };
    ScrollView mScrollView;

    public int getMetricsCategory() {
        return 66;
    }

    /* access modifiers changed from: package-private */
    public boolean isValidRequestCode(int i) {
        return i == 55 || i == 56;
    }

    static {
        boolean z = false;
        if (SystemProperties.getInt("ro.debuggable", 0) == 1) {
            z = true;
        }
        DEBUG = z;
    }

    public void onGlobalLayout() {
        this.mInitiateButton.setEnabled(hasReachedBottom(this.mScrollView));
    }

    private void setUpActionBarAndTitle() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e("MainClear", "No activity attached, skipping setUpActionBarAndTitle");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.e("MainClear", "No actionbar, skipping setUpActionBarAndTitle");
            return;
        }
        actionBar.hide();
        activity.getWindow().setStatusBarColor(0);
    }

    /* access modifiers changed from: private */
    public boolean runKeyguardConfirmation(int i) {
        Resources resources = getActivity().getResources();
        if (!DemoModeUtils.isDemoModeEnabled(getActivity())) {
            return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(resources.getText(C1992R$string.main_clear_short_title)).show();
        }
        DemoModeUtils.launchMasterClearConfirmation(this, i);
        return true;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        onActivityResultInternal(i, i2, intent);
    }

    /* access modifiers changed from: package-private */
    public void onActivityResultInternal(int i, int i2, Intent intent) {
        Intent accountConfirmationIntent;
        if (isValidRequestCode(i)) {
            if (i2 != -1) {
                establishInitialState();
            } else if (56 == i || (accountConfirmationIntent = getAccountConfirmationIntent()) == null) {
                showFinalConfirmation();
            } else {
                showAccountCredentialConfirmation(accountConfirmationIntent);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void showFinalConfirmation() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("erase_sd", this.mExternalStorage.isChecked());
        bundle.putBoolean("erase_esim", this.mEsimStorage.isChecked());
        Intent intent = new Intent();
        intent.setClass(getContext(), Settings.FactoryResetConfirmActivity.class);
        intent.putExtra(":settings:show_fragment", MainClearConfirm.class.getName());
        intent.putExtra(":settings:show_fragment_args", bundle);
        intent.putExtra(":settings:show_fragment_title_resid", C1992R$string.main_clear_confirm_title);
        intent.putExtra(":settings:source_metrics", getMetricsCategory());
        getContext().startActivity(intent);
    }

    /* access modifiers changed from: package-private */
    public void showAccountCredentialConfirmation(Intent intent) {
        startActivityForResult(intent, 56);
    }

    /* access modifiers changed from: package-private */
    public Intent getAccountConfirmationIntent() {
        ActivityInfo activityInfo;
        FragmentActivity activity = getActivity();
        String string = activity.getString(C1992R$string.account_type);
        String string2 = activity.getString(C1992R$string.account_confirmation_package);
        String string3 = activity.getString(C1992R$string.account_confirmation_class);
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2) || TextUtils.isEmpty(string3)) {
            Log.i("MainClear", "Resources not set for account confirmation.");
            return null;
        }
        Account[] accountsByType = AccountManager.get(activity).getAccountsByType(string);
        if (accountsByType == null || accountsByType.length <= 0) {
            Log.d("MainClear", "No " + string + " accounts installed!");
        } else {
            Intent component = new Intent().setPackage(string2).setComponent(new ComponentName(string2, string3));
            ResolveInfo resolveActivity = activity.getPackageManager().resolveActivity(component, 0);
            if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null && string2.equals(activityInfo.packageName)) {
                return component;
            }
            Log.i("MainClear", "Unable to resolve Activity: " + string2 + "/" + string3);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void establishInitialState() {
        setUpActionBarAndTitle();
        setUpInitiateButton();
        this.mExternalStorageContainer = this.mContentView.findViewById(C1985R$id.erase_external_container);
        this.mExternalStorage = (CheckBox) this.mContentView.findViewById(C1985R$id.erase_external);
        this.mEsimStorageContainer = this.mContentView.findViewById(C1985R$id.erase_esim_container);
        this.mEsimStorage = (CheckBox) this.mContentView.findViewById(C1985R$id.erase_esim);
        ScrollView scrollView = this.mScrollView;
        if (scrollView != null) {
            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        this.mScrollView = (ScrollView) this.mContentView.findViewById(C1985R$id.main_clear_scrollview);
        if (!hasPublicSDCard()) {
            this.mExternalStorageContainer.setVisibility(8);
            this.mContentView.findViewById(C1985R$id.erase_external_option_text).setVisibility(8);
            this.mContentView.findViewById(C1985R$id.also_erases_external).setVisibility(0);
            this.mExternalStorage.setChecked(StorageManager.hasAdoptable());
        } else {
            this.mExternalStorageContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainClear.this.mExternalStorage.toggle();
                }
            });
        }
        if (!showWipeEuicc()) {
            this.mEsimStorage.setChecked(false);
        } else if (showWipeEuiccCheckbox()) {
            this.mEsimStorageContainer.setVisibility(0);
            this.mEsimStorageContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainClear.this.mEsimStorage.toggle();
                }
            });
        } else {
            this.mContentView.findViewById(C1985R$id.also_erases_esim).setVisibility(0);
            this.mContentView.findViewById(C1985R$id.no_cancel_mobile_plan).setVisibility(0);
            this.mEsimStorage.setChecked(true);
        }
        loadAccountList((UserManager) getActivity().getSystemService("user"));
        StringBuffer stringBuffer = new StringBuffer();
        View findViewById = this.mContentView.findViewById(C1985R$id.main_clear_container);
        getContentDescription(findViewById, stringBuffer);
        findViewById.setContentDescription(stringBuffer);
        this.mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            public void onScrollChange(View view, int i, int i2, int i3, int i4) {
                if ((view instanceof ScrollView) && MainClear.this.hasReachedBottom((ScrollView) view)) {
                    MainClear.this.mInitiateButton.setEnabled(true);
                    MainClear.this.mScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                }
            }
        });
        this.mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /* access modifiers changed from: package-private */
    public boolean showWipeEuicc() {
        Context context = getContext();
        if (!isEuiccEnabled(context)) {
            return false;
        }
        if (Settings.Global.getInt(context.getContentResolver(), "euicc_provisioned", 0) != 0 || DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean showWipeEuiccCheckbox() {
        return SystemProperties.getBoolean("masterclear.allow_retain_esim_profiles_after_fdr", false);
    }

    /* access modifiers changed from: protected */
    public boolean isEuiccEnabled(Context context) {
        return ((EuiccManager) context.getSystemService("euicc")).isEnabled();
    }

    /* access modifiers changed from: package-private */
    public boolean hasReachedBottom(ScrollView scrollView) {
        if (scrollView.getChildCount() >= 1 && scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) > 0) {
            return false;
        }
        return true;
    }

    private void setUpInitiateButton() {
        if (this.mInitiateButton == null) {
            FooterBarMixin footerBarMixin = (FooterBarMixin) ((GlifLayout) this.mContentView.findViewById(C1985R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class);
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.main_clear_button_text).setListener(this.mInitiateListener).setButtonType(0).setTheme(C1993R$style.SudGlifButton_Primary).build());
            this.mInitiateButton = footerBarMixin.getPrimaryButton();
        }
    }

    private void getContentDescription(View view, StringBuffer stringBuffer) {
        if (view.getVisibility() == 0) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    getContentDescription(viewGroup.getChildAt(i), stringBuffer);
                }
            } else if (view instanceof TextView) {
                stringBuffer.append(((TextView) view).getText());
                stringBuffer.append(",");
            }
        }
    }

    private boolean hasPublicSDCard() {
        for (VolumeInfo volumeInfo : ((StorageManager) getActivity().getSystemService("storage")).getVolumes()) {
            if (volumeInfo.getType() == 0 && volumeInfo.getDisk() != null && volumeInfo.isVisible() && volumeInfo.getDisk().isSd()) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:57:0x0163  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadAccountList(android.os.UserManager r24) {
        /*
            r23 = this;
            r1 = r23
            android.view.View r0 = r1.mContentView
            int r2 = com.android.settings.C1985R$id.accounts_label
            android.view.View r2 = r0.findViewById(r2)
            android.view.View r0 = r1.mContentView
            int r3 = com.android.settings.C1985R$id.accounts
            android.view.View r0 = r0.findViewById(r3)
            r3 = r0
            android.widget.LinearLayout r3 = (android.widget.LinearLayout) r3
            r3.removeAllViews()
            androidx.fragment.app.FragmentActivity r4 = r23.getActivity()
            int r0 = android.os.UserHandle.myUserId()
            r5 = r24
            java.util.List r6 = r5.getProfiles(r0)
            int r7 = r6.size()
            android.accounts.AccountManager r8 = android.accounts.AccountManager.get(r4)
            java.lang.String r0 = "layout_inflater"
            java.lang.Object r0 = r4.getSystemService(r0)
            r9 = r0
            android.view.LayoutInflater r9 = (android.view.LayoutInflater) r9
            r0 = 0
            r11 = 0
        L_0x0039:
            if (r11 >= r7) goto L_0x01b0
            java.lang.Object r13 = r6.get(r11)
            android.content.pm.UserInfo r13 = (android.content.pm.UserInfo) r13
            int r14 = r13.id
            android.os.UserHandle r15 = new android.os.UserHandle
            r15.<init>(r14)
            android.accounts.Account[] r10 = r8.getAccountsAsUser(r14)
            int r12 = r10.length
            if (r12 != 0) goto L_0x0055
            r17 = r6
            r16 = r8
            goto L_0x01a6
        L_0x0055:
            android.accounts.AccountManager r5 = android.accounts.AccountManager.get(r4)
            android.accounts.AuthenticatorDescription[] r5 = r5.getAuthenticatorTypesAsUser(r14)
            int r14 = r5.length
            r17 = r6
            r6 = 1
            if (r7 <= r6) goto L_0x0086
            android.view.View r6 = com.android.settings.Utils.inflateCategoryHeader(r9, r3)
            r16 = r8
            r8 = 16908310(0x1020016, float:2.387729E-38)
            android.view.View r18 = r6.findViewById(r8)
            r8 = r18
            android.widget.TextView r8 = (android.widget.TextView) r8
            boolean r13 = r13.isManagedProfile()
            if (r13 == 0) goto L_0x007d
            int r13 = com.android.settings.C1992R$string.category_work
            goto L_0x007f
        L_0x007d:
            int r13 = com.android.settings.C1992R$string.category_personal
        L_0x007f:
            r8.setText(r13)
            r3.addView(r6)
            goto L_0x0088
        L_0x0086:
            r16 = r8
        L_0x0088:
            r6 = r0
            r8 = 0
        L_0x008a:
            if (r8 >= r12) goto L_0x01a5
            r13 = r10[r8]
            r0 = 0
        L_0x008f:
            r18 = 0
            r19 = r10
            if (r0 >= r14) goto L_0x00ae
            java.lang.String r10 = r13.type
            r20 = r12
            r12 = r5[r0]
            java.lang.String r12 = r12.type
            boolean r10 = r10.equals(r12)
            if (r10 == 0) goto L_0x00a7
            r0 = r5[r0]
            r10 = r0
            goto L_0x00b2
        L_0x00a7:
            int r0 = r0 + 1
            r10 = r19
            r12 = r20
            goto L_0x008f
        L_0x00ae:
            r20 = r12
            r10 = r18
        L_0x00b2:
            java.lang.String r12 = "MainClear"
            if (r10 != 0) goto L_0x00d7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "No descriptor for account name="
            r0.append(r10)
            java.lang.String r10 = r13.name
            r0.append(r10)
            java.lang.String r10 = " type="
            r0.append(r10)
            java.lang.String r10 = r13.type
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r12, r0)
            goto L_0x00f9
        L_0x00d7:
            java.lang.String r0 = r13.type
            boolean r0 = com.motorola.settings.accounts.MotoAccountsHelper.isHidden(r4, r0)
            if (r0 == 0) goto L_0x0102
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x00f9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "Skipping moto hidden account type: "
            r0.append(r10)
            java.lang.String r10 = r13.type
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r12, r0)
        L_0x00f9:
            r21 = r5
            r22 = r14
            r10 = 16908310(0x1020016, float:2.387729E-38)
            goto L_0x0199
        L_0x0102:
            int r0 = r10.iconId     // Catch:{ NameNotFoundException -> 0x0147, NotFoundException -> 0x012b }
            if (r0 == 0) goto L_0x0126
            java.lang.String r0 = r10.packageName     // Catch:{ NameNotFoundException -> 0x0147, NotFoundException -> 0x012b }
            r21 = r5
            r5 = 0
            android.content.Context r0 = r4.createPackageContextAsUser(r0, r5, r15)     // Catch:{ NameNotFoundException -> 0x0149, NotFoundException -> 0x0124 }
            android.content.pm.PackageManager r5 = r4.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0149, NotFoundException -> 0x0124 }
            r22 = r14
            int r14 = r10.iconId     // Catch:{ NameNotFoundException -> 0x014b, NotFoundException -> 0x0122 }
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r14)     // Catch:{ NameNotFoundException -> 0x014b, NotFoundException -> 0x0122 }
            android.graphics.drawable.Drawable r0 = r5.getUserBadgedIcon(r0, r15)     // Catch:{ NameNotFoundException -> 0x014b, NotFoundException -> 0x0122 }
            r18 = r0
            goto L_0x0161
        L_0x0122:
            r0 = move-exception
            goto L_0x0130
        L_0x0124:
            r0 = move-exception
            goto L_0x012e
        L_0x0126:
            r21 = r5
            r22 = r14
            goto L_0x0161
        L_0x012b:
            r0 = move-exception
            r21 = r5
        L_0x012e:
            r22 = r14
        L_0x0130:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r14 = "Invalid icon id for account type "
            r5.append(r14)
            java.lang.String r10 = r10.type
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            android.util.Log.w(r12, r5, r0)
            goto L_0x0161
        L_0x0147:
            r21 = r5
        L_0x0149:
            r22 = r14
        L_0x014b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "Bad package name for account type "
            r0.append(r5)
            java.lang.String r5 = r10.type
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r12, r0)
        L_0x0161:
            if (r18 != 0) goto L_0x016b
            android.content.pm.PackageManager r0 = r4.getPackageManager()
            android.graphics.drawable.Drawable r18 = r0.getDefaultActivityIcon()
        L_0x016b:
            r0 = r18
            int r6 = r6 + 1
            int r5 = com.android.settings.C1987R$layout.main_clear_account
            r10 = 0
            android.view.View r5 = r9.inflate(r5, r3, r10)
            r10 = 16908294(0x1020006, float:2.3877246E-38)
            android.view.View r10 = r5.findViewById(r10)
            android.widget.ImageView r10 = (android.widget.ImageView) r10
            r10.setImageDrawable(r0)
            r10 = 16908310(0x1020016, float:2.387729E-38)
            android.view.View r0 = r5.findViewById(r10)
            android.widget.TextView r0 = (android.widget.TextView) r0
            androidx.fragment.app.FragmentActivity r12 = r23.getActivity()
            java.lang.String r12 = com.motorola.settings.accounts.MotoAccountsHelper.getAccountDisplayName(r12, r13)
            r0.setText(r12)
            r3.addView(r5)
        L_0x0199:
            int r8 = r8 + 1
            r10 = r19
            r12 = r20
            r5 = r21
            r14 = r22
            goto L_0x008a
        L_0x01a5:
            r0 = r6
        L_0x01a6:
            int r11 = r11 + 1
            r5 = r24
            r8 = r16
            r6 = r17
            goto L_0x0039
        L_0x01b0:
            r6 = 1
            r4 = 0
            if (r0 <= 0) goto L_0x01ba
            r2.setVisibility(r4)
            r3.setVisibility(r4)
        L_0x01ba:
            android.view.View r0 = r1.mContentView
            int r1 = com.android.settings.C1985R$id.other_users_present
            android.view.View r0 = r0.findViewById(r1)
            int r1 = r24.getUserCount()
            int r1 = r1 - r7
            if (r1 <= 0) goto L_0x01cb
            r12 = r6
            goto L_0x01cc
        L_0x01cb:
            r12 = r4
        L_0x01cc:
            if (r12 == 0) goto L_0x01d0
            r10 = r4
            goto L_0x01d2
        L_0x01d0:
            r10 = 8
        L_0x01d2:
            r0.setVisibility(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.MainClear.loadAccountList(android.os.UserManager):void");
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = getContext();
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_factory_reset", UserHandle.myUserId());
        if ((!UserManager.get(context).isAdminUser() || RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_factory_reset", UserHandle.myUserId())) && !Utils.isDemoUser(context)) {
            return layoutInflater.inflate(C1987R$layout.main_clear_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_factory_reset", checkIfRestrictionEnforced).setOnDismissListener(new MainClear$$ExternalSyntheticLambda0(this)).show();
            return new View(getContext());
        }
        this.mContentView = layoutInflater.inflate(C1987R$layout.main_clear, (ViewGroup) null);
        establishInitialState();
        return this.mContentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(DialogInterface dialogInterface) {
        getActivity().finish();
    }
}
