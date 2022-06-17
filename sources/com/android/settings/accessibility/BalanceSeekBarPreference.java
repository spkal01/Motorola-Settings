package com.android.settings.accessibility;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1979R$attr;
import com.android.settings.C1987R$layout;
import com.android.settings.widget.SeekBarPreference;

public class BalanceSeekBarPreference extends SeekBarPreference {
    private final Context mContext;
    private ImageView mIconView;
    private BalanceSeekBar mSeekBar;

    public BalanceSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, TypedArrayUtils.getAttr(context, C1979R$attr.preferenceStyle, 16842894));
        this.mContext = context;
        setLayoutResource(C1987R$layout.preference_balance_slider);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mSeekBar = (BalanceSeekBar) preferenceViewHolder.findViewById(16909418);
        this.mIconView = (ImageView) preferenceViewHolder.findViewById(16908294);
        init();
    }

    private void init() {
        if (this.mSeekBar != null) {
            float floatForUser = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "master_balance", 0.0f, -2);
            this.mSeekBar.setMax(200);
            this.mSeekBar.setProgress(((int) (floatForUser * 100.0f)) + 100);
            this.mSeekBar.setEnabled(isEnabled());
        }
    }
}
