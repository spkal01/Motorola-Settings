package com.android.settings.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.fuelgauge.BatterySaverUtils;

public class BatterySaverController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, BatterySaverReceiver.BatterySaverListener {
    private static final String KEY_BATTERY_SAVER = "battery_saver_summary";
    private Preference mBatterySaverPref;
    private final BatterySaverReceiver mBatteryStateChangeReceiver;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            BatterySaverController.this.updateSummary();
        }
    };
    private final PowerManager mPowerManager = ((PowerManager) this.mContext.getSystemService(PowerManager.class));

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public String getPreferenceKey() {
        return KEY_BATTERY_SAVER;
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

    public void onBatteryChanged(boolean z) {
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BatterySaverController(Context context) {
        super(context, KEY_BATTERY_SAVER);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mBatteryStateChangeReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
        BatterySaverUtils.revertScheduleToNoneIfNeeded(context);
    }

    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_battery_saver_customizable) ? 3 : 1;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mBatterySaverPref = preferenceScreen.findPreference(KEY_BATTERY_SAVER);
    }

    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), true, this.mObserver);
        this.mBatteryStateChangeReceiver.setListening(true);
        updateSummary();
    }

    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
        this.mBatteryStateChangeReceiver.setListening(false);
    }

    public CharSequence getSummary() {
        if (this.mPowerManager.isPowerSaveMode()) {
            return this.mContext.getString(C1992R$string.battery_saver_on_summary);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (Settings.Global.getInt(contentResolver, "automatic_power_save_mode", 0) != 0) {
            return this.mContext.getString(C1992R$string.battery_saver_pref_auto_routine_summary);
        }
        int i = Settings.Global.getInt(contentResolver, "low_power_trigger_level", 0);
        if (i == 0) {
            return this.mContext.getString(C1992R$string.battery_saver_off_summary);
        }
        return this.mContext.getString(C1992R$string.battery_saver_off_scheduled_summary, new Object[]{Utils.formatPercentage(i)});
    }

    /* access modifiers changed from: private */
    public void updateSummary() {
        Preference preference = this.mBatterySaverPref;
        if (preference != null) {
            preference.setSummary(getSummary());
        }
    }

    private void logPowerSaver() {
        if (this.mPowerManager.isPowerSaveMode()) {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            int i = Settings.Global.getInt(contentResolver, "automatic_power_save_mode", 0);
            int i2 = 2;
            if (i == 0) {
                this.mMetricsFeatureProvider.action(this.mContext, 52, (Pair<Integer, Object>[]) new Pair[]{Pair.create(1784, 3), Pair.create(1785, Integer.valueOf(Settings.Global.getInt(contentResolver, "low_power_trigger_level", 0)))});
                i2 = 3;
            } else if (i != 1) {
                i2 = 1;
            }
            this.mMetricsFeatureProvider.action(this.mContext, 52, (Pair<Integer, Object>[]) new Pair[]{Pair.create(1784, Integer.valueOf(i2))});
        }
    }

    public void onPowerSaveModeChanged() {
        updateSummary();
        logPowerSaver();
    }
}
