package com.android.settings.connecteddevice;

import android.content.Context;
import android.net.Uri;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.password.PasswordUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.slices.SlicePreferenceController;

public class ConnectedDeviceDashboardFragment extends DashboardFragment {
    private static final boolean DEBUG = Log.isLoggable("ConnectedDeviceFrag", 3);
    static final String KEY_AVAILABLE_DEVICES = "available_device_list";
    static final String KEY_CONNECTED_DEVICES = "connected_device_list";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.connected_devices);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ConnectedDeviceFrag";
    }

    public int getMetricsCategory() {
        return 747;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_connected_devices;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.connected_devices;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        boolean z = true;
        boolean z2 = DeviceConfig.getBoolean("settings_ui", "bt_near_by_suggestion_enabled", true);
        String callingAppPackageName = PasswordUtils.getCallingAppPackageName(getActivity().getActivityToken());
        if (DEBUG) {
            Log.d("ConnectedDeviceFrag", "onAttach() calling package name is : " + callingAppPackageName);
        }
        ((AvailableMediaDeviceGroupController) use(AvailableMediaDeviceGroupController.class)).init(this);
        ((ConnectedDeviceGroupController) use(ConnectedDeviceGroupController.class)).init(this);
        ((PreviouslyConnectedDevicePreferenceController) use(PreviouslyConnectedDevicePreferenceController.class)).init(this);
        ((SlicePreferenceController) use(SlicePreferenceController.class)).setSliceUri(z2 ? Uri.parse(getString(C1992R$string.config_nearby_devices_slice_uri)) : null);
        DiscoverableFooterPreferenceController discoverableFooterPreferenceController = (DiscoverableFooterPreferenceController) use(DiscoverableFooterPreferenceController.class);
        if (!TextUtils.equals("com.android.settings", callingAppPackageName) && !TextUtils.equals("com.android.systemui", callingAppPackageName)) {
            z = false;
        }
        discoverableFooterPreferenceController.setAlwaysDiscoverable(z);
    }
}
