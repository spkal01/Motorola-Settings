package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.notification.RedactionInterstitial;
import com.google.android.setupcompat.util.WizardManagerHelper;

public class SetupRedactionInterstitial extends RedactionInterstitial {

    public static class SetupRedactionInterstitialFragment extends RedactionInterstitial.RedactionInterstitialFragment {
    }

    public static void setEnabled(Context context, boolean z) {
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, SetupRedactionInterstitial.class), z ? 1 : 2, 1);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        if (!WizardManagerHelper.isAnySetupWizard(getIntent())) {
            finish();
        }
        super.onCreate(bundle);
    }

    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SetupRedactionInterstitialFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return SetupRedactionInterstitialFragment.class.getName().equals(str);
    }
}
