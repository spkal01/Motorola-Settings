package com.android.settings.applications;

import android.content.Context;

public class GameSettingsFeatureProviderImpl implements GameSettingsFeatureProvider {
    public boolean isSupported(Context context) {
        return false;
    }

    public void launchGameSettings(Context context) {
    }
}
