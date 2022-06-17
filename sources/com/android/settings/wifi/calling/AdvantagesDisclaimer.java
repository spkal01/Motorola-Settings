package com.android.settings.wifi.calling;

import android.content.Context;
import com.android.settings.C1992R$string;

public class AdvantagesDisclaimer extends DisclaimerItem {
    /* access modifiers changed from: protected */
    public String getName() {
        return "AdvantagesDisclaimer";
    }

    /* access modifiers changed from: protected */
    public String getPrefKey() {
        return "key_has_agreed_advantages_disclaimer";
    }

    public AdvantagesDisclaimer(Context context, int i) {
        super(context, i);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShow() {
        if (getCarrierConfig().getBoolean("moto_show_customized_wfc_help_and_dialog_bool")) {
            return super.shouldShow();
        }
        logd("shouldShow: false due to carrier config is default(false).");
        return false;
    }

    /* access modifiers changed from: protected */
    public int getTitleId() {
        return C1992R$string.wfc_disclaimer_advantages_title_text;
    }

    /* access modifiers changed from: protected */
    public int getMessageId() {
        return C1992R$string.wfc_disclaimer_advantages_desc_text;
    }
}
