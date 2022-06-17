package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.widget.FooterPreference;

public class BatterySaverSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.battery_saver_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return !context.getResources().getBoolean(C1980R$bool.config_battery_saver_customizable);
        }
    };
    private String mHelpUri;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "BatterySaverSettings";
    }

    public int getMetricsCategory() {
        return 1881;
    }

    public void onStart() {
        super.onStart();
        setupFooter();
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.battery_saver_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_battery_saver_settings;
    }

    /* access modifiers changed from: package-private */
    public void setupFooter() {
        String string = getString(C1992R$string.help_url_battery_saver_settings);
        this.mHelpUri = string;
        if (!TextUtils.isEmpty(string)) {
            addHelpLink();
        }
    }

    /* access modifiers changed from: package-private */
    public void addHelpLink() {
        FooterPreference footerPreference = (FooterPreference) getPreferenceScreen().findPreference("battery_saver_footer_preference");
        if (footerPreference != null) {
            footerPreference.setSelectable(false);
            footerPreference.setLearnMoreAction(new BatterySaverSettings$$ExternalSyntheticLambda0(this));
            footerPreference.setLearnMoreContentDescription(getString(C1992R$string.battery_saver_link_a11y));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addHelpLink$0(View view) {
        this.mMetricsFeatureProvider.action(getContext(), 1779, (Pair<Integer, Object>[]) new Pair[0]);
        startActivityForResult(HelpUtils.getHelpIntent(getContext(), getString(C1992R$string.help_url_battery_saver_settings), ""), 0);
    }
}
