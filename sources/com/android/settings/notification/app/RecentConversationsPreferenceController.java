package com.android.settings.notification.app;

import android.app.people.ConversationChannel;
import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentConversationsPreferenceController extends AbstractPreferenceController {
    private final NotificationBackend mBackend;
    protected Comparator<ConversationChannel> mConversationComparator = new Comparator<ConversationChannel>() {
        private final Collator sCollator = Collator.getInstance();

        public int compare(ConversationChannel conversationChannel, ConversationChannel conversationChannel2) {
            if (conversationChannel.getShortcutInfo().getLabel() == null && conversationChannel2.getShortcutInfo().getLabel() != null) {
                return 1;
            }
            if (conversationChannel.getShortcutInfo().getLabel() != null && conversationChannel2.getShortcutInfo().getLabel() == null) {
                return -1;
            }
            int compare = this.sCollator.compare(conversationChannel.getShortcutInfo().getLabel().toString(), conversationChannel2.getShortcutInfo().getLabel().toString());
            return compare == 0 ? conversationChannel.getNotificationChannel().getId().compareTo(conversationChannel2.getNotificationChannel().getId()) : compare;
        }
    };
    private List<ConversationChannel> mConversations;
    private final IPeopleManager mPs;

    public String getPreferenceKey() {
        return "recent_conversations";
    }

    public boolean isAvailable() {
        return true;
    }

    public RecentConversationsPreferenceController(Context context, NotificationBackend notificationBackend, IPeopleManager iPeopleManager) {
        super(context);
        this.mBackend = notificationBackend;
        this.mPs = iPeopleManager;
    }

    /* access modifiers changed from: package-private */
    public LayoutPreference getClearAll(PreferenceGroup preferenceGroup) {
        LayoutPreference layoutPreference = new LayoutPreference(this.mContext, C1987R$layout.conversations_clear_recents);
        layoutPreference.setOrder(1);
        Button button = (Button) layoutPreference.findViewById(C1985R$id.conversation_settings_clear_recents);
        button.setOnClickListener(new C1132x815781db(this, preferenceGroup, button));
        return layoutPreference;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getClearAll$0(PreferenceGroup preferenceGroup, Button button, View view) {
        try {
            this.mPs.removeAllRecentConversations();
            for (int preferenceCount = preferenceGroup.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference = preferenceGroup.getPreference(preferenceCount);
                if ((preference instanceof RecentConversationPreference) && ((RecentConversationPreference) preference).hasClearListener()) {
                    preferenceGroup.removePreference(preference);
                }
            }
            button.announceForAccessibility(this.mContext.getString(C1992R$string.recent_convos_removed));
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not clear recents", e);
        }
    }

    public void updateState(Preference preference) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
        try {
            this.mConversations = this.mPs.getRecentConversations().getList();
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could get recents", e);
        }
        Collections.sort(this.mConversations, this.mConversationComparator);
        populateList(this.mConversations, preferenceCategory);
    }

    /* access modifiers changed from: protected */
    public void populateList(List<ConversationChannel> list, PreferenceGroup preferenceGroup) {
        LayoutPreference clearAll;
        preferenceGroup.removeAll();
        boolean populateConversations = list != null ? populateConversations(list, preferenceGroup) : false;
        if (preferenceGroup.getPreferenceCount() == 0) {
            preferenceGroup.setVisible(false);
            return;
        }
        preferenceGroup.setVisible(true);
        if (populateConversations && (clearAll = getClearAll(preferenceGroup)) != null) {
            preferenceGroup.addPreference(clearAll);
        }
    }

    /* access modifiers changed from: protected */
    public boolean populateConversations(List<ConversationChannel> list, PreferenceGroup preferenceGroup) {
        int i = 100;
        boolean z = false;
        for (ConversationChannel next : list) {
            if (next.getNotificationChannel().getImportance() != 0 && (next.getNotificationChannelGroup() == null || !next.getNotificationChannelGroup().isBlocked())) {
                int i2 = i + 1;
                RecentConversationPreference createConversationPref = createConversationPref(preferenceGroup, next, i);
                preferenceGroup.addPreference(createConversationPref);
                if (createConversationPref.hasClearListener()) {
                    z = true;
                }
                i = i2;
            }
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public RecentConversationPreference createConversationPref(PreferenceGroup preferenceGroup, ConversationChannel conversationChannel, int i) {
        String str = conversationChannel.getShortcutInfo().getPackage();
        int uid = conversationChannel.getUid();
        String id = conversationChannel.getShortcutInfo().getId();
        RecentConversationPreference recentConversationPreference = new RecentConversationPreference(this.mContext);
        if (!conversationChannel.hasActiveNotifications()) {
            recentConversationPreference.setOnClearClickListener(new C1134x815781dd(this, str, uid, id, recentConversationPreference, preferenceGroup));
        }
        recentConversationPreference.setOrder(i);
        recentConversationPreference.setTitle(getTitle(conversationChannel));
        recentConversationPreference.setSummary(getSummary(conversationChannel));
        recentConversationPreference.setIcon(this.mBackend.getConversationDrawable(this.mContext, conversationChannel.getShortcutInfo(), str, uid, false));
        recentConversationPreference.setKey(conversationChannel.getNotificationChannel().getId() + ":" + id);
        recentConversationPreference.setOnPreferenceClickListener(new C1133x815781dc(this, str, uid, conversationChannel, id, recentConversationPreference));
        return recentConversationPreference;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createConversationPref$1(String str, int i, String str2, RecentConversationPreference recentConversationPreference, PreferenceGroup preferenceGroup) {
        try {
            this.mPs.removeRecentConversation(str, UserHandle.getUserId(i), str2);
            recentConversationPreference.getClearView().announceForAccessibility(this.mContext.getString(C1992R$string.recent_convo_removed));
            preferenceGroup.removePreference(recentConversationPreference);
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not clear recent", e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createConversationPref$2(String str, int i, ConversationChannel conversationChannel, String str2, RecentConversationPreference recentConversationPreference, Preference preference) {
        this.mBackend.createConversationNotificationChannel(str, i, conversationChannel.getNotificationChannel(), str2);
        getSubSettingLauncher(conversationChannel, recentConversationPreference.getTitle()).launch();
        return true;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getSummary(ConversationChannel conversationChannel) {
        if (conversationChannel.getNotificationChannelGroup() == null) {
            return conversationChannel.getNotificationChannel().getName();
        }
        return this.mContext.getString(C1992R$string.notification_conversation_summary, new Object[]{conversationChannel.getNotificationChannel().getName(), conversationChannel.getNotificationChannelGroup().getName()});
    }

    /* access modifiers changed from: package-private */
    public CharSequence getTitle(ConversationChannel conversationChannel) {
        return conversationChannel.getShortcutInfo().getLabel();
    }

    /* access modifiers changed from: package-private */
    public SubSettingLauncher getSubSettingLauncher(ConversationChannel conversationChannel, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", conversationChannel.getUid());
        bundle.putString("package", conversationChannel.getShortcutInfo().getPackage());
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannel.getNotificationChannel().getId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannel.getShortcutInfo().getId());
        return new SubSettingLauncher(this.mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setUserHandle(UserHandle.getUserHandleForUid(conversationChannel.getUid())).setTitleText(charSequence).setSourceMetricsCategory(1834);
    }
}
