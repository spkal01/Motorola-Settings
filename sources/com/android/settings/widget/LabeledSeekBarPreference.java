package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.R$styleable;

public class LabeledSeekBarPreference extends SeekBarPreference {
    private Preference.OnPreferenceChangeListener mStopListener;
    private final int mTextEndId;
    private final int mTextStartId;
    private final int mTickMarkId;

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C1987R$layout.preference_labeled_slider);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LabeledSeekBarPreference);
        int i3 = R$styleable.LabeledSeekBarPreference_textStart;
        int i4 = C1992R$string.summary_placeholder;
        this.mTextStartId = obtainStyledAttributes.getResourceId(i3, i4);
        this.mTextEndId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_textEnd, i4);
        this.mTickMarkId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_tickMark, 0);
        obtainStyledAttributes.recycle();
    }

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.seekBarPreferenceStyle, 17957082), 0);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(16908308)).setText(this.mTextStartId);
        ((TextView) preferenceViewHolder.findViewById(16908309)).setText(this.mTextEndId);
        if (this.mTickMarkId != 0) {
            ((SeekBar) preferenceViewHolder.findViewById(16909418)).setTickMark(getContext().getDrawable(this.mTickMarkId));
        }
    }

    public void setOnPreferenceChangeStopListener(Preference.OnPreferenceChangeListener onPreferenceChangeListener) {
        this.mStopListener = onPreferenceChangeListener;
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        Preference.OnPreferenceChangeListener onPreferenceChangeListener = this.mStopListener;
        if (onPreferenceChangeListener != null) {
            onPreferenceChangeListener.onPreferenceChange(this, Integer.valueOf(seekBar.getProgress()));
        }
    }
}
