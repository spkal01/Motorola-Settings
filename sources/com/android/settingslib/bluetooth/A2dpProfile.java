package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A2dpProfile implements LocalBluetoothProfile {
    static final ParcelUuid[] SINK_UUIDS = {BluetoothUuid.A2DP_SINK, BluetoothUuid.ADV_AUDIO_DIST};

    /* renamed from: V */
    private static boolean f139V = true;
    private final BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public final LocalBluetoothProfileManager mProfileManager;
    /* access modifiers changed from: private */
    public BluetoothA2dp mService;

    public boolean accessProfileEnabled() {
        return true;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17303201;
    }

    public int getOrdinal() {
        return 1;
    }

    public int getProfileId() {
        return 2;
    }

    public String toString() {
        return "A2DP";
    }

    private final class A2dpServiceListener implements BluetoothProfile.ServiceListener {
        private A2dpServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothA2dp unused = A2dpProfile.this.mService = (BluetoothA2dp) bluetoothProfile;
            List<BluetoothDevice> connectedDevices = A2dpProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice remove = connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = A2dpProfile.this.mDeviceManager.findDevice(remove);
                if (findDevice == null) {
                    Log.w("A2dpProfile", "A2dpProfile found new device: " + remove);
                    findDevice = A2dpProfile.this.mDeviceManager.addDevice(remove);
                }
                findDevice.onProfileStateChanged(A2dpProfile.this, 2);
                findDevice.refresh();
            }
            boolean unused2 = A2dpProfile.this.mIsProfileReady = true;
            A2dpProfile.this.mProfileManager.callServiceConnectedListeners();
        }

        public void onServiceDisconnected(int i) {
            boolean unused = A2dpProfile.this.mIsProfileReady = false;
        }
    }

    public boolean isProfileReady() {
        return this.mIsProfileReady;
    }

    A2dpProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mContext = context;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        defaultAdapter.getProfileProxy(context, new A2dpServiceListener(), 2);
    }

    public List<BluetoothDevice> getConnectedDevices() {
        return getDevicesByStates(new int[]{2, 1, 3});
    }

    private List<BluetoothDevice> getDevicesByStates(int[] iArr) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return new ArrayList(0);
        }
        return bluetoothA2dp.getDevicesMatchingConnectionStates(iArr);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return 0;
        }
        return bluetoothA2dp.getConnectionState(bluetoothDevice);
    }

    public boolean setActiveDevice(BluetoothDevice bluetoothDevice) {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            return false;
        }
        if (bluetoothDevice == null) {
            return bluetoothAdapter.removeActiveDevice(0);
        }
        return bluetoothAdapter.setActiveDevice(bluetoothDevice, 0);
    }

    public BluetoothDevice getActiveDevice() {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return null;
        }
        return bluetoothA2dp.getActiveDevice();
    }

    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp != null && bluetoothA2dp.getConnectionPolicy(bluetoothDevice) > 0) {
            return true;
        }
        return false;
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return false;
        }
        if (!z) {
            return bluetoothA2dp.setConnectionPolicy(bluetoothDevice, 0);
        }
        if (bluetoothA2dp.getConnectionPolicy(bluetoothDevice) < 100) {
            return this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return false;
    }

    public boolean supportsHighQualityAudio(BluetoothDevice bluetoothDevice) {
        if (f139V) {
            Log.d("A2dpProfile", " execute supportsHighQualityAudio()");
        }
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            if (f139V) {
                Log.d("A2dpProfile", "mService is null.");
            }
            return false;
        }
        if (bluetoothDevice == null) {
            bluetoothDevice = bluetoothA2dp.getActiveDevice();
        }
        if (bluetoothDevice != null && this.mService.isOptionalCodecsSupported(bluetoothDevice) == 1) {
            return true;
        }
        return false;
    }

    public boolean isHighQualityAudioEnabled(BluetoothDevice bluetoothDevice) {
        if (f139V) {
            Log.d("A2dpProfile", " execute isHighQualityAudioEnabled()");
        }
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            if (f139V) {
                Log.d("A2dpProfile", "mService is null.");
            }
            return false;
        }
        if (bluetoothDevice == null) {
            bluetoothDevice = bluetoothA2dp.getActiveDevice();
        }
        if (bluetoothDevice == null) {
            return false;
        }
        int isOptionalCodecsEnabled = this.mService.isOptionalCodecsEnabled(bluetoothDevice);
        if (isOptionalCodecsEnabled != -1) {
            if (isOptionalCodecsEnabled == 1) {
                return true;
            }
            return false;
        } else if (getConnectionStatus(bluetoothDevice) != 2 && supportsHighQualityAudio(bluetoothDevice)) {
            return true;
        } else {
            BluetoothCodecConfig bluetoothCodecConfig = null;
            if (this.mService.getCodecStatus(bluetoothDevice) != null) {
                bluetoothCodecConfig = this.mService.getCodecStatus(bluetoothDevice).getCodecConfig();
            }
            if (bluetoothCodecConfig != null) {
                return !bluetoothCodecConfig.isMandatoryCodec();
            }
            return false;
        }
    }

    public void setHighQualityAudioEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        if (f139V) {
            Log.d("A2dpProfile", " execute setHighQualityAudioEnabled()");
        }
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp != null) {
            if (bluetoothDevice == null) {
                bluetoothDevice = bluetoothA2dp.getActiveDevice();
            }
            if (bluetoothDevice != null) {
                this.mService.setOptionalCodecsEnabled(bluetoothDevice, z ? 1 : 0);
                if (getConnectionStatus(bluetoothDevice) == 2) {
                    if (z) {
                        this.mService.enableOptionalCodecs(bluetoothDevice);
                    } else {
                        this.mService.disableOptionalCodecs(bluetoothDevice);
                    }
                }
            }
        } else if (f139V) {
            Log.d("A2dpProfile", "mService is null.");
        }
    }

    public String getHighQualityAudioOptionLabel(BluetoothDevice bluetoothDevice) {
        BluetoothDevice bluetoothDevice2;
        BluetoothCodecConfig[] bluetoothCodecConfigArr;
        int i;
        if (f139V) {
            Log.d("A2dpProfile", " execute getHighQualityAudioOptionLabel()");
        }
        if (bluetoothDevice != null) {
            bluetoothDevice2 = bluetoothDevice;
        } else {
            bluetoothDevice2 = this.mService.getActiveDevice();
        }
        int i2 = R$string.bluetooth_profile_a2dp_high_quality_unknown_codec;
        if (bluetoothDevice2 != null && supportsHighQualityAudio(bluetoothDevice)) {
            char c = 2;
            if (getConnectionStatus(bluetoothDevice) == 2) {
                BluetoothA2dp bluetoothA2dp = this.mService;
                BluetoothCodecConfig bluetoothCodecConfig = null;
                if (bluetoothA2dp == null || bluetoothA2dp.getCodecStatus(bluetoothDevice) == null) {
                    bluetoothCodecConfigArr = null;
                } else {
                    bluetoothCodecConfigArr = this.mService.getCodecStatus(bluetoothDevice).getCodecsSelectableCapabilities();
                    Arrays.sort(bluetoothCodecConfigArr, A2dpProfile$$ExternalSyntheticLambda0.INSTANCE);
                }
                if (bluetoothCodecConfigArr != null && bluetoothCodecConfigArr.length >= 1) {
                    bluetoothCodecConfig = bluetoothCodecConfigArr[0];
                }
                if (bluetoothCodecConfig == null || bluetoothCodecConfig.isMandatoryCodec()) {
                    i = 1000000;
                } else {
                    i = bluetoothCodecConfig.getCodecType();
                }
                switch (i) {
                    case 0:
                        c = 1;
                        break;
                    case 1:
                        break;
                    case 2:
                        c = 3;
                        break;
                    case 3:
                        c = 4;
                        break;
                    case 4:
                        c = 5;
                        break;
                    case 5:
                        c = 6;
                        break;
                    case 6:
                        c = 7;
                        break;
                    case 7:
                        c = 8;
                        break;
                    case 8:
                        c = 9;
                        break;
                    case 9:
                        c = 10;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                if (c < 0) {
                    return this.mContext.getString(i2);
                }
                Context context = this.mContext;
                return context.getString(R$string.bluetooth_profile_a2dp_high_quality, new Object[]{context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_titles)[c]});
            }
        }
        return this.mContext.getString(i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getHighQualityAudioOptionLabel$0(BluetoothCodecConfig bluetoothCodecConfig, BluetoothCodecConfig bluetoothCodecConfig2) {
        return bluetoothCodecConfig2.getCodecPriority() - bluetoothCodecConfig.getCodecPriority();
    }

    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return R$string.bluetooth_profile_a2dp;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        Log.d("A2dpProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(2, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("A2dpProfile", "Error cleaning up A2DP proxy", th);
            }
        }
    }
}
