package com.motorola.settings.network.tmo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.settings.C1992R$string;

public class TmoEnabledNetworkDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private PreferredWarningListener mListener;

    public interface PreferredWarningListener {
        void onAction(boolean z);
    }

    public static TmoEnabledNetworkDialogFragment newInstance() {
        return new TmoEnabledNetworkDialogFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage((CharSequence) getResources().getString(C1992R$string.warning_dialog_from_2g_to_lte3g)).setIconAttribute(16843605).setPositiveButton(C1992R$string.okay, (DialogInterface.OnClickListener) this).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) this).setCancelable(false);
        return builder.create();
    }

    public void setCallback(PreferredWarningListener preferredWarningListener) {
        this.mListener = preferredWarningListener;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        PreferredWarningListener preferredWarningListener = this.mListener;
        if (preferredWarningListener != null) {
            preferredWarningListener.onAction(i == -1);
        }
    }
}
