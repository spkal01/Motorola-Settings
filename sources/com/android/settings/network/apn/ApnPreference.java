package com.android.settings.network.apn;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1979R$attr;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;

public class ApnPreference extends Preference implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private boolean mHideDetails;
    private boolean mProtectFromCheckedChange;
    private boolean mSelectable;
    private int mSubId;
    private CompoundButton sCurrentChecked;
    private String sSelectedKey;

    public ApnPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSubId = -1;
        this.sSelectedKey = null;
        this.sCurrentChecked = null;
        this.mProtectFromCheckedChange = false;
        this.mSelectable = true;
        this.mHideDetails = false;
    }

    public ApnPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, C1979R$attr.apnPreferenceStyle);
    }

    public ApnPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((RelativeLayout) preferenceViewHolder.findViewById(C1985R$id.text_layout)).setOnClickListener(this);
        View findViewById = preferenceViewHolder.findViewById(C1985R$id.apn_radiobutton);
        if (findViewById != null && (findViewById instanceof RadioButton)) {
            RadioButton radioButton = (RadioButton) findViewById;
            if (this.mSelectable) {
                radioButton.setOnCheckedChangeListener(this);
                boolean equals = getKey().equals(this.sSelectedKey);
                if (equals) {
                    this.sCurrentChecked = radioButton;
                    this.sSelectedKey = getKey();
                }
                this.mProtectFromCheckedChange = true;
                radioButton.setChecked(equals);
                this.mProtectFromCheckedChange = false;
                radioButton.setVisibility(0);
                return;
            }
            radioButton.setVisibility(8);
        }
    }

    public void setChecked() {
        this.sSelectedKey = getKey();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        Log.i("ApnPreference", "ID: " + getKey() + " :" + z);
        if (!this.mProtectFromCheckedChange) {
            if (z) {
                CompoundButton compoundButton2 = this.sCurrentChecked;
                if (compoundButton2 != null) {
                    compoundButton2.setChecked(false);
                }
                this.sCurrentChecked = compoundButton;
                String key = getKey();
                this.sSelectedKey = key;
                callChangeListener(key);
                return;
            }
            this.sCurrentChecked = null;
            this.sSelectedKey = null;
        }
    }

    public void onClick(View view) {
        super.onClick();
        Context context = getContext();
        int parseInt = Integer.parseInt(getKey());
        if (context == null) {
            Log.w("ApnPreference", "No context available for pos=" + parseInt);
        } else if (this.mHideDetails) {
            Toast.makeText(context, context.getString(C1992R$string.cannot_change_apn_toast), 1).show();
        } else {
            Intent intent = new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, (long) parseInt));
            intent.putExtra("sub_id", this.mSubId);
            intent.addFlags(1);
            context.startActivity(intent);
        }
    }

    public void setSelectable(boolean z) {
        this.mSelectable = z;
    }

    public void setSubId(int i) {
        this.mSubId = i;
    }

    public void setHideDetails() {
        this.mHideDetails = true;
    }
}
