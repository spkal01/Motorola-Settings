package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.utils.ThreadUtils;

public class EmptyTrashFragment extends InstrumentedDialogFragment {
    private final OnEmptyTrashCompleteListener mOnEmptyTrashCompleteListener;
    private final Fragment mParentFragment;
    private final long mTrashSize;
    private final int mUserId;

    public interface OnEmptyTrashCompleteListener {
        void onEmptyTrashComplete();
    }

    public int getMetricsCategory() {
        return 1875;
    }

    public EmptyTrashFragment(Fragment fragment, int i, long j, OnEmptyTrashCompleteListener onEmptyTrashCompleteListener) {
        this.mParentFragment = fragment;
        setTargetFragment(fragment, 0);
        this.mUserId = i;
        this.mTrashSize = j;
        this.mOnEmptyTrashCompleteListener = onEmptyTrashCompleteListener;
    }

    public void show() {
        show(this.mParentFragment.getFragmentManager(), "empty_trash");
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(C1992R$string.storage_trash_dialog_title).setMessage((CharSequence) getActivity().getString(C1992R$string.storage_trash_dialog_ask_message, new Object[]{StorageUtils.getStorageSizeLabel(getActivity(), this.mTrashSize)})).setPositiveButton(C1992R$string.storage_trash_dialog_confirm, (DialogInterface.OnClickListener) new EmptyTrashFragment$$ExternalSyntheticLambda0(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        emptyTrashAsync();
    }

    private void emptyTrashAsync() {
        FragmentActivity activity = getActivity();
        try {
            Context createPackageContextAsUser = activity.createPackageContextAsUser(activity.getApplicationContext().getPackageName(), 0, UserHandle.of(this.mUserId));
            Bundle bundle = new Bundle();
            bundle.putInt("android:query-arg-match-trashed", 3);
            ThreadUtils.postOnBackgroundThread((Runnable) new EmptyTrashFragment$$ExternalSyntheticLambda2(this, createPackageContextAsUser, bundle));
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("EmptyTrashFragment", "Not able to get Context for user ID " + this.mUserId);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$emptyTrashAsync$2(Context context, Bundle bundle) {
        context.getContentResolver().delete(MediaStore.Files.getContentUri("external"), bundle);
        if (this.mOnEmptyTrashCompleteListener != null) {
            ThreadUtils.postOnMainThread(new EmptyTrashFragment$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$emptyTrashAsync$1() {
        this.mOnEmptyTrashCompleteListener.onEmptyTrashComplete();
    }
}
