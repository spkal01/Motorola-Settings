package com.android.settings.dashboard.profileselector;

import com.android.settings.deviceinfo.storage.StorageEntry;
import java.util.function.Predicate;

public final /* synthetic */ class ProfileSelectStorageFragment$1$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ StorageEntry f$0;

    public /* synthetic */ ProfileSelectStorageFragment$1$$ExternalSyntheticLambda1(StorageEntry storageEntry) {
        this.f$0 = storageEntry;
    }

    public final boolean test(Object obj) {
        return ((StorageEntry) obj).equals(this.f$0);
    }
}
