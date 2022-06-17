package com.motorola.settings.biometrics.fingerprint;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.net.Uri;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toolbar;
import com.android.settings.C1980R$bool;
import com.android.settings.C1981R$color;
import com.android.settings.C1982R$dimen;
import com.android.settings.C1985R$id;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.setupdesign.GlifLayout;
import com.motorola.android.provider.MotorolaSettings;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FingerprintUtils {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private static final Uri SIDE_FPS_URI = Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "show_power_touch_fps_enrollment");
    private static Future<Boolean> mFutureShowSideFPS;
    private static int sMotoFODEnabled = -1;

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public static boolean shouldShowSideFPSFeatures(Context context) {
        return isSideFingerPrint(context) && isPrimaryUser(context) && isSetupComplete(context) && shouldShowAdditionalFeature(context) && isPowerTouchFeatureEnabled();
    }

    private static boolean isSetupComplete(Context context) {
        return isDeviceProvisioned(context);
    }

    public static void checkPowerTouchEnabledInBackground(Context context) {
        if (isSideFingerPrint(context) && isPrimaryUser(context) && isSetupComplete(context) && shouldShowAdditionalFeature(context)) {
            mFutureShowSideFPS = ThreadUtils.postOnBackgroundThread((Callable) new FingerprintUtils$$ExternalSyntheticLambda0(context));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ Object lambda$checkPowerTouchEnabledInBackground$0(Context context) throws Exception {
        Cursor query = context.getContentResolver().query(SIDE_FPS_URI, (String[]) null, (String) null, (String[]) null, (String) null);
        boolean z = false;
        if (query != null) {
            if (query.moveToNext() && query.getInt(query.getColumnIndex("show_power_touch_fps_enrollment")) == 1) {
                z = true;
            }
            query.close();
        }
        return Boolean.valueOf(z);
    }

    private static boolean isPowerTouchFeatureEnabled() {
        try {
            Future<Boolean> future = mFutureShowSideFPS;
            if (future != null) {
                return future.get().booleanValue();
            }
            return false;
        } catch (InterruptedException | ExecutionException unused) {
            return false;
        }
    }

    public static boolean isSideFingerPrint(Context context) {
        return context.getResources().getBoolean(C1980R$bool.config_side_fingerprint_enabled);
    }

    private static boolean shouldShowAdditionalFeature(Context context) {
        return context.getSharedPreferences("powertouch_pref", 0).getBoolean("addition_feature_showed", true);
    }

    public static void setShowAdditionalFeature(Context context) {
        context.getSharedPreferences("powertouch_pref", 0).edit().putBoolean("addition_feature_showed", false).apply();
    }

    public static boolean getTouchToUnlockState(Context context) {
        return MotorolaSettings.Global.getInt(context.getContentResolver(), "sidefps_touch_to_unlock", 1) == 1;
    }

    public static void setTouchToUnlockState(Context context, boolean z) {
        MotorolaSettings.Global.putInt(context.getContentResolver(), "sidefps_touch_to_unlock", z ? 1 : 0);
    }

    public static boolean supportSideFpsUpDown(Context context) {
        return context.getResources().getBoolean(17891839);
    }

    private static boolean isPrimaryUser(Context context) {
        return ((UserManager) context.getSystemService("user")).isSystemUser();
    }

    public static boolean shouldShowTouchToUnlockSettings(Context context, boolean z, boolean z2, boolean z3) {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(context);
        if (fingerprintManagerOrNull == null || !isSideFingerPrint(context)) {
            return false;
        }
        int size = fingerprintManagerOrNull.getEnrolledFingerprints(MY_USER_ID).size();
        if (size != 1 || !z3) {
            return size == 1 && !z2 && !z;
        }
        return true;
    }

    public static boolean isDarkTheme(Context context) {
        return ((UiModeManager) context.getSystemService(UiModeManager.class)).getNightMode() == 2;
    }

    public static void applyFODTheme(BiometricEnrollBase biometricEnrollBase) {
        GlifLayout glifLayout = (GlifLayout) biometricEnrollBase.findViewById(C1985R$id.setup_wizard_layout);
        View findViewById = biometricEnrollBase.findViewById(16908290);
        Resources resources = biometricEnrollBase.getResources();
        int i = C1981R$color.fod_fingerprint_background_color;
        findViewById.setBackgroundColor(resources.getColor(i, (Resources.Theme) null));
        glifLayout.setHeaderColor(resources.getColorStateList(C1981R$color.fod_fingerprint_title_color, (Resources.Theme) null));
        glifLayout.getDescriptionTextView().setTextColor(resources.getColorStateList(C1981R$color.fod_fingerprint_description_color, (Resources.Theme) null));
        biometricEnrollBase.getWindow().setNavigationBarColor(resources.getColor(i, (Resources.Theme) null));
        biometricEnrollBase.getWindow().setNavigationBarDividerColor(resources.getColor(i, (Resources.Theme) null));
        View decorView = biometricEnrollBase.getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & -8193);
    }

    public static void overrideFpEnrollSuwHeaderTitleSizeIfNecessary(BiometricEnrollBase biometricEnrollBase) {
        int dimensionPixelSize = biometricEnrollBase.getResources().getDimensionPixelSize(C1982R$dimen.fingerprint_enroll_suw_header_title_size);
        if (dimensionPixelSize != 0) {
            ((GlifLayout) biometricEnrollBase.findViewById(C1985R$id.setup_wizard_layout)).getHeaderTextView().setTextSize(0, (float) dimensionPixelSize);
        }
    }

    public static boolean needsDarkThemeEnforced(Context context) {
        return !isDarkTheme(context) && isMotoFODEnabled(context);
    }

    public static void setDarkThemeToolBar(Activity activity) {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) activity.findViewById(C1985R$id.collapsing_toolbar);
        Resources resources = activity.getResources();
        collapsingToolbarLayout.setBackgroundColor(resources.getColor(C1981R$color.fod_fingerprint_background_color, (Resources.Theme) null));
        int i = C1981R$color.fod_fingerprint_title_color;
        collapsingToolbarLayout.setCollapsedTitleTextColor(resources.getColorStateList(i, (Resources.Theme) null));
        collapsingToolbarLayout.setExpandedTitleTextColor(resources.getColorStateList(i, (Resources.Theme) null));
        Toolbar toolbar = (Toolbar) activity.findViewById(C1985R$id.action_bar);
        toolbar.getNavigationIcon().setTint(resources.getColor(i));
        toolbar.getNavigationIcon().setAutoMirrored(true);
    }

    public static boolean isMotoFODEnabled(Context context) {
        if (sMotoFODEnabled == -1) {
            List sensorPropertiesInternal = ((FingerprintManager) context.getSystemService(FingerprintManager.class)).getSensorPropertiesInternal();
            sMotoFODEnabled = (!(sensorPropertiesInternal != null && sensorPropertiesInternal.size() == 1 && ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal.get(0)).isAnyUdfpsType()) || context.getResources().getBoolean(17891840)) ? 0 : 1;
        }
        if (sMotoFODEnabled == 1) {
            return true;
        }
        return false;
    }

    public static int getFingerprintAnimationStyle(Context context) {
        return MotorolaSettings.Secure.getIntForUser(context.getContentResolver(), "fod_animation_style", 0, MY_USER_ID);
    }

    public static void setFingerprintAnimationStyle(Context context, int i) {
        MotorolaSettings.Secure.putIntForUser(context.getContentResolver(), "fod_animation_style", i, MY_USER_ID);
    }

    public static void checkAndEnableFODAnimationStyle(Context context) {
        if (isMotoFODEnabled(context)) {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, FODAnimationSettings.class), 1, 1);
        }
    }
}
