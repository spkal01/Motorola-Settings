package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.internal.accessibility.AccessibilityShortcutController;

public class OneHandedSettingsUtils {
    static final Uri ONE_HANDED_MODE_ENABLED_URI = Settings.Secure.getUriFor("one_handed_mode_enabled");
    static final String ONE_HANDED_MODE_TARGET_NAME = AccessibilityShortcutController.ONE_HANDED_COMPONENT_NAME.getShortClassName();
    static final Uri SHORTCUT_ENABLED_URI = Settings.Secure.getUriFor("accessibility_button_targets");
    static final Uri SHOW_NOTIFICATION_ENABLED_URI = Settings.Secure.getUriFor("swipe_bottom_to_notification_enabled");
    private static int sCurrentUserId;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final SettingsObserver mSettingsObserver = new SettingsObserver(new Handler(Looper.getMainLooper()));

    public interface TogglesCallback {
        void onChange(Uri uri);
    }

    public enum OneHandedTimeout {
        NEVER(0),
        SHORT(4),
        MEDIUM(8),
        LONG(12);
        
        private final int mValue;

        private OneHandedTimeout(int i) {
            this.mValue = i;
        }

        public int getValue() {
            return this.mValue;
        }
    }

    OneHandedSettingsUtils(Context context) {
        this.mContext = context;
        sCurrentUserId = UserHandle.myUserId();
    }

    public static boolean isSupportOneHandedMode() {
        return SystemProperties.getBoolean("ro.support_one_handed_mode", false);
    }

    public static boolean isOneHandedModeEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "one_handed_mode_enabled", 0, sCurrentUserId) == 1;
    }

    public static void setOneHandedModeEnabled(Context context, boolean z) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "one_handed_mode_enabled", z ? 1 : 0, sCurrentUserId);
    }

    public static boolean setTapsAppToExitEnabled(Context context, boolean z) {
        return Settings.Secure.putIntForUser(context.getContentResolver(), "taps_app_to_exit", z ? 1 : 0, sCurrentUserId);
    }

    public static void setUserId(int i) {
        sCurrentUserId = i;
    }

    public static void setTimeoutValue(Context context, int i) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "one_handed_mode_timeout", i, sCurrentUserId);
    }

    public static boolean isSwipeDownNotificationEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "swipe_bottom_to_notification_enabled", 0, sCurrentUserId) == 1;
    }

    public static void setSwipeDownNotificationEnabled(Context context, boolean z) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "swipe_bottom_to_notification_enabled", z ? 1 : 0, sCurrentUserId);
    }

    public boolean setNavigationBarMode(Context context, String str) {
        return Settings.Secure.putStringForUser(context.getContentResolver(), "navigation_mode", str, UserHandle.myUserId());
    }

    public static int getNavigationBarMode(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "navigation_mode", 2, sCurrentUserId);
    }

    public static boolean canEnableController(Context context) {
        return (isOneHandedModeEnabled(context) && getNavigationBarMode(context) != 0) || getShortcutEnabled(context);
    }

    public static boolean getShortcutEnabled(Context context) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "accessibility_button_targets", sCurrentUserId);
        if (stringForUser != null) {
            return stringForUser.contains(ONE_HANDED_MODE_TARGET_NAME);
        }
        return false;
    }

    public void setShortcutEnabled(Context context, boolean z) {
        Settings.Secure.putStringForUser(context.getContentResolver(), "accessibility_button_targets", z ? ONE_HANDED_MODE_TARGET_NAME : "", sCurrentUserId);
    }

    public void registerToggleAwareObserver(TogglesCallback togglesCallback) {
        this.mSettingsObserver.observe();
        this.mSettingsObserver.setCallback(togglesCallback);
    }

    public void unregisterToggleAwareObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    }

    private final class SettingsObserver extends ContentObserver {
        private TogglesCallback mCallback;

        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: private */
        public void setCallback(TogglesCallback togglesCallback) {
            this.mCallback = togglesCallback;
        }

        public void observe() {
            ContentResolver contentResolver = OneHandedSettingsUtils.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(OneHandedSettingsUtils.ONE_HANDED_MODE_ENABLED_URI, true, this);
            contentResolver.registerContentObserver(OneHandedSettingsUtils.SHOW_NOTIFICATION_ENABLED_URI, true, this);
            contentResolver.registerContentObserver(OneHandedSettingsUtils.SHORTCUT_ENABLED_URI, true, this);
        }

        public void onChange(boolean z, Uri uri) {
            TogglesCallback togglesCallback = this.mCallback;
            if (togglesCallback != null) {
                togglesCallback.onChange(uri);
            }
        }
    }
}
