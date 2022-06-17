package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsManager;
import android.text.TextUtils;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ContactDiscoveryDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private CharSequence mCarrierName;
    private ImsManager mImsManager;
    private int mSubId;

    public int getMetricsCategory() {
        return 0;
    }

    public static ContactDiscoveryDialogFragment newInstance(int i, CharSequence charSequence) {
        ContactDiscoveryDialogFragment contactDiscoveryDialogFragment = new ContactDiscoveryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        bundle.putCharSequence("carrier_name_key", charSequence);
        contactDiscoveryDialogFragment.setArguments(bundle);
        return contactDiscoveryDialogFragment;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        this.mSubId = arguments.getInt("sub_id_key");
        this.mCarrierName = arguments.getCharSequence("carrier_name_key");
        this.mImsManager = getImsManager(context);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        String str;
        String str2;
        String str3;
        String str4;
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        TelephonyManager createForSubscriptionId = ((TelephonyManager) getContext().getSystemService("phone")).createForSubscriptionId(this.mSubId);
        if (!TextUtils.isEmpty(this.mCarrierName)) {
            if (createForSubscriptionId.getSimCarrierId() == 1839) {
                i = C1992R$string.contact_discovery_opt_in_dialog_message_carrier_vzw;
            } else {
                i = C1992R$string.contact_discovery_opt_in_dialog_message_carrier_default;
            }
            str3 = getContext().getString(C1992R$string.contact_discovery_opt_in_dialog_title_carrier, new Object[]{this.mCarrierName});
            str4 = getContext().getString(i, new Object[]{this.mCarrierName});
            str2 = getContext().getString(C1992R$string.confirmation_turn_on_carrier);
            str = getContext().getString(C1992R$string.confirmation_turn_off_carrier);
        } else {
            str3 = getContext().getString(C1992R$string.contact_discovery_opt_in_dialog_title_no_carrier_defined);
            str4 = getContext().getString(C1992R$string.contact_discovery_opt_in_dialog_message_no_carrier_defined);
            str2 = getContext().getString(C1992R$string.confirmation_turn_on);
            str = getContext().getString(17039360);
        }
        builder.setMessage(str4).setTitle(str3).setIconAttribute(16843605).setPositiveButton(str2, this).setNegativeButton(str, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            MobileNetworkUtils.setContactDiscoveryEnabled(this.mImsManager, this.mSubId, true);
        }
    }

    public ImsManager getImsManager(Context context) {
        return (ImsManager) context.getSystemService(ImsManager.class);
    }

    public static String getFragmentTag(int i) {
        return "discovery_dialog:" + i;
    }
}
