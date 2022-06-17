package com.android.settings.applications.autofill;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import android.service.autofill.AutofillServiceInfo;
import androidx.preference.Preference;

public final /* synthetic */ class PasswordsPreferenceController$$ExternalSyntheticLambda1 implements Preference.OnPreferenceClickListener {
    public final /* synthetic */ ServiceInfo f$0;
    public final /* synthetic */ AutofillServiceInfo f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ PasswordsPreferenceController$$ExternalSyntheticLambda1(ServiceInfo serviceInfo, AutofillServiceInfo autofillServiceInfo, Context context, int i) {
        this.f$0 = serviceInfo;
        this.f$1 = autofillServiceInfo;
        this.f$2 = context;
        this.f$3 = i;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$2.startActivityAsUser(new Intent("android.intent.action.MAIN").setClassName(this.f$0.packageName, this.f$1.getPasswordsActivity()), UserHandle.of(this.f$3));
    }
}
