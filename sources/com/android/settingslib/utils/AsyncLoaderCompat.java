package com.android.settingslib.utils;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

public abstract class AsyncLoaderCompat<T> extends AsyncTaskLoader<T> {
    private T mResult;

    /* access modifiers changed from: protected */
    public abstract void onDiscardResult(T t);

    public AsyncLoaderCompat(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onStartLoading() {
        T t = this.mResult;
        if (t != null) {
            deliverResult(t);
        }
        if (takeContentChanged() || this.mResult == null) {
            forceLoad();
        }
    }

    /* access modifiers changed from: protected */
    public void onStopLoading() {
        cancelLoad();
    }

    public void deliverResult(T t) {
        if (!isReset()) {
            T t2 = this.mResult;
            this.mResult = t;
            if (isStarted()) {
                super.deliverResult(t);
            }
            if (t2 != null && t2 != this.mResult) {
                onDiscardResult(t2);
            }
        } else if (t != null) {
            onDiscardResult(t);
        }
    }

    /* access modifiers changed from: protected */
    public void onReset() {
        super.onReset();
        onStopLoading();
        T t = this.mResult;
        if (t != null) {
            onDiscardResult(t);
        }
        this.mResult = null;
    }

    public void onCanceled(T t) {
        super.onCanceled(t);
        if (t != null) {
            onDiscardResult(t);
        }
    }
}
