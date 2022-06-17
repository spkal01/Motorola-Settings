package com.motorola.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.android.internal.app.LocaleStore;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import java.util.Arrays;
import java.util.Locale;

public class PartiallySupportedLocaleNotificationHandler extends BroadcastReceiver {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;

    public void onReceive(Context context, Intent intent) {
        if (!Utils.isDeviceProvisioned(context)) {
            cancelNotification(context);
            LocaleStore.LocaleInfo localeInfo = LocaleStore.getLocaleInfo(Locale.getDefault());
            if (Arrays.asList(context.getResources().getStringArray(17236193)).contains(localeInfo.getId())) {
                if (DEBUG) {
                    Log.d("PartialLocale", "Locale is partially supported : " + localeInfo.getId());
                }
                createNotification(context);
                return;
            }
            return;
        }
        cancelNotification(context);
        disableReceiver(context);
    }

    private void createNotification(Context context) {
        if (DEBUG) {
            Log.d("PartialLocale", "Create notification for partially supported locale");
        }
        Resources resources = context.getResources();
        NotificationChannel notificationChannel = new NotificationChannel("partially_supported_locale_notification_channel", resources.getString(C1992R$string.partially_supported_locale_channel_title), 2);
        NotificationCompat.Builder contentTitle = new NotificationCompat.Builder(context, "partially_supported_locale_notification_channel").setSmallIcon(C1983R$drawable.ic_settings_language).setContentTitle(resources.getString(C1992R$string.partially_supported_locale_notification_title));
        int i = C1992R$string.partially_supported_locale_notification_summary;
        NotificationCompat.Builder contentText = contentTitle.setContentText(resources.getString(i));
        Intent intent = new Intent(context, Settings.LocalePickerActivity.class);
        intent.addFlags(268468224);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 67108864);
        contentText.setStyle(new NotificationCompat.BigTextStyle().bigText(resources.getString(i)));
        contentText.setContentIntent(activity);
        contentText.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1, contentText.build());
    }

    public static void cancelNotification(Context context) {
        if (DEBUG) {
            Log.d("PartialLocale", "Cancel notification for partially supported locale");
        }
        ((NotificationManager) context.getSystemService("notification")).deleteNotificationChannel("partially_supported_locale_notification_channel");
    }

    public static void disableReceiver(Context context) {
        if (DEBUG) {
            Log.d("PartialLocale", "Disabling the PartiallySupportedLocaleNotificationHandler.");
        }
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, PartiallySupportedLocaleNotificationHandler.class), 2, 1);
    }
}
