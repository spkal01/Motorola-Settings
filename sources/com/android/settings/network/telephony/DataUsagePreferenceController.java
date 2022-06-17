package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C1992R$string;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.datausage.lib.DataUsageLib;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class DataUsagePreferenceController extends TelephonyBasePreferenceController {
    private static final String LOG_TAG = "DataUsagePreferCtrl";
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private AtomicReference<NetworkTemplate> mTemplate = new AtomicReference<>();
    private Future<NetworkTemplate> mTemplateFuture;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DataUsagePreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        return SubscriptionManager.isValidSubscriptionId(i) ^ true ? 1 : 0;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Intent intent = new Intent("android.settings.MOBILE_DATA_USAGE");
        intent.putExtra("network_template", getNetworkTemplate());
        intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
        this.mContext.startActivity(intent);
        return true;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            preference.setEnabled(false);
        } else {
            this.mExecutor.execute(new DataUsagePreferenceController$$ExternalSyntheticLambda0(this, preference));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(Preference preference) {
        this.mHandler.post(new DataUsagePreferenceController$$ExternalSyntheticLambda1(getDataUsageSummary(this.mContext, this.mSubId), preference));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateState$0(CharSequence charSequence, Preference preference) {
        if (charSequence == null) {
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(charSequence);
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTemplate.set((Object) null);
        this.mTemplateFuture = ThreadUtils.postOnBackgroundThread((Callable) new DataUsagePreferenceController$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Object lambda$init$2() throws Exception {
        return fetchMobileTemplate(this.mContext, this.mSubId);
    }

    private NetworkTemplate fetchMobileTemplate(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return null;
        }
        return DataUsageLib.getMobileTemplate(context, i);
    }

    private NetworkTemplate getNetworkTemplate() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        NetworkTemplate networkTemplate = this.mTemplate.get();
        if (networkTemplate != null) {
            return networkTemplate;
        }
        try {
            NetworkTemplate networkTemplate2 = this.mTemplateFuture.get();
            try {
                this.mTemplate.set(networkTemplate2);
                return networkTemplate2;
            } catch (InterruptedException | NullPointerException | ExecutionException e) {
                e = e;
                networkTemplate = networkTemplate2;
            }
        } catch (InterruptedException | NullPointerException | ExecutionException e2) {
            e = e2;
            Log.e(LOG_TAG, "Fail to get data usage template", e);
            return networkTemplate;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public DataUsageController.DataUsageInfo getDataUsageInfo(DataUsageController dataUsageController) {
        return dataUsageController.getDataUsageInfo(getNetworkTemplate());
    }

    private CharSequence getDataUsageSummary(Context context, int i) {
        DataUsageController dataUsageController = new DataUsageController(context);
        dataUsageController.setSubscriptionId(i);
        DataUsageController.DataUsageInfo dataUsageInfo = getDataUsageInfo(dataUsageController);
        if (dataUsageInfo == null) {
            return null;
        }
        long j = dataUsageInfo.usageLevel;
        if (j <= 0) {
            j = dataUsageController.getHistoricalUsageLevel(getNetworkTemplate());
        }
        if (j <= 0) {
            return null;
        }
        return context.getString(C1992R$string.data_usage_template, new Object[]{DataUsageUtils.formatDataUsage(context, j), dataUsageInfo.period});
    }
}
