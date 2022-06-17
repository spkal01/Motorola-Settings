package com.android.settings.fuelgauge;

import android.content.ContentValues;
import android.content.Context;
import android.os.LocaleList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class ConvertUtils {
    private static final BatteryHistEntry EMPTY_BATTERY_HIST_ENTRY = new BatteryHistEntry(new ContentValues());
    private static final Map<String, BatteryHistEntry> EMPTY_BATTERY_MAP = new HashMap();
    static double PERCENTAGE_OF_TOTAL_THRESHOLD = 1.0d;
    private static boolean sIs24HourFormat;
    static Locale sLocale;
    static Locale sLocaleForHour;
    static SimpleDateFormat sSimpleDateFormat;
    static SimpleDateFormat sSimpleDateFormatForHour;
    static String sZoneId;
    static String sZoneIdForHour;

    private static double getDiffValue(double d, double d2, double d3) {
        double d4 = 0.0d;
        double d5 = d2 > d ? d2 - d : 0.0d;
        if (d3 > d2) {
            d4 = d3 - d2;
        }
        return d5 + d4;
    }

    private static long getDiffValue(long j, long j2, long j3) {
        long j4 = 0;
        long j5 = j2 > j ? j2 - j : 0;
        if (j3 > j2) {
            j4 = j3 - j2;
        }
        return j5 + j4;
    }

    public static String utcToLocalTime(Context context, long j) {
        Locale locale = getLocale(context);
        String id = TimeZone.getDefault().getID();
        if (!id.equals(sZoneId) || !locale.equals(sLocale) || sSimpleDateFormat == null) {
            sLocale = locale;
            sZoneId = id;
            sSimpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss", locale);
        }
        return sSimpleDateFormat.format(new Date(j));
    }

    public static String utcToLocalTimeHour(Context context, long j, boolean z) {
        Locale locale = getLocale(context);
        String id = TimeZone.getDefault().getID();
        if (!id.equals(sZoneIdForHour) || !locale.equals(sLocaleForHour) || sIs24HourFormat != z || sSimpleDateFormatForHour == null) {
            sLocaleForHour = locale;
            sZoneIdForHour = id;
            sIs24HourFormat = z;
            sSimpleDateFormatForHour = new SimpleDateFormat(sIs24HourFormat ? "HH" : "h", locale);
        }
        return sSimpleDateFormatForHour.format(new Date(j)).toLowerCase(locale);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00ee, code lost:
        if (r5 == 0.0d) goto L_0x00f9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Map<java.lang.Integer, java.util.List<com.android.settings.fuelgauge.BatteryDiffEntry>> getIndexedUsageMap(android.content.Context r37, int r38, long[] r39, java.util.Map<java.lang.Long, java.util.Map<java.lang.String, com.android.settings.fuelgauge.BatteryHistEntry>> r40, boolean r41) {
        /*
            r0 = r40
            if (r0 == 0) goto L_0x0186
            boolean r1 = r40.isEmpty()
            if (r1 == 0) goto L_0x000c
            goto L_0x0186
        L_0x000c:
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2 = 0
        L_0x0012:
            r3 = r38
            if (r2 >= r3) goto L_0x017b
            int r4 = r2 * 2
            r5 = r39[r4]
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            int r6 = r4 + 1
            r6 = r39[r6]
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            int r4 = r4 + 2
            r7 = r39[r4]
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            java.util.Map<java.lang.String, com.android.settings.fuelgauge.BatteryHistEntry> r7 = EMPTY_BATTERY_MAP
            java.lang.Object r5 = r0.getOrDefault(r5, r7)
            java.util.Map r5 = (java.util.Map) r5
            java.lang.Object r6 = r0.getOrDefault(r6, r7)
            java.util.Map r6 = (java.util.Map) r6
            java.lang.Object r4 = r0.getOrDefault(r4, r7)
            java.util.Map r4 = (java.util.Map) r4
            boolean r7 = r5.isEmpty()
            if (r7 != 0) goto L_0x0164
            boolean r7 = r6.isEmpty()
            if (r7 != 0) goto L_0x0164
            boolean r7 = r4.isEmpty()
            if (r7 == 0) goto L_0x0056
            goto L_0x0164
        L_0x0056:
            java.util.HashSet r7 = new java.util.HashSet
            r7.<init>()
            java.util.Set r8 = r5.keySet()
            r7.addAll(r8)
            java.util.Set r8 = r6.keySet()
            r7.addAll(r8)
            java.util.Set r8 = r4.keySet()
            r7.addAll(r8)
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r2)
            r1.put(r9, r8)
            java.util.Iterator r7 = r7.iterator()
            r11 = 0
        L_0x0082:
            boolean r13 = r7.hasNext()
            if (r13 == 0) goto L_0x014a
            java.lang.Object r13 = r7.next()
            java.lang.String r13 = (java.lang.String) r13
            com.android.settings.fuelgauge.BatteryHistEntry r14 = EMPTY_BATTERY_HIST_ENTRY
            java.lang.Object r15 = r5.getOrDefault(r13, r14)
            com.android.settings.fuelgauge.BatteryHistEntry r15 = (com.android.settings.fuelgauge.BatteryHistEntry) r15
            java.lang.Object r16 = r6.getOrDefault(r13, r14)
            r9 = r16
            com.android.settings.fuelgauge.BatteryHistEntry r9 = (com.android.settings.fuelgauge.BatteryHistEntry) r9
            java.lang.Object r10 = r4.getOrDefault(r13, r14)
            com.android.settings.fuelgauge.BatteryHistEntry r10 = (com.android.settings.fuelgauge.BatteryHistEntry) r10
            long r13 = r15.mForegroundUsageTimeInMs
            r16 = r4
            long r3 = r9.mForegroundUsageTimeInMs
            r23 = r5
            r24 = r6
            long r5 = r10.mForegroundUsageTimeInMs
            r17 = r13
            r19 = r3
            r21 = r5
            long r3 = getDiffValue((long) r17, (long) r19, (long) r21)
            long r5 = r15.mBackgroundUsageTimeInMs
            long r13 = r9.mBackgroundUsageTimeInMs
            r25 = r1
            long r0 = r10.mBackgroundUsageTimeInMs
            r17 = r5
            r19 = r13
            r21 = r0
            long r0 = getDiffValue((long) r17, (long) r19, (long) r21)
            double r5 = r15.mConsumePower
            double r13 = r9.mConsumePower
            r27 = r7
            r26 = r8
            double r7 = r10.mConsumePower
            r17 = r5
            r19 = r13
            r21 = r7
            double r5 = getDiffValue((double) r17, (double) r19, (double) r21)
            r7 = 0
            int r13 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x00f1
            int r7 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r7 != 0) goto L_0x00f1
            r7 = 0
            int r13 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x00f3
            goto L_0x00f9
        L_0x00f1:
            r7 = 0
        L_0x00f3:
            com.android.settings.fuelgauge.BatteryHistEntry r36 = selectBatteryHistEntry(r15, r9, r10)
            if (r36 != 0) goto L_0x010b
        L_0x00f9:
            r3 = r38
            r0 = r40
            r4 = r16
            r5 = r23
            r6 = r24
            r1 = r25
            r8 = r26
        L_0x0107:
            r7 = r27
            goto L_0x0082
        L_0x010b:
            long r9 = r3 + r0
            float r9 = (float) r9
            r10 = 1255913984(0x4adbba00, float:7200000.0)
            int r13 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r13 <= 0) goto L_0x0126
            float r10 = r10 / r9
            float r3 = (float) r3
            float r3 = r3 * r10
            int r3 = java.lang.Math.round(r3)
            long r3 = (long) r3
            float r0 = (float) r0
            float r0 = r0 * r10
            int r0 = java.lang.Math.round(r0)
            long r0 = (long) r0
            double r9 = (double) r10
            double r5 = r5 * r9
        L_0x0126:
            r32 = r0
            r30 = r3
            r34 = r5
            double r11 = r11 + r34
            com.android.settings.fuelgauge.BatteryDiffEntry r0 = new com.android.settings.fuelgauge.BatteryDiffEntry
            r28 = r0
            r29 = r37
            r28.<init>(r29, r30, r32, r34, r36)
            r1 = r26
            r1.add(r0)
            r3 = r38
            r0 = r40
            r8 = r1
            r4 = r16
            r5 = r23
            r6 = r24
            r1 = r25
            goto L_0x0107
        L_0x014a:
            r25 = r1
            r1 = r8
            java.util.Iterator r0 = r1.iterator()
        L_0x0151:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0161
            java.lang.Object r1 = r0.next()
            com.android.settings.fuelgauge.BatteryDiffEntry r1 = (com.android.settings.fuelgauge.BatteryDiffEntry) r1
            r1.setTotalConsumePower(r11)
            goto L_0x0151
        L_0x0161:
            r3 = r25
            goto L_0x0174
        L_0x0164:
            r25 = r1
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r3 = r25
            r3.put(r0, r1)
        L_0x0174:
            int r2 = r2 + 1
            r0 = r40
            r1 = r3
            goto L_0x0012
        L_0x017b:
            r3 = r1
            r0 = -1
            insert24HoursData(r0, r3)
            if (r41 == 0) goto L_0x0185
            purgeLowPercentageAndFakeData(r3)
        L_0x0185:
            return r3
        L_0x0186:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.ConvertUtils.getIndexedUsageMap(android.content.Context, int, long[], java.util.Map, boolean):java.util.Map");
    }

    private static void insert24HoursData(int i, Map<Integer, List<BatteryDiffEntry>> map) {
        HashMap hashMap = new HashMap();
        double d = 0.0d;
        for (List<BatteryDiffEntry> it : map.values()) {
            for (BatteryDiffEntry batteryDiffEntry : it) {
                String key = batteryDiffEntry.mBatteryHistEntry.getKey();
                BatteryDiffEntry batteryDiffEntry2 = (BatteryDiffEntry) hashMap.get(key);
                if (batteryDiffEntry2 == null) {
                    hashMap.put(key, batteryDiffEntry.clone());
                } else {
                    batteryDiffEntry2.mForegroundUsageTimeInMs += batteryDiffEntry.mForegroundUsageTimeInMs;
                    batteryDiffEntry2.mBackgroundUsageTimeInMs += batteryDiffEntry.mBackgroundUsageTimeInMs;
                    batteryDiffEntry2.mConsumePower += batteryDiffEntry.mConsumePower;
                }
                d += batteryDiffEntry.mConsumePower;
            }
        }
        ArrayList<BatteryDiffEntry> arrayList = new ArrayList<>(hashMap.values());
        for (BatteryDiffEntry totalConsumePower : arrayList) {
            totalConsumePower.setTotalConsumePower(d);
        }
        map.put(Integer.valueOf(i), arrayList);
    }

    private static void purgeLowPercentageAndFakeData(Map<Integer, List<BatteryDiffEntry>> map) {
        for (List<BatteryDiffEntry> it : map.values()) {
            Iterator it2 = it.iterator();
            while (it2.hasNext()) {
                BatteryDiffEntry batteryDiffEntry = (BatteryDiffEntry) it2.next();
                if (batteryDiffEntry.getPercentOfTotal() < PERCENTAGE_OF_TOTAL_THRESHOLD || "fake_package".equals(batteryDiffEntry.getPackageName())) {
                    it2.remove();
                }
            }
        }
    }

    private static BatteryHistEntry selectBatteryHistEntry(BatteryHistEntry batteryHistEntry, BatteryHistEntry batteryHistEntry2, BatteryHistEntry batteryHistEntry3) {
        if (batteryHistEntry != null && batteryHistEntry != EMPTY_BATTERY_HIST_ENTRY) {
            return batteryHistEntry;
        }
        if (batteryHistEntry2 != null && batteryHistEntry2 != EMPTY_BATTERY_HIST_ENTRY) {
            return batteryHistEntry2;
        }
        if (batteryHistEntry3 == null || batteryHistEntry3 == EMPTY_BATTERY_HIST_ENTRY) {
            return null;
        }
        return batteryHistEntry3;
    }

    static Locale getLocale(Context context) {
        if (context == null) {
            return Locale.getDefault();
        }
        LocaleList locales = context.getResources().getConfiguration().getLocales();
        if (locales == null || locales.isEmpty()) {
            return Locale.getDefault();
        }
        return locales.get(0);
    }
}
