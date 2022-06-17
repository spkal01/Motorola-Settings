package com.android.settings.security;

import android.app.Dialog;
import android.app.admin.DevicePolicyEventLogger;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.widget.ActionButtonsPreference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CredentialManagementAppButtonsController extends BasePreferenceController {
    private static final String TAG = "CredentialManagementApp";
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Fragment mFragment;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mHasCredentialManagerPackage;
    private final int mRemoveIcon;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 1;
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

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CredentialManagementAppButtonsController(Context context, String str) {
        super(context, str);
        if (context.getResources().getConfiguration().getLayoutDirection() == 1) {
            this.mRemoveIcon = C1983R$drawable.ic_redo_24;
        } else {
            this.mRemoveIcon = C1983R$drawable.ic_undo_24;
        }
    }

    public void setParentFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mExecutor.execute(new C1272x68006a68(this, preferenceScreen));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(PreferenceScreen preferenceScreen) {
        try {
            this.mHasCredentialManagerPackage = KeyChain.bind(this.mContext).getService().hasCredentialManagementApp();
        } catch (RemoteException | InterruptedException unused) {
            Log.e(TAG, "Unable to display credential management app buttons");
        }
        this.mHandler.post(new C1273x68006a69(this, preferenceScreen));
    }

    /* access modifiers changed from: private */
    /* renamed from: displayButtons */
    public void lambda$displayPreference$0(PreferenceScreen preferenceScreen) {
        if (this.mHasCredentialManagerPackage) {
            ((ActionButtonsPreference) preferenceScreen.findPreference(getPreferenceKey())).setButton1Text(C1992R$string.uninstall_certs_credential_management_app).setButton1Icon(C1983R$drawable.ic_upload).setButton1OnClickListener(new C1269x68006a65(this)).setButton2Text(C1992R$string.remove_credential_management_app).setButton2Icon(this.mRemoveIcon).setButton2OnClickListener(new C1270x68006a66(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$displayButtons$2(View view) {
        uninstallCertificates();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$displayButtons$3(View view) {
        showRemoveCredentialManagementAppDialog();
    }

    private void uninstallCertificates() {
        this.mExecutor.execute(new C1271x68006a67(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$uninstallCertificates$4() {
        try {
            IKeyChainService service = KeyChain.bind(this.mContext).getService();
            for (String removeKeyPair : service.getCredentialManagementAppPolicy().getAliases()) {
                service.removeKeyPair(removeKeyPair);
            }
        } catch (RemoteException | InterruptedException unused) {
            Log.e(TAG, "Unable to uninstall certificates");
        }
    }

    private void showRemoveCredentialManagementAppDialog() {
        RemoveCredentialManagementAppDialog.newInstance().show(this.mFragment.getParentFragmentManager(), RemoveCredentialManagementAppDialog.class.getName());
    }

    public static class RemoveCredentialManagementAppDialog extends InstrumentedDialogFragment {
        public int getMetricsCategory() {
            return 1895;
        }

        public static RemoveCredentialManagementAppDialog newInstance() {
            return new RemoveCredentialManagementAppDialog();
        }

        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getContext(), C1993R$style.Theme_AlertDialog).setTitle(C1992R$string.remove_credential_management_app_dialog_title).setMessage(C1992R$string.remove_credential_management_app_dialog_message).setPositiveButton(C1992R$string.remove_credential_management_app, (DialogInterface.OnClickListener) new C1274xe8f0fbac(this)).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) new C1275xe8f0fbad(this)).create();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
            removeCredentialManagementApp();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
            dismiss();
        }

        private void removeCredentialManagementApp() {
            Executors.newSingleThreadExecutor().execute(new C1276xe8f0fbae(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$removeCredentialManagementApp$2() {
            try {
                KeyChain.bind(getContext()).getService().removeCredentialManagementApp();
                DevicePolicyEventLogger.createEvent(187).write();
                getParentFragment().getActivity().finish();
            } catch (RemoteException | InterruptedException unused) {
                Log.e(CredentialManagementAppButtonsController.TAG, "Unable to remove the credential management app");
            }
        }
    }
}
