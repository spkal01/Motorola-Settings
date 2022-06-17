package com.google.android.setupcompat.util;

import android.util.Log;

public final class Logger {
    private final String prefix;

    public Logger(String str) {
        this.prefix = "[" + str + "] ";
    }

    public boolean isD() {
        return Log.isLoggable("SetupLibrary", 3);
    }

    public boolean isI() {
        return Log.isLoggable("SetupLibrary", 4);
    }

    public void atDebug(String str) {
        if (isD()) {
            Log.d("SetupLibrary", this.prefix.concat(str));
        }
    }

    public void atInfo(String str) {
        if (isI()) {
            Log.i("SetupLibrary", this.prefix.concat(str));
        }
    }

    /* renamed from: w */
    public void mo20057w(String str) {
        Log.w("SetupLibrary", this.prefix.concat(str));
    }

    /* renamed from: e */
    public void mo20053e(String str) {
        Log.e("SetupLibrary", this.prefix.concat(str));
    }

    /* renamed from: e */
    public void mo20054e(String str, Throwable th) {
        Log.e("SetupLibrary", this.prefix.concat(str), th);
    }
}
