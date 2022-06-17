package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.telephony.SubscriptionManager;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.android.settings.network.telephony.EnabledNetworkModePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.ArrayList;

public class ExtendedEnabledNetworkModePreferenceController extends EnabledNetworkModePreferenceController {
    protected FragmentManager mFragmentManager;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ExtendedEnabledNetworkModePreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isFtr5084Enabled(this.mContext, i)) {
            return 2;
        }
        return super.getAvailabilityStatus(i);
    }

    public void init(Lifecycle lifecycle, FragmentManager fragmentManager, int i) {
        super.init(lifecycle, i);
        this.mFragmentManager = fragmentManager;
        this.mBuilder = new ExtendedPreferenceEntriesBuilder(this.mContext, i);
    }

    protected class ExtendedPreferenceEntriesBuilder extends EnabledNetworkModePreferenceController.PreferenceEntriesBuilder {
        protected ExtendedPreferenceEntriesBuilder(Context context, int i) {
            super(context, i);
        }

        /* access modifiers changed from: protected */
        public void setPreferenceEntries() {
            if (showNrList() && MotoTelephonyFeature.isCarrierNeedsCustom5gPnt(this.mContext, this.mSubId)) {
                addCustom5gEntries();
            } else if (showNrList() || !MotoTelephonyFeature.isCarrierNeedsCustomNon5gPnt(this.mContext, this.mSubId)) {
                super.setPreferenceEntries();
            } else {
                addCustomNon5gEntries();
            }
            if (!MotoTelephonyFeature.isFtr4808Enabled(this.mContext, this.mSubId)) {
                return;
            }
            if (MotoTelephonyFeature.isCarrierNeedsCustom3gPnt(this.mContext, this.mSubId)) {
                addCustom3gEntries();
                return;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < this.mEntries.size(); i++) {
                String str = this.mEntries.get(i);
                Integer num = this.mEntriesValue.get(i);
                if (MotoMobileNetworkUtils.is2G(num.intValue()) || MotoMobileNetworkUtils.is3G(num.intValue())) {
                    arrayList.add(str);
                    arrayList2.add(num);
                }
            }
            this.mEntries.clear();
            this.mEntriesValue.clear();
            this.mEntries.addAll(arrayList);
            this.mEntriesValue.addAll(arrayList2);
            if (this.mEntries.size() == 0) {
                int i2 = this.mTelephonyManager.getPhoneType() == 2 ? 4 : 0;
                this.mEntries.add(this.mContext.getString(C1992R$string.network_3G));
                this.mEntriesValue.add(Integer.valueOf(i2));
            }
            String string = this.mContext.getString(C1992R$string.network_recommended);
            if (!this.mEntries.stream().anyMatch(new C1928xf0c7cfb2(string))) {
                for (int i3 = 0; i3 < this.mEntriesValue.size(); i3++) {
                    String str2 = this.mEntries.get(i3);
                    if (MotoMobileNetworkUtils.is3G(this.mEntriesValue.get(i3).intValue())) {
                        this.mEntries.remove(i3);
                        this.mEntries.add(i3, str2 + string);
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void addCustom5gEntries() {
            clearAllEntries();
            this.mIs5gEntryDisplayed = true;
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
            for (String add : resourcesForSubId.getStringArray(C1978R$array.carrier_custom_pnt_5g)) {
                this.mEntries.add(add);
            }
            for (int valueOf : resourcesForSubId.getIntArray(C1978R$array.carrier_custom_pnt_5g_values)) {
                this.mEntriesValue.add(Integer.valueOf(valueOf));
            }
        }

        /* access modifiers changed from: protected */
        public void addCustomNon5gEntries() {
            clearAllEntries();
            this.mIs5gEntryDisplayed = false;
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
            for (String add : resourcesForSubId.getStringArray(C1978R$array.carrier_custom_pnt_non_5g)) {
                this.mEntries.add(add);
            }
            for (int valueOf : resourcesForSubId.getIntArray(C1978R$array.carrier_custom_pnt_non_5g_values)) {
                this.mEntriesValue.add(Integer.valueOf(valueOf));
            }
        }

        /* access modifiers changed from: protected */
        public void addCustom3gEntries() {
            MotoMnsLog.logd("EnabledNetworkMode", "addCustom3gEntries");
            clearAllEntries();
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
            for (String add : resourcesForSubId.getStringArray(C1978R$array.carrier_custom_pnt_3g)) {
                this.mEntries.add(add);
            }
            for (int valueOf : resourcesForSubId.getIntArray(C1978R$array.carrier_custom_pnt_3g_values)) {
                this.mEntriesValue.add(Integer.valueOf(valueOf));
            }
        }

        /* access modifiers changed from: protected */
        public boolean showNrList() {
            return MotoMobileNetworkUtils.simcardSupports5gOption(this.mAllowed5gNetworkType, this.mSupported5gRadioAccessFamily, this.mContext, this.mSubId);
        }
    }
}
