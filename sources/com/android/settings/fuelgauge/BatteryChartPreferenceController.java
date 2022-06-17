package com.android.settings.fuelgauge;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.fuelgauge.BatteryChartView;
import com.android.settings.fuelgauge.ExpandDividerPreference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatteryChartPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnCreate, OnDestroy, OnSaveInstanceState, BatteryChartView.OnSelectListener, OnResume, ExpandDividerPreference.OnExpandListener {
    private static int sUiMode;
    private final SettingsActivity mActivity;
    PreferenceGroup mAppListPrefGroup;
    BatteryChartView mBatteryChartView;
    long[] mBatteryHistoryKeys;
    int[] mBatteryHistoryLevels;
    Map<Integer, List<BatteryDiffEntry>> mBatteryIndexedMap;
    BatteryUtils mBatteryUtils;
    ExpandDividerPreference mExpandDividerPreference;
    private FooterPreference mFooterPreference;
    private final InstrumentedPreferenceFragment mFragment;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIs24HourFormat = false;
    boolean mIsExpanded = false;
    private boolean mIsFooterPrefAdded = false;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final CharSequence[] mNotAllowShowEntryPackages;
    private final CharSequence[] mNotAllowShowSummaryPackages;
    Context mPrefContext;
    final Map<String, Preference> mPreferenceCache = new HashMap();
    private final String mPreferenceKey;
    private PreferenceScreen mPreferenceScreen;
    final List<BatteryDiffEntry> mSystemEntries = new ArrayList();
    int mTrapezoidIndex = -2;

    public boolean isAvailable() {
        return true;
    }

    public BatteryChartPreferenceController(Context context, String str, Lifecycle lifecycle, SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        super(context);
        this.mActivity = settingsActivity;
        this.mFragment = instrumentedPreferenceFragment;
        this.mPreferenceKey = str;
        this.mIs24HourFormat = DateFormat.is24HourFormat(context);
        this.mNotAllowShowSummaryPackages = context.getResources().getTextArray(C1978R$array.allowlist_hide_summary_in_battery_usage);
        this.mNotAllowShowEntryPackages = context.getResources().getTextArray(C1978R$array.allowlist_hide_entry_in_battery_usage);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            this.mTrapezoidIndex = bundle.getInt("current_time_slot", this.mTrapezoidIndex);
            this.mIsExpanded = bundle.getBoolean("expand_system_info", this.mIsExpanded);
            Log.d("BatteryChartPreferenceController", String.format("onCreate() slotIndex=%d isExpanded=%b", new Object[]{Integer.valueOf(this.mTrapezoidIndex), Boolean.valueOf(this.mIsExpanded)}));
        }
    }

    public void onResume() {
        int i = this.mContext.getResources().getConfiguration().uiMode & 48;
        if (sUiMode != i) {
            sUiMode = i;
            BatteryDiffEntry.clearCache();
            Log.d("BatteryChartPreferenceController", "clear icon and label cache since uiMode is changed");
        }
        this.mIs24HourFormat = DateFormat.is24HourFormat(this.mContext);
        this.mMetricsFeatureProvider.action(this.mPrefContext, 1880, (Pair<Integer, Object>[]) new Pair[0]);
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.putInt("current_time_slot", this.mTrapezoidIndex);
            bundle.putBoolean("expand_system_info", this.mIsExpanded);
            Log.d("BatteryChartPreferenceController", String.format("onSaveInstanceState() slotIndex=%d isExpanded=%b", new Object[]{Integer.valueOf(this.mTrapezoidIndex), Boolean.valueOf(this.mIsExpanded)}));
        }
    }

    public void onDestroy() {
        if (this.mActivity.isChangingConfigurations()) {
            BatteryDiffEntry.clearCache();
        }
        this.mHandler.removeCallbacksAndMessages((Object) null);
        this.mPreferenceCache.clear();
        PreferenceGroup preferenceGroup = this.mAppListPrefGroup;
        if (preferenceGroup != null) {
            preferenceGroup.removeAll();
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPrefContext = preferenceScreen.getContext();
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mAppListPrefGroup = preferenceGroup;
        preferenceGroup.setOrderingAsAdded(false);
        this.mAppListPrefGroup.setTitle((CharSequence) this.mPrefContext.getString(C1992R$string.battery_app_usage_for_past_24));
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference("battery_graph_footer");
        this.mFooterPreference = footerPreference;
        if (footerPreference != null) {
            preferenceScreen.removePreference(footerPreference);
        }
    }

    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!(preference instanceof PowerGaugePreference)) {
            return false;
        }
        PowerGaugePreference powerGaugePreference = (PowerGaugePreference) preference;
        BatteryDiffEntry batteryDiffEntry = powerGaugePreference.getBatteryDiffEntry();
        BatteryHistEntry batteryHistEntry = batteryDiffEntry.mBatteryHistEntry;
        String str = batteryHistEntry.mPackageName;
        boolean isAppEntry = batteryHistEntry.isAppEntry();
        this.mMetricsFeatureProvider.action(this.mPrefContext, isAppEntry ? 1768 : 1769, (Pair<Integer, Object>[]) new Pair[]{new Pair(1, str), new Pair(2, Integer.valueOf(batteryHistEntry.mBatteryLevel)), new Pair(3, powerGaugePreference.getPercent())});
        Log.d("BatteryChartPreferenceController", String.format("handleClick() label=%s key=%s package=%s", new Object[]{batteryDiffEntry.getAppLabel(), batteryHistEntry.getKey(), batteryHistEntry.mPackageName}));
        AdvancedPowerUsageDetail.startBatteryDetailPage(this.mActivity, this.mFragment, batteryDiffEntry, powerGaugePreference.getPercent(), isValidToShowSummary(str), getSlotInformation());
        return true;
    }

    public void onSelect(int i) {
        Log.d("BatteryChartPreferenceController", "onChartSelect:" + i);
        refreshUi(i, false);
        this.mMetricsFeatureProvider.action(this.mPrefContext, i == -1 ? 1767 : 1766, (Pair<Integer, Object>[]) new Pair[0]);
    }

    public void onExpand(boolean z) {
        this.mIsExpanded = z;
        this.mMetricsFeatureProvider.action(this.mPrefContext, 1770, z);
        refreshExpandUi();
    }

    /* access modifiers changed from: package-private */
    public void setBatteryHistoryMap(Map<Long, Map<String, BatteryHistEntry>> map) {
        if (map == null || map.isEmpty()) {
            this.mBatteryIndexedMap = null;
            this.mBatteryHistoryKeys = null;
            this.mBatteryHistoryLevels = null;
            addFooterPreferenceIfNeeded(false);
            return;
        }
        this.mBatteryHistoryKeys = getBatteryHistoryKeys(map);
        this.mBatteryHistoryLevels = new int[13];
        for (int i = 0; i < 13; i++) {
            long j = this.mBatteryHistoryKeys[i * 2];
            Map map2 = map.get(Long.valueOf(j));
            if (map2 == null || map2.isEmpty()) {
                Log.e("BatteryChartPreferenceController", "abnormal entry list in the timestamp:" + ConvertUtils.utcToLocalTime(this.mPrefContext, j));
            } else {
                float f = 0.0f;
                for (BatteryHistEntry batteryHistEntry : map2.values()) {
                    f += (float) batteryHistEntry.mBatteryLevel;
                }
                this.mBatteryHistoryLevels[i] = Math.round(f / ((float) map2.size()));
            }
        }
        forceRefreshUi();
        Context context = this.mPrefContext;
        long[] jArr = this.mBatteryHistoryKeys;
        Log.d("BatteryChartPreferenceController", String.format("setBatteryHistoryMap() size=%d key=%s\nlevels=%s", new Object[]{Integer.valueOf(map.size()), ConvertUtils.utcToLocalTime(context, jArr[jArr.length - 1]), Arrays.toString(this.mBatteryHistoryLevels)}));
        new LoadAllItemsInfoTask(map).execute(new Void[0]);
    }

    /* access modifiers changed from: package-private */
    public void setBatteryChartView(BatteryChartView batteryChartView) {
        if (this.mBatteryChartView != batteryChartView) {
            this.mHandler.post(new BatteryChartPreferenceController$$ExternalSyntheticLambda2(this, batteryChartView));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: setBatteryChartViewInner */
    public void lambda$setBatteryChartView$0(BatteryChartView batteryChartView) {
        this.mBatteryChartView = batteryChartView;
        batteryChartView.setOnSelectListener(this);
        forceRefreshUi();
    }

    /* access modifiers changed from: private */
    public void forceRefreshUi() {
        int i = this.mTrapezoidIndex;
        if (i == -2) {
            i = -1;
        }
        BatteryChartView batteryChartView = this.mBatteryChartView;
        if (batteryChartView != null) {
            batteryChartView.setLevels(this.mBatteryHistoryLevels);
            this.mBatteryChartView.setSelectedIndex(i);
            setTimestampLabel();
        }
        refreshUi(i, true);
    }

    /* access modifiers changed from: package-private */
    public boolean refreshUi(int i, boolean z) {
        if (this.mBatteryIndexedMap == null || this.mBatteryChartView == null || (this.mTrapezoidIndex == i && !z)) {
            return false;
        }
        Log.d("BatteryChartPreferenceController", String.format("refreshUi: index=%d size=%d isForce:%b", new Object[]{Integer.valueOf(i), Integer.valueOf(this.mBatteryIndexedMap.size()), Boolean.valueOf(z)}));
        this.mTrapezoidIndex = i;
        this.mHandler.post(new BatteryChartPreferenceController$$ExternalSyntheticLambda1(this));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshUi$1() {
        long currentTimeMillis = System.currentTimeMillis();
        removeAndCacheAllPrefs();
        addAllPreferences();
        refreshCategoryTitle();
        Log.d("BatteryChartPreferenceController", String.format("refreshUi is finished in %d/ms", new Object[]{Long.valueOf(System.currentTimeMillis() - currentTimeMillis)}));
    }

    private void addAllPreferences() {
        List list = this.mBatteryIndexedMap.get(Integer.valueOf(this.mTrapezoidIndex));
        addFooterPreferenceIfNeeded(!list.isEmpty());
        ArrayList arrayList = new ArrayList();
        this.mSystemEntries.clear();
        list.forEach(new BatteryChartPreferenceController$$ExternalSyntheticLambda3(this, arrayList));
        Comparator<BatteryDiffEntry> comparator = BatteryDiffEntry.COMPARATOR;
        Collections.sort(arrayList, comparator);
        Collections.sort(this.mSystemEntries, comparator);
        Log.d("BatteryChartPreferenceController", String.format("addAllPreferences() app=%d system=%d", new Object[]{Integer.valueOf(arrayList.size()), Integer.valueOf(this.mSystemEntries.size())}));
        if (!arrayList.isEmpty()) {
            addPreferenceToScreen(arrayList);
        }
        if (!this.mSystemEntries.isEmpty()) {
            if (this.mExpandDividerPreference == null) {
                ExpandDividerPreference expandDividerPreference = new ExpandDividerPreference(this.mPrefContext);
                this.mExpandDividerPreference = expandDividerPreference;
                expandDividerPreference.setOnExpandListener(this);
                this.mExpandDividerPreference.setIsExpanded(this.mIsExpanded);
            }
            this.mExpandDividerPreference.setOrder(this.mAppListPrefGroup.getPreferenceCount());
            this.mAppListPrefGroup.addPreference(this.mExpandDividerPreference);
        }
        refreshExpandUi();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addAllPreferences$2(List list, BatteryDiffEntry batteryDiffEntry) {
        String packageName = batteryDiffEntry.getPackageName();
        if (!isValidToShowEntry(packageName)) {
            Log.w("BatteryChartPreferenceController", "ignore showing item:" + packageName);
            return;
        }
        if (batteryDiffEntry.isSystemEntry()) {
            this.mSystemEntries.add(batteryDiffEntry);
        } else {
            list.add(batteryDiffEntry);
        }
        if (this.mTrapezoidIndex >= 0) {
            validateUsageTime(batteryDiffEntry);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: com.android.settings.fuelgauge.PowerGaugePreference} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addPreferenceToScreen(java.util.List<com.android.settings.fuelgauge.BatteryDiffEntry> r11) {
        /*
            r10 = this;
            androidx.preference.PreferenceGroup r0 = r10.mAppListPrefGroup
            if (r0 == 0) goto L_0x00c0
            boolean r0 = r11.isEmpty()
            if (r0 == 0) goto L_0x000c
            goto L_0x00c0
        L_0x000c:
            androidx.preference.PreferenceGroup r0 = r10.mAppListPrefGroup
            int r0 = r0.getPreferenceCount()
            java.util.Iterator r11 = r11.iterator()
        L_0x0016:
            boolean r1 = r11.hasNext()
            if (r1 == 0) goto L_0x00c0
            java.lang.Object r1 = r11.next()
            com.android.settings.fuelgauge.BatteryDiffEntry r1 = (com.android.settings.fuelgauge.BatteryDiffEntry) r1
            r2 = 0
            java.lang.String r3 = r1.getAppLabel()
            android.graphics.drawable.Drawable r4 = r1.getAppIcon()
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            java.lang.String r6 = "BatteryChartPreferenceController"
            if (r5 != 0) goto L_0x00a6
            if (r4 != 0) goto L_0x0036
            goto L_0x00a6
        L_0x0036:
            com.android.settings.fuelgauge.BatteryHistEntry r5 = r1.mBatteryHistEntry
            java.lang.String r5 = r5.getKey()
            androidx.preference.PreferenceGroup r7 = r10.mAppListPrefGroup
            androidx.preference.Preference r7 = r7.findPreference(r5)
            com.android.settings.fuelgauge.PowerGaugePreference r7 = (com.android.settings.fuelgauge.PowerGaugePreference) r7
            r8 = 1
            if (r7 == 0) goto L_0x0061
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r9 = "preference should be removed for:"
            r2.append(r9)
            java.lang.String r9 = r1.getPackageName()
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            android.util.Log.w(r6, r2)
            r2 = r8
            goto L_0x006a
        L_0x0061:
            java.util.Map<java.lang.String, androidx.preference.Preference> r6 = r10.mPreferenceCache
            java.lang.Object r6 = r6.get(r5)
            r7 = r6
            com.android.settings.fuelgauge.PowerGaugePreference r7 = (com.android.settings.fuelgauge.PowerGaugePreference) r7
        L_0x006a:
            if (r7 != 0) goto L_0x007b
            com.android.settings.fuelgauge.PowerGaugePreference r7 = new com.android.settings.fuelgauge.PowerGaugePreference
            android.content.Context r6 = r10.mPrefContext
            r7.<init>(r6)
            r7.setKey(r5)
            java.util.Map<java.lang.String, androidx.preference.Preference> r6 = r10.mPreferenceCache
            r6.put(r5, r7)
        L_0x007b:
            r7.setIcon((android.graphics.drawable.Drawable) r4)
            r7.setTitle((java.lang.CharSequence) r3)
            r7.setOrder(r0)
            double r3 = r1.getPercentOfTotal()
            r7.setPercent(r3)
            r7.setSingleLineTitle(r8)
            r7.setBatteryDiffEntry(r1)
            boolean r3 = r1.validForRestriction()
            r7.setEnabled(r3)
            r10.setPreferenceSummary(r7, r1)
            if (r2 != 0) goto L_0x00a2
            androidx.preference.PreferenceGroup r1 = r10.mAppListPrefGroup
            r1.addPreference(r7)
        L_0x00a2:
            int r0 = r0 + 1
            goto L_0x0016
        L_0x00a6:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cannot find app resource for:"
            r2.append(r3)
            java.lang.String r1 = r1.getPackageName()
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            android.util.Log.w(r6, r1)
            goto L_0x0016
        L_0x00c0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.BatteryChartPreferenceController.addPreferenceToScreen(java.util.List):void");
    }

    private void removeAndCacheAllPrefs() {
        PreferenceGroup preferenceGroup = this.mAppListPrefGroup;
        if (preferenceGroup != null && preferenceGroup.getPreferenceCount() != 0) {
            int preferenceCount = this.mAppListPrefGroup.getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                Preference preference = this.mAppListPrefGroup.getPreference(i);
                if (!TextUtils.isEmpty(preference.getKey())) {
                    this.mPreferenceCache.put(preference.getKey(), preference);
                }
            }
            this.mAppListPrefGroup.removeAll();
        }
    }

    private void refreshExpandUi() {
        if (this.mIsExpanded) {
            addPreferenceToScreen(this.mSystemEntries);
            return;
        }
        for (BatteryDiffEntry batteryDiffEntry : this.mSystemEntries) {
            Preference findPreference = this.mAppListPrefGroup.findPreference(batteryDiffEntry.mBatteryHistEntry.getKey());
            if (findPreference != null) {
                this.mAppListPrefGroup.removePreference(findPreference);
                this.mPreferenceCache.put(findPreference.getKey(), findPreference);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void refreshCategoryTitle() {
        String slotInformation = getSlotInformation();
        Log.d("BatteryChartPreferenceController", String.format("refreshCategoryTitle:%s", new Object[]{slotInformation}));
        PreferenceGroup preferenceGroup = this.mAppListPrefGroup;
        if (preferenceGroup != null) {
            preferenceGroup.setTitle((CharSequence) getSlotInformation(true, slotInformation));
        }
        ExpandDividerPreference expandDividerPreference = this.mExpandDividerPreference;
        if (expandDividerPreference != null) {
            expandDividerPreference.setTitle(getSlotInformation(false, slotInformation));
        }
    }

    private String getSlotInformation(boolean z, String str) {
        if (str == null) {
            if (z) {
                return this.mPrefContext.getString(C1992R$string.battery_app_usage_for_past_24);
            }
            return this.mPrefContext.getString(C1992R$string.battery_system_usage_for_past_24);
        } else if (z) {
            return this.mPrefContext.getString(C1992R$string.battery_app_usage_for, new Object[]{str});
        } else {
            return this.mPrefContext.getString(C1992R$string.battery_system_usage_for, new Object[]{str});
        }
    }

    private String getSlotInformation() {
        int i = this.mTrapezoidIndex;
        if (i < 0) {
            return null;
        }
        return String.format("%s - %s", new Object[]{ConvertUtils.utcToLocalTimeHour(this.mPrefContext, this.mBatteryHistoryKeys[i * 2], this.mIs24HourFormat), ConvertUtils.utcToLocalTimeHour(this.mPrefContext, this.mBatteryHistoryKeys[(this.mTrapezoidIndex + 1) * 2], this.mIs24HourFormat)});
    }

    /* access modifiers changed from: package-private */
    public void setPreferenceSummary(PowerGaugePreference powerGaugePreference, BatteryDiffEntry batteryDiffEntry) {
        long j = batteryDiffEntry.mForegroundUsageTimeInMs;
        long j2 = batteryDiffEntry.mBackgroundUsageTimeInMs;
        long j3 = j + j2;
        String str = null;
        if (!isValidToShowSummary(batteryDiffEntry.getPackageName())) {
            powerGaugePreference.setSummary((CharSequence) null);
            return;
        }
        if (j3 == 0) {
            powerGaugePreference.setSummary((CharSequence) null);
        } else if (j == 0 && j2 != 0) {
            str = buildUsageTimeInfo(j2, true);
        } else if (j3 < 60000) {
            str = buildUsageTimeInfo(j3, false);
        } else {
            str = buildUsageTimeInfo(j3, false);
            if (j2 > 0) {
                str = str + "\n" + buildUsageTimeInfo(j2, true);
            }
        }
        powerGaugePreference.setSummary((CharSequence) str);
    }

    private String buildUsageTimeInfo(long j, boolean z) {
        int i;
        int i2;
        if (j < 60000) {
            Context context = this.mPrefContext;
            if (z) {
                i2 = C1992R$string.battery_usage_background_less_than_one_minute;
            } else {
                i2 = C1992R$string.battery_usage_total_less_than_one_minute;
            }
            return context.getString(i2);
        }
        CharSequence formatElapsedTime = StringUtil.formatElapsedTime(this.mPrefContext, (double) j, false, false);
        if (z) {
            i = C1992R$string.battery_usage_for_background_time;
        } else {
            i = C1992R$string.battery_usage_for_total_time;
        }
        return this.mPrefContext.getString(i, new Object[]{formatElapsedTime});
    }

    /* access modifiers changed from: package-private */
    public boolean isValidToShowSummary(String str) {
        return !contains(str, this.mNotAllowShowSummaryPackages);
    }

    /* access modifiers changed from: package-private */
    public boolean isValidToShowEntry(String str) {
        return !contains(str, this.mNotAllowShowEntryPackages);
    }

    /* access modifiers changed from: package-private */
    public void setTimestampLabel() {
        long[] jArr;
        BatteryChartView batteryChartView = this.mBatteryChartView;
        if (batteryChartView != null && (jArr = this.mBatteryHistoryKeys) != null) {
            batteryChartView.setLatestTimestamp(jArr[jArr.length - 1]);
        }
    }

    private void addFooterPreferenceIfNeeded(boolean z) {
        FooterPreference footerPreference;
        int i;
        if (!this.mIsFooterPrefAdded && (footerPreference = this.mFooterPreference) != null) {
            this.mIsFooterPrefAdded = true;
            Context context = this.mPrefContext;
            if (z) {
                i = C1992R$string.battery_usage_screen_footer;
            } else {
                i = C1992R$string.battery_usage_screen_footer_empty;
            }
            footerPreference.setTitle((CharSequence) context.getString(i));
            this.mHandler.post(new BatteryChartPreferenceController$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addFooterPreferenceIfNeeded$3() {
        this.mPreferenceScreen.addPreference(this.mFooterPreference);
    }

    private static boolean contains(String str, CharSequence[] charSequenceArr) {
        if (!(str == null || charSequenceArr == null)) {
            for (CharSequence equals : charSequenceArr) {
                if (TextUtils.equals(str, equals)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean validateUsageTime(BatteryDiffEntry batteryDiffEntry) {
        long j = batteryDiffEntry.mForegroundUsageTimeInMs;
        long j2 = batteryDiffEntry.mBackgroundUsageTimeInMs;
        long j3 = j + j2;
        if (j <= 7200000 && j2 <= 7200000 && j3 <= 7200000) {
            return true;
        }
        Log.e("BatteryChartPreferenceController", "validateUsageTime() fail for\n" + batteryDiffEntry);
        return false;
    }

    public static List<BatteryDiffEntry> getBatteryLast24HrUsageData(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        Map<Long, Map<String, BatteryHistEntry>> batteryHistory = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).getBatteryHistory(context);
        if (batteryHistory == null || batteryHistory.isEmpty()) {
            return null;
        }
        Log.d("BatteryChartPreferenceController", String.format("getBatteryLast24HrData() size=%d time=&d/ms", new Object[]{Integer.valueOf(batteryHistory.size()), Long.valueOf(System.currentTimeMillis() - currentTimeMillis)}));
        return ConvertUtils.getIndexedUsageMap(context, 12, getBatteryHistoryKeys(batteryHistory), batteryHistory, true).get(-1);
    }

    private static long[] getBatteryHistoryKeys(Map<Long, Map<String, BatteryHistEntry>> map) {
        ArrayList arrayList = new ArrayList(map.keySet());
        Collections.sort(arrayList);
        long[] jArr = new long[25];
        for (int i = 0; i < 25; i++) {
            jArr[i] = ((Long) arrayList.get(i)).longValue();
        }
        return jArr;
    }

    private final class LoadAllItemsInfoTask extends AsyncTask<Void, Void, Map<Integer, List<BatteryDiffEntry>>> {
        private long[] mBatteryHistoryKeysCache;
        private Map<Long, Map<String, BatteryHistEntry>> mBatteryHistoryMap;

        private LoadAllItemsInfoTask(Map<Long, Map<String, BatteryHistEntry>> map) {
            this.mBatteryHistoryMap = map;
            this.mBatteryHistoryKeysCache = BatteryChartPreferenceController.this.mBatteryHistoryKeys;
        }

        /* access modifiers changed from: protected */
        public Map<Integer, List<BatteryDiffEntry>> doInBackground(Void... voidArr) {
            if (BatteryChartPreferenceController.this.mPrefContext == null || this.mBatteryHistoryKeysCache == null) {
                return null;
            }
            long currentTimeMillis = System.currentTimeMillis();
            Map<Integer, List<BatteryDiffEntry>> indexedUsageMap = ConvertUtils.getIndexedUsageMap(BatteryChartPreferenceController.this.mPrefContext, 12, this.mBatteryHistoryKeysCache, this.mBatteryHistoryMap, true);
            for (List<BatteryDiffEntry> forEach : indexedUsageMap.values()) {
                forEach.forEach(C0944xd2dd8c4.INSTANCE);
            }
            Log.d("BatteryChartPreferenceController", String.format("execute LoadAllItemsInfoTask in %d/ms", new Object[]{Long.valueOf(System.currentTimeMillis() - currentTimeMillis)}));
            return indexedUsageMap;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Map<Integer, List<BatteryDiffEntry>> map) {
            this.mBatteryHistoryMap = null;
            this.mBatteryHistoryKeysCache = null;
            if (map != null) {
                BatteryChartPreferenceController.this.mHandler.post(new C0943xd2dd8c3(this, map));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$1(Map map) {
            BatteryChartPreferenceController batteryChartPreferenceController = BatteryChartPreferenceController.this;
            batteryChartPreferenceController.mBatteryIndexedMap = map;
            batteryChartPreferenceController.forceRefreshUi();
        }
    }
}
