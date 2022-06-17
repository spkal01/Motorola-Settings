package com.android.settings.network.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.telephony.OperatorInfo;
import com.android.settings.C1980R$bool;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.telephony.NetworkScanHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.MotoTelephonyFeature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkSelectSettings extends DashboardFragment {
    private final NetworkScanHelper.NetworkScanCallback mCallback = new NetworkScanHelper.NetworkScanCallback() {
        public void onResults(List<CellInfo> list) {
            NetworkSelectSettings.this.mHandler.obtainMessage(2, list).sendToTarget();
        }

        public void onComplete() {
            NetworkSelectSettings.this.mHandler.obtainMessage(4).sendToTarget();
        }

        public void onError(int i) {
            NetworkSelectSettings.this.mHandler.obtainMessage(3, i, 0).sendToTarget();
        }
    };
    List<CellInfo> mCellInfoList;
    private List<String> mForbiddenPlmns;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i;
            int i2 = message.what;
            if (i2 == 1) {
                boolean booleanValue = ((Boolean) message.obj).booleanValue();
                NetworkSelectSettings.this.stopNetworkQuery();
                NetworkSelectSettings.this.setProgressBarVisible(false);
                NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                NetworkOperatorPreference networkOperatorPreference = NetworkSelectSettings.this.mSelectedPreference;
                if (networkOperatorPreference != null) {
                    if (booleanValue) {
                        i = C1992R$string.network_connected;
                    } else {
                        i = C1992R$string.network_could_not_connect;
                    }
                    networkOperatorPreference.setSummary(i);
                } else {
                    Log.e("NetworkSelectSettings", "No preference to update!");
                }
                boolean unused = NetworkSelectSettings.this.requestManualNetworkSelect = false;
            } else if (i2 == 2) {
                List list = (List) message.obj;
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    Log.d("NetworkSelectSettings", "CellInfoList (drop): " + CellInfoUtil.cellInfoListToString(new ArrayList(list)));
                    return;
                }
                NetworkSelectSettings.access$410(NetworkSelectSettings.this);
                if (NetworkSelectSettings.this.mWaitingForNumberOfScanResults <= 0 && !NetworkSelectSettings.this.isResumed()) {
                    NetworkSelectSettings.this.stopNetworkQuery();
                }
                NetworkSelectSettings networkSelectSettings = NetworkSelectSettings.this;
                networkSelectSettings.mCellInfoList = networkSelectSettings.doAggregation(list);
                Log.d("NetworkSelectSettings", "CellInfoList: " + CellInfoUtil.cellInfoListToString(NetworkSelectSettings.this.mCellInfoList));
                List<CellInfo> list2 = NetworkSelectSettings.this.mCellInfoList;
                if (list2 != null && list2.size() != 0) {
                    NetworkOperatorPreference updateAllPreferenceCategory = NetworkSelectSettings.this.updateAllPreferenceCategory();
                    if (updateAllPreferenceCategory != null) {
                        NetworkSelectSettings networkSelectSettings2 = NetworkSelectSettings.this;
                        if (networkSelectSettings2.mSelectedPreference != null) {
                            networkSelectSettings2.mSelectedPreference = updateAllPreferenceCategory;
                        }
                    } else if (NetworkSelectSettings.this.requestManualNetworkSelect && updateAllPreferenceCategory == null) {
                        NetworkSelectSettings.this.mSelectedPreference.setSummary(C1992R$string.network_connecting);
                    }
                    NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                } else if (!NetworkSelectSettings.this.requestManualNetworkSelect) {
                    NetworkSelectSettings.this.addMessagePreference(C1992R$string.empty_networks_list);
                    NetworkSelectSettings.this.setProgressBarVisible(true);
                }
            } else if (i2 == 3) {
                int i3 = message.arg1;
                Log.d("NetworkSelectSettings", "errCode:" + i3 + ", retryCount:" + NetworkSelectSettings.this.retryCount);
                if (NetworkSelectSettings.this.mUseNewApi || i3 != 1 || NetworkSelectSettings.this.retryCount >= 30) {
                    NetworkSelectSettings.this.stopNetworkQuery();
                    Log.i("NetworkSelectSettings", "Network scan failure " + message.arg1 + ": scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                    if (NetworkSelectSettings.this.mRequestIdManualNetworkScan >= NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                        if (NetworkSelectSettings.this.requestManualNetworkSelect) {
                            NetworkSelectSettings.this.clearPreferenceSummary();
                            NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                            return;
                        }
                        NetworkSelectSettings.this.addMessagePreference(C1992R$string.network_query_error);
                        return;
                    }
                    return;
                }
                NetworkSelectSettings.this.mHandler.sendEmptyMessageDelayed(100, 2000);
            } else if (i2 == 4) {
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.d("NetworkSelectSettings", "Network scan complete: scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan >= NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    if (NetworkSelectSettings.this.requestManualNetworkSelect) {
                        NetworkSelectSettings.this.clearPreferenceSummary();
                        NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                        return;
                    }
                    NetworkSelectSettings networkSelectSettings3 = NetworkSelectSettings.this;
                    if (networkSelectSettings3.mCellInfoList == null) {
                        networkSelectSettings3.addMessagePreference(C1992R$string.empty_networks_list);
                    }
                }
            } else if (i2 == 100) {
                NetworkSelectSettings.access$608(NetworkSelectSettings.this);
                NetworkSelectSettings.this.mNetworkScanHelper.startNetworkScan(1);
            }
        }
    };
    boolean mIsAggregationEnabled = false;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private final ExecutorService mNetworkScanExecutor = Executors.newFixedThreadPool(1);
    /* access modifiers changed from: private */
    public NetworkScanHelper mNetworkScanHelper;
    PreferenceCategory mPreferenceCategory;
    private View mProgressHeader;
    /* access modifiers changed from: private */
    public long mRequestIdManualNetworkScan;
    /* access modifiers changed from: private */
    public long mRequestIdManualNetworkSelect;
    NetworkOperatorPreference mSelectedPreference;
    private boolean mShow4GForLTE = false;
    private Preference mStatusMessagePreference;
    private int mSubId = -1;
    TelephonyManager mTelephonyManager;
    /* access modifiers changed from: private */
    public boolean mUseNewApi;
    /* access modifiers changed from: private */
    public long mWaitingForNumberOfScanResults;
    /* access modifiers changed from: private */
    public boolean requestManualNetworkSelect = false;
    /* access modifiers changed from: private */
    public int retryCount;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "NetworkSelectSettings";
    }

    public int getMetricsCategory() {
        return 1581;
    }

    static /* synthetic */ long access$410(NetworkSelectSettings networkSelectSettings) {
        long j = networkSelectSettings.mWaitingForNumberOfScanResults;
        networkSelectSettings.mWaitingForNumberOfScanResults = j - 1;
        return j;
    }

    static /* synthetic */ int access$608(NetworkSelectSettings networkSelectSettings) {
        int i = networkSelectSettings.retryCount;
        networkSelectSettings.retryCount = i + 1;
        return i;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUseNewApi = getContext().getResources().getBoolean(17891577);
        this.mSubId = getArguments().getInt("android.provider.extra.SUB_ID");
        this.mUseNewApi |= MotoMobileNetworkUtils.modemSupports5g(getContext(), this.mSubId);
        this.mPreferenceCategory = (PreferenceCategory) findPreference("network_operators_preference");
        Preference preference = new Preference(getContext());
        this.mStatusMessagePreference = preference;
        preference.setSelectable(false);
        this.mSelectedPreference = null;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        this.mNetworkScanHelper = new NetworkScanHelper(createForSubscriptionId, this.mCallback, this.mNetworkScanExecutor);
        PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        if (configForSubId != null) {
            this.mShow4GForLTE = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        this.mIsAggregationEnabled = getContext().getResources().getBoolean(C1980R$bool.config_network_selection_list_aggregation_enabled);
        Log.d("NetworkSelectSettings", "init: mUseNewApi:" + this.mUseNewApi + " ,mIsAggregationEnabled:" + this.mIsAggregationEnabled);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity() != null) {
            this.mProgressHeader = setPinnedHeaderView(C1987R$layout.progress_header).findViewById(C1985R$id.progress_bar_animation);
            setProgressBarVisible(false);
        }
        forceUpdateConnectedPreferenceCategory();
    }

    public void onStart() {
        super.onStart();
        updateForbiddenPlmns();
        if (!isProgressBarVisible() && this.mWaitingForNumberOfScanResults <= 0) {
            startNetworkQuery();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateForbiddenPlmns() {
        List<String> list;
        if (!MotoTelephonyFeature.isFtr5609Enabled(getContext(), this.mSubId)) {
            String[] forbiddenPlmns = this.mTelephonyManager.getForbiddenPlmns();
            if (forbiddenPlmns != null) {
                list = Arrays.asList(forbiddenPlmns);
            } else {
                list = new ArrayList<>();
            }
            this.mForbiddenPlmns = list;
            if (MotoMnsLog.DEBUG) {
                MotoMnsLog.logd("NetworkSelectSettings", "forbiddenPlmn size:" + this.mForbiddenPlmns.size());
                for (String logd : this.mForbiddenPlmns) {
                    MotoMnsLog.logd("NetworkSelectSettings", logd);
                }
            }
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mWaitingForNumberOfScanResults <= 0) {
            stopNetworkQuery();
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference != this.mSelectedPreference) {
            stopNetworkQuery();
            clearPreferenceSummary();
            NetworkOperatorPreference networkOperatorPreference = this.mSelectedPreference;
            if (networkOperatorPreference != null) {
                networkOperatorPreference.setSummary(C1992R$string.network_disconnected);
            }
            NetworkOperatorPreference networkOperatorPreference2 = (NetworkOperatorPreference) preference;
            this.mSelectedPreference = networkOperatorPreference2;
            networkOperatorPreference2.setSummary(C1992R$string.network_connecting);
            this.mMetricsFeatureProvider.action(getContext(), 1210, (Pair<Integer, Object>[]) new Pair[0]);
            setProgressBarVisible(true);
            getPreferenceScreen().setEnabled(false);
            this.requestManualNetworkSelect = true;
            this.mRequestIdManualNetworkSelect = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2;
            ThreadUtils.postOnBackgroundThread((Runnable) new NetworkSelectSettings$$ExternalSyntheticLambda0(this, this.mSelectedPreference.getOperatorInfo()));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceTreeClick$0(OperatorInfo operatorInfo) {
        Message obtainMessage = this.mHandler.obtainMessage(1);
        obtainMessage.obj = Boolean.valueOf(this.mTelephonyManager.setNetworkSelectionModeManual(operatorInfo, true));
        obtainMessage.sendToTarget();
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.choose_network;
    }

    /* access modifiers changed from: package-private */
    public List<CellInfo> doAggregation(List<CellInfo> list) {
        if (!this.mIsAggregationEnabled) {
            Log.d("NetworkSelectSettings", "no aggregation");
            return new ArrayList(list);
        }
        HashMap hashMap = new HashMap();
        for (CellInfo next : list) {
            String operatorNumeric = CellInfoUtil.getOperatorNumeric(next);
            if (next.isRegistered() || !hashMap.containsKey(operatorNumeric)) {
                hashMap.put(operatorNumeric, next);
            } else if (!((CellInfo) hashMap.get(operatorNumeric)).isRegistered() && getRATLevel((CellInfo) hashMap.get(operatorNumeric)) <= getRATLevel(next)) {
                hashMap.put(operatorNumeric, next);
            }
        }
        ArrayList arrayList = new ArrayList();
        for (CellInfo next2 : list) {
            if (hashMap.containsValue(next2)) {
                arrayList.add(next2);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x007d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.settings.network.telephony.NetworkOperatorPreference updateAllPreferenceCategory() {
        /*
            r12 = this;
            androidx.preference.PreferenceCategory r0 = r12.mPreferenceCategory
            int r0 = r0.getPreferenceCount()
        L_0x0006:
            java.util.List<android.telephony.CellInfo> r1 = r12.mCellInfoList
            int r1 = r1.size()
            if (r0 <= r1) goto L_0x001a
            int r0 = r0 + -1
            androidx.preference.PreferenceCategory r1 = r12.mPreferenceCategory
            androidx.preference.Preference r2 = r1.getPreference(r0)
            r1.removePreference(r2)
            goto L_0x0006
        L_0x001a:
            r1 = 0
            r2 = 0
            r3 = r1
            r4 = r2
        L_0x001e:
            java.util.List<android.telephony.CellInfo> r5 = r12.mCellInfoList
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0083
            java.util.List<android.telephony.CellInfo> r5 = r12.mCellInfoList
            java.lang.Object r5 = r5.get(r3)
            r8 = r5
            android.telephony.CellInfo r8 = (android.telephony.CellInfo) r8
            if (r3 >= r0) goto L_0x0046
            androidx.preference.PreferenceCategory r5 = r12.mPreferenceCategory
            androidx.preference.Preference r5 = r5.getPreference(r3)
            boolean r6 = r5 instanceof com.android.settings.network.telephony.NetworkOperatorPreference
            if (r6 == 0) goto L_0x0041
            com.android.settings.network.telephony.NetworkOperatorPreference r5 = (com.android.settings.network.telephony.NetworkOperatorPreference) r5
            r5.updateCell(r8)
            goto L_0x0047
        L_0x0041:
            androidx.preference.PreferenceCategory r6 = r12.mPreferenceCategory
            r6.removePreference(r5)
        L_0x0046:
            r5 = r2
        L_0x0047:
            if (r5 != 0) goto L_0x0061
            com.android.settings.network.telephony.NetworkOperatorPreference r5 = new com.android.settings.network.telephony.NetworkOperatorPreference
            android.content.Context r7 = r12.getPrefContext()
            java.util.List<java.lang.String> r9 = r12.mForbiddenPlmns
            boolean r10 = r12.mShow4GForLTE
            int r11 = r12.mSubId
            r6 = r5
            r6.<init>((android.content.Context) r7, (android.telephony.CellInfo) r8, (java.util.List<java.lang.String>) r9, (boolean) r10, (int) r11)
            r5.setOrder(r3)
            androidx.preference.PreferenceCategory r6 = r12.mPreferenceCategory
            r6.addPreference(r5)
        L_0x0061:
            java.lang.String r6 = r5.getOperatorName()
            r5.setKey(r6)
            java.util.List<android.telephony.CellInfo> r6 = r12.mCellInfoList
            java.lang.Object r6 = r6.get(r3)
            android.telephony.CellInfo r6 = (android.telephony.CellInfo) r6
            boolean r6 = r6.isRegistered()
            if (r6 == 0) goto L_0x007d
            int r4 = com.android.settings.C1992R$string.network_connected
            r5.setSummary((int) r4)
            r4 = r5
            goto L_0x0080
        L_0x007d:
            r5.setSummary((java.lang.CharSequence) r2)
        L_0x0080:
            int r3 = r3 + 1
            goto L_0x001e
        L_0x0083:
            java.util.List<android.telephony.CellInfo> r0 = r12.mCellInfoList
            int r0 = r0.size()
            if (r1 >= r0) goto L_0x00aa
            java.util.List<android.telephony.CellInfo> r0 = r12.mCellInfoList
            java.lang.Object r0 = r0.get(r1)
            android.telephony.CellInfo r0 = (android.telephony.CellInfo) r0
            com.android.settings.network.telephony.NetworkOperatorPreference r2 = r12.mSelectedPreference
            if (r2 == 0) goto L_0x00a7
            boolean r0 = r2.isSameCell(r0)
            if (r0 == 0) goto L_0x00a7
            androidx.preference.PreferenceCategory r0 = r12.mPreferenceCategory
            androidx.preference.Preference r0 = r0.getPreference(r1)
            com.android.settings.network.telephony.NetworkOperatorPreference r0 = (com.android.settings.network.telephony.NetworkOperatorPreference) r0
            r12.mSelectedPreference = r0
        L_0x00a7:
            int r1 = r1 + 1
            goto L_0x0083
        L_0x00aa:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.NetworkSelectSettings.updateAllPreferenceCategory():com.android.settings.network.telephony.NetworkOperatorPreference");
    }

    private void forceUpdateConnectedPreferenceCategory() {
        ServiceState serviceState;
        List<NetworkRegistrationInfo> networkRegistrationInfoListForTransportType;
        int dataState = this.mTelephonyManager.getDataState();
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (dataState == 2 && (serviceState = telephonyManager.getServiceState()) != null && (networkRegistrationInfoListForTransportType = serviceState.getNetworkRegistrationInfoListForTransportType(1)) != null && networkRegistrationInfoListForTransportType.size() != 0) {
            if (this.mForbiddenPlmns == null) {
                updateForbiddenPlmns();
            }
            for (NetworkRegistrationInfo cellIdentity : networkRegistrationInfoListForTransportType) {
                CellIdentity cellIdentity2 = cellIdentity.getCellIdentity();
                if (cellIdentity2 != null) {
                    NetworkOperatorPreference networkOperatorPreference = new NetworkOperatorPreference(getPrefContext(), cellIdentity2, this.mForbiddenPlmns, this.mShow4GForLTE, this.mSubId);
                    if (!networkOperatorPreference.isForbiddenNetwork()) {
                        networkOperatorPreference.setSummary(C1992R$string.network_connected);
                        networkOperatorPreference.setIcon(4);
                        this.mPreferenceCategory.addPreference(networkOperatorPreference);
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void clearPreferenceSummary() {
        int preferenceCount = this.mPreferenceCategory.getPreferenceCount();
        while (preferenceCount > 0) {
            preferenceCount--;
            ((NetworkOperatorPreference) this.mPreferenceCategory.getPreference(preferenceCount)).setSummary((CharSequence) null);
        }
    }

    private long getNewRequestId() {
        return Math.max(this.mRequestIdManualNetworkSelect, this.mRequestIdManualNetworkScan) + 1;
    }

    private boolean isProgressBarVisible() {
        View view = this.mProgressHeader;
        if (view != null && view.getVisibility() == 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void setProgressBarVisible(boolean z) {
        View view = this.mProgressHeader;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    /* access modifiers changed from: private */
    public void addMessagePreference(int i) {
        setProgressBarVisible(false);
        this.mStatusMessagePreference.setTitle(i);
        this.mPreferenceCategory.removeAll();
        this.mPreferenceCategory.addPreference(this.mStatusMessagePreference);
    }

    private void startNetworkQuery() {
        int i = 1;
        setProgressBarVisible(true);
        if (this.mNetworkScanHelper != null) {
            this.mRequestIdManualNetworkScan = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2;
            NetworkScanHelper networkScanHelper = this.mNetworkScanHelper;
            if (this.mUseNewApi) {
                i = 2;
            }
            networkScanHelper.startNetworkScan(i);
            if (!this.mUseNewApi) {
                getPreferenceScreen().setEnabled(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopNetworkQuery() {
        setProgressBarVisible(false);
        NetworkScanHelper networkScanHelper = this.mNetworkScanHelper;
        if (networkScanHelper != null) {
            this.mWaitingForNumberOfScanResults = 0;
            networkScanHelper.stopNetworkQuery();
        }
        this.retryCount = 0;
    }

    public void onDestroy() {
        stopNetworkQuery();
        this.mNetworkScanExecutor.shutdown();
        super.onDestroy();
        this.mNetworkScanHelper.removeCallBack();
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    private int getRATLevel(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return 1;
        }
        if ((cellInfo instanceof CellInfoCdma) || (cellInfo instanceof CellInfoWcdma) || (cellInfo instanceof CellInfoTdscdma)) {
            return 2;
        }
        if (cellInfo instanceof CellInfoLte) {
            return 3;
        }
        return cellInfo instanceof CellInfoNr ? 4 : 0;
    }
}
