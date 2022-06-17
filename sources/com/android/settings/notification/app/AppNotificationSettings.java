package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1994R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AppNotificationSettings extends NotificationSettings {
    private static final boolean DEBUG = Log.isLoggable("AppNotificationSettings", 3);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "AppNotificationSettings";
    }

    public int getMetricsCategory() {
        return 72;
    }

    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("AppNotificationSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        getActivity().setTitle(this.mAppRow.label);
        for (NotificationPreferenceController next : this.mControllers) {
            next.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, (Drawable) null, (ShortcutInfo) null, this.mSuspendedAppsAdmin, (List<String>) null);
            next.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.app_notification_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        this.mControllers.add(new BlockPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new BadgePreferenceController(context, this.mBackend));
        this.mControllers.add(new AllowSoundPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new ImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new MinImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new HighImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new SoundPreferenceController(context, this, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new LightsPreferenceController(context, this.mBackend));
        this.mControllers.add(new VibrationPreferenceController(context, this.mBackend));
        this.mControllers.add(new VisibilityPreferenceController(context, new LockPatternUtils(context), this.mBackend));
        this.mControllers.add(new DndPreferenceController(context, this.mBackend));
        this.mControllers.add(new AppLinkPreferenceController(context));
        this.mControllers.add(new DescriptionPreferenceController(context));
        this.mControllers.add(new NotificationsOffPreferenceController(context));
        this.mControllers.add(new DeletedChannelsPreferenceController(context, this.mBackend));
        this.mControllers.add(new ChannelListPreferenceController(context, this.mBackend));
        this.mControllers.add(new AppConversationListPreferenceController(context, this.mBackend));
        this.mControllers.add(new InvalidConversationInfoPreferenceController(context, this.mBackend));
        this.mControllers.add(new InvalidConversationPreferenceController(context, this.mBackend));
        this.mControllers.add(new BubbleSummaryPreferenceController(context, this.mBackend));
        return new ArrayList(this.mControllers);
    }
}
