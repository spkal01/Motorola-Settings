package com.android.settings.accessibility;

import android.content.DialogInterface;

/* renamed from: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0606x835f330e implements DialogInterface.OnClickListener {
    public final /* synthetic */ AccessibilityShortcutPreferenceFragment f$0;

    public /* synthetic */ C0606x835f330e(AccessibilityShortcutPreferenceFragment accessibilityShortcutPreferenceFragment) {
        this.f$0 = accessibilityShortcutPreferenceFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.callOnAlertDialogCheckboxClicked(dialogInterface, i);
    }
}
