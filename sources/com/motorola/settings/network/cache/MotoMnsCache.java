package com.motorola.settings.network.cache;

import android.content.Context;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.os.UserManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.settings.network.SubscriptionUtil;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotoMnsCache {
    private static MotoMnsCache sCache;
    private List<SubscriptionInfo> mActiveSubInfos;
    private List<SubscriptionInfo> mAvailableSubInfos;
    private Map<Integer, PersistableBundle> mCarrierConfigs;
    private CarrierConfigManager mCarrierManager;
    private Map<String, Boolean> mComponentFlags;
    private Context mContext;
    private Map<String, Boolean> mPackageFlags;
    private Map<Integer, Integer> mPhoneIds;
    private Map<Integer, Resources> mResources;
    private int[] mSubIds;
    private Map<Integer, SubscriptionInfo> mSubInfos;
    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;
    private UserManager mUserManager;

    private MotoMnsCache(Context context) {
        MotoMnsLog.logd("MotoMnsCache", "create cache");
        this.mContext = context.getApplicationContext();
        createService();
    }

    public static MotoMnsCache getIns(Context context) {
        if (sCache == null) {
            sCache = new MotoMnsCache(context);
        }
        return sCache;
    }

    private void createService() {
        this.mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mCarrierManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
    }

    public static void close() {
        MotoMnsLog.logd("MotoMnsCache", "close cache");
        sCache = null;
    }

    public void reset() {
        MotoMnsLog.logd("MotoMnsCache", "reset cache");
        this.mSubIds = null;
        this.mPhoneIds = null;
        this.mSubInfos = null;
        this.mActiveSubInfos = null;
        this.mAvailableSubInfos = null;
        this.mCarrierConfigs = null;
        this.mResources = null;
        this.mPackageFlags = null;
        this.mComponentFlags = null;
    }

    public int[] getActiveSubIds() {
        if (this.mSubIds == null) {
            this.mSubIds = this.mSubscriptionManager.getActiveSubscriptionIdList();
        }
        return this.mSubIds;
    }

    public int getPhoneId(int i) {
        if (this.mPhoneIds == null) {
            this.mPhoneIds = new HashMap();
        }
        if (this.mPhoneIds.get(Integer.valueOf(i)) == null) {
            this.mPhoneIds.put(Integer.valueOf(i), Integer.valueOf(SubscriptionManager.getPhoneId(i)));
        }
        return this.mPhoneIds.get(Integer.valueOf(i)).intValue();
    }

    public SubscriptionInfo getActiveSubInfo(int i) {
        if (this.mSubInfos == null) {
            this.mSubInfos = new HashMap();
        }
        if (this.mSubInfos.get(Integer.valueOf(i)) == null) {
            this.mSubInfos.put(Integer.valueOf(i), this.mSubscriptionManager.getActiveSubscriptionInfo(i));
        }
        return this.mSubInfos.get(Integer.valueOf(i));
    }

    public List<SubscriptionInfo> getAvailableSubInfos() {
        if (this.mAvailableSubInfos == null) {
            this.mAvailableSubInfos = SubscriptionUtil.getAvailableSubscriptions(this.mContext);
        }
        return this.mAvailableSubInfos;
    }

    public SubscriptionInfo getAvailableSubInfo(int i) {
        List<SubscriptionInfo> availableSubInfos = getAvailableSubInfos();
        if (availableSubInfos == null) {
            return null;
        }
        for (SubscriptionInfo next : availableSubInfos) {
            if (next.getSubscriptionId() == i) {
                return next;
            }
        }
        return null;
    }

    public PersistableBundle getCarrierConfigForSubId(int i) {
        if (this.mCarrierConfigs == null) {
            this.mCarrierConfigs = new HashMap();
        }
        if (this.mCarrierConfigs.get(Integer.valueOf(i)) == null) {
            this.mCarrierConfigs.put(Integer.valueOf(i), this.mCarrierManager.getConfigForSubId(i));
        }
        return this.mCarrierConfigs.get(Integer.valueOf(i));
    }

    public Resources getResourcesForSubId(int i) {
        if (this.mResources == null) {
            this.mResources = new HashMap();
        }
        if (this.mResources.get(Integer.valueOf(i)) == null) {
            this.mResources.put(Integer.valueOf(i), SubscriptionManager.getResourcesForSubId(this.mContext, i));
        }
        return this.mResources.get(Integer.valueOf(i));
    }

    public boolean isPackageEnabled(String str, boolean z) {
        if (this.mPackageFlags == null) {
            this.mPackageFlags = new HashMap();
        }
        String str2 = str + z;
        if (this.mPackageFlags.get(str2) == null) {
            this.mPackageFlags.put(str2, Boolean.valueOf(MotoMobileNetworkUtils.isPackageEnabled(this.mContext, str, z)));
        }
        return this.mPackageFlags.get(str2).booleanValue();
    }

    public boolean isComponentEnabled(String str, boolean z) {
        if (this.mComponentFlags == null) {
            this.mComponentFlags = new HashMap();
        }
        String str2 = str + z;
        if (this.mComponentFlags.get(str2) == null) {
            this.mComponentFlags.put(str2, Boolean.valueOf(MotoMobileNetworkUtils.isComponentEnabled(this.mContext, str, z)));
        }
        return this.mComponentFlags.get(str2).booleanValue();
    }
}
