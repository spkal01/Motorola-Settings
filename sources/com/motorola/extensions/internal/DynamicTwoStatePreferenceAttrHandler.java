package com.motorola.extensions.internal;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.motorola.extensions.preference.DynamicTwoStatePreferenceDelegator;

public class DynamicTwoStatePreferenceAttrHandler extends DynamicPreferenceAttrHandler {
    private boolean mIntercept;
    protected int mMaxLines = 1;
    private boolean mOffIntercept;
    private boolean mOnIntercept;
    private CharSequence mSummaryOff;
    private CharSequence mSummaryOn;

    public DynamicTwoStatePreferenceAttrHandler(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void handleAttribute(int i, CharSequence charSequence, AttributeSet attributeSet, int i2) {
        if (i == 10) {
            this.mSummaryOn = getTextFromResource(charSequence, attributeSet.getAttributeResourceValue(i2, 0));
        } else if (i == 11) {
            this.mSummaryOff = getTextFromResource(charSequence, attributeSet.getAttributeResourceValue(i2, 0));
        } else if (i != 30) {
            switch (i) {
                case 24:
                    this.mIntercept = Boolean.parseBoolean(charSequence.toString());
                    return;
                case 25:
                    this.mOnIntercept = Boolean.parseBoolean(charSequence.toString());
                    return;
                case 26:
                    this.mOffIntercept = Boolean.parseBoolean(charSequence.toString());
                    return;
                default:
                    return;
            }
        } else {
            this.mMaxLines = Integer.parseInt(charSequence.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void setValueFromCursor(Preference preference, Cursor cursor, int i) {
        if (i >= 0) {
            ((TwoStatePreference) preference).setChecked(cursor.getInt(i) != 0);
        }
    }

    /* access modifiers changed from: protected */
    public void setProperties(TwoStatePreference twoStatePreference) {
        DynamicTwoStatePreferenceDelegator delegator;
        if (!TextUtils.isEmpty(this.mDataAuthority)) {
            if (!TextUtils.isEmpty(this.mSummaryOn)) {
                if (this.mShowHtmlSummary) {
                    twoStatePreference.setSummaryOn((CharSequence) Html.fromHtml(this.mSummaryOn.toString(), 0));
                } else {
                    twoStatePreference.setSummaryOn(this.mSummaryOn);
                }
            }
            if (!TextUtils.isEmpty(this.mSummaryOff)) {
                if (this.mShowHtmlSummary) {
                    twoStatePreference.setSummaryOff(Html.fromHtml(this.mSummaryOff.toString(), 0));
                } else {
                    twoStatePreference.setSummaryOff(this.mSummaryOff);
                }
            }
            if ((twoStatePreference instanceof DynamicTwoStatePreferenceDelegator.DelegatorHelper) && (delegator = ((DynamicTwoStatePreferenceDelegator.DelegatorHelper) twoStatePreference).getDelegator()) != null) {
                delegator.setInterceptor(this.mIntercept);
                delegator.setOnInterceptor(this.mOnIntercept);
                delegator.setOffInterceptor(this.mOffIntercept);
                delegator.setMaxLines(this.mMaxLines);
                delegator.setPreferenceDataUri(getDynamicValuesUri());
            }
            super.setProperties(twoStatePreference);
            return;
        }
        throw new IllegalArgumentException("dataAuthority attribute is required");
    }
}
