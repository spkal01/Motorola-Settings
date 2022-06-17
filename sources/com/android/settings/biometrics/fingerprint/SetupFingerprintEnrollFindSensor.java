package com.android.settings.biometrics.fingerprint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C1992R$string;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.motorola.settings.biometrics.fingerprint.SetupFODScreenProtectorWarningActivity;

public class SetupFingerprintEnrollFindSensor extends FingerprintEnrollFindSensor {
    public int getMetricsCategory() {
        return 247;
    }

    /* access modifiers changed from: protected */
    public Intent getFingerprintEnrollingIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollEnrolling.class);
        intent.putExtra("hw_auth_token", this.mToken);
        intent.putExtra("challenge", this.mChallenge);
        intent.putExtra("sensor_id", this.mSensorId);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        new SkipFingerprintDialog().show(getSupportFragmentManager());
    }

    public static class SkipFingerprintDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        public int getMetricsCategory() {
            return 573;
        }

        public Dialog onCreateDialog(Bundle bundle) {
            return onCreateDialogBuilder().create();
        }

        public AlertDialog.Builder onCreateDialogBuilder() {
            return new AlertDialog.Builder(getContext()).setTitle(C1992R$string.setup_fingerprint_enroll_skip_title).setPositiveButton(C1992R$string.skip_anyway_button_label, (DialogInterface.OnClickListener) this).setNegativeButton(C1992R$string.go_back_button_label, (DialogInterface.OnClickListener) this).setMessage(C1992R$string.setup_fingerprint_enroll_skip_after_adding_lock_text);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            FragmentActivity activity;
            if (i == -1 && (activity = getActivity()) != null) {
                activity.setResult(2);
                activity.finish();
            }
        }

        public void show(FragmentManager fragmentManager) {
            show(fragmentManager, "skip_dialog");
        }
    }

    /* access modifiers changed from: protected */
    public Intent getFODScreenProtectorWarningIntent() {
        Intent intentForClassName = getIntentForClassName(SetupFODScreenProtectorWarningActivity.class.getName());
        SetupWizardUtils.copySetupExtras(getIntent(), intentForClassName);
        return intentForClassName;
    }
}
