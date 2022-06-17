package com.android.settings.notification;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class NotificationAssistantDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    public int getMetricsCategory() {
        return 790;
    }

    public static NotificationAssistantDialogFragment newInstance(Fragment fragment, ComponentName componentName) {
        String str;
        NotificationAssistantDialogFragment notificationAssistantDialogFragment = new NotificationAssistantDialogFragment();
        Bundle bundle = new Bundle();
        if (componentName == null) {
            str = "";
        } else {
            str = componentName.flattenToString();
        }
        bundle.putString("c", str);
        notificationAssistantDialogFragment.setArguments(bundle);
        notificationAssistantDialogFragment.setTargetFragment(fragment, 0);
        return notificationAssistantDialogFragment;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getContext()).setMessage((CharSequence) getResources().getString(C1992R$string.notification_assistant_security_warning_summary)).setCancelable(true).setPositiveButton(C1992R$string.okay, (DialogInterface.OnClickListener) this).create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        ((ConfigureNotificationSettings) getTargetFragment()).enableNAS(ComponentName.unflattenFromString(getArguments().getString("c")));
    }
}
