package com.android.settings.display;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import com.android.internal.view.RotationPolicy;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.search.Indexable$SearchIndexProvider;

public class SmartAutoRotatePreferenceFragment extends DashboardFragment {
    static final String AUTO_ROTATE_SWITCH_PREFERENCE_ID = "auto_rotate_switch";
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.auto_rotate_settings);
    private RotationPolicy.RotationPolicyListener mRotationPolicyListener;
    /* access modifiers changed from: private */
    public AutoRotateSwitchBarController mSwitchBarController;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "SmartAutoRotatePreferenceFragment";
    }

    public int getMetricsCategory() {
        return 1867;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.auto_rotate_settings;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((SmartAutoRotateController) use(SmartAutoRotateController.class)).init(getLifecycle());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        createHeader(settingsActivity);
        Preference findPreference = findPreference("footer_preference");
        if (findPreference != null) {
            findPreference.setTitle((CharSequence) Html.fromHtml(getString(C1992R$string.smart_rotate_text_headline), 63));
            findPreference.setVisible(SmartAutoRotateController.isRotationResolverServiceAvailable(settingsActivity));
        }
        return onCreateView;
    }

    /* access modifiers changed from: package-private */
    public void createHeader(SettingsActivity settingsActivity) {
        if (SmartAutoRotateController.isRotationResolverServiceAvailable(settingsActivity)) {
            SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
            switchBar.setTitle(getContext().getString(C1992R$string.auto_rotate_settings_primary_switch_title));
            switchBar.show();
            this.mSwitchBarController = new AutoRotateSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
            findPreference(AUTO_ROTATE_SWITCH_PREFERENCE_ID).setVisible(false);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mRotationPolicyListener == null) {
            this.mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() {
                public void onChange() {
                    if (SmartAutoRotatePreferenceFragment.this.mSwitchBarController != null) {
                        SmartAutoRotatePreferenceFragment.this.mSwitchBarController.onChange();
                    }
                }
            };
        }
        RotationPolicy.registerRotationPolicyListener(getPrefContext(), this.mRotationPolicyListener);
    }

    public void onPause() {
        super.onPause();
        if (this.mRotationPolicyListener != null) {
            RotationPolicy.unregisterRotationPolicyListener(getPrefContext(), this.mRotationPolicyListener);
        }
    }
}
