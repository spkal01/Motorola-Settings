package com.android.settings.enterprise;

import android.content.Context;
import com.android.settings.C1992R$string;
import com.android.settingslib.enterprise.DeviceAdminStringProvider;
import java.util.Objects;

class DeviceAdminStringProviderImpl implements DeviceAdminStringProvider {
    private final Context mContext;

    DeviceAdminStringProviderImpl(Context context) {
        Objects.requireNonNull(context);
        this.mContext = context;
    }

    public String getDefaultDisabledByPolicyTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title);
    }

    public String getDisallowAdjustVolumeTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_adjust_volume);
    }

    public String getDisallowOutgoingCallsTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_outgoing_calls);
    }

    public String getDisallowSmsTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_sms);
    }

    public String getDisableCameraTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_camera);
    }

    public String getDisableScreenCaptureTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_screen_capture);
    }

    public String getSuspendPackagesTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_suspend_packages);
    }

    public String getDefaultDisabledByPolicyContent() {
        return this.mContext.getString(C1992R$string.default_admin_support_msg);
    }

    public String getLearnMoreHelpPageUrl() {
        return this.mContext.getString(C1992R$string.help_url_action_disabled_by_it_admin);
    }

    public String getDisabledByPolicyTitleForFinancedDevice() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_financed_device);
    }

    public String getDisabledBiometricsParentConsentTitle() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_title_biometric_parental_consent);
    }

    public String getDisabledBiometricsParentConsentContent() {
        return this.mContext.getString(C1992R$string.disabled_by_policy_content_biometric_parental_consent);
    }
}
