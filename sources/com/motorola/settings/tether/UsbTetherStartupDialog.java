package com.motorola.settings.tether;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;

public class UsbTetherStartupDialog extends DialogFragment {
    private static final String TAG = UsbTetherStartupDialog.class.getSimpleName();
    private WarningDialogCallback mCallback;

    public static UsbTetherStartupDialog showDialog(FragmentManager fragmentManager, WarningDialogCallback warningDialogCallback) {
        UsbTetherStartupDialog usbTetherStartupDialog = new UsbTetherStartupDialog();
        usbTetherStartupDialog.setCallback(warningDialogCallback);
        usbTetherStartupDialog.setCancelable(false);
        usbTetherStartupDialog.show(fragmentManager, TAG);
        return usbTetherStartupDialog;
    }

    public void setCallback(WarningDialogCallback warningDialogCallback) {
        this.mCallback = warningDialogCallback;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        View inflate = View.inflate(getContext(), C1987R$layout.wifi_ap_startup_warning, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C1985R$id.TextView01);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(String.format(getString(C1992R$string.usb_startup_warning_text), new Object[]{""}));
        CheckBox checkBox = (CheckBox) inflate.findViewById(C1985R$id.checkbox_warn_again);
        checkBox.setChecked(TetherStartupDialogUtils.getCheckBoxCheckedFromSharedPrefs(getContext()));
        checkBox.setOnCheckedChangeListener(new UsbTetherStartupDialog$$ExternalSyntheticLambda2(this));
        return new AlertDialog.Builder(getContext()).setTitle(C1992R$string.usb_startup_title).setView(inflate).setPositiveButton(17039370, new UsbTetherStartupDialog$$ExternalSyntheticLambda1(this, checkBox)).setNegativeButton(17039360, new UsbTetherStartupDialog$$ExternalSyntheticLambda0(this)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(CompoundButton compoundButton, boolean z) {
        TetherStartupDialogUtils.setCheckBoxCheckedOnSharedPrefs(getContext(), z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(CheckBox checkBox, DialogInterface dialogInterface, int i) {
        if (checkBox.isChecked()) {
            TetherStartupDialogUtils.setDoNotShowAgain(getContext());
        }
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onPositive();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$2(DialogInterface dialogInterface, int i) {
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onNegative();
        }
    }
}
