package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.os.storage.VolumeInfo;
import java.util.function.Function;

public final /* synthetic */ class StorageUtils$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ StorageUtils$$ExternalSyntheticLambda0(Context context) {
        this.f$0 = context;
    }

    public final Object apply(Object obj) {
        return StorageUtils.lambda$getAllStorageEntries$1(this.f$0, (VolumeInfo) obj);
    }
}
