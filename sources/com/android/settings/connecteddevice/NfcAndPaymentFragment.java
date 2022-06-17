package com.android.settings.connecteddevice;

import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class NfcAndPaymentFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.nfc_and_payment_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "NfcAndPaymentFragment";
    }

    public int getMetricsCategory() {
        return 1828;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.nfc_and_payment_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_nfc_and_payment_settings;
    }
}
