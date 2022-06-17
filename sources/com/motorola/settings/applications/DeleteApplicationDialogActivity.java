package com.motorola.settings.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C1992R$string;
import com.android.settingslib.Utils;
import com.motorola.settingslib.InstalledAppUtils;
import java.lang.ref.WeakReference;

public class DeleteApplicationDialogActivity extends Activity {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!"user".equals(Build.TYPE));
    /* access modifiers changed from: private */
    public static String mPackageName;
    /* access modifiers changed from: private */
    public DialogInfo dialogInfo;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (!DeleteApplicationDialogActivity.this.isFinishing() && !DeleteApplicationDialogActivity.this.isDestroyed()) {
                if (message.arg1 != 1) {
                    Log.d("DeleteApplicationDialogActivity", "Uninstall failed for " + DeleteApplicationDialogActivity.mPackageName + " with code " + message.arg1);
                    Toast.makeText(DeleteApplicationDialogActivity.this.mContext, DeleteApplicationDialogActivity.this.getString(C1992R$string.uninstall_failed), 1).show();
                    DeleteApplicationDialogActivity.this.setResultAndFinish(0);
                    return;
                }
                Toast.makeText(DeleteApplicationDialogActivity.this.mContext, DeleteApplicationDialogActivity.this.getString(C1992R$string.uninstall_done), 1).show();
                DeleteApplicationDialogActivity.this.setResultAndFinish(-1);
            }
        }
    };
    protected PackageManager mPm;
    private UserHandle mUser;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (DEBUG) {
            Log.d("DeleteApplicationDialogActivity", "onCreate()");
        }
        if (!getString(C1992R$string.launcher_packagename).equals(getCallingPackage())) {
            finish();
            return;
        }
        Intent intent = getIntent();
        this.dialogInfo = new DialogInfo();
        this.mContext = getBaseContext();
        mPackageName = intent != null ? intent.getStringExtra("delete_packagename") : null;
        this.mPm = getPackageManager();
        UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        this.mUser = userHandle;
        if (userHandle == null) {
            this.mUser = Process.myUserHandle();
        } else if (!((UserManager) getSystemService("user")).getUserProfiles().contains(this.mUser)) {
            Context context = this.mContext;
            Toast.makeText(context, "User " + Process.myUserHandle() + " can't request uninstall for user " + this.mUser, 1).show();
            setResultAndFinish(0);
            return;
        }
        String str = mPackageName;
        if (str != null) {
            try {
                this.dialogInfo.mPackageInfo = this.mPm.getPackageInfo(str, 12864);
                this.dialogInfo.mAppInfo = this.mPm.getApplicationInfo(mPackageName, 8704);
                showDialogInner(1);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("DeleteApplicationDialogActivity", "Exception when retrieving package:" + mPackageName, e);
            }
        }
    }

    private boolean isCanDisabledApp(String str) {
        return !InstalledAppUtils.get(this.mContext).isPackageNonDisableable(str) && !Utils.isSystemPackage(getResources(), this.mPm, this.dialogInfo.mPackageInfo);
    }

    /* access modifiers changed from: package-private */
    public void setResultAndFinish(int i) {
        setResult(i);
        finish();
    }

    /* access modifiers changed from: protected */
    public void showDialogInner(int i) {
        DeleteApplicationDialogFragment newInstance = DeleteApplicationDialogFragment.newInstance(i);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        newInstance.setCancelable(false);
        newInstance.show(beginTransaction, "DeleteApplicationDialog " + i);
    }

    private boolean isAppInstalled(String str) {
        try {
            getPackageManager().getApplicationInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException | NullPointerException unused) {
            return false;
        }
    }

    private void startUninstallPkg(String str, boolean z) {
        getPackageManager().deletePackageAsUser(str, new PackageDeleteObserver(), z ? 2 : 0, this.mUser.getIdentifier());
    }

    /* access modifiers changed from: private */
    public void startDeleteProcess() {
        if (!isAppInstalled(mPackageName)) {
            if (DEBUG) {
                Log.d("DeleteApplicationDialogActivity", "Tried to uninstall pkg '" + mPackageName + "' but it's not installed");
            }
        } else if (!isCanDisabledApp(mPackageName)) {
            showDialogInner(3);
        } else if ((this.dialogInfo.mAppInfo.flags & 1) != 0) {
            new DisableChanger(this, this.dialogInfo.mAppInfo, 3).execute(new Object[]{null});
            showDialogInner(2);
        } else {
            startUninstallPkg(mPackageName, true);
        }
    }

    private static class DisableChanger extends AsyncTask<Object, Object, Object> {
        final WeakReference<DeleteApplicationDialogActivity> mActivity;
        final ApplicationInfo mInfo;
        final PackageManager mPm;
        final int mState;

        DisableChanger(DeleteApplicationDialogActivity deleteApplicationDialogActivity, ApplicationInfo applicationInfo, int i) {
            this.mPm = deleteApplicationDialogActivity.mPm;
            this.mActivity = new WeakReference<>(deleteApplicationDialogActivity);
            this.mInfo = applicationInfo;
            this.mState = i;
        }

        /* access modifiers changed from: protected */
        public Object doInBackground(Object... objArr) {
            this.mPm.setApplicationEnabledSetting(this.mInfo.packageName, this.mState, 0);
            return null;
        }
    }

    static class DialogInfo {
        ApplicationInfo mAppInfo;
        PackageInfo mPackageInfo;

        DialogInfo() {
        }
    }

    class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        PackageDeleteObserver() {
        }

        public void packageDeleted(String str, int i) {
            Message obtainMessage = DeleteApplicationDialogActivity.this.mHandler.obtainMessage(1);
            obtainMessage.arg1 = i;
            obtainMessage.obj = str;
            DeleteApplicationDialogActivity.this.mHandler.sendMessage(obtainMessage);
        }
    }

    public static class DeleteApplicationDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
        static DeleteApplicationDialogFragment newInstance(int i) {
            DeleteApplicationDialogFragment deleteApplicationDialogFragment = new DeleteApplicationDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            deleteApplicationDialogFragment.setArguments(bundle);
            return deleteApplicationDialogFragment;
        }

        public AlertDialog onCreateDialog(Bundle bundle) {
            int i = getArguments().getInt("id");
            DialogInfo access$300 = ((DeleteApplicationDialogActivity) getActivity()).dialogInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            CharSequence loadLabel = access$300.mAppInfo.loadLabel(packageManager);
            if (i == 1) {
                return new AlertDialog.Builder(getActivity()).setTitle(loadLabel).setIcon(access$300.mAppInfo.loadIcon(packageManager)).setMessage(getActivity().getText(C1992R$string.delete_confirm_question)).setPositiveButton(C1992R$string.confirm_delet_app, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((DeleteApplicationDialogActivity) DeleteApplicationDialogFragment.this.getActivity()).startDeleteProcess();
                    }
                }).setNegativeButton(C1992R$string.dlg_cancel, this).create();
            }
            if (i == 2) {
                return new AlertDialog.Builder(getActivity()).setTitle(loadLabel).setIcon(access$300.mAppInfo.loadIcon(packageManager)).setMessage(getActivity().getText(C1992R$string.disable_system_app_warning)).setNegativeButton(C1992R$string.delet_app_got_it, this).create();
            }
            if (i != 3) {
                return null;
            }
            return new AlertDialog.Builder(getActivity()).setTitle(loadLabel).setIcon(access$300.mAppInfo.loadIcon(packageManager)).setMessage(getActivity().getText(C1992R$string.system_app_warning)).setNegativeButton(C1992R$string.delet_app_got_it, this).create();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                if (DeleteApplicationDialogActivity.DEBUG) {
                    Log.d("DeleteApplicationDialogActivity", "DialogInterface.BUTTON_POSITIVE");
                }
                ((DeleteApplicationDialogActivity) getActivity()).setResultAndFinish(-1);
            } else if (i == -2) {
                if (DeleteApplicationDialogActivity.DEBUG) {
                    Log.d("DeleteApplicationDialogActivity", "DialogInterface.BUTTON_NEGATIVE");
                }
                ((DeleteApplicationDialogActivity) getActivity()).setResultAndFinish(0);
            }
        }

        public void onCancel(DialogInterface dialogInterface) {
            if (DeleteApplicationDialogActivity.DEBUG) {
                Log.d("DeleteApplicationDialogActivity", "onCancel()");
            }
            ((DeleteApplicationDialogActivity) getActivity()).setResultAndFinish(0);
        }
    }
}
