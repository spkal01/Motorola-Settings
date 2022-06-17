package com.motorola.settings.display.colors;

import android.content.Context;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.display.ColorModePreferenceFragment;
import com.android.settings.utils.CandidateInfoExtra;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import com.motorola.settings.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

public class ExtendedColorModePreferenceFragment extends ColorModePreferenceFragment {
    /* access modifiers changed from: protected */
    public void addStaticPreferences(PreferenceScreen preferenceScreen) {
        if (!DisplayUtils.isColorTemperatureModeAvailable(getContext())) {
            super.addStaticPreferences(preferenceScreen);
            return;
        }
        LayoutPreference layoutPreference = new LayoutPreference(preferenceScreen.getContext(), C1987R$layout.color_temperature_preview);
        layoutPreference.setSelectable(false);
        preferenceScreen.addPreference(layoutPreference);
    }

    /* access modifiers changed from: protected */
    public List<? extends CandidateInfo> getCandidates() {
        if (!DisplayUtils.isColorTemperatureModeAvailable(getContext())) {
            return super.getCandidates();
        }
        Context context = getContext();
        int[] intArray = context.getResources().getIntArray(17236004);
        ArrayList arrayList = new ArrayList();
        if (intArray != null) {
            for (int i : intArray) {
                if (i == 0) {
                    arrayList.add(new CandidateInfoExtra(context.getText(C1992R$string.color_mode_option_natural), context.getText(C1992R$string.color_temperature_option_natural_summary), getKeyForColorMode(0), true));
                } else if (i == 2) {
                    arrayList.add(new CandidateInfoExtra(context.getText(C1992R$string.color_mode_option_saturated), context.getText(C1992R$string.color_temperature_option_saturated_summary), getKeyForColorMode(2), true));
                }
            }
        }
        return arrayList;
    }

    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        super.bindPreferenceExtra(radioButtonPreference, str, candidateInfo, str2, str3);
        radioButtonPreference.setAppendixVisibility(8);
        if (candidateInfo instanceof CandidateInfoExtra) {
            radioButtonPreference.setSummary(((CandidateInfoExtra) candidateInfo).loadSummary());
        }
    }

    public void updateCandidates() {
        super.updateCandidates();
        if (DisplayUtils.isColorTemperatureModeAvailable(getContext())) {
            addPreferencesFromResource(C1994R$xml.extended_color_mode_settings);
            ArrayList arrayList = new ArrayList();
            arrayList.add(new ColorTemperaturePreferenceController(getContext()));
            arrayList.forEach(new ExtendedColorModePreferenceFragment$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCandidates$0(AbstractPreferenceController abstractPreferenceController) {
        abstractPreferenceController.displayPreference(getPreferenceScreen());
    }
}
