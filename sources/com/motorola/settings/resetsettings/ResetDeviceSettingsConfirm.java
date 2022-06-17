package com.motorola.settings.resetsettings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.core.InstrumentedFragment;
import com.motorola.settings.resetsettings.ResetDeviceSettingsManagerTask;

public class ResetDeviceSettingsConfirm extends InstrumentedFragment implements ResetDeviceSettingsManagerTask.ResetDeviceSettingsManagerListener {
    Activity mActivity;
    View mContentView;
    View.OnClickListener mFinalClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            ResetDeviceSettingsConfirm.this.mResetTask.reset();
            ResetDeviceSettingsConfirm.this.getActivity().getWindow().setFlags(16, 16);
            ResetDeviceSettingsConfirm.this.mContentView.findViewById(C1985R$id.execute_device_settings).setEnabled(false);
        }
    };
    private ProgressDialog mProgressDialog;
    /* access modifiers changed from: private */
    public ResetDeviceSettingsManagerTask mResetTask;

    public int getMetricsCategory() {
        return 84;
    }

    private ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(C1992R$string.reset_device_settings_progress_text));
        return progressDialog;
    }

    private void establishFinalConfirmationState() {
        this.mContentView.findViewById(C1985R$id.execute_device_settings).setOnClickListener(this.mFinalClickListener);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mContentView = layoutInflater.inflate(C1987R$layout.reset_device_settings_confirm, (ViewGroup) null);
        establishFinalConfirmationState();
        return this.mContentView;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mActivity = getActivity();
        this.mResetTask = ResetDeviceSettingsManagerTask.getInstance();
        updateUI();
    }

    public void onStart() {
        super.onStart();
        this.mResetTask.attach(this.mActivity, this);
    }

    public void onStop() {
        this.mResetTask.detach();
        super.onStop();
    }

    public void onDestroy() {
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    public void onInit() {
        updateUI();
    }

    public void onSuccess() {
        updateUI();
        Toast.makeText(this.mActivity, C1992R$string.reset_device_settings_complete_toast, 0).show();
    }

    private void updateUI() {
        if (this.mResetTask.isInProgress()) {
            ProgressDialog progressDialog = getProgressDialog(this.mActivity);
            this.mProgressDialog = progressDialog;
            progressDialog.show();
            return;
        }
        ProgressDialog progressDialog2 = this.mProgressDialog;
        if (progressDialog2 != null) {
            progressDialog2.dismiss();
            this.mContentView.findViewById(C1985R$id.execute_device_settings).setEnabled(true);
            getActivity().getWindow().clearFlags(16);
        }
    }
}
