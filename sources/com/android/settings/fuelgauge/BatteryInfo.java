package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.BatteryStats;
import android.os.BatteryStatsManager;
import android.os.BatteryUsageStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.SparseIntArray;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.UsageView;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.settingslib.utils.StringUtil;

public class BatteryInfo {
    public long averageTimeToDischarge = -1;
    public int batteryLevel;
    public String batteryPercentString;
    public int batteryStatus;
    public CharSequence chargeLabel;
    public boolean discharging = true;
    public boolean isOverheated;
    private BatteryUsageStats mBatteryUsageStats;
    /* access modifiers changed from: private */
    public boolean mCharging;
    public CharSequence remainingLabel;
    public long remainingTimeUs = 0;
    public String statusLabel;
    public String suggestionLabel;
    /* access modifiers changed from: private */
    public long timePeriod;

    public interface BatteryDataParser {
        void onDataGap();

        void onDataPoint(long j, BatteryStats.HistoryItem historyItem);

        void onParsingDone();

        void onParsingStarted(long j, long j2);
    }

    public interface Callback {
        void onBatteryInfoLoaded(BatteryInfo batteryInfo);
    }

    public void bindHistory(final UsageView usageView, BatteryDataParser... batteryDataParserArr) {
        String str;
        final Context context = usageView.getContext();
        C09461 r1 = new BatteryDataParser() {
            byte lastLevel;
            int lastTime = -1;
            SparseIntArray points = new SparseIntArray();
            long startTime;

            public void onParsingStarted(long j, long j2) {
                this.startTime = j;
                long unused = BatteryInfo.this.timePeriod = j2 - j;
                usageView.clearPaths();
                usageView.configureGraph((int) BatteryInfo.this.timePeriod, 100);
            }

            public void onDataPoint(long j, BatteryStats.HistoryItem historyItem) {
                int i = (int) j;
                this.lastTime = i;
                byte b = historyItem.batteryLevel;
                this.lastLevel = b;
                this.points.put(i, b);
            }

            public void onDataGap() {
                if (this.points.size() > 1) {
                    usageView.addPath(this.points);
                }
                this.points.clear();
            }

            public void onParsingDone() {
                onDataGap();
                if (BatteryInfo.this.remainingTimeUs != 0) {
                    PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
                    if (BatteryInfo.this.mCharging || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context)) {
                        int i = this.lastTime;
                        if (i >= 0) {
                            this.points.put(i, this.lastLevel);
                            this.points.put((int) (BatteryInfo.this.timePeriod + PowerUtil.convertUsToMs(BatteryInfo.this.remainingTimeUs)), BatteryInfo.this.mCharging ? 100 : 0);
                        }
                    } else {
                        this.points = powerUsageFeatureProvider.getEnhancedBatteryPredictionCurve(context, this.startTime);
                    }
                }
                SparseIntArray sparseIntArray = this.points;
                if (sparseIntArray != null && sparseIntArray.size() > 0) {
                    SparseIntArray sparseIntArray2 = this.points;
                    usageView.configureGraph(sparseIntArray2.keyAt(sparseIntArray2.size() - 1), 100);
                    usageView.addProjectedPath(this.points);
                }
            }
        };
        BatteryDataParser[] batteryDataParserArr2 = new BatteryDataParser[(batteryDataParserArr.length + 1)];
        for (int i = 0; i < batteryDataParserArr.length; i++) {
            batteryDataParserArr2[i] = batteryDataParserArr[i];
        }
        batteryDataParserArr2[batteryDataParserArr.length] = r1;
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, true);
        batteryStatsHelper.create((Bundle) null);
        parseBatteryHistory(batteryStatsHelper.getStats(), batteryDataParserArr2);
        String string = context.getString(R$string.charge_length_format, new Object[]{Formatter.formatShortElapsedTime(context, this.timePeriod)});
        long j = this.remainingTimeUs;
        if (j != 0) {
            str = context.getString(R$string.remaining_length_format, new Object[]{Formatter.formatShortElapsedTime(context, j / 1000)});
        } else {
            str = "";
        }
        usageView.setBottomLabels(new CharSequence[]{string, str});
    }

    public static void getBatteryInfo(Context context, Callback callback, boolean z) {
        getBatteryInfo(context, callback, (BatteryUsageStats) null, z);
    }

    public static void getBatteryInfo(final Context context, final Callback callback, final BatteryUsageStats batteryUsageStats, final boolean z) {
        new AsyncTask<Void, Void, BatteryInfo>() {
            /* access modifiers changed from: protected */
            public BatteryInfo doInBackground(Void... voidArr) {
                BatteryUsageStats batteryUsageStats = batteryUsageStats;
                if (batteryUsageStats == null) {
                    batteryUsageStats = ((BatteryStatsManager) context.getSystemService(BatteryStatsManager.class)).getBatteryUsageStats();
                }
                return BatteryInfo.getBatteryInfo(context, batteryUsageStats, z);
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(BatteryInfo batteryInfo) {
                long currentTimeMillis = System.currentTimeMillis();
                callback.onBatteryInfoLoaded(batteryInfo);
                BatteryUtils.logRuntime("BatteryInfo", "time for callback", currentTimeMillis);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static BatteryInfo getBatteryInfo(Context context, BatteryUsageStats batteryUsageStats, boolean z) {
        Estimate enhancedBatteryPrediction;
        Context context2 = context;
        BatteryUtils.logRuntime("BatteryInfo", "time for getStats", System.currentTimeMillis());
        long currentTimeMillis = System.currentTimeMillis();
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context2);
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        Intent registerReceiver = context2.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        boolean z2 = registerReceiver.getIntExtra("plugged", -1) == 0;
        if (!z2 || powerUsageFeatureProvider == null || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context2) || (enhancedBatteryPrediction = powerUsageFeatureProvider.getEnhancedBatteryPrediction(context2)) == null) {
            Estimate estimate = new Estimate(z2 ? batteryUsageStats.getBatteryTimeRemainingMs() : 0, false, -1);
            BatteryUtils.logRuntime("BatteryInfo", "time for regular BatteryInfo", currentTimeMillis);
            return getBatteryInfo(context, registerReceiver, batteryUsageStats, estimate, convertMsToUs, z);
        }
        Estimate.storeCachedEstimate(context2, enhancedBatteryPrediction);
        BatteryUtils.logRuntime("BatteryInfo", "time for enhanced BatteryInfo", currentTimeMillis);
        return getBatteryInfo(context, registerReceiver, batteryUsageStats, enhancedBatteryPrediction, convertMsToUs, z);
    }

    public static BatteryInfo getBatteryInfo(Context context, Intent intent, BatteryUsageStats batteryUsageStats, Estimate estimate, long j, boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.mBatteryUsageStats = batteryUsageStats;
        int batteryLevel2 = Utils.getBatteryLevel(intent);
        batteryInfo.batteryLevel = batteryLevel2;
        batteryInfo.batteryPercentString = Utils.formatPercentage(batteryLevel2);
        boolean z2 = false;
        batteryInfo.mCharging = intent.getIntExtra("plugged", 0) != 0;
        batteryInfo.averageTimeToDischarge = estimate.getAverageDischargeTime();
        if (intent.getIntExtra("health", 1) == 3) {
            z2 = true;
        }
        batteryInfo.isOverheated = z2;
        batteryInfo.statusLabel = Utils.getBatteryStatus(context, intent);
        batteryInfo.batteryStatus = intent.getIntExtra("status", 1);
        if (!batteryInfo.mCharging) {
            updateBatteryInfoDischarging(context, z, estimate, batteryInfo);
        } else {
            updateBatteryInfoCharging(context, intent, batteryUsageStats, batteryInfo);
        }
        BatteryUtils.logRuntime("BatteryInfo", "time for getBatteryInfo", currentTimeMillis);
        return batteryInfo;
    }

    private static void updateBatteryInfoCharging(Context context, Intent intent, BatteryUsageStats batteryUsageStats, BatteryInfo batteryInfo) {
        String str;
        Resources resources = context.getResources();
        long chargeTimeRemainingMs = batteryUsageStats.getChargeTimeRemainingMs();
        int intExtra = intent.getIntExtra("status", 1);
        batteryInfo.discharging = false;
        batteryInfo.suggestionLabel = null;
        if (batteryInfo.isOverheated && intExtra != 5) {
            batteryInfo.remainingLabel = null;
            batteryInfo.chargeLabel = context.getString(R$string.power_charging_limited, new Object[]{batteryInfo.batteryPercentString});
        } else if (chargeTimeRemainingMs <= 0 || intExtra == 5) {
            String batteryStatus2 = Utils.getBatteryStatus(context, intent);
            batteryInfo.remainingLabel = null;
            if (batteryInfo.batteryLevel == 100) {
                str = batteryInfo.batteryPercentString;
            } else {
                str = resources.getString(R$string.power_charging, new Object[]{batteryInfo.batteryPercentString, com.android.settings.Utils.toLowerCaseIfAllowed(context, batteryStatus2)});
            }
            batteryInfo.chargeLabel = str;
        } else {
            long convertMsToUs = PowerUtil.convertMsToUs(chargeTimeRemainingMs);
            batteryInfo.remainingTimeUs = convertMsToUs;
            CharSequence formatElapsedTime = StringUtil.formatElapsedTime(context, (double) PowerUtil.convertUsToMs(convertMsToUs), false, true);
            int i = R$string.power_charging_duration;
            batteryInfo.remainingLabel = context.getString(R$string.power_remaining_charging_duration_only, new Object[]{formatElapsedTime});
            batteryInfo.chargeLabel = context.getString(i, new Object[]{batteryInfo.batteryPercentString, formatElapsedTime});
        }
    }

    private static void updateBatteryInfoDischarging(Context context, boolean z, Estimate estimate, BatteryInfo batteryInfo) {
        long convertMsToUs = PowerUtil.convertMsToUs(estimate.getEstimateMillis());
        if (convertMsToUs > 0) {
            batteryInfo.remainingTimeUs = convertMsToUs;
            boolean z2 = false;
            batteryInfo.remainingLabel = PowerUtil.getBatteryRemainingStringFormatted(context, PowerUtil.convertUsToMs(convertMsToUs), (String) null, false);
            long convertUsToMs = PowerUtil.convertUsToMs(convertMsToUs);
            String str = batteryInfo.batteryPercentString;
            if (estimate.isBasedOnUsage() && !z) {
                z2 = true;
            }
            batteryInfo.chargeLabel = PowerUtil.getBatteryRemainingStringFormatted(context, convertUsToMs, str, z2);
            batteryInfo.suggestionLabel = PowerUtil.getBatteryTipStringFormatted(context, PowerUtil.convertUsToMs(convertMsToUs));
            return;
        }
        batteryInfo.remainingLabel = null;
        batteryInfo.suggestionLabel = null;
        batteryInfo.chargeLabel = batteryInfo.batteryPercentString;
    }

    public void parseBatteryHistory(BatteryStats batteryStats, BatteryDataParser... batteryDataParserArr) {
        long j;
        long j2;
        long j3;
        long j4;
        long j5;
        int i;
        char c;
        long j6;
        BatteryStats batteryStats2 = batteryStats;
        BatteryDataParser[] batteryDataParserArr2 = batteryDataParserArr;
        long j7 = 0;
        if (batteryStats.startIteratingHistoryLocked()) {
            BatteryStats.HistoryItem historyItem = new BatteryStats.HistoryItem();
            j5 = 0;
            j4 = 0;
            j3 = 0;
            j2 = 0;
            j = 0;
            boolean z = true;
            i = 0;
            int i2 = 0;
            while (batteryStats2.getNextHistoryLocked(historyItem)) {
                i2++;
                int i3 = i;
                if (z) {
                    j = historyItem.time;
                    z = false;
                }
                byte b = historyItem.cmd;
                if (b == 5 || b == 7) {
                    long j8 = historyItem.currentTime;
                    if (j8 > j3 + 15552000000L || historyItem.time < j + 300000) {
                        j5 = 0;
                    }
                    long j9 = historyItem.time;
                    if (j5 == 0) {
                        j5 = j8 - (j9 - j);
                    }
                    j2 = j9;
                    j3 = j8;
                }
                if (historyItem.isDeltaData()) {
                    j4 = historyItem.time;
                    i = i2;
                } else {
                    i = i3;
                }
            }
            int i4 = i;
        } else {
            j5 = 0;
            j4 = 0;
            j3 = 0;
            j2 = 0;
            j = 0;
            i = 0;
        }
        batteryStats.finishIteratingHistoryLocked();
        long j10 = (j3 + j4) - j2;
        for (BatteryDataParser onParsingStarted : batteryDataParserArr2) {
            onParsingStarted.onParsingStarted(j5, j10);
        }
        if (j10 > j5 && batteryStats.startIteratingHistoryLocked()) {
            BatteryStats.HistoryItem historyItem2 = new BatteryStats.HistoryItem();
            long j11 = 0;
            int i5 = 0;
            while (batteryStats2.getNextHistoryLocked(historyItem2) && i5 < i) {
                if (historyItem2.isDeltaData()) {
                    long j12 = historyItem2.time;
                    j11 += j12 - j2;
                    long j13 = j11 - j5;
                    long j14 = j13 < j7 ? j7 : j13;
                    for (BatteryDataParser onDataPoint : batteryDataParserArr2) {
                        onDataPoint.onDataPoint(j14, historyItem2);
                    }
                    j2 = j12;
                    c = 7;
                } else {
                    byte b2 = historyItem2.cmd;
                    c = 7;
                    if (b2 == 5 || b2 == 7) {
                        j6 = historyItem2.currentTime;
                        if (j6 < j5) {
                            j6 = (historyItem2.time - j) + j5;
                        }
                        j2 = historyItem2.time;
                    } else {
                        j6 = j11;
                    }
                    if (b2 != 6 && (b2 != 5 || Math.abs(j11 - j6) > 3600000)) {
                        for (BatteryDataParser onDataGap : batteryDataParserArr2) {
                            onDataGap.onDataGap();
                        }
                    }
                    j11 = j6;
                }
                i5++;
                char c2 = c;
                j7 = 0;
            }
        }
        batteryStats.finishIteratingHistoryLocked();
        for (BatteryDataParser onParsingDone : batteryDataParserArr2) {
            onParsingDone.onParsingDone();
        }
    }
}
