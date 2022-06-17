package com.motorola.settings.display.refreshrate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.android.settings.C1978R$array;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.support.actionbar.HelpMenuController;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.ArrayList;
import java.util.List;

public class DisplayRefreshRatePreferenceFragment extends RadioButtonPickerFragment implements HelpResourceProvider {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.display_refresh_rate_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return DisplayRefreshRateUtil.isDisplayRefreshRateAvailable(context);
        }
    };
    FooterPreference mFooterPreference;

    public int getMetricsCategory() {
        return 3002;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SearchMenuController.init((InstrumentedPreferenceFragment) this);
        HelpMenuController.init((ObservablePreferenceFragment) this);
        FooterPreference footerPreference = new FooterPreference(getPrefContext());
        this.mFooterPreference = footerPreference;
        footerPreference.setIcon(C1983R$drawable.ic_info_outline_24dp);
        this.mFooterPreference.setTitle(C1992R$string.display_refresh_rate_pref_summary);
        getPreferenceScreen().addPreference(this.mFooterPreference);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.display_refresh_rate_settings;
    }

    /* access modifiers changed from: protected */
    public String getDefaultKey() {
        return getDefaultRefreshRateKey();
    }

    private String getDefaultRefreshRateKey() {
        int currentRefreshRate = DisplayRefreshRateUtil.getCurrentRefreshRate(getContext());
        return "display_refresh_rate_" + currentRefreshRate;
    }

    /* access modifiers changed from: protected */
    public boolean setDefaultKey(String str) {
        DisplayRefreshRateUtil.setRefreshRate(getContext(), (float) (str.length() > 21 ? Integer.parseInt(str.substring(21, str.length())) : 0));
        return true;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_refresh_rate;
    }

    /* access modifiers changed from: protected */
    public void onSelectionPerformed(boolean z) {
        super.onSelectionPerformed(z);
        getActivity().finish();
    }

    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if (candidateInfo instanceof DisplayRefreshRateCandidateInfo) {
            radioButtonPreference.setSummary(((DisplayRefreshRateCandidateInfo) candidateInfo).loadSummary());
            radioButtonPreference.setAppendixVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public List<? extends CandidateInfo> getCandidates() {
        getContext();
        ArrayList arrayList = new ArrayList();
        int[] intArray = getResources().getIntArray(C1978R$array.supported_refresh_rate_value);
        for (int i : intArray) {
            arrayList.add(new DisplayRefreshRateCandidateInfo(DisplayRefreshRateUtil.getRefreshRateTitle(getActivity(), i), DisplayRefreshRateUtil.getRefreshRateSummary(getActivity(), i), "display_refresh_rate_" + i, true));
        }
        return arrayList;
    }

    static class DisplayRefreshRateCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;
        private final CharSequence mSummary;

        public Drawable loadIcon() {
            return null;
        }

        DisplayRefreshRateCandidateInfo(CharSequence charSequence, CharSequence charSequence2, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mSummary = charSequence2;
            this.mKey = str;
        }

        public CharSequence loadLabel() {
            return this.mLabel;
        }

        public CharSequence loadSummary() {
            return this.mSummary;
        }

        public String getKey() {
            return this.mKey;
        }
    }
}
