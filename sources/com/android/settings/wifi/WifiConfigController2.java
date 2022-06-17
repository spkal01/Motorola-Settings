package com.android.settings.wifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.InetAddresses;
import android.net.IpConfiguration;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.security.LegacyVpnProfileStore;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.net.module.util.ProxyUtils;
import com.android.settings.C1978R$array;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.utils.AndroidKeystoreAliasLoader;
import com.android.settings.wifi.details2.WifiPrivacyPreferenceController2;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.wifitrackerlib.WifiEntry;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class WifiConfigController2 implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, View.OnKeyListener {
    static final String[] UNDESIRED_CERTIFICATES = {"MacRandSecret", "MacRandSapSecret"};
    private static final int[] WAPI_PSK_TYPE = {0, 1};
    protected int REQUEST_INSTALL_CERTS = 1;
    private List<SubscriptionInfo> mActiveSubscriptionInfos = new ArrayList();
    /* access modifiers changed from: private */
    public final WifiConfigUiBase2 mConfigUi;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public TextView mDns1View;
    private TextView mDns2View;
    private String mDoNotProvideEapUserCertString;
    private String mDoNotValidateEapServerString;
    private TextView mEapAnonymousView;
    Spinner mEapCaCertSpinner;
    private TextView mEapDomainView;
    private TextView mEapIdentityView;
    Spinner mEapMethodSpinner;
    private Spinner mEapOcspSpinner;
    Spinner mEapSimSpinner;
    private Spinner mEapUserCertSpinner;
    /* access modifiers changed from: private */
    public TextView mGatewayView;
    private boolean mHaveWapiCert = false;
    private Spinner mHiddenSettingsSpinner;
    private TextView mHiddenWarningView;
    private ProxyInfo mHttpProxy = null;
    String mInstallCertsString;
    private TextView mIpAddressView;
    private IpConfiguration.IpAssignment mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
    private Spinner mIpSettingsSpinner;
    private int mLastShownEapMethod;
    private String[] mLevels;
    private Spinner mMeteredSettingsSpinner;
    private int mMode;
    private String mMultipleCertSetString;
    /* access modifiers changed from: private */
    public TextView mNetworkPrefixLengthView;
    private TextView mPasswordView;
    private ArrayAdapter<CharSequence> mPhase2Adapter;
    private ArrayAdapter<CharSequence> mPhase2PeapAdapter;
    private Spinner mPhase2Spinner;
    private ArrayAdapter<CharSequence> mPhase2TtlsAdapter;
    private Spinner mPrivacySettingsSpinner;
    private TextView mProxyExclusionListView;
    private TextView mProxyHostView;
    private TextView mProxyPacView;
    private TextView mProxyPortView;
    private IpConfiguration.ProxySettings mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
    private Spinner mProxySettingsSpinner;
    Integer[] mSecurityInPosition;
    private Spinner mSecuritySpinner;
    private CheckBox mSharedCheckBox;
    private ImageButton mSsidScanButton;
    private TextView mSsidView;
    private StaticIpConfiguration mStaticIpConfiguration = null;
    private String mUnspecifiedCertString;
    private String mUseSystemCertsString;
    private final View mView;
    private Spinner mWapiCertSpinner;
    private Spinner mWapiPskTypeSpinner;
    private final WifiEntry mWifiEntry;
    int mWifiEntrySecurity;
    private final WifiManager mWifiManager;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public WifiConfigController2(WifiConfigUiBase2 wifiConfigUiBase2, View view, WifiEntry wifiEntry, int i) {
        this.mConfigUi = wifiConfigUiBase2;
        this.mView = view;
        this.mWifiEntry = wifiEntry;
        Context context = wifiConfigUiBase2.getContext();
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        initWifiConfigController2(wifiEntry, i);
    }

    public WifiConfigController2(WifiConfigUiBase2 wifiConfigUiBase2, View view, WifiEntry wifiEntry, int i, WifiManager wifiManager) {
        this.mConfigUi = wifiConfigUiBase2;
        this.mView = view;
        this.mWifiEntry = wifiEntry;
        this.mContext = wifiConfigUiBase2.getContext();
        this.mWifiManager = wifiManager;
        initWifiConfigController2(wifiEntry, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x01df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initWifiConfigController2(com.android.wifitrackerlib.WifiEntry r10, int r11) {
        /*
            r9 = this;
            r0 = 0
            if (r10 != 0) goto L_0x0005
            r10 = r0
            goto L_0x0009
        L_0x0005:
            int r10 = r10.getSecurity()
        L_0x0009:
            r9.mWifiEntrySecurity = r10
            r9.mMode = r11
            android.content.Context r10 = r9.mContext
            android.content.res.Resources r10 = r10.getResources()
            int r11 = com.android.settings.C1978R$array.wifi_signal
            java.lang.String[] r11 = r10.getStringArray(r11)
            r9.mLevels = r11
            android.content.Context r11 = r9.mContext
            boolean r11 = com.android.settingslib.Utils.isWifiOnly(r11)
            if (r11 != 0) goto L_0x003c
            android.content.Context r11 = r9.mContext
            android.content.res.Resources r11 = r11.getResources()
            r1 = 17891556(0x11100e4, float:2.6632933E-38)
            boolean r11 = r11.getBoolean(r1)
            if (r11 != 0) goto L_0x0033
            goto L_0x003c
        L_0x0033:
            int r11 = com.android.settings.C1978R$array.wifi_peap_phase2_entries_with_sim_auth
            android.widget.ArrayAdapter r11 = r9.getSpinnerAdapterWithEapMethodsTts(r11)
            r9.mPhase2PeapAdapter = r11
            goto L_0x0044
        L_0x003c:
            int r11 = com.android.settings.C1978R$array.wifi_peap_phase2_entries
            android.widget.ArrayAdapter r11 = r9.getSpinnerAdapter((int) r11)
            r9.mPhase2PeapAdapter = r11
        L_0x0044:
            int r11 = com.android.settings.C1978R$array.wifi_ttls_phase2_entries
            android.widget.ArrayAdapter r11 = r9.getSpinnerAdapter((int) r11)
            r9.mPhase2TtlsAdapter = r11
            android.content.Context r11 = r9.mContext
            int r1 = com.android.settings.C1992R$string.wifi_unspecified
            java.lang.String r11 = r11.getString(r1)
            r9.mUnspecifiedCertString = r11
            android.content.Context r11 = r9.mContext
            int r1 = com.android.settings.C1992R$string.wifi_multiple_cert_added
            java.lang.String r11 = r11.getString(r1)
            r9.mMultipleCertSetString = r11
            android.content.Context r11 = r9.mContext
            int r1 = com.android.settings.C1992R$string.wifi_use_system_certs
            java.lang.String r11 = r11.getString(r1)
            r9.mUseSystemCertsString = r11
            android.content.Context r11 = r9.mContext
            int r1 = com.android.settings.C1992R$string.wifi_do_not_provide_eap_user_cert
            java.lang.String r11 = r11.getString(r1)
            r9.mDoNotProvideEapUserCertString = r11
            android.content.Context r11 = r9.mContext
            int r1 = com.android.settings.C1992R$string.wifi_do_not_validate_eap_server
            java.lang.String r11 = r11.getString(r1)
            r9.mDoNotValidateEapServerString = r11
            android.content.Context r11 = r9.mContext
            int r1 = com.android.settings.C1992R$string.wifi_install_credentials
            java.lang.String r11 = r11.getString(r1)
            r9.mInstallCertsString = r11
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.ssid_scanner_button
            android.view.View r11 = r11.findViewById(r1)
            android.widget.ImageButton r11 = (android.widget.ImageButton) r11
            r9.mSsidScanButton = r11
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.ip_settings
            android.view.View r11 = r11.findViewById(r1)
            android.widget.Spinner r11 = (android.widget.Spinner) r11
            r9.mIpSettingsSpinner = r11
            r11.setOnItemSelectedListener(r9)
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.proxy_settings
            android.view.View r11 = r11.findViewById(r1)
            android.widget.Spinner r11 = (android.widget.Spinner) r11
            r9.mProxySettingsSpinner = r11
            r11.setOnItemSelectedListener(r9)
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.shared
            android.view.View r11 = r11.findViewById(r1)
            android.widget.CheckBox r11 = (android.widget.CheckBox) r11
            r9.mSharedCheckBox = r11
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.metered_settings
            android.view.View r11 = r11.findViewById(r1)
            android.widget.Spinner r11 = (android.widget.Spinner) r11
            r9.mMeteredSettingsSpinner = r11
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.hidden_settings
            android.view.View r11 = r11.findViewById(r1)
            android.widget.Spinner r11 = (android.widget.Spinner) r11
            r9.mHiddenSettingsSpinner = r11
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.privacy_settings
            android.view.View r11 = r11.findViewById(r1)
            android.widget.Spinner r11 = (android.widget.Spinner) r11
            r9.mPrivacySettingsSpinner = r11
            android.net.wifi.WifiManager r11 = r9.mWifiManager
            boolean r11 = r11.isConnectedMacRandomizationSupported()
            if (r11 == 0) goto L_0x00f5
            android.view.View r11 = r9.mView
            int r1 = com.android.settings.C1985R$id.privacy_settings_fields
            android.view.View r11 = r11.findViewById(r1)
            r11.setVisibility(r0)
        L_0x00f5:
            android.widget.Spinner r11 = r9.mHiddenSettingsSpinner
            r1 = 1
            r11.setSelection(r1)
            android.widget.Spinner r11 = r9.mHiddenSettingsSpinner
            r11.setOnItemSelectedListener(r9)
            android.view.View r11 = r9.mView
            int r2 = com.android.settings.C1985R$id.hidden_settings_warning
            android.view.View r11 = r11.findViewById(r2)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r9.mHiddenWarningView = r11
            android.widget.Spinner r2 = r9.mHiddenSettingsSpinner
            int r2 = r2.getSelectedItemPosition()
            r3 = 8
            if (r2 != 0) goto L_0x0118
            r2 = r3
            goto L_0x0119
        L_0x0118:
            r2 = r0
        L_0x0119:
            r11.setVisibility(r2)
            r11 = 10
            java.lang.Integer[] r11 = new java.lang.Integer[r11]
            r9.mSecurityInPosition = r11
            com.android.wifitrackerlib.WifiEntry r11 = r9.mWifiEntry
            if (r11 != 0) goto L_0x0136
            r9.configureSecuritySpinner()
            com.android.settings.wifi.WifiConfigUiBase2 r11 = r9.mConfigUi
            int r0 = com.android.settings.C1992R$string.wifi_save
            java.lang.String r0 = r10.getString(r0)
            r11.setSubmitButton(r0)
            goto L_0x0344
        L_0x0136:
            com.android.settings.wifi.WifiConfigUiBase2 r2 = r9.mConfigUi
            java.lang.String r11 = r11.getTitle()
            r2.setTitle((java.lang.CharSequence) r11)
            android.view.View r11 = r9.mView
            int r2 = com.android.settings.C1985R$id.info
            android.view.View r11 = r11.findViewById(r2)
            android.view.ViewGroup r11 = (android.view.ViewGroup) r11
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            boolean r2 = r2.isSaved()
            r4 = 2
            if (r2 == 0) goto L_0x01f7
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            android.net.wifi.WifiConfiguration r2 = r2.getWifiConfiguration()
            android.widget.Spinner r5 = r9.mMeteredSettingsSpinner
            int r6 = r2.meteredOverride
            r5.setSelection(r6)
            android.widget.Spinner r5 = r9.mHiddenSettingsSpinner
            boolean r6 = r2.hiddenSSID
            r5.setSelection(r6)
            int r5 = r2.macRandomizationSetting
            int r5 = com.android.settings.wifi.details2.WifiPrivacyPreferenceController2.translateMacRandomizedValueToPrefValue(r5)
            android.widget.Spinner r6 = r9.mPrivacySettingsSpinner
            r6.setSelection(r5)
            android.net.IpConfiguration r5 = r2.getIpConfiguration()
            android.net.IpConfiguration$IpAssignment r5 = r5.getIpAssignment()
            android.net.IpConfiguration$IpAssignment r6 = android.net.IpConfiguration.IpAssignment.STATIC
            if (r5 != r6) goto L_0x01a5
            android.widget.Spinner r5 = r9.mIpSettingsSpinner
            r5.setSelection(r1)
            android.net.IpConfiguration r5 = r2.getIpConfiguration()
            android.net.StaticIpConfiguration r5 = r5.getStaticIpConfiguration()
            if (r5 == 0) goto L_0x01a3
            android.net.LinkAddress r6 = r5.getIpAddress()
            if (r6 == 0) goto L_0x01a3
            int r6 = com.android.settings.C1992R$string.wifi_ip_address
            android.net.LinkAddress r5 = r5.getIpAddress()
            java.net.InetAddress r5 = r5.getAddress()
            java.lang.String r5 = r5.getHostAddress()
            r9.addRow(r11, r6, r5)
        L_0x01a3:
            r5 = r1
            goto L_0x01ab
        L_0x01a5:
            android.widget.Spinner r5 = r9.mIpSettingsSpinner
            r5.setSelection(r0)
            r5 = r0
        L_0x01ab:
            android.widget.CheckBox r6 = r9.mSharedCheckBox
            boolean r7 = r2.shared
            r6.setEnabled(r7)
            boolean r6 = r2.shared
            if (r6 != 0) goto L_0x01b7
            r5 = r1
        L_0x01b7:
            android.net.IpConfiguration r6 = r2.getIpConfiguration()
            android.net.IpConfiguration$ProxySettings r6 = r6.getProxySettings()
            android.net.IpConfiguration$ProxySettings r7 = android.net.IpConfiguration.ProxySettings.STATIC
            if (r6 != r7) goto L_0x01ca
            android.widget.Spinner r5 = r9.mProxySettingsSpinner
            r5.setSelection(r1)
        L_0x01c8:
            r5 = r1
            goto L_0x01d9
        L_0x01ca:
            android.net.IpConfiguration$ProxySettings r7 = android.net.IpConfiguration.ProxySettings.PAC
            if (r6 != r7) goto L_0x01d4
            android.widget.Spinner r5 = r9.mProxySettingsSpinner
            r5.setSelection(r4)
            goto L_0x01c8
        L_0x01d4:
            android.widget.Spinner r6 = r9.mProxySettingsSpinner
            r6.setSelection(r0)
        L_0x01d9:
            boolean r6 = r2.isPasspoint()
            if (r6 == 0) goto L_0x01f8
            int r6 = com.android.settings.C1992R$string.passpoint_label
            android.content.Context r7 = r9.mContext
            int r8 = com.android.settings.C1992R$string.passpoint_content
            java.lang.String r7 = r7.getString(r8)
            java.lang.Object[] r8 = new java.lang.Object[r1]
            java.lang.String r2 = r2.providerFriendlyName
            r8[r0] = r2
            java.lang.String r2 = java.lang.String.format(r7, r8)
            r9.addRow(r11, r6, r2)
            goto L_0x01f8
        L_0x01f7:
            r5 = r0
        L_0x01f8:
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            boolean r2 = r2.isSaved()
            if (r2 != 0) goto L_0x0210
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            int r2 = r2.getConnectedState()
            if (r2 == r4) goto L_0x0210
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            boolean r2 = r2.isSubscription()
            if (r2 == 0) goto L_0x0214
        L_0x0210:
            int r2 = r9.mMode
            if (r2 == 0) goto L_0x024d
        L_0x0214:
            r9.showSecurityFields(r1, r1)
            r9.showIpConfigFields()
            r9.showProxyFields()
            android.view.View r2 = r9.mView
            int r6 = com.android.settings.C1985R$id.wifi_advanced_togglebox
            android.view.View r2 = r2.findViewById(r6)
            android.widget.CheckBox r2 = (android.widget.CheckBox) r2
            if (r5 != 0) goto L_0x023d
            android.view.View r6 = r9.mView
            int r7 = com.android.settings.C1985R$id.wifi_advanced_toggle
            android.view.View r6 = r6.findViewById(r7)
            r6.setVisibility(r0)
            r2.setOnCheckedChangeListener(r9)
            r2.setChecked(r5)
            r9.setAdvancedOptionAccessibilityString()
        L_0x023d:
            android.view.View r2 = r9.mView
            int r6 = com.android.settings.C1985R$id.wifi_advanced_fields
            android.view.View r2 = r2.findViewById(r6)
            if (r5 == 0) goto L_0x0249
            r5 = r0
            goto L_0x024a
        L_0x0249:
            r5 = r3
        L_0x024a:
            r2.setVisibility(r5)
        L_0x024d:
            int r2 = r9.mMode
            if (r2 != r4) goto L_0x025e
            com.android.settings.wifi.WifiConfigUiBase2 r11 = r9.mConfigUi
            int r0 = com.android.settings.C1992R$string.wifi_save
            java.lang.String r0 = r10.getString(r0)
            r11.setSubmitButton(r0)
            goto L_0x033f
        L_0x025e:
            if (r2 != r1) goto L_0x026d
            com.android.settings.wifi.WifiConfigUiBase2 r11 = r9.mConfigUi
            int r0 = com.android.settings.C1992R$string.wifi_connect
            java.lang.String r0 = r10.getString(r0)
            r11.setSubmitButton(r0)
            goto L_0x033f
        L_0x026d:
            java.lang.String r2 = r9.getSignalString()
            com.android.wifitrackerlib.WifiEntry r5 = r9.mWifiEntry
            int r5 = r5.getConnectedState()
            if (r5 != 0) goto L_0x0288
            if (r2 == 0) goto L_0x0288
            com.android.settings.wifi.WifiConfigUiBase2 r11 = r9.mConfigUi
            int r0 = com.android.settings.C1992R$string.wifi_connect
            java.lang.String r0 = r10.getString(r0)
            r11.setSubmitButton(r0)
            goto L_0x031c
        L_0x0288:
            if (r2 == 0) goto L_0x028f
            int r5 = com.android.settings.C1992R$string.wifi_signal
            r9.addRow(r11, r5, r2)
        L_0x028f:
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            com.android.wifitrackerlib.WifiEntry$ConnectedInfo r2 = r2.getConnectedInfo()
            if (r2 == 0) goto L_0x02b4
            int r5 = r2.linkSpeedMbps
            if (r5 < 0) goto L_0x02b4
            int r5 = com.android.settings.C1992R$string.wifi_speed
            int r6 = com.android.settings.C1992R$string.link_speed
            java.lang.String r6 = r10.getString(r6)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            int r7 = r2.linkSpeedMbps
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r1[r0] = r7
            java.lang.String r1 = java.lang.String.format(r6, r1)
            r9.addRow(r11, r5, r1)
        L_0x02b4:
            if (r2 == 0) goto L_0x0306
            int r1 = r2.frequencyMhz
            r2 = -1
            if (r1 == r2) goto L_0x0306
            r2 = 0
            r5 = 2400(0x960, float:3.363E-42)
            if (r1 < r5) goto L_0x02cb
            r5 = 2500(0x9c4, float:3.503E-42)
            if (r1 >= r5) goto L_0x02cb
            int r1 = com.android.settings.C1992R$string.wifi_band_24ghz
            java.lang.String r2 = r10.getString(r1)
            goto L_0x02ff
        L_0x02cb:
            r5 = 4900(0x1324, float:6.866E-42)
            if (r1 < r5) goto L_0x02da
            r5 = 5900(0x170c, float:8.268E-42)
            if (r1 >= r5) goto L_0x02da
            int r1 = com.android.settings.C1992R$string.wifi_band_5ghz
            java.lang.String r2 = r10.getString(r1)
            goto L_0x02ff
        L_0x02da:
            r5 = 5925(0x1725, float:8.303E-42)
            if (r1 < r5) goto L_0x02e9
            r5 = 7125(0x1bd5, float:9.984E-42)
            if (r1 >= r5) goto L_0x02e9
            int r1 = com.android.settings.C1992R$string.wifi_band_6ghz
            java.lang.String r2 = r10.getString(r1)
            goto L_0x02ff
        L_0x02e9:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Unexpected frequency "
            r5.append(r6)
            r5.append(r1)
            java.lang.String r1 = r5.toString()
            java.lang.String r5 = "WifiConfigController2"
            android.util.Log.e(r5, r1)
        L_0x02ff:
            if (r2 == 0) goto L_0x0306
            int r1 = com.android.settings.C1992R$string.wifi_frequency
            r9.addRow(r11, r1, r2)
        L_0x0306:
            int r1 = com.android.settings.C1992R$string.wifi_security
            com.android.wifitrackerlib.WifiEntry r2 = r9.mWifiEntry
            java.lang.String r0 = r2.getSecurityString(r0)
            r9.addRow(r11, r1, r0)
            android.view.View r11 = r9.mView
            int r0 = com.android.settings.C1985R$id.ip_fields
            android.view.View r11 = r11.findViewById(r0)
            r11.setVisibility(r3)
        L_0x031c:
            com.android.wifitrackerlib.WifiEntry r11 = r9.mWifiEntry
            boolean r11 = r11.isSaved()
            if (r11 != 0) goto L_0x0334
            com.android.wifitrackerlib.WifiEntry r11 = r9.mWifiEntry
            int r11 = r11.getConnectedState()
            if (r11 == r4) goto L_0x0334
            com.android.wifitrackerlib.WifiEntry r11 = r9.mWifiEntry
            boolean r11 = r11.isSubscription()
            if (r11 == 0) goto L_0x033f
        L_0x0334:
            com.android.settings.wifi.WifiConfigUiBase2 r11 = r9.mConfigUi
            int r0 = com.android.settings.C1992R$string.wifi_forget
            java.lang.String r0 = r10.getString(r0)
            r11.setForgetButton(r0)
        L_0x033f:
            android.widget.ImageButton r11 = r9.mSsidScanButton
            r11.setVisibility(r3)
        L_0x0344:
            android.widget.CheckBox r11 = r9.mSharedCheckBox
            r11.setVisibility(r3)
            com.android.settings.wifi.WifiConfigUiBase2 r11 = r9.mConfigUi
            int r0 = com.android.settings.C1992R$string.wifi_cancel
            java.lang.String r10 = r10.getString(r0)
            r11.setCancelButton(r10)
            com.android.settings.wifi.WifiConfigUiBase2 r10 = r9.mConfigUi
            android.widget.Button r10 = r10.getSubmitButton()
            if (r10 == 0) goto L_0x035f
            r9.enableSubmitIfAppropriate()
        L_0x035f:
            android.view.View r9 = r9.mView
            int r10 = com.android.settings.C1985R$id.l_wifidialog
            android.view.View r9 = r9.findViewById(r10)
            r9.requestFocus()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController2.initWifiConfigController2(com.android.wifitrackerlib.WifiEntry, int):void");
    }

    private void addRow(ViewGroup viewGroup, int i, String str) {
        View inflate = this.mConfigUi.getLayoutInflater().inflate(C1987R$layout.wifi_dialog_row, viewGroup, false);
        ((TextView) inflate.findViewById(C1985R$id.name)).setText(i);
        ((TextView) inflate.findViewById(C1985R$id.value)).setText(str);
        viewGroup.addView(inflate);
    }

    /* access modifiers changed from: package-private */
    public String getSignalString() {
        int level;
        if (this.mWifiEntry.getLevel() == -1 || (level = this.mWifiEntry.getLevel()) <= -1) {
            return null;
        }
        String[] strArr = this.mLevels;
        if (level < strArr.length) {
            return strArr[level];
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void hideForgetButton() {
        Button forgetButton = this.mConfigUi.getForgetButton();
        if (forgetButton != null) {
            forgetButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideSubmitButton() {
        Button submitButton = this.mConfigUi.getSubmitButton();
        if (submitButton != null) {
            submitButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: package-private */
    public void enableSubmitIfAppropriate() {
        Button submitButton = this.mConfigUi.getSubmitButton();
        if (submitButton != null) {
            submitButton.setEnabled(isSubmittable());
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isValidPsk(String str) {
        if (str.length() == 64 && str.matches("[0-9A-Fa-f]{64}")) {
            return true;
        }
        if (str.length() < 8 || str.length() > 63) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isValidSaePassword(String str) {
        return str.length() >= 1 && str.length() <= 63;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0051, code lost:
        r0 = r8.mWifiEntry;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005d, code lost:
        r0 = r8.mWifiEntry;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isSubmittable() {
        /*
            r8 = this;
            android.widget.TextView r0 = r8.mPasswordView
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0046
            int r3 = r8.mWifiEntrySecurity
            if (r3 != r1) goto L_0x0010
            int r0 = r0.length()
            if (r0 == 0) goto L_0x0047
        L_0x0010:
            int r0 = r8.mWifiEntrySecurity
            r3 = 2
            if (r0 != r3) goto L_0x0025
            android.widget.TextView r0 = r8.mPasswordView
            java.lang.CharSequence r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            boolean r0 = r8.isValidPsk(r0)
            if (r0 == 0) goto L_0x0047
        L_0x0025:
            int r0 = r8.mWifiEntrySecurity
            r3 = 5
            if (r0 != r3) goto L_0x003a
            android.widget.TextView r0 = r8.mPasswordView
            java.lang.CharSequence r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            boolean r0 = r8.isValidSaePassword(r0)
            if (r0 == 0) goto L_0x0047
        L_0x003a:
            int r0 = r8.mWifiEntrySecurity
            r3 = 7
            if (r0 != r3) goto L_0x0046
            boolean r0 = r8.isWapiPskValid()
            if (r0 != 0) goto L_0x0046
            goto L_0x0047
        L_0x0046:
            r1 = r2
        L_0x0047:
            android.widget.TextView r0 = r8.mSsidView
            if (r0 == 0) goto L_0x0051
            int r0 = r0.length()
            if (r0 == 0) goto L_0x0077
        L_0x0051:
            com.android.wifitrackerlib.WifiEntry r0 = r8.mWifiEntry
            if (r0 == 0) goto L_0x005b
            boolean r0 = r0.isSaved()
            if (r0 != 0) goto L_0x005d
        L_0x005b:
            if (r1 != 0) goto L_0x0077
        L_0x005d:
            com.android.wifitrackerlib.WifiEntry r0 = r8.mWifiEntry
            if (r0 == 0) goto L_0x0072
            boolean r0 = r0.isSaved()
            if (r0 == 0) goto L_0x0072
            if (r1 == 0) goto L_0x0072
            android.widget.TextView r0 = r8.mPasswordView
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0072
            goto L_0x0077
        L_0x0072:
            boolean r0 = r8.ipAndProxyFieldsAreValid()
            goto L_0x0078
        L_0x0077:
            r0 = r2
        L_0x0078:
            int r1 = r8.mWifiEntrySecurity
            r3 = 6
            r4 = 9
            r5 = 3
            r6 = 8
            if (r1 == r5) goto L_0x0086
            if (r1 == r4) goto L_0x0086
            if (r1 != r3) goto L_0x00d5
        L_0x0086:
            android.widget.Spinner r1 = r8.mEapCaCertSpinner
            if (r1 == 0) goto L_0x00d5
            android.view.View r1 = r8.mView
            int r7 = com.android.settings.C1985R$id.l_ca_cert
            android.view.View r1 = r1.findViewById(r7)
            int r1 = r1.getVisibility()
            if (r1 == r6) goto L_0x00d5
            android.widget.Spinner r1 = r8.mEapCaCertSpinner
            java.lang.Object r1 = r1.getSelectedItem()
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r7 = r8.mUnspecifiedCertString
            boolean r7 = r1.equals(r7)
            if (r7 == 0) goto L_0x00aa
        L_0x00a8:
            r0 = r2
            goto L_0x00d5
        L_0x00aa:
            java.lang.String r7 = r8.mDoNotValidateEapServerString
            boolean r1 = r1.equals(r7)
            if (r1 != 0) goto L_0x00d5
            android.widget.TextView r1 = r8.mEapDomainView
            if (r1 == 0) goto L_0x00d5
            android.view.View r1 = r8.mView
            int r7 = com.android.settings.C1985R$id.l_domain
            android.view.View r1 = r1.findViewById(r7)
            int r1 = r1.getVisibility()
            if (r1 == r6) goto L_0x00d5
            android.widget.TextView r1 = r8.mEapDomainView
            java.lang.CharSequence r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x00d5
            goto L_0x00a8
        L_0x00d5:
            int r1 = r8.mWifiEntrySecurity
            if (r1 == r5) goto L_0x00dd
            if (r1 == r4) goto L_0x00dd
            if (r1 != r3) goto L_0x00fe
        L_0x00dd:
            android.widget.Spinner r1 = r8.mEapUserCertSpinner
            if (r1 == 0) goto L_0x00fe
            android.view.View r1 = r8.mView
            int r3 = com.android.settings.C1985R$id.l_user_cert
            android.view.View r1 = r1.findViewById(r3)
            int r1 = r1.getVisibility()
            if (r1 == r6) goto L_0x00fe
            android.widget.Spinner r1 = r8.mEapUserCertSpinner
            java.lang.Object r1 = r1.getSelectedItem()
            java.lang.String r8 = r8.mUnspecifiedCertString
            boolean r8 = r1.equals(r8)
            if (r8 == 0) goto L_0x00fe
            goto L_0x00ff
        L_0x00fe:
            r2 = r0
        L_0x00ff:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController2.isSubmittable():boolean");
    }

    /* access modifiers changed from: package-private */
    public void showWarningMessagesIfAppropriate() {
        View view = this.mView;
        int i = C1985R$id.no_ca_cert_warning;
        view.findViewById(i).setVisibility(8);
        View view2 = this.mView;
        int i2 = C1985R$id.no_user_cert_warning;
        view2.findViewById(i2).setVisibility(8);
        View view3 = this.mView;
        int i3 = C1985R$id.no_domain_warning;
        view3.findViewById(i3).setVisibility(8);
        View view4 = this.mView;
        int i4 = C1985R$id.ssid_too_long_warning;
        view4.findViewById(i4).setVisibility(8);
        TextView textView = this.mSsidView;
        if (textView != null && WifiUtils.isSSIDTooLong(textView.getText().toString())) {
            this.mView.findViewById(i4).setVisibility(0);
        }
        if (!(this.mEapCaCertSpinner == null || this.mView.findViewById(C1985R$id.l_ca_cert).getVisibility() == 8)) {
            String str = (String) this.mEapCaCertSpinner.getSelectedItem();
            if (str.equals(this.mDoNotValidateEapServerString)) {
                this.mView.findViewById(i).setVisibility(0);
            } else if (!str.equals(this.mUnspecifiedCertString) && this.mEapDomainView != null && this.mView.findViewById(C1985R$id.l_domain).getVisibility() != 8 && TextUtils.isEmpty(this.mEapDomainView.getText().toString())) {
                this.mView.findViewById(i3).setVisibility(0);
            }
        }
        if (this.mWifiEntrySecurity == 6 && this.mEapMethodSpinner.getSelectedItemPosition() == 1 && ((String) this.mEapUserCertSpinner.getSelectedItem()).equals(this.mUnspecifiedCertString)) {
            this.mView.findViewById(i2).setVisibility(0);
        }
    }

    private boolean isWapiPskValid() {
        if (this.mPasswordView.length() < 8 || this.mPasswordView.length() > 64) {
            return false;
        }
        String charSequence = this.mPasswordView.getText().toString();
        if (WAPI_PSK_TYPE[this.mWapiPskTypeSpinner.getSelectedItemPosition()] != 1 || (this.mPasswordView.length() % 2 == 0 && charSequence.matches("[0-9A-Fa-f]*"))) {
            return true;
        }
        return false;
    }

    public WifiConfiguration getConfig() {
        if (this.mMode == 0) {
            return null;
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        WifiEntry wifiEntry = this.mWifiEntry;
        if (wifiEntry == null) {
            wifiConfiguration.SSID = "\"" + this.mSsidView.getText().toString() + "\"";
            wifiConfiguration.hiddenSSID = this.mHiddenSettingsSpinner.getSelectedItemPosition() == 1;
        } else {
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(wifiEntry.getTitle());
            if (this.mWifiEntry.isSaved()) {
                wifiConfiguration.networkId = this.mWifiEntry.getWifiConfiguration().networkId;
                wifiConfiguration.hiddenSSID = this.mWifiEntry.getWifiConfiguration().hiddenSSID;
            }
        }
        wifiConfiguration.shared = this.mSharedCheckBox.isChecked();
        int i = this.mWifiEntrySecurity;
        switch (i) {
            case 0:
                wifiConfiguration.setSecurityParams(0);
                break;
            case 1:
                wifiConfiguration.setSecurityParams(1);
                if (this.mPasswordView.length() != 0) {
                    int length = this.mPasswordView.length();
                    String charSequence = this.mPasswordView.getText().toString();
                    if ((length != 10 && length != 26 && length != 58) || !charSequence.matches("[0-9A-Fa-f]*")) {
                        String[] strArr = wifiConfiguration.wepKeys;
                        strArr[0] = '\"' + charSequence + '\"';
                        break;
                    } else {
                        wifiConfiguration.wepKeys[0] = charSequence;
                        break;
                    }
                }
                break;
            case 2:
                wifiConfiguration.setSecurityParams(2);
                if (this.mPasswordView.length() != 0) {
                    String charSequence2 = this.mPasswordView.getText().toString();
                    if (!charSequence2.matches("[0-9A-Fa-f]{64}")) {
                        wifiConfiguration.preSharedKey = '\"' + charSequence2 + '\"';
                        break;
                    } else {
                        wifiConfiguration.preSharedKey = charSequence2;
                        break;
                    }
                }
                break;
            case 3:
            case 6:
            case 9:
                if (i == 6) {
                    wifiConfiguration.setSecurityParams(5);
                } else if (i == 9) {
                    wifiConfiguration.setSecurityParams(9);
                } else {
                    wifiConfiguration.setSecurityParams(3);
                }
                wifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
                int selectedItemPosition = this.mEapMethodSpinner.getSelectedItemPosition();
                int selectedItemPosition2 = this.mPhase2Spinner.getSelectedItemPosition();
                wifiConfiguration.enterpriseConfig.setEapMethod(selectedItemPosition);
                if (selectedItemPosition != 0) {
                    if (selectedItemPosition == 2) {
                        if (selectedItemPosition2 == 0) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(1);
                        } else if (selectedItemPosition2 == 1) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(2);
                        } else if (selectedItemPosition2 == 2) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                        } else if (selectedItemPosition2 != 3) {
                            Log.e("WifiConfigController2", "Unknown phase2 method" + selectedItemPosition2);
                        } else {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                        }
                    }
                } else if (selectedItemPosition2 == 0) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                } else if (selectedItemPosition2 == 1) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                } else if (selectedItemPosition2 == 2) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(5);
                } else if (selectedItemPosition2 == 3) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(6);
                } else if (selectedItemPosition2 != 4) {
                    Log.e("WifiConfigController2", "Unknown phase2 method" + selectedItemPosition2);
                } else {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(7);
                }
                if (wifiConfiguration.enterpriseConfig.isAuthenticationSimBased() && this.mActiveSubscriptionInfos.size() > 0) {
                    wifiConfiguration.carrierId = this.mActiveSubscriptionInfos.get(this.mEapSimSpinner.getSelectedItemPosition()).getCarrierId();
                }
                String str = (String) this.mEapCaCertSpinner.getSelectedItem();
                wifiConfiguration.enterpriseConfig.setCaCertificateAliases((String[]) null);
                wifiConfiguration.enterpriseConfig.setCaPath((String) null);
                wifiConfiguration.enterpriseConfig.setDomainSuffixMatch(this.mEapDomainView.getText().toString());
                if (!str.equals(this.mUnspecifiedCertString) && !str.equals(this.mDoNotValidateEapServerString)) {
                    if (str.equals(this.mUseSystemCertsString)) {
                        wifiConfiguration.enterpriseConfig.setCaPath("/system/etc/security/cacerts");
                    } else if (str.equals(this.mMultipleCertSetString)) {
                        WifiEntry wifiEntry2 = this.mWifiEntry;
                        if (wifiEntry2 != null) {
                            if (!wifiEntry2.isSaved()) {
                                Log.e("WifiConfigController2", "Multiple certs can only be set when editing saved network");
                            }
                            wifiConfiguration.enterpriseConfig.setCaCertificateAliases(this.mWifiEntry.getWifiConfiguration().enterpriseConfig.getCaCertificateAliases());
                        }
                    } else {
                        wifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[]{str});
                    }
                }
                if (!(wifiConfiguration.enterpriseConfig.getCaCertificateAliases() == null || wifiConfiguration.enterpriseConfig.getCaPath() == null)) {
                    Log.e("WifiConfigController2", "ca_cert (" + wifiConfiguration.enterpriseConfig.getCaCertificateAliases() + ") and ca_path (" + wifiConfiguration.enterpriseConfig.getCaPath() + ") should not both be non-null");
                }
                if (str.equals(this.mUnspecifiedCertString) || str.equals(this.mDoNotValidateEapServerString)) {
                    wifiConfiguration.enterpriseConfig.setOcsp(0);
                } else {
                    wifiConfiguration.enterpriseConfig.setOcsp(this.mEapOcspSpinner.getSelectedItemPosition());
                }
                String str2 = (String) this.mEapUserCertSpinner.getSelectedItem();
                if (str2.equals(this.mUnspecifiedCertString) || str2.equals(this.mDoNotProvideEapUserCertString)) {
                    str2 = "";
                }
                wifiConfiguration.enterpriseConfig.setClientCertificateAlias(str2);
                if (selectedItemPosition == 4 || selectedItemPosition == 5 || selectedItemPosition == 6) {
                    wifiConfiguration.enterpriseConfig.setIdentity("");
                    wifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                } else if (selectedItemPosition == 3) {
                    wifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                    wifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                } else {
                    wifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                    wifiConfiguration.enterpriseConfig.setAnonymousIdentity(this.mEapAnonymousView.getText().toString());
                }
                if (this.mPasswordView.isShown()) {
                    if (this.mPasswordView.length() > 0) {
                        wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                        break;
                    }
                } else {
                    wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                    break;
                }
                break;
            case 4:
                wifiConfiguration.setSecurityParams(6);
                break;
            case 5:
                wifiConfiguration.setSecurityParams(4);
                if (this.mPasswordView.length() != 0) {
                    String charSequence3 = this.mPasswordView.getText().toString();
                    wifiConfiguration.preSharedKey = '\"' + charSequence3 + '\"';
                    break;
                }
                break;
            case 7:
                wifiConfiguration.allowedProtocols.set(3);
                wifiConfiguration.allowedKeyManagement.set(13);
                if (this.mPasswordView.length() != 0) {
                    if (this.mWapiPskTypeSpinner.getSelectedItemPosition() != 0) {
                        wifiConfiguration.preSharedKey = this.mPasswordView.getText().toString();
                        break;
                    } else {
                        wifiConfiguration.preSharedKey = '\"' + this.mPasswordView.getText().toString() + '\"';
                        break;
                    }
                }
                break;
            case 8:
                wifiConfiguration.allowedProtocols.set(3);
                wifiConfiguration.allowedKeyManagement.set(14);
                wifiConfiguration.enterpriseConfig.setEapMethod(8);
                if (this.mWapiCertSpinner.getSelectedItemPosition() != 0) {
                    wifiConfiguration.enterpriseConfig.setWapiCertSuite((String) this.mWapiCertSpinner.getSelectedItem());
                    break;
                } else {
                    wifiConfiguration.enterpriseConfig.setWapiCertSuite("auto");
                    break;
                }
            default:
                return null;
        }
        IpConfiguration ipConfiguration = new IpConfiguration();
        ipConfiguration.setIpAssignment(this.mIpAssignment);
        ipConfiguration.setProxySettings(this.mProxySettings);
        ipConfiguration.setStaticIpConfiguration(this.mStaticIpConfiguration);
        ipConfiguration.setHttpProxy(this.mHttpProxy);
        wifiConfiguration.setIpConfiguration(ipConfiguration);
        Spinner spinner = this.mMeteredSettingsSpinner;
        if (spinner != null) {
            wifiConfiguration.meteredOverride = spinner.getSelectedItemPosition();
        }
        Spinner spinner2 = this.mPrivacySettingsSpinner;
        if (spinner2 != null) {
            wifiConfiguration.macRandomizationSetting = WifiPrivacyPreferenceController2.translatePrefValueToMacRandomizedValue(spinner2.getSelectedItemPosition());
        }
        return wifiConfiguration;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0077 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean ipAndProxyFieldsAreValid() {
        /*
            r6 = this;
            android.widget.Spinner r0 = r6.mIpSettingsSpinner
            r1 = 1
            if (r0 == 0) goto L_0x000e
            int r0 = r0.getSelectedItemPosition()
            if (r0 != r1) goto L_0x000e
            android.net.IpConfiguration$IpAssignment r0 = android.net.IpConfiguration.IpAssignment.STATIC
            goto L_0x0010
        L_0x000e:
            android.net.IpConfiguration$IpAssignment r0 = android.net.IpConfiguration.IpAssignment.DHCP
        L_0x0010:
            r6.mIpAssignment = r0
            android.net.IpConfiguration$IpAssignment r2 = android.net.IpConfiguration.IpAssignment.STATIC
            r3 = 0
            if (r0 != r2) goto L_0x0025
            android.net.StaticIpConfiguration r0 = new android.net.StaticIpConfiguration
            r0.<init>()
            r6.mStaticIpConfiguration = r0
            int r0 = r6.validateIpConfigFields(r0)
            if (r0 == 0) goto L_0x0025
            return r3
        L_0x0025:
            android.widget.Spinner r0 = r6.mProxySettingsSpinner
            int r0 = r0.getSelectedItemPosition()
            android.net.IpConfiguration$ProxySettings r2 = android.net.IpConfiguration.ProxySettings.NONE
            r6.mProxySettings = r2
            r2 = 0
            r6.mHttpProxy = r2
            if (r0 != r1) goto L_0x0078
            android.widget.TextView r2 = r6.mProxyHostView
            if (r2 == 0) goto L_0x0078
            android.net.IpConfiguration$ProxySettings r0 = android.net.IpConfiguration.ProxySettings.STATIC
            r6.mProxySettings = r0
            java.lang.CharSequence r0 = r2.getText()
            java.lang.String r0 = r0.toString()
            android.widget.TextView r2 = r6.mProxyPortView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            android.widget.TextView r4 = r6.mProxyExclusionListView
            java.lang.CharSequence r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            int r5 = java.lang.Integer.parseInt(r2)     // Catch:{ NumberFormatException -> 0x0061 }
            int r2 = com.android.settings.ProxySelector.validate(r0, r2, r4)     // Catch:{ NumberFormatException -> 0x0062 }
            goto L_0x0064
        L_0x0061:
            r5 = r3
        L_0x0062:
            int r2 = com.android.settings.C1992R$string.proxy_error_invalid_port
        L_0x0064:
            if (r2 != 0) goto L_0x0077
            java.lang.String r2 = ","
            java.lang.String[] r2 = r4.split(r2)
            java.util.List r2 = java.util.Arrays.asList(r2)
            android.net.ProxyInfo r0 = android.net.ProxyInfo.buildDirectProxy(r0, r5, r2)
            r6.mHttpProxy = r0
            goto L_0x009f
        L_0x0077:
            return r3
        L_0x0078:
            r2 = 2
            if (r0 != r2) goto L_0x009f
            android.widget.TextView r0 = r6.mProxyPacView
            if (r0 == 0) goto L_0x009f
            android.net.IpConfiguration$ProxySettings r2 = android.net.IpConfiguration.ProxySettings.PAC
            r6.mProxySettings = r2
            java.lang.CharSequence r0 = r0.getText()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x008e
            return r3
        L_0x008e:
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
            if (r0 != 0) goto L_0x0099
            return r3
        L_0x0099:
            android.net.ProxyInfo r0 = android.net.ProxyInfo.buildPacProxy(r0)
            r6.mHttpProxy = r0
        L_0x009f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController2.ipAndProxyFieldsAreValid():boolean");
    }

    private Inet4Address getIPv4Address(String str) {
        try {
            return (Inet4Address) InetAddresses.parseNumericAddress(str);
        } catch (ClassCastException | IllegalArgumentException unused) {
            return null;
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0077 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int validateIpConfigFields(android.net.StaticIpConfiguration r7) {
        /*
            r6 = this;
            android.widget.TextView r0 = r6.mIpAddressView
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            java.lang.CharSequence r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x0017
            int r6 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_ip_address
            return r6
        L_0x0017:
            java.net.Inet4Address r0 = r6.getIPv4Address(r0)
            if (r0 == 0) goto L_0x012f
            java.net.InetAddress r2 = java.net.Inet4Address.ANY
            boolean r2 = r0.equals(r2)
            if (r2 == 0) goto L_0x0027
            goto L_0x012f
        L_0x0027:
            android.net.StaticIpConfiguration$Builder r2 = new android.net.StaticIpConfiguration$Builder
            r2.<init>()
            java.util.List r3 = r7.getDnsServers()
            android.net.StaticIpConfiguration$Builder r2 = r2.setDnsServers(r3)
            java.lang.String r3 = r7.getDomains()
            android.net.StaticIpConfiguration$Builder r2 = r2.setDomains(r3)
            java.net.InetAddress r3 = r7.getGateway()
            android.net.StaticIpConfiguration$Builder r2 = r2.setGateway(r3)
            android.net.LinkAddress r7 = r7.getIpAddress()
            android.net.StaticIpConfiguration$Builder r7 = r2.setIpAddress(r7)
            r2 = -1
            android.widget.TextView r3 = r6.mNetworkPrefixLengthView     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            java.lang.CharSequence r3 = r3.getText()     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            java.lang.String r3 = r3.toString()     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            int r2 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            if (r2 < 0) goto L_0x006b
            r3 = 32
            if (r2 <= r3) goto L_0x0062
            goto L_0x006b
        L_0x0062:
            android.net.LinkAddress r3 = new android.net.LinkAddress     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            r3.<init>(r0, r2)     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            r7.setIpAddress(r3)     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
            goto L_0x008b
        L_0x006b:
            int r0 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_network_prefix_length     // Catch:{ NumberFormatException -> 0x007a, IllegalArgumentException -> 0x0077 }
        L_0x006d:
            android.net.StaticIpConfiguration r7 = r7.build()
            r6.mStaticIpConfiguration = r7
            return r0
        L_0x0074:
            r0 = move-exception
            goto L_0x0128
        L_0x0077:
            int r0 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_ip_address     // Catch:{ all -> 0x0074 }
            goto L_0x006d
        L_0x007a:
            android.widget.TextView r3 = r6.mNetworkPrefixLengthView     // Catch:{ all -> 0x0074 }
            com.android.settings.wifi.WifiConfigUiBase2 r4 = r6.mConfigUi     // Catch:{ all -> 0x0074 }
            android.content.Context r4 = r4.getContext()     // Catch:{ all -> 0x0074 }
            int r5 = com.android.settings.C1992R$string.wifi_network_prefix_length_hint     // Catch:{ all -> 0x0074 }
            java.lang.String r4 = r4.getString(r5)     // Catch:{ all -> 0x0074 }
            r3.setText(r4)     // Catch:{ all -> 0x0074 }
        L_0x008b:
            android.widget.TextView r3 = r6.mGatewayView     // Catch:{ all -> 0x0074 }
            java.lang.CharSequence r3 = r3.getText()     // Catch:{ all -> 0x0074 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0074 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0074 }
            if (r4 == 0) goto L_0x00b6
            java.net.InetAddress r0 = com.android.net.module.util.NetUtils.getNetworkPart(r0, r2)     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            byte[] r0 = r0.getAddress()     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            int r2 = r0.length     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            r3 = 1
            int r2 = r2 - r3
            r0[r2] = r3     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            android.widget.TextView r2 = r6.mGatewayView     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            java.net.InetAddress r0 = java.net.InetAddress.getByAddress(r0)     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            java.lang.String r0 = r0.getHostAddress()     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            r2.setText(r0)     // Catch:{ RuntimeException | UnknownHostException -> 0x00cb }
            goto L_0x00cb
        L_0x00b6:
            java.net.Inet4Address r0 = r6.getIPv4Address(r3)     // Catch:{ all -> 0x0074 }
            if (r0 != 0) goto L_0x00bf
            int r0 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_gateway     // Catch:{ all -> 0x0074 }
            goto L_0x006d
        L_0x00bf:
            boolean r2 = r0.isMulticastAddress()     // Catch:{ all -> 0x0074 }
            if (r2 == 0) goto L_0x00c8
            int r0 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_gateway     // Catch:{ all -> 0x0074 }
            goto L_0x006d
        L_0x00c8:
            r7.setGateway(r0)     // Catch:{ all -> 0x0074 }
        L_0x00cb:
            android.widget.TextView r0 = r6.mDns1View     // Catch:{ all -> 0x0074 }
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ all -> 0x0074 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0074 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x0074 }
            r2.<init>()     // Catch:{ all -> 0x0074 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0074 }
            if (r3 == 0) goto L_0x00f2
            android.widget.TextView r0 = r6.mDns1View     // Catch:{ all -> 0x0074 }
            com.android.settings.wifi.WifiConfigUiBase2 r3 = r6.mConfigUi     // Catch:{ all -> 0x0074 }
            android.content.Context r3 = r3.getContext()     // Catch:{ all -> 0x0074 }
            int r4 = com.android.settings.C1992R$string.wifi_dns1_hint     // Catch:{ all -> 0x0074 }
            java.lang.String r3 = r3.getString(r4)     // Catch:{ all -> 0x0074 }
            r0.setText(r3)     // Catch:{ all -> 0x0074 }
            goto L_0x00ff
        L_0x00f2:
            java.net.Inet4Address r0 = r6.getIPv4Address(r0)     // Catch:{ all -> 0x0074 }
            if (r0 != 0) goto L_0x00fc
            int r0 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_dns     // Catch:{ all -> 0x0074 }
            goto L_0x006d
        L_0x00fc:
            r2.add(r0)     // Catch:{ all -> 0x0074 }
        L_0x00ff:
            android.widget.TextView r0 = r6.mDns2View     // Catch:{ all -> 0x0074 }
            int r0 = r0.length()     // Catch:{ all -> 0x0074 }
            if (r0 <= 0) goto L_0x011e
            android.widget.TextView r0 = r6.mDns2View     // Catch:{ all -> 0x0074 }
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ all -> 0x0074 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0074 }
            java.net.Inet4Address r0 = r6.getIPv4Address(r0)     // Catch:{ all -> 0x0074 }
            if (r0 != 0) goto L_0x011b
            int r0 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_dns     // Catch:{ all -> 0x0074 }
            goto L_0x006d
        L_0x011b:
            r2.add(r0)     // Catch:{ all -> 0x0074 }
        L_0x011e:
            r7.setDnsServers(r2)     // Catch:{ all -> 0x0074 }
            android.net.StaticIpConfiguration r7 = r7.build()
            r6.mStaticIpConfiguration = r7
            return r1
        L_0x0128:
            android.net.StaticIpConfiguration r7 = r7.build()
            r6.mStaticIpConfiguration = r7
            throw r0
        L_0x012f:
            int r6 = com.android.settings.C1992R$string.wifi_ip_settings_invalid_ip_address
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController2.validateIpConfigFields(android.net.StaticIpConfiguration):int");
    }

    /* access modifiers changed from: protected */
    public void showSecurityFields(boolean z, boolean z2) {
        boolean z3;
        WifiEntry wifiEntry;
        int i = this.mWifiEntrySecurity;
        if (i == 0 || i == 4) {
            this.mView.findViewById(C1985R$id.security_fields).setVisibility(8);
            return;
        }
        this.mView.findViewById(C1985R$id.security_fields).setVisibility(0);
        if (this.mPasswordView == null) {
            TextView textView = (TextView) this.mView.findViewById(C1985R$id.password);
            this.mPasswordView = textView;
            textView.addTextChangedListener(this);
            this.mPasswordView.setOnEditorActionListener(this);
            this.mPasswordView.setOnKeyListener(this);
            ((CheckBox) this.mView.findViewById(C1985R$id.show_password)).setOnCheckedChangeListener(this);
            WifiEntry wifiEntry2 = this.mWifiEntry;
            if (wifiEntry2 != null && wifiEntry2.isSaved()) {
                this.mPasswordView.setHint(C1992R$string.wifi_unchanged);
            }
        }
        if (this.mWifiEntrySecurity != 7) {
            this.mView.findViewById(C1985R$id.wapi_psk).setVisibility(8);
        } else {
            this.mView.findViewById(C1985R$id.wapi_psk).setVisibility(0);
            this.mWapiPskTypeSpinner = (Spinner) this.mView.findViewById(C1985R$id.wapi_psk_type);
            WifiEntry wifiEntry3 = this.mWifiEntry;
            if (wifiEntry3 != null && wifiEntry3.isSaved()) {
                this.mWifiEntry.getWifiConfiguration();
            }
            this.mWapiPskTypeSpinner.setOnItemSelectedListener(this);
        }
        if (this.mWifiEntrySecurity != 8) {
            this.mView.findViewById(C1985R$id.wapi_cert).setVisibility(8);
            this.mView.findViewById(C1985R$id.password_layout).setVisibility(0);
            this.mView.findViewById(C1985R$id.show_password_layout).setVisibility(0);
            int i2 = this.mWifiEntrySecurity;
            if (i2 == 3 || i2 == 9 || i2 == 6) {
                this.mView.findViewById(C1985R$id.eap).setVisibility(0);
                if (this.mEapMethodSpinner == null) {
                    Spinner spinner = (Spinner) this.mView.findViewById(C1985R$id.method);
                    this.mEapMethodSpinner = spinner;
                    spinner.setOnItemSelectedListener(this);
                    this.mEapSimSpinner = (Spinner) this.mView.findViewById(C1985R$id.sim);
                    Spinner spinner2 = (Spinner) this.mView.findViewById(C1985R$id.phase2);
                    this.mPhase2Spinner = spinner2;
                    spinner2.setOnItemSelectedListener(this);
                    Spinner spinner3 = (Spinner) this.mView.findViewById(C1985R$id.ca_cert);
                    this.mEapCaCertSpinner = spinner3;
                    spinner3.setOnItemSelectedListener(this);
                    this.mEapOcspSpinner = (Spinner) this.mView.findViewById(C1985R$id.ocsp);
                    TextView textView2 = (TextView) this.mView.findViewById(C1985R$id.domain);
                    this.mEapDomainView = textView2;
                    textView2.addTextChangedListener(this);
                    Spinner spinner4 = (Spinner) this.mView.findViewById(C1985R$id.user_cert);
                    this.mEapUserCertSpinner = spinner4;
                    spinner4.setOnItemSelectedListener(this);
                    this.mEapIdentityView = (TextView) this.mView.findViewById(C1985R$id.identity);
                    this.mEapAnonymousView = (TextView) this.mView.findViewById(C1985R$id.anonymous);
                    setAccessibilityDelegateForSecuritySpinners();
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (z) {
                    if (this.mWifiEntrySecurity == 6) {
                        this.mEapMethodSpinner.setAdapter(getSpinnerAdapter(C1978R$array.wifi_eap_method));
                        this.mEapMethodSpinner.setSelection(1);
                        this.mEapMethodSpinner.setEnabled(false);
                    } else if (Utils.isWifiOnly(this.mContext) || !this.mContext.getResources().getBoolean(17891556)) {
                        this.mEapMethodSpinner.setAdapter(getSpinnerAdapter(C1978R$array.eap_method_without_sim_auth));
                        this.mEapMethodSpinner.setEnabled(true);
                    } else {
                        this.mEapMethodSpinner.setAdapter(getSpinnerAdapterWithEapMethodsTts(C1978R$array.wifi_eap_method));
                        this.mEapMethodSpinner.setEnabled(true);
                    }
                }
                if (z2) {
                    loadSims();
                    AndroidKeystoreAliasLoader androidKeystoreAliasLoader = getAndroidKeystoreAliasLoader();
                    loadCertificates(this.mEapCaCertSpinner, androidKeystoreAliasLoader.getCaCertAliases(), this.mDoNotValidateEapServerString, false, true);
                    loadCertificates(this.mEapUserCertSpinner, androidKeystoreAliasLoader.getKeyCertAliases(), this.mDoNotProvideEapUserCertString, false, false);
                    setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
                }
                if (!z3 || (wifiEntry = this.mWifiEntry) == null || !wifiEntry.isSaved()) {
                    showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
                    return;
                }
                WifiConfiguration wifiConfiguration = this.mWifiEntry.getWifiConfiguration();
                WifiEnterpriseConfig wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig;
                int eapMethod = wifiEnterpriseConfig.getEapMethod();
                int phase2Method = wifiEnterpriseConfig.getPhase2Method();
                this.mEapMethodSpinner.setSelection(eapMethod);
                this.mLastShownEapMethod = eapMethod;
                showEapFieldsByMethod(eapMethod);
                if (eapMethod != 0) {
                    if (eapMethod == 2) {
                        if (phase2Method == 1) {
                            this.mPhase2Spinner.setSelection(0);
                        } else if (phase2Method == 2) {
                            this.mPhase2Spinner.setSelection(1);
                        } else if (phase2Method == 3) {
                            this.mPhase2Spinner.setSelection(2);
                        } else if (phase2Method != 4) {
                            Log.e("WifiConfigController2", "Invalid phase 2 method " + phase2Method);
                        } else {
                            this.mPhase2Spinner.setSelection(3);
                        }
                    }
                } else if (phase2Method == 3) {
                    this.mPhase2Spinner.setSelection(0);
                } else if (phase2Method == 4) {
                    this.mPhase2Spinner.setSelection(1);
                } else if (phase2Method == 5) {
                    this.mPhase2Spinner.setSelection(2);
                } else if (phase2Method == 6) {
                    this.mPhase2Spinner.setSelection(3);
                } else if (phase2Method != 7) {
                    Log.e("WifiConfigController2", "Invalid phase 2 method " + phase2Method);
                } else {
                    this.mPhase2Spinner.setSelection(4);
                }
                if (wifiEnterpriseConfig.isAuthenticationSimBased()) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= this.mActiveSubscriptionInfos.size()) {
                            break;
                        } else if (wifiConfiguration.carrierId == this.mActiveSubscriptionInfos.get(i3).getCarrierId()) {
                            this.mEapSimSpinner.setSelection(i3);
                            break;
                        } else {
                            i3++;
                        }
                    }
                }
                if (!TextUtils.isEmpty(wifiEnterpriseConfig.getCaPath())) {
                    setSelection(this.mEapCaCertSpinner, this.mUseSystemCertsString);
                } else {
                    String[] caCertificateAliases = wifiEnterpriseConfig.getCaCertificateAliases();
                    if (caCertificateAliases == null) {
                        setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
                    } else if (caCertificateAliases.length == 1) {
                        setSelection(this.mEapCaCertSpinner, caCertificateAliases[0]);
                    } else {
                        loadCertificates(this.mEapCaCertSpinner, getAndroidKeystoreAliasLoader().getCaCertAliases(), this.mDoNotValidateEapServerString, true, true);
                        setSelection(this.mEapCaCertSpinner, this.mMultipleCertSetString);
                    }
                }
                this.mEapOcspSpinner.setSelection(wifiEnterpriseConfig.getOcsp());
                this.mEapDomainView.setText(wifiEnterpriseConfig.getDomainSuffixMatch());
                String clientCertificateAlias = wifiEnterpriseConfig.getClientCertificateAlias();
                if (TextUtils.isEmpty(clientCertificateAlias)) {
                    setSelection(this.mEapUserCertSpinner, this.mDoNotProvideEapUserCertString);
                } else {
                    setSelection(this.mEapUserCertSpinner, clientCertificateAlias);
                }
                this.mEapIdentityView.setText(wifiEnterpriseConfig.getIdentity());
                this.mEapAnonymousView.setText(wifiEnterpriseConfig.getAnonymousIdentity());
                return;
            }
            this.mView.findViewById(C1985R$id.eap).setVisibility(8);
            return;
        }
        this.mView.findViewById(C1985R$id.password_layout).setVisibility(8);
        this.mView.findViewById(C1985R$id.show_password_layout).setVisibility(8);
        this.mView.findViewById(C1985R$id.eap).setVisibility(8);
        this.mView.findViewById(C1985R$id.wapi_cert).setVisibility(0);
        Spinner spinner5 = (Spinner) this.mView.findViewById(C1985R$id.wapi_cert_select);
        this.mWapiCertSpinner = spinner5;
        loadWapiCertificates(spinner5);
        WifiEntry wifiEntry4 = this.mWifiEntry;
        if (wifiEntry4 != null && wifiEntry4.isSaved()) {
            WifiConfiguration wifiConfiguration2 = this.mWifiEntry.getWifiConfiguration();
            if (wifiConfiguration2.enterpriseConfig.getWapiCertSuite().equals("auto")) {
                Log.d("WifiConfigController2", "Read WAPI_CERT sel cert Mode: " + wifiConfiguration2.enterpriseConfig.getWapiCertSuite());
                this.mWapiCertSpinner.setSelection(0);
                return;
            }
            Log.d("WifiConfigController2", "Read WAPI_CERT sel cert name: " + wifiConfiguration2.enterpriseConfig.getWapiCertSuite());
            setSelection(this.mWapiCertSpinner, wifiConfiguration2.enterpriseConfig.getWapiCertSuite());
        }
    }

    private void setAccessibilityDelegateForSecuritySpinners() {
        C13951 r0 = new View.AccessibilityDelegate() {
            public void sendAccessibilityEvent(View view, int i) {
                if (i != 4) {
                    super.sendAccessibilityEvent(view, i);
                }
            }
        };
        this.mEapMethodSpinner.setAccessibilityDelegate(r0);
        this.mPhase2Spinner.setAccessibilityDelegate(r0);
        this.mEapCaCertSpinner.setAccessibilityDelegate(r0);
        this.mEapOcspSpinner.setAccessibilityDelegate(r0);
        this.mEapUserCertSpinner.setAccessibilityDelegate(r0);
    }

    private void showEapFieldsByMethod(int i) {
        this.mView.findViewById(C1985R$id.l_method).setVisibility(0);
        this.mView.findViewById(C1985R$id.l_identity).setVisibility(0);
        this.mView.findViewById(C1985R$id.l_domain).setVisibility(0);
        View view = this.mView;
        int i2 = C1985R$id.l_ca_cert;
        view.findViewById(i2).setVisibility(0);
        this.mView.findViewById(C1985R$id.l_ocsp).setVisibility(0);
        this.mView.findViewById(C1985R$id.password_layout).setVisibility(0);
        this.mView.findViewById(C1985R$id.show_password_layout).setVisibility(0);
        View view2 = this.mView;
        int i3 = C1985R$id.l_sim;
        view2.findViewById(i3).setVisibility(0);
        this.mConfigUi.getContext();
        switch (i) {
            case 0:
                ArrayAdapter<CharSequence> arrayAdapter = this.mPhase2Adapter;
                ArrayAdapter<CharSequence> arrayAdapter2 = this.mPhase2PeapAdapter;
                if (arrayAdapter != arrayAdapter2) {
                    this.mPhase2Adapter = arrayAdapter2;
                    this.mPhase2Spinner.setAdapter(arrayAdapter2);
                }
                this.mView.findViewById(C1985R$id.l_phase2).setVisibility(0);
                this.mView.findViewById(C1985R$id.l_anonymous).setVisibility(0);
                showPeapFields();
                setUserCertInvisible();
                break;
            case 1:
                this.mView.findViewById(C1985R$id.l_user_cert).setVisibility(0);
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setPasswordInvisible();
                this.mView.findViewById(i3).setVisibility(8);
                break;
            case 2:
                ArrayAdapter<CharSequence> arrayAdapter3 = this.mPhase2Adapter;
                ArrayAdapter<CharSequence> arrayAdapter4 = this.mPhase2TtlsAdapter;
                if (arrayAdapter3 != arrayAdapter4) {
                    this.mPhase2Adapter = arrayAdapter4;
                    this.mPhase2Spinner.setAdapter(arrayAdapter4);
                }
                this.mView.findViewById(C1985R$id.l_phase2).setVisibility(0);
                this.mView.findViewById(C1985R$id.l_anonymous).setVisibility(0);
                setUserCertInvisible();
                this.mView.findViewById(i3).setVisibility(8);
                break;
            case 3:
                setPhase2Invisible();
                setCaCertInvisible();
                setOcspInvisible();
                setDomainInvisible();
                setAnonymousIdentInvisible();
                setUserCertInvisible();
                this.mView.findViewById(i3).setVisibility(8);
                break;
            case 4:
            case 5:
            case 6:
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setCaCertInvisible();
                setOcspInvisible();
                setDomainInvisible();
                setUserCertInvisible();
                setPasswordInvisible();
                setIdentityInvisible();
                break;
        }
        if (this.mView.findViewById(i2).getVisibility() != 8) {
            String str = (String) this.mEapCaCertSpinner.getSelectedItem();
            if (str.equals(this.mDoNotValidateEapServerString) || str.equals(this.mUnspecifiedCertString)) {
                setDomainInvisible();
                setOcspInvisible();
            }
        }
    }

    private void showPeapFields() {
        int selectedItemPosition = this.mPhase2Spinner.getSelectedItemPosition();
        if (selectedItemPosition == 2 || selectedItemPosition == 3 || selectedItemPosition == 4) {
            this.mEapIdentityView.setText("");
            this.mView.findViewById(C1985R$id.l_identity).setVisibility(8);
            setPasswordInvisible();
            this.mView.findViewById(C1985R$id.l_sim).setVisibility(0);
            return;
        }
        this.mView.findViewById(C1985R$id.l_identity).setVisibility(0);
        this.mView.findViewById(C1985R$id.l_anonymous).setVisibility(0);
        this.mView.findViewById(C1985R$id.password_layout).setVisibility(0);
        this.mView.findViewById(C1985R$id.show_password_layout).setVisibility(0);
        this.mView.findViewById(C1985R$id.l_sim).setVisibility(8);
    }

    private void setIdentityInvisible() {
        this.mView.findViewById(C1985R$id.l_identity).setVisibility(8);
    }

    private void setPhase2Invisible() {
        this.mView.findViewById(C1985R$id.l_phase2).setVisibility(8);
    }

    private void setCaCertInvisible() {
        this.mView.findViewById(C1985R$id.l_ca_cert).setVisibility(8);
        setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
    }

    private void setOcspInvisible() {
        this.mView.findViewById(C1985R$id.l_ocsp).setVisibility(8);
        this.mEapOcspSpinner.setSelection(0);
    }

    private void setDomainInvisible() {
        this.mView.findViewById(C1985R$id.l_domain).setVisibility(8);
        this.mEapDomainView.setText("");
    }

    private void setUserCertInvisible() {
        this.mView.findViewById(C1985R$id.l_user_cert).setVisibility(8);
        setSelection(this.mEapUserCertSpinner, this.mUnspecifiedCertString);
    }

    private void setAnonymousIdentInvisible() {
        this.mView.findViewById(C1985R$id.l_anonymous).setVisibility(8);
        this.mEapAnonymousView.setText("");
    }

    private void setPasswordInvisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(C1985R$id.password_layout).setVisibility(8);
        this.mView.findViewById(C1985R$id.show_password_layout).setVisibility(8);
    }

    private void showIpConfigFields() {
        StaticIpConfiguration staticIpConfiguration;
        this.mView.findViewById(C1985R$id.ip_fields).setVisibility(0);
        WifiEntry wifiEntry = this.mWifiEntry;
        WifiConfiguration wifiConfiguration = (wifiEntry == null || !wifiEntry.isSaved()) ? null : this.mWifiEntry.getWifiConfiguration();
        if (this.mIpSettingsSpinner.getSelectedItemPosition() == 1) {
            this.mView.findViewById(C1985R$id.staticip).setVisibility(0);
            if (this.mIpAddressView == null) {
                TextView textView = (TextView) this.mView.findViewById(C1985R$id.ipaddress);
                this.mIpAddressView = textView;
                textView.addTextChangedListener(this);
                TextView textView2 = (TextView) this.mView.findViewById(C1985R$id.gateway);
                this.mGatewayView = textView2;
                textView2.addTextChangedListener(getIpConfigFieldsTextWatcher(textView2));
                TextView textView3 = (TextView) this.mView.findViewById(C1985R$id.network_prefix_length);
                this.mNetworkPrefixLengthView = textView3;
                textView3.addTextChangedListener(getIpConfigFieldsTextWatcher(textView3));
                TextView textView4 = (TextView) this.mView.findViewById(C1985R$id.dns1);
                this.mDns1View = textView4;
                textView4.addTextChangedListener(getIpConfigFieldsTextWatcher(textView4));
                TextView textView5 = (TextView) this.mView.findViewById(C1985R$id.dns2);
                this.mDns2View = textView5;
                textView5.addTextChangedListener(this);
            }
            if (wifiConfiguration != null && (staticIpConfiguration = wifiConfiguration.getIpConfiguration().getStaticIpConfiguration()) != null) {
                if (staticIpConfiguration.getIpAddress() != null) {
                    this.mIpAddressView.setText(staticIpConfiguration.getIpAddress().getAddress().getHostAddress());
                    this.mNetworkPrefixLengthView.setText(Integer.toString(staticIpConfiguration.getIpAddress().getPrefixLength()));
                }
                if (staticIpConfiguration.getGateway() != null) {
                    this.mGatewayView.setText(staticIpConfiguration.getGateway().getHostAddress());
                }
                Iterator it = staticIpConfiguration.getDnsServers().iterator();
                if (it.hasNext()) {
                    this.mDns1View.setText(((InetAddress) it.next()).getHostAddress());
                }
                if (it.hasNext()) {
                    this.mDns2View.setText(((InetAddress) it.next()).getHostAddress());
                    return;
                }
                return;
            }
            return;
        }
        this.mView.findViewById(C1985R$id.staticip).setVisibility(8);
    }

    private void showProxyFields() {
        ProxyInfo httpProxy;
        ProxyInfo httpProxy2;
        this.mView.findViewById(C1985R$id.proxy_settings_fields).setVisibility(0);
        WifiEntry wifiEntry = this.mWifiEntry;
        WifiConfiguration wifiConfiguration = (wifiEntry == null || !wifiEntry.isSaved()) ? null : this.mWifiEntry.getWifiConfiguration();
        if (this.mProxySettingsSpinner.getSelectedItemPosition() == 1) {
            setVisibility(C1985R$id.proxy_warning_limited_support, 0);
            setVisibility(C1985R$id.proxy_fields, 0);
            setVisibility(C1985R$id.proxy_pac_field, 8);
            if (this.mProxyHostView == null) {
                TextView textView = (TextView) this.mView.findViewById(C1985R$id.proxy_hostname);
                this.mProxyHostView = textView;
                textView.addTextChangedListener(this);
                TextView textView2 = (TextView) this.mView.findViewById(C1985R$id.proxy_port);
                this.mProxyPortView = textView2;
                textView2.addTextChangedListener(this);
                TextView textView3 = (TextView) this.mView.findViewById(C1985R$id.proxy_exclusionlist);
                this.mProxyExclusionListView = textView3;
                textView3.addTextChangedListener(this);
            }
            if (wifiConfiguration != null && (httpProxy2 = wifiConfiguration.getHttpProxy()) != null) {
                this.mProxyHostView.setText(httpProxy2.getHost());
                this.mProxyPortView.setText(Integer.toString(httpProxy2.getPort()));
                this.mProxyExclusionListView.setText(ProxyUtils.exclusionListAsString(httpProxy2.getExclusionList()));
            }
        } else if (this.mProxySettingsSpinner.getSelectedItemPosition() == 2) {
            setVisibility(C1985R$id.proxy_warning_limited_support, 8);
            setVisibility(C1985R$id.proxy_fields, 8);
            setVisibility(C1985R$id.proxy_pac_field, 0);
            if (this.mProxyPacView == null) {
                TextView textView4 = (TextView) this.mView.findViewById(C1985R$id.proxy_pac);
                this.mProxyPacView = textView4;
                textView4.addTextChangedListener(this);
            }
            if (wifiConfiguration != null && (httpProxy = wifiConfiguration.getHttpProxy()) != null) {
                this.mProxyPacView.setText(httpProxy.getPacFileUrl().toString());
            }
        } else {
            setVisibility(C1985R$id.proxy_warning_limited_support, 8);
            setVisibility(C1985R$id.proxy_fields, 8);
            setVisibility(C1985R$id.proxy_pac_field, 8);
        }
    }

    private void setVisibility(int i, int i2) {
        View findViewById = this.mView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(i2);
        }
    }

    /* access modifiers changed from: package-private */
    public AndroidKeystoreAliasLoader getAndroidKeystoreAliasLoader() {
        return new AndroidKeystoreAliasLoader(102);
    }

    /* access modifiers changed from: package-private */
    public void loadSims() {
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
        this.mActiveSubscriptionInfos = activeSubscriptionInfoList;
        if (activeSubscriptionInfoList == null) {
            this.mActiveSubscriptionInfos = Collections.EMPTY_LIST;
        }
        if (this.mActiveSubscriptionInfos.size() == 0) {
            this.mEapSimSpinner.setAdapter(getSpinnerAdapter(new String[]{this.mContext.getString(C1992R$string.wifi_no_sim_card)}));
            this.mEapSimSpinner.setSelection(0);
            this.mEapSimSpinner.setEnabled(false);
            return;
        }
        String[] strArr = (String[]) this.mActiveSubscriptionInfos.stream().map(new WifiConfigController2$$ExternalSyntheticLambda1(this)).toArray(WifiConfigController2$$ExternalSyntheticLambda2.INSTANCE);
        this.mEapSimSpinner.setAdapter(getSpinnerAdapter(strArr));
        this.mEapSimSpinner.setSelection(0);
        if (strArr.length == 1) {
            this.mEapSimSpinner.setEnabled(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$loadSims$0(SubscriptionInfo subscriptionInfo) {
        return SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$loadSims$1(int i) {
        return new String[i];
    }

    /* access modifiers changed from: package-private */
    public void loadCertificates(Spinner spinner, Collection<String> collection, String str, boolean z, boolean z2) {
        this.mConfigUi.getContext();
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mUnspecifiedCertString);
        if (z) {
            arrayList.add(this.mMultipleCertSetString);
        }
        if (z2) {
            arrayList.add(this.mUseSystemCertsString);
            arrayList.add(this.mInstallCertsString);
        }
        if (!(collection == null || collection.size() == 0)) {
            arrayList.addAll((Collection) collection.stream().filter(WifiConfigController2$$ExternalSyntheticLambda3.INSTANCE).collect(Collectors.toList()));
        }
        if (!TextUtils.isEmpty(str) && this.mWifiEntrySecurity != 6) {
            arrayList.add(str);
        }
        if (arrayList.size() == 2) {
            arrayList.remove(this.mUnspecifiedCertString);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        spinner.setAdapter(getSpinnerAdapter((String[]) arrayList.toArray(new String[arrayList.size()])));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$loadCertificates$2(String str) {
        for (String startsWith : UNDESIRED_CERTIFICATES) {
            if (str.startsWith(startsWith)) {
                return false;
            }
        }
        return true;
    }

    private void loadWapiCertificates(Spinner spinner) {
        Context context = this.mConfigUi.getContext();
        String string = context.getString(C1992R$string.wifi_unspecified);
        String string2 = context.getString(C1992R$string.wapi_auto_sel_cert);
        ArrayList arrayList = new ArrayList();
        String[] list = LegacyVpnProfileStore.list("WAPI_USER_");
        if (list == null || list.length <= 0) {
            arrayList.add(string);
        } else {
            arrayList.add(string2);
            for (String add : list) {
                arrayList.add(add);
            }
        }
        if (arrayList.size() > 1) {
            this.mHaveWapiCert = true;
        } else {
            this.mHaveWapiCert = false;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, 17367048, (String[]) arrayList.toArray(new String[0]));
        arrayAdapter.setDropDownViewResource(17367049);
        spinner.setAdapter(arrayAdapter);
    }

    private void setSelection(Spinner spinner, String str) {
        if (str != null) {
            ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
            for (int count = arrayAdapter.getCount() - 1; count >= 0; count--) {
                if (str.equals(arrayAdapter.getItem(count))) {
                    spinner.setSelection(count);
                    return;
                }
            }
        }
    }

    public void afterTextChanged(Editable editable) {
        ThreadUtils.postOnMainThread(new WifiConfigController2$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$afterTextChanged$3() {
        showWarningMessagesIfAppropriate();
        enableSubmitIfAppropriate();
    }

    private TextWatcher getIpConfigFieldsTextWatcher(final TextView textView) {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    if (textView.getId() == C1985R$id.gateway) {
                        WifiConfigController2.this.mGatewayView.setHint(C1992R$string.wifi_gateway_hint);
                    } else if (textView.getId() == C1985R$id.network_prefix_length) {
                        WifiConfigController2.this.mNetworkPrefixLengthView.setHint(C1992R$string.wifi_network_prefix_length_hint);
                    } else if (textView.getId() == C1985R$id.dns1) {
                        WifiConfigController2.this.mDns1View.setHint(C1992R$string.wifi_dns1_hint);
                    }
                    Button submitButton = WifiConfigController2.this.mConfigUi.getSubmitButton();
                    if (submitButton != null) {
                        submitButton.setEnabled(false);
                        return;
                    }
                    return;
                }
                ThreadUtils.postOnMainThread(new WifiConfigController2$2$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$afterTextChanged$0() {
                WifiConfigController2.this.showWarningMessagesIfAppropriate();
                WifiConfigController2.this.enableSubmitIfAppropriate();
            }
        };
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (textView != this.mPasswordView || i != 6 || !isSubmittable()) {
            return false;
        }
        this.mConfigUi.dispatchSubmit();
        return true;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (view != this.mPasswordView || i != 66 || !isSubmittable()) {
            return false;
        }
        this.mConfigUi.dispatchSubmit();
        return true;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton.getId() == C1985R$id.show_password) {
            int selectionEnd = this.mPasswordView.getSelectionEnd();
            this.mPasswordView.setInputType((z ? 144 : 128) | 1);
            if (selectionEnd >= 0) {
                ((EditText) this.mPasswordView).setSelection(selectionEnd);
            }
        } else if (compoundButton.getId() == C1985R$id.wifi_advanced_togglebox) {
            hideSoftKeyboard(this.mView.getWindowToken());
            compoundButton.setVisibility(8);
            this.mView.findViewById(C1985R$id.wifi_advanced_fields).setVisibility(0);
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        int i2 = 8;
        if (adapterView == this.mSecuritySpinner) {
            this.mWifiEntrySecurity = this.mSecurityInPosition[i].intValue();
            showSecurityFields(true, true);
            if (WifiDppUtils.isSupportEnrolleeQrCodeScanner(this.mContext, this.mWifiEntrySecurity)) {
                this.mSsidScanButton.setVisibility(0);
            } else {
                this.mSsidScanButton.setVisibility(8);
            }
        } else {
            Spinner spinner = this.mEapMethodSpinner;
            if (adapterView == spinner) {
                int selectedItemPosition = spinner.getSelectedItemPosition();
                if (this.mLastShownEapMethod != selectedItemPosition) {
                    this.mLastShownEapMethod = selectedItemPosition;
                    showSecurityFields(false, true);
                }
            } else if (adapterView == this.mEapCaCertSpinner) {
                if (adapterView.getItemAtPosition(i).toString().equals(this.mInstallCertsString)) {
                    startActivityForInstallCerts();
                }
                showSecurityFields(false, false);
            } else if (adapterView == this.mPhase2Spinner && spinner.getSelectedItemPosition() == 0) {
                showPeapFields();
            } else if (adapterView == this.mProxySettingsSpinner) {
                showProxyFields();
            } else if (adapterView == this.mHiddenSettingsSpinner) {
                TextView textView = this.mHiddenWarningView;
                if (i != 0) {
                    i2 = 0;
                }
                textView.setVisibility(i2);
            } else {
                showIpConfigFields();
            }
        }
        showWarningMessagesIfAppropriate();
        enableSubmitIfAppropriate();
    }

    private void startActivityForInstallCerts() {
        Intent intent = new Intent("android.credentials.INSTALL");
        intent.setFlags(268435456);
        intent.setComponent(new ComponentName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain"));
        intent.putExtra("certificate_install_usage", "wifi");
        this.mContext.startActivity(intent);
    }

    public void updatePassword() {
        ((TextView) this.mView.findViewById(C1985R$id.password)).setInputType((((CheckBox) this.mView.findViewById(C1985R$id.show_password)).isChecked() ? 144 : 128) | 1);
    }

    public WifiEntry getWifiEntry() {
        return this.mWifiEntry;
    }

    private void configureSecuritySpinner() {
        int i;
        int i2;
        this.mConfigUi.setTitle(C1992R$string.wifi_add_network);
        TextView textView = (TextView) this.mView.findViewById(C1985R$id.ssid);
        this.mSsidView = textView;
        textView.addTextChangedListener(this);
        Spinner spinner = (Spinner) this.mView.findViewById(C1985R$id.security);
        this.mSecuritySpinner = spinner;
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, 17367048, 16908308);
        arrayAdapter.setDropDownViewResource(17367049);
        this.mSecuritySpinner.setAdapter(arrayAdapter);
        arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_none));
        this.mSecurityInPosition[0] = 0;
        if (this.mWifiManager.isEnhancedOpenSupported()) {
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_owe));
            this.mSecurityInPosition[1] = 4;
            i = 2;
        } else {
            i = 1;
        }
        arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_wep));
        int i3 = i + 1;
        this.mSecurityInPosition[i] = 1;
        arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_wpa_wpa2));
        int i4 = i3 + 1;
        this.mSecurityInPosition[i3] = 2;
        if (this.mWifiManager.isWpa3SaeSupported()) {
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_sae));
            int i5 = i4 + 1;
            this.mSecurityInPosition[i4] = 5;
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_eap_wpa_wpa2));
            int i6 = i5 + 1;
            this.mSecurityInPosition[i5] = 3;
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_eap_wpa3));
            i2 = i6 + 1;
            this.mSecurityInPosition[i6] = 9;
        } else {
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_eap));
            this.mSecurityInPosition[i4] = 3;
            i2 = i4 + 1;
        }
        if (this.mWifiManager.isWpa3SuiteBSupported()) {
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_eap_suiteb));
            this.mSecurityInPosition[i2] = 6;
            i2++;
        }
        if (this.mWifiManager.isWapiSupported()) {
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_wapi_psk));
            this.mSecurityInPosition[i2] = 7;
            arrayAdapter.add(this.mContext.getString(C1992R$string.wifi_security_wapi_cert));
            this.mSecurityInPosition[i2 + 1] = 8;
        }
        arrayAdapter.notifyDataSetChanged();
        this.mView.findViewById(C1985R$id.type).setVisibility(0);
        showIpConfigFields();
        showProxyFields();
        this.mView.findViewById(C1985R$id.wifi_advanced_toggle).setVisibility(0);
        this.mView.findViewById(C1985R$id.hidden_settings_field).setVisibility(0);
        ((CheckBox) this.mView.findViewById(C1985R$id.wifi_advanced_togglebox)).setOnCheckedChangeListener(this);
        setAdvancedOptionAccessibilityString();
    }

    /* access modifiers changed from: package-private */
    public CharSequence[] findAndReplaceTargetStrings(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, CharSequence[] charSequenceArr3) {
        if (charSequenceArr2.length != charSequenceArr3.length) {
            return charSequenceArr;
        }
        CharSequence[] charSequenceArr4 = new CharSequence[charSequenceArr.length];
        for (int i = 0; i < charSequenceArr.length; i++) {
            charSequenceArr4[i] = charSequenceArr[i];
            for (int i2 = 0; i2 < charSequenceArr2.length; i2++) {
                if (TextUtils.equals(charSequenceArr[i], charSequenceArr2[i2])) {
                    charSequenceArr4[i] = charSequenceArr3[i2];
                }
            }
        }
        return charSequenceArr4;
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int i) {
        return getSpinnerAdapter(this.mContext.getResources().getStringArray(i));
    }

    /* access modifiers changed from: package-private */
    public ArrayAdapter<CharSequence> getSpinnerAdapter(String[] strArr) {
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this.mContext, 17367048, strArr);
        arrayAdapter.setDropDownViewResource(17367049);
        return arrayAdapter;
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapterWithEapMethodsTts(int i) {
        Resources resources = this.mContext.getResources();
        String[] stringArray = resources.getStringArray(i);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this.mContext, 17367048, createAccessibleEntries(stringArray, findAndReplaceTargetStrings(stringArray, resources.getStringArray(C1978R$array.wifi_eap_method_target_strings), resources.getStringArray(C1978R$array.wifi_eap_method_tts_strings))));
        arrayAdapter.setDropDownViewResource(17367049);
        return arrayAdapter;
    }

    private SpannableString[] createAccessibleEntries(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        SpannableString[] spannableStringArr = new SpannableString[charSequenceArr.length];
        for (int i = 0; i < charSequenceArr.length; i++) {
            spannableStringArr[i] = com.android.settings.Utils.createAccessibleSequence(charSequenceArr[i], charSequenceArr2[i].toString());
        }
        return spannableStringArr;
    }

    private void hideSoftKeyboard(IBinder iBinder) {
        ((InputMethodManager) this.mContext.getSystemService(InputMethodManager.class)).hideSoftInputFromWindow(iBinder, 0);
    }

    private void setAdvancedOptionAccessibilityString() {
        ((CheckBox) this.mView.findViewById(C1985R$id.wifi_advanced_togglebox)).setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setCheckable(false);
                accessibilityNodeInfo.setClassName((CharSequence) null);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, WifiConfigController2.this.mContext.getString(C1992R$string.wifi_advanced_toggle_description_collapsed)));
            }
        });
    }

    public int getCurSecurity() {
        return this.mWifiEntrySecurity;
    }

    public boolean checkWapiParam() {
        String string = this.mConfigUi.getContext().getString(C1992R$string.wifi_unchanged);
        int i = this.mWifiEntrySecurity;
        if (i == 8) {
            if (!this.mHaveWapiCert) {
                return false;
            }
        } else if (i == 7) {
            int i2 = WAPI_PSK_TYPE[this.mWapiPskTypeSpinner.getSelectedItemPosition()];
            String charSequence = this.mPasswordView.getText().toString();
            if (string.equals(this.mPasswordView.getHint() != null ? this.mPasswordView.getHint().toString() : "")) {
                return true;
            }
            return checkWapiPassword(charSequence, i2);
        }
        return true;
    }

    private boolean checkWapiPassword(String str, int i) {
        if (str.length() == 0) {
            Toast.makeText(this.mConfigUi.getContext(), C1992R$string.wapi_password_empty, 1).show();
            return false;
        } else if (str.length() < 8 || str.length() > 64) {
            Toast.makeText(this.mConfigUi.getContext(), C1992R$string.wapi_password_length_invalid, 1).show();
            return false;
        } else {
            if (i == 1) {
                int checkHexPasswd = checkHexPasswd(str);
                if (checkHexPasswd == -2) {
                    Toast.makeText(this.mConfigUi.getContext(), C1992R$string.wapi_password_hex_format_invalid, 1).show();
                    return false;
                } else if (checkHexPasswd == -1) {
                    Toast.makeText(this.mConfigUi.getContext(), C1992R$string.wapi_password_hex_length_invalid, 1).show();
                    return false;
                }
            }
            return true;
        }
    }

    private static int checkHexPasswd(String str) {
        if (str.length() % 2 == 1) {
            return -1;
        }
        for (int length = str.length() - 1; length >= 0; length--) {
            char charAt = str.charAt(length);
            if ((charAt < '0' || charAt > '9') && ((charAt < 'A' || charAt > 'F') && (charAt < 'a' || charAt > 'f'))) {
                return -2;
            }
        }
        return 0;
    }
}
