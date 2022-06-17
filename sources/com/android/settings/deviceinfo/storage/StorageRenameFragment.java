package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class StorageRenameFragment extends InstrumentedDialogFragment {
    public int getMetricsCategory() {
        return 563;
    }

    public static void show(Fragment fragment, VolumeInfo volumeInfo) {
        StorageRenameFragment storageRenameFragment = new StorageRenameFragment();
        storageRenameFragment.setTargetFragment(fragment, 0);
        Bundle bundle = new Bundle();
        bundle.putString("android.os.storage.extra.FS_UUID", volumeInfo.getFsUuid());
        storageRenameFragment.setArguments(bundle);
        storageRenameFragment.show(fragment.getFragmentManager(), "rename");
    }

    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        String string = getArguments().getString("android.os.storage.extra.FS_UUID");
        VolumeRecord findRecordByUuid = storageManager.findRecordByUuid(string);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = LayoutInflater.from(builder.getContext()).inflate(C1987R$layout.dialog_edittext, (ViewGroup) null, false);
        EditText editText = (EditText) inflate.findViewById(C1985R$id.edittext);
        editText.setText(findRecordByUuid.getNickname());
        editText.requestFocus();
        return builder.setTitle(C1992R$string.storage_rename_title).setView(inflate).setPositiveButton(C1992R$string.save, (DialogInterface.OnClickListener) new StorageRenameFragment$$ExternalSyntheticLambda0(storageManager, string, editText)).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }
}
