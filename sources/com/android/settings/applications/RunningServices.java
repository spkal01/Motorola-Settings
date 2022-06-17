package com.android.settings.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.LoadingViewController;

public class RunningServices extends SettingsPreferenceFragment {
    private View mLoadingContainer;
    /* access modifiers changed from: private */
    public LoadingViewController mLoadingViewController;
    private Menu mOptionsMenu;
    private final Runnable mRunningProcessesAvail = new Runnable() {
        public void run() {
            RunningServices.this.mLoadingViewController.showContent(true);
        }
    };
    private RunningProcessesView mRunningProcessesView;

    public int getMetricsCategory() {
        return 404;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C1992R$string.runningservices_settings_title);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C1987R$layout.manage_applications_running, (ViewGroup) null);
        RunningProcessesView runningProcessesView = (RunningProcessesView) inflate.findViewById(C1985R$id.running_processes);
        this.mRunningProcessesView = runningProcessesView;
        runningProcessesView.doCreate();
        View findViewById = inflate.findViewById(C1985R$id.loading_container);
        this.mLoadingContainer = findViewById;
        this.mLoadingViewController = new LoadingViewController(findViewById, this.mRunningProcessesView);
        return inflate;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        this.mOptionsMenu = menu;
        menu.add(0, 1, 1, C1992R$string.show_running_services).setShowAsAction(0);
        menu.add(0, 2, 2, C1992R$string.show_background_processes).setShowAsAction(0);
        updateOptionsMenu();
    }

    public void onResume() {
        super.onResume();
        if (this.mRunningProcessesView.doResume(this, this.mRunningProcessesAvail)) {
            this.mLoadingViewController.showContent(false);
        } else {
            this.mLoadingViewController.showLoadingView();
        }
    }

    public void onPause() {
        super.onPause();
        this.mRunningProcessesView.doPause();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            this.mRunningProcessesView.mAdapter.setShowBackground(false);
        } else if (itemId != 2) {
            return false;
        } else {
            this.mRunningProcessesView.mAdapter.setShowBackground(true);
        }
        updateOptionsMenu();
        return true;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        boolean showBackground = this.mRunningProcessesView.mAdapter.getShowBackground();
        this.mOptionsMenu.findItem(1).setVisible(showBackground);
        this.mOptionsMenu.findItem(2).setVisible(!showBackground);
    }
}
