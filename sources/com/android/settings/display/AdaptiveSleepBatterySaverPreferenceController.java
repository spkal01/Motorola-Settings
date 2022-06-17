package com.android.settings.display;

import android.content.Context;
import android.os.PowerManager;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settingslib.widget.BannerMessagePreference;

public class AdaptiveSleepBatterySaverPreferenceController {
    private final Context mContext;
    private final PowerManager mPowerManager;
    BannerMessagePreference mPreference;

    public AdaptiveSleepBatterySaverPreferenceController(Context context) {
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mContext = context;
    }

    public void addToScreen(PreferenceScreen preferenceScreen) {
        initializePreference();
        preferenceScreen.addPreference(this.mPreference);
        updateVisibility();
    }

    /* access modifiers changed from: package-private */
    public boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    public void updateVisibility() {
        initializePreference();
        this.mPreference.setVisible(isPowerSaveMode());
    }

    private void initializePreference() {
        if (this.mPreference == null) {
            BannerMessagePreference bannerMessagePreference = new BannerMessagePreference(this.mContext);
            this.mPreference = bannerMessagePreference;
            bannerMessagePreference.setTitle(C1992R$string.ambient_camera_summary_battery_saver_on);
            this.mPreference.setPositiveButtonText(C1992R$string.disable_text);
            this.mPreference.setPositiveButtonOnClickListener(new C0894x9778124f(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initializePreference$0(View view) {
        this.mPowerManager.setPowerSaveModeEnabled(false);
    }
}
