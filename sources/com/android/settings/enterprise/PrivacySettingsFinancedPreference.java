package com.android.settings.enterprise;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.C1994R$xml;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivacySettingsFinancedPreference implements PrivacySettingsPreference {
    private final Context mContext;

    public PrivacySettingsFinancedPreference(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public int getPreferenceScreenResId() {
        return C1994R$xml.financed_privacy_settings;
    }

    public List<SearchIndexableResource> getXmlResourcesToIndex() {
        SearchIndexableResource searchIndexableResource = new SearchIndexableResource(this.mContext);
        searchIndexableResource.xmlResId = getPreferenceScreenResId();
        return Collections.singletonList(searchIndexableResource);
    }

    public List<AbstractPreferenceController> createPreferenceControllers(boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new NetworkLogsPreferenceController(this.mContext));
        arrayList.add(new BugReportsPreferenceController(this.mContext));
        arrayList.add(new SecurityLogsPreferenceController(this.mContext));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new EnterpriseInstalledPackagesPreferenceController(this.mContext, z));
        arrayList.addAll(arrayList2);
        arrayList.add(new PreferenceCategoryController(this.mContext, "exposure_changes_category").setChildren(arrayList2));
        arrayList.add(new FailedPasswordWipeCurrentUserPreferenceController(this.mContext));
        return arrayList;
    }
}
