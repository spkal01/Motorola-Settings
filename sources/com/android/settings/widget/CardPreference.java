package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.C1979R$attr;

public class CardPreference extends Preference {
    public CardPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public CardPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, C1979R$attr.cardPreferenceStyle);
    }
}
