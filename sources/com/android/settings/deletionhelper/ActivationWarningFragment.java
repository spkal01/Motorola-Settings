package com.android.settings.deletionhelper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.settings.C1992R$string;

public class ActivationWarningFragment extends DialogFragment {
    public static ActivationWarningFragment newInstance() {
        return new ActivationWarningFragment();
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(C1992R$string.automatic_storage_manager_activation_warning).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
    }
}
