package com.android.settings.backup;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class BackupDataPreferenceController extends BasePreferenceController {
    private PrivacySettingsConfigData mPSCD = PrivacySettingsConfigData.getInstance();

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

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BackupDataPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        if (!PrivacySettingsUtils.isAdminUser(this.mContext)) {
            return 4;
        }
        return PrivacySettingsUtils.isInvisibleKey(this.mContext, "backup_data") ? 3 : 0;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mPSCD.isBackupGray()) {
            preference.setEnabled(false);
        }
    }

    public CharSequence getSummary() {
        if (this.mPSCD.isBackupGray()) {
            return null;
        }
        if (this.mPSCD.isBackupEnabled()) {
            return this.mContext.getText(C1992R$string.accessibility_feature_state_on);
        }
        return this.mContext.getText(C1992R$string.accessibility_feature_state_off);
    }
}
