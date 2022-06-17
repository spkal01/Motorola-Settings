package com.android.settings.deviceinfo.storage;

import android.os.storage.StorageManager;
import android.os.storage.VolumeRecord;
import java.util.function.Predicate;

public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda3 implements Predicate {
    public final /* synthetic */ StorageManager f$0;

    public /* synthetic */ StorageUtils$$ExternalSyntheticLambda3(StorageManager storageManager) {
        this.f$0 = storageManager;
    }

    public final boolean test(Object obj) {
        return StorageUtils.isVolumeRecordMissed(this.f$0, (VolumeRecord) obj);
    }
}
