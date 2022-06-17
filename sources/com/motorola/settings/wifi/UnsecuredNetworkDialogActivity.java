package com.motorola.settings.wifi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.slice.WifiScanWorker;
import com.android.wifitrackerlib.WifiEntry;

public class UnsecuredNetworkDialogActivity extends Activity {
    private static final String TAG = "UnsecuredNetworkDialogActivity";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra("key_chosen_wifientry_key");
        WifiScanWorker wifiScanWorker = getWifiScanWorker(getIntent());
        if (wifiScanWorker != null) {
            WifiEntry wifiEntry = wifiScanWorker.getWifiEntry(stringExtra);
            if (wifiEntry != null) {
                UnsecuredNetworkDialog.show(this, wifiEntry.getSsid(), new UnsecuredNetworkDialogActivity$$ExternalSyntheticLambda1(this, wifiEntry), new UnsecuredNetworkDialogActivity$$ExternalSyntheticLambda0(this));
                return;
            }
            Log.e(TAG, "wifiEntry was null, finishing activity");
            finish();
            return;
        }
        Log.e(TAG, "wifiScanWorker was null, finishing activity");
        finish();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(WifiEntry wifiEntry, DialogInterface dialogInterface, int i) {
        finish();
        wifiEntry.connect((WifiEntry.ConnectCallback) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(DialogInterface dialogInterface, int i) {
        finish();
    }

    /* access modifiers changed from: package-private */
    public WifiScanWorker getWifiScanWorker(Intent intent) {
        return (WifiScanWorker) SliceBackgroundWorker.getInstance((Uri) intent.getParcelableExtra("key_wifi_slice_uri"));
    }
}
