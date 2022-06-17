package com.motorola.extensions.internal;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.motorola.extensions.internal.DynamicPreferenceAttrHandler;

public class DynamicListPreferenceAttrHandler extends DynamicPreferenceAttrHandler {
    /* access modifiers changed from: private */
    public static final String TAG = "DynamicListPreferenceAttrHandler";
    private Drawable mDialogIcon;
    private CharSequence mDialogTitle;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private CharSequence[] mSummaryEntries;

    public String getTypeForPath() {
        return "list_preference";
    }

    public DynamicListPreferenceAttrHandler(Context context) {
        super(context);
    }

    private CharSequence[] getArrayFromResource(String str, int i) {
        Resources resources = this.mTargetContext.getResources();
        if (i > 0) {
            return resources.getTextArray(i);
        }
        int identifier = resources.getIdentifier(str, "array", this.mTargetContext.getPackageName());
        if (identifier != 0) {
            return resources.getTextArray(identifier);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void handleAttribute(int i, CharSequence charSequence, AttributeSet attributeSet, int i2) {
        if (!TextUtils.isEmpty(charSequence)) {
            switch (i) {
                case 15:
                    this.mEntries = getArrayFromResource(charSequence.toString(), attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                case 16:
                    this.mEntryValues = getArrayFromResource(charSequence.toString(), attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                case 17:
                    this.mDialogTitle = getTextFromResource(charSequence, attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                case 18:
                    this.mDialogIcon = getDrawableFromResource(charSequence, attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                case 19:
                    this.mPositiveButtonText = getTextFromResource(charSequence, attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                case 20:
                    this.mNegativeButtonText = getTextFromResource(charSequence, attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                case 21:
                    this.mSummaryEntries = getArrayFromResource(charSequence.toString(), attributeSet.getAttributeResourceValue(i2, 0));
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r2 = (androidx.preference.ListPreference) r2;
        r3 = r3.getString(r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setValueFromCursor(androidx.preference.Preference r2, android.database.Cursor r3, int r4) {
        /*
            r1 = this;
            if (r4 < 0) goto L_0x001d
            androidx.preference.ListPreference r2 = (androidx.preference.ListPreference) r2
            java.lang.String r3 = r3.getString(r4)
            int r4 = r2.findIndexOfValue(r3)
            if (r4 < 0) goto L_0x001d
            java.lang.CharSequence[] r0 = r1.mSummaryEntries
            int r0 = r0.length
            if (r4 >= r0) goto L_0x001d
            r2.setValue(r3)
            java.lang.CharSequence[] r1 = r1.mSummaryEntries
            r1 = r1[r4]
            r2.setSummary(r1)
        L_0x001d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.extensions.internal.DynamicListPreferenceAttrHandler.setValueFromCursor(androidx.preference.Preference, android.database.Cursor, int):void");
    }

    private void setProperties(ListPreference listPreference) {
        CharSequence[] charSequenceArr;
        CharSequence[] charSequenceArr2 = this.mEntries;
        if (!(charSequenceArr2 == null || (charSequenceArr = this.mEntryValues) == null)) {
            if (charSequenceArr2.length == charSequenceArr.length) {
                listPreference.setEntries(charSequenceArr2);
                listPreference.setEntryValues(this.mEntryValues);
                CharSequence[] charSequenceArr3 = this.mSummaryEntries;
                if (charSequenceArr3 == null || charSequenceArr3.length != this.mEntries.length) {
                    this.mSummaryEntries = this.mEntries;
                } else {
                    listPreference.getExtras().putCharSequenceArray("summaryEntries", this.mSummaryEntries);
                }
            } else {
                throw new IllegalArgumentException("15 array length " + this.mEntries.length + "does not match the " + 15 + " array length " + this.mEntryValues.length);
            }
        }
        if (!TextUtils.isEmpty(this.mDataAuthority)) {
            listPreference.setDialogTitle(this.mDialogTitle);
            listPreference.setDialogIcon(this.mDialogIcon);
            if (!TextUtils.isEmpty(this.mPositiveButtonText)) {
                listPreference.setPositiveButtonText(this.mPositiveButtonText);
            }
            if (!TextUtils.isEmpty(this.mNegativeButtonText)) {
                listPreference.setNegativeButtonText(this.mNegativeButtonText);
            }
            super.setProperties(listPreference);
            listPreference.setOnPreferenceChangeListener(new DynamicListOnPreferenceChangeListener(getDynamicValuesUri()));
            return;
        }
        throw new IllegalArgumentException("dataAuthority attribute is required");
    }

    public Preference inflate(Context context, DynamicPreferenceAttrHandler.Holder holder) {
        ListPreference listPreference;
        CharSequence[] charSequenceArr;
        PreferenceGroup preferenceGroup = holder.root;
        if (!TextUtils.isEmpty(this.mTitle)) {
            CharSequence[] charSequenceArr2 = this.mEntries;
            if (charSequenceArr2 == null || (charSequenceArr = this.mEntryValues) == null || charSequenceArr2.length != charSequenceArr.length) {
                throw new IllegalArgumentException("DynamicListPreference requires an 15 array and an 16 array");
            }
            if (!TextUtils.isEmpty(this.mClassName)) {
                Preference preferenceInstance = PreferenceLoader.getPreferenceInstance(context, this.mClassName.toString());
                if (preferenceInstance instanceof ListPreference) {
                    listPreference = (ListPreference) preferenceInstance;
                } else {
                    throw new IllegalArgumentException(this.mClassName + " is not an instance of ListPreference");
                }
            } else {
                listPreference = new ListPreference(context);
            }
            setProperties(listPreference);
        } else {
            listPreference = null;
        }
        return inflateInternal(context, holder, listPreference);
    }

    private static class DynamicListOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        private final Uri mDataUri;

        public DynamicListOnPreferenceChangeListener(Uri uri) {
            this.mDataUri = uri;
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            CharSequence[] charSequenceArr;
            int i;
            try {
                long nanoTime = System.nanoTime();
                String str = (String) obj;
                ListPreference listPreference = (ListPreference) preference;
                CharSequence[] entries = listPreference.getEntries();
                Bundle extras = listPreference.getExtras();
                if (!extras.containsKey("summaryEntries") || (charSequenceArr = extras.getCharSequenceArray("summaryEntries")) == null || charSequenceArr.length != entries.length) {
                    charSequenceArr = entries;
                }
                int findIndexOfValue = listPreference.findIndexOfValue(str);
                if (findIndexOfValue < 0 || findIndexOfValue >= entries.length) {
                    i = 0;
                } else if (this.mDataUri != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("value", str);
                    i = preference.getContext().getContentResolver().update(this.mDataUri, contentValues, (String) null, (String[]) null);
                } else {
                    i = 1;
                }
                if (DynamicPreferenceAttrHandler.DEBUG) {
                    Log.d(DynamicListPreferenceAttrHandler.TAG, "Time taken for update = " + (((double) (System.nanoTime() - nanoTime)) / 1000000.0d) + "ms");
                }
                if (i > 0) {
                    listPreference.setSummary(charSequenceArr[findIndexOfValue]);
                    return true;
                }
            } catch (Throwable th) {
                Log.w(DynamicListPreferenceAttrHandler.TAG, "Error on update", th);
            }
            return false;
        }
    }
}
