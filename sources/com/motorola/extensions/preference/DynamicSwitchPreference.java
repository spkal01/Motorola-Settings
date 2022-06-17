package com.motorola.extensions.preference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.motorola.extensions.preference.DynamicPreferenceDataObserver;
import com.motorola.extensions.preference.DynamicTwoStatePreferenceDelegator;

public class DynamicSwitchPreference extends SwitchPreference implements DynamicPreferenceDataObserver.IAutoRefresh, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, DynamicTwoStatePreferenceDelegator.DelegatorHelper {
    private static final String TAG = DynamicSwitchPreference.class.getSimpleName();
    private DynamicTwoStatePreferenceDelegator mTwoStatePrefDelegator = null;

    public boolean onPreferenceClick(Preference preference) {
        return true;
    }

    public DynamicSwitchPreference(Context context) {
        super(context);
        init();
    }

    public DynamicSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public DynamicSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setOnPreferenceChangeListener(this);
        setOnPreferenceClickListener(this);
        this.mTwoStatePrefDelegator = new DynamicTwoStatePreferenceDelegator(getContext(), this);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mTwoStatePrefDelegator.onBindViewHolder(preferenceViewHolder);
    }

    public void setIntent(Intent intent) {
        this.mTwoStatePrefDelegator.setIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.mTwoStatePrefDelegator.onAttachedToHierarchy(preferenceManager);
    }

    public void onDetached() {
        super.onDetached();
        this.mTwoStatePrefDelegator.onDetached();
    }

    public void setAutoRefresh(Uri uri, boolean z) {
        this.mTwoStatePrefDelegator.setAutoRefresh(uri, z);
    }

    public void refresh() {
        this.mTwoStatePrefDelegator.refresh();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        return this.mTwoStatePrefDelegator.onPreferenceChange(preference, obj);
    }

    public DynamicTwoStatePreferenceDelegator getDelegator() {
        return this.mTwoStatePrefDelegator;
    }
}
