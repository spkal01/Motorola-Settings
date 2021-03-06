package com.android.settingslib.enterprise;

public interface DeviceAdminStringProvider {
    String getDefaultDisabledByPolicyContent();

    String getDefaultDisabledByPolicyTitle();

    String getDisableCameraTitle();

    String getDisableScreenCaptureTitle();

    String getDisabledBiometricsParentConsentContent();

    String getDisabledBiometricsParentConsentTitle();

    String getDisabledByPolicyTitleForFinancedDevice();

    String getDisallowAdjustVolumeTitle();

    String getDisallowOutgoingCallsTitle();

    String getDisallowSmsTitle();

    String getLearnMoreHelpPageUrl();

    String getSuspendPackagesTitle();
}
