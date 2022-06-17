package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class SmartBatteryTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BatteryTip createFromParcel(Parcel parcel) {
            return new SmartBatteryTip(parcel);
        }

        public BatteryTip[] newArray(int i) {
            return new SmartBatteryTip[i];
        }
    };

    public SmartBatteryTip(int i) {
        super(0, i, false);
    }

    private SmartBatteryTip(Parcel parcel) {
        super(parcel);
    }

    public CharSequence getTitle(Context context) {
        return context.getString(C1992R$string.battery_tip_smart_battery_title);
    }

    public CharSequence getSummary(Context context) {
        return context.getString(C1992R$string.battery_tip_smart_battery_summary);
    }

    public int getIconId() {
        return C1983R$drawable.ic_perm_device_information_red_24dp;
    }

    public void updateState(BatteryTip batteryTip) {
        this.mState = batteryTip.mState;
    }

    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1350, this.mState);
    }
}
