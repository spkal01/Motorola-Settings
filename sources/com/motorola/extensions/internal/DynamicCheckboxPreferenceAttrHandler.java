package com.motorola.extensions.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.motorola.extensions.internal.DynamicPreferenceAttrHandler;
import com.motorola.extensions.preference.DynamicCheckboxPreference;

public class DynamicCheckboxPreferenceAttrHandler extends DynamicTwoStatePreferenceAttrHandler {
    public String getTypeForPath() {
        return "checkbox_preference";
    }

    public DynamicCheckboxPreferenceAttrHandler(Context context) {
        super(context);
    }

    public Preference inflate(Context context, DynamicPreferenceAttrHandler.Holder holder) {
        DynamicCheckboxPreference dynamicCheckboxPreference;
        PreferenceGroup preferenceGroup = holder.root;
        DynamicCheckboxPreference dynamicCheckboxPreference2 = null;
        if (!TextUtils.isEmpty(this.mTitle)) {
            if (!TextUtils.isEmpty(this.mClassName)) {
                Preference preferenceInstance = PreferenceLoader.getPreferenceInstance(context, this.mClassName.toString());
                if (preferenceInstance instanceof DynamicCheckboxPreference) {
                    dynamicCheckboxPreference = (DynamicCheckboxPreference) preferenceInstance;
                } else {
                    throw new IllegalArgumentException(this.mClassName + " is not an instance of DynamicCheckboxPreference");
                }
            } else {
                int styleResourceId = DynamicPreferenceAttrHandler.getStyleResourceId(context, this.mStyle);
                if (styleResourceId != 0) {
                    dynamicCheckboxPreference2 = new DynamicCheckboxPreference(context, (AttributeSet) null, styleResourceId);
                    setProperties(dynamicCheckboxPreference2);
                } else {
                    dynamicCheckboxPreference = new DynamicCheckboxPreference(context);
                }
            }
            dynamicCheckboxPreference2 = dynamicCheckboxPreference;
            setProperties(dynamicCheckboxPreference2);
        }
        return inflateInternal(context, holder, dynamicCheckboxPreference2);
    }
}
