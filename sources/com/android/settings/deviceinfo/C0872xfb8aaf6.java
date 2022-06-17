package com.android.settings.deviceinfo;

import android.util.SparseArray;
import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settings.deviceinfo.storage.UserIconLoader;

/* renamed from: com.android.settings.deviceinfo.StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0872xfb8aaf6 implements UserIconLoader.FetchUserIconTask {
    public final /* synthetic */ StorageCategoryFragment.IconLoaderCallbacks f$0;

    public /* synthetic */ C0872xfb8aaf6(StorageCategoryFragment.IconLoaderCallbacks iconLoaderCallbacks) {
        this.f$0 = iconLoaderCallbacks;
    }

    public final SparseArray getUserIcons() {
        return this.f$0.lambda$onCreateLoader$0();
    }
}
