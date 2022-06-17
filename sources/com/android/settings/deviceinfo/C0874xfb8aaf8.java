package com.android.settings.deviceinfo;

import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Predicate;

/* renamed from: com.android.settings.deviceinfo.StorageCategoryFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C0874xfb8aaf8 implements Predicate {
    public static final /* synthetic */ C0874xfb8aaf8 INSTANCE = new C0874xfb8aaf8();

    private /* synthetic */ C0874xfb8aaf8() {
    }

    public final boolean test(Object obj) {
        return StorageCategoryFragment.IconLoaderCallbacks.lambda$onLoadFinished$1((AbstractPreferenceController) obj);
    }
}
