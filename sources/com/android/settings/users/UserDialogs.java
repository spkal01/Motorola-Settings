package com.android.settings.users;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settingslib.R$string;

public final class UserDialogs {
    public static Dialog createRemoveDialog(Context context, int i, DialogInterface.OnClickListener onClickListener) {
        UserInfo userInfo = ((UserManager) context.getSystemService("user")).getUserInfo(i);
        AlertDialog.Builder negativeButton = new AlertDialog.Builder(context).setPositiveButton(C1992R$string.user_delete_button, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        if (userInfo.isManagedProfile()) {
            negativeButton.setTitle(C1992R$string.work_profile_confirm_remove_title);
            View createRemoveManagedUserDialogView = createRemoveManagedUserDialogView(context, i);
            if (createRemoveManagedUserDialogView != null) {
                negativeButton.setView(createRemoveManagedUserDialogView);
            } else {
                negativeButton.setMessage(C1992R$string.work_profile_confirm_remove_message);
            }
        } else if (UserHandle.myUserId() == i) {
            negativeButton.setTitle(C1992R$string.user_confirm_remove_self_title);
            negativeButton.setMessage(C1992R$string.user_confirm_remove_self_message);
        } else if (userInfo.isRestricted()) {
            negativeButton.setTitle(C1992R$string.user_profile_confirm_remove_title);
            negativeButton.setMessage(C1992R$string.user_profile_confirm_remove_message);
        } else {
            negativeButton.setTitle(C1992R$string.user_confirm_remove_title);
            negativeButton.setMessage(C1992R$string.user_confirm_remove_message);
        }
        return negativeButton.create();
    }

    private static View createRemoveManagedUserDialogView(Context context, int i) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo adminApplicationInfo = Utils.getAdminApplicationInfo(context, i);
        if (adminApplicationInfo == null) {
            return null;
        }
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C1987R$layout.delete_managed_profile_dialog, (ViewGroup) null);
        ((ImageView) inflate.findViewById(C1985R$id.delete_managed_profile_mdm_icon_view)).setImageDrawable(packageManager.getApplicationIcon(adminApplicationInfo));
        CharSequence applicationLabel = packageManager.getApplicationLabel(adminApplicationInfo);
        CharSequence userBadgedLabel = packageManager.getUserBadgedLabel(applicationLabel, new UserHandle(i));
        TextView textView = (TextView) inflate.findViewById(C1985R$id.delete_managed_profile_device_manager_name);
        textView.setText(applicationLabel);
        if (!applicationLabel.toString().contentEquals(userBadgedLabel)) {
            textView.setContentDescription(userBadgedLabel);
        }
        return inflate;
    }

    public static Dialog createEnablePhoneCallsAndSmsDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(C1992R$string.user_enable_calling_and_sms_confirm_title).setMessage(C1992R$string.user_enable_calling_and_sms_confirm_message).setPositiveButton(C1992R$string.okay, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    public static Dialog createEnablePhoneCallsDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(C1992R$string.user_enable_calling_confirm_title).setMessage(C1992R$string.user_enable_calling_confirm_message).setPositiveButton(C1992R$string.okay, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    public static Dialog createSetupUserDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(R$string.user_setup_dialog_title).setMessage(R$string.user_setup_dialog_message).setPositiveButton(R$string.user_setup_button_setup_now, onClickListener).setNegativeButton(R$string.user_setup_button_setup_later, (DialogInterface.OnClickListener) null).create();
    }

    public static Dialog createResetGuestDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(R$string.guest_reset_guest_dialog_title).setMessage(C1992R$string.user_exit_guest_confirm_message).setPositiveButton(R$string.guest_reset_guest_confirm_button, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }
}
