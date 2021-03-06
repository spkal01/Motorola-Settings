package com.android.settings.wifi.dpp;

import android.app.Application;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiManager;
import android.util.SparseArray;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class WifiDppInitiatorViewModel extends AndroidViewModel {
    /* access modifiers changed from: private */
    public int[] mBandArray;
    /* access modifiers changed from: private */
    public MutableLiveData<Integer> mEnrolleeSuccessNetworkId;
    /* access modifiers changed from: private */
    public boolean mIsWifiDppHandshaking;
    /* access modifiers changed from: private */
    public MutableLiveData<Integer> mStatusCode;
    /* access modifiers changed from: private */
    public SparseArray<int[]> mTriedChannels;
    /* access modifiers changed from: private */
    public String mTriedSsid;

    public WifiDppInitiatorViewModel(Application application) {
        super(application);
    }

    /* access modifiers changed from: package-private */
    public MutableLiveData<Integer> getEnrolleeSuccessNetworkId() {
        if (this.mEnrolleeSuccessNetworkId == null) {
            this.mEnrolleeSuccessNetworkId = new MutableLiveData<>();
        }
        return this.mEnrolleeSuccessNetworkId;
    }

    /* access modifiers changed from: package-private */
    public MutableLiveData<Integer> getStatusCode() {
        if (this.mStatusCode == null) {
            this.mStatusCode = new MutableLiveData<>();
        }
        return this.mStatusCode;
    }

    /* access modifiers changed from: package-private */
    public String getTriedSsid() {
        return this.mTriedSsid;
    }

    /* access modifiers changed from: package-private */
    public SparseArray<int[]> getTriedChannels() {
        return this.mTriedChannels;
    }

    /* access modifiers changed from: package-private */
    public int[] getBandArray() {
        return this.mBandArray;
    }

    /* access modifiers changed from: package-private */
    public boolean isWifiDppHandshaking() {
        return this.mIsWifiDppHandshaking;
    }

    /* access modifiers changed from: package-private */
    public void startEasyConnectAsConfiguratorInitiator(String str, int i) {
        this.mIsWifiDppHandshaking = true;
        ((WifiManager) getApplication().getSystemService(WifiManager.class)).startEasyConnectAsConfiguratorInitiator(str, i, 0, getApplication().getMainExecutor(), new EasyConnectDelegateCallback());
    }

    /* access modifiers changed from: package-private */
    public void startEasyConnectAsEnrolleeInitiator(String str) {
        this.mIsWifiDppHandshaking = true;
        ((WifiManager) getApplication().getSystemService(WifiManager.class)).startEasyConnectAsEnrolleeInitiator(str, getApplication().getMainExecutor(), new EasyConnectDelegateCallback());
    }

    private class EasyConnectDelegateCallback extends EasyConnectStatusCallback {
        public void onProgress(int i) {
        }

        private EasyConnectDelegateCallback() {
        }

        public void onEnrolleeSuccess(int i) {
            boolean unused = WifiDppInitiatorViewModel.this.mIsWifiDppHandshaking = false;
            WifiDppInitiatorViewModel.this.mEnrolleeSuccessNetworkId.setValue(Integer.valueOf(i));
        }

        public void onConfiguratorSuccess(int i) {
            boolean unused = WifiDppInitiatorViewModel.this.mIsWifiDppHandshaking = false;
            WifiDppInitiatorViewModel.this.mStatusCode.setValue(1);
        }

        public void onFailure(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
            boolean unused = WifiDppInitiatorViewModel.this.mIsWifiDppHandshaking = false;
            String unused2 = WifiDppInitiatorViewModel.this.mTriedSsid = str;
            SparseArray unused3 = WifiDppInitiatorViewModel.this.mTriedChannels = sparseArray;
            int[] unused4 = WifiDppInitiatorViewModel.this.mBandArray = iArr;
            WifiDppInitiatorViewModel.this.mStatusCode.setValue(Integer.valueOf(i));
        }
    }
}
