package com.android.settings.wifi.addappnetworks;

import com.android.settings.wifi.addappnetworks.AddAppNetworksFragment;
import java.util.function.Predicate;

public final /* synthetic */ class AddAppNetworksFragment$$ExternalSyntheticLambda4 implements Predicate {
    public final /* synthetic */ AddAppNetworksFragment f$0;

    public /* synthetic */ AddAppNetworksFragment$$ExternalSyntheticLambda4(AddAppNetworksFragment addAppNetworksFragment) {
        this.f$0 = addAppNetworksFragment;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$filterByAdminRestrictions$1((AddAppNetworksFragment.UiConfigurationItem) obj);
    }
}
