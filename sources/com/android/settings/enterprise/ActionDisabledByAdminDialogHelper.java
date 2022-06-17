package com.android.settings.enterprise;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Process;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.enterprise.ActionDisabledByAdminController;
import com.android.settingslib.enterprise.ActionDisabledByAdminControllerFactory;
import java.util.Objects;

public final class ActionDisabledByAdminDialogHelper {
    private final ActionDisabledByAdminController mActionDisabledByAdminController;
    private final Activity mActivity;
    private ViewGroup mDialogView;
    RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private String mRestriction;

    public ActionDisabledByAdminDialogHelper(Activity activity) {
        this(activity, (String) null);
    }

    public ActionDisabledByAdminDialogHelper(Activity activity, String str) {
        this.mActivity = activity;
        this.mDialogView = (ViewGroup) LayoutInflater.from(activity).inflate(C1987R$layout.admin_support_details_dialog, (ViewGroup) null);
        this.mActionDisabledByAdminController = ActionDisabledByAdminControllerFactory.createInstance(activity, str, new DeviceAdminStringProviderImpl(activity), UserHandle.SYSTEM);
    }

    private int getEnforcementAdminUserId(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        UserHandle userHandle = enforcedAdmin.user;
        if (userHandle == null) {
            return -10000;
        }
        return userHandle.getIdentifier();
    }

    private int getEnforcementAdminUserId() {
        return getEnforcementAdminUserId(this.mEnforcedAdmin);
    }

    public AlertDialog.Builder prepareDialogBuilder(String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        AlertDialog.Builder view = new AlertDialog.Builder(this.mActivity).setPositiveButton(C1992R$string.okay, this.mActionDisabledByAdminController.getPositiveButtonListener(this.mActivity, enforcedAdmin)).setView((View) this.mDialogView);
        prepareDialogBuilder(view, str, enforcedAdmin);
        return view;
    }

    /* access modifiers changed from: package-private */
    public void prepareDialogBuilder(AlertDialog.Builder builder, String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mActionDisabledByAdminController.initialize(new ActionDisabledLearnMoreButtonLauncherImpl(this.mActivity, builder));
        this.mEnforcedAdmin = enforcedAdmin;
        this.mRestriction = str;
        initializeDialogViews(this.mDialogView, enforcedAdmin, getEnforcementAdminUserId(), this.mRestriction);
        this.mActionDisabledByAdminController.setupLearnMoreButton(this.mActivity);
    }

    public void updateDialog(String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (!this.mEnforcedAdmin.equals(enforcedAdmin) || !Objects.equals(this.mRestriction, str)) {
            this.mEnforcedAdmin = enforcedAdmin;
            this.mRestriction = str;
            initializeDialogViews(this.mDialogView, enforcedAdmin, getEnforcementAdminUserId(), this.mRestriction);
        }
    }

    private void initializeDialogViews(View view, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, int i, String str) {
        ComponentName componentName = enforcedAdmin.component;
        if (componentName != null) {
            this.mActionDisabledByAdminController.updateEnforcedAdmin(enforcedAdmin, i);
            setAdminSupportIcon(view, componentName, i);
            UserHandle userHandle = null;
            if (isNotCurrentUserOrProfile(componentName, i)) {
                componentName = null;
            }
            setAdminSupportTitle(view, str);
            if (i != -10000) {
                userHandle = UserHandle.of(i);
            }
            setAdminSupportDetails(this.mActivity, view, new RestrictedLockUtils.EnforcedAdmin(componentName, userHandle));
        }
    }

    private boolean isNotCurrentUserOrProfile(ComponentName componentName, int i) {
        return !RestrictedLockUtilsInternal.isAdminInCurrentUserOrProfile(this.mActivity, componentName) || !RestrictedLockUtils.isCurrentUserOrProfile(this.mActivity, i);
    }

    /* access modifiers changed from: package-private */
    public void setAdminSupportIcon(View view, ComponentName componentName, int i) {
        ImageView imageView = (ImageView) view.requireViewById(C1985R$id.admin_support_icon);
        imageView.setImageDrawable(this.mActivity.getDrawable(C1983R$drawable.ic_lock_closed));
        imageView.setImageTintList(Utils.getColorAccent(this.mActivity));
    }

    /* access modifiers changed from: package-private */
    public void setAdminSupportTitle(View view, String str) {
        TextView textView = (TextView) view.findViewById(C1985R$id.admin_support_dialog_title);
        if (textView != null) {
            textView.setText(this.mActionDisabledByAdminController.getAdminSupportTitle(str));
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdminSupportDetails(Activity activity, View view, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (enforcedAdmin != null && enforcedAdmin.component != null) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService("device_policy");
            CharSequence charSequence = null;
            if (!RestrictedLockUtilsInternal.isAdminInCurrentUserOrProfile(activity, enforcedAdmin.component) || !RestrictedLockUtils.isCurrentUserOrProfile(activity, getEnforcementAdminUserId(enforcedAdmin))) {
                enforcedAdmin.component = null;
            } else {
                if (enforcedAdmin.user == null) {
                    enforcedAdmin.user = UserHandle.of(UserHandle.myUserId());
                }
                if (UserHandle.isSameApp(Process.myUid(), 1000)) {
                    charSequence = devicePolicyManager.getShortSupportMessageForUser(enforcedAdmin.component, getEnforcementAdminUserId(enforcedAdmin));
                }
            }
            CharSequence adminSupportContentString = this.mActionDisabledByAdminController.getAdminSupportContentString(this.mActivity, charSequence);
            TextView textView = (TextView) view.findViewById(C1985R$id.admin_support_msg);
            if (adminSupportContentString != null) {
                textView.setText(adminSupportContentString);
            }
        }
    }
}
