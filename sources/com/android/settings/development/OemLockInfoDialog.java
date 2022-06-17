package com.android.settings.development;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class OemLockInfoDialog extends InstrumentedDialogFragment {
    public int getMetricsCategory() {
        return 1238;
    }

    public static void show(Fragment fragment) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("OemLockInfoDialog") == null) {
            new OemLockInfoDialog().show(childFragmentManager, "OemLockInfoDialog");
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(C1992R$string.oem_lock_info_message).create();
    }
}
