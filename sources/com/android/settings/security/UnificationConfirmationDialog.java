package com.android.settings.security;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class UnificationConfirmationDialog extends InstrumentedDialogFragment {
    public int getMetricsCategory() {
        return 532;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        int i2;
        SecuritySettings securitySettings = (SecuritySettings) getParentFragment();
        boolean z = getArguments().getBoolean("compliant");
        AlertDialog.Builder title = new AlertDialog.Builder(getActivity()).setTitle(C1992R$string.lock_settings_profile_unification_dialog_title);
        if (z) {
            i = C1992R$string.lock_settings_profile_unification_dialog_body;
        } else {
            i = C1992R$string.lock_settings_profile_unification_dialog_uncompliant_body;
        }
        AlertDialog.Builder message = title.setMessage(i);
        if (z) {
            i2 = C1992R$string.lock_settings_profile_unification_dialog_confirm;
        } else {
            i2 = C1992R$string.lock_settings_profile_unification_dialog_uncompliant_confirm;
        }
        return message.setPositiveButton(i2, (DialogInterface.OnClickListener) new UnificationConfirmationDialog$$ExternalSyntheticLambda0(securitySettings)).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        ((SecuritySettings) getParentFragment()).updateUnificationPreference();
    }
}
