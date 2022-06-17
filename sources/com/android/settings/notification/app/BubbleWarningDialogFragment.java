package com.android.settings.notification.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class BubbleWarningDialogFragment extends InstrumentedDialogFragment {
    public int getMetricsCategory() {
        return 1702;
    }

    public BubbleWarningDialogFragment setPkgPrefInfo(String str, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putString("p", str);
        bundle.putInt("u", i);
        bundle.putInt("pref", i2);
        setArguments(bundle);
        return this;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        String string = arguments.getString("p");
        int i = arguments.getInt("u");
        int i2 = arguments.getInt("pref");
        return new AlertDialog.Builder(getContext()).setMessage((CharSequence) getResources().getString(C1992R$string.bubbles_feature_disabled_dialog_text)).setTitle((CharSequence) getResources().getString(C1992R$string.bubbles_feature_disabled_dialog_title)).setCancelable(true).setPositiveButton(C1992R$string.bubbles_feature_disabled_button_approve, (DialogInterface.OnClickListener) new BubbleWarningDialogFragment$$ExternalSyntheticLambda1(this, string, i, i2)).setNegativeButton(C1992R$string.bubbles_feature_disabled_button_cancel, (DialogInterface.OnClickListener) new BubbleWarningDialogFragment$$ExternalSyntheticLambda0(this, string, i)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(String str, int i, int i2, DialogInterface dialogInterface, int i3) {
        BubblePreferenceController.applyBubblesApproval(getContext(), str, i, i2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(String str, int i, DialogInterface dialogInterface, int i2) {
        BubblePreferenceController.revertBubblesApproval(getContext(), str, i);
    }
}
