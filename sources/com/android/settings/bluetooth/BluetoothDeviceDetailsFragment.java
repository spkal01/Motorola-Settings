package com.android.settings.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.DeviceConfig;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceCategory;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.BluetoothServiceConnectionListener;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.BlockingSlicePrefController;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.motorola.settings.development.bluetooth.BluetoothLHDCArDialogPreferenceController;
import com.motorola.settings.development.bluetooth.BluetoothLHDCQualityDialogPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceDetailsFragment extends RestrictedDashboardFragment {
    static int EDIT_DEVICE_NAME_ITEM_ID = 1;
    static TestDataFactory sTestDataFactory;
    /* access modifiers changed from: private */
    public BluetoothA2dp mBluetoothA2dp;
    /* access modifiers changed from: private */
    public final BluetoothA2dpConfigStore mBluetoothA2dpConfigStore = new BluetoothA2dpConfigStore();
    private final BroadcastReceiver mBluetoothA2dpReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDeviceDetailsFragment.this.mBluetoothA2dp != null) {
                BluetoothDeviceDetailsFragment bluetoothDeviceDetailsFragment = BluetoothDeviceDetailsFragment.this;
                bluetoothDeviceDetailsFragment.lhdcCategoryVisibleController(bluetoothDeviceDetailsFragment.mBluetoothA2dp);
            }
            if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(action)) {
                BluetoothCodecStatus parcelableExtra = intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS");
                for (AbstractPreferenceController abstractPreferenceController : BluetoothDeviceDetailsFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                        ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothCodecUpdated();
                    }
                }
            }
        }
    };
    private final BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            synchronized (BluetoothDeviceDetailsFragment.this.mBluetoothA2dpConfigStore) {
                BluetoothA2dp unused = BluetoothDeviceDetailsFragment.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
            }
            BluetoothDeviceDetailsFragment bluetoothDeviceDetailsFragment = BluetoothDeviceDetailsFragment.this;
            bluetoothDeviceDetailsFragment.lhdcCategoryVisibleController(bluetoothDeviceDetailsFragment.mBluetoothA2dp);
            for (AbstractPreferenceController abstractPreferenceController : BluetoothDeviceDetailsFragment.this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                    ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceConnected(BluetoothDeviceDetailsFragment.this.mBluetoothA2dp);
                }
            }
        }

        public void onServiceDisconnected(int i) {
            synchronized (BluetoothDeviceDetailsFragment.this.mBluetoothA2dpConfigStore) {
                BluetoothA2dp unused = BluetoothDeviceDetailsFragment.this.mBluetoothA2dp = null;
            }
            for (AbstractPreferenceController abstractPreferenceController : BluetoothDeviceDetailsFragment.this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                    ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceDisconnected();
                }
            }
        }
    };
    CachedBluetoothDevice mCachedDevice;
    String mDeviceAddress;
    LocalBluetoothManager mManager;
    /* access modifiers changed from: private */
    public List<AbstractPreferenceController> mPreferenceControllers = new ArrayList();

    interface TestDataFactory {
        CachedBluetoothDevice getDevice(String str);

        LocalBluetoothManager getManager(Context context);
    }

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "BTDeviceDetailsFrg";
    }

    public int getMetricsCategory() {
        return 1009;
    }

    public BluetoothDeviceDetailsFragment() {
        super("no_config_bluetooth");
    }

    /* access modifiers changed from: package-private */
    public LocalBluetoothManager getLocalBluetoothManager(Context context) {
        TestDataFactory testDataFactory = sTestDataFactory;
        if (testDataFactory != null) {
            return testDataFactory.getManager(context);
        }
        return Utils.getLocalBtManager(context);
    }

    /* access modifiers changed from: package-private */
    public CachedBluetoothDevice getCachedDevice(String str) {
        TestDataFactory testDataFactory = sTestDataFactory;
        if (testDataFactory != null) {
            return testDataFactory.getDevice(str);
        }
        return this.mManager.getCachedDeviceManager().findDevice(this.mManager.getBluetoothAdapter().getRemoteDevice(str));
    }

    public void onAttach(Context context) {
        this.mDeviceAddress = getArguments().getString("device_address");
        this.mManager = getLocalBluetoothManager(context);
        this.mCachedDevice = getCachedDevice(this.mDeviceAddress);
        super.onAttach(context);
        if (this.mCachedDevice == null) {
            Log.w("BTDeviceDetailsFrg", "onAttach() CachedDevice is null!");
            finish();
            return;
        }
        ((AdvancedBluetoothDetailsHeaderController) use(AdvancedBluetoothDetailsHeaderController.class)).init(this.mCachedDevice);
        BluetoothFeatureProvider bluetoothFeatureProvider = FeatureFactory.getFactory(context).getBluetoothFeatureProvider(context);
        ((BlockingSlicePrefController) use(BlockingSlicePrefController.class)).setSliceUri(DeviceConfig.getBoolean("settings_ui", "bt_slice_settings_enabled", true) ? bluetoothFeatureProvider.getBluetoothDeviceSettingsUri(this.mCachedDevice.getDevice()) : null);
    }

    public void onResume() {
        super.onResume();
        finishFragmentIfNecessary();
    }

    /* access modifiers changed from: package-private */
    public void finishFragmentIfNecessary() {
        if (this.mCachedDevice.getBondState() == 10) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.bluetooth_device_details_fragment;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, EDIT_DEVICE_NAME_ITEM_ID, 0, C1992R$string.bluetooth_rename_button);
        add.setIcon(17303644);
        add.setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != EDIT_DEVICE_NAME_ITEM_ID) {
            return super.onOptionsItemSelected(menuItem);
        }
        RemoteDeviceNameDialogFragment.newInstance(this.mCachedDevice).show(getFragmentManager(), "RemoteDeviceName");
        return true;
    }

    private void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, intentFilter);
    }

    private void unregisterReceivers() {
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        registerReceivers();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(getActivity(), this.mBluetoothA2dpServiceListener, 2);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceivers();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
            this.mBluetoothA2dp = null;
        }
    }

    public void setLhdcCategoryVisible(boolean z) {
        ((PreferenceCategory) findPreference("lhdc_option")).setVisible(z);
    }

    public void lhdcCategoryVisibleController(BluetoothA2dp bluetoothA2dp) {
        BluetoothDevice activeDevice = bluetoothA2dp.getActiveDevice();
        BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(this.mCachedDevice.getDevice());
        if (activeDevice == null || !activeDevice.equals(this.mCachedDevice.getDevice())) {
            setLhdcCategoryVisible(false);
        } else if (codecStatus != null) {
            BluetoothCodecConfig codecConfig = codecStatus.getCodecConfig();
            if (codecConfig == null || codecConfig.getCodecType() != 7) {
                setLhdcCategoryVisible(false);
            } else {
                setLhdcCategoryVisible(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        if (this.mCachedDevice != null) {
            Lifecycle settingsLifecycle = getSettingsLifecycle();
            arrayList.add(new BluetoothDetailsHeaderController(context, this, this.mCachedDevice, settingsLifecycle, this.mManager));
            arrayList.add(new BluetoothDetailsButtonsController(context, this, this.mCachedDevice, settingsLifecycle));
            arrayList.add(new BluetoothDetailsCompanionAppsController(context, this, this.mCachedDevice, settingsLifecycle));
            arrayList.add(new BluetoothDetailsProfilesController(context, this, this.mManager, this.mCachedDevice, settingsLifecycle));
            arrayList.add(new BluetoothDetailsMacAddressController(context, this, this.mCachedDevice, settingsLifecycle));
            arrayList.add(new BluetoothLHDCArDialogPreferenceController(context, settingsLifecycle, new BluetoothA2dpConfigStore()));
            arrayList.add(new BluetoothLHDCQualityDialogPreferenceController(context, settingsLifecycle, new BluetoothA2dpConfigStore()));
        }
        this.mPreferenceControllers = (ArrayList) arrayList.clone();
        return arrayList;
    }
}
