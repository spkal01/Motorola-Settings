package com.android.settings.applications;

import com.android.settings.applications.HibernatedAppsPreferenceController;

public final /* synthetic */ class HibernatedAppsPreferenceController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ HibernatedAppsPreferenceController f$0;
    public final /* synthetic */ HibernatedAppsPreferenceController.UnusedCountLoadedCallback f$1;

    public /* synthetic */ HibernatedAppsPreferenceController$$ExternalSyntheticLambda2(HibernatedAppsPreferenceController hibernatedAppsPreferenceController, HibernatedAppsPreferenceController.UnusedCountLoadedCallback unusedCountLoadedCallback) {
        this.f$0 = hibernatedAppsPreferenceController;
        this.f$1 = unusedCountLoadedCallback;
    }

    public final void run() {
        this.f$0.lambda$loadUnusedCount$2(this.f$1);
    }
}
