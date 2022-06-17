package com.android.settings.network.telephony;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.network.MobileNetworkSummaryStatus$$ExternalSyntheticLambda3;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.helper.SelectableSubscriptions;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFactory;
import com.motorola.settings.network.cache.MotoMnsCache;
import java.util.List;

public class MobileNetworkActivity extends SettingsBaseActivity implements ProxySubscriptionManager.OnActiveSubscriptionChangedListener {
    static final String MOBILE_SETTINGS_TAG = "mobile_settings:";
    static final int SUB_ID_NULL = Integer.MIN_VALUE;
    private int mCurSubscriptionId;
    private boolean mFragmentForceReload = true;
    private boolean mIsAdminUser = true;
    private boolean mPendingSubscriptionChange = false;
    ProxySubscriptionManager mProxySubscriptionMgr;
    private Resources mResourcesBySubId;
    private boolean mSupportUpdateUI = true;

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.mIsAdminUser) {
            validate(intent);
            setIntent(intent);
            int i = SUB_ID_NULL;
            if (intent != null) {
                i = intent.getIntExtra("android.provider.extra.SUB_ID", SUB_ID_NULL);
            }
            int i2 = this.mCurSubscriptionId;
            this.mCurSubscriptionId = i;
            this.mFragmentForceReload = i == i2;
            SubscriptionInfo subscription = getSubscription();
            updateSubscriptions(subscription, (Bundle) null);
            if (i != i2 || !doesIntentContainOptInAction(intent)) {
                removeContactDiscoveryDialog(i2);
            }
            if (doesIntentContainOptInAction(intent)) {
                maybeShowContactDiscoveryDialog(subscription);
            }
            MotoMnsLog.logd("MobileNetworkActivity", "oldSubId:" + i2 + ", mCurSubscriptionId:" + this.mCurSubscriptionId);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        MotoMnsLog.logd("MobileNetworkActivity", "onCreate: +");
        super.onCreate(bundle);
        boolean isAdminUser = ((UserManager) getSystemService(UserManager.class)).isAdminUser();
        this.mIsAdminUser = isAdminUser;
        if (!isAdminUser) {
            setContentView(C1987R$layout.mobile_network_settings_disallowed);
            setActionBar((Toolbar) findViewById(C1985R$id.mobile_action_bar));
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(C1985R$id.action_bar);
        toolbar.setVisibility(0);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        getProxySubscriptionManager().setLifecycle(getLifecycle());
        Intent intent = getIntent();
        validate(intent);
        int i = SUB_ID_NULL;
        if (bundle != null) {
            i = bundle.getInt("android.provider.extra.SUB_ID", SUB_ID_NULL);
        } else if (intent != null) {
            i = intent.getIntExtra("android.provider.extra.SUB_ID", SUB_ID_NULL);
        }
        this.mCurSubscriptionId = i;
        registerActiveSubscriptionsListener();
        SubscriptionInfo subscription = getSubscription();
        maybeShowContactDiscoveryDialog(subscription);
        updateSubscriptions(subscription, (Bundle) null);
        MotoMnsLog.logd("MobileNetworkActivity", "onCreate: -");
    }

    /* access modifiers changed from: package-private */
    public ProxySubscriptionManager getProxySubscriptionManager() {
        if (this.mProxySubscriptionMgr == null) {
            this.mProxySubscriptionMgr = ProxySubscriptionManager.getInstance(this);
        }
        return this.mProxySubscriptionMgr;
    }

    /* access modifiers changed from: package-private */
    public void registerActiveSubscriptionsListener() {
        getProxySubscriptionManager().addActiveSubscriptionsListener(this);
    }

    public void onChanged() {
        if (!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            this.mPendingSubscriptionChange = true;
        } else if (this.mIsAdminUser) {
            MotoMnsCache.getIns(this).reset();
            SubscriptionInfo subscription = getSubscription();
            int i = this.mCurSubscriptionId;
            MotoMnsLog.logd("MobileNetworkActivity", "onChanged: info:" + subscription);
            updateSubscriptions(subscription, (Bundle) null);
            if (subscription == null) {
                if (i != SUB_ID_NULL && !isFinishing() && !isDestroyed()) {
                    finish();
                }
            } else if (subscription.getSubscriptionId() != i) {
                removeContactDiscoveryDialog(i);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        MotoMnsLog.logd("MobileNetworkActivity", "onStart: +");
        if (this.mIsAdminUser) {
            getProxySubscriptionManager().setLifecycle(getLifecycle());
        }
        super.onStart();
        if (this.mIsAdminUser) {
            if (this.mPendingSubscriptionChange) {
                this.mPendingSubscriptionChange = false;
                onChanged();
            }
            this.mSupportUpdateUI = true;
            MotoMnsLog.logd("MobileNetworkActivity", "onStart: -");
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        MotoMnsLog.logd("MobileNetworkActivity", "onDestroy: +");
        super.onDestroy();
        ProxySubscriptionManager proxySubscriptionManager = this.mProxySubscriptionMgr;
        if (proxySubscriptionManager != null) {
            proxySubscriptionManager.removeActiveSubscriptionsListener(this);
            MotoMnsLog.logd("MobileNetworkActivity", "onDestroy: -");
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveInstanceState(bundle);
    }

    /* access modifiers changed from: package-private */
    public void saveInstanceState(Bundle bundle) {
        bundle.putInt("android.provider.extra.SUB_ID", this.mCurSubscriptionId);
    }

    private void updateTitleAndNavigation(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo != null) {
            setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, (Context) this));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateSubscriptions(SubscriptionInfo subscriptionInfo, Bundle bundle) {
        if (subscriptionInfo != null) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                this.mResourcesBySubId = SubscriptionManager.getResourcesForSubId(this, subscriptionId);
            }
            updateTitleAndNavigation(subscriptionInfo);
            if (bundle == null) {
                switchFragment(subscriptionInfo);
            }
            this.mCurSubscriptionId = subscriptionId;
            this.mFragmentForceReload = false;
        }
    }

    /* access modifiers changed from: package-private */
    public SubscriptionInfo getSubscription() {
        List call = new SelectableSubscriptions(this, true).call();
        SubscriptionAnnotation subscriptionAnnotation = this.mCurSubscriptionId != SUB_ID_NULL ? (SubscriptionAnnotation) call.stream().filter(MobileNetworkSummaryStatus$$ExternalSyntheticLambda3.INSTANCE).filter(new MobileNetworkActivity$$ExternalSyntheticLambda0(this)).findFirst().orElse((Object) null) : null;
        if (subscriptionAnnotation == null) {
            subscriptionAnnotation = (SubscriptionAnnotation) call.stream().filter(MobileNetworkSummaryStatus$$ExternalSyntheticLambda3.INSTANCE).filter(MobileNetworkActivity$$ExternalSyntheticLambda1.INSTANCE).findFirst().orElse((Object) null);
        }
        if (subscriptionAnnotation == null) {
            return null;
        }
        return subscriptionAnnotation.getSubInfo();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getSubscription$0(SubscriptionAnnotation subscriptionAnnotation) {
        return subscriptionAnnotation.getSubscriptionId() == this.mCurSubscriptionId;
    }

    /* access modifiers changed from: package-private */
    public SubscriptionInfo getSubscriptionForSubId(int i) {
        return MotoMnsCache.getIns(this).getAvailableSubInfo(i);
    }

    /* access modifiers changed from: package-private */
    public void switchFragment(SubscriptionInfo subscriptionInfo) {
        if (!this.mSupportUpdateUI) {
            Log.d("MobileNetworkActivity", "switchFragment - do not support update UI");
            return;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", subscriptionId);
        if (intent != null && "android.settings.MMS_MESSAGE_SETTING".equals(intent.getAction())) {
            bundle.putString(":settings:fragment_args_key", "mms_message");
        }
        String buildFragmentTag = buildFragmentTag(subscriptionId);
        if (supportFragmentManager.findFragmentByTag(buildFragmentTag) != null) {
            if (!this.mFragmentForceReload) {
                Log.d("MobileNetworkActivity", "Keep current fragment: " + buildFragmentTag);
                return;
            }
            Log.d("MobileNetworkActivity", "Construct fragment: " + buildFragmentTag);
        }
        MobileNetworkSettings createMobileNetworkSettings = MotoTelephonyFactory.createMobileNetworkSettings(this, subscriptionId);
        MotoMnsLog.logd("MobileNetworkActivity", "switchFragment - fragment:" + createMobileNetworkSettings.getClass().getName() + ", mFragmentForceReload:" + this.mFragmentForceReload + ", subInfo:" + subscriptionInfo);
        createMobileNetworkSettings.setArguments(bundle);
        beginTransaction.replace(C1985R$id.content_frame, createMobileNetworkSettings, buildFragmentTag);
        beginTransaction.commitAllowingStateLoss();
    }

    private void removeContactDiscoveryDialog(int i) {
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (contactDiscoveryFragment != null) {
            contactDiscoveryFragment.dismiss();
        }
    }

    private ContactDiscoveryDialogFragment getContactDiscoveryFragment(int i) {
        return (ContactDiscoveryDialogFragment) getSupportFragmentManager().findFragmentByTag(ContactDiscoveryDialogFragment.getFragmentTag(i));
    }

    private void maybeShowContactDiscoveryDialog(SubscriptionInfo subscriptionInfo) {
        CharSequence charSequence;
        int i;
        if (subscriptionInfo != null) {
            i = subscriptionInfo.getSubscriptionId();
            charSequence = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, (Context) this);
        } else {
            i = -1;
            charSequence = "";
        }
        boolean z = doesIntentContainOptInAction(getIntent()) && MobileNetworkUtils.isContactDiscoveryVisible(this, i) && !MobileNetworkUtils.isContactDiscoveryEnabled((Context) this, i);
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (z) {
            if (contactDiscoveryFragment == null) {
                contactDiscoveryFragment = ContactDiscoveryDialogFragment.newInstance(i, charSequence);
            }
            if (!contactDiscoveryFragment.isAdded()) {
                contactDiscoveryFragment.show(getSupportFragmentManager(), ContactDiscoveryDialogFragment.getFragmentTag(i));
            }
        }
    }

    private boolean doesIntentContainOptInAction(Intent intent) {
        return TextUtils.equals(intent != null ? intent.getAction() : null, "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN");
    }

    private void validate(Intent intent) {
        if (doesIntentContainOptInAction(intent) && SUB_ID_NULL == intent.getIntExtra("android.provider.extra.SUB_ID", SUB_ID_NULL)) {
            throw new IllegalArgumentException("Intent with action SHOW_CAPABILITY_DISCOVERY_OPT_IN must also include the extra Settings#EXTRA_SUB_ID");
        }
    }

    /* access modifiers changed from: package-private */
    public String buildFragmentTag(int i) {
        return MOBILE_SETTINGS_TAG + i;
    }

    public Resources getResources() {
        Resources resources = this.mResourcesBySubId;
        return resources == null ? super.getResources() : resources;
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MotoMnsLog.logd("MobileNetworkActivity", "onStop: +");
        super.onStop();
        if (this.mIsAdminUser) {
            this.mSupportUpdateUI = false;
            MotoMnsLog.logd("MobileNetworkActivity", "onStop: -");
        }
    }
}
