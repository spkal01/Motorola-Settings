package com.android.settings.applications;

import android.content.Context;
import android.os.Bundle;
import android.text.format.Formatter;
import androidx.preference.Preference;
import com.android.settings.C1978R$array;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SummaryPreference;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
import com.motorola.settings.datausage.MotoDataUsageUtils;

public class ProcessStatsSummary extends ProcessStatsBase implements Preference.OnPreferenceClickListener {
    private Preference mAppListPreference;
    private Preference mAverageUsed;
    private Preference mFree;
    private Preference mPerformance;
    private SummaryPreference mSummaryPref;
    private Preference mTotalMemory;

    public int getMetricsCategory() {
        return 202;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C1994R$xml.process_stats_summary);
        this.mSummaryPref = (SummaryPreference) findPreference("status_header");
        this.mPerformance = findPreference("performance");
        this.mTotalMemory = findPreference("total_memory");
        this.mAverageUsed = findPreference("average_used");
        this.mFree = findPreference("free");
        Preference findPreference = findPreference("apps_list");
        this.mAppListPreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
    }

    public void refreshUi() {
        CharSequence charSequence;
        Context context = getContext();
        ProcStatsData.MemInfo memInfo = this.mStatsManager.getMemInfo();
        double d = memInfo.realUsedRam;
        double d2 = memInfo.realTotalRam;
        double d3 = memInfo.realFreeRam;
        long j = (long) d;
        Formatter.BytesResult formatBytes = MotoDataUsageUtils.formatBytes(context, j, 1);
        long j2 = (long) d2;
        String formatShortFileSize = Formatter.formatShortFileSize(context, j2);
        String formatShortFileSize2 = Formatter.formatShortFileSize(context, (long) d3);
        CharSequence[] textArray = getResources().getTextArray(C1978R$array.ram_states);
        int memState = this.mStatsManager.getMemState();
        if (memState < 0 || memState >= textArray.length - 1) {
            charSequence = textArray[textArray.length - 1];
        } else {
            charSequence = textArray[memState];
        }
        this.mSummaryPref.setAmount(formatBytes.value);
        this.mSummaryPref.setUnits(formatBytes.units);
        float f = (float) (d / (d3 + d));
        this.mSummaryPref.setRatios(f, 0.0f, 1.0f - f);
        this.mPerformance.setSummary(charSequence);
        this.mTotalMemory.setSummary((CharSequence) formatShortFileSize);
        this.mAverageUsed.setSummary((CharSequence) Utils.formatPercentage(j, j2));
        this.mFree.setSummary((CharSequence) formatShortFileSize2);
        String string = getString(ProcessStatsBase.sDurationLabels[this.mDurationIndex]);
        int size = this.mStatsManager.getEntries().size();
        this.mAppListPreference.setSummary((CharSequence) getResources().getQuantityString(C1990R$plurals.memory_usage_apps_summary, size, new Object[]{Integer.valueOf(size), string}));
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_process_stats_summary;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference != this.mAppListPreference) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("transfer_stats", true);
        bundle.putInt("duration_index", this.mDurationIndex);
        this.mStatsManager.xferStats();
        new SubSettingLauncher(getContext()).setDestination(ProcessStatsUi.class.getName()).setTitleRes(C1992R$string.memory_usage_apps).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
        return true;
    }
}
