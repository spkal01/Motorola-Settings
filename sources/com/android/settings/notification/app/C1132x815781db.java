package com.android.settings.notification.app;

import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceGroup;

/* renamed from: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1132x815781db implements View.OnClickListener {
    public final /* synthetic */ RecentConversationsPreferenceController f$0;
    public final /* synthetic */ PreferenceGroup f$1;
    public final /* synthetic */ Button f$2;

    public /* synthetic */ C1132x815781db(RecentConversationsPreferenceController recentConversationsPreferenceController, PreferenceGroup preferenceGroup, Button button) {
        this.f$0 = recentConversationsPreferenceController;
        this.f$1 = preferenceGroup;
        this.f$2 = button;
    }

    public final void onClick(View view) {
        this.f$0.lambda$getClearAll$0(this.f$1, this.f$2, view);
    }
}
