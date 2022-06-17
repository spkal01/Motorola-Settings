package com.android.settings.sim;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.network.SubscriptionUtil;

public class PreferredSimDialogFragment extends SimDialogFragment implements DialogInterface.OnClickListener {
    public int getMetricsCategory() {
        return 1709;
    }

    public static PreferredSimDialogFragment newInstance() {
        PreferredSimDialogFragment preferredSimDialogFragment = new PreferredSimDialogFragment();
        preferredSimDialogFragment.setArguments(SimDialogFragment.initArguments(3, C1992R$string.sim_preferred_title));
        return preferredSimDialogFragment;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(getTitleResId()).setPositiveButton(C1992R$string.yes, (DialogInterface.OnClickListener) this).setNegativeButton(C1992R$string.f61no, (DialogInterface.OnClickListener) null).create();
        updateDialog(create);
        return create;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            SimDialogActivity simDialogActivity = (SimDialogActivity) getActivity();
            SubscriptionInfo preferredSubscription = getPreferredSubscription();
            if (preferredSubscription != null) {
                simDialogActivity.onSubscriptionSelected(getDialogType(), preferredSubscription.getSubscriptionId());
            }
        }
    }

    public SubscriptionInfo getPreferredSubscription() {
        return getSubscriptionManager().getActiveSubscriptionInfoForSimSlotIndex(getActivity().getIntent().getIntExtra(SimDialogActivity.PREFERRED_SIM, -1));
    }

    private void updateDialog(AlertDialog alertDialog) {
        Log.d("PreferredSimDialogFrag", "Dialog updated, dismiss status: " + this.mWasDismissed);
        SubscriptionInfo preferredSubscription = getPreferredSubscription();
        if (!this.mWasDismissed) {
            if (preferredSubscription == null) {
                dismiss();
                return;
            }
            alertDialog.setMessage(getContext().getString(C1992R$string.sim_preferred_message, new Object[]{SubscriptionUtil.getUniqueSubscriptionDisplayName(preferredSubscription, getContext())}));
        }
    }

    public void updateDialog() {
        updateDialog((AlertDialog) getDialog());
    }

    /* access modifiers changed from: protected */
    public SubscriptionManager getSubscriptionManager() {
        return (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
    }
}
