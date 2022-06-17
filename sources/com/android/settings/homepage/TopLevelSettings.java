package com.android.settings.homepage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1980R$bool;
import com.android.settings.C1994R$xml;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.SupportPreferenceController;
import com.android.settingslib.core.instrumentation.Instrumentable;

public class TopLevelSettings extends DashboardFragment implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.top_level_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };

    public Fragment getCallbackFragment() {
        return this;
    }

    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "TopLevelSettings";
    }

    public int getMetricsCategory() {
        return 35;
    }

    public TopLevelSettings() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("need_search_icon_in_action_bar", false);
        setArguments(bundle);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.top_level_settings;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((SupportPreferenceController) use(SupportPreferenceController.class)).setActivity(getActivity());
    }

    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        new SubSettingLauncher(getActivity()).setDestination(preference.getFragment()).setArguments(preference.getExtras()).setSourceMetricsCategory(preferenceFragmentCompat instanceof Instrumentable ? ((Instrumentable) preferenceFragmentCompat).getMetricsCategory() : 0).setTitleRes(-1).launch();
        return true;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            int homepageIconColor = Utils.getHomepageIconColor(getContext());
            int preferenceCount = preferenceScreen.getPreferenceCount();
            int i = 0;
            while (i < preferenceCount) {
                Preference preference = preferenceScreen.getPreference(i);
                if (preference != null) {
                    Drawable icon = preference.getIcon();
                    if (icon != null) {
                        icon.setTint(homepageIconColor);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldForceRoundedIcon() {
        return getContext().getResources().getBoolean(C1980R$bool.config_force_rounded_icon_TopLevelSettings);
    }
}
