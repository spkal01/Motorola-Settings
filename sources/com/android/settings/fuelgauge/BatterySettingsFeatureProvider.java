package com.android.settings.fuelgauge;

import android.content.ComponentName;

public interface BatterySettingsFeatureProvider {
    ComponentName getReplacingActivity(ComponentName componentName);
}
