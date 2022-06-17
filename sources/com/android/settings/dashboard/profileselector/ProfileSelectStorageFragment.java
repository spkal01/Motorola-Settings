package com.android.settings.dashboard.profileselector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.Utils;
import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settings.deviceinfo.VolumeOptionMenuController;
import com.android.settings.deviceinfo.storage.AutomaticStorageManagementSwitchPreferenceController;
import com.android.settings.deviceinfo.storage.DiskInitFragment;
import com.android.settings.deviceinfo.storage.StorageEntry;
import com.android.settings.deviceinfo.storage.StorageSelectionPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUsageProgressBarPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUtils;
import java.util.ArrayList;
import java.util.List;

public class ProfileSelectStorageFragment extends ProfileSelectFragment {
    private Fragment[] mFragments;
    private VolumeOptionMenuController mOptionMenuController;
    /* access modifiers changed from: private */
    public StorageEntry mSelectedStorageEntry;
    /* access modifiers changed from: private */
    public final List<StorageEntry> mStorageEntries = new ArrayList();
    private final StorageEventListener mStorageEventListener = new StorageEventListener() {
        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (StorageUtils.isStorageSettingsInterestedVolume(volumeInfo)) {
                StorageEntry storageEntry = new StorageEntry(ProfileSelectStorageFragment.this.getContext(), volumeInfo);
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
                    ProfileSelectStorageFragment.this.mStorageEntries.removeIf(new ProfileSelectStorageFragment$1$$ExternalSyntheticLambda1(storageEntry));
                    ProfileSelectStorageFragment.this.mStorageEntries.add(storageEntry);
                    if (storageEntry.equals(ProfileSelectStorageFragment.this.mSelectedStorageEntry)) {
                        StorageEntry unused = ProfileSelectStorageFragment.this.mSelectedStorageEntry = storageEntry;
                    }
                    ProfileSelectStorageFragment.this.refreshUi();
                    return;
                }
                if (ProfileSelectStorageFragment.this.mStorageEntries.remove(storageEntry)) {
                    if (storageEntry.equals(ProfileSelectStorageFragment.this.mSelectedStorageEntry)) {
                        ProfileSelectStorageFragment profileSelectStorageFragment = ProfileSelectStorageFragment.this;
                        StorageEntry unused2 = profileSelectStorageFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(profileSelectStorageFragment.getContext());
                    }
                    ProfileSelectStorageFragment.this.refreshUi();
                }
            }
        }

        public void onVolumeRecordChanged(VolumeRecord volumeRecord) {
            if (StorageUtils.isVolumeRecordMissed(ProfileSelectStorageFragment.this.mStorageManager, volumeRecord)) {
                StorageEntry storageEntry = new StorageEntry(volumeRecord);
                if (!ProfileSelectStorageFragment.this.mStorageEntries.contains(storageEntry)) {
                    ProfileSelectStorageFragment.this.mStorageEntries.add(storageEntry);
                    ProfileSelectStorageFragment.this.refreshUi();
                    return;
                }
                return;
            }
            VolumeInfo findVolumeByUuid = ProfileSelectStorageFragment.this.mStorageManager.findVolumeByUuid(volumeRecord.getFsUuid());
            if (findVolumeByUuid != null && ProfileSelectStorageFragment.this.mStorageEntries.removeIf(new ProfileSelectStorageFragment$1$$ExternalSyntheticLambda0(volumeRecord))) {
                ProfileSelectStorageFragment.this.mStorageEntries.add(new StorageEntry(ProfileSelectStorageFragment.this.getContext(), findVolumeByUuid));
                ProfileSelectStorageFragment.this.refreshUi();
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onVolumeRecordChanged$1(VolumeRecord volumeRecord, StorageEntry storageEntry) {
            return storageEntry.isVolumeInfo() && TextUtils.equals(storageEntry.getFsUuid(), volumeRecord.getFsUuid());
        }

        public void onVolumeForgotten(String str) {
            StorageEntry storageEntry = new StorageEntry(new VolumeRecord(0, str));
            if (ProfileSelectStorageFragment.this.mStorageEntries.remove(storageEntry)) {
                if (ProfileSelectStorageFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    ProfileSelectStorageFragment profileSelectStorageFragment = ProfileSelectStorageFragment.this;
                    StorageEntry unused = profileSelectStorageFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(profileSelectStorageFragment.getContext());
                }
                ProfileSelectStorageFragment.this.refreshUi();
            }
        }

        public void onDiskScanned(DiskInfo diskInfo, int i) {
            if (StorageUtils.isDiskUnsupported(diskInfo)) {
                StorageEntry storageEntry = new StorageEntry(diskInfo);
                if (!ProfileSelectStorageFragment.this.mStorageEntries.contains(storageEntry)) {
                    ProfileSelectStorageFragment.this.mStorageEntries.add(storageEntry);
                    ProfileSelectStorageFragment.this.refreshUi();
                }
            }
        }

        public void onDiskDestroyed(DiskInfo diskInfo) {
            StorageEntry storageEntry = new StorageEntry(diskInfo);
            if (ProfileSelectStorageFragment.this.mStorageEntries.remove(storageEntry)) {
                if (ProfileSelectStorageFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    ProfileSelectStorageFragment profileSelectStorageFragment = ProfileSelectStorageFragment.this;
                    StorageEntry unused = profileSelectStorageFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(profileSelectStorageFragment.getContext());
                }
                ProfileSelectStorageFragment.this.refreshUi();
            }
        }
    };
    /* access modifiers changed from: private */
    public StorageManager mStorageManager;
    private StorageSelectionPreferenceController mStorageSelectionController;
    private StorageUsageProgressBarPreferenceController mStorageUsageProgressBarController;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ProfileSelStorageFrag";
    }

    public int getMetricsCategory() {
        return 745;
    }

    public Fragment[] getFragments() {
        Fragment[] fragmentArr = this.mFragments;
        if (fragmentArr != null) {
            return fragmentArr;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 2);
        StorageCategoryFragment storageCategoryFragment = new StorageCategoryFragment();
        storageCategoryFragment.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 1);
        StorageCategoryFragment storageCategoryFragment2 = new StorageCategoryFragment();
        storageCategoryFragment2.setArguments(bundle2);
        Fragment[] fragmentArr2 = {storageCategoryFragment2, storageCategoryFragment};
        this.mFragments = fragmentArr2;
        return fragmentArr2;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.storage_dashboard_header_fragment;
    }

    /* access modifiers changed from: private */
    public void refreshUi() {
        this.mStorageSelectionController.setStorageEntries(this.mStorageEntries);
        this.mStorageSelectionController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        this.mStorageUsageProgressBarController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        Fragment[] fragments = getFragments();
        int length = fragments.length;
        int i = 0;
        while (i < length) {
            Fragment fragment = fragments[i];
            if (fragment instanceof StorageCategoryFragment) {
                ((StorageCategoryFragment) fragment).refreshUi(this.mSelectedStorageEntry);
                i++;
            } else {
                throw new IllegalStateException("Wrong fragment type to refreshUi");
            }
        }
        this.mOptionMenuController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        getActivity().invalidateOptionsMenu();
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
        initializeOptionsMenu(activity);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((AutomaticStorageManagementSwitchPreferenceController) use(AutomaticStorageManagementSwitchPreferenceController.class)).setFragmentManager(getFragmentManager());
        StorageSelectionPreferenceController storageSelectionPreferenceController = (StorageSelectionPreferenceController) use(StorageSelectionPreferenceController.class);
        this.mStorageSelectionController = storageSelectionPreferenceController;
        storageSelectionPreferenceController.setOnItemSelectedListener(new ProfileSelectStorageFragment$$ExternalSyntheticLambda0(this));
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
}
