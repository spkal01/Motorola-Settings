package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.C1981R$color;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class SummaryTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BatteryTip createFromParcel(Parcel parcel) {
            return new SummaryTip(parcel);
        }

        public BatteryTip[] newArray(int i) {
            return new SummaryTip[i];
        }
    };
    private long mAverageTimeMs;

    SummaryTip(Parcel parcel) {
        super(parcel);
        this.mAverageTimeMs = parcel.readLong();
    }

    public CharSequence getTitle(Context context) {
        return context.getString(C1992R$string.battery_tip_summary_title);
    }

    public CharSequence getSummary(Context context) {
        return context.getString(C1992R$string.battery_tip_summary_summary);
    }

    public int getIconId() {
        return C1983R$drawable.ic_battery_status_good_24dp;
    }

    public int getIconTintColorId() {
        return C1981R$color.battery_good_color_light;
    }

    public void updateState(BatteryTip batteryTip) {
        this.mState = batteryTip.mState;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(this.mAverageTimeMs);
    }

    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1349, this.mState);
    }
}
