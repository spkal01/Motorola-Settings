package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkPolicyManager;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.Settings;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;

public class BackgroundDataConditionController implements ConditionalCardController {

    /* renamed from: ID */
    static final int f107ID = Objects.hash(new Object[]{"BackgroundDataConditionController"});
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final NetworkPolicyManager mNetworkPolicyManager;

    public void startMonitoringStateChange() {
    }

    public void stopMonitoringStateChange() {
    }

    public BackgroundDataConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mNetworkPolicyManager = (NetworkPolicyManager) context.getSystemService("netpolicy");
    }

    public long getId() {
        return (long) f107ID;
    }

    public boolean isDisplayable() {
        return this.mNetworkPolicyManager.getRestrictBackground();
    }

    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent(context, Settings.DataUsageSummaryActivity.class));
    }

    public void onActionClick() {
        this.mNetworkPolicyManager.setRestrictBackground(false);
        this.mConditionManager.onConditionChanged();
    }

    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId((long) f107ID).setMetricsConstant(378).setActionText(this.mAppContext.getText(C1992R$string.condition_turn_off));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = C1992R$string.condition_bg_data_title;
        sb.append(context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(C1992R$string.condition_bg_data_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(C1983R$drawable.ic_data_saver)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }
}
