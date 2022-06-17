package com.motorola.settings.security.screenlock;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import com.android.internal.util.Preconditions;
import com.android.settings.Utils;

final class SetPrivacyPasswordController {
    private boolean isOpenFingerprint = false;
    private final DevicePolicyManager mDevicePolicyManager;
    private final FingerprintManager mFingerprintManager;
    private final PackageManager mPackageManager;
    private final int mTargetUserId;
    private final C1965Ui mUi;

    /* renamed from: com.motorola.settings.security.screenlock.SetPrivacyPasswordController$Ui */
    interface C1965Ui {
        void launchChooseLock(Bundle bundle);
    }

    public static SetPrivacyPasswordController create(Context context, C1965Ui ui) {
        int currentUser = ActivityManager.getCurrentUser();
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(context);
        Utils.getFaceManagerOrNull(context);
        return new SetPrivacyPasswordController(currentUser, context.getPackageManager(), fingerprintManagerOrNull, (DevicePolicyManager) context.getSystemService("device_policy"), ui);
    }

    SetPrivacyPasswordController(int i, PackageManager packageManager, FingerprintManager fingerprintManager, DevicePolicyManager devicePolicyManager, C1965Ui ui) {
        this.mTargetUserId = i;
        this.mPackageManager = (PackageManager) Preconditions.checkNotNull(packageManager);
        this.mFingerprintManager = fingerprintManager;
        this.mDevicePolicyManager = (DevicePolicyManager) Preconditions.checkNotNull(devicePolicyManager);
        this.mUi = (C1965Ui) Preconditions.checkNotNull(ui);
    }

    public void dispatchSetNewPasswordIntent() {
        Bundle bundle;
        boolean hasSystemFeature = this.mPackageManager.hasSystemFeature("android.hardware.fingerprint");
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        boolean z = fingerprintManager != null && fingerprintManager.isHardwareDetected() && !this.mFingerprintManager.hasEnrolledFingerprints(this.mTargetUserId) && !isFingerprintDisabledByAdmin();
        if (!this.isOpenFingerprint || !hasSystemFeature || !z) {
            bundle = new Bundle();
        } else {
            bundle = getFingerprintChooseLockExtras();
        }
        bundle.putInt("android.intent.extra.USER_ID", this.mTargetUserId);
        this.mUi.launchChooseLock(bundle);
    }

    private Bundle getFingerprintChooseLockExtras() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("hide_insecure_options", true);
        bundle.putBoolean("request_gk_pw_handle", true);
        bundle.putBoolean("for_fingerprint", true);
        return bundle;
    }

    private boolean isFingerprintDisabledByAdmin() {
        return (this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null, this.mTargetUserId) & 32) != 0;
    }
}
