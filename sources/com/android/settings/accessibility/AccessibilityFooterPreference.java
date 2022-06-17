package com.android.settings.accessibility;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.widget.FooterPreference;

public final class AccessibilityFooterPreference extends FooterPreference {
    private boolean mLinkEnabled;

    public AccessibilityFooterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AccessibilityFooterPreference(Context context) {
        super(context);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        if (this.mLinkEnabled) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textView.setMovementMethod((MovementMethod) null);
        }
    }

    public void setLinkEnabled(boolean z) {
        if (this.mLinkEnabled != z) {
            this.mLinkEnabled = z;
            notifyChanged();
        }
    }
}
