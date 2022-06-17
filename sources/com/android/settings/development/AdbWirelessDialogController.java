package com.android.settings.development;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;

public class AdbWirelessDialogController {
    private Context mContext;
    private TextView mFailedMsg;
    private TextView mIpAddr;
    private int mMode;
    private TextView mSixDigitCode;
    private final AdbWirelessDialogUiBase mUi;
    private final View mView;

    public AdbWirelessDialogController(AdbWirelessDialogUiBase adbWirelessDialogUiBase, View view, int i) {
        this.mUi = adbWirelessDialogUiBase;
        this.mView = view;
        this.mMode = i;
        Context context = adbWirelessDialogUiBase.getContext();
        this.mContext = context;
        Resources resources = context.getResources();
        this.mSixDigitCode = (TextView) view.findViewById(C1985R$id.pairing_code);
        this.mIpAddr = (TextView) view.findViewById(C1985R$id.ip_addr);
        int i2 = this.mMode;
        if (i2 == 0) {
            adbWirelessDialogUiBase.setTitle((CharSequence) resources.getString(C1992R$string.adb_pairing_device_dialog_title));
            view.findViewById(C1985R$id.l_pairing_six_digit).setVisibility(0);
            adbWirelessDialogUiBase.setCancelButton(resources.getString(C1992R$string.cancel));
            adbWirelessDialogUiBase.setCanceledOnTouchOutside(false);
        } else if (i2 == 2) {
            String string = resources.getString(C1992R$string.adb_pairing_device_dialog_failed_msg);
            adbWirelessDialogUiBase.setTitle(C1992R$string.adb_pairing_device_dialog_failed_title);
            view.findViewById(C1985R$id.l_pairing_failed).setVisibility(0);
            TextView textView = (TextView) view.findViewById(C1985R$id.pairing_failed_label);
            this.mFailedMsg = textView;
            textView.setText(string);
            adbWirelessDialogUiBase.setSubmitButton(resources.getString(C1992R$string.okay));
        } else if (i2 == 3) {
            adbWirelessDialogUiBase.setTitle(C1992R$string.adb_pairing_device_dialog_failed_title);
            view.findViewById(C1985R$id.l_qrcode_pairing_failed).setVisibility(0);
            adbWirelessDialogUiBase.setSubmitButton(resources.getString(C1992R$string.okay));
        }
        view.findViewById(C1985R$id.l_adbwirelessdialog).requestFocus();
    }

    public void setPairingCode(String str) {
        this.mSixDigitCode.setText(str);
    }

    public void setIpAddr(String str) {
        this.mIpAddr.setText(str);
    }
}
