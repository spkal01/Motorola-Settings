package com.motorola.settings.relativevolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.multivolume.AppVolumeState;
import com.motorola.settings.relativevolume.MultiVolumeHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelativeVolumeWorker extends SliceBackgroundWorker {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    private List<AppVolumeState> mAvailableList;
    private Context mContext;
    private UIHandler mHandler;
    /* access modifiers changed from: private */
    public boolean mIsSlicePinned;
    private int mLastAppCount;
    private long mLastRefreshTimeInMills;
    private RelativeVolumeReceiver mRelativeVolumeReceiver;
    private final MultiVolumeHelper.UICallback mUICallback = new MyUICallback();

    private class UIHandler extends Handler {
        private UIHandler() {
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                List list = null;
                Object obj = message.obj;
                if (obj != null) {
                    list = (List) obj;
                }
                RelativeVolumeWorker.this.notifyUpdate(list);
            }
        }
    }

    public RelativeVolumeWorker(Context context, Uri uri) {
        super(context, uri);
        this.mContext = context;
        this.mRelativeVolumeReceiver = new RelativeVolumeReceiver();
        this.mLastAppCount = 0;
    }

    /* access modifiers changed from: protected */
    public void onSlicePinned() {
        int i;
        this.mHandler = new UIHandler();
        this.mAvailableList = new ArrayList();
        this.mIsSlicePinned = true;
        this.mLastRefreshTimeInMills = 0;
        this.mContext.registerReceiver(this.mRelativeVolumeReceiver, getIntentFilter(), "android.permission.MODIFY_AUDIO_ROUTING", (Handler) null);
        boolean z = DEBUG;
        if (z) {
            Log.d("RV-RelativeVolumeWorker", "onSlicePinned");
        }
        MultiVolumeHelper.getInstance(this.mContext.getApplicationContext()).setUIHandlerAndCallback(new Handler(), this.mUICallback);
        if (MultiVolumeHelper.getInstance(this.mContext.getApplicationContext()).isServiceConnected()) {
            List<AppVolumeState> cachedAppList = MultiVolumeHelper.getInstance(this.mContext.getApplicationContext()).getCachedAppList();
            if (z) {
                StringBuilder sb = new StringBuilder();
                sb.append("onSlicePinned get app volume size: ");
                if (cachedAppList == null) {
                    i = 0;
                } else {
                    i = cachedAppList.size();
                }
                sb.append(i);
                Log.d("RV-RelativeVolumeWorker", sb.toString());
            }
            notifyUpdate(cachedAppList);
            return;
        }
        if (z) {
            Log.d("RV-RelativeVolumeWorker", "onSlicePinned call bindMultiVolumeService");
        }
        notifyUpdate((List<AppVolumeState>) null);
        MultiVolumeHelper.getInstance(this.mContext.getApplicationContext()).bindMultiVolumeService();
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.motorola.dynamicvolume.action.RELEVANT_VOLUME_CONFIGURATION_CHANGED_ACTION");
        return intentFilter;
    }

    /* access modifiers changed from: protected */
    public void onSliceUnpinned() {
        this.mIsSlicePinned = false;
        if (DEBUG) {
            Log.d("RV-RelativeVolumeWorker", "onSliceUnpinned call removeCallback");
        }
        MultiVolumeHelper.getInstance(this.mContext.getApplicationContext()).removeCallback(this.mUICallback);
        UIHandler uIHandler = this.mHandler;
        if (uIHandler != null) {
            uIHandler.removeMessages(1);
            this.mHandler = null;
        }
        try {
            this.mContext.unregisterReceiver(this.mRelativeVolumeReceiver);
        } catch (Exception unused) {
            Log.e("RV-RelativeVolumeWorker", "unregisterReceiver failed ");
        }
        this.mAvailableList = null;
    }

    private void doUpdateResult(List<AppVolumeState> list) {
        UIHandler uIHandler;
        int appCount = RelativeVolumeUtils.getAppCount(list);
        updateResults(list);
        if (!(this.mLastAppCount == 0 || appCount != 0 || (uIHandler = this.mHandler) == null)) {
            uIHandler.removeMessages(1);
            Message obtainMessage = this.mHandler.obtainMessage(1);
            obtainMessage.obj = list == null ? new ArrayList() : null;
            this.mHandler.sendMessageDelayed(obtainMessage, 1000);
            if (DEBUG) {
                Log.d("RV-RelativeVolumeWorker", "update again due to app count change from nonzero to zero");
            }
        }
        this.mLastAppCount = appCount;
    }

    /* access modifiers changed from: private */
    public void notifyUpdate(List<AppVolumeState> list) {
        UIHandler uIHandler = this.mHandler;
        if (uIHandler != null) {
            uIHandler.removeMessages(1);
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.mLastRefreshTimeInMills < 1000) {
                if (DEBUG) {
                    Log.d("RV-RelativeVolumeWorker", "do not refresh slice too frequently");
                }
                long min = Math.min(Math.abs(currentTimeMillis - this.mLastRefreshTimeInMills), 1000);
                Message obtainMessage = this.mHandler.obtainMessage(1);
                obtainMessage.obj = list;
                this.mHandler.sendMessageDelayed(obtainMessage, min);
                return;
            }
            this.mLastRefreshTimeInMills = currentTimeMillis;
            updateAvailableList(list);
            doUpdateResult(this.mAvailableList);
        }
    }

    private void updateAvailableList(List<AppVolumeState> list) {
        this.mAvailableList = list;
    }

    public void close() throws IOException {
        this.mRelativeVolumeReceiver = null;
    }

    private class RelativeVolumeReceiver extends BroadcastReceiver {
        private RelativeVolumeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("com.motorola.dynamicvolume.EXTRA_CHANGE_REASON", 4);
            if (RelativeVolumeWorker.DEBUG) {
                Log.d("RV-RelativeVolumeWorker", "onReceive getAction: " + intent.getAction() + " change reason: " + intExtra);
            }
            if (!RelativeVolumeWorker.this.mIsSlicePinned) {
                if (RelativeVolumeWorker.DEBUG) {
                    Log.d("RV-RelativeVolumeWorker", "Slice not pinned, ignore this broadcast");
                }
            } else if (intExtra == 6) {
                boolean booleanExtra = intent.getBooleanExtra("com.motorola.dynamicvolume.EXTRA_FEATURE_STATUS", false);
                int intExtra2 = intent.getIntExtra("com.motorola.dynamicvolume.EXTRA_USER_ID", -1);
                if (RelativeVolumeWorker.DEBUG) {
                    Log.d("RV-RelativeVolumeWorker", "onReceive serviceActive: " + booleanExtra);
                }
                if (booleanExtra) {
                    MultiVolumeHelper.getInstance(context.getApplicationContext()).bindMultiVolumeService(intExtra2);
                    return;
                }
                RelativeVolumeWorker.this.notifyUpdate((List<AppVolumeState>) null);
                MultiVolumeHelper.getInstance(context.getApplicationContext()).unBindMultiVolumeService(intExtra2);
            }
        }
    }

    private class MyUICallback implements MultiVolumeHelper.UICallback {
        public void onMusicVolumeChanged(int i, double d) {
        }

        public void safeMediaVolumeHandled(int i) {
        }

        private MyUICallback() {
        }

        public void onMultiVolumeChanged(int i, double d, List<AppVolumeState> list) {
            if (RelativeVolumeWorker.DEBUG) {
                Log.d("RV-RelativeVolumeWorker", "onAppVolumesChanged (musicProgress: " + i + " musicPercentage: " + d + "), app volume: " + RelativeVolumeUtils.appVolumesToString(list));
            }
            if (RelativeVolumeWorker.this.mIsSlicePinned) {
                RelativeVolumeWorker.this.notifyUpdate(list);
            }
        }

        public void onAppVolumesChanged(List<AppVolumeState> list) {
            if (RelativeVolumeWorker.DEBUG) {
                Log.d("RV-RelativeVolumeWorker", "onAppVolumesChanged app volume: " + RelativeVolumeUtils.appVolumesToString(list));
            }
            if (RelativeVolumeWorker.this.mIsSlicePinned) {
                RelativeVolumeWorker.this.notifyUpdate(list);
            }
        }
    }
}
