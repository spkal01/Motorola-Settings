package com.android.settings.accounts;

import android.util.Log;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;

public class AccountPreference extends Preference {
    private boolean mShowTypeIcon;
    private int mStatus;
    private ImageView mSyncStatusIcon;

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (!this.mShowTypeIcon) {
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(16908294);
            this.mSyncStatusIcon = imageView;
            imageView.setImageResource(getSyncStatusIcon(this.mStatus));
            this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
        }
    }

    private int getSyncStatusIcon(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return C1983R$drawable.ic_settings_sync_failed;
                }
                if (i != 3) {
                    if (i != 4) {
                        int i2 = C1983R$drawable.ic_settings_sync_failed;
                        Log.e("AccountPreference", "Unknown sync status: " + i);
                        return i2;
                    }
                }
            }
            return C1983R$drawable.ic_settings_sync_disabled;
        }
        return C1983R$drawable.ic_settings_sync;
    }

    private String getSyncContentDescription(int i) {
        if (i == 0) {
            return getContext().getString(C1992R$string.accessibility_sync_enabled);
        }
        if (i == 1) {
            return getContext().getString(C1992R$string.accessibility_sync_disabled);
        }
        if (i == 2) {
            return getContext().getString(C1992R$string.accessibility_sync_error);
        }
        if (i == 3) {
            return getContext().getString(C1992R$string.accessibility_sync_in_progress);
        }
        if (i == 4) {
            return getContext().getString(C1992R$string.sync_none);
        }
        Log.e("AccountPreference", "Unknown sync status: " + i);
        return getContext().getString(C1992R$string.accessibility_sync_error);
    }
}
