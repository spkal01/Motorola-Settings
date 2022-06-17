package com.android.settingslib.enterprise;

import android.content.Context;

final class FinancedDeviceActionDisabledByAdminController extends BaseActionDisabledByAdminController {
    public CharSequence getAdminSupportContentString(Context context, CharSequence charSequence) {
        return charSequence;
    }

    FinancedDeviceActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider) {
        super(deviceAdminStringProvider);
    }

    public void setupLearnMoreButton(Context context) {
        assertInitialized();
        this.mLauncher.setupLearnMoreButtonToShowAdminPolicies(context, this.mEnforcementAdminUserId, this.mEnforcedAdmin);
    }

    public String getAdminSupportTitle(String str) {
        return this.mStringProvider.getDisabledByPolicyTitleForFinancedDevice();
    }
}
