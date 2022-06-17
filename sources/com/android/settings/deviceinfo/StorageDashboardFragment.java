package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.SearchIndexableResource;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.storage.AutomaticStorageManagementSwitchPreferenceController;
import com.android.settings.deviceinfo.storage.DiskInitFragment;
import com.android.settings.deviceinfo.storage.SecondaryUserController;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.StorageEntry;
import com.android.settings.deviceinfo.storage.StorageItemPreferenceController;
import com.android.settings.deviceinfo.storage.StorageSelectionPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUsageProgressBarPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUtils;
import com.android.settings.deviceinfo.storage.UserIconLoader;
import com.android.settings.deviceinfo.storage.VolumeSizesLoader;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StorageDashboardFragment extends DashboardFragment implements LoaderManager.LoaderCallbacks<SparseArray<StorageAsyncLoader.StorageResult>>, Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C1994R$xml.storage_dashboard_fragment;
            return Arrays.asList(new SearchIndexableResource[]{searchIndexableResource});
        }

        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new StorageItemPreferenceController(context, (Fragment) null, (VolumeInfo) null, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)), false));
            arrayList.addAll(SecondaryUserController.getSecondaryUserControllers(context, (UserManager) context.getSystemService(UserManager.class), false));
            return arrayList;
        }
    };
    private SparseArray<StorageAsyncLoader.StorageResult> mAppsResult;
    private Preference mFreeUpSpacePreference;
    private boolean mIsWorkProfile;
    private VolumeOptionMenuController mOptionMenuController;
    private StorageItemPreferenceController mPreferenceController;
    /* access modifiers changed from: private */
    public List<AbstractPreferenceController> mSecondaryUsers;
    /* access modifiers changed from: private */
    public StorageEntry mSelectedStorageEntry;
    /* access modifiers changed from: private */
    public final List<StorageEntry> mStorageEntries = new ArrayList();
    private final StorageEventListener mStorageEventListener = new StorageEventListener() {
        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (StorageUtils.isStorageSettingsInterestedVolume(volumeInfo)) {
                StorageEntry storageEntry = new StorageEntry(StorageDashboardFragment.this.getContext(), volumeInfo);
                int state = volumeInfo.getState();
                if (state != 0) {
                    if (!(state == 2 || state == 3)) {
                        if (state != 5) {
                            if (state != 6) {
                                if (!(state == 7 || state == 8)) {
                                    return;
                                }
                            }
                        }
                    }
                    StorageDashboardFragment.this.mStorageEntries.removeIf(new StorageDashboardFragment$1$$ExternalSyntheticLambda1(storageEntry));
                    StorageDashboardFragment.this.mStorageEntries.add(storageEntry);
                    if (storageEntry.equals(StorageDashboardFragment.this.mSelectedStorageEntry)) {
                        StorageEntry unused = StorageDashboardFragment.this.mSelectedStorageEntry = storageEntry;
                    }
                    StorageDashboardFragment.this.refreshUi();
                    return;
                }
                if (StorageDashboardFragment.this.mStorageEntries.remove(storageEntry)) {
                    if (storageEntry.equals(StorageDashboardFragment.this.mSelectedStorageEntry)) {
                        StorageDashboardFragment storageDashboardFragment = StorageDashboardFragment.this;
                        StorageEntry unused2 = storageDashboardFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(storageDashboardFragment.getContext());
                    }
                    StorageDashboardFragment.this.refreshUi();
                }
            }
        }

        public void onVolumeRecordChanged(VolumeRecord volumeRecord) {
            if (StorageUtils.isVolumeRecordMissed(StorageDashboardFragment.this.mStorageManager, volumeRecord)) {
                StorageEntry storageEntry = new StorageEntry(volumeRecord);
                if (!StorageDashboardFragment.this.mStorageEntries.contains(storageEntry)) {
                    StorageDashboardFragment.this.mStorageEntries.add(storageEntry);
                    StorageDashboardFragment.this.refreshUi();
                    return;
                }
                return;
            }
            VolumeInfo findVolumeByUuid = StorageDashboardFragment.this.mStorageManager.findVolumeByUuid(volumeRecord.getFsUuid());
            if (findVolumeByUuid != null && StorageDashboardFragment.this.mStorageEntries.removeIf(new StorageDashboardFragment$1$$ExternalSyntheticLambda0(volumeRecord))) {
                StorageDashboardFragment.this.mStorageEntries.add(new StorageEntry(StorageDashboardFragment.this.getContext(), findVolumeByUuid));
                StorageDashboardFragment.this.refreshUi();
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onVolumeRecordChanged$1(VolumeRecord volumeRecord, StorageEntry storageEntry) {
            return storageEntry.isVolumeInfo() && TextUtils.equals(storageEntry.getFsUuid(), volumeRecord.getFsUuid());
        }

        public void onVolumeForgotten(String str) {
            StorageEntry storageEntry = new StorageEntry(new VolumeRecord(0, str));
            if (StorageDashboardFragment.this.mStorageEntries.remove(storageEntry)) {
                if (StorageDashboardFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    StorageDashboardFragment storageDashboardFragment = StorageDashboardFragment.this;
                    StorageEntry unused = storageDashboardFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(storageDashboardFragment.getContext());
                }
                StorageDashboardFragment.this.refreshUi();
            }
        }

        public void onDiskScanned(DiskInfo diskInfo, int i) {
            if (StorageUtils.isDiskUnsupported(diskInfo)) {
                StorageEntry storageEntry = new StorageEntry(diskInfo);
                if (!StorageDashboardFragment.this.mStorageEntries.contains(storageEntry)) {
                    StorageDashboardFragment.this.mStorageEntries.add(storageEntry);
                    StorageDashboardFragment.this.refreshUi();
                }
            }
        }

        public void onDiskDestroyed(DiskInfo diskInfo) {
            StorageEntry storageEntry = new StorageEntry(diskInfo);
            if (StorageDashboardFragment.this.mStorageEntries.remove(storageEntry)) {
                if (StorageDashboardFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    StorageDashboardFragment storageDashboardFragment = StorageDashboardFragment.this;
                    StorageEntry unused = storageDashboardFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(storageDashboardFragment.getContext());
                }
                StorageDashboardFragment.this.refreshUi();
            }
        }
    };
    /* access modifiers changed from: private */
    public PrivateStorageInfo mStorageInfo;
    /* access modifiers changed from: private */
    public StorageManager mStorageManager;
    private StorageSelectionPreferenceController mStorageSelectionController;
    private StorageUsageProgressBarPreferenceController mStorageUsageProgressBarController;
    private int mUserId;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "StorageDashboardFrag";
    }

    public int getMetricsCategory() {
        return 745;
    }

    public void onLoaderReset(Loader<SparseArray<StorageAsyncLoader.StorageResult>> loader) {
    }

    /* access modifiers changed from: private */
    public void refreshUi() {
        this.mStorageSelectionController.setStorageEntries(this.mStorageEntries);
        this.mStorageSelectionController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        this.mStorageUsageProgressBarController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        this.mOptionMenuController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        getActivity().invalidateOptionsMenu();
        setSecondaryUsersVisible(false);
        if (!this.mSelectedStorageEntry.isMounted()) {
            this.mPreferenceController.setVolume((VolumeInfo) null);
        } else if (this.mSelectedStorageEntry.isPrivate()) {
            this.mStorageInfo = null;
            this.mAppsResult = null;
            maybeSetLoading(isQuotaSupported());
            this.mPreferenceController.setVolume((VolumeInfo) null);
            LoaderManager loaderManager = getLoaderManager();
            Bundle bundle = Bundle.EMPTY;
            loaderManager.restartLoader(0, bundle, this);
            getLoaderManager().restartLoader(2, bundle, new VolumeSizeCallbacks());
            getLoaderManager().restartLoader(1, bundle, new IconLoaderCallbacks());
        } else {
            this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
        }
    }

    public void onCreate(Bundle bundle) {
        StorageEntry storageEntry;
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        if (bundle == null) {
            VolumeInfo maybeInitializeVolume = Utils.maybeInitializeVolume(storageManager, getArguments());
            if (maybeInitializeVolume == null) {
                storageEntry = StorageEntry.getDefaultInternalStorageEntry(getContext());
            } else {
                storageEntry = new StorageEntry(getContext(), maybeInitializeVolume);
            }
            this.mSelectedStorageEntry = storageEntry;
        } else {
            this.mSelectedStorageEntry = (StorageEntry) bundle.getParcelable("selected_storage_entry_key");
        }
        initializePreference();
        initializeOptionsMenu(activity);
    }

    private void initializePreference() {
        Preference findPreference = getPreferenceScreen().findPreference("free_up_space");
        this.mFreeUpSpacePreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
    }

    public void onAttach(Context context) {
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mIsWorkProfile = false;
        this.mUserId = UserHandle.myUserId();
        super.onAttach(context);
        ((AutomaticStorageManagementSwitchPreferenceController) use(AutomaticStorageManagementSwitchPreferenceController.class)).setFragmentManager(getFragmentManager());
        StorageSelectionPreferenceController storageSelectionPreferenceController = (StorageSelectionPreferenceController) use(StorageSelectionPreferenceController.class);
        this.mStorageSelectionController = storageSelectionPreferenceController;
        storageSelectionPreferenceController.setOnItemSelectedListener(new StorageDashboardFragment$$ExternalSyntheticLambda0(this));
        this.mStorageUsageProgressBarController = (StorageUsageProgressBarPreferenceController) use(StorageUsageProgressBarPreferenceController.class);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(StorageEntry storageEntry) {
        this.mSelectedStorageEntry = storageEntry;
        refreshUi();
        if (storageEntry.isDiskInfoUnsupported() || storageEntry.isUnmountable()) {
            DiskInitFragment.show(this, C1992R$string.storage_dialog_unmountable, storageEntry.getDiskId());
        } else if (storageEntry.isVolumeRecordMissed()) {
            StorageUtils.launchForgetMissingVolumeRecordFragment(getContext(), storageEntry);
        }
    }

    /* access modifiers changed from: package-private */
    public void initializeOptionsMenu(Activity activity) {
        this.mOptionMenuController = new VolumeOptionMenuController(activity, this, this.mSelectedStorageEntry);
        getSettingsLifecycle().addObserver(this.mOptionMenuController);
        setHasOptionsMenu(true);
        activity.invalidateOptionsMenu();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        EntityHeaderController.newInstance(getActivity(), this, (View) null).setRecyclerView(getListView(), getSettingsLifecycle());
    }

    public void onResume() {
        super.onResume();
        this.mStorageEntries.clear();
        this.mStorageEntries.addAll(StorageUtils.getAllStorageEntries(getContext(), this.mStorageManager));
        refreshUi();
        this.mStorageManager.registerListener(this.mStorageEventListener);
    }

    public void onPause() {
        super.onPause();
        this.mStorageManager.unregisterListener(this.mStorageEventListener);
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable("selected_storage_entry_key", this.mSelectedStorageEntry);
        super.onSaveInstanceState(bundle);
    }

    public int getHelpResource() {
        return C1992R$string.help_url_storage_dashboard;
    }

    /* access modifiers changed from: private */
    public void onReceivedSizes() {
        if (this.mStorageInfo != null && this.mAppsResult != null) {
            if (getView().findViewById(C1985R$id.loading_container).getVisibility() == 0) {
                setLoading(false, true);
            }
            PrivateStorageInfo privateStorageInfo = this.mStorageInfo;
            long j = privateStorageInfo.totalBytes - privateStorageInfo.freeBytes;
            this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
            this.mPreferenceController.setUsedSize(j);
            this.mPreferenceController.setTotalSize(this.mStorageInfo.totalBytes);
            int size = this.mSecondaryUsers.size();
            for (int i = 0; i < size; i++) {
                AbstractPreferenceController abstractPreferenceController = this.mSecondaryUsers.get(i);
                if (abstractPreferenceController instanceof SecondaryUserController) {
                    ((SecondaryUserController) abstractPreferenceController).setTotalSize(this.mStorageInfo.totalBytes);
                }
            }
            this.mPreferenceController.onLoadFinished(this.mAppsResult, this.mUserId);
            updateSecondaryUserControllers(this.mSecondaryUsers, this.mAppsResult);
            setSecondaryUsersVisible(true);
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.storage_dashboard_fragment;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        StorageItemPreferenceController storageItemPreferenceController = new StorageItemPreferenceController(context, this, (VolumeInfo) null, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)), this.mIsWorkProfile);
        this.mPreferenceController = storageItemPreferenceController;
        arrayList.add(storageItemPreferenceController);
        List<AbstractPreferenceController> secondaryUserControllers = SecondaryUserController.getSecondaryUserControllers(context, this.mUserManager, this.mIsWorkProfile);
        this.mSecondaryUsers = secondaryUserControllers;
        arrayList.addAll(secondaryUserControllers);
        return arrayList;
    }

    private void updateSecondaryUserControllers(List<AbstractPreferenceController> list, SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AbstractPreferenceController abstractPreferenceController = list.get(i);
            if (abstractPreferenceController instanceof StorageAsyncLoader.ResultHandler) {
                ((StorageAsyncLoader.ResultHandler) abstractPreferenceController).handleResult(sparseArray);
            }
        }
    }

    public Loader<SparseArray<StorageAsyncLoader.StorageResult>> onCreateLoader(int i, Bundle bundle) {
        Context context = getContext();
        return new StorageAsyncLoader(context, this.mUserManager, this.mSelectedStorageEntry.getFsUuid(), new StorageStatsSource(context), context.getPackageManager());
    }

    public void onLoadFinished(Loader<SparseArray<StorageAsyncLoader.StorageResult>> loader, SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
        onReceivedSizes();
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference != this.mFreeUpSpacePreference) {
            return false;
        }
        Context context = getContext();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        metricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
        metricsFeatureProvider.action(context, 840, (Pair<Integer, Object>[]) new Pair[0]);
        context.startActivityAsUser(new Intent("android.os.storage.action.MANAGE_STORAGE"), new UserHandle(this.mUserId));
        return true;
    }

    public PrivateStorageInfo getPrivateStorageInfo() {
        return this.mStorageInfo;
    }

    public void setPrivateStorageInfo(PrivateStorageInfo privateStorageInfo) {
        this.mStorageInfo = privateStorageInfo;
    }

    public SparseArray<StorageAsyncLoader.StorageResult> getStorageResult() {
        return this.mAppsResult;
    }

    public void setStorageResult(SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
    }

    public void maybeSetLoading(boolean z) {
        if ((z && (this.mStorageInfo == null || this.mAppsResult == null)) || (!z && this.mStorageInfo == null)) {
            setLoading(true, false);
        }
    }

    private boolean isQuotaSupported() {
        return this.mSelectedStorageEntry.isMounted() && ((StorageStatsManager) getActivity().getSystemService(StorageStatsManager.class)).isQuotaSupported(this.mSelectedStorageEntry.getFsUuid());
    }

    private void setSecondaryUsersVisible(boolean z) {
        Optional findAny = this.mSecondaryUsers.stream().filter(StorageDashboardFragment$$ExternalSyntheticLambda2.INSTANCE).map(StorageDashboardFragment$$ExternalSyntheticLambda1.INSTANCE).findAny();
        if (findAny.isPresent()) {
            ((SecondaryUserController) findAny.get()).setPreferenceGroupVisible(z);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setSecondaryUsersVisible$1(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof SecondaryUserController;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ SecondaryUserController lambda$setSecondaryUsersVisible$2(AbstractPreferenceController abstractPreferenceController) {
        return (SecondaryUserController) abstractPreferenceController;
    }

    public final class IconLoaderCallbacks implements LoaderManager.LoaderCallbacks<SparseArray<Drawable>> {
        public void onLoaderReset(Loader<SparseArray<Drawable>> loader) {
        }

        public IconLoaderCallbacks() {
        }

        public Loader<SparseArray<Drawable>> onCreateLoader(int i, Bundle bundle) {
            return new UserIconLoader(StorageDashboardFragment.this.getContext(), new C0877x7ad960f6(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ SparseArray lambda$onCreateLoader$0() {
            return UserIconLoader.loadUserIconsWithContext(StorageDashboardFragment.this.getContext());
        }

        public void onLoadFinished(Loader<SparseArray<Drawable>> loader, SparseArray<Drawable> sparseArray) {
            StorageDashboardFragment.this.mSecondaryUsers.stream().filter(C0879x7ad960f8.INSTANCE).forEach(new C0878x7ad960f7(sparseArray));
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onLoadFinished$1(AbstractPreferenceController abstractPreferenceController) {
            return abstractPreferenceController instanceof UserIconLoader.UserIconHandler;
        }
    }

    public final class VolumeSizeCallbacks implements LoaderManager.LoaderCallbacks<PrivateStorageInfo> {
        public void onLoaderReset(Loader<PrivateStorageInfo> loader) {
        }

        public VolumeSizeCallbacks() {
        }

        public Loader<PrivateStorageInfo> onCreateLoader(int i, Bundle bundle) {
            Context context = StorageDashboardFragment.this.getContext();
            return new VolumeSizesLoader(context, new StorageManagerVolumeProvider(StorageDashboardFragment.this.mStorageManager), (StorageStatsManager) context.getSystemService(StorageStatsManager.class), StorageDashboardFragment.this.mSelectedStorageEntry.getVolumeInfo());
        }

        public void onLoadFinished(Loader<PrivateStorageInfo> loader, PrivateStorageInfo privateStorageInfo) {
            if (privateStorageInfo == null) {
                StorageDashboardFragment.this.getActivity().finish();
                return;
            }
            PrivateStorageInfo unused = StorageDashboardFragment.this.mStorageInfo = privateStorageInfo;
            StorageDashboardFragment.this.onReceivedSizes();
        }
    }
}
