package com.motorola.settings.wifi.tether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.motorola.settings.tether.UnifiedSettingsUtils;
import java.util.Map;

public class WifiTetherWhitelistController extends WifiTetherBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private WifiProgressCategory mAllowedDevices;
    private Context mContext;
    private DeviceEditDialog mDeviceEditDialog;
    private Resources mRes;
    /* access modifiers changed from: private */
    public SharedPreferences mSharedPrefs;

    public String getPreferenceKey() {
        return "wifi_tether_allowed_devices_list";
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    public WifiTetherWhitelistController(Context context, SharedPreferences sharedPreferences) {
        super(context, (WifiTetherBasePreferenceController.OnTetherConfigUpdateListener) null);
        this.mContext = context;
        this.mSharedPrefs = sharedPreferences;
        this.mRes = context.getResources();
    }

    public void updateDisplay() {
        if (this.mAllowedDevices != null) {
            updateAddedDevices(!this.mSharedPrefs.getBoolean("clientControlByUser", false));
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        WifiProgressCategory wifiProgressCategory = (WifiProgressCategory) this.mPreference;
        this.mAllowedDevices = wifiProgressCategory;
        if (wifiProgressCategory != null) {
            wifiProgressCategory.setOrderingAsAdded(true);
            this.mAllowedDevices.removeAll();
        }
        updateDisplay();
    }

    public void preferenceClicked(String str) {
        DeviceEditDialog deviceEditDialog = new DeviceEditDialog(this.mContext, (WifiTetherConnectedDevice) this.mAllowedDevices.findPreference(str));
        this.mDeviceEditDialog = deviceEditDialog;
        deviceEditDialog.show();
    }

    private void updateAddedDevices(boolean z) {
        this.mAllowedDevices.removeAll();
        Map<String, ?> all = this.mSharedPrefs.getAll();
        all.remove("clientControlByUser");
        if (!all.isEmpty()) {
            for (Map.Entry next : all.entrySet()) {
                WifiTetherConnectedDevice wifiTetherConnectedDevice = new WifiTetherConnectedDevice(this.mContext, (String) next.getKey());
                if (((String) next.getValue()).length() == 0) {
                    wifiTetherConnectedDevice.mDeviceName = this.mRes.getString(C1992R$string.wifi_tether_unknown_device);
                    all.put((String) next.getKey(), wifiTetherConnectedDevice.mDeviceName);
                } else {
                    wifiTetherConnectedDevice.mDeviceName = (String) next.getValue();
                }
                wifiTetherConnectedDevice.updateDisplay();
                wifiTetherConnectedDevice.setEnabled(!z);
                this.mAllowedDevices.addPreference(wifiTetherConnectedDevice);
            }
        }
        if (UnifiedSettingsUtils.isVzwUnifiedSettingsInstalledAndEnabled(this.mContext)) {
            UnifiedSettingsUtils.sendUnifiedSettingsBroadcast(this.mContext, "com.motorola.wifi.TETHER_ALLOWED_DEVICES_STATE_CHANGED");
        }
    }

    private class DeviceEditDialog extends AlertDialog implements DialogInterface.OnClickListener, TextWatcher {
        private Context mContext;
        private EditText mDevEntry;
        private WifiTetherConnectedDevice mDevice;
        private EditText mMacEntry;
        private Button mOkButton;
        private Resources mRes;
        private SharedPreferences.Editor mSharedPrefsEditor;
        private View mView;

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        protected DeviceEditDialog(Context context, WifiTetherConnectedDevice wifiTetherConnectedDevice) {
            super(context);
            this.mContext = context;
            this.mDevice = wifiTetherConnectedDevice;
            this.mRes = context.getResources();
            this.mSharedPrefsEditor = WifiTetherWhitelistController.this.mSharedPrefs.edit();
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle bundle) {
            View inflate = getLayoutInflater().inflate(C1987R$layout.wifi_tether_add_device, (ViewGroup) null);
            this.mView = inflate;
            setView(inflate);
            setButton(-1, this.mRes.getString(C1992R$string.okay), this);
            setButton(-2, this.mRes.getString(C1992R$string.cancel), this);
            if (this.mDevice != null) {
                setButton(-3, this.mRes.getString(C1992R$string.delete), this);
                setTitle(this.mRes.getString(C1992R$string.wifi_tether_edit_allowed_device));
            } else {
                setTitle(this.mRes.getString(C1992R$string.wifi_tether_add_allowed_device));
            }
            setInverseBackgroundForced(true);
            this.mMacEntry = (EditText) this.mView.findViewById(C1985R$id.text_view_enter_mac);
            this.mDevEntry = (EditText) this.mView.findViewById(C1985R$id.text_view_enter_name);
            this.mMacEntry.addTextChangedListener(this);
            this.mMacEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View view, boolean z) {
                    DeviceEditDialog.this.getWindow().setSoftInputMode(5);
                }
            });
            super.onCreate(bundle);
            Button button = getButton(-1);
            this.mOkButton = button;
            WifiTetherConnectedDevice wifiTetherConnectedDevice = this.mDevice;
            if (wifiTetherConnectedDevice != null) {
                this.mMacEntry.setText(wifiTetherConnectedDevice.mMACAddress, TextView.BufferType.EDITABLE);
                if (!this.mDevice.mDeviceName.equals(this.mRes.getString(C1992R$string.wifi_tether_unknown_device))) {
                    this.mDevEntry.setText(this.mDevice.mDeviceName, TextView.BufferType.EDITABLE);
                    return;
                }
                return;
            }
            button.setEnabled(false);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                String upperCase = this.mMacEntry.getText().toString().trim().toUpperCase();
                WifiTetherConnectedDevice wifiTetherConnectedDevice = this.mDevice;
                if (wifiTetherConnectedDevice != null) {
                    this.mSharedPrefsEditor.remove(wifiTetherConnectedDevice.mMACAddress);
                } else if (WifiTetherWhitelistController.this.mSharedPrefs.contains(upperCase)) {
                    Toast.makeText(this.mContext, C1992R$string.wifi_tether_mac_exists, 0).show();
                    return;
                }
                String obj = this.mDevEntry.getText().toString();
                this.mSharedPrefsEditor.putBoolean("clientControlByUser", true);
                this.mSharedPrefsEditor.putString(upperCase, obj).apply();
            } else if (i == -3) {
                this.mSharedPrefsEditor.remove(this.mDevice.mMACAddress);
                this.mSharedPrefsEditor.apply();
            }
            dismiss();
        }

        public void afterTextChanged(Editable editable) {
            if (this.mMacEntry.length() < 17 || !this.mMacEntry.getText().toString().trim().matches("([0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}")) {
                this.mOkButton.setEnabled(false);
            } else {
                this.mOkButton.setEnabled(true);
            }
        }
    }
}
