package com.android.settings.deviceinfo.storage;

import android.content.pm.UserInfo;
import java.util.Comparator;

public final /* synthetic */ class StorageAsyncLoader$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ StorageAsyncLoader$$ExternalSyntheticLambda0 INSTANCE = new StorageAsyncLoader$$ExternalSyntheticLambda0();

    private /* synthetic */ StorageAsyncLoader$$ExternalSyntheticLambda0() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((UserInfo) obj).id, ((UserInfo) obj2).id);
    }
}
