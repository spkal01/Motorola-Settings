package com.motorola.settings.network.att;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.text.TextUtils;
import android.util.Log;

public class WfcSwitchManager extends ContentObserver {
    private Context mContext;
    private boolean mDisabledUi;
    private boolean mInCallStatus = false;
    private WfcChangeListener mListener;
    private Uri mOnStopNotifyUri;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            if (!TextUtils.isEmpty(str)) {
                WfcSwitchManager.this.requestRefresh();
            }
        }
    };
    private WfcSwitchStates mState = WfcSwitchStates.UNRESOLVED;
    private int mSubId;
    private String mSummary;
    private Uri mSummaryUri;
    private TelephonyManager mTm;

    public interface WfcChangeListener {
        void toggleStateChanged();
    }

    private enum WfcSwitchStates {
        UNRESOLVED,
        OFF,
        ENABLING,
        ON
    }

    public WfcSwitchManager(Context context, WfcChangeListener wfcChangeListener, Uri uri, Uri uri2, int i) {
        super(new Handler());
        this.mListener = wfcChangeListener;
        this.mContext = context;
        this.mTm = (TelephonyManager) context.getSystemService("phone");
        this.mSummaryUri = uri;
        this.mOnStopNotifyUri = uri2;
        this.mSubId = i;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        requestRefresh();
    }

    public void register() {
        this.mContext.getContentResolver().registerContentObserver(Uri.withAppendedPath(SubscriptionManager.WFC_ENABLED_CONTENT_URI, String.valueOf(this.mSubId)), false, this);
        if (this.mSummaryUri != null) {
            this.mContext.getContentResolver().registerContentObserver(this.mSummaryUri, false, this);
        }
        this.mTm.listen(this.mPhoneStateListener, 32);
        this.mState = WfcSwitchStates.UNRESOLVED;
        this.mInCallStatus = inCall();
        requestRefresh();
    }

    public void unregister() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
        this.mTm.listen(this.mPhoneStateListener, 0);
    }

    private void initStates() {
        readSummaryUri();
        if (isWfcEnabledByUser()) {
            this.mState = WfcSwitchStates.ON;
        } else if (isEnablingWfc()) {
            this.mState = WfcSwitchStates.ENABLING;
        } else {
            this.mState = WfcSwitchStates.OFF;
        }
        this.mInCallStatus = inCall();
    }

    public boolean isWfcStateON() {
        return this.mState == WfcSwitchStates.ON;
    }

    public boolean isWfcStateEnabling() {
        return this.mState == WfcSwitchStates.ENABLING;
    }

    private void readSummaryUri() {
        if (this.mSummaryUri != null) {
            Cursor cursor = null;
            try {
                cursor = this.mContext.getContentResolver().query(this.mSummaryUri, new String[]{"value"}, (String) null, (String[]) null, (String) null);
                if (cursor != null && cursor.moveToFirst()) {
                    boolean z = false;
                    this.mSummary = cursor.getString(0);
                    if (cursor.moveToNext()) {
                        if (cursor.getInt(0) == 1) {
                            z = true;
                        }
                        this.mDisabledUi = z;
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private boolean isEnablingWfc() {
        return !TextUtils.isEmpty(this.mSummary);
    }

    private boolean isWfcEnabledByUser() {
        try {
            ImsMmTelManager mmTelManager = getMmTelManager();
            if (mmTelManager != null) {
                return mmTelManager.isVoWiFiSettingEnabled();
            }
            return false;
        } catch (Throwable th) {
            Log.i("Settings.WfcChangeObserver", "isWfcEnabledByUser() exception:" + th.getMessage());
            return false;
        }
    }

    public boolean inWifiCall() {
        return this.mInCallStatus;
    }

    /* access modifiers changed from: private */
    public void requestRefresh() {
        WfcSwitchStates wfcSwitchStates = this.mState;
        boolean z = this.mInCallStatus;
        boolean z2 = this.mDisabledUi;
        initStates();
        if (this.mListener == null) {
            return;
        }
        if (wfcSwitchStates != this.mState || z != this.mInCallStatus || z2 != this.mDisabledUi) {
            if (!Build.IS_USER) {
                StringBuilder sb = new StringBuilder();
                sb.append("requestRefresh notify current state = ");
                sb.append(this.mState.toString());
                sb.append(" and call state = ");
                sb.append(this.mInCallStatus ? "IN CALL" : "NOT IN CALL");
                Log.i("Settings.WfcChangeObserver", sb.toString());
            }
            this.mListener.toggleStateChanged();
        }
    }

    public void notifyOnStop() {
        if (this.mOnStopNotifyUri != null) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("value", Boolean.TRUE);
            this.mContext.getContentResolver().update(this.mOnStopNotifyUri, contentValues, (String) null, (String[]) null);
        }
    }

    private boolean inCall() {
        if (this.mTm.createForSubscriptionId(this.mSubId).getCallState() == 0) {
            return false;
        }
        try {
            ImsMmTelManager mmTelManager = getMmTelManager();
            if (mmTelManager != null) {
                return mmTelManager.isAvailable(1, 1);
            }
            return false;
        } catch (Throwable th) {
            Log.i("Settings.WfcChangeObserver", "isWfcEnabledByUser() exception:" + th.getMessage());
            return false;
        }
    }

    public String getSummary() {
        return this.mSummary;
    }

    public boolean disableUi() {
        return this.mDisabledUi;
    }

    private ImsMmTelManager getMmTelManager() {
        ImsManager imsManager;
        if (!((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(this.mSubId) || (imsManager = (ImsManager) this.mContext.getSystemService(ImsManager.class)) == null) {
            return null;
        }
        return imsManager.getImsMmTelManager(this.mSubId);
    }
}
