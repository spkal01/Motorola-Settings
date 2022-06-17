package com.motorola.settings.network.att;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedMobileDataPreferenceController;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoMobileNetworkUtils;

public class AttMobileDataPreferenceController extends ExtendedMobileDataPreferenceController {
    private static final String TAG = "AttMobileDataPreferenceController";
    private Intent mMotDataDisableDialogIntent;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AttMobileDataPreferenceController(Context context, String str) {
        super(context, str);
    }

    public boolean setChecked(boolean z) {
        if (isChecked()) {
            this.mMotDataDisableDialogIntent = getMotoDataDisableDialog();
        } else {
            this.mMotDataDisableDialogIntent = null;
        }
        MotoMnsLog.logd(TAG, "mMotDataDisableDialogIntent:" + this.mMotDataDisableDialogIntent);
        if (this.mMotDataDisableDialogIntent == null) {
            return super.setChecked(z);
        }
        return false;
    }

    private Intent getMotoDataDisableDialog() {
        Context context = this.mContext;
        return MotoMobileNetworkUtils.getIntentResolvedToSystemActivity(context, context.getString(C1992R$string.mobile_data_disable_dialog_intent));
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey()) || (intent = this.mMotDataDisableDialogIntent) == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        this.mContext.startActivity(intent);
        return true;
    }
}
