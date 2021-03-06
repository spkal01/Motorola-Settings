package com.android.settings.notification.history;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NotificationStation extends SettingsPreferenceFragment {
    private static final String TAG = NotificationStation.class.getSimpleName();
    private Context mContext;
    private final NotificationListenerService mListener = new NotificationListenerService() {
        public void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
            Object[] objArr = new Object[2];
            int i = 0;
            objArr[0] = statusBarNotification.getNotification();
            if (rankingMap != null) {
                i = rankingMap.getOrderedKeys().length;
            }
            objArr[1] = Integer.valueOf(i);
            NotificationStation.logd("onNotificationPosted: %s, with update for %d", objArr);
            NotificationListenerService.RankingMap unused = NotificationStation.this.mRanking = rankingMap;
            if (!statusBarNotification.getNotification().isGroupSummary()) {
                NotificationStation.this.addOrUpdateNotification(statusBarNotification);
            }
        }

        public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
            Object[] objArr = new Object[1];
            objArr[0] = Integer.valueOf(rankingMap == null ? 0 : rankingMap.getOrderedKeys().length);
            NotificationStation.logd("onNotificationRankingUpdate with update for %d", objArr);
            NotificationListenerService.RankingMap unused = NotificationStation.this.mRanking = rankingMap;
            if (!statusBarNotification.getNotification().isGroupSummary()) {
                NotificationStation.this.markNotificationAsDismissed(statusBarNotification);
            }
        }

        public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
            Object[] objArr = new Object[1];
            objArr[0] = Integer.valueOf(rankingMap == null ? 0 : rankingMap.getOrderedKeys().length);
            NotificationStation.logd("onNotificationRankingUpdate with update for %d", objArr);
            NotificationListenerService.RankingMap unused = NotificationStation.this.mRanking = rankingMap;
            NotificationStation.this.updateNotificationsFromRanking();
        }

        public void onListenerConnected() {
            NotificationListenerService.RankingMap unused = NotificationStation.this.mRanking = getCurrentRanking();
            Object[] objArr = new Object[1];
            objArr[0] = Integer.valueOf(NotificationStation.this.mRanking == null ? 0 : NotificationStation.this.mRanking.getOrderedKeys().length);
            NotificationStation.logd("onListenerConnected with update for %d", objArr);
            NotificationStation.this.populateNotifications();
        }
    };
    private INotificationManager mNoMan;
    private LinkedList<HistoricalNotificationInfo> mNotificationInfos;
    private final Comparator<HistoricalNotificationInfo> mNotificationSorter = NotificationStation$$ExternalSyntheticLambda0.INSTANCE;
    private PackageManager mPm;
    /* access modifiers changed from: private */
    public NotificationListenerService.RankingMap mRanking;

    /* access modifiers changed from: private */
    public static void logd(String str, Object... objArr) {
    }

    public int getMetricsCategory() {
        return 75;
    }

    private static class HistoricalNotificationInfo {
        public boolean active;
        public boolean alerted;
        public boolean badged;
        public NotificationChannel channel;
        public String channelId;
        public Drawable icon;
        public String key;
        public CharSequence notificationExtra;
        public String pkg;
        public CharSequence pkgname;
        public int priority;
        public CharSequence rankingExtra;
        public CharSequence text;
        public long timestamp;
        public CharSequence title;
        public int user;
        public boolean visuallyInterruptive;

        private HistoricalNotificationInfo() {
        }

        public void updateFrom(HistoricalNotificationInfo historicalNotificationInfo) {
            this.channel = historicalNotificationInfo.channel;
            this.icon = historicalNotificationInfo.icon;
            this.title = historicalNotificationInfo.title;
            this.text = historicalNotificationInfo.text;
            this.priority = historicalNotificationInfo.priority;
            this.timestamp = historicalNotificationInfo.timestamp;
            this.active = historicalNotificationInfo.active;
            this.alerted = historicalNotificationInfo.alerted;
            this.visuallyInterruptive = historicalNotificationInfo.visuallyInterruptive;
            this.notificationExtra = historicalNotificationInfo.notificationExtra;
            this.rankingExtra = historicalNotificationInfo.rankingExtra;
        }
    }

    public void onAttach(Activity activity) {
        logd("onAttach(%s)", activity.getClass().getSimpleName());
        super.onAttach(activity);
        this.mContext = activity;
        this.mPm = activity.getPackageManager();
        this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        this.mNotificationInfos = new LinkedList<>();
    }

    public void onDetach() {
        logd("onDetach()", new Object[0]);
        super.onDetach();
    }

    public void onPause() {
        try {
            this.mListener.unregisterAsSystemService();
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot unregister listener", e);
        }
        super.onPause();
    }

    public void onActivityCreated(Bundle bundle) {
        logd("onActivityCreated(%s)", bundle);
        super.onActivityCreated(bundle);
        Utils.forceCustomPadding(getListView(), false);
    }

    public void onResume() {
        logd("onResume()", new Object[0]);
        super.onResume();
        try {
            this.mListener.registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), ActivityManager.getCurrentUser());
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot register listener", e);
        }
    }

    /* access modifiers changed from: private */
    public void populateNotifications() {
        loadNotifications();
        int size = this.mNotificationInfos.size();
        logd("adding %d infos", Integer.valueOf(size));
        if (getPreferenceScreen() == null) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
        }
        getPreferenceScreen().removeAll();
        for (int i = 0; i < size; i++) {
            getPreferenceScreen().addPreference(new HistoricalNotificationPreference(getPrefContext(), this.mNotificationInfos.get(i), i));
        }
    }

    /* access modifiers changed from: private */
    public void markNotificationAsDismissed(StatusBarNotification statusBarNotification) {
        int size = this.mNotificationInfos.size();
        for (int i = 0; i < size; i++) {
            HistoricalNotificationInfo historicalNotificationInfo = this.mNotificationInfos.get(i);
            if (TextUtils.equals(historicalNotificationInfo.key, statusBarNotification.getKey())) {
                historicalNotificationInfo.active = false;
                ((HistoricalNotificationPreference) getPreferenceScreen().findPreference(statusBarNotification.getKey())).updatePreference(historicalNotificationInfo);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void addOrUpdateNotification(StatusBarNotification statusBarNotification) {
        boolean z = true;
        HistoricalNotificationInfo createFromSbn = createFromSbn(statusBarNotification, true);
        int size = this.mNotificationInfos.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            HistoricalNotificationInfo historicalNotificationInfo = this.mNotificationInfos.get(i);
            if (TextUtils.equals(historicalNotificationInfo.key, statusBarNotification.getKey()) && historicalNotificationInfo.active && !createFromSbn.alerted && !createFromSbn.visuallyInterruptive) {
                historicalNotificationInfo.updateFrom(createFromSbn);
                ((HistoricalNotificationPreference) getPreferenceScreen().findPreference(statusBarNotification.getKey())).updatePreference(historicalNotificationInfo);
                z = false;
                break;
            }
            i++;
        }
        if (z) {
            this.mNotificationInfos.addFirst(createFromSbn);
            getPreferenceScreen().addPreference(new HistoricalNotificationPreference(getPrefContext(), this.mNotificationInfos.peekFirst(), this.mNotificationInfos.size() * -1));
        }
    }

    /* access modifiers changed from: private */
    public void updateNotificationsFromRanking() {
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            HistoricalNotificationInfo historicalNotificationInfo = this.mNotificationInfos.get(i);
            this.mRanking.getRanking(((HistoricalNotificationPreference) getPreferenceScreen().getPreference(i)).getKey(), ranking);
            updateFromRanking(historicalNotificationInfo);
            ((HistoricalNotificationPreference) getPreferenceScreen().findPreference(historicalNotificationInfo.key)).updatePreference(historicalNotificationInfo);
        }
    }

    private static CharSequence bold(CharSequence charSequence) {
        if (charSequence.length() == 0) {
            return charSequence;
        }
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new StyleSpan(1), 0, charSequence.length(), 0);
        return spannableString;
    }

    private static String getTitleString(Notification notification) {
        Bundle bundle = notification.extras;
        CharSequence charSequence = bundle != null ? bundle.getCharSequence("android.title") : null;
        if (charSequence == null) {
            return "";
        }
        return String.valueOf(charSequence);
    }

    private static String getTextString(Context context, Notification notification) {
        CharSequence charSequence;
        List<Notification.MessagingStyle.Message> messages;
        Bundle bundle = notification.extras;
        if (bundle != null) {
            charSequence = bundle.getCharSequence("android.text");
            Notification.Builder recoverBuilder = Notification.Builder.recoverBuilder(context, notification);
            if (recoverBuilder.getStyle() instanceof Notification.BigTextStyle) {
                charSequence = ((Notification.BigTextStyle) recoverBuilder.getStyle()).getBigText();
            } else if ((recoverBuilder.getStyle() instanceof Notification.MessagingStyle) && (messages = ((Notification.MessagingStyle) recoverBuilder.getStyle()).getMessages()) != null && messages.size() > 0) {
                charSequence = messages.get(messages.size() - 1).getText();
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.extras.getCharSequence("android.text");
            }
        } else {
            charSequence = null;
        }
        if (charSequence == null) {
            return "";
        }
        return String.valueOf(charSequence);
    }

    private Drawable loadIcon(HistoricalNotificationInfo historicalNotificationInfo, StatusBarNotification statusBarNotification) {
        Drawable loadDrawableAsUser = statusBarNotification.getNotification().getSmallIcon().loadDrawableAsUser(statusBarNotification.getPackageContext(this.mContext), historicalNotificationInfo.user);
        if (loadDrawableAsUser == null) {
            return null;
        }
        loadDrawableAsUser.mutate();
        loadDrawableAsUser.setColorFilter(statusBarNotification.getNotification().color, PorterDuff.Mode.SRC_ATOP);
        return loadDrawableAsUser;
    }

    private static String formatPendingIntent(PendingIntent pendingIntent) {
        StringBuilder sb = new StringBuilder();
        IntentSender intentSender = pendingIntent.getIntentSender();
        sb.append("Intent(pkg=");
        sb.append(intentSender.getCreatorPackage());
        try {
            if (ActivityManager.getService().isIntentSenderAnActivity(intentSender.getTarget())) {
                sb.append(" (activity)");
            }
        } catch (RemoteException unused) {
        }
        sb.append(")");
        return sb.toString();
    }

    private void loadNotifications() {
        try {
            StatusBarNotification[] activeNotificationsWithAttribution = this.mNoMan.getActiveNotificationsWithAttribution(this.mContext.getPackageName(), this.mContext.getAttributionTag());
            StatusBarNotification[] historicalNotificationsWithAttribution = this.mNoMan.getHistoricalNotificationsWithAttribution(this.mContext.getPackageName(), this.mContext.getAttributionTag(), 50, false);
            ArrayList arrayList = new ArrayList(activeNotificationsWithAttribution.length + historicalNotificationsWithAttribution.length);
            StatusBarNotification[][] statusBarNotificationArr = {activeNotificationsWithAttribution, historicalNotificationsWithAttribution};
            for (int i = 0; i < 2; i++) {
                StatusBarNotification[] statusBarNotificationArr2 = statusBarNotificationArr[i];
                for (StatusBarNotification statusBarNotification : statusBarNotificationArr2) {
                    if (!statusBarNotification.getNotification().isGroupSummary()) {
                        HistoricalNotificationInfo createFromSbn = createFromSbn(statusBarNotification, statusBarNotificationArr2 == activeNotificationsWithAttribution);
                        logd("   [%d] %s: %s", Long.valueOf(createFromSbn.timestamp), createFromSbn.pkg, createFromSbn.title);
                        arrayList.add(createFromSbn);
                    }
                }
            }
            arrayList.sort(this.mNotificationSorter);
            this.mNotificationInfos = new LinkedList<>(arrayList);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot load Notifications: ", e);
        }
    }

    private HistoricalNotificationInfo createFromSbn(StatusBarNotification statusBarNotification, boolean z) {
        int i;
        Notification notification = statusBarNotification.getNotification();
        HistoricalNotificationInfo historicalNotificationInfo = new HistoricalNotificationInfo();
        historicalNotificationInfo.pkg = statusBarNotification.getPackageName();
        boolean z2 = false;
        if (statusBarNotification.getUserId() == -1) {
            i = 0;
        } else {
            i = statusBarNotification.getUserId();
        }
        historicalNotificationInfo.user = i;
        if (i != ActivityManager.getCurrentUser()) {
            z2 = true;
        }
        historicalNotificationInfo.badged = z2;
        Drawable loadIcon = loadIcon(historicalNotificationInfo, statusBarNotification);
        historicalNotificationInfo.icon = loadIcon;
        if (loadIcon == null) {
            historicalNotificationInfo.icon = loadPackageIconDrawable(historicalNotificationInfo.pkg, historicalNotificationInfo.user);
        }
        historicalNotificationInfo.pkgname = loadPackageName(historicalNotificationInfo.pkg);
        historicalNotificationInfo.title = getTitleString(notification);
        historicalNotificationInfo.text = getTextString(statusBarNotification.getPackageContext(this.mContext), notification);
        historicalNotificationInfo.timestamp = statusBarNotification.getPostTime();
        historicalNotificationInfo.priority = notification.priority;
        historicalNotificationInfo.key = statusBarNotification.getKey();
        historicalNotificationInfo.channelId = statusBarNotification.getNotification().getChannelId();
        historicalNotificationInfo.active = z;
        historicalNotificationInfo.notificationExtra = generateExtraText(statusBarNotification, historicalNotificationInfo);
        updateFromRanking(historicalNotificationInfo);
        return historicalNotificationInfo;
    }

    private void updateFromRanking(HistoricalNotificationInfo historicalNotificationInfo) {
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        NotificationListenerService.RankingMap rankingMap = this.mRanking;
        if (rankingMap != null) {
            rankingMap.getRanking(historicalNotificationInfo.key, ranking);
            historicalNotificationInfo.alerted = ranking.getLastAudiblyAlertedMillis() > 0;
            historicalNotificationInfo.visuallyInterruptive = ranking.visuallyInterruptive();
            historicalNotificationInfo.channel = ranking.getChannel();
            historicalNotificationInfo.rankingExtra = generateRankingExtraText(historicalNotificationInfo);
        }
    }

    private CharSequence generateRankingExtraText(HistoricalNotificationInfo historicalNotificationInfo) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String string = getString(C1992R$string.notification_log_details_delimiter);
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        NotificationListenerService.RankingMap rankingMap = this.mRanking;
        if (rankingMap != null && rankingMap.getRanking(historicalNotificationInfo.key, ranking)) {
            if (historicalNotificationInfo.active && historicalNotificationInfo.alerted) {
                spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_alerted)));
            }
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_channel))).append(string).append(historicalNotificationInfo.channel.toString());
            spannableStringBuilder.append("\n").append(bold("getShortcutInfo")).append(string).append(String.valueOf(ranking.getConversationShortcutInfo()));
            String str = "true";
            spannableStringBuilder.append("\n").append(bold("isConversation")).append(string).append(ranking.isConversation() ? str : "false");
            SpannableStringBuilder append = spannableStringBuilder.append("\n").append(bold("isBubble")).append(string);
            if (!ranking.isBubble()) {
                str = "false";
            }
            append.append(str);
            if (historicalNotificationInfo.active) {
                spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_importance))).append(string).append(NotificationListenerService.Ranking.importanceToString(ranking.getImportance()));
                if (ranking.getImportanceExplanation() != null) {
                    spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_explanation))).append(string).append(ranking.getImportanceExplanation());
                }
                spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_badge))).append(string).append(Boolean.toString(ranking.canShowBadge()));
            }
        } else if (this.mRanking == null) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_ranking_null)));
        } else {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_ranking_none)));
        }
        return spannableStringBuilder;
    }

    private CharSequence generateExtraText(StatusBarNotification statusBarNotification, HistoricalNotificationInfo historicalNotificationInfo) {
        Notification notification = statusBarNotification.getNotification();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String string = getString(C1992R$string.notification_log_details_delimiter);
        spannableStringBuilder.append(bold(getString(C1992R$string.notification_log_details_package))).append(string).append(historicalNotificationInfo.pkg).append("\n").append(bold(getString(C1992R$string.notification_log_details_key))).append(string).append(statusBarNotification.getKey());
        spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_icon))).append(string).append(String.valueOf(notification.getSmallIcon()));
        spannableStringBuilder.append("\n").append(bold("postTime")).append(string).append(String.valueOf(statusBarNotification.getPostTime()));
        if (notification.getTimeoutAfter() != 0) {
            spannableStringBuilder.append("\n").append(bold("timeoutAfter")).append(string).append(String.valueOf(notification.getTimeoutAfter()));
        }
        if (statusBarNotification.isGroup()) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_group))).append(string).append(String.valueOf(statusBarNotification.getGroupKey()));
            if (notification.isGroupSummary()) {
                spannableStringBuilder.append(bold(getString(C1992R$string.notification_log_details_group_summary)));
            }
        }
        if (notification.publicVersion != null) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_public_version))).append(string).append(getTitleString(notification.publicVersion));
        }
        if (notification.contentIntent != null) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_content_intent))).append(string).append(formatPendingIntent(notification.contentIntent));
        }
        if (notification.deleteIntent != null) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_delete_intent))).append(string).append(formatPendingIntent(notification.deleteIntent));
        }
        if (notification.fullScreenIntent != null) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_full_screen_intent))).append(string).append(formatPendingIntent(notification.fullScreenIntent));
        }
        Notification.Action[] actionArr = notification.actions;
        if (actionArr != null && actionArr.length > 0) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_actions)));
            int i = 0;
            while (true) {
                Notification.Action[] actionArr2 = notification.actions;
                if (i >= actionArr2.length) {
                    break;
                }
                Notification.Action action = actionArr2[i];
                spannableStringBuilder.append("\n  ").append(String.valueOf(i)).append(' ').append(bold(getString(C1992R$string.notification_log_details_title))).append(string).append(action.title);
                if (action.actionIntent != null) {
                    spannableStringBuilder.append("\n    ").append(bold(getString(C1992R$string.notification_log_details_content_intent))).append(string).append(formatPendingIntent(action.actionIntent));
                }
                if (action.getRemoteInputs() != null) {
                    spannableStringBuilder.append("\n    ").append(bold(getString(C1992R$string.notification_log_details_remoteinput))).append(string).append(String.valueOf(action.getRemoteInputs().length));
                }
                i++;
            }
        }
        if (notification.contentView != null) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_content_view))).append(string).append(notification.contentView.toString());
        }
        if (notification.getBubbleMetadata() != null) {
            spannableStringBuilder.append("\n").append(bold("bubbleMetadata")).append(string).append(String.valueOf(notification.getBubbleMetadata()));
        }
        if (notification.getShortcutId() != null) {
            spannableStringBuilder.append("\n").append(bold("shortcutId")).append(string).append(String.valueOf(notification.getShortcutId()));
        }
        Bundle bundle = notification.extras;
        if (bundle != null && bundle.size() > 0) {
            spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_extras)));
            for (String str : notification.extras.keySet()) {
                String valueOf = String.valueOf(notification.extras.get(str));
                if (valueOf.length() > 100) {
                    valueOf = valueOf.substring(0, 100) + "...";
                }
                spannableStringBuilder.append("\n  ").append(str).append(string).append(valueOf);
            }
        }
        Parcel obtain = Parcel.obtain();
        notification.writeToParcel(obtain, 0);
        spannableStringBuilder.append("\n").append(bold(getString(C1992R$string.notification_log_details_parcel))).append(string).append(String.valueOf(obtain.dataPosition())).append(' ').append(bold(getString(C1992R$string.notification_log_details_ashmem))).append(string).append(String.valueOf(obtain.getBlobAshmemSize())).append("\n");
        return spannableStringBuilder;
    }

    private Drawable loadPackageIconDrawable(String str, int i) {
        try {
            return this.mPm.getApplicationIcon(str);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot get application icon", e);
            return null;
        }
    }

    private CharSequence loadPackageName(String str) {
        try {
            ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(str, 4194304);
            if (applicationInfo != null) {
                return this.mPm.getApplicationLabel(applicationInfo);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot load package name", e);
        }
        return str;
    }

    private static class HistoricalNotificationPreference extends Preference {
        private static long sLastExpandedTimestamp;
        private Context mContext;
        private final HistoricalNotificationInfo mInfo;
        public ViewGroup mItemView;

        public HistoricalNotificationPreference(Context context, HistoricalNotificationInfo historicalNotificationInfo, int i) {
            super(context);
            setLayoutResource(C1987R$layout.notification_log_row);
            setOrder(i);
            setKey(historicalNotificationInfo.key);
            this.mInfo = historicalNotificationInfo;
            this.mContext = context;
        }

        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            this.mItemView = (ViewGroup) preferenceViewHolder.itemView;
            updatePreference(this.mInfo);
            preferenceViewHolder.findViewById(C1985R$id.timestamp).setOnLongClickListener(new C1140xd404025a(this, preferenceViewHolder));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onBindViewHolder$0(PreferenceViewHolder preferenceViewHolder, View view) {
            View findViewById = preferenceViewHolder.findViewById(C1985R$id.extra);
            findViewById.setVisibility(findViewById.getVisibility() == 0 ? 8 : 0);
            sLastExpandedTimestamp = this.mInfo.timestamp;
            return false;
        }

        public void updatePreference(HistoricalNotificationInfo historicalNotificationInfo) {
            ViewGroup viewGroup = this.mItemView;
            if (viewGroup != null) {
                if (historicalNotificationInfo.icon != null) {
                    ((ImageView) viewGroup.findViewById(C1985R$id.icon)).setImageDrawable(this.mInfo.icon);
                }
                ((TextView) this.mItemView.findViewById(C1985R$id.pkgname)).setText(this.mInfo.pkgname);
                ViewGroup viewGroup2 = this.mItemView;
                int i = C1985R$id.timestamp;
                viewGroup2.findViewById(i).setTime(historicalNotificationInfo.timestamp);
                int i2 = 0;
                if (!TextUtils.isEmpty(historicalNotificationInfo.title)) {
                    ViewGroup viewGroup3 = this.mItemView;
                    int i3 = C1985R$id.title;
                    ((TextView) viewGroup3.findViewById(i3)).setText(historicalNotificationInfo.title);
                    this.mItemView.findViewById(i3).setVisibility(0);
                } else {
                    this.mItemView.findViewById(C1985R$id.title).setVisibility(8);
                }
                if (!TextUtils.isEmpty(historicalNotificationInfo.text)) {
                    ViewGroup viewGroup4 = this.mItemView;
                    int i4 = C1985R$id.text;
                    ((TextView) viewGroup4.findViewById(i4)).setText(historicalNotificationInfo.text);
                    this.mItemView.findViewById(i4).setVisibility(0);
                } else {
                    this.mItemView.findViewById(C1985R$id.text).setVisibility(8);
                }
                if (historicalNotificationInfo.icon != null) {
                    ((ImageView) this.mItemView.findViewById(C1985R$id.icon)).setImageDrawable(historicalNotificationInfo.icon);
                }
                ImageView imageView = (ImageView) this.mItemView.findViewById(C1985R$id.profile_badge);
                imageView.setImageDrawable(this.mContext.getPackageManager().getUserBadgeForDensity(UserHandle.of(historicalNotificationInfo.user), -1));
                imageView.setVisibility(historicalNotificationInfo.badged ? 0 : 8);
                this.mItemView.findViewById(i).setTime(this.mInfo.timestamp);
                ((TextView) this.mItemView.findViewById(C1985R$id.notification_extra)).setText(this.mInfo.notificationExtra);
                ((TextView) this.mItemView.findViewById(C1985R$id.ranking_extra)).setText(this.mInfo.rankingExtra);
                this.mItemView.findViewById(C1985R$id.extra).setVisibility(this.mInfo.timestamp == sLastExpandedTimestamp ? 0 : 8);
                this.mItemView.setAlpha(this.mInfo.active ? 1.0f : 0.5f);
                View findViewById = this.mItemView.findViewById(C1985R$id.alerted_icon);
                if (!this.mInfo.alerted) {
                    i2 = 8;
                }
                findViewById.setVisibility(i2);
            }
        }

        public void performClick() {
            Intent putExtra = new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS").putExtra("android.provider.extra.APP_PACKAGE", this.mInfo.pkg);
            HistoricalNotificationInfo historicalNotificationInfo = this.mInfo;
            NotificationChannel notificationChannel = historicalNotificationInfo.channel;
            Intent putExtra2 = putExtra.putExtra("android.provider.extra.CHANNEL_ID", notificationChannel != null ? notificationChannel.getId() : historicalNotificationInfo.channelId);
            putExtra2.addFlags(268435456);
            getContext().startActivity(putExtra2);
        }
    }
}
