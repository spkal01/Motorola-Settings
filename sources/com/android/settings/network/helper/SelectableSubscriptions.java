package com.android.settings.network.helper;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.Keep;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SelectableSubscriptions implements Callable<List<SubscriptionAnnotation>> {
    private Context mContext;
    private Predicate<SubscriptionAnnotation> mFilter;
    private Function<List<SubscriptionAnnotation>, List<SubscriptionAnnotation>> mFinisher;
    private Supplier<List<SubscriptionInfo>> mSubscriptions;

    /* access modifiers changed from: private */
    public static /* synthetic */ List lambda$new$4(List list) {
        return list;
    }

    public SelectableSubscriptions(Context context, boolean z) {
        Supplier<List<SubscriptionInfo>> supplier;
        this.mContext = context;
        if (z) {
            supplier = new SelectableSubscriptions$$ExternalSyntheticLambda8(this, context);
        } else {
            supplier = new SelectableSubscriptions$$ExternalSyntheticLambda9(this, context);
        }
        this.mSubscriptions = supplier;
        if (z) {
            this.mFilter = SelectableSubscriptions$$ExternalSyntheticLambda7.INSTANCE;
        } else {
            this.mFilter = SelectableSubscriptions$$ExternalSyntheticLambda6.INSTANCE;
        }
        this.mFinisher = SelectableSubscriptions$$ExternalSyntheticLambda3.INSTANCE;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$2(SubscriptionAnnotation subscriptionAnnotation) {
        if (subscriptionAnnotation.isExisted()) {
            return true;
        }
        if (subscriptionAnnotation.getType() != 2 || !subscriptionAnnotation.isDisplayAllowed()) {
            return false;
        }
        return true;
    }

    public SelectableSubscriptions addFinisher(UnaryOperator<List<SubscriptionAnnotation>> unaryOperator) {
        this.mFinisher = this.mFinisher.andThen(unaryOperator);
        return this;
    }

    public List<SubscriptionAnnotation> call() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        try {
            Future postOnBackgroundThread = ThreadUtils.postOnBackgroundThread((Callable) new QueryEsimCardId(telephonyManager));
            Future postOnBackgroundThread2 = ThreadUtils.postOnBackgroundThread((Callable) new QuerySimSlotIndex(telephonyManager, true, true));
            Future postOnBackgroundThread3 = ThreadUtils.postOnBackgroundThread((Callable) new QuerySimSlotIndex(telephonyManager, false, true));
            List list = this.mSubscriptions.get();
            return (List) IntStream.range(0, list.size()).mapToObj(new SelectableSubscriptions$$ExternalSyntheticLambda4(list)).map(new SelectableSubscriptions$$ExternalSyntheticLambda0(this, atomicToList((AtomicIntegerArray) postOnBackgroundThread.get()), atomicToList((AtomicIntegerArray) postOnBackgroundThread2.get()), atomicToList((AtomicIntegerArray) postOnBackgroundThread3.get()))).filter(this.mFilter).collect(Collectors.collectingAndThen(Collectors.toList(), this.mFinisher));
        } catch (Exception e) {
            Log.w("SelectableSubscriptions", "Fail to request subIdList", e);
            return Collections.emptyList();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ SubscriptionAnnotation.Builder lambda$call$5(List list, int i) {
        return new SubscriptionAnnotation.Builder(list, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ SubscriptionAnnotation lambda$call$6(List list, List list2, List list3, SubscriptionAnnotation.Builder builder) {
        return builder.build(this.mContext, list, list2, list3);
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getSubInfoList(Context context, Function<SubscriptionManager, List<SubscriptionInfo>> function) {
        SubscriptionManager subscriptionManager = getSubscriptionManager(context);
        return subscriptionManager == null ? Collections.emptyList() : function.apply(subscriptionManager);
    }

    /* access modifiers changed from: protected */
    public SubscriptionManager getSubscriptionManager(Context context) {
        return (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    /* access modifiers changed from: protected */
    /* renamed from: getAvailableSubInfoList */
    public List<SubscriptionInfo> lambda$new$0(Context context) {
        return getSubInfoList(context, SelectableSubscriptions$$ExternalSyntheticLambda2.INSTANCE);
    }

    /* access modifiers changed from: protected */
    /* renamed from: getActiveSubInfoList */
    public List<SubscriptionInfo> lambda$new$1(Context context) {
        return getSubInfoList(context, SelectableSubscriptions$$ExternalSyntheticLambda1.INSTANCE);
    }

    @Keep
    protected static List<Integer> atomicToList(AtomicIntegerArray atomicIntegerArray) {
        if (atomicIntegerArray == null) {
            return Collections.emptyList();
        }
        return (List) IntStream.range(0, atomicIntegerArray.length()).map(new SelectableSubscriptions$$ExternalSyntheticLambda5(atomicIntegerArray)).boxed().collect(Collectors.toList());
    }
}
