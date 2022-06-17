package com.android.settings.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.survey.SurveyMixin;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;
import com.motorola.settings.extensions.MotoPreferenceFragment;

public abstract class InstrumentedPreferenceFragment extends MotoPreferenceFragment implements Instrumentable {
    protected final int PLACEHOLDER_METRIC = 10000;
    protected MetricsFeatureProvider mMetricsFeatureProvider;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private VisibilityLoggerMixin mVisibilityLoggerMixin;

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return -1;
    }

    public void onAttach(Context context) {
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mVisibilityLoggerMixin = new VisibilityLoggerMixin(getMetricsCategory(), this.mMetricsFeatureProvider);
        getSettingsLifecycle().addObserver(this.mVisibilityLoggerMixin);
        getSettingsLifecycle().addObserver(new SurveyMixin(this, getClass().getSimpleName()));
        super.onAttach(context);
    }

    public void onResume() {
        this.mVisibilityLoggerMixin.setSourceMetricsCategory(getActivity());
        RecyclerView listView = getListView();
        if (listView != null) {
            OnScrollListener onScrollListener = new OnScrollListener(getClass().getName());
            this.mOnScrollListener = onScrollListener;
            listView.addOnScrollListener(onScrollListener);
        }
        super.onResume();
    }

    public void onPause() {
        RecyclerView listView = getListView();
        RecyclerView.OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            listView.removeOnScrollListener(onScrollListener);
            this.mOnScrollListener = null;
        }
        super.onPause();
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId > 0) {
            addPreferencesFromResource(preferenceScreenResId);
        }
    }

    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        updateActivityTitleWithScreenTitle(getPreferenceScreen());
    }

    public <T extends Preference> T findPreference(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return super.findPreference(charSequence);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        writePreferenceClickMetric(preference);
        return super.onPreferenceTreeClick(preference);
    }

    /* access modifiers changed from: protected */
    public final Context getPrefContext() {
        return getPreferenceManager().getContext();
    }

    /* access modifiers changed from: protected */
    public void writeElapsedTimeMetric(int i, String str) {
        this.mVisibilityLoggerMixin.writeElapsedTimeMetric(i, str);
    }

    /* access modifiers changed from: protected */
    public void writePreferenceClickMetric(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
    }

    private void updateActivityTitleWithScreenTitle(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != null) {
            CharSequence title = preferenceScreen.getTitle();
            if (!TextUtils.isEmpty(title)) {
                getActivity().setTitle(title);
                return;
            }
            Log.w("InstrumentedPrefFrag", "Screen title missing for fragment " + getClass().getName());
        }
    }

    private static final class OnScrollListener extends RecyclerView.OnScrollListener {
        private final String mClassName;
        private final InteractionJankMonitor mMonitor;

        private OnScrollListener(String str) {
            this.mMonitor = InteractionJankMonitor.getInstance();
            this.mClassName = str;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0) {
                this.mMonitor.end(28);
            } else if (i == 1) {
                this.mMonitor.begin(new InteractionJankMonitor.Configuration.Builder(28).setView(recyclerView).setTag(this.mClassName));
            }
        }
    }
}
