package com.motorola.settings.network.telephony;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SubscriptionManager;
import android.view.textclassifier.Log;
import android.widget.Toast;
import androidx.slice.Slice;
import com.android.settings.C1992R$string;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkProviderWorker;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.CarrierMobileDataWarningDialogUtils;
import com.motorola.settings.network.MotoMobileNetworkUtils;

public abstract class AbstractMobileDataSlice implements CustomSliceable {
    public static final String TAG = "AbstractMobileDataSlice";
    private final Context mContext;
    private final ExtendedProviderModelSliceHelper mHelper;

    public Slice getSlice() {
        return null;
    }

    public AbstractMobileDataSlice(Context context) {
        this.mContext = context;
        this.mHelper = new ExtendedProviderModelSliceHelper(context, this);
    }

    public Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return NetworkProviderWorker.class;
    }

    /* access modifiers changed from: package-private */
    public NetworkProviderWorker getWorker() {
        return (NetworkProviderWorker) SliceBackgroundWorker.getInstance(CustomSliceRegistry.PROVIDER_MODEL_SLICE_URI);
    }

    public void onNotifyChange(Intent intent) {
        int intExtra = intent.getIntExtra("android.provider.extra.SUB_ID", -1);
        if (intExtra != -1) {
            boolean hasExtra = intent.hasExtra("android.app.slice.extra.TOGGLE_STATE");
            boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", !hasExtra);
            if (this.mHelper.isDebuggable()) {
                String str = TAG;
                Log.d(str, "isToggleAction: " + hasExtra);
                Log.d(str, "subscriptionId: " + intExtra);
                Log.d(str, "newState: " + booleanExtra);
            }
            if (onBeforeToggleAction(intent, intExtra, booleanExtra)) {
                return;
            }
            if (intExtra == SubscriptionManager.getDefaultDataSubscriptionId()) {
                MobileNetworkUtils.setMobileDataEnabled(this.mContext, intExtra, booleanExtra, false);
            } else {
                MotoMobileNetworkUtils.setDefaultDataSubIdWithNwAutoSwitch(this.mContext, intExtra);
            }
        }
    }

    public Intent getIntent() {
        return new Intent(this.mContext, MobileNetworkActivity.class);
    }

    public boolean onBeforeToggleAction(Intent intent, int i, boolean z) {
        if (this.mHelper.isInCall() && i != SubscriptionManager.getDefaultDataSubscriptionId()) {
            Toast.makeText(this.mContext, C1992R$string.internet_slice_incall_warning_message, 0).show();
            NetworkProviderWorker worker = getWorker();
            if (worker != null) {
                worker.updateSlice();
            }
            return true;
        } else if (z || !CarrierMobileDataWarningDialogUtils.isCarrierFtrEnabled(this.mContext)) {
            if (!z && this.mHelper.isMobileDataDisableDialogEnabled()) {
                AlertDialog mobileDataDisableDialog = this.mHelper.getMobileDataDisableDialog(this.mHelper.getMobileTitle(), new AbstractMobileDataSlice$$ExternalSyntheticLambda1(this, i), new AbstractMobileDataSlice$$ExternalSyntheticLambda0(this));
                if (mobileDataDisableDialog.getWindow() != null) {
                    mobileDataDisableDialog.getWindow().setType(2009);
                    mobileDataDisableDialog.show();
                    return true;
                }
            }
            return false;
        } else {
            CarrierMobileDataWarningDialogUtils.sendDialogIntent(this.mContext, i);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBeforeToggleAction$0(int i, DialogInterface dialogInterface, int i2) {
        this.mHelper.setMobileDataDisableDialogEnabled(false);
        MobileNetworkUtils.setMobileDataEnabled(this.mContext, i, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBeforeToggleAction$1(DialogInterface dialogInterface, int i) {
        NetworkProviderWorker worker = getWorker();
        if (worker != null) {
            worker.updateSlice();
        }
    }
}
