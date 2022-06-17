package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModeRestrictNotificationsSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.zen_mode_restrict_notifications_settings) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeRestrictNotificationsSettings.buildPreferenceControllers(context, (Lifecycle) null);
        }
    };

    public int getMetricsCategory() {
        return 1400;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_interruptions;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeVisEffectsNonePreferenceController(context, lifecycle, "zen_mute_notifications"));
        arrayList.add(new ZenModeVisEffectsAllPreferenceController(context, lifecycle, "zen_hide_notifications"));
        arrayList.add(new ZenModeVisEffectsCustomPreferenceController(context, lifecycle, "zen_custom"));
        arrayList.add(new ZenFooterPreferenceController(context, lifecycle, "footer_preference"));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.zen_mode_restrict_notifications_settings;
    }
}
