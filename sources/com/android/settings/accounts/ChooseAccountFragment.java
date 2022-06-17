package com.android.settings.accounts;

import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import com.android.settings.C1994R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;

public class ChooseAccountFragment extends DashboardFragment {
    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ChooseAccountFragment";
    }

    public int getMetricsCategory() {
        return 10;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((ChooseAccountPreferenceController) use(ChooseAccountPreferenceController.class)).initialize(getIntent().getStringArrayExtra("authorities"), getIntent().getStringArrayExtra("account_types"), Utils.getSecureTargetUser(getActivity().getActivityToken(), UserManager.get(getContext()), (Bundle) null, getIntent().getExtras()), getActivity());
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.add_account_settings;
    }
}
