package com.motorola.settings.relativevolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.CollectionUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.multivolume.AppVolumeState;
import com.motorola.settings.relativevolume.AppVolumeAdapter;
import com.motorola.settings.relativevolume.MultiVolumeHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelativeVolumePreferenceController extends BasePreferenceController implements LifecycleObserver {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    private static final long INTERVAL_RELATIVE_VOLUMES_REFRESH = 150;
    public static final String KEY_RELATIVE_VOLUME = "relative_volume";
    private static final int MSG_REFRESH_ALL_DATA_DELAY = 2;
    private static final int MSG_REFRESH_FEATURE_DISABLED = 1;
    private static final int MSG_REFRESH_MUSIC_VOLUME_DELAY = 3;
    private static final String TAG = "RV-RelVolumePrefCtrl";
    private static final long USER_ATTEMPT_GRACE_PERIOD = 1000;
    private AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public final Map<Integer, AppVolumeState> mCachedAppVolumeMap;
    protected Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsStart;
    /* access modifiers changed from: private */
    public boolean mIsUserTracking;
    private int mMaxMusicVolume;
    protected RelativeVolumePreference mPreference;
    private RelativeVolumeReceiver mRelativeVolumeReceiver;
    private final MultiVolumeHelper.UICallback mUICallback;
    /* access modifiers changed from: private */
    public UIHandler mUIHandler;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public String getPreferenceKey() {
        return KEY_RELATIVE_VOLUME;
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public boolean useDynamicSliceSummary() {
        return true;
    }

    private class MyUICallback implements MultiVolumeHelper.UICallback {
        private MyUICallback() {
        }

        public void onMusicVolumeChanged(int i, double d) {
            if (RelativeVolumePreferenceController.this.mIsStart) {
                if (RelativeVolumePreferenceController.DEBUG) {
                    Log.d(RelativeVolumePreferenceController.TAG, "onMusicVolumeChanged (musicProgress: " + i + " musicPercentage: " + d);
                }
                if (i >= 0) {
                    RelativeVolumePreferenceController.this.updateMusicVolume(RelativeVolumeUtils.getMusicLevel(i));
                }
            }
        }

        public void onMultiVolumeChanged(int i, double d, List<AppVolumeState> list) {
            if (RelativeVolumePreferenceController.this.mIsStart) {
                if (RelativeVolumePreferenceController.DEBUG) {
                    Log.d(RelativeVolumePreferenceController.TAG, "onMultiVolumeRowsChanged (musicProgress: " + i + " musicPercentage: " + d + "),app volume: " + RelativeVolumeUtils.appVolumesToString(list));
                }
                if (i >= 0) {
                    RelativeVolumePreferenceController.this.updateMusicVolume(RelativeVolumeUtils.getMusicLevel(i));
                }
                RelativeVolumePreferenceController.this.loadDataToUI(list);
            }
        }

        public void onAppVolumesChanged(List<AppVolumeState> list) {
            if (RelativeVolumePreferenceController.DEBUG) {
                Log.d(RelativeVolumePreferenceController.TAG, "onAppVolumesChanged app volume: " + RelativeVolumeUtils.appVolumesToString(list));
            }
            if (RelativeVolumePreferenceController.this.mIsStart) {
                RelativeVolumePreferenceController.this.loadDataToUI(list);
            }
        }

        public void safeMediaVolumeHandled(int i) {
            if (RelativeVolumePreferenceController.DEBUG) {
                Log.d(RelativeVolumePreferenceController.TAG, "safeMediaVolumeHandled action: " + i);
            }
            if (RelativeVolumePreferenceController.this.mIsStart) {
                RelativeVolumePreference relativeVolumePreference = RelativeVolumePreferenceController.this.mPreference;
                if (relativeVolumePreference != null) {
                    relativeVolumePreference.onSafeVolumeAction(i);
                }
                RelativeVolumePreferenceController.this.delayUpdate(SystemClock.uptimeMillis() + RelativeVolumePreferenceController.USER_ATTEMPT_GRACE_PERIOD);
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadDataToUI(List<AppVolumeState> list) {
        updateAppVolumes(RelativeVolumeUtils.loadIconForApps(this.mContext, list), true);
    }

    private class UIHandler extends Handler {
        private UIHandler() {
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                RelativeVolumePreferenceController.this.updateAllData((List<AppVolumeState>) null);
            } else if (i == 2) {
                RelativeVolumePreferenceController relativeVolumePreferenceController = RelativeVolumePreferenceController.this;
                relativeVolumePreferenceController.updateAdapter(relativeVolumePreferenceController.getPlayingAppVolumesFromCache());
            } else if (i == 3) {
                RelativeVolumePreferenceController.this.doUpdateMusicVolume(((Integer) message.obj).intValue());
            }
        }
    }

    public RelativeVolumePreferenceController(Context context) {
        this(context, KEY_RELATIVE_VOLUME);
    }

    public RelativeVolumePreferenceController(Context context, String str) {
        super(context, str);
        this.mMaxMusicVolume = 15;
        this.mUICallback = new MyUICallback();
        this.mContext = context.getApplicationContext();
        this.mRelativeVolumeReceiver = new RelativeVolumeReceiver();
        this.mCachedAppVolumeMap = new HashMap();
        AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mAudioManager = audioManager;
        if (audioManager != null) {
            this.mMaxMusicVolume = audioManager.getStreamMaxVolume(3);
        }
        this.mIsUserTracking = false;
    }

    public final int getAvailabilityStatus() {
        return !RelativeVolumeUtils.isRelativeVolumeFeatureOn(this.mContext.getApplicationContext()) ? 3 : 1;
    }

    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_RELATIVE_VOLUME);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RelativeVolumePreference relativeVolumePreference = (RelativeVolumePreference) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mPreference = relativeVolumePreference;
        relativeVolumePreference.setVisible(false);
        setVolumeStateBack();
    }

    public void updateState(Preference preference) {
        if (preference != null && !shouldHidePreference()) {
            preference.setVisible(shouldShowPreference(getAdapterData()));
            delayUpdate(SystemClock.uptimeMillis() + USER_ATTEMPT_GRACE_PERIOD);
        }
    }

    private boolean shouldHidePreference() {
        if (this.mPreference == null) {
            return true;
        }
        if (RelativeVolumeUtils.isRelativeVolumeFeatureOn(this.mContext.getApplicationContext())) {
            return false;
        }
        if (DEBUG) {
            Log.e(TAG, "feature not support on this device");
        }
        this.mPreference.setVisible(false);
        return true;
    }

    public void updateAllData(List<AppVolumeState> list) {
        if (!shouldHidePreference()) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("updateAllData size: ");
                sb.append(list == null ? 0 : list.size());
                Log.d(TAG, sb.toString());
            }
            this.mPreference.updateAllData(list);
            this.mPreference.setVisible(shouldShowPreference(list));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mUIHandler = new UIHandler();
        this.mIsStart = true;
        this.mContext.registerReceiver(this.mRelativeVolumeReceiver, new IntentFilter("com.motorola.dynamicvolume.action.RELEVANT_VOLUME_CONFIGURATION_CHANGED_ACTION"), "android.permission.MODIFY_AUDIO_ROUTING", (Handler) null);
        MultiVolumeHelper.getInstance(this.mContext).setUIHandlerAndCallback(new Handler(), this.mUICallback);
        if (!MultiVolumeHelper.getInstance(this.mContext).isServiceConnected()) {
            if (DEBUG) {
                Log.d(TAG, "onStart call bindMultiVolumeService");
            }
            MultiVolumeHelper.getInstance(this.mContext).bindMultiVolumeService();
        } else if (DEBUG) {
            Log.d(TAG, "onStart service already bound");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        int i;
        List<AppVolumeState> cachedAppList = MultiVolumeHelper.getInstance(this.mContext).getCachedAppList();
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("onResume get app volume size: ");
            if (cachedAppList == null) {
                i = 0;
            } else {
                i = cachedAppList.size();
            }
            sb.append(i);
            Log.d(TAG, sb.toString());
        }
        loadDataToUI(cachedAppList);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mIsStart = false;
        UIHandler uIHandler = this.mUIHandler;
        if (uIHandler != null) {
            uIHandler.removeCallbacksAndMessages((Object) null);
            this.mUIHandler = null;
        }
        MultiVolumeHelper.getInstance(this.mContext).removeCallback(this.mUICallback);
        try {
            this.mContext.unregisterReceiver(this.mRelativeVolumeReceiver);
        } catch (Exception unused) {
            Log.e(TAG, "unregisterReceiver failed ");
        }
    }

    private List<AppVolumeState> getAdapterData() {
        RelativeVolumePreference relativeVolumePreference;
        if (!this.mIsStart || (relativeVolumePreference = this.mPreference) == null) {
            return null;
        }
        return relativeVolumePreference.getData();
    }

    private boolean shouldShowPreference(List<AppVolumeState> list) {
        return RelativeVolumeUtils.getAppCount(list) != 0;
    }

    private void resetPlayAndForegroundState(boolean z) {
        Map<Integer, AppVolumeState> map = this.mCachedAppVolumeMap;
        if (map != null) {
            for (Map.Entry next : map.entrySet()) {
                AppVolumeState appVolumeState = (AppVolumeState) next.getValue();
                if (appVolumeState != null) {
                    appVolumeState.playing = false;
                    if (z) {
                        appVolumeState.foreground = false;
                        appVolumeState.foregroundSettings = false;
                    }
                    this.mCachedAppVolumeMap.put((Integer) next.getKey(), appVolumeState);
                }
            }
        }
    }

    private void updateVolumeCache(List<AppVolumeState> list, boolean z) {
        resetPlayAndForegroundState(z);
        if (!CollectionUtils.isEmpty(list)) {
            for (AppVolumeState next : list) {
                int i = next.packageUid;
                AppVolumeState appVolumeState = this.mCachedAppVolumeMap.get(Integer.valueOf(i));
                if (appVolumeState != null) {
                    appVolumeState.timeInMills = next.timeInMills;
                    appVolumeState.playing = next.playing;
                    appVolumeState.ratio = next.ratio;
                    appVolumeState.storedPercentage = next.storedPercentage;
                    int i2 = next.appLevel;
                    appVolumeState.appLevel = i2;
                    double d = next.percentage;
                    if (d >= 0.0d) {
                        appVolumeState.percentage = d;
                    } else {
                        appVolumeState.percentage = ((double) i2) / (((double) this.mMaxMusicVolume) * 1.0d);
                    }
                    if (i2 > 0) {
                        appVolumeState.lastSetLevel = i2;
                    }
                    if (z) {
                        appVolumeState.foreground = next.foregroundSettings | next.foreground;
                    }
                } else {
                    appVolumeState = next.copy();
                }
                if (DEBUG) {
                    Log.d(TAG, "updateVolumeCache package: " + appVolumeState.packageName + " appLevel: " + appVolumeState.appLevel + " playing: " + appVolumeState.playing + " storedPercentage: " + appVolumeState.storedPercentage);
                }
                this.mCachedAppVolumeMap.put(Integer.valueOf(i), appVolumeState);
            }
        }
    }

    /* access modifiers changed from: private */
    public List<AppVolumeState> getPlayingAppVolumesFromCache() {
        if (this.mCachedAppVolumeMap != null) {
            ArrayList arrayList = new ArrayList();
            for (AppVolumeState next : this.mCachedAppVolumeMap.values()) {
                if (DEBUG) {
                    Log.d(TAG, "getAppsFromCache pack: " + next.packageName + " appLevel: " + next.appLevel + " playing: " + next.playing + " storedPercentage: " + next.storedPercentage + " foreground: " + next.foreground + " foregroundSettings: " + next.foregroundSettings);
                }
                if (next != null && (next.playing || next.foreground || next.foregroundSettings)) {
                    arrayList.add(next.copy());
                }
            }
            return RelativeVolumeUtils.sortAppVolumeStates(arrayList);
        }
        if (DEBUG) {
            Log.d(TAG, "getPlayingAppVolumesFromCache Map is null: ");
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void doUpdateMusicVolume(int i) {
        if (this.mPreference != null) {
            if (DEBUG) {
                Log.d(TAG, "do update music volume to: " + i);
            }
            this.mPreference.updateMusicVolume(i);
        }
    }

    /* access modifiers changed from: private */
    public void updateMusicVolume(int i) {
        if (this.mIsStart) {
            RelativeVolumePreference relativeVolumePreference = this.mPreference;
            if (relativeVolumePreference == null) {
                return;
            }
            if (!relativeVolumePreference.isUserTracking() && !this.mIsUserTracking) {
                long userAttemptMills = this.mPreference.getUserAttemptMills();
                if (inGracePeriod(userAttemptMills)) {
                    if (DEBUG) {
                        Log.d(TAG, "delay 1s to refresh music volume as user is tracking");
                    }
                    delayUpdateMusicVolume(userAttemptMills + USER_ATTEMPT_GRACE_PERIOD, i);
                    return;
                }
                doUpdateMusicVolume(i);
            } else if (DEBUG) {
                Log.d(TAG, "not update music volume when user is tracking");
            }
        } else if (DEBUG) {
            Log.d(TAG, "not update music volume as controller stoped");
        }
    }

    private void updateAppVolumes(List<AppVolumeState> list, boolean z) {
        updateVolumeCache(list, z);
        updateAdapter(list);
    }

    /* access modifiers changed from: private */
    public void updateAdapter(List<AppVolumeState> list) {
        if (this.mIsStart) {
            RelativeVolumePreference relativeVolumePreference = this.mPreference;
            if (relativeVolumePreference == null) {
                return;
            }
            if (!relativeVolumePreference.isUserTracking() && !this.mIsUserTracking) {
                long userAttemptMills = this.mPreference.getUserAttemptMills();
                if (inGracePeriod(userAttemptMills)) {
                    if (DEBUG) {
                        Log.d(TAG, "delay 1s to refresh app volumes as user is tracking");
                    }
                    delayUpdate(userAttemptMills + USER_ATTEMPT_GRACE_PERIOD);
                    return;
                }
                updateAllData(list);
            } else if (DEBUG) {
                Log.d(TAG, "do not update ui when user is tracking");
            }
        } else if (DEBUG) {
            Log.d(TAG, "not update adapter as controller stoped");
        }
    }

    /* access modifiers changed from: private */
    public void notifyFeatureDisabled() {
        if (this.mUIHandler != null) {
            if (DEBUG) {
                Log.d(TAG, "notifyFeatureDisabled");
            }
            this.mUIHandler.removeMessages(1);
            this.mUIHandler.sendEmptyMessageDelayed(1, INTERVAL_RELATIVE_VOLUMES_REFRESH);
        }
    }

    /* access modifiers changed from: private */
    public void delayUpdate(long j) {
        if (this.mUIHandler != null) {
            if (DEBUG) {
                Log.d(TAG, "delayUpdate updateMills: " + j);
            }
            this.mUIHandler.removeMessages(2);
            this.mUIHandler.sendEmptyMessageAtTime(2, j);
        }
    }

    private void delayUpdateMusicVolume(long j, int i) {
        if (this.mUIHandler != null) {
            if (DEBUG) {
                Log.d(TAG, "delayUpdateMusicVolume updateMills: " + j);
            }
            this.mUIHandler.removeMessages(3);
            Message obtainMessage = this.mUIHandler.obtainMessage(3);
            obtainMessage.obj = Integer.valueOf(i);
            this.mUIHandler.sendMessageAtTime(obtainMessage, j);
        }
    }

    /* access modifiers changed from: private */
    public boolean inGracePeriod(long j) {
        return SystemClock.uptimeMillis() - j < USER_ATTEMPT_GRACE_PERIOD;
    }

    private void setVolumeStateBack() {
        RelativeVolumePreference relativeVolumePreference = this.mPreference;
        if (relativeVolumePreference != null) {
            relativeVolumePreference.setVolumeStateCallback(new AppVolumeStateCallBack(this.mContext));
        }
    }

    private class AppVolumeStateCallBack implements AppVolumeAdapter.VolumeStateCallback {
        private Context mCtx;

        public AppVolumeStateCallBack(Context context) {
            this.mCtx = context;
        }

        public void onTrackingStateChanged(boolean z) {
            boolean unused = RelativeVolumePreferenceController.this.mIsUserTracking = z;
            if (!RelativeVolumePreferenceController.this.mIsUserTracking && RelativeVolumePreferenceController.this.mUIHandler != null) {
                RelativeVolumePreference relativeVolumePreference = RelativeVolumePreferenceController.this.mPreference;
                long userAttemptMills = relativeVolumePreference == null ? 0 : relativeVolumePreference.getUserAttemptMills();
                boolean access$1200 = RelativeVolumePreferenceController.this.inGracePeriod(userAttemptMills);
                if (RelativeVolumePreferenceController.DEBUG) {
                    Log.d(RelativeVolumePreferenceController.TAG, "track stoped, refresh data, inGracePeriod: " + access$1200);
                }
                RelativeVolumePreferenceController.this.mUIHandler.removeMessages(2);
                if (access$1200) {
                    RelativeVolumePreferenceController.this.mUIHandler.sendEmptyMessageAtTime(2, userAttemptMills + RelativeVolumePreferenceController.USER_ATTEMPT_GRACE_PERIOD);
                } else {
                    RelativeVolumePreferenceController.this.mUIHandler.sendEmptyMessageDelayed(2, RelativeVolumePreferenceController.INTERVAL_RELATIVE_VOLUMES_REFRESH);
                }
            }
        }

        public void onAppVolumeChanged(String str, int i, int i2, double d, int i3) {
            AppVolumeState appVolumeState;
            MultiVolumeHelper.getInstance(this.mCtx).changeAppRow(str, i, i2, d, i3);
            if (RelativeVolumePreferenceController.this.mCachedAppVolumeMap != null && (appVolumeState = (AppVolumeState) RelativeVolumePreferenceController.this.mCachedAppVolumeMap.get(Integer.valueOf(i))) != null) {
                appVolumeState.percentage = d;
                appVolumeState.appLevel = i2;
                appVolumeState.lastSetLevel = i2;
                if (RelativeVolumePreferenceController.DEBUG) {
                    Log.d(RelativeVolumePreferenceController.TAG, "onAppVolumeChanged update cache for package: " + appVolumeState.packageName + " appLevel: " + appVolumeState.appLevel + " percentage: " + appVolumeState.percentage + " storedPercentage: " + appVolumeState.storedPercentage);
                }
                RelativeVolumePreferenceController.this.mCachedAppVolumeMap.put(Integer.valueOf(i), appVolumeState);
            }
        }

        public void onMusicVolumeChanged(int i) {
            MultiVolumeHelper.getInstance(this.mCtx).changeMusicRow(i, -1.0d);
        }
    }

    public class RelativeVolumeReceiver extends BroadcastReceiver {
        public RelativeVolumeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (RelativeVolumePreferenceController.DEBUG) {
                Log.d(RelativeVolumePreferenceController.TAG, "onReceive getAction: " + intent.getAction());
            }
            if ("com.motorola.dynamicvolume.action.RELEVANT_VOLUME_CONFIGURATION_CHANGED_ACTION".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("com.motorola.dynamicvolume.EXTRA_CHANGE_REASON", 4);
                if (RelativeVolumePreferenceController.DEBUG) {
                    Log.d(RelativeVolumePreferenceController.TAG, "relative volume change reason: " + intExtra);
                }
                if (intExtra == 6) {
                    boolean booleanExtra = intent.getBooleanExtra("com.motorola.dynamicvolume.EXTRA_FEATURE_STATUS", false);
                    int intExtra2 = intent.getIntExtra("com.motorola.dynamicvolume.EXTRA_USER_ID", -1);
                    if (RelativeVolumePreferenceController.DEBUG) {
                        Log.d(RelativeVolumePreferenceController.TAG, "[MultiVolumeService]  serviceActive: " + booleanExtra);
                    }
                    if (booleanExtra) {
                        MultiVolumeHelper.getInstance(context).bindMultiVolumeService(intExtra2);
                        return;
                    }
                    RelativeVolumePreferenceController.this.notifyFeatureDisabled();
                    MultiVolumeHelper.getInstance(context).unBindMultiVolumeService();
                }
            }
        }
    }
}
