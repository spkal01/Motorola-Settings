package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;

public class AlwaysOnVpnCurrentUserPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    public String getPreferenceKey() {
        return "always_on_vpn_primary_user";
    }

    public AlwaysOnVpnCurrentUserPreferenceController(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    public void updateState(Preference preference) {
        int i;
        if (this.mFeatureProvider.isInCompMode()) {
            i = C1992R$string.enterprise_privacy_always_on_vpn_personal;
        } else {
            i = C1992R$string.enterprise_privacy_always_on_vpn_device;
        }
        preference.setTitle(i);
    }

    public boolean isAvailable() {
        return this.mFeatureProvider.isAlwaysOnVpnSetInCurrentUser();
    }
}
