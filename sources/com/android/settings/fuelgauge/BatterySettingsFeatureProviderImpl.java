package com.android.settings.fuelgauge;

import android.content.ComponentName;
import android.content.Context;

public class BatterySettingsFeatureProviderImpl implements BatterySettingsFeatureProvider {
    protected Context mContext;

    public ComponentName getReplacingActivity(ComponentName componentName) {
        return componentName;
    }

    public BatterySettingsFeatureProviderImpl(Context context) {
        this.mContext = context.getApplicationContext();
    }
}
