package com.android.settings.password;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C1992R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class SetupSkipDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    public int getMetricsCategory() {
        return 573;
    }

    public static SetupSkipDialog newInstance(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        SetupSkipDialog setupSkipDialog = new SetupSkipDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("frp_supported", z);
        bundle.putBoolean("lock_type_pattern", z2);
        bundle.putBoolean("lock_type_alphanumeric", z3);
        bundle.putBoolean("for_fingerprint", z4);
        bundle.putBoolean("for_face", z5);
        bundle.putBoolean("for_biometrics", z6);
        setupSkipDialog.setArguments(bundle);
        return setupSkipDialog;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return onCreateDialogBuilder().create();
    }

    public AlertDialog.Builder onCreateDialogBuilder() {
        int i;
        int i2;
        int i3;
        Bundle arguments = getArguments();
        boolean z = arguments.getBoolean("for_face");
        boolean z2 = arguments.getBoolean("for_fingerprint");
        boolean z3 = arguments.getBoolean("for_biometrics");
        if (z || z2 || z3) {
            boolean z4 = false;
            boolean z5 = z || z3;
            if (z2 || z3) {
                z4 = true;
            }
            if (arguments.getBoolean("lock_type_pattern")) {
                i2 = getPatternSkipTitleRes(z5, z4);
                i = getPatternSkipMessageRes(z5, z4);
            } else if (arguments.getBoolean("lock_type_alphanumeric")) {
                i2 = getPasswordSkipTitleRes(z5, z4);
                i = getPasswordSkipMessageRes(z5, z4);
            } else {
                i2 = getPinSkipTitleRes(z5, z4);
                i = getPinSkipMessageRes(z5, z4);
            }
            return new AlertDialog.Builder(getContext()).setPositiveButton(C1992R$string.skip_lock_screen_dialog_button_label, (DialogInterface.OnClickListener) this).setNegativeButton(C1992R$string.cancel_lock_screen_dialog_button_label, (DialogInterface.OnClickListener) this).setTitle(i2).setMessage(i);
        }
        AlertDialog.Builder title = new AlertDialog.Builder(getContext()).setPositiveButton(C1992R$string.skip_anyway_button_label, (DialogInterface.OnClickListener) this).setNegativeButton(C1992R$string.go_back_button_label, (DialogInterface.OnClickListener) this).setTitle(C1992R$string.lock_screen_intro_skip_title);
        if (arguments.getBoolean("frp_supported")) {
            i3 = C1992R$string.lock_screen_intro_skip_dialog_text_frp;
        } else {
            i3 = C1992R$string.lock_screen_intro_skip_dialog_text;
        }
        return title.setMessage(i3);
    }

    private int getPatternSkipTitleRes(boolean z, boolean z2) {
        if (z && z2) {
            return C1992R$string.lock_screen_pattern_skip_biometrics_title;
        }
        if (z) {
            return C1992R$string.lock_screen_pattern_skip_face_title;
        }
        if (z2) {
            return C1992R$string.lock_screen_pattern_skip_fingerprint_title;
        }
        return C1992R$string.lock_screen_pattern_skip_title;
    }

    private int getPatternSkipMessageRes(boolean z, boolean z2) {
        if (z && z2) {
            return C1992R$string.lock_screen_pattern_skip_biometrics_message;
        }
        if (z) {
            return C1992R$string.lock_screen_pattern_skip_face_message;
        }
        if (z2) {
            return C1992R$string.lock_screen_pattern_skip_fingerprint_message;
        }
        return C1992R$string.lock_screen_pattern_skip_message;
    }

    private int getPasswordSkipTitleRes(boolean z, boolean z2) {
        if (z && z2) {
            return C1992R$string.lock_screen_password_skip_biometrics_title;
        }
        if (z) {
            return C1992R$string.lock_screen_password_skip_face_title;
        }
        if (z2) {
            return C1992R$string.lock_screen_password_skip_fingerprint_title;
        }
        return C1992R$string.lock_screen_password_skip_title;
    }

    private int getPasswordSkipMessageRes(boolean z, boolean z2) {
        if (z && z2) {
            return C1992R$string.lock_screen_password_skip_biometrics_message;
        }
        if (z) {
            return C1992R$string.lock_screen_password_skip_face_message;
        }
        if (z2) {
            return C1992R$string.lock_screen_password_skip_fingerprint_message;
        }
        return C1992R$string.lock_screen_password_skip_message;
    }

    private int getPinSkipTitleRes(boolean z, boolean z2) {
        if (z && z2) {
            return C1992R$string.lock_screen_pin_skip_biometrics_title;
        }
        if (z) {
            return C1992R$string.lock_screen_pin_skip_face_title;
        }
        if (z2) {
            return C1992R$string.lock_screen_pin_skip_fingerprint_title;
        }
        return C1992R$string.lock_screen_pin_skip_title;
    }

    private int getPinSkipMessageRes(boolean z, boolean z2) {
        if (z && z2) {
            return C1992R$string.lock_screen_pin_skip_biometrics_message;
        }
        if (z) {
            return C1992R$string.lock_screen_pin_skip_face_message;
        }
        if (z2) {
            return C1992R$string.lock_screen_pin_skip_fingerprint_message;
        }
        return C1992R$string.lock_screen_pin_skip_message;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        FragmentActivity activity = getActivity();
        if (i == -2) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.requestFocus();
                ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(currentFocus, 1);
            }
        } else if (i == -1) {
            activity.setResult(11);
            activity.finish();
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "skip_dialog");
    }
}
