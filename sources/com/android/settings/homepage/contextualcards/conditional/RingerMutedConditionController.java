package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;

public class RingerMutedConditionController extends AbnormalRingerConditionController {

    /* renamed from: ID */
    static final int f114ID = Objects.hash(new Object[]{"RingerMutedConditionController"});
    private final Context mAppContext;

    public RingerMutedConditionController(Context context, ConditionManager conditionManager) {
        super(context, conditionManager);
        this.mAppContext = context;
    }

    public long getId() {
        return (long) f114ID;
    }

    public boolean isDisplayable() {
        return this.mAudioManager.getRingerModeInternal() == 0;
    }

    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId((long) f114ID).setMetricsConstant(1368).setActionText(this.mAppContext.getText(C1992R$string.condition_device_muted_action_turn_on_sound));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = C1992R$string.condition_device_muted_title;
        sb.append(context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(C1992R$string.condition_device_muted_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(C1983R$drawable.ic_notifications_off_24dp)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }
}
