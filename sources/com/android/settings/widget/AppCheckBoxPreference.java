package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class AppCheckBoxPreference extends CheckBoxPreference {
    public AppCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C1987R$layout.preference_app);
    }

    public AppCheckBoxPreference(Context context) {
        super(context);
        setLayoutResource(C1987R$layout.preference_app);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        LinearLayout linearLayout = (LinearLayout) preferenceViewHolder.findViewById(C1985R$id.summary_container);
        if (linearLayout != null) {
            linearLayout.setVisibility(TextUtils.isEmpty(getSummary()) ? 8 : 0);
        }
    }
}
