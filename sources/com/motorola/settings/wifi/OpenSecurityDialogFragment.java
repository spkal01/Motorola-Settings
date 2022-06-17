package com.motorola.settings.wifi;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;

public class OpenSecurityDialogFragment extends DialogFragment {
    private static final String TAG = OpenSecurityDialogFragment.class.getSimpleName();
    private DialogInterface.OnClickListener mListener;

    public static void show(FragmentManager fragmentManager, DialogInterface.OnClickListener onClickListener) {
        OpenSecurityDialogFragment openSecurityDialogFragment = new OpenSecurityDialogFragment();
        openSecurityDialogFragment.setCancelable(false);
        openSecurityDialogFragment.setRetainInstance(true);
        openSecurityDialogFragment.setListener(onClickListener);
        openSecurityDialogFragment.show(fragmentManager, TAG);
    }

    private void setListener(DialogInterface.OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    private int getTitleResId() {
        if (getContext().getResources().getBoolean(C1980R$bool.droid_wifi_ap_settings)) {
            return C1992R$string.SecTypeWarningAlertDlg_title;
        }
        return C1992R$string.open_security_warning_title;
    }

    private int getMessageResId() {
        if (getContext().getResources().getBoolean(C1980R$bool.droid_wifi_ap_settings)) {
            return C1992R$string.SecTypeWarningAlertDlg_message;
        }
        return C1992R$string.open_security_warning_msg;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getContext()).setTitle(getTitleResId()).setMessage(getMessageResId()).setPositiveButton(17039370, this.mListener).setNegativeButton(17039360, this.mListener).create();
    }
}
