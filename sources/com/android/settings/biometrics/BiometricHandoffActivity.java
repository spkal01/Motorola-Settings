package com.android.settings.biometrics;

import android.os.Bundle;
import android.view.View;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;

public class BiometricHandoffActivity extends BiometricEnrollBase {
    private FooterButton mPrimaryFooterButton;

    public int getMetricsCategory() {
        return 1894;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1987R$layout.biometric_handoff);
        setHeaderText(C1992R$string.biometric_settings_hand_back_to_guardian);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(getPrimaryFooterButton());
    }

    /* access modifiers changed from: protected */
    public FooterButton getPrimaryFooterButton() {
        if (this.mPrimaryFooterButton == null) {
            this.mPrimaryFooterButton = new FooterButton.Builder(this).setText(C1992R$string.biometric_settings_hand_back_to_guardian_ok).setButtonType(5).setListener(new BiometricHandoffActivity$$ExternalSyntheticLambda0(this)).setTheme(C1993R$style.SudGlifButton_Primary).build();
        }
        return this.mPrimaryFooterButton;
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        setResult(-1);
        finish();
    }
}
