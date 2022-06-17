package com.motorola.extensions.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.motorola.extensions.internal.DynamicPreferenceAttrHandler;
import com.motorola.extensions.preference.DynamicSwitchPreference;

public class DynamicSwitchPreferenceAttrHandler extends DynamicTwoStatePreferenceAttrHandler {
    public String getTypeForPath() {
        return "checkbox_preference";
    }

    public DynamicSwitchPreferenceAttrHandler(Context context) {
        super(context);
    }

    public Preference inflate(Context context, DynamicPreferenceAttrHandler.Holder holder) {
        DynamicSwitchPreference dynamicSwitchPreference;
        PreferenceGroup preferenceGroup = holder.root;
        DynamicSwitchPreference dynamicSwitchPreference2 = null;
        if (!TextUtils.isEmpty(this.mTitle)) {
            if (!TextUtils.isEmpty(this.mClassName)) {
                Preference preferenceInstance = PreferenceLoader.getPreferenceInstance(context, this.mClassName.toString());
                if (preferenceInstance instanceof DynamicSwitchPreference) {
                    dynamicSwitchPreference = (DynamicSwitchPreference) preferenceInstance;
                } else {
                    throw new IllegalArgumentException(this.mClassName + " is not an instance of DynamicSwitchPreference");
                }
            } else {
                int styleResourceId = DynamicPreferenceAttrHandler.getStyleResourceId(context, this.mStyle);
                if (styleResourceId != 0) {
                    dynamicSwitchPreference2 = new DynamicSwitchPreference(context, (AttributeSet) null, styleResourceId);
                    setProperties(dynamicSwitchPreference2);
                } else {
                    dynamicSwitchPreference = new DynamicSwitchPreference(context);
                }
            }
            dynamicSwitchPreference2 = dynamicSwitchPreference;
            setProperties(dynamicSwitchPreference2);
        }
        return inflateInternal(context, holder, dynamicSwitchPreference2);
    }
}
