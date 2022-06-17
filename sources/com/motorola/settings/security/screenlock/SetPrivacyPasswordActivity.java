package com.motorola.settings.security.screenlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.constraintlayout.widget.R$styleable;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.SetupChooseLockGeneric;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.motorola.settings.security.screenlock.SetPrivacyPasswordController;

public class SetPrivacyPasswordActivity extends Activity implements SetPrivacyPasswordController.C1965Ui {
    private String mNewPasswordAction;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String action = getIntent().getAction();
        this.mNewPasswordAction = action;
        if (!"android.app.action.SET_PRIVACY_SPACE_PASSWORD".equals(action)) {
            Log.e("SetPrivacyPasswordActivity", "Unexpected action to launch this activity");
            finish();
            return;
        }
        SetPrivacyPasswordController.create(this, this).dispatchSetNewPasswordIntent();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        setVisible(true);
    }

    public void launchChooseLock(Bundle bundle) {
        Intent intent;
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            intent = new Intent(this, SetupChooseLockGeneric.class);
        } else {
            intent = new Intent(this, ChooseLockGeneric.class);
        }
        intent.setAction(this.mNewPasswordAction);
        intent.putExtras(bundle);
        intent.putExtra("device_password_requirement_only", false);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        startActivityForResult(intent, R$styleable.Constraint_pathMotionArc);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 105) {
            setResult(i2, intent);
            finish();
        }
    }
}
