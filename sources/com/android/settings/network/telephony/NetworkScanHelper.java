package com.android.settings.network.telephony;

import android.telephony.CellInfo;
import android.telephony.NetworkScan;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessSpecifier;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyScanManager;
import android.util.Log;
import com.android.internal.telephony.CellNetworkScanResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class NetworkScanHelper {
    static final boolean INCREMENTAL_RESULTS = true;
    static final int INCREMENTAL_RESULTS_PERIODICITY_SEC = 3;
    static final int MAX_SEARCH_TIME_SEC = 300;
    static final int SEARCH_PERIODICITY_SEC = 5;
    private final Executor mExecutor;
    private final TelephonyScanManager.NetworkScanCallback mInternalNetworkScanCallback = new NetworkScanCallbackImpl();
    private NetworkScanCallback mNetworkScanCallback;
    private ListenableFuture<List<CellInfo>> mNetworkScanFuture;
    private NetworkScan mNetworkScanRequester;
    private final TelephonyManager mTelephonyManager;

    public interface NetworkScanCallback {
        void onComplete();

        void onError(int i);

        void onResults(List<CellInfo> list);
    }

    /* access modifiers changed from: private */
    public static int convertToScanErrorCode(int i) {
        return i != 2 ? 1 : 10000;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$hasNrSaCapability$0(int i) {
        if (i == 2) {
            return INCREMENTAL_RESULTS;
        }
        return false;
    }

    public NetworkScanHelper(TelephonyManager telephonyManager, NetworkScanCallback networkScanCallback, Executor executor) {
        this.mTelephonyManager = telephonyManager;
        this.mNetworkScanCallback = networkScanCallback;
        this.mExecutor = executor;
    }

    /* access modifiers changed from: package-private */
    public NetworkScanRequest createNetworkScanForPreferredAccessNetworks() {
        long allowedNetworkTypesBitmask = this.mTelephonyManager.getAllowedNetworkTypesBitmask() & 906119;
        ArrayList arrayList = new ArrayList();
        int i = (allowedNetworkTypesBitmask > 0 ? 1 : (allowedNetworkTypesBitmask == 0 ? 0 : -1));
        if (i == 0 || (32843 & allowedNetworkTypesBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(1, (int[]) null, (int[]) null));
        }
        if (i == 0 || (93108 & allowedNetworkTypesBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(2, (int[]) null, (int[]) null));
        }
        if (i == 0 || (397312 & allowedNetworkTypesBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(3, (int[]) null, (int[]) null));
        }
        if (i == 0 || (hasNrSaCapability() && (allowedNetworkTypesBitmask & 524288) != 0)) {
            arrayList.add(new RadioAccessSpecifier(6, (int[]) null, (int[]) null));
            Log.d("NetworkScanHelper", "radioAccessSpecifiers add NGRAN.");
        }
        return new NetworkScanRequest(0, (RadioAccessSpecifier[]) arrayList.toArray(new RadioAccessSpecifier[arrayList.size()]), 5, MAX_SEARCH_TIME_SEC, INCREMENTAL_RESULTS, 3, (ArrayList) null);
    }

    public void startNetworkScan(int i) {
        if (i == 1) {
            SettableFuture create = SettableFuture.create();
            this.mNetworkScanFuture = create;
            Futures.addCallback(create, new FutureCallback<List<CellInfo>>() {
                public void onSuccess(List<CellInfo> list) {
                    NetworkScanHelper.this.onResults(list);
                    NetworkScanHelper.this.onComplete();
                }

                public void onFailure(Throwable th) {
                    if (!(th instanceof CancellationException)) {
                        NetworkScanHelper.this.onError(Integer.parseInt(th.getMessage()));
                    }
                }
            }, MoreExecutors.directExecutor());
            this.mExecutor.execute(new NetworkScanSyncTask(this.mTelephonyManager, (SettableFuture) this.mNetworkScanFuture));
        } else if (i == 2 && this.mNetworkScanRequester == null) {
            NetworkScan requestNetworkScan = this.mTelephonyManager.requestNetworkScan(createNetworkScanForPreferredAccessNetworks(), this.mExecutor, this.mInternalNetworkScanCallback);
            this.mNetworkScanRequester = requestNetworkScan;
            if (requestNetworkScan == null) {
                onError(10000);
            }
        }
    }

    public void stopNetworkQuery() {
        NetworkScan networkScan = this.mNetworkScanRequester;
        if (networkScan != null) {
            networkScan.stopScan();
            this.mNetworkScanRequester = null;
        }
        ListenableFuture<List<CellInfo>> listenableFuture = this.mNetworkScanFuture;
        if (listenableFuture != null) {
            listenableFuture.cancel(INCREMENTAL_RESULTS);
            this.mNetworkScanFuture = null;
        }
    }

    /* access modifiers changed from: private */
    public void onResults(List<CellInfo> list) {
        NetworkScanCallback networkScanCallback = this.mNetworkScanCallback;
        if (networkScanCallback != null) {
            networkScanCallback.onResults(list);
        }
    }

    /* access modifiers changed from: private */
    public void onComplete() {
        NetworkScanCallback networkScanCallback = this.mNetworkScanCallback;
        if (networkScanCallback != null) {
            networkScanCallback.onComplete();
        }
    }

    /* access modifiers changed from: private */
    public void onError(int i) {
        NetworkScanCallback networkScanCallback = this.mNetworkScanCallback;
        if (networkScanCallback != null) {
            networkScanCallback.onError(i);
        }
    }

    private boolean hasNrSaCapability() {
        return Arrays.stream(this.mTelephonyManager.getPhoneCapability().getDeviceNrCapabilities()).anyMatch(NetworkScanHelper$$ExternalSyntheticLambda0.INSTANCE);
    }

    private final class NetworkScanCallbackImpl extends TelephonyScanManager.NetworkScanCallback {
        private NetworkScanCallbackImpl() {
        }

        public void onResults(List<CellInfo> list) {
            Log.d("NetworkScanHelper", "Async scan onResults() results = " + CellInfoUtil.cellInfoListToString(list));
            NetworkScanHelper.this.onResults(list);
        }

        public void onComplete() {
            Log.d("NetworkScanHelper", "async scan onComplete()");
            NetworkScanHelper.this.onComplete();
        }

        public void onError(int i) {
            Log.d("NetworkScanHelper", "async scan onError() errorCode = " + i);
            NetworkScanHelper.this.onError(i);
        }
    }

    private static final class NetworkScanSyncTask implements Runnable {
        private final SettableFuture<List<CellInfo>> mCallback;
        private final TelephonyManager mTelephonyManager;

        NetworkScanSyncTask(TelephonyManager telephonyManager, SettableFuture<List<CellInfo>> settableFuture) {
            this.mTelephonyManager = telephonyManager;
            this.mCallback = settableFuture;
        }

        public void run() {
            CellNetworkScanResult availableNetworks = this.mTelephonyManager.getAvailableNetworks();
            if (availableNetworks.getStatus() == 1) {
                List list = (List) availableNetworks.getOperators().stream().map(new NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0(this.mTelephonyManager.getNetworkOperator())).collect(Collectors.toList());
                Log.d("NetworkScanHelper", "Sync network scan completed, cellInfos = " + CellInfoUtil.cellInfoListToString(list));
                this.mCallback.set(list);
                return;
            }
            Throwable th = new Throwable(Integer.toString(NetworkScanHelper.convertToScanErrorCode(availableNetworks.getStatus())));
            this.mCallback.setException(th);
            Log.d("NetworkScanHelper", "Sync network scan error, ex = " + th);
        }
    }

    public void removeCallBack() {
        this.mNetworkScanCallback = null;
    }
}
