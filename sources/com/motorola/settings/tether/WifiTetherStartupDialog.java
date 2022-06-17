package com.motorola.settings.tether;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;

public class WifiTetherStartupDialog extends DialogFragment {
    private static final String TAG = WifiTetherStartupDialog.class.getSimpleName();
    private WarningDialogCallback mCallback;

    public static void show(FragmentManager fragmentManager, WarningDialogCallback warningDialogCallback) {
        WifiTetherStartupDialog wifiTetherStartupDialog = new WifiTetherStartupDialog();
        wifiTetherStartupDialog.setCallback(warningDialogCallback);
        wifiTetherStartupDialog.setCancelable(false);
        wifiTetherStartupDialog.setRetainInstance(true);
        wifiTetherStartupDialog.show(fragmentManager, TAG);
    }

    public void setCallback(WarningDialogCallback warningDialogCallback) {
        this.mCallback = warningDialogCallback;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        View inflate = View.inflate(getContext(), C1987R$layout.wifi_ap_startup_warning, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C1985R$id.TextView01);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(getDialogMessageText());
        return new AlertDialog.Builder(getContext()).setTitle(C1992R$string.wifi_ap_startup_title).setView(inflate).setPositiveButton(17039370, (DialogInterface.OnClickListener) new WifiTetherStartupDialog$$ExternalSyntheticLambda1(this, (CheckBox) inflate.findViewById(C1985R$id.checkbox_warn_again))).setNegativeButton(17039360, (DialogInterface.OnClickListener) new WifiTetherStartupDialog$$ExternalSyntheticLambda0(this)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(CheckBox checkBox, DialogInterface dialogInterface, int i) {
        if (checkBox.isChecked()) {
            TetherStartupDialogUtils.setDoNotShowAgain(getContext());
        }
        if (HotspotRoamingWarningDialog.shouldShow(getContext())) {
            HotspotRoamingWarningDialog.show(getFragmentManager(), this.mCallback);
            return;
        }
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onPositive();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        WarningDialogCallback warningDialogCallback = this.mCallback;
        if (warningDialogCallback != null) {
            warningDialogCallback.onNegative();
        }
    }

    private String getDialogMessageText() {
        return getString(C1992R$string.wifi_ap_startup_warning_text, new Object[]{HotspotWifiConflictWarningDialog.shouldShow(getContext()) ? getString(C1992R$string.wifi_ap_startup_warning_subtext_wifi_will_be_turned_off) : "", ((WifiManager) getContext().getSystemService(WifiManager.class)).getSoftApConfiguration().getSsid()});
    }

    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage((Message) null);
        }
        super.onDestroyView();
    }
}
