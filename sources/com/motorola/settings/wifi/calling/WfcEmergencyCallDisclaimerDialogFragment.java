package com.motorola.settings.wifi.calling;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;

public class WfcEmergencyCallDisclaimerDialogFragment extends DialogFragment {
    private static String TAG = "WfcEmergencyCallDisclaimerDialogFragment";
    /* access modifiers changed from: private */
    public WfcDisclaimerCallback mDisclaimerCallback;

    public interface WfcDisclaimerCallback {
        void accept();

        void cancel();
    }

    public static void show(FragmentManager fragmentManager, WfcDisclaimerCallback wfcDisclaimerCallback) {
        newInstance(wfcDisclaimerCallback).show(fragmentManager, TAG);
    }

    public static boolean shouldShow(Context context, int i, boolean z) {
        return z && !isDontShowAgainChecked(context);
    }

    public void setDisclaimerCallback(WfcDisclaimerCallback wfcDisclaimerCallback) {
        this.mDisclaimerCallback = wfcDisclaimerCallback;
    }

    private static WfcEmergencyCallDisclaimerDialogFragment newInstance(WfcDisclaimerCallback wfcDisclaimerCallback) {
        WfcEmergencyCallDisclaimerDialogFragment wfcEmergencyCallDisclaimerDialogFragment = new WfcEmergencyCallDisclaimerDialogFragment();
        wfcEmergencyCallDisclaimerDialogFragment.setDisclaimerCallback(wfcDisclaimerCallback);
        return wfcEmergencyCallDisclaimerDialogFragment;
    }

    private static boolean isDontShowAgainChecked(Context context) {
        String sharedPreferencesKey = getSharedPreferencesKey(context);
        return getSharedPreferences(context, sharedPreferencesKey).getBoolean(sharedPreferencesKey, false);
    }

    private static String getSharedPreferencesKey(Context context) {
        return context.getResources().getString(C1992R$string.show_customized_wfc_disclaimer_dialog_preference_key);
    }

    private static SharedPreferences getSharedPreferences(Context context, String str) {
        return context.getSharedPreferences(str, 0);
    }

    public static boolean isWFCEmergencyWarningsEnabled(Context context, int i) {
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("moto_show_customized_wfc_disclaimer_dialog");
    }

    public Dialog onCreateDialog(Bundle bundle) {
        View inflate = View.inflate(getContext(), C1987R$layout.vowifi_dialog_layout, (ViewGroup) null);
        final String sharedPreferencesKey = getSharedPreferencesKey(getContext());
        final SharedPreferences sharedPreferences = getSharedPreferences(getContext(), sharedPreferencesKey);
        ((CheckBox) inflate.findViewById(C1985R$id.dont_show_it_again_checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean(sharedPreferencesKey, z);
                edit.apply();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(inflate);
        builder.setTitle(getContext().getResources().getString(C1992R$string.vowifi_dialog_title));
        builder.setCancelable(false);
        builder.setPositiveButton(C1992R$string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (WfcEmergencyCallDisclaimerDialogFragment.this.mDisclaimerCallback != null) {
                    WfcEmergencyCallDisclaimerDialogFragment.this.mDisclaimerCallback.accept();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(C1992R$string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (WfcEmergencyCallDisclaimerDialogFragment.this.mDisclaimerCallback != null) {
                    WfcEmergencyCallDisclaimerDialogFragment.this.mDisclaimerCallback.cancel();
                }
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }
}
