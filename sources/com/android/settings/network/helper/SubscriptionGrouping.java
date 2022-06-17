package com.android.settings.network.helper;

import android.os.ParcelUuid;
import android.util.Log;
import androidx.annotation.Keep;
import com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda9;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SubscriptionGrouping implements UnaryOperator<List<SubscriptionAnnotation>> {
    public List<SubscriptionAnnotation> apply(List<SubscriptionAnnotation> list) {
        Log.d("SubscriptionGrouping", "Grouping " + list);
        Map map = (Map) list.stream().filter(SubscriptionGrouping$$ExternalSyntheticLambda3.INSTANCE).collect(Collectors.groupingBy(new SubscriptionGrouping$$ExternalSyntheticLambda2(this)));
        map.replaceAll(new SubscriptionGrouping$$ExternalSyntheticLambda1(this));
        return (List) map.values().stream().flatMap(DashboardFragment$$ExternalSyntheticLambda9.INSTANCE).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ List lambda$apply$1(ParcelUuid parcelUuid, List list) {
        return (parcelUuid == SubscriptionAnnotation.EMPTY_UUID || list.size() <= 1) ? list : Collections.singletonList(selectBestFromList(list));
    }

    /* access modifiers changed from: protected */
    @Keep
    /* renamed from: getGroupUuid */
    public ParcelUuid lambda$apply$0(SubscriptionAnnotation subscriptionAnnotation) {
        ParcelUuid groupUuid = subscriptionAnnotation.getGroupUuid();
        return groupUuid == null ? SubscriptionAnnotation.EMPTY_UUID : groupUuid;
    }

    /* access modifiers changed from: protected */
    public SubscriptionAnnotation selectBestFromList(List<SubscriptionAnnotation> list) {
        return (SubscriptionAnnotation) list.stream().sorted(SubscriptionGrouping$$ExternalSyntheticLambda0.INSTANCE.thenComparingInt(SubscriptionGrouping$$ExternalSyntheticLambda5.INSTANCE).thenComparingInt(new SubscriptionGrouping$$ExternalSyntheticLambda4(list))).findFirst().orElse((Object) null);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$selectBestFromList$2(SubscriptionAnnotation subscriptionAnnotation, SubscriptionAnnotation subscriptionAnnotation2) {
        if (subscriptionAnnotation.isDisplayAllowed() != subscriptionAnnotation2.isDisplayAllowed()) {
            if (subscriptionAnnotation.isDisplayAllowed()) {
                return -1;
            }
            return 1;
        } else if (subscriptionAnnotation.isActive() != subscriptionAnnotation2.isActive()) {
            if (subscriptionAnnotation.isActive()) {
                return -1;
            }
            return 1;
        } else if (subscriptionAnnotation.isExisted() == subscriptionAnnotation2.isExisted()) {
            return 0;
        } else {
            if (subscriptionAnnotation.isExisted()) {
                return -1;
            }
            return 1;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$selectBestFromList$3(SubscriptionAnnotation subscriptionAnnotation) {
        return -subscriptionAnnotation.getType();
    }
}
