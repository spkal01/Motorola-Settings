package com.android.settings.notification.app;

import android.content.Context;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C1992R$string;
import com.android.settings.notification.NotificationBackend;
import java.util.Collections;
import java.util.List;

public class PriorityConversationsPreferenceController extends ConversationListPreferenceController {
    private List<ConversationChannelWrapper> mConversations;

    public String getPreferenceKey() {
        return "important_conversations";
    }

    public boolean isAvailable() {
        return true;
    }

    public PriorityConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    /* access modifiers changed from: package-private */
    public Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(C1992R$string.important_conversations_summary_bubbles);
        preference.setSelectable(false);
        return preference;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }

    public void updateState(Preference preference) {
        List<ConversationChannelWrapper> list = this.mBackend.getConversations(true).getList();
        this.mConversations = list;
        Collections.sort(list, this.mConversationComparator);
        populateList(this.mConversations, (PreferenceCategory) preference);
    }
}
