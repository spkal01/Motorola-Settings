package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.os.PowerManager;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settings.fuelgauge.batterysaver.BatterySaverSettings;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import java.util.Objects;

public class BatterySaverConditionController implements ConditionalCardController, BatterySaverReceiver.BatterySaverListener {

    /* renamed from: ID */
    static final int f108ID = Objects.hash(new Object[]{"BatterySaverConditionController"});
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final PowerManager mPowerManager;
    private final BatterySaverReceiver mReceiver;

    public void onBatteryChanged(boolean z) {
    }

    public BatterySaverConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
    }

    public long getId() {
        return (long) f108ID;
    }

    public boolean isDisplayable() {
        return this.mPowerManager.isPowerSaveMode();
    }

    public void onPrimaryClick(Context context) {
        new SubSettingLauncher(context).setDestination(BatterySaverSettings.class.getName()).setSourceMetricsCategory(35).setTitleRes(C1992R$string.battery_saver).launch();
    }

    public void onActionClick() {
        BatterySaverUtils.setPowerSaveMode(this.mAppContext, false, false);
    }

    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId((long) f108ID).setMetricsConstant(379).setActionText(this.mAppContext.getText(C1992R$string.condition_turn_off));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = C1992R$string.condition_battery_title;
        sb.append(context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(C1992R$string.condition_battery_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(C1983R$drawable.ic_battery_saver_accent_24dp)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }

    public void startMonitoringStateChange() {
        this.mReceiver.setListening(true);
    }

    public void stopMonitoringStateChange() {
        this.mReceiver.setListening(false);
    }

    public void onPowerSaveModeChanged() {
        this.mConditionManager.onConditionChanged();
    }
}
