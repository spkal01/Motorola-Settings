package com.android.settings.vpn2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedPreference;

public class VpnInfoPreference extends RestrictedPreference implements View.OnClickListener {
    private String mHelpUrl;
    private boolean mIsInsecureVpn = false;

    /* access modifiers changed from: protected */
    public boolean shouldHideSecondTarget() {
        return false;
    }

    public VpnInfoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHelpUrl = context.getString(C1992R$string.help_url_insecure_vpn);
    }

    /* access modifiers changed from: protected */
    public int getSecondTargetResId() {
        return C1987R$layout.preference_widget_warning;
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C1985R$id.warning_button);
        if (!this.mIsInsecureVpn || TextUtils.isEmpty(this.mHelpUrl)) {
            findViewById.setVisibility(8);
            findViewById.setOnClickListener(this);
            findViewById.setEnabled(false);
        } else {
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(this);
            findViewById.setEnabled(true);
        }
        preferenceViewHolder.findViewById(C1985R$id.two_target_divider).setVisibility(8);
    }

    public void onClick(View view) {
        Intent helpIntent;
        if (view.getId() == C1985R$id.warning_button && (helpIntent = HelpUtils.getHelpIntent(getContext(), this.mHelpUrl, getClass().getName())) != null) {
            ((Activity) getContext()).startActivityForResult(helpIntent, 0);
        }
    }

    public void setInsecureVpn(boolean z) {
        if (this.mIsInsecureVpn != z) {
            this.mIsInsecureVpn = z;
            notifyChanged();
        }
    }
}
