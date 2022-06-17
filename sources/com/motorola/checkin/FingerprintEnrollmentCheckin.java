package com.motorola.checkin;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class FingerprintEnrollmentCheckin {
    private static final boolean DBG = Build.IS_DEBUGGABLE;
    private static FingerprintEnrollmentCheckin mInstance;
    private static Object mLock = new Object();
    private Context mContext;
    private CheckinHandler mHandler;
    private HandlerThread mHandlerThread;

    private FingerprintEnrollmentCheckin(Context context) {
        this.mContext = context;
    }

    public static void startCheckin(Context context) {
        if (mInstance == null) {
            mInstance = new FingerprintEnrollmentCheckin(context);
        }
    }

    private static FingerprintEnrollmentCheckin peekInstance() {
        return mInstance;
    }

    private void startHandler() {
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread(FingerprintEnrollmentCheckin.class.getName());
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mHandler = new CheckinHandler(this.mHandlerThread.getLooper());
        }
    }

    private boolean logAbandonEvent(Event event) {
        startHandler();
        CheckinHandler checkinHandler = this.mHandler;
        return checkinHandler.sendMessage(checkinHandler.obtainMessage(0, event));
    }

    /* access modifiers changed from: private */
    public void handleLogEvent(int i, Object obj) {
        if (i == 0) {
            ((Event) obj).logEvent(this.mContext);
        }
    }

    public static void logUserAbandonEvents(Context context, String str) {
        try {
            FingerprintEnrollmentCheckin peekInstance = peekInstance();
            if (peekInstance != null) {
                Event event = new Event("FPS_ENROLL");
                event.add("ui_prog", str);
                peekInstance.logAbandonEvent(event);
            }
        } catch (Exception e) {
            Log.e("FingerprintEnrollmentCheckin", "Fail to log event " + e);
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public static StringBuilder concat(StringBuilder sb, Object... objArr) {
        if (!(objArr == null || sb == null)) {
            for (Object append : objArr) {
                sb.append(append);
            }
        }
        return sb;
    }

    private class Event {
        String mTag = "MOT_FPS";
        StringBuilder mValue = new StringBuilder("[");

        Event(String str) {
            Long l = new Long(System.currentTimeMillis());
            add("ID", str);
            add("ver", "1.0");
            add("time", l.toString());
        }

        /* access modifiers changed from: package-private */
        public String getValue() {
            StringBuilder sb = this.mValue;
            if (!"]".equals(sb.substring(sb.length() - 1, this.mValue.length()))) {
                this.mValue.append("]");
            }
            return this.mValue.toString();
        }

        /* access modifiers changed from: package-private */
        public String getTag() {
            return this.mTag;
        }

        /* access modifiers changed from: package-private */
        public void add(Object obj, Object obj2) {
            StringBuilder unused = FingerprintEnrollmentCheckin.concat(this.mValue, obj, "=", obj2, ";");
        }

        /* access modifiers changed from: package-private */
        public void logEvent(Context context) {
            String[] split = getValue().split("[\\[\\]=;]");
            if (split.length > 6) {
                String tag = getTag();
                String str = split[2];
                String str2 = split[4];
                Long valueOf = Long.valueOf(Long.parseLong(split[6]));
                if (CheckinEventWrapper.isInitialized()) {
                    CheckinEventWrapper checkinEventWrapper = new CheckinEventWrapper();
                    if (checkinEventWrapper.setHeader(tag, str, str2, valueOf.longValue())) {
                        for (int i = 7; i < split.length; i += 2) {
                            checkinEventWrapper.setValue(split[i], split[i + 1]);
                        }
                        checkinEventWrapper.publish(context.getContentResolver());
                    }
                }
            }
        }
    }

    class CheckinHandler extends Handler {
        private CheckinHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            try {
                FingerprintEnrollmentCheckin.this.handleLogEvent(message.what, message.obj);
            } catch (Throwable th) {
                Log.e("FingerprintEnrollmentCheckin", "Fail handleMessage - Exception=" + th);
                th.printStackTrace();
            }
        }
    }
}
