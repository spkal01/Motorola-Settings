package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Switch;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$styleable;
import androidx.preference.TwoStatePreference;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;
import java.util.List;

public class SettingsMainSwitchPreference extends TwoStatePreference implements OnMainSwitchChangeListener {
    private final List<SettingsMainSwitchBar.OnBeforeCheckedChangeListener> mBeforeCheckedChangeListeners = new ArrayList();
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private SettingsMainSwitchBar mMainSwitchBar;
    private RestrictedPreferenceHelper mRestrictedHelper;
    private final List<OnMainSwitchChangeListener> mSwitchChangeListeners = new ArrayList();
    private CharSequence mTitle;

    public SettingsMainSwitchPreference(Context context) {
        super(context);
        init(context, (AttributeSet) null);
    }

    public SettingsMainSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public SettingsMainSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    public SettingsMainSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(false);
        preferenceViewHolder.setDividerAllowedBelow(false);
        RestrictedPreferenceHelper restrictedPreferenceHelper = this.mRestrictedHelper;
        if (restrictedPreferenceHelper != null) {
            this.mEnforcedAdmin = restrictedPreferenceHelper.checkRestrictionEnforced();
        }
        this.mMainSwitchBar = (SettingsMainSwitchBar) preferenceViewHolder.findViewById(C1985R$id.main_switch_bar);
        initMainSwitchBar();
        if (isVisible()) {
            this.mMainSwitchBar.show();
            if (this.mMainSwitchBar.isChecked() != isChecked()) {
                setChecked(isChecked());
            }
            registerListenerToSwitchBar();
            return;
        }
        this.mMainSwitchBar.hide();
    }

    private void init(Context context, AttributeSet attributeSet) {
        setLayoutResource(C1987R$layout.preference_widget_main_switch);
        this.mSwitchChangeListeners.add(this);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.Preference, 0, 0);
            CharSequence text = TypedArrayUtils.getText(obtainStyledAttributes, R$styleable.Preference_title, R$styleable.Preference_android_title);
            if (!TextUtils.isEmpty(text)) {
                setTitle(text.toString());
            }
            obtainStyledAttributes.recycle();
            this.mRestrictedHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
        }
    }

    public void setChecked(boolean z) {
        super.setChecked(z);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setChecked(z);
        }
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setTitle(charSequence);
        }
    }

    public void onSwitchChanged(Switch switchR, boolean z) {
        super.setChecked(z);
    }

    public void show() {
        setVisible(true);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.show();
        }
    }

    public void hide() {
        setVisible(false);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.hide();
        }
    }

    public void setCheckedInternal(boolean z) {
        super.setChecked(z);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setCheckedInternal(z);
        }
    }

    public void setSwitchBarEnabled(boolean z) {
        setEnabled(z);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setEnabled(z);
        }
    }

    public void setOnBeforeCheckedChangeListener(SettingsMainSwitchBar.OnBeforeCheckedChangeListener onBeforeCheckedChangeListener) {
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar == null) {
            this.mBeforeCheckedChangeListeners.add(onBeforeCheckedChangeListener);
        } else {
            settingsMainSwitchBar.setOnBeforeCheckedChangeListener(onBeforeCheckedChangeListener);
        }
    }

    public void addOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar == null) {
            this.mSwitchChangeListeners.add(onMainSwitchChangeListener);
        } else {
            settingsMainSwitchBar.addOnSwitchChangeListener(onMainSwitchChangeListener);
        }
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mEnforcedAdmin = enforcedAdmin;
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setDisabledByAdmin(enforcedAdmin);
        }
    }

    private void initMainSwitchBar() {
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setTitle(this.mTitle);
            this.mMainSwitchBar.setDisabledByAdmin(this.mEnforcedAdmin);
        }
    }

    private void registerListenerToSwitchBar() {
        for (SettingsMainSwitchBar.OnBeforeCheckedChangeListener onBeforeCheckedChangeListener : this.mBeforeCheckedChangeListeners) {
            this.mMainSwitchBar.setOnBeforeCheckedChangeListener(onBeforeCheckedChangeListener);
        }
        for (OnMainSwitchChangeListener addOnSwitchChangeListener : this.mSwitchChangeListeners) {
            this.mMainSwitchBar.addOnSwitchChangeListener(addOnSwitchChangeListener);
        }
        this.mBeforeCheckedChangeListeners.clear();
        this.mSwitchChangeListeners.clear();
    }
}
