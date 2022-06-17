package com.android.settings.network;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.settings.network.helper.SelectableSubscriptions;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settings.network.helper.SubscriptionGrouping;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MobileNetworkSummaryStatus {
    private boolean mDisableReEntranceUpdate;
    private Future<Boolean> mIsEuiccConfiguable;
    private Boolean mIsEuiccConfiguableCache;
    private Future<Boolean> mIsPsimDisableSupported;
    private Boolean mIsPsimDisableSupportedCache;
    private List<SubscriptionAnnotation> mSubscriptionList;
    private Future<Map<Integer, CharSequence>> mUniqueNameMapping;
    private Map<Integer, CharSequence> mUniqueNameMappingCache;

    public void update(Context context, Consumer<MobileNetworkSummaryStatus> consumer) {
        if (this.mDisableReEntranceUpdate) {
            Log.d("MobileNetworkSummaryStatus", "network summary query ignored");
            if (consumer != null) {
                consumer.accept(this);
                return;
            }
            return;
        }
        this.mDisableReEntranceUpdate = true;
        Log.d("MobileNetworkSummaryStatus", "network summary query");
        this.mIsEuiccConfiguable = ThreadUtils.postOnBackgroundThread((Callable) new MobileNetworkSummaryStatus$$ExternalSyntheticLambda1(this, context));
        this.mUniqueNameMapping = ThreadUtils.postOnBackgroundThread((Callable) new MobileNetworkSummaryStatus$$ExternalSyntheticLambda2(this, context));
        this.mIsPsimDisableSupported = ThreadUtils.postOnBackgroundThread((Callable) new MobileNetworkSummaryStatus$$ExternalSyntheticLambda0(this, context));
        this.mSubscriptionList = getSubscriptions(context);
        if (consumer != null) {
            consumer.accept(this);
        }
        this.mDisableReEntranceUpdate = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Object lambda$update$0(Context context) throws Exception {
        return Boolean.valueOf(isEuiccConfiguable(context));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Object lambda$update$2(Context context) throws Exception {
        return Boolean.valueOf(isPhysicalSimDisableSupported(context));
    }

    public List<SubscriptionAnnotation> getSubscriptionList() {
        return this.mSubscriptionList;
    }

    public CharSequence getDisplayName(int i) {
        Future<Map<Integer, CharSequence>> future = this.mUniqueNameMapping;
        if (future != null) {
            try {
                this.mUniqueNameMappingCache = future.get();
            } catch (Exception e) {
                Log.w("MobileNetworkSummaryStatus", "Fail to get display names", e);
            }
            this.mUniqueNameMapping = null;
        }
        Map<Integer, CharSequence> map = this.mUniqueNameMappingCache;
        if (map == null) {
            return null;
        }
        return map.get(Integer.valueOf(i));
    }

    public boolean isEuiccConfigSupport() {
        Future<Boolean> future = this.mIsEuiccConfiguable;
        if (future != null) {
            try {
                this.mIsEuiccConfiguableCache = future.get();
            } catch (Exception e) {
                Log.w("MobileNetworkSummaryStatus", "Fail to get euicc config status", e);
            }
            this.mIsEuiccConfiguable = null;
        }
        Boolean bool = this.mIsEuiccConfiguableCache;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isPhysicalSimDisableSupport() {
        Future<Boolean> future = this.mIsPsimDisableSupported;
        if (future != null) {
            try {
                this.mIsPsimDisableSupportedCache = future.get();
            } catch (Exception e) {
                Log.w("MobileNetworkSummaryStatus", "Fail to get pSIM disable support", e);
            }
            this.mIsPsimDisableSupported = null;
        }
        Boolean bool = this.mIsPsimDisableSupportedCache;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    private List<SubscriptionAnnotation> getSubscriptions(Context context) {
        return (List) new SelectableSubscriptions(context, true).addFinisher(new SubscriptionGrouping()).call().stream().filter(MobileNetworkSummaryStatus$$ExternalSyntheticLambda3.INSTANCE).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    /* renamed from: getUniqueNameForDisplay */
    public Map<Integer, CharSequence> lambda$update$1(Context context) {
        return SubscriptionUtil.getUniqueSubscriptionDisplayNames(context);
    }

    private boolean isPhysicalSimDisableSupported(Context context) {
        return SubscriptionUtil.showToggleForPhysicalSim((SubscriptionManager) context.getSystemService(SubscriptionManager.class));
    }

    private boolean isEuiccConfiguable(Context context) {
        return MobileNetworkUtils.showEuiccSettingsDetecting(context).booleanValue();
    }
}
