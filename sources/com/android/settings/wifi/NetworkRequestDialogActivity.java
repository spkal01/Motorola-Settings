package com.android.settings.wifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C1992R$string;
import com.android.settings.wifi.NetworkRequestErrorDialogFragment;
import com.android.wifitrackerlib.WifiEntry;
import java.util.List;

public class NetworkRequestDialogActivity extends FragmentActivity implements WifiManager.NetworkRequestMatchCallback {
    private static String TAG = "NetworkRequestDialogActivity";
    NetworkRequestDialogBaseFragment mDialogFragment;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 0) {
                removeMessages(0);
                NetworkRequestDialogActivity.this.stopScanningAndPopErrorDialog(NetworkRequestErrorDialogFragment.ERROR_DIALOG_TYPE.TIME_OUT);
            }
        }
    };
    private boolean mIsSpecifiedSsid;
    private WifiConfiguration mMatchedConfig;
    ProgressDialog mProgressDialog;
    private boolean mShowingErrorDialog;
    private WifiManager.NetworkRequestUserSelectionCallback mUserSelectionCallback;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            this.mIsSpecifiedSsid = intent.getBooleanExtra("com.android.settings.wifi.extra.REQUEST_IS_FOR_SINGLE_NETWORK", false);
        }
        if (this.mIsSpecifiedSsid) {
            showProgressDialog(getString(C1992R$string.network_connection_searching_message));
            return;
        }
        NetworkRequestDialogFragment newInstance = NetworkRequestDialogFragment.newInstance();
        this.mDialogFragment = newInstance;
        newInstance.show(getSupportFragmentManager(), TAG);
    }

    private void showProgressDialog(String str) {
        dismissDialogs();
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.mProgressDialog = progressDialog;
        progressDialog.setIndeterminate(true);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.setMessage(str);
        this.mProgressDialog.show();
    }

    private void showSingleSsidRequestDialog(String str, boolean z) {
        dismissDialogs();
        this.mDialogFragment = new NetworkRequestSingleSsidDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_REQUEST_SSID", str);
        bundle.putBoolean("DIALOG_IS_TRYAGAIN", z);
        this.mDialogFragment.setArguments(bundle);
        if (!getSupportFragmentManager().isStateSaved()) {
            this.mDialogFragment.show(getSupportFragmentManager(), TAG);
        }
    }

    private void dismissDialogs() {
        NetworkRequestDialogBaseFragment networkRequestDialogBaseFragment = this.mDialogFragment;
        if (networkRequestDialogBaseFragment != null) {
            networkRequestDialogBaseFragment.dismiss();
            this.mDialogFragment = null;
        }
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        if (wifiManager != null) {
            wifiManager.registerNetworkRequestMatchCallback(new HandlerExecutor(this.mHandler), this);
        }
        this.mHandler.sendEmptyMessageDelayed(0, 30000);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        this.mHandler.removeMessages(0);
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        if (wifiManager != null) {
            wifiManager.unregisterNetworkRequestMatchCallback(this);
        }
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void stopScanningAndPopErrorDialog(NetworkRequestErrorDialogFragment.ERROR_DIALOG_TYPE error_dialog_type) {
        dismissDialogs();
        if (!this.mShowingErrorDialog) {
            NetworkRequestErrorDialogFragment newInstance = NetworkRequestErrorDialogFragment.newInstance();
            newInstance.setRejectCallback(this.mUserSelectionCallback);
            Bundle bundle = new Bundle();
            bundle.putSerializable("DIALOG_ERROR_TYPE", error_dialog_type);
            newInstance.setArguments(bundle);
            if (!getSupportFragmentManager().isStateSaved()) {
                newInstance.show(getSupportFragmentManager(), TAG);
            }
            this.mShowingErrorDialog = true;
        }
    }

    public void onUserSelectionCallbackRegistration(WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback) {
        if (this.mIsSpecifiedSsid) {
            this.mUserSelectionCallback = networkRequestUserSelectionCallback;
        } else if (!this.mShowingErrorDialog) {
            this.mDialogFragment.onUserSelectionCallbackRegistration(networkRequestUserSelectionCallback);
        }
    }

    public void onAbort() {
        stopScanningAndPopErrorDialog(NetworkRequestErrorDialogFragment.ERROR_DIALOG_TYPE.ABORT);
    }

    public void onMatch(List<ScanResult> list) {
        if (!this.mShowingErrorDialog) {
            this.mHandler.removeMessages(0);
            if (!this.mIsSpecifiedSsid) {
                this.mDialogFragment.onMatch(list);
            } else if (this.mMatchedConfig == null) {
                WifiConfiguration wifiConfig = WifiUtils.getWifiConfig((WifiEntry) null, list.get(0));
                this.mMatchedConfig = wifiConfig;
                showSingleSsidRequestDialog(WifiInfo.sanitizeSsid(wifiConfig.SSID), false);
            }
        }
    }

    public void onUserSelectionConnectSuccess(WifiConfiguration wifiConfiguration) {
        if (!isFinishing()) {
            Toast.makeText(this, C1992R$string.network_connection_connect_successful, 0).show();
            setResult(-1);
            finish();
        }
    }

    public void onUserSelectionConnectFailure(WifiConfiguration wifiConfiguration) {
        if (!isFinishing()) {
            Toast.makeText(this, C1992R$string.network_connection_connect_failure, 0).show();
            setResult(-1);
            finish();
        }
    }

    public void onClickConnectButton() {
        WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback = this.mUserSelectionCallback;
        if (networkRequestUserSelectionCallback != null) {
            networkRequestUserSelectionCallback.select(this.mMatchedConfig);
            showProgressDialog(getString(C1992R$string.network_connection_connecting_message));
        }
    }

    public void onClickRescanButton() {
        this.mHandler.sendEmptyMessageDelayed(0, 30000);
        this.mShowingErrorDialog = false;
        if (this.mIsSpecifiedSsid) {
            this.mMatchedConfig = null;
            showProgressDialog(getString(C1992R$string.network_connection_searching_message));
            return;
        }
        NetworkRequestDialogFragment newInstance = NetworkRequestDialogFragment.newInstance();
        this.mDialogFragment = newInstance;
        newInstance.show(getSupportFragmentManager(), TAG);
    }

    public void onCancel() {
        dismissDialogs();
        WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback = this.mUserSelectionCallback;
        if (networkRequestUserSelectionCallback != null) {
            networkRequestUserSelectionCallback.reject();
        }
        finish();
    }
}
