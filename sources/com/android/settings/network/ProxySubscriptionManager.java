package com.android.settings.network;

import android.content.Context;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProxySubscriptionManager implements LifecycleObserver {
    private static ProxySubscriptionManager sSingleton;
    private List<OnActiveSubscriptionChangedListener> mActiveSubscriptionsListeners;
    private GlobalSettingsChangeListener mAirplaneModeMonitor;
    private Lifecycle mLifecycle;
    private List<OnActiveSubscriptionChangedListener> mPendingNotifyListeners;
    private ActiveSubscriptionsListener mSubscriptionMonitor;

    public interface OnActiveSubscriptionChangedListener {
        Lifecycle getLifecycle() {
            return null;
        }

        void onChanged();
    }

    public static ProxySubscriptionManager getInstance(Context context) {
        ProxySubscriptionManager proxySubscriptionManager = sSingleton;
        if (proxySubscriptionManager != null) {
            return proxySubscriptionManager;
        }
        ProxySubscriptionManager proxySubscriptionManager2 = new ProxySubscriptionManager(context.getApplicationContext());
        sSingleton = proxySubscriptionManager2;
        return proxySubscriptionManager2;
    }

    private ProxySubscriptionManager(Context context) {
        Looper mainLooper = context.getMainLooper();
        C10551 r6 = new ActiveSubscriptionsListener(mainLooper, context) {
            public void onChanged() {
                ProxySubscriptionManager.this.notifySubscriptionInfoMightChanged();
            }
        };
        final C10551 r5 = r6;
        init(context, r6, new GlobalSettingsChangeListener(mainLooper, context, "airplane_mode_on") {
            public void onChanged(String str) {
                r5.clearCache();
                ProxySubscriptionManager.this.notifySubscriptionInfoMightChanged();
            }
        });
    }

    /* access modifiers changed from: protected */
    @Keep
    public void init(Context context, ActiveSubscriptionsListener activeSubscriptionsListener, GlobalSettingsChangeListener globalSettingsChangeListener) {
        this.mActiveSubscriptionsListeners = new ArrayList();
        this.mPendingNotifyListeners = new ArrayList();
        this.mSubscriptionMonitor = activeSubscriptionsListener;
        this.mAirplaneModeMonitor = globalSettingsChangeListener;
        activeSubscriptionsListener.start();
    }

    /* access modifiers changed from: protected */
    @Keep
    public void notifySubscriptionInfoMightChanged() {
        ArrayList arrayList = new ArrayList(this.mPendingNotifyListeners);
        arrayList.addAll(this.mActiveSubscriptionsListeners);
        this.mActiveSubscriptionsListeners.clear();
        this.mPendingNotifyListeners.clear();
        processStatusChangeOnListeners(arrayList);
    }

    public void setLifecycle(Lifecycle lifecycle) {
        Lifecycle lifecycle2 = this.mLifecycle;
        if (lifecycle2 != lifecycle) {
            if (lifecycle2 != null) {
                lifecycle2.removeObserver(this);
            }
            if (lifecycle != null) {
                lifecycle.addObserver(this);
            }
            this.mLifecycle = lifecycle;
            this.mAirplaneModeMonitor.notifyChangeBasedOn(lifecycle);
        }
    }

    /* access modifiers changed from: package-private */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mSubscriptionMonitor.start();
        List<OnActiveSubscriptionChangedListener> list = this.mPendingNotifyListeners;
        this.mPendingNotifyListeners = new ArrayList();
        processStatusChangeOnListeners(list);
    }

    /* access modifiers changed from: package-private */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mSubscriptionMonitor.stop();
    }

    /* access modifiers changed from: package-private */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mSubscriptionMonitor.close();
        this.mAirplaneModeMonitor.close();
        Lifecycle lifecycle = this.mLifecycle;
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
            this.mLifecycle = null;
            sSingleton = null;
        }
    }

    public SubscriptionManager get() {
        return this.mSubscriptionMonitor.getSubscriptionManager();
    }

    public int getActiveSubscriptionInfoCountMax() {
        return this.mSubscriptionMonitor.getActiveSubscriptionInfoCountMax();
    }

    public List<SubscriptionInfo> getActiveSubscriptionsInfo() {
        return this.mSubscriptionMonitor.getActiveSubscriptionsInfo();
    }

    public SubscriptionInfo getActiveSubscriptionInfo(int i) {
        return this.mSubscriptionMonitor.getActiveSubscriptionInfo(i);
    }

    public List<SubscriptionInfo> getAccessibleSubscriptionsInfo() {
        return this.mSubscriptionMonitor.getAccessibleSubscriptionsInfo();
    }

    public SubscriptionInfo getAccessibleSubscriptionInfo(int i) {
        return this.mSubscriptionMonitor.getAccessibleSubscriptionInfo(i);
    }

    @Keep
    public void addActiveSubscriptionsListener(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mPendingNotifyListeners);
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mActiveSubscriptionsListeners);
        if (onActiveSubscriptionChangedListener != null && getListenerState(onActiveSubscriptionChangedListener) != -1) {
            this.mActiveSubscriptionsListeners.add(onActiveSubscriptionChangedListener);
        }
    }

    @Keep
    public void removeActiveSubscriptionsListener(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mPendingNotifyListeners);
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mActiveSubscriptionsListeners);
    }

    private int getListenerState(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        Lifecycle lifecycle = onActiveSubscriptionChangedListener.getLifecycle();
        if (lifecycle == null) {
            return 1;
        }
        Lifecycle.State currentState = lifecycle.getCurrentState();
        if (currentState != Lifecycle.State.DESTROYED) {
            return currentState.isAtLeast(Lifecycle.State.STARTED) ? 1 : 0;
        }
        Log.d("ProxySubscriptionManager", "Listener dead detected - " + onActiveSubscriptionChangedListener);
        return -1;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$removeSpecificListenerAndCleanList$0(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener, OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener2) {
        return onActiveSubscriptionChangedListener2 == onActiveSubscriptionChangedListener || getListenerState(onActiveSubscriptionChangedListener2) == -1;
    }

    private void removeSpecificListenerAndCleanList(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener, List<OnActiveSubscriptionChangedListener> list) {
        list.removeIf(new ProxySubscriptionManager$$ExternalSyntheticLambda4(this, onActiveSubscriptionChangedListener));
    }

    private void processStatusChangeOnListeners(List<OnActiveSubscriptionChangedListener> list) {
        Map map = (Map) list.stream().collect(Collectors.groupingBy(new ProxySubscriptionManager$$ExternalSyntheticLambda3(this)));
        map.computeIfPresent(0, new ProxySubscriptionManager$$ExternalSyntheticLambda1(this));
        map.computeIfPresent(1, new ProxySubscriptionManager$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Integer lambda$processStatusChangeOnListeners$1(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        return Integer.valueOf(getListenerState(onActiveSubscriptionChangedListener));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ List lambda$processStatusChangeOnListeners$2(Integer num, List list) {
        this.mPendingNotifyListeners.addAll(list);
        return list;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ List lambda$processStatusChangeOnListeners$4(Integer num, List list) {
        this.mActiveSubscriptionsListeners.addAll(list);
        list.stream().forEach(ProxySubscriptionManager$$ExternalSyntheticLambda2.INSTANCE);
        return list;
    }
}
