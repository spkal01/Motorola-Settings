package com.android.settings.location;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.location.LocationEnabler;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationSettings extends DashboardFragment implements LocationEnabler.LocationModeChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.location_settings);
    private LocationEnabler mLocationEnabler;
    private LocationSwitchBarController mSwitchBarController;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "LocationSettings";
    }

    public int getMetricsCategory() {
        return 63;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        switchBar.setTitle(getContext().getString(C1992R$string.location_settings_primary_switch_title));
        switchBar.show();
        this.mSwitchBarController = new LocationSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
        this.mLocationEnabler = new LocationEnabler(getContext(), this, getSettingsLifecycle());
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppLocationPermissionPreferenceController) use(AppLocationPermissionPreferenceController.class)).init(this);
        ((RecentLocationAccessPreferenceController) use(RecentLocationAccessPreferenceController.class)).init(this);
        ((RecentLocationAccessSeeAllButtonPreferenceController) use(RecentLocationAccessSeeAllButtonPreferenceController.class)).init(this);
        ((LocationForWorkPreferenceController) use(LocationForWorkPreferenceController.class)).init(this);
        ((LocationSettingsFooterPreferenceController) use(LocationSettingsFooterPreferenceController.class)).init(this);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.location_settings;
    }

    public void onLocationModeChanged(int i, boolean z) {
        if (this.mLocationEnabler.isEnabled(i)) {
            scrollToPreference("recent_location_access");
        }
    }

    static void addPreferencesSorted(List<Preference> list, PreferenceGroup preferenceGroup) {
        Collections.sort(list, Comparator.comparing(LocationSettings$$ExternalSyntheticLambda0.INSTANCE));
        for (Preference addPreference : list) {
            preferenceGroup.addPreference(addPreference);
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_url_location_access;
    }
}
