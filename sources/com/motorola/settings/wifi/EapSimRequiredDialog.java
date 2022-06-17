package com.motorola.settings.wifi;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import java.util.List;

public class EapSimRequiredDialog extends DialogFragment {
    private static final String TAG = EapSimRequiredDialog.class.getSimpleName();
    private String mSsid;

    public static void show(Context context, String str) {
        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("arg_ssid", str);
        EapSimRequiredDialog eapSimRequiredDialog = new EapSimRequiredDialog();
        eapSimRequiredDialog.setArguments(bundle);
        eapSimRequiredDialog.setCancelable(false);
        eapSimRequiredDialog.setRetainInstance(true);
        eapSimRequiredDialog.show(fragmentManager, TAG);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        Context context = getContext();
        String string = arguments.getString("arg_ssid");
        this.mSsid = string;
        String removeDoubleQuotes = removeDoubleQuotes(string);
        if (context.getResources().getString(C1992R$string.config_wifi_Verizon_ssid).equals(removeDoubleQuotes)) {
            removeDoubleQuotes = context.getResources().getString(C1992R$string.config_wifi_Verizon_ssid_alias);
        }
        return new AlertDialog.Builder(getContext()).setTitle((CharSequence) context.getResources().getString(C1992R$string.wifi_eap_sim_removed_title, new Object[]{removeDoubleQuotes})).setMessage((CharSequence) context.getResources().getString(C1992R$string.wifi_eap_sim_required_message, new Object[]{removeDoubleQuotes})).setPositiveButton(C1992R$string.okay, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0022, code lost:
        r3 = r0.carrierId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0018, code lost:
        r3 = r0.enterpriseConfig;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean shouldShowSimRequiredDialog(android.content.Context r2, com.android.wifitrackerlib.WifiEntry r3) {
        /*
            android.net.wifi.WifiConfiguration r0 = r3.getWifiConfiguration()
            boolean r1 = r3.isSaved()
            if (r1 != 0) goto L_0x0010
            boolean r1 = r3.isSuggestion()
            if (r1 == 0) goto L_0x002f
        L_0x0010:
            if (r0 == 0) goto L_0x002f
            java.lang.String r3 = r3.getSsid()
            if (r3 == 0) goto L_0x002f
            android.net.wifi.WifiEnterpriseConfig r3 = r0.enterpriseConfig
            if (r3 == 0) goto L_0x002f
            boolean r3 = r3.isAuthenticationSimBased()
            if (r3 == 0) goto L_0x002f
            int r3 = r0.carrierId
            r0 = -1
            if (r3 == r0) goto L_0x002f
            boolean r2 = isCarrierConfigSimPresent(r2, r3)
            if (r2 != 0) goto L_0x002f
            r2 = 1
            return r2
        L_0x002f:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.wifi.EapSimRequiredDialog.shouldShowSimRequiredDialog(android.content.Context, com.android.wifitrackerlib.WifiEntry):boolean");
    }

    static String removeDoubleQuotes(String str) {
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    private static boolean isCarrierConfigSimPresent(Context context, int i) {
        int simStateForSlotIndex;
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) context.getSystemService("telephony_subscription_service")).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null && !activeSubscriptionInfoList.isEmpty()) {
            for (SubscriptionInfo next : activeSubscriptionInfoList) {
                if (next.getCarrierId() == i && ((simStateForSlotIndex = SubscriptionManager.getSimStateForSlotIndex(next.getSimSlotIndex())) == 5 || simStateForSlotIndex == 10)) {
                    return true;
                }
            }
        }
        return false;
    }
}
