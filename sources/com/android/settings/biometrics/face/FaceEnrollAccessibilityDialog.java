package com.android.settings.biometrics.face;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class FaceEnrollAccessibilityDialog extends InstrumentedDialogFragment {
    private DialogInterface.OnClickListener mPositiveButtonListener;

    public int getMetricsCategory() {
        return 1506;
    }

    public static FaceEnrollAccessibilityDialog newInstance() {
        return new FaceEnrollAccessibilityDialog();
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener onClickListener) {
        this.mPositiveButtonListener = onClickListener;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int i = C1992R$string.f64x89cc8ccc;
        int i2 = C1992R$string.f65x2371810;
        builder.setMessage(i).setNegativeButton(i2, FaceEnrollAccessibilityDialog$$ExternalSyntheticLambda1.INSTANCE).setPositiveButton(C1992R$string.f66xf7e2a5d4, new FaceEnrollAccessibilityDialog$$ExternalSyntheticLambda0(this));
        return builder.create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        this.mPositiveButtonListener.onClick(dialogInterface, i);
    }
}
