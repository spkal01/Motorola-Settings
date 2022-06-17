package com.android.settings.accessibility;

import android.os.Vibrator;
import com.android.settings.C1994R$xml;

public class NotificationVibrationPreferenceFragment extends VibrationPreferenceFragment {
    public int getMetricsCategory() {
        return 1293;
    }

    /* access modifiers changed from: protected */
    public int getPreviewVibrationAudioAttributesUsage() {
        return 5;
    }

    /* access modifiers changed from: protected */
    public String getVibrationEnabledSetting() {
        return "";
    }

    /* access modifiers changed from: protected */
    public String getVibrationIntensitySetting() {
        return "notification_vibration_intensity";
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.accessibility_notification_vibration_settings;
    }

    /* access modifiers changed from: protected */
    public int getDefaultVibrationIntensity() {
        return ((Vibrator) getContext().getSystemService(Vibrator.class)).getDefaultNotificationVibrationIntensity();
    }
}
