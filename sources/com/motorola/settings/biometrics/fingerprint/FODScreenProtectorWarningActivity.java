package com.motorola.settings.biometrics.fingerprint;

import android.os.Bundle;
import android.view.View;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;

public class FODScreenProtectorWarningActivity extends FingerprintEnrollTutorialBase {
    public int getMetricsCategory() {
        return 49;
    }

    /* access modifiers changed from: protected */
    public boolean needsFODTheme() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean trackAbandonAction() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(C1992R$string.fingerprint_enroll_button_got_it).setListener(new FODScreenProtectorWarningActivity$$ExternalSyntheticLambda0(this)).setButtonType(5).setTheme(C1993R$style.FingerprintButton_Primary).build());
    }

    public int getTitleResId() {
        return C1992R$string.fod_screen_protector_warning_title;
    }

    public int getMsgResId() {
        return C1992R$string.fod_screen_protector_warning_message;
    }

    /* access modifiers changed from: protected */
    public int getAnimationResId() {
        return C1991R$raw.fod_screen_protector_warning;
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        setResult(1);
        finish();
    }
}
