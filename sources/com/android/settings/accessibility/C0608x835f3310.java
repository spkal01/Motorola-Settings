package com.android.settings.accessibility;

import android.view.accessibility.AccessibilityManager;

/* renamed from: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C0608x835f3310 implements AccessibilityManager.TouchExplorationStateChangeListener {
    public final /* synthetic */ AccessibilityShortcutPreferenceFragment f$0;

    public /* synthetic */ C0608x835f3310(AccessibilityShortcutPreferenceFragment accessibilityShortcutPreferenceFragment) {
        this.f$0 = accessibilityShortcutPreferenceFragment;
    }

    public final void onTouchExplorationStateChanged(boolean z) {
        this.f$0.lambda$onCreateView$0(z);
    }
}
