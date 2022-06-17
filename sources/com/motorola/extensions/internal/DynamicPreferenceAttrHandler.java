package com.motorola.extensions.internal;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.motorola.extensions.preference.DynamicPreference;
import com.motorola.extensions.preference.DynamicPreferenceDataObserver;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DynamicPreferenceAttrHandler {
    protected static final boolean DEBUG = (!Build.IS_USER);
    private static final String RESOURCE_STRING_REFERENCE_PATTERN = Pattern.compile("^[$].+:.+/.+").pattern();
    private static final String TAG = "DynamicPreferenceAttrHandler";
    protected boolean mAutoRefresh;
    protected CharSequence mClassName;
    protected CharSequence mDataAuthority;
    protected CharSequence mDependecyKey;
    protected String[] mDisableKeys;
    protected boolean mEnabled;
    protected CharSequence mFragment;
    protected Drawable mIcon;
    protected Intent mIntent;
    protected CharSequence mKey;
    protected CharSequence mOrderAboveKey;
    protected CharSequence mOrderBelowKey;
    protected boolean mOrderFirst;
    protected int mOrderPriority = 0;
    protected OrderReference mOrderReference;
    protected String[] mRedirectLinkages;
    protected String[] mRemoveKeys;
    protected CharSequence mReplaceKey;
    protected boolean mSelectable;
    protected boolean mShowHtmlSummary;
    protected boolean mShowHtmlTitle;
    protected CharSequence mStyle;
    protected CharSequence mSummary;
    protected final Context mTargetContext;
    protected CharSequence mTitle;
    protected boolean mVisible;

    public String getTypeForPath() {
        return "preference";
    }

    /* access modifiers changed from: protected */
    public void handleAttribute(int i, CharSequence charSequence, AttributeSet attributeSet, int i2) {
    }

    public static class Holder {
        public final ArrayList<Preference> addedPrefs;
        public final ArrayList<Preference> removedPrefs;
        public final PreferenceGroup root;

        public Holder(PreferenceGroup preferenceGroup, ArrayList<Preference> arrayList, ArrayList<Preference> arrayList2) {
            this.root = preferenceGroup;
            this.addedPrefs = arrayList;
            this.removedPrefs = arrayList2;
        }
    }

    public DynamicPreferenceAttrHandler(Context context) {
        this.mTargetContext = context;
        this.mEnabled = true;
        this.mVisible = true;
        this.mSelectable = true;
    }

    public final void readAttrs(AttributeSet attributeSet) {
        for (int i = 0; i < attributeSet.getAttributeCount(); i++) {
            String attributeValue = attributeSet.getAttributeValue(i);
            String attributeName = attributeSet.getAttributeName(i);
            if (DEBUG) {
                Log.d(TAG, attributeName + " = " + attributeValue);
            }
            if (!TextUtils.isEmpty(attributeName) && !TextUtils.isEmpty(attributeValue)) {
                String trim = attributeValue.toString().trim();
                int attrCode = DynamicPreferenceInflater.getAttrCode(attributeName);
                switch (attrCode) {
                    case 1:
                        this.mTitle = getTextFromResource(trim, attributeSet.getAttributeResourceValue(i, 0));
                        break;
                    case 2:
                        this.mSummary = getTextFromResource(trim, attributeSet.getAttributeResourceValue(i, 0));
                        break;
                    case 3:
                        this.mKey = trim;
                        break;
                    case 4:
                        this.mReplaceKey = trim;
                        break;
                    case 5:
                        this.mOrderAboveKey = trim;
                        break;
                    case 6:
                        this.mOrderBelowKey = trim;
                        break;
                    case 7:
                        this.mIcon = getDrawableFromResource(trim, attributeSet.getAttributeResourceValue(i, 0));
                        break;
                    case 8:
                        this.mRemoveKeys = trim.toString().split("\\|");
                        break;
                    case 9:
                        this.mOrderFirst = Boolean.parseBoolean(trim.toString());
                        break;
                    case 12:
                        this.mDataAuthority = trim;
                        break;
                    case 13:
                        this.mEnabled = Boolean.parseBoolean(trim.toString());
                        break;
                    case 14:
                        this.mDependecyKey = trim;
                        break;
                    case 22:
                        this.mStyle = trim;
                        break;
                    case 23:
                        this.mAutoRefresh = Boolean.parseBoolean(trim.toString());
                        break;
                    case 27:
                        this.mSelectable = Boolean.parseBoolean(trim.toString());
                        break;
                    case 28:
                        this.mClassName = trim;
                        break;
                    case 29:
                        this.mRedirectLinkages = trim.toString().split("\\|");
                        break;
                    case 31:
                        try {
                            this.mOrderPriority = Integer.parseInt(trim.toString());
                            break;
                        } catch (NumberFormatException unused) {
                            this.mOrderPriority = 0;
                            break;
                        }
                    case 32:
                        this.mDisableKeys = trim.toString().split("\\|");
                        break;
                    case 33:
                        this.mFragment = trim;
                        break;
                    case 34:
                        this.mShowHtmlTitle = Boolean.parseBoolean(trim.toString());
                        break;
                    case 35:
                        this.mShowHtmlSummary = Boolean.parseBoolean(trim.toString());
                        break;
                    default:
                        handleAttribute(attrCode, trim, attributeSet, i);
                        break;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public CharSequence getTextFromResource(CharSequence charSequence, int i) {
        Resources resources = this.mTargetContext.getResources();
        if (i > 0) {
            if (DEBUG) {
                Log.d(TAG, "getText(valueResId) = " + resources.getText(i));
            }
            return resources.getText(i);
        }
        int identifier = resources.getIdentifier(charSequence.toString(), "string", this.mTargetContext.getPackageName());
        if (identifier == 0) {
            String charSequence2 = charSequence.toString();
            if (charSequence2.matches(RESOURCE_STRING_REFERENCE_PATTERN)) {
                String[] split = charSequence2.split(":", 2);
                String substring = split[0].substring(1);
                String[] split2 = split[1].split("/", 2);
                String str = "@" + split2[0] + "/" + split2[1];
                try {
                    resources = this.mTargetContext.createPackageContext(substring, 0).getResources();
                    identifier = resources.getIdentifier(str, split2[0], substring);
                } catch (Exception unused) {
                }
                charSequence = str;
            }
        }
        return resources.getText(identifier, charSequence);
    }

    /* access modifiers changed from: protected */
    public Drawable getDrawableFromResource(CharSequence charSequence, int i) {
        Resources resources = this.mTargetContext.getResources();
        if (i > 0) {
            return this.mTargetContext.getDrawable(i);
        }
        int identifier = resources.getIdentifier(charSequence.toString(), "drawable", this.mTargetContext.getPackageName());
        if (identifier == 0) {
            return null;
        }
        try {
            return this.mTargetContext.getDrawable(identifier);
        } catch (Resources.NotFoundException unused) {
            return null;
        }
    }

    public void setIntent(Intent intent) {
        this.mIntent = intent;
    }

    /* access modifiers changed from: protected */
    public void handleDisable(Holder holder) {
        PreferenceGroup preferenceGroup = holder.root;
        String[] strArr = this.mDisableKeys;
        if (strArr != null) {
            for (String findPreferenceAndParent : strArr) {
                OrderReference findPreferenceAndParent2 = findPreferenceAndParent(preferenceGroup, findPreferenceAndParent);
                if (findPreferenceAndParent2 != null) {
                    findPreferenceAndParent2.get().setEnabled(false);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleRemove(Holder holder) {
        OrderReference findPreferenceAndParent;
        PreferenceGroup preferenceGroup = holder.root;
        if (!TextUtils.isEmpty(this.mReplaceKey) && this.mVisible && (findPreferenceAndParent = findPreferenceAndParent(preferenceGroup, this.mReplaceKey)) != null) {
            removePreference(holder, findPreferenceAndParent);
            this.mOrderReference = findPreferenceAndParent;
        }
        String[] strArr = this.mRemoveKeys;
        if (strArr != null) {
            for (String findPreferenceAndParent2 : strArr) {
                OrderReference findPreferenceAndParent3 = findPreferenceAndParent(preferenceGroup, findPreferenceAndParent2);
                if (findPreferenceAndParent3 != null) {
                    removePreference(holder, findPreferenceAndParent3);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleRedirectLinkages(PreferenceGroup preferenceGroup) {
        String[] strArr;
        if (!TextUtils.isEmpty(this.mReplaceKey) && !TextUtils.isEmpty(this.mKey) && (strArr = this.mRedirectLinkages) != null) {
            for (String findPreference : strArr) {
                Preference findPreference2 = preferenceGroup.findPreference(findPreference);
                if (findPreference2 != null) {
                    findPreference2.setDependency(this.mTargetContext.getPackageName() + "." + this.mKey.toString());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void removePreference(Holder holder, OrderReference orderReference) {
        String dependency;
        PreferenceGroup preferenceGroup = holder.root;
        Preference preference = orderReference.get();
        if (preference.hasKey()) {
            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                Preference preference2 = preferenceGroup.getPreference(i);
                if (!(preference2 == null || (dependency = preference2.getDependency()) == null || !dependency.equals(preference.getKey()))) {
                    preference2.setDependency((String) null);
                    preference2.setEnabled(true);
                    preference2.onDependencyChanged((Preference) null, false);
                }
            }
        }
        orderReference.getParent().removePreference(preference);
        ArrayList<Preference> arrayList = holder.removedPrefs;
        if (arrayList != null) {
            arrayList.add(preference);
        }
    }

    /* access modifiers changed from: protected */
    public void setProperties(Preference preference) {
        boolean z = false;
        preference.setPersistent(false);
        if (this.mShowHtmlTitle) {
            preference.setTitle((CharSequence) Html.fromHtml(this.mTitle.toString(), 0));
        } else {
            preference.setTitle(this.mTitle);
        }
        if (this.mOrderFirst) {
            Bundle extras = preference.getExtras();
            extras.putBoolean("orderFirst", true);
            extras.putInt("orderPriority", this.mOrderPriority);
        } else if (!TextUtils.isEmpty(this.mOrderAboveKey)) {
            Bundle extras2 = preference.getExtras();
            extras2.putCharSequence("orderRefKey", this.mOrderAboveKey);
            extras2.putInt("orderPriority", this.mOrderPriority);
        } else if (!TextUtils.isEmpty(this.mOrderBelowKey)) {
            Bundle extras3 = preference.getExtras();
            extras3.putCharSequence("orderRefKey", this.mOrderBelowKey);
            extras3.putInt("orderPriority", this.mOrderPriority);
        }
        if (!TextUtils.isEmpty(this.mKey)) {
            preference.setKey(this.mTargetContext.getPackageName() + "." + this.mKey);
        } else {
            preference.setKey(this.mTargetContext.getPackageName());
        }
        if (!TextUtils.isEmpty(this.mSummary)) {
            if (this.mShowHtmlSummary) {
                preference.setSummary((CharSequence) Html.fromHtml(this.mSummary.toString(), 0));
            } else {
                preference.setSummary(this.mSummary);
            }
        }
        if (!TextUtils.isEmpty(this.mFragment)) {
            preference.setFragment(this.mFragment.toString());
        } else {
            Intent intent = this.mIntent;
            if (intent != null) {
                intent.setFlags(intent.getFlags() | 268435456);
                preference.setIntent(this.mIntent);
            }
        }
        Drawable drawable = this.mIcon;
        if (drawable != null) {
            preference.setIcon(drawable);
        }
        preference.setEnabled(this.mEnabled);
        preference.setSelectable(this.mSelectable);
        Uri dynamicValuesUri = getDynamicValuesUri();
        if (dynamicValuesUri != null) {
            Cursor query = preference.getContext().getContentResolver().query(dynamicValuesUri, (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        int columnIndex = query.getColumnIndex("visible");
                        if (columnIndex >= 0) {
                            boolean z2 = query.getInt(columnIndex) != 0;
                            this.mVisible = z2;
                            preference.setVisible(z2);
                        }
                        int columnIndex2 = query.getColumnIndex("enabled");
                        if (columnIndex2 >= 0) {
                            if (query.getInt(columnIndex2) != 0) {
                                z = true;
                            }
                            preference.setEnabled(z);
                        }
                        int columnIndex3 = query.getColumnIndex("summary");
                        if (columnIndex3 >= 0) {
                            setSummaryFromCursor(preference, query, columnIndex3);
                        }
                        int columnIndex4 = query.getColumnIndex("value");
                        if (columnIndex4 >= 0) {
                            setValueFromCursor(preference, query, columnIndex4);
                        }
                        query.close();
                        return;
                    }
                } catch (Throwable th) {
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
            throw new IllegalArgumentException("'" + this.mDataAuthority + "' is invalid for '" + this.mKey + "'");
        }
    }

    /* access modifiers changed from: protected */
    public Preference inflateInternal(Context context, Holder holder, Preference preference) {
        boolean z;
        boolean z2;
        Uri dynamicValuesUri = getDynamicValuesUri();
        if (preference == null || dynamicValuesUri == null || !(z2 = this.mAutoRefresh) || !(preference instanceof DynamicPreferenceDataObserver.IAutoRefresh)) {
            z = false;
        } else {
            ((DynamicPreferenceDataObserver.IAutoRefresh) preference).setAutoRefresh(dynamicValuesUri, z2);
            Uri.Builder buildUpon = dynamicValuesUri.buildUpon();
            buildUpon.appendQueryParameter("listen", "true");
            context.getContentResolver().update(buildUpon.build(), new ContentValues(), (String) null, (String[]) null);
            z = true;
        }
        if (!this.mVisible && !z) {
            return null;
        }
        PreferenceGroup preferenceGroup = holder.root;
        handleRemove(holder);
        handleDisable(holder);
        if (preference == null) {
            return null;
        }
        OrderReference orderReference = getOrderReference(preferenceGroup);
        if (orderReference != null) {
            preferenceGroup = orderReference.getParent();
            preference.setOrder(getOrderWithRearrangement(orderReference));
        }
        preferenceGroup.addPreference(preference);
        ArrayList<Preference> arrayList = holder.addedPrefs;
        if (arrayList != null) {
            arrayList.add(preference);
        }
        if (!TextUtils.isEmpty(this.mDependecyKey)) {
            preference.setDependency(this.mDependecyKey.toString());
        }
        handleRedirectLinkages(preferenceGroup);
        return preference;
    }

    /* access modifiers changed from: protected */
    public Uri getDynamicValuesUri() {
        if (TextUtils.isEmpty(this.mDataAuthority) || TextUtils.isEmpty(this.mKey)) {
            return null;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority(this.mDataAuthority.toString());
        builder.appendPath(getTypeForPath());
        builder.appendPath(this.mKey.toString());
        return builder.build();
    }

    /* access modifiers changed from: protected */
    public void setValueFromCursor(Preference preference, Cursor cursor, int i) {
        setSummaryFromCursor(preference, cursor, i);
    }

    /* access modifiers changed from: protected */
    public void setSummaryFromCursor(Preference preference, Cursor cursor, int i) {
        String string;
        if (i >= 0 && (string = cursor.getString(i)) != null) {
            if (this.mShowHtmlSummary) {
                preference.setSummary((CharSequence) Html.fromHtml(string, 0));
            } else {
                preference.setSummary((CharSequence) string);
            }
        }
    }

    protected static int getStyleResourceId(Context context, CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return 0;
        }
        String charSequence2 = charSequence.toString();
        if (charSequence2.startsWith("@") || charSequence2.startsWith("?")) {
            charSequence2 = charSequence2.substring(1);
        }
        return context.getResources().getIdentifier(charSequence2, (String) null, context.getPackageName());
    }

    public Preference inflate(Context context, Holder holder) {
        Preference dynamicPreference;
        PreferenceGroup preferenceGroup = holder.root;
        DynamicPreference dynamicPreference2 = null;
        if (!TextUtils.isEmpty(this.mTitle)) {
            if (!TextUtils.isEmpty(this.mClassName)) {
                dynamicPreference = PreferenceLoader.getPreferenceInstance(context, this.mClassName.toString());
            } else {
                int styleResourceId = getStyleResourceId(context, this.mStyle);
                if (styleResourceId != 0) {
                    dynamicPreference2 = new DynamicPreference(context, (AttributeSet) null, styleResourceId);
                    setProperties(dynamicPreference2);
                } else {
                    dynamicPreference = new DynamicPreference(context);
                }
            }
            dynamicPreference2 = dynamicPreference;
            setProperties(dynamicPreference2);
        }
        return inflateInternal(context, holder, dynamicPreference2);
    }

    public DynamicPreferenceAttrHandler prepareChild(boolean z, CharSequence charSequence, CharSequence charSequence2, int i) {
        this.mRemoveKeys = null;
        this.mReplaceKey = null;
        this.mOrderAboveKey = charSequence;
        this.mOrderBelowKey = charSequence2;
        this.mOrderFirst = z;
        this.mOrderReference = null;
        this.mRedirectLinkages = null;
        this.mOrderPriority = i;
        this.mDisableKeys = null;
        return this;
    }

    public void setOrderReference(OrderReference orderReference) {
        this.mOrderReference = orderReference;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00e0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.motorola.extensions.internal.DynamicPreferenceAttrHandler.OrderReference getOrderReference(androidx.preference.PreferenceGroup r12) {
        /*
            r11 = this;
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r0 = r11.mOrderReference
            if (r0 != 0) goto L_0x00e5
            int r0 = r12.getPreferenceCount()
            if (r0 <= 0) goto L_0x00e5
            r0 = 0
            boolean r1 = r11.mOrderFirst
            r2 = 2
            java.lang.String r3 = "orderPriority"
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x0046
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r0 = new com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference
            androidx.preference.Preference r1 = r12.getPreference(r5)
            r0.<init>(r12, r1, r5)
            androidx.preference.Preference r1 = r0.mPreference
            if (r1 == 0) goto L_0x0043
            android.os.Bundle r1 = r1.peekExtras()
            if (r1 == 0) goto L_0x0043
            java.lang.String r6 = "orderFirst"
            boolean r6 = r1.getBoolean(r6, r5)
            if (r6 == 0) goto L_0x0043
            int r1 = r1.getInt(r3, r5)
            int r3 = r11.mOrderPriority
            if (r3 >= r1) goto L_0x0043
            androidx.preference.Preference r0 = r0.mPreference
            java.lang.String r0 = r0.getKey()
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r0 = findPreferenceAndParent(r12, r0)
            goto L_0x00de
        L_0x0043:
            r2 = r4
            goto L_0x00de
        L_0x0046:
            java.lang.CharSequence r1 = r11.mOrderAboveKey
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            java.lang.String r6 = "orderRefKey"
            if (r1 != 0) goto L_0x0090
            java.lang.CharSequence r0 = r11.mOrderAboveKey
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r0 = findPreferenceAndParent(r12, r0)
            if (r0 == 0) goto L_0x0090
            int r1 = r0.mIndex
            int r1 = r1 - r4
        L_0x005b:
            if (r1 < 0) goto L_0x0090
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r7 = new com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference
            androidx.preference.Preference r8 = r12.getPreference(r1)
            r7.<init>(r12, r8, r1)
            androidx.preference.Preference r8 = r7.mPreference
            if (r8 == 0) goto L_0x0089
            android.os.Bundle r8 = r8.peekExtras()
            if (r8 == 0) goto L_0x0089
            java.lang.CharSequence r9 = r8.getCharSequence(r6)
            if (r9 == 0) goto L_0x0089
            java.lang.CharSequence r10 = r11.mOrderAboveKey
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x0089
            int r8 = r8.getInt(r3, r5)
            int r9 = r11.mOrderPriority
            if (r9 >= r8) goto L_0x0089
            r0 = r7
            r7 = r4
            goto L_0x008a
        L_0x0089:
            r7 = r5
        L_0x008a:
            if (r7 != 0) goto L_0x008d
            goto L_0x0090
        L_0x008d:
            int r1 = r1 + -1
            goto L_0x005b
        L_0x0090:
            if (r0 != 0) goto L_0x0043
            java.lang.CharSequence r1 = r11.mOrderBelowKey
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0043
            java.lang.CharSequence r0 = r11.mOrderBelowKey
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r0 = findPreferenceAndParent(r12, r0)
            if (r0 == 0) goto L_0x0043
            int r1 = r0.mIndex
            int r1 = r1 + r4
        L_0x00a5:
            int r7 = r12.getPreferenceCount()
            if (r1 >= r7) goto L_0x00de
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r7 = new com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference
            androidx.preference.Preference r8 = r12.getPreference(r1)
            r7.<init>(r12, r8, r1)
            androidx.preference.Preference r8 = r7.mPreference
            if (r8 == 0) goto L_0x00d7
            android.os.Bundle r8 = r8.peekExtras()
            if (r8 == 0) goto L_0x00d7
            java.lang.CharSequence r9 = r8.getCharSequence(r6)
            if (r9 == 0) goto L_0x00d7
            java.lang.CharSequence r10 = r11.mOrderBelowKey
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x00d7
            int r8 = r8.getInt(r3, r5)
            int r9 = r11.mOrderPriority
            if (r9 >= r8) goto L_0x00d7
            r0 = r7
            r7 = r4
            goto L_0x00d8
        L_0x00d7:
            r7 = r5
        L_0x00d8:
            if (r7 != 0) goto L_0x00db
            goto L_0x00de
        L_0x00db:
            int r1 = r1 + 1
            goto L_0x00a5
        L_0x00de:
            if (r0 == 0) goto L_0x00e5
            r0.setOrderFlag(r2)
            r11.mOrderReference = r0
        L_0x00e5:
            com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference r11 = r11.mOrderReference
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.extensions.internal.DynamicPreferenceAttrHandler.getOrderReference(androidx.preference.PreferenceGroup):com.motorola.extensions.internal.DynamicPreferenceAttrHandler$OrderReference");
    }

    private static int getOrderWithRearrangement(OrderReference orderReference) {
        PreferenceGroup parent = orderReference.getParent();
        int order = orderReference.get().getOrder();
        int i = orderReference.mIndex;
        int orderFlag = orderReference.getOrderFlag();
        if (orderFlag == 0) {
            return order;
        }
        if (orderFlag == 2) {
            i++;
            order++;
        }
        while (i < parent.getPreferenceCount()) {
            Preference preference = parent.getPreference(i);
            preference.setOrder(preference.getOrder() + 1);
            i++;
        }
        return order;
    }

    protected static OrderReference findPreferenceAndParent(PreferenceGroup preferenceGroup, CharSequence charSequence) {
        OrderReference findPreferenceAndParent;
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            String key = preference.getKey();
            if (key != null && key.equals(charSequence)) {
                return new OrderReference(preferenceGroup, preference, i);
            }
            if ((preference instanceof PreferenceGroup) && (findPreferenceAndParent = findPreferenceAndParent((PreferenceGroup) preference, charSequence)) != null) {
                return findPreferenceAndParent;
            }
        }
        return null;
    }

    protected static class OrderReference {
        int mIndex;
        int mOrderFlag = 0;
        PreferenceGroup mParent;
        Preference mPreference;

        OrderReference(PreferenceGroup preferenceGroup, Preference preference, int i) {
            this.mParent = preferenceGroup;
            this.mPreference = preference;
            this.mIndex = i;
        }

        OrderReference(PreferenceGroup preferenceGroup, Preference preference) {
            this.mParent = preferenceGroup;
            this.mPreference = preference;
            int i = 0;
            String key = preference.getKey();
            while (i < preferenceGroup.getPreferenceCount()) {
                Preference preference2 = preferenceGroup.getPreference(i);
                if ((key == null && preference2 == preference) || (key != null && preference2.getKey() != null && key.equals(preference2.getKey()))) {
                    break;
                }
                i++;
            }
            this.mIndex = i;
        }

        public void setOrderFlag(int i) {
            if (i == 0 || i == 1 || i == 2) {
                this.mOrderFlag = i;
            } else {
                this.mOrderFlag = 0;
            }
        }

        public int getOrderFlag() {
            return this.mOrderFlag;
        }

        public Preference get() {
            return this.mPreference;
        }

        public PreferenceGroup getParent() {
            return this.mParent;
        }
    }
}
