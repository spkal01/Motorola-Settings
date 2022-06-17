package com.android.settingslib.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.RestrictedLockUtils;
import java.util.Objects;

public abstract class ActionDisabledLearnMoreButtonLauncher {
    public static ResolveActivityChecker DEFAULT_RESOLVE_ACTIVITY_CHECKER = ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda0.INSTANCE;

    interface ResolveActivityChecker {
        boolean canResolveActivityAsUser(PackageManager packageManager, String str, UserHandle userHandle);
    }

    /* access modifiers changed from: protected */
    public void finishSelf() {
    }

    /* access modifiers changed from: protected */
    public abstract void launchShowAdminPolicies(Context context, UserHandle userHandle, ComponentName componentName);

    /* access modifiers changed from: protected */
    public abstract void launchShowAdminSettings(Context context);

    public abstract void setLearnMoreButton(Runnable runnable);

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$static$0(PackageManager packageManager, String str, UserHandle userHandle) {
        return packageManager.resolveActivityAsUser(createLearnMoreIntent(str), 65536, userHandle.getIdentifier()) != null;
    }

    public final void setupLearnMoreButtonToShowAdminPolicies(Context context, int i, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(enforcedAdmin, "enforcedAdmin cannot be null");
        if (isSameProfileGroup(context, i) || isEnforcedByDeviceOwnerOnSystemUserMode(context, i)) {
            setLearnMoreButton(new ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda1(this, context, enforcedAdmin));
        }
    }

    public final void setupLearnMoreButtonToLaunchHelpPage(Context context, String str, UserHandle userHandle) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(str, "url cannot be null");
        setLearnMoreButton(new ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda2(this, context, str, userHandle));
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isSameProfileGroup(Context context, int i) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        return userManager.isSameProfileGroup(i, userManager.getUserHandle());
    }

    private boolean isEnforcedByDeviceOwnerOnSystemUserMode(Context context, int i) {
        if (i == 0 && i == ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getDeviceOwnerUserId()) {
            return true;
        }
        return false;
    }

    @VisibleForTesting
    /* renamed from: showHelpPage */
    public void lambda$setupLearnMoreButtonToLaunchHelpPage$2(Context context, String str, UserHandle userHandle) {
        context.startActivityAsUser(createLearnMoreIntent(str), userHandle);
        finishSelf();
    }

    /* access modifiers changed from: protected */
    public final boolean canLaunchHelpPage(PackageManager packageManager, String str, UserHandle userHandle, ResolveActivityChecker resolveActivityChecker) {
        return resolveActivityChecker.canResolveActivityAsUser(packageManager, str, userHandle);
    }

    /* access modifiers changed from: private */
    /* renamed from: showAdminPolicies */
    public void lambda$setupLearnMoreButtonToShowAdminPolicies$1(Context context, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        ComponentName componentName = enforcedAdmin.component;
        if (componentName != null) {
            launchShowAdminPolicies(context, enforcedAdmin.user, componentName);
        } else {
            launchShowAdminSettings(context);
        }
        finishSelf();
    }

    private static Intent createLearnMoreIntent(String str) {
        return new Intent("android.intent.action.VIEW", Uri.parse(str)).setFlags(276824064);
    }
}
