package com.android.settings.biometrics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricManager;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1977R$anim;
import com.android.settings.C1993R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.motorola.settings.biometrics.face.FaceUtils;
import com.motorola.settingslib.displayutils.EndlessLayoutMixin;
import java.util.List;

public class BiometricEnrollActivity extends InstrumentedActivity {
    private boolean mConfirmingCredentials;
    private Long mGkPwHandle;
    private boolean mHasFeatureFace = false;
    private boolean mHasFeatureFingerprint = false;
    private boolean mIsEnrollActionLogged;
    private boolean mIsFaceEnrollable = false;
    private boolean mIsFingerprintEnrollable = false;
    private MultiBiometricEnrollHelper mMultiBiometricEnrollHelper;
    private ParentalConsentHelper mParentalConsentHelper;
    private Bundle mParentalOptions;
    private boolean mParentalOptionsRequired = false;
    private boolean mSkipReturnToParent = false;
    private int mUserId = UserHandle.myUserId();

    public static final class InternalActivity extends BiometricEnrollActivity {
    }

    private static boolean isSuccessfulConfirmOrChooseCredential(int i, int i2) {
        return (i == 1 && i2 == 1) || (i == 2 && i2 == -1);
    }

    public int getMetricsCategory() {
        return 1586;
    }

    public void onCreate(Bundle bundle) {
        int i;
        int i2;
        int i3;
        int i4;
        super.onCreate(bundle);
        Intent intent = getIntent();
        getLifecycle().addObserver(new EndlessLayoutMixin(this));
        if (this instanceof InternalActivity) {
            this.mUserId = intent.getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
            if (BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
                this.mGkPwHandle = Long.valueOf(BiometricUtils.getGatekeeperPasswordHandle(getIntent()));
            }
        }
        boolean z = false;
        if (bundle != null) {
            this.mConfirmingCredentials = bundle.getBoolean("confirming_credentials", false);
            this.mIsEnrollActionLogged = bundle.getBoolean("enroll_action_logged", false);
            this.mParentalOptions = bundle.getBundle("enroll_preferences");
            if (bundle.containsKey("gk_pw_handle")) {
                this.mGkPwHandle = Long.valueOf(bundle.getLong("gk_pw_handle"));
            }
        }
        if (!this.mIsEnrollActionLogged && "android.settings.BIOMETRIC_ENROLL".equals(intent.getAction())) {
            this.mIsEnrollActionLogged = true;
            BiometricManager biometricManager = (BiometricManager) getSystemService(BiometricManager.class);
            int i5 = 12;
            if (biometricManager != null) {
                i5 = biometricManager.canAuthenticate(15);
                i4 = biometricManager.canAuthenticate(255);
                i3 = biometricManager.canAuthenticate(32768);
            } else {
                i3 = 12;
                i4 = 12;
            }
            FrameworkStatsLog.write(355, i5 == 0, i4 == 0, i3 == 0, intent.hasExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED"), intent.getIntExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED", 0));
        }
        if (intent.getStringExtra("theme") == null) {
            intent.putExtra("theme", SetupWizardUtils.getThemeString(intent));
        }
        PackageManager packageManager = getApplicationContext().getPackageManager();
        this.mHasFeatureFingerprint = packageManager.hasSystemFeature("android.hardware.fingerprint");
        this.mHasFeatureFace = packageManager.hasSystemFeature("android.hardware.biometrics.face");
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        if (this.mHasFeatureFace && !isAnySetupWizard) {
            FaceManager faceManager = (FaceManager) getSystemService(FaceManager.class);
            List sensorPropertiesInternal = faceManager.getSensorPropertiesInternal();
            if (!sensorPropertiesInternal.isEmpty()) {
                if (isAnySetupWizard) {
                    i2 = 1;
                } else {
                    i2 = ((FaceSensorPropertiesInternal) sensorPropertiesInternal.get(0)).maxEnrollmentsPerUser;
                }
                this.mIsFaceEnrollable = faceManager.getEnrolledFaces(this.mUserId).size() < i2;
            }
        }
        if (this.mHasFeatureFingerprint) {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FingerprintManager.class);
            List sensorPropertiesInternal2 = fingerprintManager.getSensorPropertiesInternal();
            if (!sensorPropertiesInternal2.isEmpty()) {
                if (isAnySetupWizard) {
                    i = 1;
                } else {
                    i = ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal2.get(0)).maxEnrollmentsPerUser;
                }
                this.mIsFingerprintEnrollable = fingerprintManager.getEnrolledFingerprints(this.mUserId).size() < i;
            }
        }
        this.mParentalOptionsRequired = intent.getBooleanExtra("require_consent", false);
        this.mSkipReturnToParent = intent.getBooleanExtra("skip_return_to_parent", false);
        Log.d("BiometricEnrollActivity", "parentalOptionsRequired: " + this.mParentalOptionsRequired + ", skipReturnToParent: " + this.mSkipReturnToParent + ", isSetupWizard: " + isAnySetupWizard);
        if (!isAnySetupWizard || !this.mParentalOptionsRequired) {
            if (isAnySetupWizard && this.mParentalOptionsRequired) {
                if (ParentalControlsUtils.parentConsentRequired(this, 10) != null) {
                    z = true;
                }
                if (z) {
                    Log.w("BiometricEnrollActivity", "Consent was already setup - skipping enrollment");
                    setResult(2);
                    finish();
                    return;
                }
            }
            if (!this.mParentalOptionsRequired || this.mParentalOptions != null) {
                startEnroll();
                return;
            }
            this.mParentalConsentHelper = new ParentalConsentHelper(this.mGkPwHandle);
            setOrConfirmCredentialsNow();
            return;
        }
        Log.w("BiometricEnrollActivity", "Enrollment with parental consent is not supported when launched  directly from SuW - skipping enrollment");
        setResult(2);
        finish();
    }

    private void startEnroll() {
        int intExtra = getIntent().getIntExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED", 255);
        Log.d("BiometricEnrollActivity", "Authenticators: " + intExtra);
        startEnrollWith(intExtra, WizardManagerHelper.isAnySetupWizard(getIntent()));
    }

    private void startEnrollWith(@BiometricManager.Authenticators.Types int i, boolean z) {
        boolean z2;
        int canAuthenticate;
        if (z || this.mParentalOptionsRequired || (canAuthenticate = ((BiometricManager) getSystemService(BiometricManager.class)).canAuthenticate(i)) == 11) {
            boolean z3 = this.mHasFeatureFace;
            boolean z4 = true;
            if (FaceUtils.isMotoFaceUnlock()) {
                z3 = z3 && i != 15;
            }
            boolean z5 = this.mHasFeatureFingerprint;
            if (this.mParentalOptionsRequired) {
                Bundle bundle = this.mParentalOptions;
                if (bundle != null) {
                    z2 = z2 && ParentalConsentHelper.hasFaceConsent(bundle);
                    if (!z5 || !ParentalConsentHelper.hasFingerprintConsent(this.mParentalOptions)) {
                        z4 = false;
                    }
                    z5 = z4;
                } else {
                    throw new IllegalStateException("consent options required, but not set");
                }
            }
            if (!z && i == 32768) {
                launchCredentialOnlyEnroll();
                finish();
            } else if (!z2 || !z5) {
                if (z5) {
                    launchFingerprintOnlyEnroll();
                } else if (z2) {
                    launchFaceOnlyEnroll();
                } else {
                    if (this.mParentalOptionsRequired) {
                        Log.d("BiometricEnrollActivity", "No consent for any modality: skipping enrollment");
                        setResult(-1, newResultIntent());
                    } else {
                        Log.e("BiometricEnrollActivity", "Unknown state, finishing (was SUW: " + z + ")");
                    }
                    finish();
                }
            } else if (this.mGkPwHandle != null) {
                launchFaceAndFingerprintEnroll();
            } else {
                setOrConfirmCredentialsNow();
            }
        } else {
            Log.e("BiometricEnrollActivity", "Unexpected result (has enrollments): " + canAuthenticate);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("confirming_credentials", this.mConfirmingCredentials);
        bundle.putBoolean("enroll_action_logged", this.mIsEnrollActionLogged);
        Bundle bundle2 = this.mParentalOptions;
        if (bundle2 != null) {
            bundle.putBundle("enroll_preferences", bundle2);
        }
        Long l = this.mGkPwHandle;
        if (l != null) {
            bundle.putLong("gk_pw_handle", l.longValue());
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.mParentalConsentHelper != null) {
            boolean z = true;
            boolean z2 = ParentalControlsUtils.parentConsentRequired(this, 8) != null;
            boolean z3 = ParentalControlsUtils.parentConsentRequired(this, 2) != null;
            boolean z4 = z2 && this.mHasFeatureFace;
            if (!z3 || !this.mHasFeatureFingerprint) {
                z = false;
            }
            Log.d("BiometricEnrollActivity", "faceConsentRequired: " + z2 + ", fpConsentRequired: " + z3 + ", hasFeatureFace: " + this.mHasFeatureFace + ", hasFeatureFingerprint: " + this.mHasFeatureFingerprint + ", faceEnrollable: " + this.mIsFaceEnrollable + ", fpEnrollable: " + this.mIsFingerprintEnrollable);
            this.mParentalConsentHelper.setConsentRequirement(z4, z);
            handleOnActivityResultWhileConsenting(i, i2, intent);
            return;
        }
        handleOnActivityResultWhileEnrolling(i, i2, intent);
    }

    private void handleOnActivityResultWhileConsenting(int i, int i2, Intent intent) {
        overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
        if (i == 1 || i == 2) {
            this.mConfirmingCredentials = false;
            if (isSuccessfulConfirmOrChooseCredential(i, i2)) {
                updateGatekeeperPasswordHandle(intent);
                if (!this.mParentalConsentHelper.launchNext(this, 3)) {
                    Log.e("BiometricEnrollActivity", "Nothing to prompt for consent (no modalities enabled)!");
                    finish();
                    return;
                }
                return;
            }
            Log.d("BiometricEnrollActivity", "Unknown result for set/choose lock: " + i2);
            setResult(i2);
            finish();
        } else if (i != 3) {
            Log.w("BiometricEnrollActivity", "Unknown consenting requestCode: " + i + ", finishing");
            finish();
        } else if (i2 != 4 && i2 != 5) {
            Log.d("BiometricEnrollActivity", "Unknown or cancelled parental consent");
            setResult(0);
            finish();
        } else if (!this.mParentalConsentHelper.launchNext(this, 3, i2, intent)) {
            this.mParentalOptions = this.mParentalConsentHelper.getConsentResult();
            this.mParentalConsentHelper = null;
            Log.d("BiometricEnrollActivity", "Enrollment consent options set, starting enrollment: " + this.mParentalOptions);
            startEnrollWith(4095, WizardManagerHelper.isAnySetupWizard(getIntent()));
        }
    }

    private void handleOnActivityResultWhileEnrolling(int i, int i2, Intent intent) {
        if (i == 4) {
            Log.d("BiometricEnrollActivity", "Enrollment complete, requesting handoff, result: " + i2);
            setResult(-1, newResultIntent());
            finish();
        } else if (this.mMultiBiometricEnrollHelper == null) {
            overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
            if (i == 1 || i == 2) {
                this.mConfirmingCredentials = false;
                if (!isSuccessfulConfirmOrChooseCredential(i, i2) || !this.mHasFeatureFace || !this.mHasFeatureFingerprint) {
                    Log.d("BiometricEnrollActivity", "Unknown result for set/choose lock: " + i2);
                    setResult(i2);
                    finish();
                    return;
                }
                updateGatekeeperPasswordHandle(intent);
                launchFaceAndFingerprintEnroll();
            } else if (i != 5) {
                Log.w("BiometricEnrollActivity", "Unknown enrolling requestCode: " + i + ", finishing");
                finish();
            } else {
                finishOrLaunchHandToParent(i2);
            }
        } else {
            Log.d("BiometricEnrollActivity", "RequestCode: " + i + " resultCode: " + i2);
            BiometricUtils.removeGatekeeperPasswordHandle((Context) this, this.mGkPwHandle.longValue());
            finishOrLaunchHandToParent(i2);
        }
    }

    private void finishOrLaunchHandToParent(int i) {
        if (!this.mParentalOptionsRequired) {
            setResult(i);
            finish();
        } else if (!this.mSkipReturnToParent) {
            launchHandoffToParent();
        } else {
            setResult(-1, newResultIntent());
            finish();
        }
    }

    private Intent newResultIntent() {
        Intent intent = new Intent();
        Bundle deepCopy = this.mParentalOptions.deepCopy();
        intent.putExtra("consent_status", deepCopy);
        Log.v("BiometricEnrollActivity", "Result consent status: " + deepCopy);
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        int theme2 = SetupWizardUtils.getTheme(this, getIntent());
        theme.applyStyle(C1993R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, theme2, z);
    }

    private void setOrConfirmCredentialsNow() {
        if (!this.mConfirmingCredentials) {
            this.mConfirmingCredentials = true;
            if (!userHasPassword(this.mUserId)) {
                launchChooseLock();
            } else {
                launchConfirmLock();
            }
        }
    }

    private void updateGatekeeperPasswordHandle(Intent intent) {
        this.mGkPwHandle = Long.valueOf(BiometricUtils.getGatekeeperPasswordHandle(intent));
        ParentalConsentHelper parentalConsentHelper = this.mParentalConsentHelper;
        if (parentalConsentHelper != null) {
            parentalConsentHelper.updateGatekeeperHandle(intent);
        }
    }

    private boolean userHasPassword(int i) {
        return new LockPatternUtils(this).getActivePasswordQuality(((UserManager) getSystemService(UserManager.class)).getCredentialOwnerProfile(i)) != 0;
    }

    private void launchChooseLock() {
        Log.d("BiometricEnrollActivity", "launchChooseLock");
        Intent chooseLockIntent = BiometricUtils.getChooseLockIntent(this, getIntent());
        chooseLockIntent.putExtra("hide_insecure_options", true);
        chooseLockIntent.putExtra("request_gk_pw_handle", true);
        chooseLockIntent.putExtra("for_biometrics", true);
        int i = this.mUserId;
        if (i != -10000) {
            chooseLockIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        startActivityForResult(chooseLockIntent, 1);
    }

    private void launchConfirmLock() {
        Log.d("BiometricEnrollActivity", "launchConfirmLock");
        ChooseLockSettingsHelper.Builder builder = new ChooseLockSettingsHelper.Builder(this);
        builder.setRequestCode(2).setRequestGatekeeperPasswordHandle(true).setForegroundOnly(true).setReturnCredentials(true);
        int i = this.mUserId;
        if (i != -10000) {
            builder.setUserId(i);
        }
        if (!builder.show()) {
            finish();
        }
    }

    private void launchSingleSensorEnrollActivity(Intent intent, int i) {
        BiometricUtils.launchEnrollForResult(this, intent, i, this instanceof InternalActivity ? getIntent().getByteArrayExtra("hw_auth_token") : null, this.mGkPwHandle, this.mUserId);
    }

    private void launchCredentialOnlyEnroll() {
        Intent intent = new Intent(this, ChooseLockGeneric.class);
        intent.putExtra("hide_insecure_options", true);
        launchSingleSensorEnrollActivity(intent, 0);
    }

    private void launchFingerprintOnlyEnroll() {
        Intent intent;
        if (!getIntent().getBooleanExtra("skip_intro", false) || !(this instanceof InternalActivity)) {
            intent = BiometricUtils.getFingerprintIntroIntent(this, getIntent());
        } else {
            intent = BiometricUtils.getFingerprintFindSensorIntent(this, getIntent());
        }
        launchSingleSensorEnrollActivity(intent, 5);
    }

    private void launchFaceOnlyEnroll() {
        launchSingleSensorEnrollActivity(BiometricUtils.getFaceIntroIntent(this, getIntent()), 5);
    }

    private void launchFaceAndFingerprintEnroll() {
        MultiBiometricEnrollHelper multiBiometricEnrollHelper = new MultiBiometricEnrollHelper(this, this.mUserId, this.mIsFaceEnrollable, this.mIsFingerprintEnrollable, this.mGkPwHandle.longValue());
        this.mMultiBiometricEnrollHelper = multiBiometricEnrollHelper;
        multiBiometricEnrollHelper.startNextStep();
    }

    private void launchHandoffToParent() {
        startActivityForResult(BiometricUtils.getHandoffToParentIntent(this, getIntent()), 4);
    }
}
