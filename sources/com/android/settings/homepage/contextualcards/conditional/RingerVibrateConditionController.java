package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;

public class RingerVibrateConditionController extends AbnormalRingerConditionController {

    /* renamed from: ID */
    static final int f115ID = Objects.hash(new Object[]{"RingerVibrateConditionController"});
    private final Context mAppContext;

    public RingerVibrateConditionController(Context context, ConditionManager conditionManager) {
        super(context, conditionManager);
        this.mAppContext = context;
    }

    public long getId() {
        return (long) f115ID;
    }

    public boolean isDisplayable() {
        return this.mAudioManager.getRingerModeInternal() == 1;
    }

    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId((long) f115ID).setMetricsConstant(1369).setActionText(this.mAppContext.getText(C1992R$string.condition_device_muted_action_turn_on_sound));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = C1992R$string.condition_device_vibrate_title;
        sb.append(context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(C1992R$string.condition_device_vibrate_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(C1983R$drawable.ic_volume_ringer_vibrate)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }
}
