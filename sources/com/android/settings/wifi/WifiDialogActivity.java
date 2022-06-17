package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import com.android.settings.C1993R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.WifiDialog;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.core.lifecycle.ObservableActivity;
import com.android.settingslib.wifi.AccessPoint;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiEntry;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
import java.time.ZoneOffset;

public class WifiDialogActivity extends ObservableActivity implements WifiDialog.WifiDialogListener, WifiDialog2.WifiDialog2Listener, DialogInterface.OnDismissListener {
    static final String KEY_CONNECT_FOR_CALLER = "connect_for_caller";
    static final int REQUEST_CODE_WIFI_DPP_ENROLLEE_QR_CODE_SCANNER = 0;
    static final int RESULT_CONNECTED = 1;
    private AccessPoint mAccessPoint;
    private WifiDialog mDialog;
    private WifiDialog2 mDialog2;
    private Intent mIntent;
    private boolean mIsWifiTrackerLib;
    private NetworkDetailsTracker mNetworkDetailsTracker;
    private HandlerThread mWorkerThread;

    /* JADX WARNING: type inference failed for: r8v0, types: [java.time.Clock, com.android.settings.wifi.WifiDialogActivity$1] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Intent intent = getIntent();
        this.mIntent = intent;
        if (WizardManagerHelper.isSetupWizardIntent(intent)) {
            setTheme(SetupWizardUtils.getTransparentTheme(this, this.mIntent));
        }
        super.onCreate(bundle);
        boolean z = !TextUtils.isEmpty(this.mIntent.getStringExtra("key_chosen_wifientry_key"));
        this.mIsWifiTrackerLib = z;
        if (z) {
            HandlerThread handlerThread = new HandlerThread("WifiDialogActivity{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
            this.mWorkerThread = handlerThread;
            handlerThread.start();
            this.mNetworkDetailsTracker = FeatureFactory.getFactory(this).getWifiTrackerLibProvider().createNetworkDetailsTracker(getLifecycle(), this, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) {
                public long millis() {
                    return SystemClock.elapsedRealtime();
                }
            }, 15000, 10000, this.mIntent.getStringExtra("key_chosen_wifientry_key"));
            return;
        }
        Bundle bundleExtra = this.mIntent.getBundleExtra("access_point_state");
        if (bundleExtra != null) {
            this.mAccessPoint = new AccessPoint((Context) this, bundleExtra);
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        int i;
        super.onStart();
        if (this.mDialog2 == null && this.mDialog == null) {
            if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
                if (ThemeHelper.isSetupWizardDayNightEnabled(this)) {
                    i = C1993R$style.SuwAlertDialogThemeCompat_DayNight;
                } else {
                    i = C1993R$style.SuwAlertDialogThemeCompat_Light;
                }
                if (this.mIsWifiTrackerLib) {
                    this.mDialog2 = WifiDialog2.createModal(this, this, this.mNetworkDetailsTracker.getWifiEntry(), 1, i);
                } else {
                    this.mDialog = WifiDialog.createModal(this, this, this.mAccessPoint, 1, i);
                }
            } else if (this.mIsWifiTrackerLib) {
                this.mDialog2 = WifiDialog2.createModal(this, this, this.mNetworkDetailsTracker.getWifiEntry(), 1);
            } else {
                this.mDialog = WifiDialog.createModal(this, this, this.mAccessPoint, 1);
            }
            if (this.mIsWifiTrackerLib) {
                this.mDialog2.show();
                this.mDialog2.setOnDismissListener(this);
                return;
            }
            this.mDialog.show();
            this.mDialog.setOnDismissListener(this);
        }
    }

    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }

    public void onDestroy() {
        if (this.mIsWifiTrackerLib) {
            WifiDialog2 wifiDialog2 = this.mDialog2;
            if (wifiDialog2 != null && wifiDialog2.isShowing()) {
                this.mDialog2.dismiss();
                this.mDialog2 = null;
            }
            this.mWorkerThread.quit();
        } else {
            WifiDialog wifiDialog = this.mDialog;
            if (wifiDialog != null && wifiDialog.isShowing()) {
                this.mDialog.dismiss();
                this.mDialog = null;
            }
        }
        super.onDestroy();
    }

    public void onForget(WifiDialog2 wifiDialog2) {
        WifiEntry wifiEntry = wifiDialog2.getController().getWifiEntry();
        if (wifiEntry != null && wifiEntry.canForget()) {
            wifiEntry.forget((WifiEntry.ForgetCallback) null);
        }
        setResult(2);
        finish();
    }

    public void onForget(WifiDialog wifiDialog) {
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        AccessPoint accessPoint = wifiDialog.getController().getAccessPoint();
        if (accessPoint != null) {
            if (accessPoint.isSaved()) {
                wifiManager.forget(accessPoint.getConfig().networkId, (WifiManager.ActionListener) null);
            } else if (accessPoint.getNetworkInfo() == null || accessPoint.getNetworkInfo().getState() == NetworkInfo.State.DISCONNECTED) {
                Log.e("WifiDialogActivity", "Failed to forget invalid network " + accessPoint.getConfig());
            } else {
                wifiManager.disableEphemeralNetwork(AccessPoint.convertToQuotedString(accessPoint.getSsidStr()));
            }
        }
        Intent intent = new Intent();
        if (accessPoint != null) {
            Bundle bundle = new Bundle();
            accessPoint.saveWifiState(bundle);
            intent.putExtra("access_point_state", bundle);
        }
        setResult(2);
        finish();
    }

    public void onSubmit(WifiDialog2 wifiDialog2) {
        WifiEntry wifiEntry = wifiDialog2.getController().getWifiEntry();
        WifiConfiguration config = wifiDialog2.getController().getConfig();
        Intent intent = null;
        if (getIntent().getBooleanExtra(KEY_CONNECT_FOR_CALLER, true)) {
            if (config != null || wifiEntry == null || !wifiEntry.canConnect()) {
                ((WifiManager) getSystemService(WifiManager.class)).connect(config, (WifiManager.ActionListener) null);
            } else {
                wifiEntry.connect((WifiEntry.ConnectCallback) null);
            }
        }
        if (hasPermissionForResult()) {
            intent = createResultData(config, (AccessPoint) null);
        }
        setResult(1, intent);
        finish();
    }

    public void onSubmit(WifiDialog wifiDialog) {
        NetworkInfo networkInfo;
        WifiConfiguration config = wifiDialog.getController().getConfig();
        AccessPoint accessPoint = wifiDialog.getController().getAccessPoint();
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        Intent intent = null;
        if (getIntent().getBooleanExtra(KEY_CONNECT_FOR_CALLER, true)) {
            if (config != null) {
                wifiManager.save(config, (WifiManager.ActionListener) null);
                if (accessPoint != null && ((networkInfo = accessPoint.getNetworkInfo()) == null || !networkInfo.isConnected())) {
                    wifiManager.connect(config, (WifiManager.ActionListener) null);
                }
            } else if (accessPoint != null && accessPoint.isSaved()) {
                wifiManager.connect(accessPoint.getConfig(), (WifiManager.ActionListener) null);
            }
        }
        if (hasPermissionForResult()) {
            intent = createResultData(config, accessPoint);
        }
        setResult(1, intent);
        finish();
    }

    /* access modifiers changed from: protected */
    public Intent createResultData(WifiConfiguration wifiConfiguration, AccessPoint accessPoint) {
        Intent intent = new Intent();
        if (accessPoint != null) {
            Bundle bundle = new Bundle();
            accessPoint.saveWifiState(bundle);
            intent.putExtra("access_point_state", bundle);
        }
        if (wifiConfiguration != null) {
            intent.putExtra("wifi_configuration", wifiConfiguration);
        }
        return intent;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog2 = null;
        this.mDialog = null;
        finish();
    }

    public void onScan(WifiDialog2 wifiDialog2, String str) {
        Intent enrolleeQrCodeScannerIntent = WifiDppUtils.getEnrolleeQrCodeScannerIntent(str);
        WizardManagerHelper.copyWizardManagerExtras(this.mIntent, enrolleeQrCodeScannerIntent);
        startActivityForResult(enrolleeQrCodeScannerIntent, 0);
    }

    public void onScan(WifiDialog wifiDialog, String str) {
        Intent enrolleeQrCodeScannerIntent = WifiDppUtils.getEnrolleeQrCodeScannerIntent(str);
        WizardManagerHelper.copyWizardManagerExtras(this.mIntent, enrolleeQrCodeScannerIntent);
        startActivityForResult(enrolleeQrCodeScannerIntent, 0);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0 && i2 == -1) {
            if (hasPermissionForResult()) {
                setResult(1, intent);
            } else {
                setResult(1);
            }
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasPermissionForResult() {
        String callingPackage = getCallingPackage();
        if (callingPackage == null) {
            Log.d("WifiDialogActivity", "Failed to get the calling package, don't return the result.");
            EventLog.writeEvent(1397638484, new Object[]{"185126813", -1, "no calling package"});
            return false;
        } else if (getPackageManager().checkPermission("android.permission.ACCESS_COARSE_LOCATION", callingPackage) == 0) {
            Log.d("WifiDialogActivity", "The calling package has ACCESS_COARSE_LOCATION permission for result.");
            return true;
        } else if (getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", callingPackage) == 0) {
            Log.d("WifiDialogActivity", "The calling package has ACCESS_FINE_LOCATION permission for result.");
            return true;
        } else {
            Log.d("WifiDialogActivity", "The calling package does not have the necessary permissions for result.");
            try {
                EventLog.writeEvent(1397638484, new Object[]{"185126813", Integer.valueOf(getPackageManager().getPackageUid(callingPackage, 0)), "no permission"});
            } catch (PackageManager.NameNotFoundException e) {
                EventLog.writeEvent(1397638484, new Object[]{"185126813", -1, "no permission"});
                Log.w("WifiDialogActivity", "Cannot find the UID, calling package: " + callingPackage, e);
            }
            return false;
        }
    }
}
