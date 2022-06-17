package com.motorola.settings.tether;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.slice.WifiScanWorker;

public class WifiHotspotConflictWarningDialogActivity extends Activity {
    /* access modifiers changed from: private */
    public WifiManager mWifiManager;
    private final WarningDialogCallback warningDialogCallback = new WarningDialogCallback() {
        public void onPositive() {
            WifiHotspotConflictWarningDialogActivity.this.mWifiManager.setWifiEnabled(true);
            WifiHotspotConflictWarningDialogActivity.this.finish();
        }

        public void onNegative() {
            WifiHotspotConflictWarningDialogActivity wifiHotspotConflictWarningDialogActivity = WifiHotspotConflictWarningDialogActivity.this;
            WifiScanWorker wifiScanWorker = wifiHotspotConflictWarningDialogActivity.getWifiScanWorker(wifiHotspotConflictWarningDialogActivity.getIntent());
            if (wifiScanWorker != null) {
                wifiScanWorker.onWifiStateChanged();
            }
            WifiHotspotConflictWarningDialogActivity.this.finish();
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        if (WifiHotspotConflictWarningDialog.isConflictingWithHotspot(this)) {
            WifiHotspotConflictWarningDialog.show(getFragmentManager(), this.warningDialogCallback);
            return;
        }
        this.mWifiManager.setWifiEnabled(true);
        finish();
    }

    /* access modifiers changed from: package-private */
    public WifiScanWorker getWifiScanWorker(Intent intent) {
        return (WifiScanWorker) SliceBackgroundWorker.getInstance((Uri) intent.getParcelableExtra("key_provider_model_slice_uri"));
    }
}
