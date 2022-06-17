package com.android.settings.display;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C1992R$string;
import com.android.settingslib.widget.BannerMessagePreference;

public class AdaptiveSleepCameraStatePreferenceController {
    private final Context mContext;
    @VisibleForTesting
    BannerMessagePreference mPreference;
    private final SensorPrivacyManager mPrivacyManager;

    public AdaptiveSleepCameraStatePreferenceController(Context context) {
        SensorPrivacyManager instance = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = instance;
        instance.addSensorPrivacyListener(2, new C0895x31edb191(this));
        this.mContext = context;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, boolean z) {
        updateVisibility();
    }

    public void addToScreen(PreferenceScreen preferenceScreen) {
        initializePreference();
        preferenceScreen.addPreference(this.mPreference);
        updateVisibility();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isCameraLocked() {
        return this.mPrivacyManager.isSensorPrivacyEnabled(2);
    }

    public void updateVisibility() {
        initializePreference();
        this.mPreference.setVisible(isCameraLocked());
    }

    private void initializePreference() {
        if (this.mPreference == null) {
            BannerMessagePreference bannerMessagePreference = new BannerMessagePreference(this.mContext);
            this.mPreference = bannerMessagePreference;
            bannerMessagePreference.setTitle(C1992R$string.auto_rotate_camera_lock_title);
            this.mPreference.setSummary(C1992R$string.adaptive_sleep_camera_lock_summary);
            this.mPreference.setPositiveButtonText(C1992R$string.allow);
            this.mPreference.setPositiveButtonOnClickListener(new C0896x31edb192(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initializePreference$1(View view) {
        this.mPrivacyManager.setSensorPrivacy(3, 2, false);
    }
}
