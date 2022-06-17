package com.android.settings.backup;

import android.content.Context;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class PrivacySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.privacy_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            BackupSettingsHelper backupSettingsHelper = new BackupSettingsHelper(context);
            return !backupSettingsHelper.isBackupProvidedByManufacturer() && !backupSettingsHelper.isIntentProvidedByTransport();
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "PrivacySettings";
    }

    public int getMetricsCategory() {
        return 81;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.privacy_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_backup_reset;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        updatePrivacySettingsConfigData(context);
    }

    /* access modifiers changed from: protected */
    public void updatePreferenceStates() {
        updatePrivacySettingsConfigData(getContext());
        super.updatePreferenceStates();
    }

    private void updatePrivacySettingsConfigData(Context context) {
        if (PrivacySettingsUtils.isAdminUser(context)) {
            PrivacySettingsUtils.updatePrivacyBuffer(context, PrivacySettingsConfigData.getInstance());
        }
    }
}
