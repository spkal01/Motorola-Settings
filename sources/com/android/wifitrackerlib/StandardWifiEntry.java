package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@VisibleForTesting
public class StandardWifiEntry extends WifiEntry {
    private final Context mContext;
    private final boolean mIsEnhancedOpenSupported;
    private boolean mIsUserShareable;
    private final boolean mIsWpa3SaeSupported;
    private final boolean mIsWpa3SuiteBSupported;
    private final StandardWifiEntryKey mKey;
    private final Map<Integer, List<ScanResult>> mMatchingScanResults;
    private final Map<Integer, WifiConfiguration> mMatchingWifiConfigs;
    private MotoDevicePolicyManager mMotoDPMS;
    private String mRecommendationServiceLabel;
    private boolean mShouldAutoOpenCaptivePortal;
    private final List<ScanResult> mTargetScanResults;
    private List<Integer> mTargetSecurityTypes;
    private WifiConfiguration mTargetWifiConfig;

    StandardWifiEntry(Context context, Handler handler, StandardWifiEntryKey standardWifiEntryKey, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        this.mMatchingScanResults = new HashMap();
        this.mMatchingWifiConfigs = new HashMap();
        this.mTargetScanResults = new ArrayList();
        this.mTargetSecurityTypes = new ArrayList();
        this.mIsUserShareable = false;
        this.mShouldAutoOpenCaptivePortal = false;
        this.mMotoDPMS = null;
        this.mContext = context;
        this.mKey = standardWifiEntryKey;
        this.mIsWpa3SaeSupported = wifiManager.isWpa3SaeSupported();
        this.mIsWpa3SuiteBSupported = wifiManager.isWpa3SuiteBSupported();
        this.mIsEnhancedOpenSupported = wifiManager.isEnhancedOpenSupported();
        updateRecommendationServiceLabel();
        this.mMotoDPMS = (MotoDevicePolicyManager) context.getSystemService("mot_device_policy");
    }

    StandardWifiEntry(Context context, Handler handler, StandardWifiEntryKey standardWifiEntryKey, List<WifiConfiguration> list, List<ScanResult> list2, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        this(context, handler, standardWifiEntryKey, wifiManager, wifiNetworkScoreCache, z);
        if (list != null && !list.isEmpty()) {
            updateConfig(list);
        }
        if (list2 != null && !list2.isEmpty()) {
            updateScanResultInfo(list2);
        }
    }

    public String getKey() {
        return this.mKey.toString();
    }

    /* access modifiers changed from: package-private */
    public StandardWifiEntryKey getStandardWifiEntryKey() {
        return this.mKey;
    }

    public String getTitle() {
        return this.mKey.getScanResultKey().getSsid();
    }

    public synchronized String getSummary(boolean z) {
        StringJoiner stringJoiner;
        String str;
        stringJoiner = new StringJoiner(this.mContext.getString(R$string.wifitrackerlib_summary_separator));
        int connectedState = getConnectedState();
        if (connectedState == 0) {
            str = Utils.getDisconnectedDescription(this.mContext, this.mTargetWifiConfig, this.mForSavedNetworksPage, z);
        } else if (connectedState == 1) {
            str = Utils.getConnectingDescription(this.mContext, this.mNetworkInfo);
        } else if (connectedState != 2) {
            Log.e("StandardWifiEntry", "getConnectedState() returned unknown state: " + connectedState);
            str = null;
        } else {
            str = Utils.getConnectedDescription(this.mContext, this.mTargetWifiConfig, this.mNetworkCapabilities, this.mRecommendationServiceLabel, this.mIsDefaultNetwork, this.mIsLowQuality);
        }
        if (!TextUtils.isEmpty(str)) {
            stringJoiner.add(str);
        }
        String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty(speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    public CharSequence getSecondSummary() {
        return getConnectedState() == 2 ? Utils.getImsiProtectionDescription(this.mContext, getWifiConfiguration()) : "";
    }

    public String getSsid() {
        return this.mKey.getScanResultKey().getSsid();
    }

    public synchronized List<Integer> getSecurityTypes() {
        return new ArrayList(this.mTargetSecurityTypes);
    }

    public synchronized String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            String macAddress = wifiInfo.getMacAddress();
            if (!TextUtils.isEmpty(macAddress) && !TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
                return macAddress;
            }
        }
        if (this.mTargetWifiConfig != null) {
            if (getPrivacy() == 1) {
                return this.mTargetWifiConfig.getRandomizedMacAddress().toString();
            }
        }
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        if (factoryMacAddresses.length <= 0) {
            return null;
        }
        return factoryMacAddresses[0];
    }

    public synchronized boolean isMetered() {
        boolean z;
        WifiConfiguration wifiConfiguration;
        z = true;
        if (getMeteredChoice() != 1 && ((wifiConfiguration = this.mTargetWifiConfig) == null || !wifiConfiguration.meteredHint)) {
            z = false;
        }
        return z;
    }

    public synchronized boolean isSaved() {
        WifiConfiguration wifiConfiguration;
        wifiConfiguration = this.mTargetWifiConfig;
        return wifiConfiguration != null && !wifiConfiguration.fromWifiNetworkSuggestion && !wifiConfiguration.isEphemeral();
    }

    public synchronized boolean isSuggestion() {
        WifiConfiguration wifiConfiguration;
        wifiConfiguration = this.mTargetWifiConfig;
        return wifiConfiguration != null && wifiConfiguration.fromWifiNetworkSuggestion;
    }

    public synchronized WifiConfiguration getWifiConfiguration() {
        if (!isSaved()) {
            return null;
        }
        return this.mTargetWifiConfig;
    }

    private boolean cannotConnectWithAdminRestrictions() {
        List allowedSsids;
        List blockedSsids;
        MotoDevicePolicyManager motoDevicePolicyManager = this.mMotoDPMS;
        if (motoDevicePolicyManager == null) {
            return false;
        }
        if (motoDevicePolicyManager.hasUserRestriction("disallow_wifi_unsecure_networks") && getSecurity() == 0) {
            if (Build.IS_DEBUGGABLE) {
                Log.d("StandardWifiEntry:", getSsid() + ": Can't connect to unsecure WiFis Networks");
            }
            return true;
        } else if (!this.mMotoDPMS.hasUserRestriction("no_wifi_connection_by_ssid")) {
            return false;
        } else {
            int wifiRestrictionState = this.mMotoDPMS.getWifiRestrictionState();
            if (wifiRestrictionState == 2 && (blockedSsids = this.mMotoDPMS.getBlockedSsids()) != null && blockedSsids.contains(getSsid())) {
                if (Build.IS_DEBUGGABLE) {
                    Log.d("StandardWifiEntry:", getSsid() + ": Can't connect to blocked SSID");
                }
                return true;
            } else if (wifiRestrictionState != 1 || (allowedSsids = this.mMotoDPMS.getAllowedSsids()) == null || allowedSsids.contains(getSsid())) {
                return false;
            } else {
                if (Build.IS_DEBUGGABLE) {
                    Log.d("StandardWifiEntry:", getSsid() + ": Connect to allowed SSID");
                }
                return true;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0072, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0074, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0076, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean canConnect() {
        /*
            r5 = this;
            monitor-enter(r5)
            boolean r0 = r5.cannotConnectWithAdminRestrictions()     // Catch:{ all -> 0x0077 }
            r1 = 0
            if (r0 == 0) goto L_0x000a
            monitor-exit(r5)
            return r1
        L_0x000a:
            int r0 = r5.mLevel     // Catch:{ all -> 0x0077 }
            r2 = -1
            if (r0 == r2) goto L_0x0075
            int r0 = r5.getConnectedState()     // Catch:{ all -> 0x0077 }
            if (r0 == 0) goto L_0x0016
            goto L_0x0075
        L_0x0016:
            java.util.List<java.lang.Integer> r0 = r5.mTargetSecurityTypes     // Catch:{ all -> 0x0077 }
            r3 = 3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x0077 }
            boolean r0 = r0.contains(r3)     // Catch:{ all -> 0x0077 }
            r3 = 1
            if (r0 == 0) goto L_0x0073
            android.net.wifi.WifiConfiguration r0 = r5.mTargetWifiConfig     // Catch:{ all -> 0x0077 }
            if (r0 == 0) goto L_0x0073
            android.net.wifi.WifiEnterpriseConfig r0 = r0.enterpriseConfig     // Catch:{ all -> 0x0077 }
            if (r0 == 0) goto L_0x0073
            boolean r0 = r0.isAuthenticationSimBased()     // Catch:{ all -> 0x0077 }
            if (r0 != 0) goto L_0x0034
            monitor-exit(r5)
            return r3
        L_0x0034:
            android.content.Context r0 = r5.mContext     // Catch:{ all -> 0x0077 }
            java.lang.String r4 = "telephony_subscription_service"
            java.lang.Object r0 = r0.getSystemService(r4)     // Catch:{ all -> 0x0077 }
            android.telephony.SubscriptionManager r0 = (android.telephony.SubscriptionManager) r0     // Catch:{ all -> 0x0077 }
            java.util.List r0 = r0.getActiveSubscriptionInfoList()     // Catch:{ all -> 0x0077 }
            if (r0 == 0) goto L_0x0071
            int r4 = r0.size()     // Catch:{ all -> 0x0077 }
            if (r4 != 0) goto L_0x004b
            goto L_0x0071
        L_0x004b:
            android.net.wifi.WifiConfiguration r4 = r5.mTargetWifiConfig     // Catch:{ all -> 0x0077 }
            int r4 = r4.carrierId     // Catch:{ all -> 0x0077 }
            if (r4 != r2) goto L_0x0053
            monitor-exit(r5)
            return r3
        L_0x0053:
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0077 }
        L_0x0057:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x0077 }
            if (r2 == 0) goto L_0x006f
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x0077 }
            android.telephony.SubscriptionInfo r2 = (android.telephony.SubscriptionInfo) r2     // Catch:{ all -> 0x0077 }
            int r2 = r2.getCarrierId()     // Catch:{ all -> 0x0077 }
            android.net.wifi.WifiConfiguration r4 = r5.mTargetWifiConfig     // Catch:{ all -> 0x0077 }
            int r4 = r4.carrierId     // Catch:{ all -> 0x0077 }
            if (r2 != r4) goto L_0x0057
            monitor-exit(r5)
            return r3
        L_0x006f:
            monitor-exit(r5)
            return r1
        L_0x0071:
            monitor-exit(r5)
            return r1
        L_0x0073:
            monitor-exit(r5)
            return r3
        L_0x0075:
            monitor-exit(r5)
            return r1
        L_0x0077:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.canConnect():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x012e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x013e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void connect(com.android.wifitrackerlib.WifiEntry.ConnectCallback r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.cannotConnectWithAdminRestrictions()     // Catch:{ all -> 0x013f }
            if (r0 == 0) goto L_0x0027
            boolean r5 = android.os.Build.IS_DEBUGGABLE     // Catch:{ all -> 0x013f }
            if (r5 == 0) goto L_0x0025
            java.lang.String r5 = "StandardWifiEntry:"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x013f }
            r0.<init>()     // Catch:{ all -> 0x013f }
            java.lang.String r1 = "Can't connect to current WiFi Network: "
            r0.append(r1)     // Catch:{ all -> 0x013f }
            java.lang.String r1 = r4.getSsid()     // Catch:{ all -> 0x013f }
            r0.append(r1)     // Catch:{ all -> 0x013f }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x013f }
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x013f }
        L_0x0025:
            monitor-exit(r4)
            return
        L_0x0027:
            r4.mConnectCallback = r5     // Catch:{ all -> 0x013f }
            r0 = 1
            r4.mShouldAutoOpenCaptivePortal = r0     // Catch:{ all -> 0x013f }
            android.net.wifi.WifiManager r0 = r4.mWifiManager     // Catch:{ all -> 0x013f }
            r0.stopRestrictingAutoJoinToSubscriptionId()     // Catch:{ all -> 0x013f }
            boolean r0 = r4.isSaved()     // Catch:{ all -> 0x013f }
            if (r0 != 0) goto L_0x010d
            boolean r0 = r4.isSuggestion()     // Catch:{ all -> 0x013f }
            if (r0 == 0) goto L_0x003f
            goto L_0x010d
        L_0x003f:
            java.util.List<java.lang.Integer> r0 = r4.mTargetSecurityTypes     // Catch:{ all -> 0x013f }
            r1 = 6
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x013f }
            boolean r0 = r0.contains(r2)     // Catch:{ all -> 0x013f }
            r2 = 0
            if (r0 == 0) goto L_0x00bf
            android.net.wifi.WifiConfiguration r5 = new android.net.wifi.WifiConfiguration     // Catch:{ all -> 0x013f }
            r5.<init>()     // Catch:{ all -> 0x013f }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x013f }
            r0.<init>()     // Catch:{ all -> 0x013f }
            java.lang.String r3 = "\""
            r0.append(r3)     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$StandardWifiEntryKey r3 = r4.mKey     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$ScanResultKey r3 = r3.getScanResultKey()     // Catch:{ all -> 0x013f }
            java.lang.String r3 = r3.getSsid()     // Catch:{ all -> 0x013f }
            r0.append(r3)     // Catch:{ all -> 0x013f }
            java.lang.String r3 = "\""
            r0.append(r3)     // Catch:{ all -> 0x013f }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x013f }
            r5.SSID = r0     // Catch:{ all -> 0x013f }
            r5.setSecurityParams(r1)     // Catch:{ all -> 0x013f }
            android.net.wifi.WifiManager r0 = r4.mWifiManager     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.WifiEntry$ConnectActionListener r1 = new com.android.wifitrackerlib.WifiEntry$ConnectActionListener     // Catch:{ all -> 0x013f }
            r1.<init>()     // Catch:{ all -> 0x013f }
            r0.connect(r5, r1)     // Catch:{ all -> 0x013f }
            java.util.List<java.lang.Integer> r5 = r4.mTargetSecurityTypes     // Catch:{ all -> 0x013f }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x013f }
            boolean r5 = r5.contains(r0)     // Catch:{ all -> 0x013f }
            if (r5 == 0) goto L_0x013d
            android.net.wifi.WifiConfiguration r5 = new android.net.wifi.WifiConfiguration     // Catch:{ all -> 0x013f }
            r5.<init>()     // Catch:{ all -> 0x013f }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x013f }
            r0.<init>()     // Catch:{ all -> 0x013f }
            java.lang.String r1 = "\""
            r0.append(r1)     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$StandardWifiEntryKey r1 = r4.mKey     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$ScanResultKey r1 = r1.getScanResultKey()     // Catch:{ all -> 0x013f }
            java.lang.String r1 = r1.getSsid()     // Catch:{ all -> 0x013f }
            r0.append(r1)     // Catch:{ all -> 0x013f }
            java.lang.String r1 = "\""
            r0.append(r1)     // Catch:{ all -> 0x013f }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x013f }
            r5.SSID = r0     // Catch:{ all -> 0x013f }
            r5.setSecurityParams(r2)     // Catch:{ all -> 0x013f }
            android.net.wifi.WifiManager r0 = r4.mWifiManager     // Catch:{ all -> 0x013f }
            r1 = 0
            r0.save(r5, r1)     // Catch:{ all -> 0x013f }
            goto L_0x013d
        L_0x00bf:
            java.util.List<java.lang.Integer> r0 = r4.mTargetSecurityTypes     // Catch:{ all -> 0x013f }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x013f }
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x013f }
            if (r0 == 0) goto L_0x0100
            android.net.wifi.WifiConfiguration r5 = new android.net.wifi.WifiConfiguration     // Catch:{ all -> 0x013f }
            r5.<init>()     // Catch:{ all -> 0x013f }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x013f }
            r0.<init>()     // Catch:{ all -> 0x013f }
            java.lang.String r1 = "\""
            r0.append(r1)     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$StandardWifiEntryKey r1 = r4.mKey     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$ScanResultKey r1 = r1.getScanResultKey()     // Catch:{ all -> 0x013f }
            java.lang.String r1 = r1.getSsid()     // Catch:{ all -> 0x013f }
            r0.append(r1)     // Catch:{ all -> 0x013f }
            java.lang.String r1 = "\""
            r0.append(r1)     // Catch:{ all -> 0x013f }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x013f }
            r5.SSID = r0     // Catch:{ all -> 0x013f }
            r5.setSecurityParams(r2)     // Catch:{ all -> 0x013f }
            android.net.wifi.WifiManager r0 = r4.mWifiManager     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.WifiEntry$ConnectActionListener r1 = new com.android.wifitrackerlib.WifiEntry$ConnectActionListener     // Catch:{ all -> 0x013f }
            r1.<init>()     // Catch:{ all -> 0x013f }
            r0.connect(r5, r1)     // Catch:{ all -> 0x013f }
            goto L_0x013d
        L_0x0100:
            if (r5 == 0) goto L_0x013d
            android.os.Handler r0 = r4.mCallbackHandler     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda2 r1 = new com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda2     // Catch:{ all -> 0x013f }
            r1.<init>(r5)     // Catch:{ all -> 0x013f }
            r0.post(r1)     // Catch:{ all -> 0x013f }
            goto L_0x013d
        L_0x010d:
            android.net.wifi.WifiConfiguration r0 = r4.mTargetWifiConfig     // Catch:{ all -> 0x013f }
            boolean r0 = com.android.wifitrackerlib.Utils.isSimCredential(r0)     // Catch:{ all -> 0x013f }
            if (r0 == 0) goto L_0x012f
            android.content.Context r0 = r4.mContext     // Catch:{ all -> 0x013f }
            android.net.wifi.WifiConfiguration r1 = r4.mTargetWifiConfig     // Catch:{ all -> 0x013f }
            int r1 = r1.carrierId     // Catch:{ all -> 0x013f }
            boolean r0 = com.android.wifitrackerlib.Utils.isSimPresent(r0, r1)     // Catch:{ all -> 0x013f }
            if (r0 != 0) goto L_0x012f
            if (r5 == 0) goto L_0x012d
            android.os.Handler r0 = r4.mCallbackHandler     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda1 r1 = new com.android.wifitrackerlib.StandardWifiEntry$$ExternalSyntheticLambda1     // Catch:{ all -> 0x013f }
            r1.<init>(r5)     // Catch:{ all -> 0x013f }
            r0.post(r1)     // Catch:{ all -> 0x013f }
        L_0x012d:
            monitor-exit(r4)
            return
        L_0x012f:
            android.net.wifi.WifiManager r5 = r4.mWifiManager     // Catch:{ all -> 0x013f }
            android.net.wifi.WifiConfiguration r0 = r4.mTargetWifiConfig     // Catch:{ all -> 0x013f }
            int r0 = r0.networkId     // Catch:{ all -> 0x013f }
            com.android.wifitrackerlib.WifiEntry$ConnectActionListener r1 = new com.android.wifitrackerlib.WifiEntry$ConnectActionListener     // Catch:{ all -> 0x013f }
            r1.<init>()     // Catch:{ all -> 0x013f }
            r5.connect(r0, r1)     // Catch:{ all -> 0x013f }
        L_0x013d:
            monitor-exit(r4)
            return
        L_0x013f:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.connect(com.android.wifitrackerlib.WifiEntry$ConnectCallback):void");
    }

    public boolean canDisconnect() {
        return getConnectedState() == 2;
    }

    public synchronized void disconnect(WifiEntry.DisconnectCallback disconnectCallback) {
        if (canDisconnect()) {
            this.mCalledDisconnect = true;
            this.mDisconnectCallback = disconnectCallback;
            this.mCallbackHandler.postDelayed(new StandardWifiEntry$$ExternalSyntheticLambda0(this, disconnectCallback), 10000);
            WifiManager wifiManager = this.mWifiManager;
            wifiManager.disableEphemeralNetwork("\"" + this.mKey.getScanResultKey().getSsid() + "\"");
            this.mWifiManager.disconnect();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnect$2(WifiEntry.DisconnectCallback disconnectCallback) {
        if (disconnectCallback != null && this.mCalledDisconnect) {
            disconnectCallback.onDisconnectResult(1);
        }
    }

    public boolean canForget() {
        return getWifiConfiguration() != null;
    }

    public synchronized void forget(WifiEntry.ForgetCallback forgetCallback) {
        if (canForget()) {
            this.mForgetCallback = forgetCallback;
            this.mWifiManager.forget(this.mTargetWifiConfig.networkId, new WifiEntry.ForgetActionListener());
        }
    }

    public synchronized boolean canSignIn() {
        NetworkCapabilities networkCapabilities;
        networkCapabilities = this.mNetworkCapabilities;
        return networkCapabilities != null && networkCapabilities.hasCapability(17);
    }

    public void signIn(WifiEntry.SignInCallback signInCallback) {
        if (canSignIn()) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).startCaptivePortalApp(this.mWifiManager.getCurrentNetwork());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0030, code lost:
        return true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0016  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean canShare() {
        /*
            r5 = this;
            monitor-enter(r5)
            android.net.wifi.WifiConfiguration r0 = r5.getWifiConfiguration()     // Catch:{ all -> 0x0033 }
            r1 = 0
            if (r0 != 0) goto L_0x000a
            monitor-exit(r5)
            return r1
        L_0x000a:
            java.util.List<java.lang.Integer> r0 = r5.mTargetSecurityTypes     // Catch:{ all -> 0x0033 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0033 }
        L_0x0010:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x0033 }
            if (r2 == 0) goto L_0x0031
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x0033 }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ all -> 0x0033 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x0033 }
            r3 = 1
            if (r2 == 0) goto L_0x002f
            if (r2 == r3) goto L_0x002f
            r4 = 2
            if (r2 == r4) goto L_0x002f
            r4 = 4
            if (r2 == r4) goto L_0x002f
            r4 = 6
            if (r2 == r4) goto L_0x002f
            goto L_0x0010
        L_0x002f:
            monitor-exit(r5)
            return r3
        L_0x0031:
            monitor-exit(r5)
            return r1
        L_0x0033:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.canShare():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0030, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean canEasyConnect() {
        /*
            r3 = this;
            monitor-enter(r3)
            android.net.wifi.WifiConfiguration r0 = r3.getWifiConfiguration()     // Catch:{ all -> 0x0031 }
            r1 = 0
            if (r0 != 0) goto L_0x000a
            monitor-exit(r3)
            return r1
        L_0x000a:
            android.net.wifi.WifiManager r0 = r3.mWifiManager     // Catch:{ all -> 0x0031 }
            boolean r0 = r0.isEasyConnectSupported()     // Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x0014
            monitor-exit(r3)
            return r1
        L_0x0014:
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x0031 }
            r2 = 2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0031 }
            boolean r0 = r0.contains(r2)     // Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x002e
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x0031 }
            r2 = 4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0031 }
            boolean r0 = r0.contains(r2)     // Catch:{ all -> 0x0031 }
            if (r0 == 0) goto L_0x002f
        L_0x002e:
            r1 = 1
        L_0x002f:
            monitor-exit(r3)
            return r1
        L_0x0031:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.canEasyConnect():boolean");
    }

    public synchronized int getMeteredChoice() {
        WifiConfiguration wifiConfiguration;
        if (!isSuggestion() && (wifiConfiguration = this.mTargetWifiConfig) != null) {
            int i = wifiConfiguration.meteredOverride;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
        }
        return 0;
    }

    public boolean canSetMeteredChoice() {
        return getWifiConfiguration() != null;
    }

    public synchronized void setMeteredChoice(int i) {
        if (canSetMeteredChoice()) {
            if (i == 0) {
                this.mTargetWifiConfig.meteredOverride = 0;
            } else if (i == 1) {
                this.mTargetWifiConfig.meteredOverride = 1;
            } else if (i == 2) {
                this.mTargetWifiConfig.meteredOverride = 2;
            }
            this.mWifiManager.save(this.mTargetWifiConfig, (WifiManager.ActionListener) null);
        }
    }

    public boolean canSetPrivacy() {
        return isSaved();
    }

    public synchronized int getPrivacy() {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration == null || wifiConfiguration.macRandomizationSetting != 0) {
            return 1;
        }
        return 0;
    }

    public synchronized void setPrivacy(int i) {
        if (canSetPrivacy()) {
            WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
            wifiConfiguration.macRandomizationSetting = i == 1 ? 3 : 0;
            this.mWifiManager.save(wifiConfiguration, (WifiManager.ActionListener) null);
        }
    }

    public synchronized boolean isAutoJoinEnabled() {
        WifiConfiguration wifiConfiguration = this.mTargetWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.allowAutojoin;
    }

    public boolean canSetAutoJoinEnabled() {
        return isSaved() || isSuggestion();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setAutoJoinEnabled(boolean r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            android.net.wifi.WifiConfiguration r0 = r2.mTargetWifiConfig     // Catch:{ all -> 0x0019 }
            if (r0 == 0) goto L_0x0017
            boolean r0 = r2.canSetAutoJoinEnabled()     // Catch:{ all -> 0x0019 }
            if (r0 != 0) goto L_0x000c
            goto L_0x0017
        L_0x000c:
            android.net.wifi.WifiManager r0 = r2.mWifiManager     // Catch:{ all -> 0x0019 }
            android.net.wifi.WifiConfiguration r1 = r2.mTargetWifiConfig     // Catch:{ all -> 0x0019 }
            int r1 = r1.networkId     // Catch:{ all -> 0x0019 }
            r0.allowAutojoin(r1, r3)     // Catch:{ all -> 0x0019 }
            monitor-exit(r2)
            return
        L_0x0017:
            monitor-exit(r2)
            return
        L_0x0019:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.setAutoJoinEnabled(boolean):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:101:0x018b, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x01b2, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0070, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0085, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x009a, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00af, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00c4, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00dd, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0017, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x015b, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String getSecurityString(boolean r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            int r0 = r0.size()     // Catch:{ all -> 0x01b3 }
            if (r0 != 0) goto L_0x0018
            if (r4 == 0) goto L_0x000e
            java.lang.String r4 = ""
            goto L_0x0016
        L_0x000e:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_none     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x0016:
            monitor-exit(r3)
            return r4
        L_0x0018:
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            int r0 = r0.size()     // Catch:{ all -> 0x01b3 }
            r1 = 1
            r2 = 0
            if (r0 != r1) goto L_0x00de
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x01b3 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x01b3 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x01b3 }
            switch(r0) {
                case 0: goto L_0x00cf;
                case 1: goto L_0x00c5;
                case 2: goto L_0x00b0;
                case 3: goto L_0x009b;
                case 4: goto L_0x0086;
                case 5: goto L_0x0071;
                case 6: goto L_0x005c;
                case 7: goto L_0x0052;
                case 8: goto L_0x0048;
                case 9: goto L_0x0033;
                default: goto L_0x0031;
            }     // Catch:{ all -> 0x01b3 }
        L_0x0031:
            goto L_0x00de
        L_0x0033:
            if (r4 == 0) goto L_0x003e
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_eap_wpa3     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x0046
        L_0x003e:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_eap_wpa3     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x0046:
            monitor-exit(r3)
            return r4
        L_0x0048:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifi_security_wapi_cert     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            monitor-exit(r3)
            return r4
        L_0x0052:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifi_security_wapi_psk     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            monitor-exit(r3)
            return r4
        L_0x005c:
            if (r4 == 0) goto L_0x0067
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_owe     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x006f
        L_0x0067:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_owe     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x006f:
            monitor-exit(r3)
            return r4
        L_0x0071:
            if (r4 == 0) goto L_0x007c
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_eap_suiteb     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x0084
        L_0x007c:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_eap_suiteb     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x0084:
            monitor-exit(r3)
            return r4
        L_0x0086:
            if (r4 == 0) goto L_0x0091
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_sae     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x0099
        L_0x0091:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_sae     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x0099:
            monitor-exit(r3)
            return r4
        L_0x009b:
            if (r4 == 0) goto L_0x00a6
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_eap_wpa_wpa2     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x00ae
        L_0x00a6:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_eap_wpa_wpa2     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x00ae:
            monitor-exit(r3)
            return r4
        L_0x00b0:
            if (r4 == 0) goto L_0x00bb
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_wpa_wpa2     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x00c3
        L_0x00bb:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_wpa_wpa2     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x00c3:
            monitor-exit(r3)
            return r4
        L_0x00c5:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_wep     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            monitor-exit(r3)
            return r4
        L_0x00cf:
            if (r4 == 0) goto L_0x00d4
            java.lang.String r4 = ""
            goto L_0x00dc
        L_0x00d4:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_none     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x00dc:
            monitor-exit(r3)
            return r4
        L_0x00de:
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            int r0 = r0.size()     // Catch:{ all -> 0x01b3 }
            r1 = 2
            if (r0 != r1) goto L_0x018c
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x01b3 }
            boolean r0 = r0.contains(r2)     // Catch:{ all -> 0x01b3 }
            if (r0 == 0) goto L_0x012e
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            r2 = 6
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x01b3 }
            boolean r0 = r0.contains(r2)     // Catch:{ all -> 0x01b3 }
            if (r0 == 0) goto L_0x012e
            java.util.StringJoiner r0 = new java.util.StringJoiner     // Catch:{ all -> 0x01b3 }
            java.lang.String r1 = "/"
            r0.<init>(r1)     // Catch:{ all -> 0x01b3 }
            android.content.Context r1 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r2 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_none     // Catch:{ all -> 0x01b3 }
            java.lang.String r1 = r1.getString(r2)     // Catch:{ all -> 0x01b3 }
            r0.add(r1)     // Catch:{ all -> 0x01b3 }
            if (r4 == 0) goto L_0x011d
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r1 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_owe     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r1)     // Catch:{ all -> 0x01b3 }
            goto L_0x0125
        L_0x011d:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r1 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_owe     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r1)     // Catch:{ all -> 0x01b3 }
        L_0x0125:
            r0.add(r4)     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r0.toString()     // Catch:{ all -> 0x01b3 }
            monitor-exit(r3)
            return r4
        L_0x012e:
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x01b3 }
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x01b3 }
            if (r0 == 0) goto L_0x015c
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            r1 = 4
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x01b3 }
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x01b3 }
            if (r0 == 0) goto L_0x015c
            if (r4 == 0) goto L_0x0152
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_wpa_wpa2_wpa3     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x015a
        L_0x0152:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_wpa_wpa2_wpa3     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x015a:
            monitor-exit(r3)
            return r4
        L_0x015c:
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            r1 = 3
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x01b3 }
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x01b3 }
            if (r0 == 0) goto L_0x018c
            java.util.List<java.lang.Integer> r0 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            r1 = 9
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x01b3 }
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x01b3 }
            if (r0 == 0) goto L_0x018c
            if (r4 == 0) goto L_0x0182
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_short_eap_wpa_wpa2_wpa3     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
            goto L_0x018a
        L_0x0182:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_eap_wpa_wpa2_wpa3     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x018a:
            monitor-exit(r3)
            return r4
        L_0x018c:
            java.lang.String r0 = "StandardWifiEntry"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b3 }
            r1.<init>()     // Catch:{ all -> 0x01b3 }
            java.lang.String r2 = "Couldn't get string for security types: "
            r1.append(r2)     // Catch:{ all -> 0x01b3 }
            java.util.List<java.lang.Integer> r2 = r3.mTargetSecurityTypes     // Catch:{ all -> 0x01b3 }
            r1.append(r2)     // Catch:{ all -> 0x01b3 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01b3 }
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x01b3 }
            if (r4 == 0) goto L_0x01a9
            java.lang.String r4 = ""
            goto L_0x01b1
        L_0x01a9:
            android.content.Context r4 = r3.mContext     // Catch:{ all -> 0x01b3 }
            int r0 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_security_none     // Catch:{ all -> 0x01b3 }
            java.lang.String r4 = r4.getString(r0)     // Catch:{ all -> 0x01b3 }
        L_0x01b1:
            monitor-exit(r3)
            return r4
        L_0x01b3:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.getSecurityString(boolean):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean shouldEditBeforeConnect() {
        /*
            r3 = this;
            monitor-enter(r3)
            android.net.wifi.WifiConfiguration r0 = r3.getWifiConfiguration()     // Catch:{ all -> 0x002f }
            r1 = 0
            if (r0 != 0) goto L_0x000a
            monitor-exit(r3)
            return r1
        L_0x000a:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r0 = r0.getNetworkSelectionStatus()     // Catch:{ all -> 0x002f }
            int r2 = r0.getNetworkSelectionStatus()     // Catch:{ all -> 0x002f }
            if (r2 == 0) goto L_0x002d
            r2 = 2
            int r2 = r0.getDisableReasonCounter(r2)     // Catch:{ all -> 0x002f }
            if (r2 > 0) goto L_0x002a
            r2 = 8
            int r2 = r0.getDisableReasonCounter(r2)     // Catch:{ all -> 0x002f }
            if (r2 > 0) goto L_0x002a
            r2 = 5
            int r0 = r0.getDisableReasonCounter(r2)     // Catch:{ all -> 0x002f }
            if (r0 <= 0) goto L_0x002d
        L_0x002a:
            r0 = 1
            monitor-exit(r3)
            return r0
        L_0x002d:
            monitor-exit(r3)
            return r1
        L_0x002f:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.shouldEditBeforeConnect():boolean");
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        String ssid = this.mKey.getScanResultKey().getSsid();
        for (ScanResult next : list) {
            if (!TextUtils.equals(next.SSID, ssid)) {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + ssid + ", Actual: " + next.SSID + ", ScanResult: " + next);
            }
        }
        this.mMatchingScanResults.clear();
        Set<Integer> securityTypes = this.mKey.getScanResultKey().getSecurityTypes();
        for (ScanResult next2 : list) {
            for (Integer intValue : Utils.getSecurityTypesFromScanResult(next2)) {
                int intValue2 = intValue.intValue();
                if (securityTypes.contains(Integer.valueOf(intValue2))) {
                    if (isSecurityTypeSupported(intValue2)) {
                        if (!this.mMatchingScanResults.containsKey(Integer.valueOf(intValue2))) {
                            this.mMatchingScanResults.put(Integer.valueOf(intValue2), new ArrayList());
                        }
                        this.mMatchingScanResults.get(Integer.valueOf(intValue2)).add(next2);
                    }
                }
            }
        }
        updateSecurityTypes();
        updateTargetScanResultInfo();
        notifyOnUpdated();
    }

    private synchronized void updateTargetScanResultInfo() {
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(this.mTargetScanResults);
        if (getConnectedState() == 0) {
            this.mLevel = bestScanResultByLevel != null ? this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level) : -1;
            this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mTargetScanResults);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateNetworkCapabilities(networkCapabilities);
        if (canSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            signIn((WifiEntry.SignInCallback) null);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void onScoreCacheUpdated() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
        } else {
            this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mTargetScanResults);
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateConfig(List<WifiConfiguration> list) throws IllegalArgumentException {
        if (list == null) {
            list = Collections.emptyList();
        }
        ScanResultKey scanResultKey = this.mKey.getScanResultKey();
        String ssid = scanResultKey.getSsid();
        Set<Integer> securityTypes = scanResultKey.getSecurityTypes();
        this.mMatchingWifiConfigs.clear();
        for (WifiConfiguration next : list) {
            if (TextUtils.equals(ssid, WifiInfo.sanitizeSsid(next.SSID))) {
                Iterator<Integer> it = Utils.getSecurityTypesFromWifiConfiguration(next).iterator();
                while (true) {
                    if (it.hasNext()) {
                        int intValue = it.next().intValue();
                        if (!securityTypes.contains(Integer.valueOf(intValue))) {
                            throw new IllegalArgumentException("Attempted to update with wrong security! Expected one of: " + securityTypes + ", Actual: " + intValue + ", Config: " + next);
                        } else if (isSecurityTypeSupported(intValue)) {
                            this.mMatchingWifiConfigs.put(Integer.valueOf(intValue), next);
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + ssid + ", Actual: " + WifiInfo.sanitizeSsid(next.SSID) + ", Config: " + next);
            }
        }
        updateSecurityTypes();
        updateTargetScanResultInfo();
        notifyOnUpdated();
    }

    private boolean isSecurityTypeSupported(int i) {
        if (i == 4) {
            return this.mIsWpa3SaeSupported;
        }
        if (i == 5) {
            return this.mIsWpa3SuiteBSupported;
        }
        if (i != 6) {
            return true;
        }
        return this.mIsEnhancedOpenSupported;
    }

    /* access modifiers changed from: protected */
    public synchronized void updateSecurityTypes() {
        this.mTargetSecurityTypes.clear();
        WifiInfo wifiInfo = this.mWifiInfo;
        if (!(wifiInfo == null || wifiInfo.getCurrentSecurityType() == -1)) {
            this.mTargetSecurityTypes.add(Integer.valueOf(this.mWifiInfo.getCurrentSecurityType()));
        }
        Set<Integer> keySet = this.mMatchingWifiConfigs.keySet();
        if (this.mTargetSecurityTypes.isEmpty() && this.mKey.isTargetingNewNetworks()) {
            boolean z = false;
            Set<Integer> keySet2 = this.mMatchingScanResults.keySet();
            Iterator<Integer> it = keySet.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (keySet2.contains(Integer.valueOf(it.next().intValue()))) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                this.mTargetSecurityTypes.addAll(keySet2);
            }
        }
        if (this.mTargetSecurityTypes.isEmpty()) {
            this.mTargetSecurityTypes.addAll(keySet);
        }
        if (this.mTargetSecurityTypes.isEmpty()) {
            this.mTargetSecurityTypes.addAll(this.mKey.getScanResultKey().getSecurityTypes());
        }
        this.mTargetWifiConfig = this.mMatchingWifiConfigs.get(Integer.valueOf(Utils.getSingleSecurityTypeFromMultipleSecurityTypes(this.mTargetSecurityTypes)));
        ArraySet arraySet = new ArraySet();
        for (Integer intValue : this.mTargetSecurityTypes) {
            int intValue2 = intValue.intValue();
            if (this.mMatchingScanResults.containsKey(Integer.valueOf(intValue2))) {
                arraySet.addAll(this.mMatchingScanResults.get(Integer.valueOf(intValue2)));
            }
        }
        this.mTargetScanResults.clear();
        this.mTargetScanResults.addAll(arraySet);
    }

    /* access modifiers changed from: package-private */
    public synchronized void setUserShareable(boolean z) {
        this.mIsUserShareable = z;
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean isUserShareable() {
        return this.mIsUserShareable;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean connectionInfoMatches(android.net.wifi.WifiInfo r4, android.net.NetworkInfo r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r5 = r4.isPasspointAp()     // Catch:{ all -> 0x0034 }
            r0 = 0
            if (r5 != 0) goto L_0x0032
            boolean r5 = r4.isOsuAp()     // Catch:{ all -> 0x0034 }
            if (r5 == 0) goto L_0x000f
            goto L_0x0032
        L_0x000f:
            java.util.Map<java.lang.Integer, android.net.wifi.WifiConfiguration> r5 = r3.mMatchingWifiConfigs     // Catch:{ all -> 0x0034 }
            java.util.Collection r5 = r5.values()     // Catch:{ all -> 0x0034 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0034 }
        L_0x0019:
            boolean r1 = r5.hasNext()     // Catch:{ all -> 0x0034 }
            if (r1 == 0) goto L_0x0030
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0034 }
            android.net.wifi.WifiConfiguration r1 = (android.net.wifi.WifiConfiguration) r1     // Catch:{ all -> 0x0034 }
            int r1 = r1.networkId     // Catch:{ all -> 0x0034 }
            int r2 = r4.getNetworkId()     // Catch:{ all -> 0x0034 }
            if (r1 != r2) goto L_0x0019
            r4 = 1
            monitor-exit(r3)
            return r4
        L_0x0030:
            monitor-exit(r3)
            return r0
        L_0x0032:
            monitor-exit(r3)
            return r0
        L_0x0034:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.StandardWifiEntry.connectionInfoMatches(android.net.wifi.WifiInfo, android.net.NetworkInfo):boolean");
    }

    private synchronized void updateRecommendationServiceLabel() {
        NetworkScorerAppData activeScorer = ((NetworkScoreManager) this.mContext.getSystemService("network_score")).getActiveScorer();
        if (activeScorer != null) {
            this.mRecommendationServiceLabel = activeScorer.getRecommendationServiceLabel();
        }
    }

    /* access modifiers changed from: protected */
    public synchronized String getScanResultDescription() {
        if (this.mTargetScanResults.size() == 0) {
            return "";
        }
        return "[" + getScanResultDescription(2400, 2500) + ";" + getScanResultDescription(4900, 5900) + ";" + getScanResultDescription(5925, 7125) + ";" + getScanResultDescription(58320, 70200) + "]";
    }

    private synchronized String getScanResultDescription(int i, int i2) {
        List list = (List) this.mTargetScanResults.stream().filter(new StandardWifiEntry$$ExternalSyntheticLambda4(i, i2)).sorted(Comparator.comparingInt(StandardWifiEntry$$ExternalSyntheticLambda6.INSTANCE)).collect(Collectors.toList());
        int size = list.size();
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(size);
        sb.append(")");
        if (size > 4) {
            int asInt = list.stream().mapToInt(StandardWifiEntry$$ExternalSyntheticLambda5.INSTANCE).max().getAsInt();
            sb.append("max=");
            sb.append(asInt);
            sb.append(",");
        }
        list.forEach(new StandardWifiEntry$$ExternalSyntheticLambda3(this, sb, SystemClock.elapsedRealtime()));
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getScanResultDescription$3(int i, int i2, ScanResult scanResult) {
        int i3 = scanResult.frequency;
        return i3 >= i && i3 <= i2;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getScanResultDescription$4(ScanResult scanResult) {
        return scanResult.level * -1;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getScanResultDescription$6(StringBuilder sb, long j, ScanResult scanResult) {
        sb.append(getScanResultDescription(scanResult, j));
    }

    private synchronized String getScanResultDescription(ScanResult scanResult, long j) {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null && scanResult.BSSID.equals(wifiInfo.getBSSID())) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        sb.append(",");
        sb.append(((int) (j - (scanResult.timestamp / 1000))) / 1000);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public String getNetworkSelectionDescription() {
        return Utils.getNetworkSelectionDescription(getWifiConfiguration());
    }

    static class StandardWifiEntryKey {
        private boolean mIsNetworkRequest;
        private boolean mIsTargetingNewNetworks;
        private ScanResultKey mScanResultKey;
        private String mSuggestionProfileKey;

        StandardWifiEntryKey(ScanResultKey scanResultKey, boolean z) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = scanResultKey;
            this.mIsTargetingNewNetworks = z;
        }

        StandardWifiEntryKey(WifiConfiguration wifiConfiguration) {
            this(wifiConfiguration, false);
        }

        StandardWifiEntryKey(WifiConfiguration wifiConfiguration, boolean z) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = new ScanResultKey(wifiConfiguration);
            if (wifiConfiguration.fromWifiNetworkSuggestion) {
                this.mSuggestionProfileKey = new StringJoiner(",").add(wifiConfiguration.creatorName).add(String.valueOf(wifiConfiguration.carrierId)).add(String.valueOf(wifiConfiguration.subscriptionId)).toString();
            } else if (wifiConfiguration.fromWifiNetworkSpecifier) {
                this.mIsNetworkRequest = true;
            }
            this.mIsTargetingNewNetworks = z;
        }

        StandardWifiEntryKey(String str) {
            this.mIsTargetingNewNetworks = false;
            this.mScanResultKey = new ScanResultKey();
            if (!str.startsWith("StandardWifiEntry:")) {
                Log.e("StandardWifiEntry", "String key does not start with key prefix!");
                return;
            }
            try {
                JSONObject jSONObject = new JSONObject(str.substring(18));
                if (jSONObject.has("SCAN_RESULT_KEY")) {
                    this.mScanResultKey = new ScanResultKey(jSONObject.getString("SCAN_RESULT_KEY"));
                }
                if (jSONObject.has("SUGGESTION_PROFILE_KEY")) {
                    this.mSuggestionProfileKey = jSONObject.getString("SUGGESTION_PROFILE_KEY");
                }
                if (jSONObject.has("IS_NETWORK_REQUEST")) {
                    this.mIsNetworkRequest = jSONObject.getBoolean("IS_NETWORK_REQUEST");
                }
                if (jSONObject.has("IS_TARGETING_NEW_NETWORKS")) {
                    this.mIsTargetingNewNetworks = jSONObject.getBoolean("IS_TARGETING_NEW_NETWORKS");
                }
            } catch (JSONException e) {
                Log.e("StandardWifiEntry", "JSONException while converting StandardWifiEntryKey to string: " + e);
            }
        }

        public String toString() {
            JSONObject jSONObject = new JSONObject();
            try {
                ScanResultKey scanResultKey = this.mScanResultKey;
                if (scanResultKey != null) {
                    jSONObject.put("SCAN_RESULT_KEY", scanResultKey.toString());
                }
                String str = this.mSuggestionProfileKey;
                if (str != null) {
                    jSONObject.put("SUGGESTION_PROFILE_KEY", str);
                }
                boolean z = this.mIsNetworkRequest;
                if (z) {
                    jSONObject.put("IS_NETWORK_REQUEST", z);
                }
                boolean z2 = this.mIsTargetingNewNetworks;
                if (z2) {
                    jSONObject.put("IS_TARGETING_NEW_NETWORKS", z2);
                }
            } catch (JSONException e) {
                Log.wtf("StandardWifiEntry", "JSONException while converting StandardWifiEntryKey to string: " + e);
            }
            return "StandardWifiEntry:" + jSONObject.toString();
        }

        /* access modifiers changed from: package-private */
        public ScanResultKey getScanResultKey() {
            return this.mScanResultKey;
        }

        /* access modifiers changed from: package-private */
        public boolean isNetworkRequest() {
            return this.mIsNetworkRequest;
        }

        /* access modifiers changed from: package-private */
        public boolean isTargetingNewNetworks() {
            return this.mIsTargetingNewNetworks;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || StandardWifiEntryKey.class != obj.getClass()) {
                return false;
            }
            StandardWifiEntryKey standardWifiEntryKey = (StandardWifiEntryKey) obj;
            if (!Objects.equals(this.mScanResultKey, standardWifiEntryKey.mScanResultKey) || !TextUtils.equals(this.mSuggestionProfileKey, standardWifiEntryKey.mSuggestionProfileKey) || this.mIsNetworkRequest != standardWifiEntryKey.mIsNetworkRequest) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mScanResultKey, this.mSuggestionProfileKey, Boolean.valueOf(this.mIsNetworkRequest)});
        }
    }

    static class ScanResultKey {
        private Set<Integer> mSecurityTypes;
        private String mSsid;

        ScanResultKey() {
            this.mSecurityTypes = new ArraySet();
        }

        ScanResultKey(String str, List<Integer> list) {
            this.mSecurityTypes = new ArraySet();
            this.mSsid = str;
            for (Integer intValue : list) {
                int intValue2 = intValue.intValue();
                this.mSecurityTypes.add(Integer.valueOf(intValue2));
                if (intValue2 == 0) {
                    this.mSecurityTypes.add(6);
                } else if (intValue2 == 6) {
                    this.mSecurityTypes.add(0);
                } else if (intValue2 == 9) {
                    this.mSecurityTypes.add(3);
                } else if (intValue2 == 2) {
                    this.mSecurityTypes.add(4);
                } else if (intValue2 == 3) {
                    this.mSecurityTypes.add(9);
                } else if (intValue2 == 4) {
                    this.mSecurityTypes.add(2);
                }
            }
        }

        ScanResultKey(ScanResult scanResult) {
            this(scanResult.SSID, Utils.getSecurityTypesFromScanResult(scanResult));
        }

        ScanResultKey(WifiConfiguration wifiConfiguration) {
            this(WifiInfo.sanitizeSsid(wifiConfiguration.SSID), Utils.getSecurityTypesFromWifiConfiguration(wifiConfiguration));
        }

        ScanResultKey(String str) {
            this.mSecurityTypes = new ArraySet();
            try {
                JSONObject jSONObject = new JSONObject(str);
                this.mSsid = jSONObject.getString("SSID");
                JSONArray jSONArray = jSONObject.getJSONArray("SECURITY_TYPES");
                for (int i = 0; i < jSONArray.length(); i++) {
                    this.mSecurityTypes.add(Integer.valueOf(jSONArray.getInt(i)));
                }
            } catch (JSONException e) {
                Log.wtf("StandardWifiEntry", "JSONException while constructing ScanResultKey from string: " + e);
            }
        }

        public String toString() {
            JSONObject jSONObject = new JSONObject();
            try {
                String str = this.mSsid;
                if (str != null) {
                    jSONObject.put("SSID", str);
                }
                if (!this.mSecurityTypes.isEmpty()) {
                    JSONArray jSONArray = new JSONArray();
                    for (Integer intValue : this.mSecurityTypes) {
                        jSONArray.put(intValue.intValue());
                    }
                    jSONObject.put("SECURITY_TYPES", jSONArray);
                }
            } catch (JSONException e) {
                Log.e("StandardWifiEntry", "JSONException while converting ScanResultKey to string: " + e);
            }
            return jSONObject.toString();
        }

        /* access modifiers changed from: package-private */
        public String getSsid() {
            return this.mSsid;
        }

        /* access modifiers changed from: package-private */
        public Set<Integer> getSecurityTypes() {
            return this.mSecurityTypes;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || ScanResultKey.class != obj.getClass()) {
                return false;
            }
            ScanResultKey scanResultKey = (ScanResultKey) obj;
            if (!TextUtils.equals(this.mSsid, scanResultKey.mSsid) || !this.mSecurityTypes.equals(scanResultKey.mSecurityTypes)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mSsid, this.mSecurityTypes});
        }
    }
}
