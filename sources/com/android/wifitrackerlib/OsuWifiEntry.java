package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.ProvisioningCallback;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.WifiEntry;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class OsuWifiEntry extends WifiEntry {
    /* access modifiers changed from: private */
    public final Context mContext;
    private final List<ScanResult> mCurrentScanResults = new ArrayList();
    private boolean mIsAlreadyProvisioned = false;
    private final String mKey;
    private MotoDevicePolicyManager mMotoDPMS = null;
    /* access modifiers changed from: private */
    public final OsuProvider mOsuProvider;
    /* access modifiers changed from: private */
    public String mOsuStatusString;
    private String mSsid;

    public String getMacAddress() {
        return null;
    }

    /* access modifiers changed from: protected */
    public String getScanResultDescription() {
        return "";
    }

    OsuWifiEntry(Context context, Handler handler, OsuProvider osuProvider, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        Preconditions.checkNotNull(osuProvider, "Cannot construct with null osuProvider!");
        this.mContext = context;
        this.mOsuProvider = osuProvider;
        this.mKey = osuProviderToOsuWifiEntryKey(osuProvider);
        this.mMotoDPMS = (MotoDevicePolicyManager) context.getSystemService("mot_device_policy");
    }

    public String getKey() {
        return this.mKey;
    }

    public synchronized String getTitle() {
        String friendlyName = this.mOsuProvider.getFriendlyName();
        if (!TextUtils.isEmpty(friendlyName)) {
            return friendlyName;
        }
        if (!TextUtils.isEmpty(this.mSsid)) {
            return this.mSsid;
        }
        Uri serverUri = this.mOsuProvider.getServerUri();
        if (serverUri == null) {
            return "";
        }
        return serverUri.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String getSummary(boolean r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = r1.mOsuStatusString     // Catch:{ all -> 0x002c }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r1)
            return r0
        L_0x0007:
            boolean r0 = r1.isAlreadyProvisioned()     // Catch:{ all -> 0x002c }
            if (r0 == 0) goto L_0x0022
            if (r2 == 0) goto L_0x0018
            android.content.Context r2 = r1.mContext     // Catch:{ all -> 0x002c }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_passpoint_expired     // Catch:{ all -> 0x002c }
            java.lang.String r2 = r2.getString(r0)     // Catch:{ all -> 0x002c }
            goto L_0x0020
        L_0x0018:
            android.content.Context r2 = r1.mContext     // Catch:{ all -> 0x002c }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_tap_to_renew_subscription_and_connect     // Catch:{ all -> 0x002c }
            java.lang.String r2 = r2.getString(r0)     // Catch:{ all -> 0x002c }
        L_0x0020:
            monitor-exit(r1)
            return r2
        L_0x0022:
            android.content.Context r2 = r1.mContext     // Catch:{ all -> 0x002c }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_tap_to_sign_up     // Catch:{ all -> 0x002c }
            java.lang.String r2 = r2.getString(r0)     // Catch:{ all -> 0x002c }
            monitor-exit(r1)
            return r2
        L_0x002c:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.OsuWifiEntry.getSummary(boolean):java.lang.String");
    }

    public synchronized String getSsid() {
        return this.mSsid;
    }

    private boolean cannotConnectWithAdminRestrictions() {
        List allowedSsids;
        MotoDevicePolicyManager motoDevicePolicyManager = this.mMotoDPMS;
        if (motoDevicePolicyManager == null || !motoDevicePolicyManager.hasUserRestriction("no_wifi_connection_by_ssid")) {
            return false;
        }
        int wifiRestrictionState = this.mMotoDPMS.getWifiRestrictionState();
        if (wifiRestrictionState == 2) {
            List blockedSsids = this.mMotoDPMS.getBlockedSsids();
            if (blockedSsids == null || !blockedSsids.contains(getSsid())) {
                return false;
            }
            if (Build.IS_DEBUGGABLE) {
                Log.d("OsuWifiEntry:", getSsid() + ": Can't connect to blocked SSID");
            }
            return true;
        } else if (wifiRestrictionState != 1 || (allowedSsids = this.mMotoDPMS.getAllowedSsids()) == null || allowedSsids.contains(getSsid())) {
            return false;
        } else {
            if (Build.IS_DEBUGGABLE) {
                Log.d("OsuWifiEntry:", getSsid() + ": SSID is not in allowed list");
            }
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0035, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean canConnect() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.cannotConnectWithAdminRestrictions()     // Catch:{ all -> 0x0036 }
            r1 = 0
            if (r0 == 0) goto L_0x0028
            boolean r0 = android.os.Build.IS_DEBUGGABLE     // Catch:{ all -> 0x0036 }
            if (r0 == 0) goto L_0x0026
            java.lang.String r0 = "OsuWifiEntry:"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0036 }
            r2.<init>()     // Catch:{ all -> 0x0036 }
            java.lang.String r3 = "Can't connect to Passpoint network: "
            r2.append(r3)     // Catch:{ all -> 0x0036 }
            java.lang.String r3 = r4.getSsid()     // Catch:{ all -> 0x0036 }
            r2.append(r3)     // Catch:{ all -> 0x0036 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0036 }
            android.util.Log.d(r0, r2)     // Catch:{ all -> 0x0036 }
        L_0x0026:
            monitor-exit(r4)
            return r1
        L_0x0028:
            int r0 = r4.mLevel     // Catch:{ all -> 0x0036 }
            r2 = -1
            if (r0 == r2) goto L_0x0034
            int r0 = r4.getConnectedState()     // Catch:{ all -> 0x0036 }
            if (r0 != 0) goto L_0x0034
            r1 = 1
        L_0x0034:
            monitor-exit(r4)
            return r1
        L_0x0036:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.OsuWifiEntry.canConnect():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void connect(com.android.wifitrackerlib.WifiEntry.ConnectCallback r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.cannotConnectWithAdminRestrictions()     // Catch:{ all -> 0x0042 }
            if (r0 == 0) goto L_0x0027
            boolean r4 = android.os.Build.IS_DEBUGGABLE     // Catch:{ all -> 0x0042 }
            if (r4 == 0) goto L_0x0025
            java.lang.String r4 = "OsuWifiEntry:"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0042 }
            r0.<init>()     // Catch:{ all -> 0x0042 }
            java.lang.String r1 = "Can't connect to Passpoint network: "
            r0.append(r1)     // Catch:{ all -> 0x0042 }
            java.lang.String r1 = r3.getSsid()     // Catch:{ all -> 0x0042 }
            r0.append(r1)     // Catch:{ all -> 0x0042 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0042 }
            android.util.Log.d(r4, r0)     // Catch:{ all -> 0x0042 }
        L_0x0025:
            monitor-exit(r3)
            return
        L_0x0027:
            r3.mConnectCallback = r4     // Catch:{ all -> 0x0042 }
            android.net.wifi.WifiManager r4 = r3.mWifiManager     // Catch:{ all -> 0x0042 }
            r4.stopRestrictingAutoJoinToSubscriptionId()     // Catch:{ all -> 0x0042 }
            android.net.wifi.WifiManager r4 = r3.mWifiManager     // Catch:{ all -> 0x0042 }
            android.net.wifi.hotspot2.OsuProvider r0 = r3.mOsuProvider     // Catch:{ all -> 0x0042 }
            android.content.Context r1 = r3.mContext     // Catch:{ all -> 0x0042 }
            java.util.concurrent.Executor r1 = r1.getMainExecutor()     // Catch:{ all -> 0x0042 }
            com.android.wifitrackerlib.OsuWifiEntry$OsuWifiEntryProvisioningCallback r2 = new com.android.wifitrackerlib.OsuWifiEntry$OsuWifiEntryProvisioningCallback     // Catch:{ all -> 0x0042 }
            r2.<init>()     // Catch:{ all -> 0x0042 }
            r4.startSubscriptionProvisioning(r0, r1, r2)     // Catch:{ all -> 0x0042 }
            monitor-exit(r3)
            return
        L_0x0042:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.OsuWifiEntry.connect(com.android.wifitrackerlib.WifiEntry$ConnectCallback):void");
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mCurrentScanResults.clear();
        this.mCurrentScanResults.addAll(list);
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
        if (bestScanResultByLevel != null) {
            this.mSsid = bestScanResultByLevel.SSID;
            if (getConnectedState() == 0) {
                this.mLevel = this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level);
            }
        } else {
            this.mLevel = -1;
        }
        notifyOnUpdated();
    }

    static String osuProviderToOsuWifiEntryKey(OsuProvider osuProvider) {
        Preconditions.checkNotNull(osuProvider, "Cannot create key with null OsuProvider!");
        return "OsuWifiEntry:" + osuProvider.getFriendlyName() + "," + osuProvider.getServerUri().toString();
    }

    /* access modifiers changed from: protected */
    public boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return wifiInfo.isOsuAp() && TextUtils.equals(wifiInfo.getPasspointProviderFriendlyName(), this.mOsuProvider.getFriendlyName());
    }

    /* access modifiers changed from: package-private */
    public OsuProvider getOsuProvider() {
        return this.mOsuProvider;
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean isAlreadyProvisioned() {
        return this.mIsAlreadyProvisioned;
    }

    /* access modifiers changed from: package-private */
    public synchronized void setAlreadyProvisioned(boolean z) {
        this.mIsAlreadyProvisioned = z;
    }

    class OsuWifiEntryProvisioningCallback extends ProvisioningCallback {
        OsuWifiEntryProvisioningCallback() {
        }

        public void onProvisioningFailure(int i) {
            synchronized (OsuWifiEntry.this) {
                if (TextUtils.equals(OsuWifiEntry.this.mOsuStatusString, OsuWifiEntry.this.mContext.getString(R$string.wifitrackerlib_osu_completing_sign_up))) {
                    OsuWifiEntry osuWifiEntry = OsuWifiEntry.this;
                    String unused = osuWifiEntry.mOsuStatusString = osuWifiEntry.mContext.getString(R$string.wifitrackerlib_osu_sign_up_failed);
                } else {
                    OsuWifiEntry osuWifiEntry2 = OsuWifiEntry.this;
                    String unused2 = osuWifiEntry2.mOsuStatusString = osuWifiEntry2.mContext.getString(R$string.wifitrackerlib_osu_connect_failed);
                }
            }
            WifiEntry.ConnectCallback connectCallback = OsuWifiEntry.this.mConnectCallback;
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
            OsuWifiEntry.this.notifyOnUpdated();
        }

        public void onProvisioningStatus(int i) {
            String str;
            boolean z = false;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    str = String.format(OsuWifiEntry.this.mContext.getString(R$string.wifitrackerlib_osu_opening_provider), new Object[]{OsuWifiEntry.this.getTitle()});
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    str = OsuWifiEntry.this.mContext.getString(R$string.wifitrackerlib_osu_completing_sign_up);
                    break;
                default:
                    str = null;
                    break;
            }
            synchronized (OsuWifiEntry.this) {
                if (!TextUtils.equals(OsuWifiEntry.this.mOsuStatusString, str)) {
                    z = true;
                }
                String unused = OsuWifiEntry.this.mOsuStatusString = str;
                if (z) {
                    OsuWifiEntry.this.notifyOnUpdated();
                }
            }
        }

        public void onProvisioningComplete() {
            ScanResult scanResult;
            synchronized (OsuWifiEntry.this) {
                OsuWifiEntry osuWifiEntry = OsuWifiEntry.this;
                String unused = osuWifiEntry.mOsuStatusString = osuWifiEntry.mContext.getString(R$string.wifitrackerlib_osu_sign_up_complete);
            }
            OsuWifiEntry.this.notifyOnUpdated();
            OsuWifiEntry osuWifiEntry2 = OsuWifiEntry.this;
            PasspointConfiguration passpointConfiguration = (PasspointConfiguration) osuWifiEntry2.mWifiManager.getMatchingPasspointConfigsForOsuProviders(Collections.singleton(osuWifiEntry2.mOsuProvider)).get(OsuWifiEntry.this.mOsuProvider);
            WifiEntry.ConnectCallback connectCallback = OsuWifiEntry.this.mConnectCallback;
            if (passpointConfiguration != null) {
                String uniqueId = passpointConfiguration.getUniqueId();
                WifiManager wifiManager = OsuWifiEntry.this.mWifiManager;
                Iterator it = wifiManager.getAllMatchingWifiConfigs(wifiManager.getScanResults()).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Pair pair = (Pair) it.next();
                    WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
                    if (TextUtils.equals(wifiConfiguration.getKey(), uniqueId)) {
                        List list = (List) ((Map) pair.second).get(0);
                        List list2 = (List) ((Map) pair.second).get(1);
                        if (list != null && !list.isEmpty()) {
                            scanResult = Utils.getBestScanResultByLevel(list);
                        } else if (list2 != null && !list2.isEmpty()) {
                            scanResult = Utils.getBestScanResultByLevel(list2);
                        }
                        wifiConfiguration.SSID = "\"" + scanResult.SSID + "\"";
                        OsuWifiEntry.this.mWifiManager.connect(wifiConfiguration, (WifiManager.ActionListener) null);
                        return;
                    }
                }
                if (connectCallback != null) {
                    connectCallback.onConnectResult(2);
                }
            } else if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
        }
    }
}
