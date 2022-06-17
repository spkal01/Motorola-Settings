package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.C1981R$color;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class EarlyWarningTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BatteryTip createFromParcel(Parcel parcel) {
            return new EarlyWarningTip(parcel);
        }

        public BatteryTip[] newArray(int i) {
            return new EarlyWarningTip[i];
        }
    };
    private boolean mPowerSaveModeOn;

    public EarlyWarningTip(int i, boolean z) {
        super(3, i, false);
        this.mPowerSaveModeOn = z;
    }

    public EarlyWarningTip(Parcel parcel) {
        super(parcel);
        this.mPowerSaveModeOn = parcel.readBoolean();
    }

    public CharSequence getTitle(Context context) {
        int i = C1992R$string.battery_tip_early_heads_up_title;
        this.mState = i;
        return context.getString(i);
    }

    public CharSequence getSummary(Context context) {
        int i = C1992R$string.battery_tip_early_heads_up_summary;
        this.mState = i;
        return context.getString(i);
    }

    public int getIconId() {
        int i = C1983R$drawable.ic_battery_status_bad_24dp;
        this.mState = i;
        return i;
    }

    public int getIconTintColorId() {
        int i = C1981R$color.battery_bad_color_light;
        this.mState = i;
        return i;
    }

    public void updateState(BatteryTip batteryTip) {
        EarlyWarningTip earlyWarningTip = (EarlyWarningTip) batteryTip;
        if (earlyWarningTip.mState == 0) {
            this.mState = 0;
        } else if (earlyWarningTip.mPowerSaveModeOn) {
            this.mState = 2;
        } else {
            this.mState = earlyWarningTip.getState();
        }
        this.mPowerSaveModeOn = earlyWarningTip.mPowerSaveModeOn;
    }

    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1351, this.mState);
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeBoolean(this.mPowerSaveModeOn);
    }
}
