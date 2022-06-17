package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.bluetooth.BluetoothPairingDetail;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class HearingAidDialogFragment extends InstrumentedDialogFragment {
    public int getMetricsCategory() {
        return 1512;
    }

    public static HearingAidDialogFragment newInstance() {
        return new HearingAidDialogFragment();
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(C1992R$string.accessibility_hearingaid_pair_instructions_message).setPositiveButton(C1992R$string.accessibility_hearingaid_instruction_continue_button, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                HearingAidDialogFragment.this.launchBluetoothAddDeviceSetting();
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
    }

    /* access modifiers changed from: private */
    public void launchBluetoothAddDeviceSetting() {
        new SubSettingLauncher(getActivity()).setDestination(BluetoothPairingDetail.class.getName()).setSourceMetricsCategory(2).launch();
    }
}
