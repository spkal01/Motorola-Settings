package com.motorola.settings.relativevolume;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import com.motorola.multivolume.AppVolumeState;
import com.motorola.multivolume.IMultiVolumeController;
import com.motorola.multivolume.IMultiVolumeService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiVolumeHelper {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    private static MultiVolumeHelper sInstance;
    /* access modifiers changed from: private */
    public List<AppVolumeState> mCachedAppVolumeStateList;
    /* access modifiers changed from: private */
    public int mCachedMusicProgress = -1;
    private Context mContext;
    private IMultiVolumeController mMultiVolumeController = new MVC();
    private SparseArray<MultiVolumeServiceConnection> mMultiVolumeServiceConnections = new SparseArray<>();
    private SparseArray<IMultiVolumeService> mMultiVolumeServices = new SparseArray<>();
    /* access modifiers changed from: private */
    public final ConcurrentHashMap<UICallback, Handler> mUICallbackMap = new ConcurrentHashMap<>();
    private int mUserId = -1;

    public interface UICallback {
        void onAppVolumesChanged(List<AppVolumeState> list);

        void onMultiVolumeChanged(int i, double d, List<AppVolumeState> list);

        void onMusicVolumeChanged(int i, double d);

        void safeMediaVolumeHandled(int i);
    }

    public MultiVolumeHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mUserId = UserHandle.myUserId();
    }

    public static MultiVolumeHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MultiVolumeHelper.class) {
                if (sInstance == null) {
                    sInstance = new MultiVolumeHelper(context);
                }
            }
        }
        return sInstance;
    }

    public void setUIHandlerAndCallback(Handler handler, UICallback uICallback) {
        ConcurrentHashMap<UICallback, Handler> concurrentHashMap;
        if (uICallback != null && handler != null && (concurrentHashMap = this.mUICallbackMap) != null) {
            concurrentHashMap.put(uICallback, handler);
        }
    }

    public void removeCallback(UICallback uICallback) {
        ConcurrentHashMap<UICallback, Handler> concurrentHashMap = this.mUICallbackMap;
        if (concurrentHashMap != null) {
            concurrentHashMap.remove(uICallback);
        }
    }

    public void changeMusicRow(int i, double d) {
        onChangeMusicRow(i * 100, d);
    }

    public void onChangeMusicRow(int i, double d) {
        try {
            if (this.mMultiVolumeServices != null) {
                for (int i2 = 0; i2 < this.mMultiVolumeServices.size(); i2++) {
                    int keyAt = this.mMultiVolumeServices.keyAt(i2);
                    IMultiVolumeService valueAt = this.mMultiVolumeServices.valueAt(i2);
                    if (valueAt != null) {
                        if (DEBUG) {
                            Log.d("RV-MultiVolumeHelper", "changeMusicRow progress: " + i);
                        }
                        valueAt.changeMusicRow(i, d);
                    } else if (DEBUG) {
                        Log.d("RV-MultiVolumeHelper", "MultiVolumeService is not ready, unable to onChangeMusicRow, userId: " + keyAt);
                    }
                }
            } else if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MultiVolumeService is not ready, unable to onChangeMusicRow");
            }
        } catch (RemoteException | SecurityException e) {
            if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "Unable to use MultiVolumeService,unable to onChangeMusicRow", e);
            }
        }
    }

    public class AppRowStatus {
        String packageName;
        double percentage;
        int progress;
        int touchType;
        int uid;

        AppRowStatus(String str, int i, int i2, double d, int i3) {
            this.packageName = str;
            this.uid = i;
            this.progress = i2;
            this.percentage = d;
            this.touchType = i3;
        }
    }

    public void changeAppRow(String str, int i, int i2, double d, int i3) {
        onChangeAppRow(new AppRowStatus(str, i, i2 * 100, d, i3));
    }

    private void onChangeAppRow(AppRowStatus appRowStatus) {
        try {
            if (this.mMultiVolumeServices != null) {
                for (int i = 0; i < this.mMultiVolumeServices.size(); i++) {
                    int keyAt = this.mMultiVolumeServices.keyAt(i);
                    IMultiVolumeService valueAt = this.mMultiVolumeServices.valueAt(i);
                    if (valueAt != null) {
                        if (DEBUG) {
                            Log.d("RV-MultiVolumeHelper", "changeAppRow userId: " + keyAt);
                        }
                        valueAt.changeAppRow(appRowStatus.packageName, appRowStatus.uid, appRowStatus.progress, appRowStatus.percentage, appRowStatus.touchType);
                    } else if (DEBUG) {
                        Log.d("RV-MultiVolumeHelper", "MultiVolumeService is not ready, unable to onChangeAppRow, userId: " + keyAt);
                    }
                }
            } else if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MultiVolumeService is not ready, unable to onChangeAppRow");
            }
        } catch (RemoteException | SecurityException e) {
            if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "Unable to use MultiVolumeService, unable to onChangeAppRow", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public IMultiVolumeService getMultiVolumeService(int i) {
        return this.mMultiVolumeServices.get(i);
    }

    /* access modifiers changed from: private */
    public void setMultiVolumeService(int i, IMultiVolumeService iMultiVolumeService) {
        this.mMultiVolumeServices.put(i, iMultiVolumeService);
    }

    private MultiVolumeServiceConnection getMultiVolumeServiceConnection(int i) {
        MultiVolumeServiceConnection multiVolumeServiceConnection = this.mMultiVolumeServiceConnections.get(i);
        if (multiVolumeServiceConnection != null) {
            return multiVolumeServiceConnection;
        }
        MultiVolumeServiceConnection multiVolumeServiceConnection2 = new MultiVolumeServiceConnection(i);
        this.mMultiVolumeServiceConnections.put(i, multiVolumeServiceConnection2);
        return multiVolumeServiceConnection2;
    }

    public void bindMultiVolumeService() {
        bindMultiVolumeService(this.mUserId);
    }

    public boolean isServiceConnected() {
        MultiVolumeServiceConnection multiVolumeServiceConnection = this.mMultiVolumeServiceConnections.get(this.mUserId);
        return multiVolumeServiceConnection != null && multiVolumeServiceConnection.isServiceConnected;
    }

    public void bindMultiVolumeService(int i) {
        if (RelativeVolumeUtils.isRelativeVolumeFeatureOn(this.mContext)) {
            boolean z = DEBUG;
            if (z) {
                Log.d("RV-MultiVolumeHelper", "bindMultiVolumeService, userId: " + i);
            }
            if (i >= 0) {
                MultiVolumeServiceConnection multiVolumeServiceConnection = this.mMultiVolumeServiceConnections.get(i);
                if (multiVolumeServiceConnection == null || !multiVolumeServiceConnection.isServiceConnected) {
                    Intent intent = new Intent("com.motorola.dynamicvolume.action.MULTI_VOLUME_SERVICE_BIND_ACTION");
                    intent.setPackage("com.motorola.dynamicvolume");
                    this.mContext.bindServiceAsUser(intent, getMultiVolumeServiceConnection(i), 1, new UserHandle(i));
                } else if (z) {
                    Log.d("RV-MultiVolumeHelper", "service connection already exist, userId: " + i);
                }
            }
        } else if (DEBUG) {
            Log.d("RV-MultiVolumeHelper", "no need bindMultiVolumeService as feature not support ");
        }
    }

    public void unBindMultiVolumeService() {
        unBindMultiVolumeService(this.mUserId);
    }

    public void unBindMultiVolumeService(int i) {
        Context context;
        if (RelativeVolumeUtils.isRelativeVolumeFeatureOn(this.mContext)) {
            unregisterVolumeController(i);
            if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "unBindMultiVolumeService, userId: " + i);
            }
            try {
                MultiVolumeServiceConnection multiVolumeServiceConnection = getMultiVolumeServiceConnection(i);
                if (!(multiVolumeServiceConnection == null || (context = this.mContext) == null)) {
                    context.unbindService(multiVolumeServiceConnection);
                }
            } catch (Exception e) {
                if (DEBUG) {
                    Log.d("RV-MultiVolumeHelper", "Unable to unBindMultiVolumeService, userId: " + i, e);
                }
            }
            setMultiVolumeService(i, (IMultiVolumeService) null);
        } else if (DEBUG) {
            Log.d("RV-MultiVolumeHelper", "no need unBindMultiVolumeService as feature not support ");
        }
    }

    /* access modifiers changed from: private */
    public void registerMultiVolumeController(int i) {
        try {
            IMultiVolumeService multiVolumeService = getMultiVolumeService(i);
            if (multiVolumeService != null) {
                if (DEBUG) {
                    Log.d("RV-MultiVolumeHelper", "registerMultiVolumeController userId: " + i);
                }
                multiVolumeService.registerVolumeController(this.mMultiVolumeController);
            } else if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MultiVolumeService is not ready, unable to registerMultiVolumeController, userId: " + i);
            }
        } catch (RemoteException | SecurityException e) {
            if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "Unable to registerMultiVolumeController, userId: " + i, e);
            }
        }
    }

    private void unregisterVolumeController(int i) {
        try {
            IMultiVolumeService multiVolumeService = getMultiVolumeService(i);
            if (multiVolumeService != null) {
                if (DEBUG) {
                    Log.d("RV-MultiVolumeHelper", "unregisterVolumeController userId: " + i);
                }
                multiVolumeService.unregisterVolumeController(this.mMultiVolumeController);
            } else if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MultiVolumeService is not ready, unable to unregisterVolumeController, userId: " + i);
            }
        } catch (RemoteException | SecurityException e) {
            if (DEBUG) {
                Log.d("RV-MultiVolumeHelper", "Unable to unregisterVolumeController, userId: " + i, e);
            }
        }
    }

    private final class MultiVolumeServiceConnection implements ServiceConnection {
        boolean isServiceConnected = false;
        int userId;

        public MultiVolumeServiceConnection(int i) {
            this.userId = i;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "onServiceConnected (ComponentName: " + componentName + "), userId: " + this.userId);
            }
            MultiVolumeHelper.this.setMultiVolumeService(this.userId, IMultiVolumeService.Stub.asInterface(iBinder));
            this.isServiceConnected = true;
            MultiVolumeHelper.this.registerMultiVolumeController(this.userId);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "onServiceDisconnected (ComponentName: " + componentName + "), userId: " + this.userId);
            }
            MultiVolumeHelper.this.clearCache();
            if (MultiVolumeHelper.this.getMultiVolumeService(this.userId) != null) {
                this.isServiceConnected = false;
                MultiVolumeHelper.this.setMultiVolumeService(this.userId, (IMultiVolumeService) null);
            }
        }

        public void onBindingDied(ComponentName componentName) {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "onBindingDied (ComponentName: " + componentName + "), userId: " + this.userId);
            }
            this.isServiceConnected = false;
            MultiVolumeHelper.this.clearCache();
        }

        public void onNullBinding(ComponentName componentName) {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", " onNullBinding ComponentName: " + componentName + " userId: " + this.userId);
            }
            this.isServiceConnected = false;
            MultiVolumeHelper.this.clearCache();
            MultiVolumeHelper.this.unBindMultiVolumeService();
        }
    }

    /* access modifiers changed from: private */
    public void updateAppCache(List<AppVolumeState> list) {
        this.mCachedAppVolumeStateList = list;
    }

    public List<AppVolumeState> getCachedAppList() {
        return this.mCachedAppVolumeStateList;
    }

    /* access modifiers changed from: private */
    public void updateMusicProgressCache(int i) {
        this.mCachedMusicProgress = i;
    }

    /* access modifiers changed from: private */
    public void clearCache() {
        if (DEBUG) {
            Log.d("RV-MultiVolumeHelper", "clearCache");
        }
        this.mCachedAppVolumeStateList = null;
        this.mCachedMusicProgress = -1;
        ConcurrentHashMap<UICallback, Handler> concurrentHashMap = this.mUICallbackMap;
        if (concurrentHashMap != null) {
            for (Map.Entry next : concurrentHashMap.entrySet()) {
                Handler handler = (Handler) next.getValue();
                final UICallback uICallback = (UICallback) next.getKey();
                if (DEBUG) {
                    Log.d("RV-MultiVolumeHelper", "clearCache notify: " + uICallback.getClass().getName());
                }
                handler.removeMessages(2);
                Message obtainMessage = handler.obtainMessage(2);
                obtainMessage.setCallback(new Runnable() {
                    public void run() {
                        uICallback.onMultiVolumeChanged(MultiVolumeHelper.this.mCachedMusicProgress, -1.0d, MultiVolumeHelper.this.mCachedAppVolumeStateList);
                    }
                });
                handler.sendMessageAtFrontOfQueue(obtainMessage);
            }
        }
    }

    private final class MVC extends IMultiVolumeController.Stub {
        private MVC() {
        }

        public void multiVolumeRowsChanged(int i, double d, List<AppVolumeState> list) throws RemoteException {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MVC multiVolumeRowsChanged (musicRrogress: " + i + " musicPercentage: " + d + "), appvolume: " + RelativeVolumeUtils.appVolumesToString(list));
            } else {
                int i2 = i;
                double d2 = d;
            }
            List<AppVolumeState> playingApps = RelativeVolumeUtils.getPlayingApps(list);
            MultiVolumeHelper.this.updateAppCache(playingApps);
            MultiVolumeHelper.this.updateMusicProgressCache(RelativeVolumeUtils.getMusicLevel(i));
            if (MultiVolumeHelper.this.mUICallbackMap != null) {
                for (Map.Entry entry : MultiVolumeHelper.this.mUICallbackMap.entrySet()) {
                    Handler handler = (Handler) entry.getValue();
                    final UICallback uICallback = (UICallback) entry.getKey();
                    if (MultiVolumeHelper.DEBUG) {
                        Log.d("RV-MultiVolumeHelper", "MVC multiVolumeRowsChanged notify: " + uICallback.getClass().getName());
                    }
                    handler.removeMessages(2);
                    Message obtainMessage = handler.obtainMessage(2);
                    final int i3 = i;
                    final double d3 = d;
                    C19451 r7 = r0;
                    final List<AppVolumeState> list2 = playingApps;
                    C19451 r0 = new Runnable() {
                        public void run() {
                            uICallback.onMultiVolumeChanged(i3, d3, list2);
                        }
                    };
                    obtainMessage.setCallback(r7);
                    handler.sendMessageAtFrontOfQueue(obtainMessage);
                }
            }
        }

        public void musicRowChanged(int i, double d) throws RemoteException {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MVC musicRowChanged (musicRrogress: " + i + " musicPercentage: " + d);
            }
            MultiVolumeHelper.this.updateMusicProgressCache(RelativeVolumeUtils.getMusicLevel(i));
            if (MultiVolumeHelper.this.mUICallbackMap != null) {
                for (Map.Entry entry : MultiVolumeHelper.this.mUICallbackMap.entrySet()) {
                    Handler handler = (Handler) entry.getValue();
                    final UICallback uICallback = (UICallback) entry.getKey();
                    handler.removeMessages(1);
                    Message obtainMessage = handler.obtainMessage(1);
                    final int i2 = i;
                    final double d2 = d;
                    obtainMessage.setCallback(new Runnable() {
                        public void run() {
                            uICallback.onMusicVolumeChanged(i2, d2);
                        }
                    });
                    handler.sendMessageAtFrontOfQueue(obtainMessage);
                }
            }
        }

        public void appRowsChanged(List<AppVolumeState> list) throws RemoteException {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MVC appRowsChanged appvolume: " + RelativeVolumeUtils.appVolumesToString(list));
            }
            final List<AppVolumeState> playingApps = RelativeVolumeUtils.getPlayingApps(list);
            MultiVolumeHelper.this.updateAppCache(playingApps);
            if (MultiVolumeHelper.this.mUICallbackMap != null) {
                for (Map.Entry entry : MultiVolumeHelper.this.mUICallbackMap.entrySet()) {
                    Handler handler = (Handler) entry.getValue();
                    final UICallback uICallback = (UICallback) entry.getKey();
                    if (MultiVolumeHelper.DEBUG) {
                        Log.d("RV-MultiVolumeHelper", "MVC appRowsChanged notify: " + uICallback.getClass().getName());
                    }
                    handler.removeMessages(3);
                    Message obtainMessage = handler.obtainMessage(3);
                    obtainMessage.setCallback(new Runnable() {
                        public void run() {
                            uICallback.onAppVolumesChanged(playingApps);
                        }
                    });
                    handler.sendMessageAtFrontOfQueue(obtainMessage);
                }
            }
        }

        public void safeMediaVolumeHandled(final int i) throws RemoteException {
            if (MultiVolumeHelper.DEBUG) {
                Log.d("RV-MultiVolumeHelper", "MVC safeMediaVolumeHandled action: " + i);
            }
            if (MultiVolumeHelper.this.mUICallbackMap != null) {
                for (Map.Entry entry : MultiVolumeHelper.this.mUICallbackMap.entrySet()) {
                    Handler handler = (Handler) entry.getValue();
                    final UICallback uICallback = (UICallback) entry.getKey();
                    handler.removeMessages(4);
                    Message obtainMessage = handler.obtainMessage(4);
                    obtainMessage.setCallback(new Runnable() {
                        public void run() {
                            uICallback.safeMediaVolumeHandled(i);
                        }
                    });
                    handler.sendMessageAtFrontOfQueue(obtainMessage);
                }
            }
        }
    }
}
