package com.android.settings.sim;

import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import com.android.settings.C1992R$string;
import com.android.settings.SidecarFragment;
import com.android.settings.network.SwitchToEuiccSubscriptionSidecar;
import com.android.settings.network.telephony.AlertDialogFragment;
import com.android.settings.network.telephony.ConfirmDialogFragment;
import com.android.settings.network.telephony.SubscriptionActionDialogActivity;

public class SwitchToEsimConfirmDialogActivity extends SubscriptionActionDialogActivity implements SidecarFragment.Listener, ConfirmDialogFragment.OnConfirmListener {
    private SubscriptionInfo mSubToEnabled = null;
    private SwitchToEuiccSubscriptionSidecar mSwitchToEuiccSubscriptionSidecar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSubToEnabled = (SubscriptionInfo) getIntent().getParcelableExtra("sub_to_enable");
        this.mSwitchToEuiccSubscriptionSidecar = SwitchToEuiccSubscriptionSidecar.get(getFragmentManager());
        SubscriptionInfo subscriptionInfo = this.mSubToEnabled;
        if (subscriptionInfo == null) {
            Log.e("SwitchToEsimConfirmDialogActivity", "Cannot find SIM to enable.");
            finish();
        } else if (bundle == null) {
            ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 1, getString(C1992R$string.switch_sim_dialog_title, new Object[]{subscriptionInfo.getDisplayName()}), getString(C1992R$string.switch_sim_dialog_text, new Object[]{this.mSubToEnabled.getDisplayName()}), getString(C1992R$string.okay), getString(C1992R$string.cancel));
        }
    }

    public void onResume() {
        super.onResume();
        this.mSwitchToEuiccSubscriptionSidecar.addListener(this);
    }

    public void onPause() {
        this.mSwitchToEuiccSubscriptionSidecar.removeListener(this);
        super.onPause();
    }

    public void onStateChange(SidecarFragment sidecarFragment) {
        SwitchToEuiccSubscriptionSidecar switchToEuiccSubscriptionSidecar = this.mSwitchToEuiccSubscriptionSidecar;
        if (sidecarFragment == switchToEuiccSubscriptionSidecar) {
            int state = switchToEuiccSubscriptionSidecar.getState();
            if (state == 2) {
                this.mSwitchToEuiccSubscriptionSidecar.reset();
                Log.i("SwitchToEsimConfirmDialogActivity", "Successfully switched to eSIM slot.");
                dismissProgressDialog();
                finish();
            } else if (state == 3) {
                this.mSwitchToEuiccSubscriptionSidecar.reset();
                Log.e("SwitchToEsimConfirmDialogActivity", "Failed switching to eSIM slot.");
                dismissProgressDialog();
                finish();
            }
        }
    }

    public void onConfirm(int i, boolean z) {
        if (!z) {
            AlertDialogFragment.show(this, getString(C1992R$string.switch_sim_dialog_no_switch_title), getString(C1992R$string.switch_sim_dialog_no_switch_text));
            return;
        }
        Log.i("SwitchToEsimConfirmDialogActivity", "User confirmed to switch to embedded slot.");
        this.mSwitchToEuiccSubscriptionSidecar.run(this.mSubToEnabled.getSubscriptionId());
        showProgressDialog(getString(C1992R$string.sim_action_switch_sub_dialog_progress, new Object[]{this.mSubToEnabled.getDisplayName()}));
    }
}
