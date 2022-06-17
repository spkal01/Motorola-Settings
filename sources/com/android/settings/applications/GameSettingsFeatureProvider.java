package com.android.settings.applications;

import android.content.Context;

public interface GameSettingsFeatureProvider {
    boolean isSupported(Context context);

    void launchGameSettings(Context context);
}
