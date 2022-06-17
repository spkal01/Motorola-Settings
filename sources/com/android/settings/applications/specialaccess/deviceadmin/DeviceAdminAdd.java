package com.android.settings.applications.specialaccess.deviceadmin;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DeviceAdminAdd extends CollapsingToolbarBaseActivity {
    Button mActionButton;
    TextView mAddMsg;
    boolean mAddMsgEllipsized = true;
    ImageView mAddMsgExpander;
    String mAddMsgText;
    boolean mAdding;
    boolean mAddingProfileOwner;
    TextView mAdminDescription;
    ImageView mAdminIcon;
    TextView mAdminName;
    ViewGroup mAdminPolicies;
    boolean mAdminPoliciesInitialized;
    TextView mAdminWarning;
    AppOpsManager mAppOps;
    Button mCancelButton;
    DevicePolicyManager mDPM;
    DeviceAdminInfo mDeviceAdmin;
    Handler mHandler;
    boolean mIsCalledFromSupportDialog = false;
    private LayoutInflater mLayoutInflaternflater;
    String mProfileOwnerName;
    TextView mProfileOwnerWarning;
    boolean mRefreshing;
    TextView mSupportMessage;
    private final IBinder mToken = new Binder();
    Button mUninstallButton;
    boolean mUninstalling = false;
    boolean mWaitingForRemoveMsg;

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        r9.activityInfo = r2;
        new android.app.admin.DeviceAdminInfo(r12, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0154, code lost:
        r13 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
            r12 = this;
            java.lang.String r0 = "Bad "
            java.lang.String r1 = "Unable to retrieve device policy "
            super.onCreate(r13)
            android.os.Handler r13 = new android.os.Handler
            android.os.Looper r2 = r12.getMainLooper()
            r13.<init>(r2)
            r12.mHandler = r13
            java.lang.String r13 = "device_policy"
            java.lang.Object r13 = r12.getSystemService(r13)
            android.app.admin.DevicePolicyManager r13 = (android.app.admin.DevicePolicyManager) r13
            r12.mDPM = r13
            java.lang.String r13 = "appops"
            java.lang.Object r13 = r12.getSystemService(r13)
            android.app.AppOpsManager r13 = (android.app.AppOpsManager) r13
            r12.mAppOps = r13
            java.lang.String r13 = "layout_inflater"
            java.lang.Object r13 = r12.getSystemService(r13)
            android.view.LayoutInflater r13 = (android.view.LayoutInflater) r13
            r12.mLayoutInflaternflater = r13
            android.content.pm.PackageManager r13 = r12.getPackageManager()
            android.content.Intent r2 = r12.getIntent()
            int r2 = r2.getFlags()
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            r2 = r2 & r3
            java.lang.String r3 = "DeviceAdminAdd"
            if (r2 == 0) goto L_0x004c
            java.lang.String r13 = "Cannot start ADD_DEVICE_ADMIN as a new task"
            android.util.Log.w(r3, r13)
            r12.finish()
            return
        L_0x004c:
            android.content.Intent r2 = r12.getIntent()
            java.lang.String r4 = "android.app.extra.CALLED_FROM_SUPPORT_DIALOG"
            r5 = 0
            boolean r2 = r2.getBooleanExtra(r4, r5)
            r12.mIsCalledFromSupportDialog = r2
            android.content.Intent r2 = r12.getIntent()
            java.lang.String r2 = r2.getAction()
            android.content.Intent r4 = r12.getIntent()
            java.lang.String r6 = "android.app.extra.DEVICE_ADMIN"
            android.os.Parcelable r4 = r4.getParcelableExtra(r6)
            android.content.ComponentName r4 = (android.content.ComponentName) r4
            r6 = 1
            if (r4 != 0) goto L_0x00a4
            android.content.Intent r4 = r12.getIntent()
            java.lang.String r7 = "android.app.extra.DEVICE_ADMIN_PACKAGE_NAME"
            java.lang.String r4 = r4.getStringExtra(r7)
            java.util.Optional r4 = r12.findAdminWithPackageName(r4)
            boolean r7 = r4.isPresent()
            if (r7 != 0) goto L_0x009c
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "No component specified in "
            r13.append(r0)
            r13.append(r2)
            java.lang.String r13 = r13.toString()
            android.util.Log.w(r3, r13)
            r12.finish()
            return
        L_0x009c:
            java.lang.Object r4 = r4.get()
            android.content.ComponentName r4 = (android.content.ComponentName) r4
            r12.mUninstalling = r6
        L_0x00a4:
            if (r2 == 0) goto L_0x0108
            java.lang.String r7 = "android.app.action.SET_PROFILE_OWNER"
            boolean r2 = r2.equals(r7)
            if (r2 == 0) goto L_0x0108
            r12.setResult(r5)
            r12.setFinishOnTouchOutside(r6)
            r12.mAddingProfileOwner = r6
            android.content.Intent r2 = r12.getIntent()
            java.lang.String r7 = "android.app.extra.PROFILE_OWNER_NAME"
            java.lang.String r2 = r2.getStringExtra(r7)
            r12.mProfileOwnerName = r2
            java.lang.String r2 = r12.getCallingPackage()
            if (r2 == 0) goto L_0x00ff
            java.lang.String r7 = r4.getPackageName()
            boolean r7 = r2.equals(r7)
            if (r7 != 0) goto L_0x00d3
            goto L_0x00ff
        L_0x00d3:
            android.content.pm.PackageInfo r7 = r13.getPackageInfo(r2, r5)     // Catch:{ NameNotFoundException -> 0x00e7 }
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo     // Catch:{ NameNotFoundException -> 0x00e7 }
            int r7 = r7.flags     // Catch:{ NameNotFoundException -> 0x00e7 }
            r7 = r7 & r6
            if (r7 != 0) goto L_0x0108
            java.lang.String r13 = "Cannot set a non-system app as a profile owner"
            android.util.Log.e(r3, r13)     // Catch:{ NameNotFoundException -> 0x00e7 }
            r12.finish()     // Catch:{ NameNotFoundException -> 0x00e7 }
            return
        L_0x00e7:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "Cannot find the package "
            r13.append(r0)
            r13.append(r2)
            java.lang.String r13 = r13.toString()
            android.util.Log.e(r3, r13)
            r12.finish()
            return
        L_0x00ff:
            java.lang.String r13 = "Unknown or incorrect caller"
            android.util.Log.e(r3, r13)
            r12.finish()
            return
        L_0x0108:
            r2 = 128(0x80, float:1.794E-43)
            android.content.pm.ActivityInfo r2 = r13.getReceiverInfo(r4, r2)     // Catch:{ NameNotFoundException -> 0x03d9 }
            android.app.admin.DevicePolicyManager r7 = r12.mDPM
            boolean r7 = r7.isAdminActive(r4)
            if (r7 != 0) goto L_0x01a0
            android.content.Intent r7 = new android.content.Intent
            java.lang.String r8 = "android.app.action.DEVICE_ADMIN_ENABLED"
            r7.<init>(r8)
            r8 = 32768(0x8000, float:4.5918E-41)
            java.util.List r13 = r13.queryBroadcastReceivers(r7, r8)
            if (r13 != 0) goto L_0x0128
            r7 = r5
            goto L_0x012c
        L_0x0128:
            int r7 = r13.size()
        L_0x012c:
            r8 = r5
        L_0x012d:
            if (r8 >= r7) goto L_0x0185
            java.lang.Object r9 = r13.get(r8)
            android.content.pm.ResolveInfo r9 = (android.content.pm.ResolveInfo) r9
            java.lang.String r10 = r2.packageName
            android.content.pm.ActivityInfo r11 = r9.activityInfo
            java.lang.String r11 = r11.packageName
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x0182
            java.lang.String r10 = r2.name
            android.content.pm.ActivityInfo r11 = r9.activityInfo
            java.lang.String r11 = r11.name
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x0182
            r9.activityInfo = r2     // Catch:{ XmlPullParserException -> 0x016c, IOException -> 0x0156 }
            android.app.admin.DeviceAdminInfo r13 = new android.app.admin.DeviceAdminInfo     // Catch:{ XmlPullParserException -> 0x016c, IOException -> 0x0156 }
            r13.<init>(r12, r9)     // Catch:{ XmlPullParserException -> 0x016c, IOException -> 0x0156 }
            r13 = r6
            goto L_0x0186
        L_0x0156:
            r13 = move-exception
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r0)
            android.content.pm.ActivityInfo r0 = r9.activityInfo
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            android.util.Log.w(r3, r0, r13)
            goto L_0x0185
        L_0x016c:
            r13 = move-exception
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r0)
            android.content.pm.ActivityInfo r0 = r9.activityInfo
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            android.util.Log.w(r3, r0, r13)
            goto L_0x0185
        L_0x0182:
            int r8 = r8 + 1
            goto L_0x012d
        L_0x0185:
            r13 = r5
        L_0x0186:
            if (r13 != 0) goto L_0x01a0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "Request to add invalid device admin: "
            r13.append(r0)
            r13.append(r4)
            java.lang.String r13 = r13.toString()
            android.util.Log.w(r3, r13)
            r12.finish()
            return
        L_0x01a0:
            android.content.pm.ResolveInfo r13 = new android.content.pm.ResolveInfo
            r13.<init>()
            r13.activityInfo = r2
            android.app.admin.DeviceAdminInfo r0 = new android.app.admin.DeviceAdminInfo     // Catch:{ XmlPullParserException -> 0x03c2, IOException -> 0x03ab }
            r0.<init>(r12, r13)     // Catch:{ XmlPullParserException -> 0x03c2, IOException -> 0x03ab }
            r12.mDeviceAdmin = r0     // Catch:{ XmlPullParserException -> 0x03c2, IOException -> 0x03ab }
            android.content.Intent r13 = r12.getIntent()
            java.lang.String r13 = r13.getAction()
            java.lang.String r0 = "android.app.action.ADD_DEVICE_ADMIN"
            boolean r13 = r0.equals(r13)
            r0 = -1
            if (r13 == 0) goto L_0x021f
            r12.mRefreshing = r5
            android.app.admin.DevicePolicyManager r13 = r12.mDPM
            boolean r13 = r13.isAdminActive(r4)
            if (r13 == 0) goto L_0x021f
            android.app.admin.DevicePolicyManager r13 = r12.mDPM
            android.os.UserHandle r1 = android.os.Process.myUserHandle()
            int r1 = r1.getIdentifier()
            boolean r13 = r13.isRemovingAdmin(r4, r1)
            if (r13 == 0) goto L_0x01f1
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "Requested admin is already being removed: "
            r13.append(r0)
            r13.append(r4)
            java.lang.String r13 = r13.toString()
            android.util.Log.w(r3, r13)
            r12.finish()
            return
        L_0x01f1:
            android.app.admin.DeviceAdminInfo r13 = r12.mDeviceAdmin
            java.util.ArrayList r13 = r13.getUsedPolicies()
            r1 = r5
        L_0x01f8:
            int r2 = r13.size()
            if (r1 >= r2) goto L_0x0214
            java.lang.Object r2 = r13.get(r1)
            android.app.admin.DeviceAdminInfo$PolicyInfo r2 = (android.app.admin.DeviceAdminInfo.PolicyInfo) r2
            android.app.admin.DevicePolicyManager r7 = r12.mDPM
            int r2 = r2.ident
            boolean r2 = r7.hasGrantedPolicy(r4, r2)
            if (r2 != 0) goto L_0x0211
            r12.mRefreshing = r6
            goto L_0x0214
        L_0x0211:
            int r1 = r1 + 1
            goto L_0x01f8
        L_0x0214:
            boolean r13 = r12.mRefreshing
            if (r13 != 0) goto L_0x021f
            r12.setResult(r0)
            r12.finish()
            return
        L_0x021f:
            android.content.Intent r13 = r12.getIntent()
            java.lang.String r1 = "android.app.extra.ADD_EXPLANATION"
            java.lang.CharSequence r13 = r13.getCharSequenceExtra(r1)
            if (r13 == 0) goto L_0x0231
            java.lang.String r13 = r13.toString()
            r12.mAddMsgText = r13
        L_0x0231:
            boolean r13 = r12.mAddingProfileOwner
            if (r13 == 0) goto L_0x02e5
            android.app.admin.DevicePolicyManager r13 = r12.mDPM
            boolean r13 = r13.hasUserSetupCompleted()
            if (r13 != 0) goto L_0x0241
            r12.addAndFinish()
            return
        L_0x0241:
            r13 = 17039925(0x1040235, float:2.4246154E-38)
            java.lang.String r13 = r12.getString(r13)
            boolean r1 = android.text.TextUtils.isEmpty(r13)
            if (r1 == 0) goto L_0x0257
            java.lang.String r13 = "Unable to set profile owner post-setup, no default supervisorprofile owner defined"
            android.util.Log.w(r3, r13)
            r12.finish()
            return
        L_0x0257:
            android.content.ComponentName r13 = android.content.ComponentName.unflattenFromString(r13)
            if (r13 == 0) goto L_0x02cd
            int r13 = r4.compareTo(r13)
            if (r13 == 0) goto L_0x0264
            goto L_0x02cd
        L_0x0264:
            androidx.appcompat.app.AlertDialog$Builder r13 = new androidx.appcompat.app.AlertDialog$Builder
            r13.<init>(r12)
            int r1 = com.android.settings.C1992R$string.profile_owner_add_title_simplified
            java.lang.CharSequence r1 = r12.getText(r1)
            androidx.appcompat.app.AlertDialog$Builder r13 = r13.setTitle((java.lang.CharSequence) r1)
            int r1 = com.android.settings.C1987R$layout.profile_owner_add
            androidx.appcompat.app.AlertDialog$Builder r13 = r13.setView((int) r1)
            int r1 = com.android.settings.C1992R$string.allow
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$2 r2 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$2
            r2.<init>()
            androidx.appcompat.app.AlertDialog$Builder r13 = r13.setPositiveButton((int) r1, (android.content.DialogInterface.OnClickListener) r2)
            int r1 = com.android.settings.C1992R$string.cancel
            r2 = 0
            androidx.appcompat.app.AlertDialog$Builder r13 = r13.setNegativeButton((int) r1, (android.content.DialogInterface.OnClickListener) r2)
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$1 r1 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$1
            r1.<init>()
            androidx.appcompat.app.AlertDialog$Builder r13 = r13.setOnDismissListener(r1)
            androidx.appcompat.app.AlertDialog r13 = r13.create()
            r13.show()
            android.widget.Button r0 = r13.getButton(r0)
            r12.mActionButton = r0
            r0.setFilterTouchesWhenObscured(r6)
            int r0 = com.android.settings.C1985R$id.add_msg_simplified
            android.view.View r0 = r13.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r12.mAddMsg = r0
            java.lang.String r1 = r12.mAddMsgText
            r0.setText(r1)
            int r0 = com.android.settings.C1985R$id.admin_warning_simplified
            android.view.View r13 = r13.findViewById(r0)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mAdminWarning = r13
            int r0 = com.android.settings.C1992R$string.device_admin_warning_simplified
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.mProfileOwnerName
            r1[r5] = r2
            java.lang.String r12 = r12.getString(r0, r1)
            r13.setText(r12)
            return
        L_0x02cd:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "Unable to set non-default profile owner post-setup "
            r13.append(r0)
            r13.append(r4)
            java.lang.String r13 = r13.toString()
            android.util.Log.w(r3, r13)
            r12.finish()
            return
        L_0x02e5:
            int r13 = com.android.settings.C1987R$layout.device_admin_add
            r12.setContentView((int) r13)
            int r13 = com.android.settings.C1985R$id.admin_icon
            android.view.View r13 = r12.findViewById(r13)
            android.widget.ImageView r13 = (android.widget.ImageView) r13
            r12.mAdminIcon = r13
            int r13 = com.android.settings.C1985R$id.admin_name
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mAdminName = r13
            int r13 = com.android.settings.C1985R$id.admin_description
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mAdminDescription = r13
            int r13 = com.android.settings.C1985R$id.profile_owner_warning
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mProfileOwnerWarning = r13
            int r13 = com.android.settings.C1985R$id.add_msg
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mAddMsg = r13
            int r13 = com.android.settings.C1985R$id.add_msg_expander
            android.view.View r13 = r12.findViewById(r13)
            android.widget.ImageView r13 = (android.widget.ImageView) r13
            r12.mAddMsgExpander = r13
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$3 r13 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$3
            r13.<init>()
            android.widget.ImageView r0 = r12.mAddMsgExpander
            r0.setOnClickListener(r13)
            android.widget.TextView r13 = r12.mAddMsg
            android.view.ViewTreeObserver r13 = r13.getViewTreeObserver()
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$4 r0 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$4
            r0.<init>()
            r13.addOnGlobalLayoutListener(r0)
            android.widget.TextView r13 = r12.mAddMsg
            r12.toggleMessageEllipsis(r13)
            int r13 = com.android.settings.C1985R$id.admin_warning
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mAdminWarning = r13
            int r13 = com.android.settings.C1985R$id.admin_policies
            android.view.View r13 = r12.findViewById(r13)
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            r12.mAdminPolicies = r13
            int r13 = com.android.settings.C1985R$id.admin_support_message
            android.view.View r13 = r12.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r12.mSupportMessage = r13
            int r13 = com.android.settings.C1985R$id.cancel_button
            android.view.View r13 = r12.findViewById(r13)
            android.widget.Button r13 = (android.widget.Button) r13
            r12.mCancelButton = r13
            r13.setFilterTouchesWhenObscured(r6)
            android.widget.Button r13 = r12.mCancelButton
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$5 r0 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$5
            r0.<init>()
            r13.setOnClickListener(r0)
            int r13 = com.android.settings.C1985R$id.uninstall_button
            android.view.View r13 = r12.findViewById(r13)
            android.widget.Button r13 = (android.widget.Button) r13
            r12.mUninstallButton = r13
            r13.setFilterTouchesWhenObscured(r6)
            android.widget.Button r13 = r12.mUninstallButton
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$6 r0 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$6
            r0.<init>()
            r13.setOnClickListener(r0)
            int r13 = com.android.settings.C1985R$id.action_button
            android.view.View r13 = r12.findViewById(r13)
            android.widget.Button r13 = (android.widget.Button) r13
            r12.mActionButton = r13
            int r13 = com.android.settings.C1985R$id.restricted_action
            android.view.View r13 = r12.findViewById(r13)
            r13.setFilterTouchesWhenObscured(r6)
            com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$7 r0 = new com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$7
            r0.<init>()
            r13.setOnClickListener(r0)
            return
        L_0x03ab:
            r13 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r3, r0, r13)
            r12.finish()
            return
        L_0x03c2:
            r13 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r3, r0, r13)
            r12.finish()
            return
        L_0x03d9:
            r13 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r3, r0, r13)
            r12.finish()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: private */
    public void showPolicyTransparencyDialogIfRequired() {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        if (isManagedProfile(this.mDeviceAdmin) && this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner())) {
            ComponentName profileOwnerAsUser = this.mDPM.getProfileOwnerAsUser(getUserId());
            if (profileOwnerAsUser != null && this.mDPM.isOrganizationOwnedDeviceWithManagedProfile()) {
                enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(profileOwnerAsUser, "no_remove_managed_profile", UserHandle.of(getUserId()));
            } else if (!hasBaseCantRemoveProfileRestriction()) {
                enforcedAdmin = getAdminEnforcingCantRemoveProfile();
            } else {
                return;
            }
            if (enforcedAdmin != null) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this, enforcedAdmin);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addAndFinish() {
        try {
            logSpecialPermissionChange(true, this.mDeviceAdmin.getComponent().getPackageName());
            this.mDPM.setActiveAdmin(this.mDeviceAdmin.getComponent(), this.mRefreshing);
            EventLog.writeEvent(90201, this.mDeviceAdmin.getActivityInfo().applicationInfo.uid);
            unrestrictAppIfPossible(BatteryUtils.getInstance(this));
            setResult(-1);
        } catch (RuntimeException e) {
            Log.w("DeviceAdminAdd", "Exception trying to activate admin " + this.mDeviceAdmin.getComponent(), e);
            if (this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
                setResult(-1);
            }
        }
        if (this.mAddingProfileOwner) {
            try {
                this.mDPM.setProfileOwner(this.mDeviceAdmin.getComponent(), this.mProfileOwnerName, UserHandle.myUserId());
            } catch (RuntimeException unused) {
                setResult(0);
            }
        }
        finish();
    }

    /* access modifiers changed from: package-private */
    public void unrestrictAppIfPossible(BatteryUtils batteryUtils) {
        batteryUtils.clearForceAppStandby(this.mDeviceAdmin.getComponent().getPackageName());
    }

    /* access modifiers changed from: package-private */
    public void continueRemoveAction(CharSequence charSequence) {
        if (this.mWaitingForRemoveMsg) {
            this.mWaitingForRemoveMsg = false;
            if (charSequence == null) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                logSpecialPermissionChange(false, this.mDeviceAdmin.getComponent().getPackageName());
                this.mDPM.removeActiveAdmin(this.mDeviceAdmin.getComponent());
                finish();
                return;
            }
            try {
                ActivityManager.getService().stopAppSwitches();
            } catch (RemoteException unused2) {
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequence("android.app.extra.DISABLE_WARNING", charSequence);
            showDialog(1, bundle);
        }
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(0, z ? 766 : 767, 0, str, 0);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mActionButton.setEnabled(true);
        if (!this.mAddingProfileOwner) {
            updateInterface();
        }
        this.mAppOps.setUserRestriction(24, true, this.mToken);
        this.mAppOps.setUserRestriction(45, true, this.mToken);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mActionButton.setEnabled(false);
        this.mAppOps.setUserRestriction(24, false, this.mToken);
        this.mAppOps.setUserRestriction(45, false, this.mToken);
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (this.mIsCalledFromSupportDialog) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int i, Bundle bundle) {
        if (i != 1) {
            return super.onCreateDialog(i, bundle);
        }
        CharSequence charSequence = bundle.getCharSequence("android.app.extra.DISABLE_WARNING");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(charSequence);
        builder.setPositiveButton(C1992R$string.dlg_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                DeviceAdminAdd deviceAdminAdd = DeviceAdminAdd.this;
                deviceAdminAdd.mDPM.removeActiveAdmin(deviceAdminAdd.mDeviceAdmin.getComponent());
                DeviceAdminAdd.this.finish();
            }
        });
        builder.setNegativeButton(C1992R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: package-private */
    public void updateInterface() {
        findViewById(C1985R$id.restricted_icon).setVisibility(8);
        this.mAdminIcon.setImageDrawable(this.mDeviceAdmin.loadIcon(getPackageManager()));
        this.mAdminName.setText(this.mDeviceAdmin.loadLabel(getPackageManager()));
        try {
            this.mAdminDescription.setText(this.mDeviceAdmin.loadDescription(getPackageManager()));
            this.mAdminDescription.setVisibility(0);
        } catch (Resources.NotFoundException unused) {
            this.mAdminDescription.setVisibility(8);
        }
        if (!TextUtils.isEmpty(this.mAddMsgText)) {
            this.mAddMsg.setText(this.mAddMsgText);
            this.mAddMsg.setVisibility(0);
        } else {
            this.mAddMsg.setVisibility(8);
            this.mAddMsgExpander.setVisibility(8);
        }
        boolean z = true;
        if (this.mRefreshing || this.mAddingProfileOwner || !this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
            addDeviceAdminPolicies(true);
            this.mAdminWarning.setText(getString(C1992R$string.device_admin_warning, new Object[]{this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager())}));
            setTitle(getText(C1992R$string.add_device_admin_msg));
            this.mActionButton.setText(getText(C1992R$string.add_device_admin));
            if (isAdminUninstallable()) {
                this.mUninstallButton.setVisibility(0);
            }
            this.mSupportMessage.setVisibility(8);
            this.mAdding = true;
            return;
        }
        this.mAdding = false;
        boolean equals = this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner());
        boolean isManagedProfile = isManagedProfile(this.mDeviceAdmin);
        if (equals && isManagedProfile) {
            this.mAdminWarning.setText(C1992R$string.admin_profile_owner_message);
            this.mActionButton.setText(C1992R$string.remove_managed_profile_label);
            RestrictedLockUtils.EnforcedAdmin adminEnforcingCantRemoveProfile = getAdminEnforcingCantRemoveProfile();
            boolean hasBaseCantRemoveProfileRestriction = hasBaseCantRemoveProfileRestriction();
            if ((hasBaseCantRemoveProfileRestriction && this.mDPM.isOrganizationOwnedDeviceWithManagedProfile()) || (adminEnforcingCantRemoveProfile != null && !hasBaseCantRemoveProfileRestriction)) {
                findViewById(C1985R$id.restricted_icon).setVisibility(0);
            }
            Button button = this.mActionButton;
            if (adminEnforcingCantRemoveProfile != null || hasBaseCantRemoveProfileRestriction) {
                z = false;
            }
            button.setEnabled(z);
        } else if (equals || this.mDeviceAdmin.getComponent().equals(this.mDPM.getDeviceOwnerComponentOnCallingUser())) {
            if (equals) {
                this.mAdminWarning.setText(C1992R$string.admin_profile_owner_user_message);
            } else if (isFinancedDevice()) {
                this.mAdminWarning.setText(C1992R$string.admin_financed_message);
            } else {
                this.mAdminWarning.setText(C1992R$string.admin_device_owner_message);
            }
            this.mActionButton.setText(C1992R$string.remove_device_admin);
            this.mActionButton.setEnabled(false);
        } else {
            addDeviceAdminPolicies(false);
            this.mAdminWarning.setText(getString(C1992R$string.device_admin_status, new Object[]{this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager())}));
            setTitle(C1992R$string.active_device_admin_msg);
            if (this.mUninstalling) {
                this.mActionButton.setText(C1992R$string.remove_and_uninstall_device_admin);
            } else {
                this.mActionButton.setText(C1992R$string.remove_device_admin);
            }
        }
        CharSequence longSupportMessageForUser = this.mDPM.getLongSupportMessageForUser(this.mDeviceAdmin.getComponent(), UserHandle.myUserId());
        if (!TextUtils.isEmpty(longSupportMessageForUser)) {
            this.mSupportMessage.setText(longSupportMessageForUser);
            this.mSupportMessage.setVisibility(0);
            return;
        }
        this.mSupportMessage.setVisibility(8);
    }

    private RestrictedLockUtils.EnforcedAdmin getAdminEnforcingCantRemoveProfile() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this, "no_remove_managed_profile", getParentUserId());
    }

    private boolean hasBaseCantRemoveProfileRestriction() {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(this, "no_remove_managed_profile", getParentUserId());
    }

    private int getParentUserId() {
        return UserManager.get(this).getProfileParent(UserHandle.myUserId()).id;
    }

    private void addDeviceAdminPolicies(boolean z) {
        if (!this.mAdminPoliciesInitialized) {
            boolean isAdminUser = UserManager.get(this).isAdminUser();
            Iterator it = this.mDeviceAdmin.getUsedPolicies().iterator();
            while (it.hasNext()) {
                DeviceAdminInfo.PolicyInfo policyInfo = (DeviceAdminInfo.PolicyInfo) it.next();
                this.mAdminPolicies.addView(getPermissionItemView(getText(isAdminUser ? policyInfo.label : policyInfo.labelForSecondaryUsers), z ? getText(isAdminUser ? policyInfo.description : policyInfo.descriptionForSecondaryUsers) : ""));
            }
            this.mAdminPoliciesInitialized = true;
        }
    }

    private View getPermissionItemView(CharSequence charSequence, CharSequence charSequence2) {
        Drawable drawable = getDrawable(17303747);
        View inflate = this.mLayoutInflaternflater.inflate(C1987R$layout.app_permission_item, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C1985R$id.permission_group);
        TextView textView2 = (TextView) inflate.findViewById(C1985R$id.permission_list);
        ((ImageView) inflate.findViewById(C1985R$id.perm_icon)).setImageDrawable(drawable);
        if (charSequence != null) {
            textView.setText(charSequence);
            textView2.setText(charSequence2);
        } else {
            textView.setText(charSequence2);
            textView2.setVisibility(8);
        }
        return inflate;
    }

    /* access modifiers changed from: package-private */
    public void toggleMessageEllipsis(View view) {
        TextView textView = (TextView) view;
        boolean z = !this.mAddMsgEllipsized;
        this.mAddMsgEllipsized = z;
        textView.setEllipsize(z ? TextUtils.TruncateAt.END : null);
        textView.setMaxLines(this.mAddMsgEllipsized ? getEllipsizedLines() : 15);
        this.mAddMsgExpander.setImageResource(this.mAddMsgEllipsized ? 17303113 : 17303112);
    }

    /* access modifiers changed from: package-private */
    public int getEllipsizedLines() {
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        return defaultDisplay.getHeight() > defaultDisplay.getWidth() ? 5 : 2;
    }

    /* access modifiers changed from: private */
    public boolean isManagedProfile(DeviceAdminInfo deviceAdminInfo) {
        UserInfo userInfo = UserManager.get(this).getUserInfo(UserHandle.getUserId(deviceAdminInfo.getActivityInfo().applicationInfo.uid));
        if (userInfo != null) {
            return userInfo.isManagedProfile();
        }
        return false;
    }

    private boolean isFinancedDevice() {
        if (this.mDPM.isDeviceManaged()) {
            DevicePolicyManager devicePolicyManager = this.mDPM;
            if (devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1) {
                return true;
            }
        }
        return false;
    }

    private Optional<ComponentName> findAdminWithPackageName(String str) {
        List<ComponentName> activeAdmins = this.mDPM.getActiveAdmins();
        if (activeAdmins == null) {
            return Optional.empty();
        }
        return activeAdmins.stream().filter(new DeviceAdminAdd$$ExternalSyntheticLambda0(str)).findAny();
    }

    private boolean isAdminUninstallable() {
        return !this.mDeviceAdmin.getActivityInfo().applicationInfo.isSystemApp();
    }
}
