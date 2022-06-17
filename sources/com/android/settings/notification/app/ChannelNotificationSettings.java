package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1994R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ChannelNotificationSettings extends NotificationSettings {
    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ChannelSettings";
    }

    public int getMetricsCategory() {
        return 265;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Bundle arguments = getArguments();
        if (preferenceScreen != null && arguments != null && !arguments.getBoolean("fromSettings", false)) {
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || this.mChannel == null) {
            Log.w("ChannelSettings", "Missing package or uid or packageinfo or channel");
            finish();
            return;
        }
        getActivity().setTitle(this.mChannel.getName());
        if (TextUtils.isEmpty(this.mChannel.getConversationId()) || this.mChannel.isDemoted()) {
            for (NotificationPreferenceController next : this.mControllers) {
                next.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, (Drawable) null, (ShortcutInfo) null, this.mSuspendedAppsAdmin, this.mPreferenceFilter);
                next.displayPreference(getPreferenceScreen());
            }
            updatePreferenceStates();
            animatePanel();
            return;
        }
        Intent intent = new SubSettingLauncher(this.mContext).setDestination(ConversationNotificationSettings.class.getName()).setArguments(getArguments()).setExtras(getIntent() != null ? getIntent().getExtras() : null).setSourceMetricsCategory(265).toIntent();
        if (this.mPreferenceFilter != null) {
            intent.setClass(this.mContext, ChannelPanelActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        for (NotificationPreferenceController next : this.mControllers) {
            if (next instanceof PreferenceManager.OnActivityResultListener) {
                ((PreferenceManager.OnActivityResultListener) next).onActivityResult(i, i2, intent);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.channel_notification_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        this.mControllers.add(new BlockPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new ImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new MinImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new HighImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new AllowSoundPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new SoundPreferenceController(context, this, this.mDependentFieldListener, this.mBackend));
        this.mControllers.add(new VibrationPreferenceController(context, this.mBackend));
        this.mControllers.add(new AppLinkPreferenceController(context));
        this.mControllers.add(new VisibilityPreferenceController(context, new LockPatternUtils(context), this.mBackend));
        this.mControllers.add(new LightsPreferenceController(context, this.mBackend));
        this.mControllers.add(new BadgePreferenceController(context, this.mBackend));
        this.mControllers.add(new DndPreferenceController(context, this.mBackend));
        this.mControllers.add(new NotificationsOffPreferenceController(context));
        this.mControllers.add(new ConversationPromotePreferenceController(context, this, this.mBackend));
        return new ArrayList(this.mControllers);
    }
}
