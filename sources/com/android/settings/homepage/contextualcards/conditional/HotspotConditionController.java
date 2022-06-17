package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.TetherSettings;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.util.Objects;

public class HotspotConditionController implements ConditionalCardController {

    /* renamed from: ID */
    static final int f112ID = Objects.hash(new Object[]{"HotspotConditionController"});
    private static final IntentFilter WIFI_AP_STATE_FILTER = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
    private final Context mAppContext;
    /* access modifiers changed from: private */
    public final ConditionManager mConditionManager;
    private final Receiver mReceiver = new Receiver();
    private final WifiManager mWifiManager;

    public HotspotConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    public long getId() {
        return (long) f112ID;
    }

    public boolean isDisplayable() {
        return this.mWifiManager.isWifiApEnabled();
    }

    public void onPrimaryClick(Context context) {
        new SubSettingLauncher(context).setDestination(TetherSettings.class.getName()).setSourceMetricsCategory(35).setTitleRes(C1992R$string.tether_settings_title_all).launch();
    }

    public void onActionClick() {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mAppContext, "no_config_tethering", UserHandle.myUserId());
        if (checkIfRestrictionEnforced != null) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mAppContext, checkIfRestrictionEnforced);
        } else {
            ((ConnectivityManager) this.mAppContext.getSystemService("connectivity")).stopTethering(0);
        }
    }

    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId((long) f112ID).setMetricsConstant(382).setActionText(this.mAppContext.getText(C1992R$string.condition_turn_off));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = C1992R$string.condition_hotspot_title;
        sb.append(context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(getSsid().toString()).setIconDrawable(this.mAppContext.getDrawable(C1983R$drawable.ic_hotspot)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }

    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, WIFI_AP_STATE_FILTER);
    }

    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    private CharSequence getSsid() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null) {
            return "";
        }
        return softApConfiguration.getSsid();
    }

    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                HotspotConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }
}
