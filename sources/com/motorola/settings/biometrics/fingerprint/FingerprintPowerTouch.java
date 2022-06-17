package com.motorola.settings.biometrics.fingerprint;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.biometrics.fingerprint.FingerprintSettings;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;

public class FingerprintPowerTouch extends FingerprintEnrollTutorialBase {
    public int getMetricsCategory() {
        return 49;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(C1992R$string.security_settings_fingerprint_enroll_not_now).setListener(new FingerprintPowerTouch$$ExternalSyntheticLambda0(this)).setButtonType(5).setTheme(C1993R$style.FingerprintButton_Secondary).build());
        this.mFooterBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(C1992R$string.security_settings_fingerprint_enroll_learn_more).setListener(new FingerprintPowerTouch$$ExternalSyntheticLambda1(this)).setButtonType(7).setTheme(C1993R$style.FingerprintButton_Primary).build());
    }

    public int getTitleResId() {
        return C1992R$string.security_settings_fingerprint_enroll_power_touch_title;
    }

    public int getMsgResId() {
        if (FingerprintUtils.supportSideFpsUpDown(this)) {
            return C1992R$string.security_settings_fingerprint_enroll_power_touch_message;
        }
        return C1992R$string.f77x1acfa9bf;
    }

    /* access modifiers changed from: protected */
    public int getAnimationResId() {
        if (FingerprintUtils.supportSideFpsUpDown(this)) {
            return C1991R$raw.doubletap_swipe;
        }
        return C1991R$raw.doubletap;
    }

    /* access modifiers changed from: protected */
    public void onNotNowButtonClick(View view) {
        FingerprintUtils.setShowAdditionalFeature(this);
        launchFingerprintSettings();
        finish();
    }

    /* access modifiers changed from: private */
    public void onLearnMoreButtonClick(View view) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.motorola.motofpstouch", "com.motorola.motoedgeassistant.ui.SettingsActivityEdge"));
            intent.setFlags(1073774592);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        finish();
    }

    private void launchFingerprintSettings() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", FingerprintSettings.class.getName());
        intent.putExtra("hw_auth_token", this.mToken);
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        intent.setFlags(603979776);
        startActivity(intent);
    }
}
