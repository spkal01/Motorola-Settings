package com.motorola.extensions.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.motorola.extensions.internal.DynamicPreferenceAttrHandler;
import java.util.ArrayList;
import java.util.Iterator;

public class DynamicCategoryPreferenceAttrHandler extends DynamicPreferenceAttrHandler {
    private static final String TAG = "DynamicCategoryPreferenceAttrHandler";
    ArrayList<DynamicPreferenceAttrHandler> mChildren = new ArrayList<>();

    public DynamicCategoryPreferenceAttrHandler(Context context) {
        super(context);
        this.mSelectable = false;
    }

    public void addChild(DynamicPreferenceAttrHandler dynamicPreferenceAttrHandler) {
        this.mChildren.add(dynamicPreferenceAttrHandler.prepareChild(this.mOrderFirst, this.mOrderAboveKey, this.mOrderBelowKey, this.mOrderPriority));
    }

    public Preference inflate(Context context, DynamicPreferenceAttrHandler.Holder holder) {
        PreferenceGroup preferenceGroup = holder.root;
        PreferenceCategory preferenceCategory = null;
        if (!TextUtils.isEmpty(this.mTitle)) {
            int styleResourceId = DynamicPreferenceAttrHandler.getStyleResourceId(context, this.mStyle);
            if (styleResourceId != 0) {
                preferenceCategory = new PreferenceCategory(context, (AttributeSet) null, styleResourceId);
            } else {
                preferenceCategory = new PreferenceCategory(context);
            }
            setProperties(preferenceCategory);
        }
        inflateInternal(context, holder, preferenceCategory);
        if (!this.mChildren.isEmpty()) {
            if (preferenceCategory != null) {
                Iterator<DynamicPreferenceAttrHandler> it = this.mChildren.iterator();
                while (it.hasNext()) {
                    DynamicPreferenceAttrHandler next = it.next();
                    try {
                        next.inflate(context, new DynamicPreferenceAttrHandler.Holder(preferenceCategory, holder.addedPrefs, holder.removedPrefs));
                    } catch (Throwable unused) {
                        String str = TAG;
                        Log.w(str, "Failed to inflate '" + next.mKey + "' for the category " + this.mKey);
                    }
                }
            } else {
                DynamicPreferenceAttrHandler.OrderReference orderReference = getOrderReference(preferenceGroup);
                Iterator<DynamicPreferenceAttrHandler> it2 = this.mChildren.iterator();
                while (it2.hasNext()) {
                    DynamicPreferenceAttrHandler next2 = it2.next();
                    if (orderReference != null) {
                        next2.setOrderReference(orderReference);
                        try {
                            Preference inflate = next2.inflate(context, holder);
                            if (inflate != null) {
                                DynamicPreferenceAttrHandler.OrderReference orderReference2 = new DynamicPreferenceAttrHandler.OrderReference(orderReference.getParent(), inflate);
                                try {
                                    orderReference2.setOrderFlag(2);
                                    orderReference = orderReference2;
                                } catch (Throwable unused2) {
                                    orderReference = orderReference2;
                                    String str2 = TAG;
                                    Log.w(str2, "Failed to inflate '" + next2.mKey + "'");
                                }
                            }
                        } catch (Throwable unused3) {
                            String str22 = TAG;
                            Log.w(str22, "Failed to inflate '" + next2.mKey + "'");
                        }
                    } else {
                        next2.inflate(context, holder);
                    }
                }
            }
        }
        return preferenceCategory != null ? preferenceCategory : preferenceGroup;
    }
}
