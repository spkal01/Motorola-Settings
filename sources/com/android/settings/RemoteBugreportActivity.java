package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

public class RemoteBugreportActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.app.extra.bugreport_notification_type", -1);
        if (intExtra == 2) {
            new AlertDialog.Builder(this).setMessage(C1992R$string.sharing_remote_bugreport_dialog_message).setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            }).setNegativeButton(17039370, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.finish();
                }
            }).create().show();
        } else if (intExtra == 1 || intExtra == 3) {
            AlertDialog.Builder title = new AlertDialog.Builder(this).setTitle(C1992R$string.share_remote_bugreport_dialog_title);
            if (intExtra == 1) {
                i = C1992R$string.share_remote_bugreport_dialog_message;
            } else {
                i = C1992R$string.share_remote_bugreport_dialog_message_finished;
            }
            title.setMessage(i).setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            }).setNegativeButton(C1992R$string.decline_remote_bugreport_action, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            }).setPositiveButton(C1992R$string.share_remote_bugreport_action, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            }).create().show();
        } else {
            Log.e("RemoteBugreportActivity", "Incorrect dialog type, no dialog shown. Received: " + intExtra);
        }
    }
}
