package com.android.settings.applications.appinfo;

import androidx.preference.Preference;

public final /* synthetic */ class AppNotificationPreferenceController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Preference f$0;
    public final /* synthetic */ CharSequence f$1;

    public /* synthetic */ AppNotificationPreferenceController$$ExternalSyntheticLambda0(Preference preference, CharSequence charSequence) {
        this.f$0 = preference;
        this.f$1 = charSequence;
    }

    public final void run() {
        this.f$0.setSummary(this.f$1);
    }
}
