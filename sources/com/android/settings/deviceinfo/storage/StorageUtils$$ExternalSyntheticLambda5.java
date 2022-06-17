package com.android.settings.deviceinfo.storage;

import android.os.storage.VolumeInfo;
import java.util.function.Predicate;

public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda5 implements Predicate {
    public static final /* synthetic */ StorageUtils$$ExternalSyntheticLambda5 INSTANCE = new StorageUtils$$ExternalSyntheticLambda5();

    private /* synthetic */ StorageUtils$$ExternalSyntheticLambda5() {
    }

    public final boolean test(Object obj) {
        return StorageUtils.isStorageSettingsInterestedVolume((VolumeInfo) obj);
    }
}
