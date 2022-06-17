package com.android.settings.datausage;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.applications.ApplicationsState;

public class UnrestrictedDataAccess extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.unrestricted_data_access_settings);
    private ApplicationsState.AppFilter mFilter;
    private boolean mShowSystem;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "UnrestrictedDataAccess";
    }

    public int getMetricsCategory() {
        return 349;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mShowSystem = bundle != null && bundle.getBoolean("show_system");
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setParentFragment(this);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 43, 0, this.mShowSystem ? C1992R$string.menu_hide_system : C1992R$string.menu_show_system);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        ApplicationsState.AppFilter appFilter;
        Class cls = UnrestrictedDataAccessPreferenceController.class;
        if (menuItem.getItemId() == 43) {
            boolean z = !this.mShowSystem;
            this.mShowSystem = z;
            menuItem.setTitle(z ? C1992R$string.menu_hide_system : C1992R$string.menu_show_system);
            if (this.mShowSystem) {
                appFilter = ApplicationsState.FILTER_ALL_ENABLED;
            } else {
                appFilter = ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
            }
            this.mFilter = appFilter;
            ((UnrestrictedDataAccessPreferenceController) use(cls)).setFilter(this.mFilter);
            ((UnrestrictedDataAccessPreferenceController) use(cls)).rebuild();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_system", this.mShowSystem);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void onAttach(Context context) {
        ApplicationsState.AppFilter appFilter;
        Class cls = UnrestrictedDataAccessPreferenceController.class;
        super.onAttach(context);
        if (this.mShowSystem) {
            appFilter = ApplicationsState.FILTER_ALL_ENABLED;
        } else {
            appFilter = ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
        }
        this.mFilter = appFilter;
        ((UnrestrictedDataAccessPreferenceController) use(cls)).setSession(getSettingsLifecycle());
        ((UnrestrictedDataAccessPreferenceController) use(cls)).setFilter(this.mFilter);
    }

    public int getHelpResource() {
        return C1992R$string.help_url_unrestricted_data_access;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.unrestricted_data_access_settings;
    }
}
