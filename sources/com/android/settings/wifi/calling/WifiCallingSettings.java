package com.android.settings.wifi.calling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.network.ActiveSubscriptionsListener;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.support.actionbar.HelpMenuController;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.widget.RtlCompatibleViewPager;
import com.android.settings.widget.SlidingTabLayout;
import com.android.settingslib.core.lifecycle.ObservableFragment;
import com.motorola.settings.wifi.calling.WifiCallingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WifiCallingSettings extends InstrumentedFragment implements HelpResourceProvider {
    private static final int[] EMPTY_SUB_ID_LIST = new int[0];
    private int mConstructionSubId;
    private WifiCallingViewPagerAdapter mPagerAdapter;
    /* access modifiers changed from: private */
    public List<SubscriptionInfo> mSil;
    private ActiveSubscriptionsListener mSubscriptionChangeListener;
    private SlidingTabLayout mTabLayout;
    private RtlCompatibleViewPager mViewPager;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$containsSubId$1(int i, int i2) {
        return i2 == i;
    }

    public int getMetricsCategory() {
        return R$styleable.Constraint_pathMotionArc;
    }

    private final class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        private InternalViewPagerListener() {
        }

        public void onPageSelected(int i) {
            WifiCallingSettings.this.updateTitleForCurrentSub();
            WifiCallingSettings.this.getActivity().invalidateOptionsMenu();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C1987R$layout.wifi_calling_settings_tabs, viewGroup, false);
        this.mTabLayout = (SlidingTabLayout) inflate.findViewById(C1985R$id.sliding_tabs);
        this.mViewPager = (RtlCompatibleViewPager) inflate.findViewById(C1985R$id.view_pager);
        WifiCallingViewPagerAdapter wifiCallingViewPagerAdapter = new WifiCallingViewPagerAdapter(getChildFragmentManager(), this.mViewPager);
        this.mPagerAdapter = wifiCallingViewPagerAdapter;
        this.mViewPager.setAdapter(wifiCallingViewPagerAdapter);
        this.mViewPager.addOnPageChangeListener(new InternalViewPagerListener());
        maybeSetViewForSubId();
        return inflate;
    }

    private int getConstructionSubId(Bundle bundle) {
        Intent intent = getActivity().getIntent();
        int intExtra = intent != null ? intent.getIntExtra("android.provider.extra.SUB_ID", -1) : -1;
        return (intExtra != -1 || bundle == null) ? intExtra : bundle.getInt("android.provider.extra.SUB_ID", -1);
    }

    private void maybeSetViewForSubId() {
        if (this.mSil != null) {
            int i = this.mConstructionSubId;
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                for (SubscriptionInfo next : this.mSil) {
                    if (i == next.getSubscriptionId()) {
                        this.mViewPager.setCurrentItem(this.mSil.indexOf(next));
                        return;
                    }
                }
            }
        }
    }

    public void onCreate(Bundle bundle) {
        this.mConstructionSubId = getConstructionSubId(bundle);
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        SearchMenuController.init((InstrumentedFragment) this);
        HelpMenuController.init((ObservableFragment) this);
        Log.d("WifiCallingSettings", "SubId=" + this.mConstructionSubId);
        int defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            defaultSubscriptionId = intent.getIntExtra("android.provider.extra.SUB_ID", defaultSubscriptionId);
        }
        if (!SubscriptionManager.isValidSubscriptionId(defaultSubscriptionId) || !WifiCallingUtils.hasCustomWFCSetting(getActivity(), Integer.valueOf(defaultSubscriptionId))) {
            if (this.mConstructionSubId != -1) {
                this.mSubscriptionChangeListener = getSubscriptionChangeListener(getContext());
            }
            this.mSil = updateSubList();
            return;
        }
        getActivity().finish();
    }

    public void onStart() {
        super.onStart();
        List<SubscriptionInfo> list = this.mSil;
        if (list == null || list.size() <= 1) {
            this.mTabLayout.setVisibility(8);
        } else {
            this.mTabLayout.setViewPager(this.mViewPager);
        }
        updateTitleForCurrentSub();
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.start();
        }
        getActivity().invalidateOptionsMenu();
    }

    public void onStop() {
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
        }
        super.onStop();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("android.provider.extra.SUB_ID", this.mConstructionSubId);
    }

    public int getHelpResource() {
        List<SubscriptionInfo> list = this.mSil;
        if (list != null && !list.isEmpty()) {
            if (WifiCallingUtils.hasMotoCustomHelp(getActivity(), this.mSil.get(this.mViewPager.getCurrentItem()).getSubscriptionId())) {
                return C1992R$string.help_uri_carrier_wfc;
            }
        }
        return C1992R$string.help_uri_wifi_calling;
    }

    final class WifiCallingViewPagerAdapter extends FragmentPagerAdapter {
        private final RtlCompatibleViewPager mViewPager;

        public WifiCallingViewPagerAdapter(FragmentManager fragmentManager, RtlCompatibleViewPager rtlCompatibleViewPager) {
            super(fragmentManager);
            this.mViewPager = rtlCompatibleViewPager;
        }

        public CharSequence getPageTitle(int i) {
            return String.valueOf(SubscriptionUtil.getUniqueSubscriptionDisplayName((SubscriptionInfo) WifiCallingSettings.this.mSil.get(i), WifiCallingSettings.this.getContext()));
        }

        public Fragment getItem(int i) {
            int subscriptionId = ((SubscriptionInfo) WifiCallingSettings.this.mSil.get(i)).getSubscriptionId();
            Log.d("WifiCallingSettings", "Adapter getItem " + i + " for subId=" + subscriptionId);
            Bundle bundle = new Bundle();
            bundle.putBoolean("need_search_icon_in_action_bar", false);
            bundle.putInt("subId", subscriptionId);
            WifiCallingSettingsForSub wifiCallingSettingsForSub = new WifiCallingSettingsForSub();
            wifiCallingSettingsForSub.setArguments(bundle);
            return wifiCallingSettingsForSub;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            Log.d("WifiCallingSettings", "Adapter instantiateItem " + i);
            return super.instantiateItem(viewGroup, this.mViewPager.getRtlAwareIndex(i));
        }

        public int getCount() {
            if (WifiCallingSettings.this.mSil == null) {
                Log.d("WifiCallingSettings", "Adapter getCount null mSil ");
                return 0;
            }
            Log.d("WifiCallingSettings", "Adapter getCount " + WifiCallingSettings.this.mSil.size());
            return WifiCallingSettings.this.mSil.size();
        }
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getSelectableSubscriptions(Context context) {
        return SubscriptionUtil.getSelectableSubscriptionInfoList(context);
    }

    private List<SubscriptionInfo> updateSubList() {
        List<SubscriptionInfo> selectableSubscriptions = getSelectableSubscriptions(getContext());
        if (selectableSubscriptions == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo next : selectableSubscriptions) {
            int subscriptionId = next.getSubscriptionId();
            try {
                if (queryImsState(subscriptionId).isWifiCallingProvisioned() && !WifiCallingUtils.hasCustomWFCSetting(getContext(), Integer.valueOf(subscriptionId))) {
                    arrayList.add(next);
                }
            } catch (Exception unused) {
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void updateTitleForCurrentSub() {
        if (CollectionUtils.size(this.mSil) > 1) {
            getActivity().getActionBar().setTitle(SubscriptionManager.getResourcesForSubId(getContext(), this.mSil.get(this.mViewPager.getCurrentItem()).getSubscriptionId()).getString(C1992R$string.wifi_calling_settings_title));
        }
    }

    /* access modifiers changed from: protected */
    public WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(getContext(), i);
    }

    /* access modifiers changed from: protected */
    public ActiveSubscriptionsListener getSubscriptionChangeListener(final Context context) {
        return new ActiveSubscriptionsListener(context.getMainLooper(), context) {
            public void onChanged() {
                WifiCallingSettings.this.onSubscriptionChange(context);
            }
        };
    }

    /* access modifiers changed from: protected */
    public void onSubscriptionChange(Context context) {
        if (this.mSubscriptionChangeListener != null) {
            int[] subscriptionIdList = subscriptionIdList(this.mSil);
            List<SubscriptionInfo> updateSubList = updateSubList();
            int[] subscriptionIdList2 = subscriptionIdList(updateSubList);
            if (subscriptionIdList2.length > 0) {
                if (subscriptionIdList.length == 0) {
                    this.mSil = updateSubList;
                    return;
                } else if (subscriptionIdList.length == subscriptionIdList2.length && (!containsSubId(subscriptionIdList, this.mConstructionSubId) || containsSubId(subscriptionIdList2, this.mConstructionSubId))) {
                    this.mSil = updateSubList;
                    return;
                }
            }
            Log.d("WifiCallingSettings", "Closed subId=" + this.mConstructionSubId + " due to subscription change: " + Arrays.toString(subscriptionIdList) + " -> " + Arrays.toString(subscriptionIdList2));
            ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
            if (activeSubscriptionsListener != null) {
                activeSubscriptionsListener.stop();
                this.mSubscriptionChangeListener = null;
            }
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void finish() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                activity.finish();
            }
        }
    }

    /* access modifiers changed from: protected */
    public int[] subscriptionIdList(List<SubscriptionInfo> list) {
        if (list == null) {
            return EMPTY_SUB_ID_LIST;
        }
        return list.stream().mapToInt(WifiCallingSettings$$ExternalSyntheticLambda1.INSTANCE).toArray();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$subscriptionIdList$0(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return -1;
        }
        return subscriptionInfo.getSubscriptionId();
    }

    /* access modifiers changed from: protected */
    public boolean containsSubId(int[] iArr, int i) {
        return Arrays.stream(iArr).anyMatch(new WifiCallingSettings$$ExternalSyntheticLambda0(i));
    }
}
