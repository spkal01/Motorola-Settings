package com.motorola.settings.biometrics.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.settings.C1977R$anim;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollBase;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollFinish;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;

public class FingerprintEnrollAlmostThere extends FingerprintEnrollBase {
    private Switch mTouchToUnlockSwitch;

    public int getMetricsCategory() {
        return 242;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1987R$layout.fingerprint_enroll_almost_there);
        setHeaderText(C1992R$string.security_settings_fingerprint_enroll_almost_there_title);
        Switch switchR = (Switch) findViewById(C1985R$id.touchToUnlockSwitch);
        this.mTouchToUnlockSwitch = switchR;
        switchR.setChecked(FingerprintUtils.getTouchToUnlockState(getBaseContext()));
        this.mTouchToUnlockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                FingerprintUtils.setTouchToUnlockState(FingerprintEnrollAlmostThere.this.getBaseContext(), z);
            }
        });
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(C1992R$string.fingerprint_enroll_button_next).setListener(new FingerprintEnrollAlmostThere$$ExternalSyntheticLambda0(this)).setButtonType(5).setTheme(C1993R$style.FingerprintButton_Primary).build());
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        Intent finishIntent = getFinishIntent();
        finishIntent.addFlags(637534208);
        finishIntent.putExtra("hw_auth_token", getIntent().getByteArrayExtra("hw_auth_token"));
        finishIntent.putExtra("from_settings_summary", getIntent().getBooleanExtra("from_settings_summary", false));
        int i = this.mUserId;
        if (i != -10000) {
            finishIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        finishIntent.putExtra("from_fingerprint_settings", getIntent().getBooleanExtra("from_fingerprint_settings", false));
        startActivity(finishIntent);
        overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
        finish();
    }

    /* access modifiers changed from: protected */
    public Intent getFinishIntent() {
        return getIntentForClassName(FingerprintEnrollFinish.class.getName());
    }
}
