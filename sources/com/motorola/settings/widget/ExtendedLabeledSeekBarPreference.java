package com.motorola.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;
import com.android.settings.C1987R$layout;
import com.android.settings.widget.LabeledSeekBarPreference;

public class ExtendedLabeledSeekBarPreference extends LabeledSeekBarPreference {
    public ExtendedLabeledSeekBarPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public ExtendedLabeledSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ExtendedLabeledSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ExtendedLabeledSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Preference, i, i2);
        setLayoutResource(obtainStyledAttributes.getResourceId(3, C1987R$layout.preference_labeled_slider));
        obtainStyledAttributes.recycle();
    }
}
