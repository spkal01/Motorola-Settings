package com.android.settings.deviceinfo.simstatus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellSignalStrength;
import android.telephony.ICellBroadcastService;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.utils.ThreadUtils;
import com.motorola.android.telephony.MotoExtPhoneStateListener;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.deviceinfo.DeviceUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SimStatusDialogController implements LifecycleObserver {
    static final int CELLULAR_NETWORK_STATE = C1985R$id.data_state_value;
    static final int CELL_DATA_NETWORK_TYPE_VALUE_ID = C1985R$id.data_network_type_value;
    static final int CELL_VOICE_NETWORK_TYPE_VALUE_ID = C1985R$id.voice_network_type_value;
    static final boolean DEBUG = Build.IS_DEBUGGABLE;
    static final int EID_INFO_LABEL_ID = C1985R$id.esim_id_label;
    static final int EID_INFO_VALUE_ID = C1985R$id.esim_id_value;
    static final int ICCID_INFO_LABEL_ID = C1985R$id.icc_id_label;
    static final int ICCID_INFO_VALUE_ID = C1985R$id.icc_id_value;
    static final int IMS_REGISTRATION_STATE_LABEL_ID = C1985R$id.ims_reg_state_label;
    static final int IMS_REGISTRATION_STATE_VALUE_ID = C1985R$id.ims_reg_state_value;
    static final int MAX_PHONE_COUNT_SINGLE_SIM = 1;
    static final int NETWORK_PROVIDER_VALUE_ID = C1985R$id.operator_name_value;
    static final int OPERATOR_INFO_LABEL_ID = C1985R$id.latest_area_info_label;
    static final int OPERATOR_INFO_VALUE_ID = C1985R$id.latest_area_info_value;
    static final int PHONE_NUMBER_VALUE_ID = C1985R$id.number_value;
    static final int ROAMING_INFO_VALUE_ID = C1985R$id.roaming_state_value;
    static final int SERVICE_STATE_VALUE_ID = C1985R$id.service_state_value;
    static final int SIGNAL_STRENGTH_LABEL_ID = C1985R$id.signal_strength_label;
    static final int SIGNAL_STRENGTH_VALUE_ID = C1985R$id.signal_strength_value;
    private final BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("SimStatusDialogCtrl", "onReceive: " + action);
            if ("android.telephony.action.AREA_INFO_UPDATED".equals(action) && intent.getIntExtra("android.telephony.extra.SLOT_INDEX", 0) == SimStatusDialogController.this.mSlotIndex) {
                SimStatusDialogController.this.updateAreaInfoText();
            } else if (!"com.android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED".equals(action)) {
            } else {
                if (intent.getExtras() == null) {
                    Log.d("SimStatusDialogCtrl", "extras is null");
                    return;
                }
                int intExtra = intent.getIntExtra("subId", -1);
                if (SimStatusDialogController.this.mSubscriptionInfo != null && SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() == intExtra) {
                    String stringExtra = intent.getStringExtra("message");
                    Log.d("SimStatusDialogCtrl", "SystemUI message is: " + stringExtra);
                    if (stringExtra != null) {
                        SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.OPERATOR_INFO_VALUE_ID, stringExtra);
                    }
                }
            }
        }
    };
    private final CarrierConfigManager mCarrierConfigManager;
    private CellBroadcastServiceConnection mCellBroadcastServiceConnection;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final SimStatusDialogFragment mDialog;
    private final EuiccManager mEuiccManager;
    private ImsMmTelManager.RegistrationCallback mImsRegStateCallback = new ImsMmTelManager.RegistrationCallback() {
        public void onRegistered(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C1992R$string.ims_reg_status_registered));
        }

        public void onRegistering(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C1992R$string.ims_reg_status_not_registered));
        }

        public void onUnregistered(ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C1992R$string.ims_reg_status_not_registered));
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C1992R$string.ims_reg_status_not_registered));
        }
    };
    private boolean mIsRegisteredListener = false;
    /* access modifiers changed from: private */
    public MotoExt5GStateListener mMotoExt5GStateListener = null;
    /* access modifiers changed from: private */
    public MotoExtTelephonyManager mMotoExtTM;
    private int mNrDataIconType;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        public void onSubscriptionsChanged() {
            int i = -1;
            int subscriptionId = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            SimStatusDialogController simStatusDialogController = SimStatusDialogController.this;
            SubscriptionInfo unused = simStatusDialogController.mSubscriptionInfo = simStatusDialogController.getPhoneSubscriptionInfo(simStatusDialogController.mSlotIndex);
            if (SimStatusDialogController.this.mSubscriptionInfo != null) {
                i = SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId();
            }
            if (subscriptionId != i) {
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                    SimStatusDialogController.this.unregisterImsRegistrationCallback(subscriptionId);
                }
                if (SubscriptionManager.isValidSubscriptionId(i)) {
                    SimStatusDialogController simStatusDialogController2 = SimStatusDialogController.this;
                    TelephonyManager unused2 = simStatusDialogController2.mTelephonyManager = simStatusDialogController2.mTelephonyManager.createForSubscriptionId(i);
                    SimStatusDialogController.this.registerImsRegistrationCallback(i);
                }
                if (SimStatusDialogController.this.mMotoExtTM != null) {
                    SimStatusDialogController.this.mMotoExtTM.listen(SimStatusDialogController.this.mMotoExt5GStateListener, 0);
                }
                if (SimStatusDialogController.this.mSubscriptionInfo != null && SimStatusDialogController.this.isOem5GAPIEnabled()) {
                    MotoExtTelephonyManager unused3 = SimStatusDialogController.this.mMotoExtTM = new MotoExtTelephonyManager(SimStatusDialogController.this.mContext, SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId());
                    SimStatusDialogController simStatusDialogController3 = SimStatusDialogController.this;
                    SimStatusDialogController simStatusDialogController4 = SimStatusDialogController.this;
                    MotoExt5GStateListener unused4 = simStatusDialogController3.mMotoExt5GStateListener = new MotoExt5GStateListener(simStatusDialogController4.mSubscriptionInfo.getSubscriptionId());
                    SimStatusDialogController.this.mMotoExtTM.listen(SimStatusDialogController.this.mMotoExt5GStateListener, 65536);
                }
            }
            if (SimStatusDialogController.this.mDialog.getActivity() != null) {
                SimStatusDialogController.this.updateSubscriptionStatus();
            }
        }
    };
    /* access modifiers changed from: private */
    public ServiceState mPreviousServiceState;
    /* access modifiers changed from: private */
    public final Resources mRes;
    private boolean mShowLatestAreaInfo;
    /* access modifiers changed from: private */
    public final int mSlotIndex;
    /* access modifiers changed from: private */
    public SubscriptionInfo mSubscriptionInfo;
    private final SubscriptionManager mSubscriptionManager;
    protected SimStatusDialogTelephonyCallback mTelephonyCallback;
    /* access modifiers changed from: private */
    public TelephonyDisplayInfo mTelephonyDisplayInfo;
    /* access modifiers changed from: private */
    public TelephonyManager mTelephonyManager;

    static String getNetworkTypeName(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            case 16:
                return "GSM";
            case 17:
                return "TD_SCDMA";
            case 18:
                return "IWLAN";
            case 20:
                return "NR SA";
            default:
                return "UNKNOWN";
        }
    }

    private class CellBroadcastServiceConnection implements ServiceConnection {
        private IBinder mService;

        private CellBroadcastServiceConnection() {
        }

        public IBinder getService() {
            return this.mService;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SimStatusDialogCtrl", "connected to CellBroadcastService");
            this.mService = iBinder;
            SimStatusDialogController.this.updateAreaInfoText();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "mICellBroadcastService has disconnected unexpectedly");
        }

        public void onBindingDied(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Binding died");
        }

        public void onNullBinding(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Null binding");
        }
    }

    public SimStatusDialogController(SimStatusDialogFragment simStatusDialogFragment, Lifecycle lifecycle, int i) {
        this.mDialog = simStatusDialogFragment;
        Context context = simStatusDialogFragment.getContext();
        this.mContext = context;
        this.mSlotIndex = i;
        this.mSubscriptionInfo = getPhoneSubscriptionInfo(i);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mEuiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mRes = context.getResources();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        if (this.mSubscriptionInfo != null && isOem5GAPIEnabled()) {
            this.mMotoExtTM = new MotoExtTelephonyManager(context, this.mSubscriptionInfo.getSubscriptionId());
            this.mMotoExt5GStateListener = new MotoExt5GStateListener(this.mSubscriptionInfo.getSubscriptionId());
        }
    }

    public void initialize() {
        requestForUpdateEid();
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            this.mTelephonyCallback = new SimStatusDialogTelephonyCallback();
            updateLatestAreaInfo();
            updateSubscriptionStatus();
        }
    }

    /* access modifiers changed from: private */
    public void updateSubscriptionStatus() {
        updateNetworkProvider();
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        SignalStrength signalStrength = this.mTelephonyManager.getSignalStrength();
        updatePhoneNumber();
        updateServiceState(serviceState);
        updateSignalStrength(signalStrength);
        updateNetworkType();
        updateRoamingStatus(serviceState);
        updateIccidNumber();
        updateImsRegistrationState();
    }

    public void deinitialize() {
        if (this.mShowLatestAreaInfo) {
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (!(cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() == null)) {
                this.mContext.unbindService(this.mCellBroadcastServiceConnection);
            }
            this.mCellBroadcastServiceConnection = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            this.mTelephonyManager = createForSubscriptionId;
            createForSubscriptionId.registerTelephonyCallback(this.mContext.getMainExecutor(), this.mTelephonyCallback);
            this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mOnSubscriptionsChangedListener);
            registerImsRegistrationCallback(this.mSubscriptionInfo.getSubscriptionId());
            if (this.mShowLatestAreaInfo) {
                updateAreaInfoText();
                this.mContext.registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.telephony.action.AREA_INFO_UPDATED"));
                this.mContext.registerReceiver(this.mAreaInfoReceiver, new IntentFilter("com.android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED"), "android.permission.RECEIVE_EMERGENCY_BROADCAST", (Handler) null);
                Intent intent = new Intent("com.android.systemui.GET_LATEST_SYSTEMUI_AREA_INFO_ACTION");
                intent.setPackage("com.android.systemui");
                this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.RECEIVE_EMERGENCY_BROADCAST");
            }
            this.mIsRegisteredListener = true;
            MotoExtTelephonyManager motoExtTelephonyManager = this.mMotoExtTM;
            if (motoExtTelephonyManager != null) {
                motoExtTelephonyManager.listen(this.mMotoExt5GStateListener, 65536);
                updateNetworkType();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            unregisterImsRegistrationCallback(subscriptionInfo.getSubscriptionId());
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
            this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
            if (this.mShowLatestAreaInfo) {
                this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
            }
            MotoExtTelephonyManager motoExtTelephonyManager = this.mMotoExtTM;
            if (motoExtTelephonyManager != null) {
                motoExtTelephonyManager.listen(this.mMotoExt5GStateListener, 0);
            }
        } else if (this.mIsRegisteredListener) {
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
            this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
            if (this.mShowLatestAreaInfo) {
                this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
            }
            this.mIsRegisteredListener = false;
        }
    }

    /* access modifiers changed from: private */
    public void updateNetworkProvider() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        this.mDialog.setText(NETWORK_PROVIDER_VALUE_ID, subscriptionInfo != null ? subscriptionInfo.getCarrierName() : null);
    }

    /* access modifiers changed from: protected */
    public void updatePhoneNumber() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null || !DeviceUtils.isVzwFtr7071(this.mContext, subscriptionInfo.getSubscriptionId())) {
            this.mDialog.setText(PHONE_NUMBER_VALUE_ID, DeviceInfoUtils.getBidiFormattedPhoneNumber(this.mContext, this.mSubscriptionInfo));
        } else {
            this.mDialog.setText(PHONE_NUMBER_VALUE_ID, (CharSequence) null);
        }
    }

    /* access modifiers changed from: private */
    public void updateDataState(int i) {
        String str;
        if (i == 0) {
            str = this.mRes.getString(C1992R$string.radioInfo_data_disconnected);
        } else if (i == 1) {
            str = this.mRes.getString(C1992R$string.radioInfo_data_connecting);
        } else if (i != 2) {
            str = i != 3 ? this.mRes.getString(C1992R$string.radioInfo_unknown) : this.mRes.getString(C1992R$string.radioInfo_data_suspended);
        } else {
            str = this.mRes.getString(C1992R$string.radioInfo_data_connected);
        }
        this.mDialog.setText(CELLULAR_NETWORK_STATE, str);
    }

    /* access modifiers changed from: private */
    public void updateAreaInfoText() {
        CellBroadcastServiceConnection cellBroadcastServiceConnection;
        ICellBroadcastService asInterface;
        if (this.mShowLatestAreaInfo && (cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection) != null && (asInterface = ICellBroadcastService.Stub.asInterface(cellBroadcastServiceConnection.getService())) != null) {
            try {
                CharSequence cellBroadcastAreaInfo = asInterface.getCellBroadcastAreaInfo(this.mSlotIndex);
                Log.d("SimStatusDialogCtrl", "getCellBroadcastAreaInfo message is: " + cellBroadcastAreaInfo);
                if (!TextUtils.isEmpty(cellBroadcastAreaInfo)) {
                    this.mDialog.setText(OPERATOR_INFO_VALUE_ID, cellBroadcastAreaInfo);
                }
            } catch (RemoteException e) {
                Log.d("SimStatusDialogCtrl", "Can't get area info. e=" + e);
            }
        }
    }

    private void bindCellBroadcastService() {
        this.mCellBroadcastServiceConnection = new CellBroadcastServiceConnection();
        Intent intent = new Intent("android.telephony.CellBroadcastService");
        String cellBroadcastServicePackage = getCellBroadcastServicePackage();
        if (!TextUtils.isEmpty(cellBroadcastServicePackage)) {
            intent.setPackage(cellBroadcastServicePackage);
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() != null) {
                Log.d("SimStatusDialogCtrl", "skipping bindService because connection already exists");
            } else if (!this.mContext.bindService(intent, this.mCellBroadcastServiceConnection, 1)) {
                Log.e("SimStatusDialogCtrl", "Unable to bind to service");
            }
        }
    }

    private String getCellBroadcastServicePackage() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.telephony.CellBroadcastService"), 1048576);
        if (queryIntentServices.size() != 1) {
            Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found " + queryIntentServices.size() + " CBS packages");
        }
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                String str = serviceInfo.packageName;
                if (TextUtils.isEmpty(str)) {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found a CBS package but packageName is null/empty");
                } else if (packageManager.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str) == 0) {
                    Log.d("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str);
                    return str;
                } else {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str + " does not have READ_PRIVILEGED_PHONE_STATE permission");
                }
            }
        }
        Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: package name not found");
        return null;
    }

    private void updateLatestAreaInfo() {
        boolean z = Resources.getSystem().getBoolean(17891711) && this.mTelephonyManager.getPhoneType() != 2;
        this.mShowLatestAreaInfo = z;
        if (z) {
            bindCellBroadcastService();
            return;
        }
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_LABEL_ID);
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_VALUE_ID);
    }

    /* access modifiers changed from: private */
    public void updateServiceState(ServiceState serviceState) {
        String str;
        int combinedServiceState = Utils.getCombinedServiceState(serviceState);
        if (!Utils.isInService(serviceState)) {
            resetSignalStrength();
        } else if (!Utils.isInService(this.mPreviousServiceState)) {
            updateSignalStrength(this.mTelephonyManager.getSignalStrength());
        }
        if (combinedServiceState == 0) {
            str = this.mRes.getString(C1992R$string.radioInfo_service_in);
        } else if (combinedServiceState == 1 || combinedServiceState == 2) {
            str = this.mRes.getString(C1992R$string.radioInfo_service_out);
        } else if (combinedServiceState != 3) {
            str = this.mRes.getString(C1992R$string.radioInfo_unknown);
        } else {
            str = this.mRes.getString(C1992R$string.radioInfo_service_off);
        }
        this.mDialog.setText(SERVICE_STATE_VALUE_ID, str);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r7.mCarrierConfigManager.getConfigForSubId(r0.getSubscriptionId());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSignalStrength(android.telephony.SignalStrength r8) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0003
            return
        L_0x0003:
            android.telephony.SubscriptionInfo r0 = r7.mSubscriptionInfo
            r1 = 1
            if (r0 == 0) goto L_0x001b
            int r0 = r0.getSubscriptionId()
            android.telephony.CarrierConfigManager r2 = r7.mCarrierConfigManager
            android.os.PersistableBundle r0 = r2.getConfigForSubId(r0)
            if (r0 == 0) goto L_0x001b
            java.lang.String r2 = "show_signal_strength_in_sim_status_bool"
            boolean r0 = r0.getBoolean(r2)
            goto L_0x001c
        L_0x001b:
            r0 = r1
        L_0x001c:
            if (r0 != 0) goto L_0x002d
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r8 = r7.mDialog
            int r0 = SIGNAL_STRENGTH_LABEL_ID
            r8.removeSettingFromScreen(r0)
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r7 = r7.mDialog
            int r8 = SIGNAL_STRENGTH_VALUE_ID
            r7.removeSettingFromScreen(r8)
            return
        L_0x002d:
            android.telephony.TelephonyManager r0 = r7.mTelephonyManager
            android.telephony.ServiceState r0 = r0.getServiceState()
            boolean r0 = com.android.settingslib.Utils.isInService(r0)
            if (r0 != 0) goto L_0x003a
            return
        L_0x003a:
            int r0 = r8.getLevel()
            if (r0 != 0) goto L_0x0044
            r7.resetSignalStrength()
            return
        L_0x0044:
            int r0 = r7.getDbm(r8)
            int r8 = r7.getAsuLevel(r8)
            r2 = -1
            r3 = 0
            if (r0 == r2) goto L_0x0055
            r4 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r4) goto L_0x0056
        L_0x0055:
            r0 = r3
        L_0x0056:
            if (r8 != r2) goto L_0x0059
            r8 = r3
        L_0x0059:
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r2 = r7.mDialog
            int r4 = SIGNAL_STRENGTH_VALUE_ID
            android.content.res.Resources r7 = r7.mRes
            int r5 = com.android.settings.C1992R$string.sim_signal_strength
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r6[r3] = r0
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6[r1] = r8
            java.lang.String r7 = r7.getString(r5, r6)
            r2.setText(r4, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.updateSignalStrength(android.telephony.SignalStrength):void");
    }

    private void resetSignalStrength() {
        this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, "0");
    }

    /* access modifiers changed from: private */
    public void updateNetworkType() {
        int i;
        boolean z;
        boolean z2 = false;
        if (this.mSubscriptionInfo == null) {
            String networkTypeName = getNetworkTypeName(0);
            this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName);
            this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName);
            return;
        }
        MotoExtTelephonyManager motoExtTelephonyManager = this.mMotoExtTM;
        if (motoExtTelephonyManager != null) {
            this.mNrDataIconType = motoExtTelephonyManager.getNrDataIconType();
        }
        int subscriptionId = this.mSubscriptionInfo.getSubscriptionId();
        int dataNetworkType = this.mTelephonyManager.getDataNetworkType();
        int voiceNetworkType = this.mTelephonyManager.getVoiceNetworkType();
        TelephonyDisplayInfo telephonyDisplayInfo = this.mTelephonyDisplayInfo;
        if (telephonyDisplayInfo == null) {
            i = 0;
        } else {
            i = telephonyDisplayInfo.getOverrideNetworkType();
        }
        String str = null;
        String networkTypeName2 = dataNetworkType != 0 ? getNetworkTypeName(dataNetworkType) : null;
        if (voiceNetworkType != 0) {
            str = getNetworkTypeName(voiceNetworkType);
        }
        if (isUseMotoExtNrType()) {
            i = getNrType();
        }
        if (DEBUG) {
            Log.d("SimStatusDialogCtrl", this.mNrDataIconType + "overrideNetworkType" + i);
        }
        if (dataNetworkType != 18) {
            if (i == 4 || i == 5) {
                networkTypeName2 = isVZWSim() ? this.mContext.getString(C1992R$string.vzw_nr_state_5g_uw) : this.mContext.getString(C1992R$string.nr_state_5g_plus);
            } else if (i == 3) {
                networkTypeName2 = this.mContext.getString(C1992R$string.nr_state_5g);
            }
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
        if (configForSubId != null) {
            z2 = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
            z = configForSubId.getBoolean("hide_lte_plus_data_icon_bool");
        } else {
            z = false;
        }
        if (z2) {
            if ("LTE".equals(networkTypeName2)) {
                networkTypeName2 = "4G";
            }
            if ("LTE".equals(str)) {
                str = "4G";
            }
        }
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (("LTE".equals(networkTypeName2) || "4G".equals(networkTypeName2)) && !z && Utils.isInService(serviceState) && serviceState.isUsingCarrierAggregation()) {
            networkTypeName2 = networkTypeName2 + "+";
        }
        if (dataNetworkType != 0 && this.mTelephonyManager.isWifiCallingAvailable() && SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            ImsMmTelManager createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(subscriptionId);
            if ((this.mTelephonyManager.isNetworkRoaming() ? createForSubscriptionId.getVoWiFiRoamingModeSetting() : createForSubscriptionId.getVoWiFiModeSetting()) != 1) {
                str = networkTypeName2;
            }
        }
        this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, str);
        this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r2.mCarrierConfigManager.getConfigForSubId(r0.getSubscriptionId());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRoamingStatus(android.telephony.ServiceState r3) {
        /*
            r2 = this;
            android.telephony.SubscriptionInfo r0 = r2.mSubscriptionInfo
            if (r0 == 0) goto L_0x0017
            int r0 = r0.getSubscriptionId()
            android.telephony.CarrierConfigManager r1 = r2.mCarrierConfigManager
            android.os.PersistableBundle r0 = r1.getConfigForSubId(r0)
            if (r0 == 0) goto L_0x0017
            java.lang.String r1 = "config_hide_roaming_status_in_sim_status_bool"
            boolean r0 = r0.getBoolean(r1)
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            if (r0 == 0) goto L_0x0029
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r3 = r2.mDialog
            int r0 = com.android.settings.C1985R$id.roaming_state_label
            r3.removeSettingFromScreen(r0)
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r2 = r2.mDialog
            int r3 = ROAMING_INFO_VALUE_ID
            r2.removeSettingFromScreen(r3)
            return
        L_0x0029:
            if (r3 != 0) goto L_0x003b
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r3 = r2.mDialog
            int r0 = ROAMING_INFO_VALUE_ID
            android.content.res.Resources r2 = r2.mRes
            int r1 = com.android.settings.C1992R$string.radioInfo_unknown
            java.lang.String r2 = r2.getString(r1)
            r3.setText(r0, r2)
            goto L_0x0060
        L_0x003b:
            boolean r3 = r3.getRoaming()
            if (r3 == 0) goto L_0x0051
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r3 = r2.mDialog
            int r0 = ROAMING_INFO_VALUE_ID
            android.content.res.Resources r2 = r2.mRes
            int r1 = com.android.settings.C1992R$string.radioInfo_roaming_in
            java.lang.String r2 = r2.getString(r1)
            r3.setText(r0, r2)
            goto L_0x0060
        L_0x0051:
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r3 = r2.mDialog
            int r0 = ROAMING_INFO_VALUE_ID
            android.content.res.Resources r2 = r2.mRes
            int r1 = com.android.settings.C1992R$string.radioInfo_roaming_not
            java.lang.String r2 = r2.getString(r1)
            r3.setText(r0, r2)
        L_0x0060:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.updateRoamingStatus(android.telephony.ServiceState):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0024  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateIccidNumber() {
        /*
            r4 = this;
            android.telephony.SubscriptionInfo r0 = r4.mSubscriptionInfo
            if (r0 == 0) goto L_0x0021
            int r0 = r0.getSubscriptionId()
            android.telephony.CarrierConfigManager r1 = r4.mCarrierConfigManager
            android.os.PersistableBundle r0 = r1.getConfigForSubId(r0)
            android.telephony.SubscriptionInfo r1 = r4.mSubscriptionInfo
            boolean r1 = r1.isEmbedded()
            if (r1 == 0) goto L_0x0018
            r0 = 1
            goto L_0x0022
        L_0x0018:
            if (r0 == 0) goto L_0x0021
            java.lang.String r1 = "show_iccid_in_sim_status_bool"
            boolean r0 = r0.getBoolean(r1)
            goto L_0x0022
        L_0x0021:
            r0 = 0
        L_0x0022:
            if (r0 != 0) goto L_0x0033
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r0 = r4.mDialog
            int r1 = ICCID_INFO_LABEL_ID
            r0.removeSettingFromScreen(r1)
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r4 = r4.mDialog
            int r0 = ICCID_INFO_VALUE_ID
            r4.removeSettingFromScreen(r0)
            goto L_0x0048
        L_0x0033:
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r0 = r4.mDialog
            int r1 = ICCID_INFO_VALUE_ID
            android.content.Context r2 = r4.mContext
            android.telephony.SubscriptionInfo r3 = r4.mSubscriptionInfo
            int r3 = r3.getSubscriptionId()
            int r4 = r4.mSlotIndex
            java.lang.String r4 = com.motorola.settings.deviceinfo.DeviceUtils.getFormattedSimSerialNumber(r2, r3, r4)
            r0.setText(r1, r4)
        L_0x0048:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.updateIccidNumber():void");
    }

    /* access modifiers changed from: protected */
    public void requestForUpdateEid() {
        ThreadUtils.postOnBackgroundThread((Runnable) new SimStatusDialogController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestForUpdateEid$1() {
        ThreadUtils.postOnMainThread(new SimStatusDialogController$$ExternalSyntheticLambda1(this, getEid(this.mSlotIndex)));
    }

    /* access modifiers changed from: protected */
    public AtomicReference<String> getEid(int i) {
        String str;
        boolean z = true;
        if (this.mTelephonyManager.getActiveModemCount() > 1) {
            int intValue = ((Integer) this.mTelephonyManager.getLogicalToPhysicalSlotMapping().getOrDefault(Integer.valueOf(i), -1)).intValue();
            if (intValue != -1) {
                Iterator<UiccCardInfo> it = this.mTelephonyManager.getUiccCardsInfo().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    UiccCardInfo next = it.next();
                    if (next.getSlotIndex() == intValue) {
                        if (next.isEuicc()) {
                            str = next.getEid();
                            if (TextUtils.isEmpty(str)) {
                                str = this.mEuiccManager.createForCardId(next.getCardId()).getEid();
                            }
                        }
                    }
                }
            }
        } else if (this.mEuiccManager.isEnabled()) {
            str = this.mEuiccManager.getEid();
            if (!z || str != null) {
                return new AtomicReference<>(str);
            }
            return null;
        }
        str = null;
        z = false;
        if (!z) {
        }
        return new AtomicReference<>(str);
    }

    /* access modifiers changed from: protected */
    /* renamed from: updateEid */
    public void lambda$requestForUpdateEid$0(AtomicReference<String> atomicReference) {
        if (atomicReference == null) {
            this.mDialog.removeSettingFromScreen(EID_INFO_LABEL_ID);
            this.mDialog.removeSettingFromScreen(EID_INFO_VALUE_ID);
        } else if (atomicReference.get() != null) {
            this.mDialog.setText(EID_INFO_VALUE_ID, atomicReference.get());
        }
    }

    private boolean isImsRegistrationStateShowUp() {
        boolean z;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            return false;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
        if (configForSubId == null) {
            z = false;
        } else {
            z = configForSubId.getBoolean("show_ims_registration_status_bool");
        }
        if (!z || (!queryImsState(subscriptionId).isWifiCallingProvisioned() && !queryVoLteState(subscriptionId).isVoLteProvisioned())) {
            return false;
        }
        return true;
    }

    private void updateImsRegistrationState() {
        if (!isImsRegistrationStateShowUp()) {
            this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_LABEL_ID);
            this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_VALUE_ID);
        }
    }

    /* access modifiers changed from: private */
    public void registerImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            try {
                ImsMmTelManager.createForSubscriptionId(i).registerImsRegistrationCallback(this.mDialog.getContext().getMainExecutor(), this.mImsRegStateCallback);
            } catch (ImsException e) {
                Log.w("SimStatusDialogCtrl", "fail to register IMS status for subId=" + i, e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void unregisterImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            ImsMmTelManager.createForSubscriptionId(i).unregisterImsRegistrationCallback(this.mImsRegStateCallback);
        }
    }

    /* access modifiers changed from: private */
    public SubscriptionInfo getPhoneSubscriptionInfo(int i) {
        return SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
    }

    private int getDbm(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength next : cellSignalStrengths) {
            if (next.getDbm() != -1) {
                return next.getDbm();
            }
        }
        return -1;
    }

    private int getAsuLevel(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength next : cellSignalStrengths) {
            if (next.getAsuLevel() != -1) {
                return next.getAsuLevel();
            }
        }
        return -1;
    }

    class SimStatusDialogTelephonyCallback extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener, TelephonyCallback.SignalStrengthsListener, TelephonyCallback.ServiceStateListener, TelephonyCallback.DisplayInfoListener {
        SimStatusDialogTelephonyCallback() {
        }

        public void onDataConnectionStateChanged(int i, int i2) {
            SimStatusDialogController.this.updateDataState(i);
            SimStatusDialogController.this.updateNetworkType();
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SimStatusDialogController.this.updateSignalStrength(signalStrength);
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            SimStatusDialogController.this.updateNetworkProvider();
            SimStatusDialogController.this.updateServiceState(serviceState);
            SimStatusDialogController.this.updateRoamingStatus(serviceState);
            ServiceState unused = SimStatusDialogController.this.mPreviousServiceState = serviceState;
            SimStatusDialogController.this.updateNetworkType();
        }

        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
            TelephonyDisplayInfo unused = SimStatusDialogController.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
            SimStatusDialogController.this.updateNetworkType();
        }
    }

    /* access modifiers changed from: package-private */
    public WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }

    /* access modifiers changed from: package-private */
    public VolteQueryImsState queryVoLteState(int i) {
        return new VolteQueryImsState(this.mContext, i);
    }

    private boolean isVZWSim() {
        return this.mSubscriptionInfo.getCarrierId() == 1839;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r2 = r2.mCarrierConfigManager.getConfigForSubId(r0.getSubscriptionId());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isOem5GAPIEnabled() {
        /*
            r2 = this;
            android.telephony.SubscriptionInfo r0 = r2.mSubscriptionInfo
            r1 = 0
            if (r0 == 0) goto L_0x0018
            int r0 = r0.getSubscriptionId()
            android.telephony.CarrierConfigManager r2 = r2.mCarrierConfigManager
            android.os.PersistableBundle r2 = r2.getConfigForSubId(r0)
            if (r2 == 0) goto L_0x0018
            java.lang.String r0 = "config_enable_oem_5g_api"
            boolean r2 = r2.getBoolean(r0)
            goto L_0x0019
        L_0x0018:
            r2 = r1
        L_0x0019:
            if (r2 == 0) goto L_0x0020
            boolean r2 = android.os.Build.SOC_MANUFACTURER_IS_QCOM
            if (r2 == 0) goto L_0x0020
            r1 = 1
        L_0x0020:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.isOem5GAPIEnabled():boolean");
    }

    public boolean isUseMotoExtNrType() {
        if (!isOem5GAPIEnabled()) {
            return false;
        }
        int i = this.mNrDataIconType;
        return i == 1 || i == 2;
    }

    public int getNrType() {
        return this.mNrDataIconType == 1 ? 3 : 4;
    }

    class MotoExt5GStateListener extends MotoExtPhoneStateListener {
        public MotoExt5GStateListener(int i) {
            super(Integer.valueOf(i));
        }
    }
}
