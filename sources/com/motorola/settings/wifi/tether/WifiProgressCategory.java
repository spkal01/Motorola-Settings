package com.motorola.settings.wifi.tether;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class WifiProgressCategory extends PreferenceCategory {
    private boolean mNoDeviceFoundAdded = false;
    private Preference mNoDeviceFoundPreference;

    public WifiProgressCategory(Context context) {
        super(context);
        setLayoutResource(C1987R$layout.preference_progress_category);
    }

    public WifiProgressCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C1987R$layout.preference_progress_category);
    }

    public WifiProgressCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C1987R$layout.preference_progress_category);
    }

    public WifiProgressCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C1987R$layout.preference_progress_category);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(C1985R$id.scanning_progress).setVisibility(8);
    }

    public void removeAll() {
        super.removeAll();
        Preference preference = this.mNoDeviceFoundPreference;
        if (preference != null) {
            addPreference(preference);
            this.mNoDeviceFoundAdded = true;
        }
    }

    public boolean addPreference(Preference preference) {
        Preference preference2;
        if (this.mNoDeviceFoundAdded && (preference2 = this.mNoDeviceFoundPreference) != null) {
            removePreference(preference2);
            this.mNoDeviceFoundAdded = false;
        }
        return super.addPreference(preference);
    }

    public void setEmptyPreference(Preference preference) {
        this.mNoDeviceFoundPreference = preference;
    }
}
