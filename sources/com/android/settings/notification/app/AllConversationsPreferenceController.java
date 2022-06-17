package com.android.settings.notification.app;

import android.content.Context;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C1992R$string;
import com.android.settings.notification.NotificationBackend;
import java.util.Collections;
import java.util.List;

public class AllConversationsPreferenceController extends ConversationListPreferenceController {
    private List<ConversationChannelWrapper> mConversations;

    public String getPreferenceKey() {
        return "other_conversations";
    }

    public boolean isAvailable() {
        return true;
    }

    public AllConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    /* access modifiers changed from: package-private */
    public Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(C1992R$string.other_conversations_summary);
        preference.setSelectable(false);
        return preference;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return !conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }

    public void updateState(Preference preference) {
        List<ConversationChannelWrapper> list = this.mBackend.getConversations(false).getList();
        this.mConversations = list;
        Collections.sort(list, this.mConversationComparator);
        populateList(this.mConversations, (PreferenceCategory) preference);
    }
}
