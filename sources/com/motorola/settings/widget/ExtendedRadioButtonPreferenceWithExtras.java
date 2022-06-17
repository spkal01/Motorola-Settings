package com.motorola.settings.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1987R$layout;
import com.android.settingslib.widget.RadioButtonPreference;

public class ExtendedRadioButtonPreferenceWithExtras extends RadioButtonPreference {
    private Button mActionButton;
    private View.OnClickListener mActionOnClickListener;
    private String mActionText;

    public ExtendedRadioButtonPreferenceWithExtras(Context context) {
        super(context);
        setLayoutResource(C1987R$layout.extended_preference_radio_with_extras);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Button button = (Button) preferenceViewHolder.findViewById(16908313);
        this.mActionButton = button;
        button.setVisibility(TextUtils.isEmpty(this.mActionText) ? 8 : 0);
        this.mActionButton.setText(this.mActionText);
        this.mActionButton.setOnClickListener(this.mActionOnClickListener);
    }

    public void setActionButton(String str) {
        this.mActionText = str;
        Button button = this.mActionButton;
        if (button != null) {
            button.setText(str);
            this.mActionButton.setVisibility(TextUtils.isEmpty(this.mActionText) ? 8 : 0);
        }
    }

    public void setActionButtonOnClickListener(View.OnClickListener onClickListener) {
        this.mActionOnClickListener = onClickListener;
        Button button = this.mActionButton;
        if (button != null) {
            button.setOnClickListener(onClickListener);
        }
    }
}
