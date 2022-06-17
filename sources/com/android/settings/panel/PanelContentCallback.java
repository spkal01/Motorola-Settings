package com.android.settings.panel;

public interface PanelContentCallback {
    void forceClose();

    void onHeaderChanged();

    void onProgressBarVisibleChanged();
}
