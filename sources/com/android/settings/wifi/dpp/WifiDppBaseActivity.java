package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.OrientationEventListener;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C1987R$layout;
import com.android.settings.C1993R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.InstrumentedActivity;
import com.motorola.settingslib.displayutils.EndlessLayoutMixin;

abstract class WifiDppBaseActivity extends InstrumentedActivity {
    private static OrientationEventListener orientationListener;
    protected FragmentManager mFragmentManager;

    /* access modifiers changed from: protected */
    public abstract void handleIntent(Intent intent);

    WifiDppBaseActivity() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getLifecycle().addObserver(new EndlessLayoutMixin(this));
        setContentView(C1987R$layout.wifi_dpp_activity);
        this.mFragmentManager = getSupportFragmentManager();
        orientationListener = new OrientationEventListener(this) {
            public void onOrientationChanged(int i) {
                Bundle bundle = new Bundle();
                bundle.putInt("orientation", i);
                WifiDppBaseActivity.this.mFragmentManager.setFragmentResult("QrCodeScanner", bundle);
            }
        };
        if (bundle == null) {
            handleIntent(getIntent());
        }
    }

    public void onResume() {
        super.onResume();
        orientationListener.enable();
    }

    public void onPause() {
        super.onPause();
        orientationListener.disable();
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        int theme2 = SetupWizardUtils.getTheme(this, getIntent());
        theme.applyStyle(C1993R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, theme2, z);
    }
}
