package com.motorola.settings.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.motorola.settings.network.cache.MotoMnsCache;

public class ContactDiscoveryActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (TextUtils.equals(intent != null ? intent.getAction() : null, "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN")) {
            int intExtra = intent.getIntExtra("android.provider.extra.SUB_ID", Integer.MIN_VALUE);
            if (Integer.MIN_VALUE != intExtra) {
                handleContactDiscoveryIntent(MotoMnsCache.getIns(this).getAvailableSubInfo(intExtra));
            } else {
                throw new IllegalArgumentException("Intent with action SHOW_CAPABILITY_DISCOVERY_OPT_IN must also include the extra Settings#EXTRA_SUB_ID");
            }
        }
        finish();
    }

    private void handleContactDiscoveryIntent(SubscriptionInfo subscriptionInfo) {
        CharSequence charSequence;
        int i;
        if (subscriptionInfo != null) {
            i = subscriptionInfo.getSubscriptionId();
            charSequence = subscriptionInfo.getDisplayName();
        } else {
            i = -1;
            charSequence = "";
        }
        if (!(MobileNetworkUtils.isContactDiscoveryVisible(this, i) && !MobileNetworkUtils.isContactDiscoveryEnabled((Context) this, i))) {
            return;
        }
        if (shouldShowNotificationForContactsDiscoveryDialog()) {
            new ContactDiscoveryNotification(this, i, charSequence).showNotification();
            return;
        }
        incrementContactsDiscoveryDialogDisplayCount();
        MotoMnsLog.logd("ContactDiscoveryActivity", "handleContactDiscoveryIntent: show contacts discovery dialog for subId " + i);
        Intent intent = new Intent(this, MobileNetworkActivity.class);
        intent.setAction("android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN");
        intent.putExtra("android.provider.extra.SUB_ID", i);
        intent.setFlags(805306368);
        startActivity(intent);
    }

    private boolean shouldShowNotificationForContactsDiscoveryDialog() {
        boolean z = false;
        int i = getSharedPreferences("contact_discovery_settings", 0).getInt("contact_discovery_display_count", 0);
        MotoMnsLog.logd("ContactDiscoveryActivity", "hasAlreadyShownContactsDiscoveryDialog: Displayed " + i + " before.");
        if (i >= 2) {
            MotoMnsLog.logi("ContactDiscoveryActivity", "hasAlreadyShownContactsDiscoveryDialog: Already displayed 2 times. Show notification instead");
            return true;
        }
        if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) == 1) {
            z = true;
        }
        MotoMnsLog.logi("ContactDiscoveryActivity", "hasAlreadyShownContactsDiscoveryDialog: setupWizardFinished: " + z);
        return !z;
    }

    private void incrementContactsDiscoveryDialogDisplayCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("contact_discovery_settings", 0);
        int i = sharedPreferences.getInt("contact_discovery_display_count", 0) + 1;
        sharedPreferences.edit().putInt("contact_discovery_display_count", i).apply();
        MotoMnsLog.logd("ContactDiscoveryActivity", "incrementContactsDiscoveryDialogDisplayCount: display count is now " + i);
    }
}
