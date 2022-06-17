package com.motorola.settings.extensions;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.motorola.extensions.preference.DynamicTwoStatePreferenceDelegator;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class MotoPreferenceFragment extends ObservablePreferenceFragment {
    private static final boolean DEBUG = (!"user".equals(Build.TYPE));
    protected ArrayList<Preference> mAddedPrefs = new ArrayList<>();
    protected ArrayList<Preference> mRemovedPrefs = new ArrayList<>();
    private DynamicTwoStatePreferenceDelegator mTwoStatePrefDelegator;

    public void onViewCreated(View view, Bundle bundle) {
        if (DPUtils.isOkToAddOrOverridePrefsPostCreate(this)) {
            addOrOverridePreferences();
        }
        super.onViewCreated(view, bundle);
    }

    public void onPreferencesLoaded() {
        if (DPUtils.isOkToAddOrOverridePrefsOnCustomLoad(this)) {
            addOrOverridePreferences();
        }
    }

    private void addOrOverridePreferences() {
        if (!this.mAddedPrefs.isEmpty()) {
            this.mAddedPrefs.clear();
        }
        if (!this.mRemovedPrefs.isEmpty()) {
            this.mRemovedPrefs.clear();
        }
        DPUtils.addOrOverridePreferences(this, this.mAddedPrefs, this.mRemovedPrefs);
        Iterator<Preference> it = this.mAddedPrefs.iterator();
        while (it.hasNext()) {
            Preference next = it.next();
            if (DEBUG) {
                Log.i("MotoPreferenceFragment", "Added pref with key : " + next.getKey());
            }
        }
        Iterator<Preference> it2 = this.mRemovedPrefs.iterator();
        while (it2.hasNext()) {
            Preference next2 = it2.next();
            if (DEBUG) {
                Log.i("MotoPreferenceFragment", "Removed pref with key : " + next2.getKey());
            }
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        DynamicTwoStatePreferenceDelegator dynamicTwoStatePreferenceDelegator = this.mTwoStatePrefDelegator;
        if (dynamicTwoStatePreferenceDelegator == null || i != dynamicTwoStatePreferenceDelegator.getRequestCode()) {
            super.onActivityResult(i, i2, intent);
            return;
        }
        this.mTwoStatePrefDelegator.onActivityResult(i, i2, intent);
        this.mTwoStatePrefDelegator = null;
    }

    public void onDisplayPreferenceDialog(Preference preference) {
        if (!(preference instanceof DynamicTwoStatePreferenceDelegator.DelegatorHelper)) {
            super.onDisplayPreferenceDialog(preference);
        } else if (this.mTwoStatePrefDelegator == null) {
            DynamicTwoStatePreferenceDelegator delegator = ((DynamicTwoStatePreferenceDelegator.DelegatorHelper) preference).getDelegator();
            this.mTwoStatePrefDelegator = delegator;
            Intent launchIntent = delegator.getLaunchIntent();
            if (launchIntent != null) {
                int requestCode = this.mTwoStatePrefDelegator.getRequestCode();
                try {
                    startActivityForResult(launchIntent, requestCode);
                } catch (ActivityNotFoundException unused) {
                    this.mTwoStatePrefDelegator.onActivityResult(requestCode, 0, (Intent) null);
                    this.mTwoStatePrefDelegator = null;
                    Toast.makeText(getContext(), 17040800, 0).show();
                }
            }
        }
    }
}
