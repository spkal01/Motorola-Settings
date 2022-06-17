package com.motorola.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.gestures.SystemNavigationPreferenceController;
import com.motorola.android.provider.MotorolaSettings;
import java.util.HashMap;

public class GesturePillChooserPreference extends Preference implements View.OnClickListener {
    private final Context mContext;
    private final HashMap<String, RadioButton> mRadios = new HashMap<>();

    public GesturePillChooserPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C1987R$layout.gesture_pill_chooser_preference);
        this.mContext = context;
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        buildView(preferenceViewHolder);
        updateCheckState((View) null);
    }

    private void buildView(PreferenceViewHolder preferenceViewHolder) {
        this.mRadios.put("key_radio_show", (RadioButton) preferenceViewHolder.findViewById(C1985R$id.show_button));
        ((ViewGroup) preferenceViewHolder.findViewById(C1985R$id.show_button_frame)).setOnClickListener(this);
        this.mRadios.put("key_radio_hide", (RadioButton) preferenceViewHolder.findViewById(C1985R$id.hide_button));
        ((ViewGroup) preferenceViewHolder.findViewById(C1985R$id.hide_button_frame)).setOnClickListener(this);
    }

    private void updateCheckState(View view) {
        int i;
        if (view == null) {
            int navBarStatus = getNavBarStatus();
            if (navBarStatus == 0) {
                i = C1985R$id.show_button_frame;
            } else {
                i = navBarStatus == 1 ? C1985R$id.hide_button_frame : -1;
            }
        } else {
            i = view.getId();
        }
        if (i == C1985R$id.show_button_frame) {
            if (setNavBarStatus("key_radio_show")) {
                this.mRadios.get("key_radio_show").setChecked(true);
                this.mRadios.get("key_radio_hide").setChecked(false);
            }
        } else if (i == C1985R$id.hide_button_frame && setNavBarStatus("key_radio_hide")) {
            this.mRadios.get("key_radio_show").setChecked(false);
            this.mRadios.get("key_radio_hide").setChecked(true);
        }
    }

    public void onClick(View view) {
        updateCheckState(view);
    }

    private boolean setNavBarStatus(String str) {
        str.hashCode();
        if (!str.equals("key_radio_hide")) {
            if (str.equals("key_radio_show")) {
                if (SystemNavigationPreferenceController.isOverlayPackageAvailable(this.mContext, "com.android.internal.systemui.navbar.gestural")) {
                    return MotorolaSettings.Secure.putInt(this.mContext.getContentResolver(), "hide_gesture_pill", 0);
                }
            }
            Log.e("GesturePillPickerPreference", "setNavBarStatus: pkg not found or write settings failed");
            return false;
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(this.mContext, "com.android.internal.systemui.navbar.hidegestural")) {
            return MotorolaSettings.Secure.putInt(this.mContext.getContentResolver(), "hide_gesture_pill", 1);
        }
        Log.e("GesturePillPickerPreference", "setNavBarStatus: pkg not found or write settings failed");
        return false;
    }

    private int getNavBarStatus() {
        return MotorolaSettings.Secure.getInt(this.mContext.getContentResolver(), "hide_gesture_pill", 0);
    }
}
