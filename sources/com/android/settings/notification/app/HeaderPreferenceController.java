package com.android.settings.notification.app;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.widget.LayoutPreference;

public class HeaderPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private final DashboardFragment mFragment;
    private EntityHeaderController mHeaderController;
    private boolean mStarted = false;

    public String getPreferenceKey() {
        return "pref_app_header";
    }

    /* access modifiers changed from: package-private */
    public boolean isIncludedInFilter() {
        return true;
    }

    public HeaderPreferenceController(Context context, DashboardFragment dashboardFragment) {
        super(context, (NotificationBackend) null);
        this.mFragment = dashboardFragment;
    }

    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    public void updateState(Preference preference) {
        DashboardFragment dashboardFragment;
        if (this.mAppRow != null && (dashboardFragment = this.mFragment) != null) {
            FragmentActivity activity = this.mStarted ? dashboardFragment.getActivity() : null;
            if (activity != null) {
                DashboardFragment dashboardFragment2 = this.mFragment;
                int i = C1985R$id.entity_header;
                EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, dashboardFragment2, ((LayoutPreference) preference).findViewById(i));
                this.mHeaderController = newInstance;
                LayoutPreference done = newInstance.setIcon(this.mAppRow.icon).setLabel(getLabel()).setSummary(getSummary()).setSecondSummary(getSecondSummary()).setPackageName(this.mAppRow.pkg).setUid(this.mAppRow.uid).setButtonActions(1, 0).setHasAppInfoLink(true).setRecyclerView(this.mFragment.getListView(), this.mFragment.getSettingsLifecycle()).done((Activity) activity, this.mContext);
                done.findViewById(i).setVisibility(0);
                done.findViewById(i).setBackground((Drawable) null);
            }
        }
    }

    public CharSequence getLabel() {
        if (this.mChannel == null || isDefaultChannel()) {
            return this.mAppRow.label;
        }
        return this.mChannel.getName();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mStarted = true;
    }

    public CharSequence getSummary() {
        if (this.mChannel == null) {
            return "";
        }
        NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
        if (notificationChannelGroup == null || TextUtils.isEmpty(notificationChannelGroup.getName())) {
            return this.mAppRow.label.toString();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        BidiFormatter instance = BidiFormatter.getInstance();
        spannableStringBuilder.append(instance.unicodeWrap(this.mAppRow.label));
        spannableStringBuilder.append(instance.unicodeWrap(this.mContext.getText(C1992R$string.notification_header_divider_symbol_with_spaces)));
        spannableStringBuilder.append(instance.unicodeWrap(this.mChannelGroup.getName().toString()));
        return spannableStringBuilder.toString();
    }

    public CharSequence getSecondSummary() {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            return null;
        }
        return notificationChannel.getDescription();
    }
}
