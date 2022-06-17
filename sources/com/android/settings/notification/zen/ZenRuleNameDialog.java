package com.android.settings.notification.zen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ZenRuleNameDialog extends InstrumentedDialogFragment {
    protected static PositiveClickListener mPositiveClickListener;

    public interface PositiveClickListener {
        void onOk(String str, Fragment fragment);
    }

    public int getMetricsCategory() {
        return 1269;
    }

    public static void show(Fragment fragment, String str, Uri uri, PositiveClickListener positiveClickListener) {
        Bundle bundle = new Bundle();
        bundle.putString("zen_rule_name", str);
        bundle.putParcelable("extra_zen_condition_id", uri);
        mPositiveClickListener = positiveClickListener;
        ZenRuleNameDialog zenRuleNameDialog = new ZenRuleNameDialog();
        zenRuleNameDialog.setArguments(bundle);
        zenRuleNameDialog.setTargetFragment(fragment, 0);
        zenRuleNameDialog.show(fragment.getFragmentManager(), "ZenRuleNameDialog");
    }

    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        Bundle arguments = getArguments();
        Uri uri = (Uri) arguments.getParcelable("extra_zen_condition_id");
        final String string = arguments.getString("zen_rule_name");
        final boolean z = string == null;
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(C1987R$layout.zen_rule_name, (ViewGroup) null, false);
        final EditText editText = (EditText) inflate.findViewById(C1985R$id.zen_mode_rule_name);
        if (!z) {
            editText.setText(string);
            editText.setSelection(editText.getText().length());
        }
        editText.setSelectAllOnFocus(true);
        AlertDialog.Builder view = new AlertDialog.Builder(context).setTitle(getTitleResource(uri, z)).setView(inflate);
        if (z) {
            i = C1992R$string.zen_mode_add;
        } else {
            i = C1992R$string.okay;
        }
        return view.setPositiveButton(i, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                CharSequence charSequence;
                String access$000 = ZenRuleNameDialog.this.trimmedText(editText);
                if (!TextUtils.isEmpty(access$000)) {
                    if (z || (charSequence = string) == null || !charSequence.equals(access$000)) {
                        ZenRuleNameDialog.mPositiveClickListener.onOk(access$000, ZenRuleNameDialog.this.getTargetFragment());
                    }
                }
            }
        }).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* access modifiers changed from: private */
    public String trimmedText(EditText editText) {
        if (editText.getText() == null) {
            return null;
        }
        return editText.getText().toString().trim();
    }

    private int getTitleResource(Uri uri, boolean z) {
        boolean isValidEventConditionId = ZenModeConfig.isValidEventConditionId(uri);
        boolean isValidScheduleConditionId = ZenModeConfig.isValidScheduleConditionId(uri);
        int i = C1992R$string.zen_mode_rule_name;
        if (!z) {
            return i;
        }
        if (isValidEventConditionId) {
            return C1992R$string.zen_mode_add_event_rule;
        }
        return isValidScheduleConditionId ? C1992R$string.zen_mode_add_time_rule : i;
    }
}
