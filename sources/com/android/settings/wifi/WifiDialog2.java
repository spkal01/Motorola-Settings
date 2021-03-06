package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.wifitrackerlib.WifiEntry;

public class WifiDialog2 extends AlertDialog implements WifiConfigUiBase2, DialogInterface.OnClickListener {
    private WifiConfigController2 mController;
    private boolean mHideSubmitButton;
    private final WifiDialog2Listener mListener;
    private final int mMode;
    private View mView;
    private final WifiEntry mWifiEntry;

    public interface WifiDialog2Listener {
        void onForget(WifiDialog2 wifiDialog2) {
        }

        void onScan(WifiDialog2 wifiDialog2, String str) {
        }

        void onSubmit(WifiDialog2 wifiDialog2) {
        }
    }

    public static WifiDialog2 createModal(Context context, WifiDialog2Listener wifiDialog2Listener, WifiEntry wifiEntry, int i) {
        return new WifiDialog2(context, wifiDialog2Listener, wifiEntry, i, 0, i == 0);
    }

    public static WifiDialog2 createModal(Context context, WifiDialog2Listener wifiDialog2Listener, WifiEntry wifiEntry, int i, int i2) {
        return new WifiDialog2(context, wifiDialog2Listener, wifiEntry, i, i2, i == 0);
    }

    WifiDialog2(Context context, WifiDialog2Listener wifiDialog2Listener, WifiEntry wifiEntry, int i, int i2, boolean z) {
        super(context, i2);
        this.mMode = i;
        this.mListener = wifiDialog2Listener;
        this.mWifiEntry = wifiEntry;
        this.mHideSubmitButton = z;
    }

    public WifiConfigController2 getController() {
        return this.mController;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(C1987R$layout.wifi_dialog, (ViewGroup) null);
        this.mView = inflate;
        setView(inflate);
        this.mController = new WifiConfigController2(this, this.mView, this.mWifiEntry, this.mMode);
        super.onCreate(bundle);
        if (this.mHideSubmitButton) {
            this.mController.hideSubmitButton();
        } else {
            this.mController.enableSubmitIfAppropriate();
        }
        if (this.mWifiEntry == null) {
            this.mController.hideForgetButton();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        ImageButton imageButton = (ImageButton) findViewById(C1985R$id.ssid_scanner_button);
        if (this.mHideSubmitButton) {
            imageButton.setVisibility(8);
        } else {
            imageButton.setOnClickListener(new WifiDialog2$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(View view) {
        if (this.mListener != null) {
            this.mListener.onScan(this, ((TextView) findViewById(C1985R$id.ssid)).getText().toString());
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mController.updatePassword();
    }

    public void dispatchSubmit() {
        WifiDialog2Listener wifiDialog2Listener = this.mListener;
        if (wifiDialog2Listener != null) {
            wifiDialog2Listener.onSubmit(this);
        }
        dismiss();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        WifiDialog2Listener wifiDialog2Listener = this.mListener;
        if (wifiDialog2Listener == null) {
            return;
        }
        if (i != -3) {
            if (i == -1) {
                wifiDialog2Listener.onSubmit(this);
            }
        } else if (WifiUtils.isNetworkLockedDown(getContext(), this.mWifiEntry.getWifiConfiguration())) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), RestrictedLockUtilsInternal.getDeviceOwner(getContext()));
        } else {
            this.mListener.onForget(this);
        }
    }

    public int getMode() {
        return this.mMode;
    }

    public Button getSubmitButton() {
        return getButton(-1);
    }

    public Button getForgetButton() {
        return getButton(-3);
    }

    public void setSubmitButton(CharSequence charSequence) {
        setButton(-1, charSequence, this);
    }

    public void setForgetButton(CharSequence charSequence) {
        setButton(-3, charSequence, this);
    }

    public void setCancelButton(CharSequence charSequence) {
        setButton(-2, charSequence, this);
    }

    public WifiEntry getWifiEntry() {
        return this.mWifiEntry;
    }
}
