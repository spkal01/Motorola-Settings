package com.android.settings.vpn2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ConfirmLockdownFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {

    public interface ConfirmLockdownListener {
        void onConfirmLockdown(Bundle bundle, boolean z, boolean z2);
    }

    public static boolean shouldShow(boolean z, boolean z2, boolean z3) {
        return z || (z3 && !z2);
    }

    public int getMetricsCategory() {
        return 548;
    }

    public static void show(Fragment fragment, boolean z, boolean z2, boolean z3, boolean z4, Bundle bundle) {
        if (fragment.getFragmentManager().findFragmentByTag("ConfirmLockdown") == null) {
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean("replacing", z);
            bundle2.putBoolean("always_on", z2);
            bundle2.putBoolean("lockdown_old", z3);
            bundle2.putBoolean("lockdown_new", z4);
            bundle2.putParcelable("options", bundle);
            ConfirmLockdownFragment confirmLockdownFragment = new ConfirmLockdownFragment();
            confirmLockdownFragment.setArguments(bundle2);
            confirmLockdownFragment.setTargetFragment(fragment, 0);
            confirmLockdownFragment.show(fragment.getFragmentManager(), "ConfirmLockdown");
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        int i2;
        int i3;
        boolean z = getArguments().getBoolean("replacing");
        getArguments().getBoolean("always_on");
        boolean z2 = getArguments().getBoolean("lockdown_old");
        boolean z3 = getArguments().getBoolean("lockdown_new");
        if (z3) {
            i = C1992R$string.vpn_require_connection_title;
        } else {
            i = z ? C1992R$string.vpn_replace_vpn_title : C1992R$string.vpn_set_vpn_title;
        }
        if (z) {
            i2 = C1992R$string.vpn_replace;
        } else {
            i2 = z3 ? C1992R$string.vpn_turn_on : C1992R$string.okay;
        }
        if (z3) {
            if (z) {
                i3 = C1992R$string.vpn_replace_always_on_vpn_enable_message;
            } else {
                i3 = C1992R$string.vpn_first_always_on_vpn_message;
            }
        } else if (z2) {
            i3 = C1992R$string.vpn_replace_always_on_vpn_disable_message;
        } else {
            i3 = C1992R$string.vpn_replace_vpn_message;
        }
        return new AlertDialog.Builder(getActivity()).setTitle(i).setMessage(i3).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(i2, (DialogInterface.OnClickListener) this).create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (getTargetFragment() instanceof ConfirmLockdownListener) {
            ((ConfirmLockdownListener) getTargetFragment()).onConfirmLockdown((Bundle) getArguments().getParcelable("options"), getArguments().getBoolean("always_on"), getArguments().getBoolean("lockdown_new"));
        }
    }
}
