package com.android.settings.development.storage;

import android.app.blob.BlobStoreManager;
import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.io.IOException;

public class SharedDataPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private BlobStoreManager mBlobStoreManager;

    public String getPreferenceKey() {
        return "shared_data";
    }

    public SharedDataPreferenceController(Context context) {
        super(context);
        this.mBlobStoreManager = (BlobStoreManager) context.getSystemService(BlobStoreManager.class);
    }

    public void updateState(Preference preference) {
        int i;
        try {
            BlobStoreManager blobStoreManager = this.mBlobStoreManager;
            boolean z = blobStoreManager != null && !blobStoreManager.queryBlobsForUser(UserHandle.CURRENT).isEmpty();
            preference.setEnabled(z);
            if (z) {
                i = C1992R$string.shared_data_summary;
            } else {
                i = C1992R$string.shared_data_no_blobs_text;
            }
            preference.setSummary(i);
        } catch (IOException e) {
            Log.e("SharedDataPrefCtrl", "Unable to fetch blobs for current user: " + e.getMessage());
            preference.setEnabled(false);
            preference.setSummary(C1992R$string.shared_data_no_blobs_text);
        }
    }
}
