package com.motorola.settings.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import com.android.settings.C1992R$string;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settingslib.Utils;

public class ContactDiscoveryNotification {
    private final int carrierId;
    private final CharSequence carrierName;
    private final Context context;
    private final int subId;

    public ContactDiscoveryNotification(Context context2, int i, CharSequence charSequence) {
        this.carrierName = charSequence;
        this.context = context2;
        this.subId = i;
        TelephonyManager telephonyManager = (TelephonyManager) context2.getSystemService(TelephonyManager.class);
        if (telephonyManager != null) {
            this.carrierId = telephonyManager.createForSubscriptionId(i).getSimCarrierId();
            return;
        }
        MotoMnsLog.logw("ContactDiscoveryNotification", "TelephonyManager is null!");
        this.carrierId = -1;
    }

    public void showNotification() {
        int i;
        MotoMnsLog.logd("ContactDiscoveryNotification", "showNotification");
        Resources resources = this.context.getResources();
        NotificationChannel notificationChannel = new NotificationChannel("send_contacts_to_carrier_notification_channel", resources.getText(C1992R$string.contact_discovery_notification_channel), 4);
        notificationChannel.setBlockable(true);
        Intent intent = new Intent(this.context, ContactDiscoveryNotificationReceiver.class);
        intent.setAction("com.motorola.settings.network.ACTION_CONFIRM_SEND_CONTACTS_NOTIFICATION");
        intent.putExtra("android.provider.extra.SUB_ID", this.subId);
        Intent intent2 = new Intent(this.context, ContactDiscoveryNotificationReceiver.class);
        intent2.setAction("com.motorola.settings.network.ACTION_REJECT_SEND_CONTACTS_NOTIFICATION");
        intent2.putExtra("android.provider.extra.SUB_ID", this.subId);
        PendingIntent broadcast = PendingIntent.getBroadcast(this.context, 0, intent, 1342177280);
        PendingIntent broadcast2 = PendingIntent.getBroadcast(this.context, 0, intent2, 1342177280);
        if (this.carrierId == 1839) {
            i = C1992R$string.contact_discovery_opt_in_dialog_message_carrier_vzw;
        } else {
            i = C1992R$string.contact_discovery_opt_in_dialog_message_carrier_default;
        }
        String string = resources.getString(C1992R$string.contact_discovery_opt_in_dialog_title_carrier, new Object[]{this.carrierName});
        String string2 = this.context.getString(i, new Object[]{this.carrierName});
        String string3 = this.context.getString(C1992R$string.confirmation_turn_on_carrier);
        String string4 = this.context.getString(C1992R$string.confirmation_turn_off_carrier);
        Context context2 = this.context;
        Icon createWithResource = Icon.createWithResource(context2, getResIdFromAttr(context2, 16843906));
        Context context3 = this.context;
        Notification.Builder color = new Notification.Builder(this.context, "send_contacts_to_carrier_notification_channel").setSmallIcon(getResIdFromAttr(this.context, 16843605)).setContentTitle(string).setContentText(string2).addAction(new Notification.Action.Builder(createWithResource, string3, broadcast).build()).addAction(new Notification.Action.Builder(Icon.createWithResource(context3, getResIdFromAttr(context3, 16844031)), string4, broadcast2).build()).setAutoCancel(false).setColor(Utils.getColorAccentDefaultColor(this.context));
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(100, color.build());
            return;
        }
        MotoMnsLog.loge("ContactDiscoveryNotification", "NotificationManager is null!");
    }

    private int getResIdFromAttr(Context context2, int i) {
        TypedValue typedValue = new TypedValue();
        context2.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    public static void cancelNotification(Context context2) {
        NotificationManager notificationManager = (NotificationManager) context2.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.cancel(100);
        } else {
            MotoMnsLog.loge("ContactDiscoveryNotification", "cancelNotification: NotificationManager is NULL");
        }
    }

    public static class ContactDiscoveryNotificationReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            MotoMnsLog.logd("ContactDiscoveryNotification", "ContactDiscoveryNotificationReceiver: received " + action);
            if (action == null) {
                MotoMnsLog.logw("ContactDiscoveryNotification", "ContactDiscoveryNotificationReceiver: NULL action");
            } else if (action.equals("com.motorola.settings.network.ACTION_CONFIRM_SEND_CONTACTS_NOTIFICATION")) {
                sendContactDiscoveryIntent(context, intent.getIntExtra("android.provider.extra.SUB_ID", SubscriptionManager.getDefaultSubscriptionId()));
                ContactDiscoveryNotification.cancelNotification(context);
            } else if (!action.equals("com.motorola.settings.network.ACTION_REJECT_SEND_CONTACTS_NOTIFICATION")) {
                MotoMnsLog.logw("ContactDiscoveryNotification", "ContactDiscoveryNotificationReceiver: Invalid action " + action);
            } else {
                ContactDiscoveryNotification.cancelNotification(context);
            }
        }

        private void sendContactDiscoveryIntent(Context context, int i) {
            MotoMnsLog.logd("ContactDiscoveryNotification", "sendContactDiscoveryIntent: show contacts discovery dialog for subId " + i);
            Intent intent = new Intent(context, MobileNetworkActivity.class);
            intent.setAction("android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN");
            intent.putExtra("android.provider.extra.SUB_ID", i);
            intent.setFlags(805306368);
            context.startActivity(intent);
        }
    }
}
