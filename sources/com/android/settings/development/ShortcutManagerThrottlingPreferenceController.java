package com.android.settings.development;

import android.content.Context;
import android.content.pm.IShortcutService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class ShortcutManagerThrottlingPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private final IShortcutService mShortcutService = getShortCutService();

    public String getPreferenceKey() {
        return "reset_shortcut_manager_throttling";
    }

    public ShortcutManagerThrottlingPreferenceController(Context context) {
        super(context);
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals("reset_shortcut_manager_throttling", preference.getKey())) {
            return false;
        }
        resetShortcutManagerThrottling();
        return true;
    }

    private void resetShortcutManagerThrottling() {
        IShortcutService iShortcutService = this.mShortcutService;
        if (iShortcutService != null) {
            try {
                iShortcutService.resetThrottling();
                Toast.makeText(this.mContext, C1992R$string.reset_shortcut_manager_throttling_complete, 0).show();
            } catch (RemoteException e) {
                Log.e("ShortcutMgrPrefCtrl", "Failed to reset rate limiting", e);
            }
        }
    }

    private IShortcutService getShortCutService() {
        try {
            return IShortcutService.Stub.asInterface(ServiceManager.getService("shortcut"));
        } catch (VerifyError unused) {
            return null;
        }
    }
}
