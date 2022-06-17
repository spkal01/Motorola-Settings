package com.android.settings.security;

import android.content.Context;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class ResetCredentialsPreferenceController extends RestrictedEncryptionPreferenceController implements LifecycleObserver, OnResume {
    private final KeyStore mKeyStore;
    private RestrictedPreference mPreference;
    private final KeyStore mWifiKeyStore;

    public String getPreferenceKey() {
        return "credentials_reset";
    }

    public ResetCredentialsPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, "no_config_credentials");
        KeyStore keyStore;
        KeyStore keyStore2 = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load((KeyStore.LoadStoreParameter) null);
        } catch (Exception unused) {
            keyStore = null;
        }
        this.mKeyStore = keyStore;
        if (context.getUser().isSystem()) {
            try {
                KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
                instance.load(new AndroidKeyStoreLoadStoreParameter(102));
                keyStore2 = instance;
            } catch (Exception unused2) {
            }
        }
        this.mWifiKeyStore = keyStore2;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onResume() {
        KeyStore keyStore;
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null && !restrictedPreference.isDisabledByAdmin()) {
            boolean z = false;
            try {
                KeyStore keyStore2 = this.mKeyStore;
                if ((keyStore2 != null && keyStore2.aliases().hasMoreElements()) || ((keyStore = this.mWifiKeyStore) != null && keyStore.aliases().hasMoreElements())) {
                    z = true;
                }
            } catch (KeyStoreException unused) {
            }
            this.mPreference.setEnabled(z);
        }
    }
}
