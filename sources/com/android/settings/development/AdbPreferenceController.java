package com.android.settings.development;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.AbstractEnableAdbPreferenceController;

public class AdbPreferenceController extends AbstractEnableAdbPreferenceController implements PreferenceControllerMixin {
    private final DevelopmentSettingsDashboardFragment mFragment;

    public AdbPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        this.mFragment = developmentSettingsDashboardFragment;
    }

    public void onAdbDialogConfirmed() {
        writeAdbSetting(true);
    }

    public void onAdbDialogDismissed() {
        updateState(this.mPreference);
    }

    public void showConfirmationDialog(Preference preference) {
        EnableAdbWarningDialog.show(this.mFragment);
    }

    /* access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeAdbSetting(false);
        this.mPreference.setChecked(false);
    }
}
