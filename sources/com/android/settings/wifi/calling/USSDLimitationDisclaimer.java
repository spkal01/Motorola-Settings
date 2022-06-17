package com.android.settings.wifi.calling;

import android.content.Context;
import com.android.settings.C1992R$string;

public class USSDLimitationDisclaimer extends DisclaimerItem {
    /* access modifiers changed from: protected */
    public String getName() {
        return "USSDLimitationDisclaimer";
    }

    /* access modifiers changed from: protected */
    public String getPrefKey() {
        return "key_has_agreed_ussd_limitation_disclaimer";
    }

    public USSDLimitationDisclaimer(Context context, int i) {
        super(context, i);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShow() {
        boolean z = getCarrierConfig().getBoolean("moto_show_wfc_ussd_disclaimer_bool");
        logd("shouldShow: " + z + "due to carrier config");
        if (!z) {
            return false;
        }
        return super.shouldShow();
    }

    /* access modifiers changed from: protected */
    public int getTitleId() {
        return C1992R$string.wfc_disclaimer_ussd_limitation_title_text;
    }

    /* access modifiers changed from: protected */
    public int getMessageId() {
        return C1992R$string.wfc_disclaimer_ussd_limitation_desc_text;
    }
}
