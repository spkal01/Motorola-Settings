package com.motorola.settings.gestures;

import android.content.Context;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.gestures.SystemNavigationGestureSettings;
import com.android.settings.gestures.SystemNavigationPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.RadioButtonPreference;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.settings.widget.ImageRadioButtonPreference;
import java.util.ArrayList;
import java.util.List;

public class ThreeButtonNavSettingsFragment extends RadioButtonPickerFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.three_button_nav_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            if (SystemNavigationGestureSettings.isCustNavBarAvailable()) {
                return SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.threebutton");
            }
            return false;
        }
    };

    public int getMetricsCategory() {
        return 0;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        SearchMenuController.init((InstrumentedPreferenceFragment) this);
    }

    public void updateCandidates() {
        String defaultKey = getDefaultKey();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        List<? extends CandidateInfo> candidates = getCandidates();
        if (candidates != null) {
            for (CandidateInfo candidateInfo : candidates) {
                ImageRadioButtonPreference imageRadioButtonPreference = new ImageRadioButtonPreference(getPrefContext());
                bindPreference(imageRadioButtonPreference, candidateInfo.getKey(), candidateInfo, defaultKey);
                preferenceScreen.addPreference(imageRadioButtonPreference);
            }
        }
    }

    public RadioButtonPreference bindPreference(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2) {
        ImageRadioButtonPreference imageRadioButtonPreference = (ImageRadioButtonPreference) radioButtonPreference;
        imageRadioButtonPreference.setTitle(candidateInfo.loadLabel());
        imageRadioButtonPreference.setImage(candidateInfo.loadIcon());
        radioButtonPreference.setKey(str);
        if (TextUtils.equals(str2, str)) {
            radioButtonPreference.setChecked(true);
        }
        radioButtonPreference.setEnabled(candidateInfo.enabled);
        radioButtonPreference.setOnClickListener(this);
        return radioButtonPreference;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.three_button_nav_settings;
    }

    /* access modifiers changed from: protected */
    public List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ImageRadioCandidateInfo(context.getText(C1992R$string.three_button_nav_ltr_title), context.getDrawable(C1983R$drawable.three_buttons_back_home_recent), "back_home_recent", true));
        arrayList.add(new ImageRadioCandidateInfo(context.getText(C1992R$string.three_button_nav_rtl_title), context.getDrawable(C1983R$drawable.three_buttons_recent_home_back), "recent_home_back", true));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public String getDefaultKey() {
        return MotorolaSettings.Secure.getInt(getContext().getContentResolver(), "three_button_layout_direction", 0) != 1 ? "back_home_recent" : "recent_home_back";
    }

    /* access modifiers changed from: protected */
    public boolean setDefaultKey(String str) {
        return setCurrentNavigationPosition(str);
    }

    private boolean setCurrentNavigationPosition(String str) {
        Context context = getContext();
        str.hashCode();
        if (str.equals("back_home_recent")) {
            return MotorolaSettings.Secure.putInt(context.getContentResolver(), "three_button_layout_direction", 0);
        }
        if (!str.equals("recent_home_back")) {
            return false;
        }
        return MotorolaSettings.Secure.putInt(context.getContentResolver(), "three_button_layout_direction", 1);
    }
}
