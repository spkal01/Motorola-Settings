package com.android.settings.dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settingslib.drawer.DynamicLifecycle;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.drawer.TileUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DynamicLifecycleDataObserver extends DynamicDataObserver implements DynamicLifecycle, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = DynamicLifecycleDataObserver.class.getSimpleName();
    private List<String> mAllowedCarrierIds;
    private Integer mAvailabilityStatus = 0;
    private Uri mAvailabilityStatusUri;
    private List<String> mBlockedCarrierIds;
    private Callback mCallback;
    private Context mContext;
    private Uri mNotifyLifecycleUri;
    private SubscriptionsChangeListener mSubChangeListener;
    private boolean mSubChangeListenerRegistered;
    private Tile mTile;

    public interface Callback {
        void onAvailabilityStatusChanged(int i);
    }

    public void onAirplaneModeChanged(boolean z) {
    }

    public static DynamicLifecycleDataObserver bindLifecycle(Context context, Tile tile, Callback callback) {
        Bundle metaData = tile.getMetaData();
        if (metaData == null) {
            return null;
        }
        if (metaData.containsKey("com.android.settings.lifecycle_observer_uri") || metaData.containsKey("com.android.settings.carrier_id_allowlist") || metaData.containsKey("com.android.settings.carrier_id_blocklist")) {
            return new DynamicLifecycleDataObserver(context, tile, callback);
        }
        return null;
    }

    DynamicLifecycleDataObserver(Context context, Tile tile, Callback callback) {
        this.mContext = context;
        this.mTile = tile;
        this.mCallback = callback;
        if (tile.getMetaData() != null) {
            Bundle metaData = tile.getMetaData();
            if (metaData.containsKey("com.android.settings.lifecycle_observer_uri")) {
                this.mNotifyLifecycleUri = TileUtils.getCompleteUri(tile, "com.android.settings.lifecycle_observer_uri", "onLifecycleEvent");
                this.mAvailabilityStatusUri = TileUtils.getCompleteUri(tile, "com.android.settings.lifecycle_observer_uri", "getAvailabilityStatus");
            } else if (metaData.containsKey("com.android.settings.carrier_id_allowlist") || metaData.containsKey("com.android.settings.carrier_id_blocklist")) {
                if (TelephonyManager.from(this.mContext).getPhoneCount() == 1) {
                    List<String> parseListValuesFromMetaData = parseListValuesFromMetaData(metaData.get("com.android.settings.carrier_id_allowlist"));
                    List<String> parseListValuesFromMetaData2 = parseListValuesFromMetaData(metaData.get("com.android.settings.carrier_id_blocklist"));
                    if (!parseListValuesFromMetaData.isEmpty()) {
                        this.mAllowedCarrierIds = parseListValuesFromMetaData;
                    }
                    if (!parseListValuesFromMetaData2.isEmpty()) {
                        this.mBlockedCarrierIds = parseListValuesFromMetaData2;
                    }
                    if (!(this.mAllowedCarrierIds == null && this.mBlockedCarrierIds == null)) {
                        this.mSubChangeListener = new SubscriptionsChangeListener(this.mContext, this);
                    }
                } else {
                    Log.e(TAG, " Allowlist / Blocklist for carrier-ids is currently not supported for multi-sim devices.");
                    this.mAvailabilityStatus = 3;
                    this.mCallback.onAvailabilityStatusChanged(getAvailabilityStatus());
                }
            }
        }
        refreshAvailabilityStatus();
    }

    private List<String> parseListValuesFromMetaData(Object obj) {
        if (obj == null || obj.equals("")) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        if (obj instanceof Integer) {
            arrayList.add(String.valueOf(obj));
        } else if (obj instanceof String) {
            arrayList.addAll(Arrays.asList(((String) obj).split(",")));
        } else {
            String str = TAG;
            Log.e(str, "Wrong type: " + obj.getClass().getSimpleName() + " specified for list values.");
        }
        return arrayList;
    }

    public Uri getUri() {
        return this.mAvailabilityStatusUri;
    }

    public void onDataChanged() {
        refreshAvailabilityStatus();
    }

    public int getAvailabilityStatus() {
        int intValue;
        synchronized (this.mAvailabilityStatus) {
            if (DEBUG) {
                String str = TAG;
                Log.d(str, "mAvailabilityStatus = " + this.mAvailabilityStatus);
            }
            intValue = this.mAvailabilityStatus.intValue();
        }
        return intValue;
    }

    public void onStart(Bundle bundle) {
        if (this.mNotifyLifecycleUri != null) {
            if (DEBUG) {
                Log.d(TAG, "onStart mNotifyLifecycleUri != null");
            }
            if (bundle == null) {
                bundle = new Bundle();
                bundle.putString("lifecycle_event", "event_lifecycle_start");
            }
            notifyLifecycleEvent(bundle);
        } else if (this.mSubChangeListener != null) {
            if (DEBUG) {
                Log.d(TAG, "onStart mSubChangeListener != null");
            }
            if (!this.mSubChangeListenerRegistered) {
                this.mSubChangeListener.start();
                this.mSubChangeListenerRegistered = true;
            }
        }
    }

    public void onStop() {
        if (this.mNotifyLifecycleUri != null) {
            if (DEBUG) {
                Log.d(TAG, "onStop mNotifyLifecycleUri != null");
            }
            Bundle bundle = new Bundle();
            bundle.putString("lifecycle_event", "event_lifecycle_stop");
            notifyLifecycleEvent(bundle);
        } else if (this.mSubChangeListener != null) {
            if (DEBUG) {
                Log.d(TAG, "onStop mSubChangeListener != null");
            }
            if (this.mSubChangeListenerRegistered) {
                try {
                    this.mSubChangeListener.stop();
                } catch (Exception unused) {
                }
                this.mSubChangeListenerRegistered = false;
            }
        }
    }

    private void notifyLifecycleEvent(Bundle bundle) {
        ThreadUtils.postOnBackgroundThread((Runnable) new DynamicLifecycleDataObserver$$ExternalSyntheticLambda4(this, bundle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyLifecycleEvent$0(Bundle bundle) {
        TileUtils.getBundleFromUri(this.mContext, this.mNotifyLifecycleUri, new ArrayMap(), bundle);
    }

    public void refreshAvailabilityStatus() {
        if (this.mAvailabilityStatusUri != null) {
            if (DEBUG) {
                Log.d(TAG, "refreshAvailabilityStatus mAvailabilityStatusUri != null");
            }
            ThreadUtils.postOnBackgroundThread((Runnable) new DynamicLifecycleDataObserver$$ExternalSyntheticLambda0(this));
        } else if (this.mSubChangeListener != null) {
            if (DEBUG) {
                Log.d(TAG, "refreshAvailabilityStatus mSubChangeListener != null");
            }
            ThreadUtils.postOnBackgroundThread((Runnable) new DynamicLifecycleDataObserver$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshAvailabilityStatus$2() {
        Integer integerFromUri = TileUtils.getIntegerFromUri(this.mContext, this.mAvailabilityStatusUri, new ArrayMap(), "availability_status");
        if (integerFromUri != null) {
            synchronized (this.mAvailabilityStatus) {
                this.mAvailabilityStatus = integerFromUri;
            }
            ThreadUtils.postOnMainThread(new DynamicLifecycleDataObserver$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshAvailabilityStatus$1() {
        this.mCallback.onAvailabilityStatusChanged(getAvailabilityStatus());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshAvailabilityStatus$4() {
        int simCarrierId = TelephonyManager.from(this.mContext).getSimCarrierId();
        if (DEBUG) {
            Log.d(TAG, "carrierId = " + simCarrierId);
        }
        synchronized (this.mAvailabilityStatus) {
            List<String> list = this.mAllowedCarrierIds;
            int i = 1;
            if (list != null) {
                if (!list.contains(Integer.toString(simCarrierId))) {
                    i = 2;
                }
                this.mAvailabilityStatus = Integer.valueOf(i);
            } else {
                List<String> list2 = this.mBlockedCarrierIds;
                if (list2 != null) {
                    if (list2.contains(Integer.toString(simCarrierId))) {
                        i = 2;
                    }
                    this.mAvailabilityStatus = Integer.valueOf(i);
                }
            }
        }
        ThreadUtils.postOnMainThread(new DynamicLifecycleDataObserver$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshAvailabilityStatus$3() {
        this.mCallback.onAvailabilityStatusChanged(getAvailabilityStatus());
    }

    public void onSubscriptionsChanged() {
        refreshAvailabilityStatus();
    }
}
