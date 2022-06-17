package com.motorola.settings.wifi.tether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TetheredClient;
import android.net.wifi.WifiClient;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import java.util.HashMap;
import java.util.List;

public class WifiTetherConnectedDevicesController {
    private WifiProgressCategory mConnectedDevices;
    private HashMap<String, WifiTetherConnectedDevice> mConnectedDevicesMap = new HashMap<>();
    /* access modifiers changed from: private */
    public Context mContext;
    private DeviceDetailDialog mDeviceDetailsDialog;

    public WifiTetherConnectedDevicesController(Context context, WifiProgressCategory wifiProgressCategory) {
        this.mConnectedDevices = wifiProgressCategory;
        this.mContext = context;
        if (this.mConnectedDevices != null) {
            Preference preference = new Preference(context);
            preference.setLayoutResource(C1987R$layout.preference_empty_list);
            preference.setTitle(C1992R$string.wifi_tether_no_devices_connected);
            preference.setSelectable(false);
            this.mConnectedDevices.setEmptyPreference(preference);
            this.mConnectedDevices.setOrderingAsAdded(true);
            this.mConnectedDevices.removeAll();
        }
    }

    public void addOrUpdateDevice(TetheredClient tetheredClient) {
        if (this.mConnectedDevices != null && this.mContext != null && tetheredClient != null && tetheredClient.getTetheringType() == 0 && tetheredClient.getMacAddress() != null) {
            WifiTetherConnectedDevice connectedDevicePreference = getConnectedDevicePreference(tetheredClient.getMacAddress().toString().toUpperCase());
            List addresses = tetheredClient.getAddresses();
            if (addresses.size() > 0) {
                TetheredClient.AddressInfo addressInfo = (TetheredClient.AddressInfo) addresses.get(0);
                connectedDevicePreference.mIPAddress = addressInfo.getAddress().toString();
                connectedDevicePreference.mDeviceName = addressInfo.getHostname();
            }
            connectedDevicePreference.updateDisplay();
            this.mConnectedDevicesMap.put(connectedDevicePreference.getKey(), connectedDevicePreference);
        }
    }

    public void addOrUpdateDevices(List<WifiClient> list) {
        WifiProgressCategory wifiProgressCategory = this.mConnectedDevices;
        if (wifiProgressCategory != null && this.mContext != null && list != null) {
            wifiProgressCategory.removeAll();
            clearDialog();
            if (list.size() == 0) {
                this.mConnectedDevicesMap.clear();
                return;
            }
            for (WifiClient macAddress : list) {
                String upperCase = macAddress.getMacAddress().toString().toUpperCase();
                WifiTetherConnectedDevice wifiTetherConnectedDevice = this.mConnectedDevicesMap.get(upperCase);
                if (wifiTetherConnectedDevice == null) {
                    wifiTetherConnectedDevice = getConnectedDevicePreference(upperCase);
                    this.mConnectedDevicesMap.put(wifiTetherConnectedDevice.getKey(), wifiTetherConnectedDevice);
                }
                wifiTetherConnectedDevice.updateDisplay();
                this.mConnectedDevices.addPreference(wifiTetherConnectedDevice);
            }
        }
    }

    private WifiTetherConnectedDevice getConnectedDevicePreference(String str) {
        WifiTetherConnectedDevice wifiTetherConnectedDevice = (WifiTetherConnectedDevice) this.mConnectedDevices.findPreference(str);
        return wifiTetherConnectedDevice == null ? new WifiTetherConnectedDevice(this.mContext, str) : wifiTetherConnectedDevice;
    }

    public void preferenceClicked(String str) {
        WifiTetherConnectedDevice wifiTetherConnectedDevice = this.mConnectedDevicesMap.get(str);
        if (wifiTetherConnectedDevice != null) {
            DeviceDetailDialog deviceDetailDialog = new DeviceDetailDialog(this.mContext, wifiTetherConnectedDevice);
            this.mDeviceDetailsDialog = deviceDetailDialog;
            deviceDetailDialog.show();
            return;
        }
        clearDialog();
    }

    private void clearDialog() {
        DeviceDetailDialog deviceDetailDialog = this.mDeviceDetailsDialog;
        if (deviceDetailDialog != null && deviceDetailDialog.isShowing()) {
            this.mDeviceDetailsDialog.dismiss();
        }
        this.mDeviceDetailsDialog = null;
    }

    private class DeviceDetailDialog extends AlertDialog implements DialogInterface.OnClickListener {
        private WifiTetherConnectedDevice mDevice;
        private View mView;

        protected DeviceDetailDialog(Context context, WifiTetherConnectedDevice wifiTetherConnectedDevice) {
            super(context);
            this.mDevice = wifiTetherConnectedDevice;
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle bundle) {
            View inflate = getLayoutInflater().inflate(C1987R$layout.wifi_tether_connected_device, (ViewGroup) null);
            this.mView = inflate;
            setView(inflate);
            setInverseBackgroundForced(true);
            setTitle(this.mDevice.mDeviceName);
            setViewItems();
            setButton(-1, WifiTetherConnectedDevicesController.this.mContext.getResources().getString(C1992R$string.okay), this);
            setButton(-2, WifiTetherConnectedDevicesController.this.mContext.getResources().getString(C1992R$string.wifi_disconnect_button_text), this);
            super.onCreate(bundle);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -2) {
                Intent intent = new Intent(WifiTetherConnectedDevicesController.this.mContext, WifiTetherManageBlocklistService.class);
                intent.putExtra("blocklistMac", this.mDevice.mMACAddress);
                WifiTetherConnectedDevicesController.this.mContext.startService(intent);
            }
            dismiss();
        }

        private void setViewItems() {
            ((TextView) this.mView.findViewById(C1985R$id.macaddress)).setText(this.mDevice.mMACAddress);
            ((TextView) this.mView.findViewById(C1985R$id.ipaddress)).setText(this.mDevice.mIPAddress);
        }
    }
}
