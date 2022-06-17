package com.motorola.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.hardware.face.FaceManager;
import androidx.preference.Preference;
import com.android.settings.Utils;
import com.android.settings.biometrics.face.FaceSettings;
import com.android.settings.biometrics.face.FaceSettingsPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.android.provider.MotorolaSettings;

public class FaceSettingsLTVPreferenceController extends FaceSettingsPreferenceController {
    private static final int DEFAULT = 0;
    public static final String KEY = "security_settings_face_lift_to_unlock";
    public static String LIFT_TO_VIEW_SYS_PROP = "ro.vendor.sensors.mot_ltv";
    private static final String MOTO_SETTING_LIFT_TO_VIEW = "property_lift_to_unlock";
    private static final int OFF = 0;

    /* renamed from: ON */
    private static final int f168ON = 1;
    private static final String TAG = "FaceSettingsLTVPreferenceController";
    public static final int TYPE_MOTO_LTV = 65556;
    private static boolean isLtvFeatureOn = false;
    private static boolean sChecked = false;
    private FaceManager mFaceManager;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceSettingsLTVPreferenceController(Context context) {
        this(context, KEY);
    }

    public FaceSettingsLTVPreferenceController(Context context, String str) {
        super(context, str);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    public boolean isChecked() {
        if (MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), MOTO_SETTING_LIFT_TO_VIEW, 0, getUserId()) == 1) {
            return true;
        }
        return false;
    }

    public boolean setChecked(boolean z) {
        return MotorolaSettings.Secure.putIntForUser(this.mContext.getContentResolver(), MOTO_SETTING_LIFT_TO_VIEW, z ? 1 : 0, getUserId());
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!FaceSettings.isFaceHardwareDetected(this.mContext)) {
            preference.setEnabled(false);
        } else if (!this.mFaceManager.hasEnrolledTemplates(getUserId())) {
            preference.setEnabled(false);
        } else {
            preference.setEnabled(true);
        }
    }

    public int getAvailabilityStatus() {
        return (!isLtvFeatureOn(this.mContext) || !FaceUtils.isMotoFaceUnlock()) ? 2 : 0;
    }

    public static boolean isLtvFeatureOn(Context context) {
        if (!sChecked) {
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            if (sensorManager != null) {
                isLtvFeatureOn = !sensorManager.getSensorList(TYPE_MOTO_LTV).isEmpty();
            }
            sChecked = true;
        }
        return isLtvFeatureOn;
    }
}
