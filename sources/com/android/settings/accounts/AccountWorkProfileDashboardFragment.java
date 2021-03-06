package com.android.settings.accounts;

import android.content.Context;
import androidx.lifecycle.LifecycleObserver;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.autofill.PasswordsPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.users.AutoSyncDataPreferenceController;
import com.android.settings.users.AutoSyncWorkDataPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AccountWorkProfileDashboardFragment extends DashboardFragment {
    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "AccountWorkProfileFrag";
    }

    public int getMetricsCategory() {
        return 1807;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.accounts_work_dashboard_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_user_and_account_dashboard;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(PasswordsPreferenceController.class));
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        AccountDashboardFragment.buildAutofillPreferenceControllers(context, arrayList);
        buildAccountPreferenceControllers(context, this, getIntent().getStringArrayExtra("authorities"), arrayList);
        return arrayList;
    }

    private static void buildAccountPreferenceControllers(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, List<AbstractPreferenceController> list) {
        AccountPreferenceController accountPreferenceController = new AccountPreferenceController(context, settingsPreferenceFragment, strArr, 2);
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getSettingsLifecycle().addObserver(accountPreferenceController);
        }
        list.add(accountPreferenceController);
        list.add(new AutoSyncDataPreferenceController(context, settingsPreferenceFragment));
        list.add(new AutoSyncWorkDataPreferenceController(context, settingsPreferenceFragment));
    }
}
