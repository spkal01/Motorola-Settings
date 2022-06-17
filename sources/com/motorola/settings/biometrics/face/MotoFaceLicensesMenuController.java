package com.motorola.settings.biometrics.face;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import com.android.settings.C1992R$string;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu;

public class MotoFaceLicensesMenuController implements LifecycleObserver, OnCreateOptionsMenu {
    private final Fragment mHost;

    public static void init(ObservablePreferenceFragment observablePreferenceFragment) {
        observablePreferenceFragment.getSettingsLifecycle().addObserver(new MotoFaceLicensesMenuController(observablePreferenceFragment));
    }

    private MotoFaceLicensesMenuController(Fragment fragment) {
        this.mHost = fragment;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Context context = this.mHost.getContext();
        if (menu != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.motorola.faceunlock", "com.motorola.faceunlock.LicencesActivity"));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                MenuItem add = menu.add(0, 0, 0, C1992R$string.security_settings_face_unlock_licenses_title);
                add.setShowAsAction(0);
                add.setOnMenuItemClickListener(new MotoFaceLicensesMenuController$$ExternalSyntheticLambda0(context, intent));
            }
        }
    }
}
