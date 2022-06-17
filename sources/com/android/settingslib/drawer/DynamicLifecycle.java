package com.android.settingslib.drawer;

import android.os.Bundle;

public interface DynamicLifecycle {
    void onStart(Bundle bundle);

    void onStop();
}
