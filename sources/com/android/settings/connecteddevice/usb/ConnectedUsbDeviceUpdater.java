package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.UserHandle;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.motorola.settings.utils.MotoCdromUtils;
import com.motorola.settings.utils.MotoReadyForUtils;
import com.motorola.settings.utils.MotoWebcamUtils;

public class ConnectedUsbDeviceUpdater {
    private DevicePreferenceCallback mDevicePreferenceCallback;
    private DashboardFragment mFragment;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private UsbBackend mUsbBackend;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener;
    RestrictedPreference mUsbPreference;
    UsbConnectionBroadcastReceiver mUsbReceiver;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, long j, int i, int i2) {
        if (z) {
            RestrictedPreference restrictedPreference = this.mUsbPreference;
            if (i2 != 2) {
                j = 0;
            }
            restrictedPreference.setSummary(getSummary(j, i));
            this.mDevicePreferenceCallback.onDeviceAdded(this.mUsbPreference);
            return;
        }
        this.mDevicePreferenceCallback.onDeviceRemoved(this.mUsbPreference);
    }

    public ConnectedUsbDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        this(context, dashboardFragment, devicePreferenceCallback, new UsbBackend(context));
    }

    ConnectedUsbDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback, UsbBackend usbBackend) {
        this.mUsbConnectionListener = new ConnectedUsbDeviceUpdater$$ExternalSyntheticLambda1(this);
        this.mFragment = dashboardFragment;
        this.mDevicePreferenceCallback = devicePreferenceCallback;
        this.mUsbBackend = usbBackend;
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mFragment.getContext()).getMetricsFeatureProvider();
    }

    public void registerCallback() {
        this.mUsbReceiver.register();
    }

    public void unregisterCallback() {
        this.mUsbReceiver.unregister();
    }

    public void initUsbPreference(Context context) {
        RestrictedPreference restrictedPreference = new RestrictedPreference(context, (AttributeSet) null);
        this.mUsbPreference = restrictedPreference;
        restrictedPreference.setTitle(C1992R$string.usb_pref);
        this.mUsbPreference.setIcon(C1983R$drawable.ic_usb);
        this.mUsbPreference.setKey("connected_usb");
        this.mUsbPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(context, UserHandle.myUserId()));
        this.mUsbPreference.setOnPreferenceClickListener(new ConnectedUsbDeviceUpdater$$ExternalSyntheticLambda0(this));
        forceUpdate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initUsbPreference$1(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, this.mFragment.getMetricsCategory());
        new SubSettingLauncher(this.mFragment.getContext()).setDestination(UsbDetailsFragment.class.getName()).setTitleRes(C1992R$string.usb_preference).setSourceMetricsCategory(this.mFragment.getMetricsCategory()).launch();
        return true;
    }

    private void forceUpdate() {
        this.mUsbReceiver.register();
    }

    public static int getSummary(long j, int i) {
        if (i != 1) {
            if (i != 2) {
                return C1992R$string.usb_summary_charging_only;
            }
            if (j == 4) {
                return C1992R$string.usb_summary_file_transfers;
            }
            if (j == 32) {
                return C1992R$string.usb_summary_tether;
            }
            if (j == 16) {
                return C1992R$string.usb_summary_photo_transfers;
            }
            if (j == 8) {
                return C1992R$string.usb_summary_MIDI;
            }
            if (j == MotoCdromUtils.FUNCTION_CDROM) {
                return C1992R$string.usb_use_CDROM_desc;
            }
            if (j == MotoWebcamUtils.FUNCTION_WEBCAM) {
                return C1992R$string.usb_use_WEBCAM_desc;
            }
            if (j == MotoReadyForUtils.FUNCTION_READYFOR) {
                return C1992R$string.usb_use_readyfor_desc;
            }
            return C1992R$string.usb_summary_charging_only;
        } else if (j == 4) {
            return C1992R$string.usb_summary_file_transfers_power;
        } else {
            if (j == 32) {
                return C1992R$string.usb_summary_tether_power;
            }
            if (j == 16) {
                return C1992R$string.usb_summary_photo_transfers_power;
            }
            if (j == 8) {
                return C1992R$string.usb_summary_MIDI_power;
            }
            return C1992R$string.usb_summary_power_only;
        }
    }
}
