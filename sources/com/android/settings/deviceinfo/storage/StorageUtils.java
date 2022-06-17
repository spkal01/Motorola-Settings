package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1992R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.PrivateVolumeForget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StorageUtils {
    public static List<StorageEntry> getAllStorageEntries(Context context, StorageManager storageManager) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll((Collection) storageManager.getVolumes().stream().filter(StorageUtils$$ExternalSyntheticLambda5.INSTANCE).map(new StorageUtils$$ExternalSyntheticLambda0(context)).collect(Collectors.toList()));
        arrayList.addAll((Collection) storageManager.getDisks().stream().filter(StorageUtils$$ExternalSyntheticLambda4.INSTANCE).map(StorageUtils$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toList()));
        arrayList.addAll((Collection) storageManager.getVolumeRecords().stream().filter(new StorageUtils$$ExternalSyntheticLambda3(storageManager)).map(StorageUtils$$ExternalSyntheticLambda2.INSTANCE).collect(Collectors.toList()));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ StorageEntry lambda$getAllStorageEntries$1(Context context, VolumeInfo volumeInfo) {
        return new StorageEntry(context, volumeInfo);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ StorageEntry lambda$getAllStorageEntries$3(DiskInfo diskInfo) {
        return new StorageEntry(diskInfo);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ StorageEntry lambda$getAllStorageEntries$5(VolumeRecord volumeRecord) {
        return new StorageEntry(volumeRecord);
    }

    public static boolean isStorageSettingsInterestedVolume(VolumeInfo volumeInfo) {
        int type = volumeInfo.getType();
        return type == 0 || type == 1 || type == 5;
    }

    public static boolean isVolumeRecordMissed(StorageManager storageManager, VolumeRecord volumeRecord) {
        if (volumeRecord.getType() == 1 && storageManager.findVolumeByUuid(volumeRecord.getFsUuid()) == null) {
            return true;
        }
        return false;
    }

    public static boolean isDiskUnsupported(DiskInfo diskInfo) {
        return diskInfo.volumeCount == 0 && diskInfo.size > 0;
    }

    public static void launchForgetMissingVolumeRecordFragment(Context context, StorageEntry storageEntry) {
        if (storageEntry != null && storageEntry.isVolumeRecordMissed()) {
            Bundle bundle = new Bundle();
            bundle.putString("android.os.storage.extra.FS_UUID", storageEntry.getFsUuid());
            new SubSettingLauncher(context).setDestination(PrivateVolumeForget.class.getCanonicalName()).setTitleRes(C1992R$string.storage_menu_forget).setSourceMetricsCategory(745).setArguments(bundle).launch();
        }
    }

    public static String getStorageSizeLabel(Context context, long j) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 1);
        return TextUtils.expandTemplate(context.getText(C1992R$string.storage_size_large), new CharSequence[]{formatBytes.value, formatBytes.units}).toString();
    }

    public static class UnmountTask extends AsyncTask<Void, Void, Exception> {
        private final Context mContext;
        private final String mDescription;
        private final StorageManager mStorageManager;
        private final String mVolumeId;

        public UnmountTask(Context context, VolumeInfo volumeInfo) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            StorageManager storageManager = (StorageManager) applicationContext.getSystemService(StorageManager.class);
            this.mStorageManager = storageManager;
            this.mVolumeId = volumeInfo.getId();
            this.mDescription = storageManager.getBestVolumeDescription(volumeInfo);
        }

        /* access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            try {
                this.mStorageManager.unmount(this.mVolumeId);
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(C1992R$string.storage_unmount_success, new Object[]{this.mDescription}), 0).show();
                return;
            }
            Log.e("StorageUtils", "Failed to unmount " + this.mVolumeId, exc);
            Context context2 = this.mContext;
            Toast.makeText(context2, context2.getString(C1992R$string.storage_unmount_failure, new Object[]{this.mDescription}), 0).show();
        }
    }

    public static class MountTask extends AsyncTask<Void, Void, Exception> {
        private final Context mContext;
        private final String mDescription;
        private final StorageManager mStorageManager;
        private final String mVolumeId;

        public MountTask(Context context, VolumeInfo volumeInfo) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            StorageManager storageManager = (StorageManager) applicationContext.getSystemService(StorageManager.class);
            this.mStorageManager = storageManager;
            this.mVolumeId = volumeInfo.getId();
            this.mDescription = storageManager.getBestVolumeDescription(volumeInfo);
        }

        /* access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            try {
                this.mStorageManager.mount(this.mVolumeId);
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(C1992R$string.storage_mount_success, new Object[]{this.mDescription}), 0).show();
                return;
            }
            Log.e("StorageUtils", "Failed to mount " + this.mVolumeId, exc);
            Context context2 = this.mContext;
            Toast.makeText(context2, context2.getString(C1992R$string.storage_mount_failure, new Object[]{this.mDescription}), 0).show();
        }
    }

    public static class SystemInfoFragment extends InstrumentedDialogFragment {
        public int getMetricsCategory() {
            return 565;
        }

        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setMessage((CharSequence) getContext().getString(C1992R$string.storage_detail_dialog_system, new Object[]{Build.VERSION.RELEASE_OR_CODENAME})).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
        }
    }
}
