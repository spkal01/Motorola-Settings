package com.android.settings.notification.app;

import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.service.notification.ConversationChannelWrapper;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.widget.LayoutPreference;

public class NoConversationsPreferenceController extends ConversationListPreferenceController {
    /* access modifiers changed from: private */
    public static String TAG = "NoConversationsPC";
    /* access modifiers changed from: private */
    public int mConversationCount = 0;
    /* access modifiers changed from: private */
    public IPeopleManager mPs;

    public String getPreferenceKey() {
        return "no_conversations";
    }

    /* access modifiers changed from: package-private */
    public Preference getSummaryPreference() {
        return null;
    }

    public boolean isAvailable() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return false;
    }

    static /* synthetic */ int access$012(NoConversationsPreferenceController noConversationsPreferenceController, int i) {
        int i2 = noConversationsPreferenceController.mConversationCount + i;
        noConversationsPreferenceController.mConversationCount = i2;
        return i2;
    }

    public NoConversationsPreferenceController(Context context, NotificationBackend notificationBackend, IPeopleManager iPeopleManager) {
        super(context, notificationBackend);
        this.mPs = iPeopleManager;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.setVisible(false);
    }

    public void updateState(final Preference preference) {
        LayoutPreference layoutPreference = (LayoutPreference) preference;
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                NoConversationsPreferenceController noConversationsPreferenceController = NoConversationsPreferenceController.this;
                int unused = noConversationsPreferenceController.mConversationCount = noConversationsPreferenceController.mBackend.getConversations(false).getList().size();
                try {
                    NoConversationsPreferenceController noConversationsPreferenceController2 = NoConversationsPreferenceController.this;
                    NoConversationsPreferenceController.access$012(noConversationsPreferenceController2, noConversationsPreferenceController2.mPs.getRecentConversations().getList().size());
                    return null;
                } catch (RemoteException e) {
                    Log.w(NoConversationsPreferenceController.TAG, "Error calling PS", e);
                    return null;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                if (NoConversationsPreferenceController.this.mContext != null) {
                    preference.setVisible(NoConversationsPreferenceController.this.mConversationCount == 0);
                }
            }
        }.execute(new Void[0]);
    }
}
