package com.android.settings.deviceinfo.storage;

import android.os.storage.DiskInfo;
import java.util.function.Function;

public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ StorageUtils$$ExternalSyntheticLambda1 INSTANCE = new StorageUtils$$ExternalSyntheticLambda1();

    private /* synthetic */ StorageUtils$$ExternalSyntheticLambda1() {
    }

    public final Object apply(Object obj) {
        return StorageUtils.lambda$getAllStorageEntries$3((DiskInfo) obj);
    }
}
