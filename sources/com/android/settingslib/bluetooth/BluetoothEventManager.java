package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import androidx.constraintlayout.widget.R$styleable;
import com.android.settingslib.R$string;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class BluetoothEventManager {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("BluetoothEventManager", 3);
    private final String ACT_BROADCAST_SOURCE_INFO = "android.bluetooth.BroadcastAudioSAManager.action.BROADCAST_SOURCE_INFO";
    private final IntentFilter mAdapterIntentFilter;
    private final BroadcastReceiver mBroadcastReceiver = new BluetoothBroadcastReceiver();
    /* access modifiers changed from: private */
    public final Collection<BluetoothCallback> mCallbacks = new CopyOnWriteArrayList();
    private final Context mContext;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public final Map<String, Handler> mHandlerMap;
    /* access modifiers changed from: private */
    public final LocalBluetoothAdapter mLocalAdapter;
    private final BroadcastReceiver mProfileBroadcastReceiver = new BluetoothBroadcastReceiver();
    private final IntentFilter mProfileIntentFilter;
    private final android.os.Handler mReceiverHandler;
    private final UserHandle mUserHandle;

    interface Handler {
        void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice);
    }

    BluetoothEventManager(LocalBluetoothAdapter localBluetoothAdapter, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, Context context, android.os.Handler handler, UserHandle userHandle) {
        Object obj;
        this.mLocalAdapter = localBluetoothAdapter;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mAdapterIntentFilter = new IntentFilter();
        this.mProfileIntentFilter = new IntentFilter();
        this.mHandlerMap = new HashMap();
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mReceiverHandler = handler;
        addHandler("android.bluetooth.adapter.action.STATE_CHANGED", new AdapterStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED", new ConnectionStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", new ScanningStateChangedHandler(true));
        addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", new ScanningStateChangedHandler(false));
        addHandler("android.bluetooth.device.action.FOUND", new DeviceFoundHandler());
        addHandler("android.bluetooth.device.action.NAME_CHANGED", new NameChangedHandler());
        addHandler("android.bluetooth.device.action.ALIAS_CHANGED", new NameChangedHandler());
        addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", new BondStateChangedHandler());
        addHandler("android.bluetooth.device.action.CLASS_CHANGED", new ClassChangedHandler());
        addHandler("android.bluetooth.device.action.UUID", new UuidChangedHandler());
        addHandler("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED", new BatteryLevelChangedHandler());
        addHandler("android.bluetooth.headset.action.HF_TWSP_BATTERY_STATE_CHANGED", new TwspBatteryLevelChangedHandler());
        addHandler("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", new AudioModeChangedHandler());
        addHandler("android.intent.action.PHONE_STATE", new AudioModeChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_CONNECTED", new AclStateChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_DISCONNECTED", new AclStateChangedHandler());
        addHandler("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED", new A2dpCodecConfigChangedHandler());
        try {
            obj = Class.forName("com.android.settingslib.bluetooth.BroadcastSourceInfoHandler").getDeclaredConstructor(new Class[]{CachedBluetoothDeviceManager.class}).newInstance(new Object[]{cachedBluetoothDeviceManager});
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            obj = null;
        }
        if (obj != null) {
            Log.d("BluetoothEventManager", "adding SourceInfo Handler");
            addHandler("android.bluetooth.BroadcastAudioSAManager.action.BROADCAST_SOURCE_INFO", (Handler) obj);
        }
        addHandler("android.bluetooth.broadcast.profile.action.BROADCAST_STATE_CHANGED", new BroadcastStateChangedHandler());
        addHandler("android.bluetooth.broadcast.profile.action.BROADCAST_ENCRYPTION_KEY_GENERATED", new BroadcastKeyGeneratedHandler());
        addHandler("android.bluetooth.vcp.profile.action.CONNECTION_MODE_CHANGED", new VcpModeChangedHandler());
        addHandler("android.bluetooth.vcp.profile.action.VOLUME_CHANGED", new VcpVolumeChangedHandler());
        registerAdapterIntentReceiver();
    }

    public void registerCallback(BluetoothCallback bluetoothCallback) {
        this.mCallbacks.add(bluetoothCallback);
    }

    public void unregisterCallback(BluetoothCallback bluetoothCallback) {
        this.mCallbacks.remove(bluetoothCallback);
    }

    /* access modifiers changed from: package-private */
    public void registerProfileIntentReceiver() {
        registerIntentReceiver(this.mProfileBroadcastReceiver, this.mProfileIntentFilter);
    }

    /* access modifiers changed from: package-private */
    public void registerAdapterIntentReceiver() {
        registerIntentReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter);
    }

    private void registerIntentReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        UserHandle userHandle = this.mUserHandle;
        if (userHandle == null) {
            this.mContext.registerReceiver(broadcastReceiver, intentFilter, (String) null, this.mReceiverHandler);
            return;
        }
        this.mContext.registerReceiverAsUser(broadcastReceiver, userHandle, intentFilter, (String) null, this.mReceiverHandler);
    }

    /* access modifiers changed from: package-private */
    public void addProfileHandler(String str, Handler handler) {
        this.mHandlerMap.put(str, handler);
        this.mProfileIntentFilter.addAction(str);
    }

    /* access modifiers changed from: package-private */
    public boolean readPairedDevices() {
        Set<BluetoothDevice> bondedDevices = this.mLocalAdapter.getBondedDevices();
        boolean z = false;
        if (bondedDevices == null) {
            return false;
        }
        for (BluetoothDevice next : bondedDevices) {
            if (this.mDeviceManager.findDevice(next) == null) {
                this.mDeviceManager.addDevice(next);
                z = true;
            }
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void dispatchDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        for (BluetoothCallback onDeviceAdded : this.mCallbacks) {
            onDeviceAdded.onDeviceAdded(cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchDeviceRemoved(CachedBluetoothDevice cachedBluetoothDevice) {
        for (BluetoothCallback onDeviceDeleted : this.mCallbacks) {
            onDeviceDeleted.onDeviceDeleted(cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        for (BluetoothCallback onProfileConnectionStateChanged : this.mCallbacks) {
            onProfileConnectionStateChanged.onProfileConnectionStateChanged(cachedBluetoothDevice, i, i2);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (BluetoothCallback onConnectionStateChanged : this.mCallbacks) {
            onConnectionStateChanged.onConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchAudioModeChanged() {
        for (CachedBluetoothDevice onAudioModeChanged : this.mDeviceManager.getCachedDevicesCopy()) {
            onAudioModeChanged.onAudioModeChanged();
        }
        for (BluetoothCallback onAudioModeChanged2 : this.mCallbacks) {
            onAudioModeChanged2.onAudioModeChanged();
        }
    }

    /* access modifiers changed from: private */
    public void dispatchBroadcastStateChanged(int i) {
        for (BluetoothCallback onBroadcastStateChanged : this.mCallbacks) {
            onBroadcastStateChanged.onBroadcastStateChanged(i);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchBroadcastKeyGenerated() {
        for (BluetoothCallback onBroadcastKeyGenerated : this.mCallbacks) {
            onBroadcastKeyGenerated.onBroadcastKeyGenerated();
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (CachedBluetoothDevice next : this.mDeviceManager.getCachedDevicesCopy()) {
            next.onActiveDeviceChanged(Objects.equals(next, cachedBluetoothDevice), i);
        }
        for (BluetoothCallback onActiveDeviceChanged : this.mCallbacks) {
            onActiveDeviceChanged.onActiveDeviceChanged(cachedBluetoothDevice, i);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchAclStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (BluetoothCallback onAclConnectionStateChanged : this.mCallbacks) {
            onAclConnectionStateChanged.onAclConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchA2dpCodecConfigChanged(CachedBluetoothDevice cachedBluetoothDevice, BluetoothCodecStatus bluetoothCodecStatus) {
        for (BluetoothCallback onA2dpCodecConfigChanged : this.mCallbacks) {
            onA2dpCodecConfigChanged.onA2dpCodecConfigChanged(cachedBluetoothDevice, bluetoothCodecStatus);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchNewGroupFound(CachedBluetoothDevice cachedBluetoothDevice, int i, UUID uuid) {
        synchronized (this.mCallbacks) {
            updateCacheDeviceInfo(i, cachedBluetoothDevice);
            for (BluetoothCallback onNewGroupFound : this.mCallbacks) {
                onNewGroupFound.onNewGroupFound(cachedBluetoothDevice, i, uuid);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchGroupDiscoveryStatusChanged(int i, int i2, int i3) {
        synchronized (this.mCallbacks) {
            for (BluetoothCallback onGroupDiscoveryStatusChanged : this.mCallbacks) {
                onGroupDiscoveryStatusChanged.onGroupDiscoveryStatusChanged(i, i2, i3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addHandler(String str, Handler handler) {
        this.mHandlerMap.put(str, handler);
        this.mAdapterIntentFilter.addAction(str);
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private BluetoothBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Handler handler = (Handler) BluetoothEventManager.this.mHandlerMap.get(action);
            if (handler != null) {
                handler.onReceive(context, intent, bluetoothDevice);
            }
        }
    }

    private class AdapterStateChangedHandler implements Handler {
        private AdapterStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            BluetoothEventManager.this.mLocalAdapter.setBluetoothStateInt(intExtra);
            for (BluetoothCallback onBluetoothStateChanged : BluetoothEventManager.this.mCallbacks) {
                onBluetoothStateChanged.onBluetoothStateChanged(intExtra);
            }
            BluetoothEventManager.this.mDeviceManager.onBluetoothStateChanged(intExtra);
        }
    }

    private class ScanningStateChangedHandler implements Handler {
        private final boolean mStarted;

        ScanningStateChangedHandler(boolean z) {
            this.mStarted = z;
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            for (BluetoothCallback onScanningStateChanged : BluetoothEventManager.this.mCallbacks) {
                onScanningStateChanged.onScanningStateChanged(this.mStarted);
            }
            BluetoothEventManager.this.mDeviceManager.onScanningStateChanged(this.mStarted);
        }
    }

    private class DeviceFoundHandler implements Handler {
        private DeviceFoundHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            short shortExtra = intent.getShortExtra("android.bluetooth.device.extra.RSSI", Short.MIN_VALUE);
            intent.getStringExtra("android.bluetooth.device.extra.NAME");
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                findDevice = BluetoothEventManager.this.mDeviceManager.addDevice(bluetoothDevice);
                Log.d("BluetoothEventManager", "DeviceFoundHandler created new CachedBluetoothDevice " + findDevice.getDevice().getAnonymizedAddress());
            } else if (findDevice.getBondState() == 12 && !findDevice.getDevice().isConnected()) {
                BluetoothEventManager.this.dispatchDeviceAdded(findDevice);
            }
            findDevice.setRssi(shortExtra);
            findDevice.setJustDiscovered(true);
        }
    }

    private class ConnectionStateChangedHandler implements Handler {
        private ConnectionStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.dispatchConnectionStateChanged(BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice), intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE));
        }
    }

    private class BroadcastStateChangedHandler implements Handler {
        private BroadcastStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.dispatchBroadcastStateChanged(intent.getIntExtra("android.bluetooth.profile.extra.STATE", Integer.MIN_VALUE));
        }
    }

    private class BroadcastKeyGeneratedHandler implements Handler {
        private BroadcastKeyGeneratedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.dispatchBroadcastKeyGenerated();
        }
    }

    private class NameChangedHandler implements Handler {
        private NameChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.mDeviceManager.onDeviceNameUpdated(bluetoothDevice);
        }
    }

    private class BondStateChangedHandler implements Handler {
        private BondStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (bluetoothDevice == null) {
                Log.e("BluetoothEventManager", "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
                return;
            }
            int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                Log.w("BluetoothEventManager", "Got bonding state changed for " + bluetoothDevice + ", but we have no record of that device.");
                findDevice = BluetoothEventManager.this.mDeviceManager.addDevice(bluetoothDevice);
            }
            if (intExtra == 12) {
                int intExtra2 = intent.getIntExtra("android.bluetooth.qti.extra.GROUP_ID", Integer.MIN_VALUE);
                if (intExtra2 != Integer.MIN_VALUE && intExtra2 >= 0) {
                    BluetoothEventManager.this.updateCacheDeviceInfo(intExtra2, findDevice);
                } else if (intent.getBooleanExtra("android.bluetooth.qti.extra.IS_PRIVATE_ADDRESS", false)) {
                    Log.d("BluetoothEventManager", "Hide showing private address in UI  " + findDevice);
                    BluetoothEventManager.this.updateIgnoreDeviceFlag(findDevice);
                }
            }
            for (BluetoothCallback onDeviceBondStateChanged : BluetoothEventManager.this.mCallbacks) {
                onDeviceBondStateChanged.onDeviceBondStateChanged(findDevice, intExtra);
            }
            findDevice.onBondingStateChanged(intExtra);
            if (intExtra == 10) {
                if (findDevice.getHiSyncId() != 0) {
                    BluetoothEventManager.this.mDeviceManager.onDeviceUnpaired(findDevice);
                }
                showUnbondMessage(context, findDevice.getName(), intent.getIntExtra("android.bluetooth.device.extra.REASON", Integer.MIN_VALUE));
            }
        }

        private void showUnbondMessage(Context context, String str, int i) {
            int i2;
            if (BluetoothEventManager.DEBUG) {
                Log.d("BluetoothEventManager", "showUnbondMessage() name : " + str + ", reason : " + i);
            }
            switch (i) {
                case 1:
                    i2 = R$string.bluetooth_pairing_pin_error_message;
                    break;
                case 2:
                    i2 = R$string.bluetooth_pairing_rejected_error_message;
                    break;
                case 4:
                    i2 = R$string.bluetooth_pairing_device_down_error_message;
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    i2 = R$string.bluetooth_pairing_error_message;
                    break;
                default:
                    Log.w("BluetoothEventManager", "showUnbondMessage: Not displaying any message for reason: " + i);
                    return;
            }
            BluetoothUtils.showError(context, str, i2);
        }
    }

    private class ClassChangedHandler implements Handler {
        private ClassChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.refresh();
            }
        }
    }

    private class UuidChangedHandler implements Handler {
        private UuidChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.onUuidChanged();
            }
        }
    }

    private class BatteryLevelChangedHandler implements Handler {
        private BatteryLevelChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.refresh();
            }
        }
    }

    private class TwspBatteryLevelChangedHandler implements Handler {
        private TwspBatteryLevelChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.mTwspBatteryState = intent.getIntExtra("android.bluetooth.headset.extra.HF_TWSP_BATTERY_STATE", -1);
                findDevice.mTwspBatteryLevel = intent.getIntExtra("android.bluetooth.headset.extra.HF_TWSP_BATTERY_LEVEL", -1);
                Log.i("BluetoothEventManager", findDevice + ": mTwspBatteryState: " + findDevice.mTwspBatteryState + "mTwspBatteryLevel: " + findDevice.mTwspBatteryLevel);
                findDevice.refresh();
            }
        }
    }

    private class ActiveDeviceChangedHandler implements Handler {
        private ActiveDeviceChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int i;
            String action = intent.getAction();
            if (action == null) {
                Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: action is null");
                return;
            }
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (action.equals("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED")) {
                i = 2;
            } else if (action.equals("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED")) {
                i = 1;
            } else if (action.equals("android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED")) {
                i = 21;
            } else {
                Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: unknown action " + action);
                return;
            }
            BluetoothEventManager.this.dispatchActiveDeviceChanged(findDevice, i);
        }
    }

    private class AclStateChangedHandler implements Handler {
        private AclStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int i;
            if (bluetoothDevice == null) {
                Log.w("BluetoothEventManager", "AclStateChangedHandler: device is null");
            } else if (!BluetoothEventManager.this.mDeviceManager.isSubDevice(bluetoothDevice)) {
                String action = intent.getAction();
                if (action == null) {
                    Log.w("BluetoothEventManager", "AclStateChangedHandler: action is null");
                    return;
                }
                CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    Log.w("BluetoothEventManager", "AclStateChangedHandler: activeDevice is null");
                    return;
                }
                if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                    i = 2;
                } else if (!action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                    Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: unknown action " + action);
                    return;
                } else {
                    i = 0;
                }
                BluetoothEventManager.this.dispatchAclStateChanged(findDevice, i);
            }
        }
    }

    private class AudioModeChangedHandler implements Handler {
        private AudioModeChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (intent.getAction() == null) {
                Log.w("BluetoothEventManager", "AudioModeChangedHandler() action is null");
            } else {
                BluetoothEventManager.this.dispatchAudioModeChanged();
            }
        }
    }

    private class A2dpCodecConfigChangedHandler implements Handler {
        private A2dpCodecConfigChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (intent.getAction() == null) {
                Log.w("BluetoothEventManager", "A2dpCodecConfigChangedHandler: action is null");
                return;
            }
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                Log.w("BluetoothEventManager", "A2dpCodecConfigChangedHandler: device is null");
                return;
            }
            BluetoothCodecStatus parcelableExtra = intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS");
            Log.d("BluetoothEventManager", "A2dpCodecConfigChangedHandler: device=" + bluetoothDevice + ", codecStatus=" + parcelableExtra);
            BluetoothEventManager.this.dispatchA2dpCodecConfigChanged(findDevice, parcelableExtra);
        }
    }

    private class VcpModeChangedHandler implements Handler {
        private VcpModeChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            int intExtra = intent.getIntExtra("android.bluetooth.vcp.profile.extra.MODE", 0);
            if (findDevice != null) {
                Log.i("BluetoothEventManager", findDevice + " Vcp connection mode change to " + intExtra);
                findDevice.refresh();
            }
        }
    }

    private class VcpVolumeChangedHandler implements Handler {
        private VcpVolumeChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                Log.i("BluetoothEventManager", findDevice + " Vcp volume change");
                findDevice.refresh();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateCacheDeviceInfo(int i, CachedBluetoothDevice cachedBluetoothDevice) {
        cachedBluetoothDevice.getDevice();
        boolean isGroupDevice = cachedBluetoothDevice.isGroupDevice();
        Log.d("BluetoothEventManager", "updateCacheDeviceInfo groupId " + i + ", cachedDevice :" + cachedBluetoothDevice + ", name :" + cachedBluetoothDevice.getName() + " isGroup :" + isGroupDevice + " groupId " + cachedBluetoothDevice.getGroupId());
        if (!isGroupDevice) {
            cachedBluetoothDevice.setDeviceType(i);
        } else if (i != cachedBluetoothDevice.getGroupId()) {
            Log.d("BluetoothEventManager", "groupId mismatch ignore" + cachedBluetoothDevice.getGroupId());
        } else {
            Log.d("BluetoothEventManager", "updateCacheDeviceInfo update ignored ");
        }
    }

    /* access modifiers changed from: private */
    public void updateIgnoreDeviceFlag(CachedBluetoothDevice cachedBluetoothDevice) {
        cachedBluetoothDevice.setDeviceType(R$styleable.Constraint_layout_goneMarginRight);
    }
}
