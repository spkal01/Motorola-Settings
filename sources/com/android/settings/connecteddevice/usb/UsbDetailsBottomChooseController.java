package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;

public class UsbDetailsBottomChooseController extends UsbDetailsController implements Preference.OnPreferenceClickListener {
    private PreferenceCategory mPreferenceCategory;
    private SwitchPreference mSwitchPreference;

    public String getPreferenceKey() {
        return "usb_bottom_choose";
    }

    public UsbDetailsBottomChooseController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        SwitchPreference switchPreference = new SwitchPreference(preferenceCategory.getContext());
        this.mSwitchPreference = switchPreference;
        switchPreference.setTitle(C1992R$string.usb_bottom_choose_switch_title);
        this.mSwitchPreference.setOnPreferenceClickListener(this);
        this.mSwitchPreference.setSummary(C1992R$string.usb_bottom_choose_switch_desc);
        this.mPreferenceCategory.addPreference(this.mSwitchPreference);
        if (isFactoryMode()) {
            preferenceScreen.removePreference(this.mPreferenceCategory);
        }
        refresh(false, this.mUsbBackend.getDefaultUsbFunctions(), 0, 0);
    }

    /* access modifiers changed from: protected */
    public void refresh(boolean z, long j, int i, int i2) {
        this.mSwitchPreference.setChecked(SystemProperties.getBoolean("persist.sys.usb.bottom_choose", true));
        this.mPreferenceCategory.setEnabled(z);
    }

    public boolean onPreferenceClick(Preference preference) {
        SystemProperties.set("persist.sys.usb.bottom_choose", Boolean.toString(this.mSwitchPreference.isChecked()));
        return true;
    }

    public boolean isAvailable() {
        return !Utils.isMonkeyRunning();
    }

    private boolean isFactoryMode() {
        String str = SystemProperties.get("ro.bootmode", "unknown");
        boolean z = SystemProperties.getInt("ro.vendor.build.motfactory", 0) == 1;
        if ("factory".equalsIgnoreCase(str) || "mot-factory".equalsIgnoreCase(str) || z) {
            return true;
        }
        return false;
    }
}
