package com.motorola.settings.display.edge;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import com.android.settings.C1978R$array;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.LabeledSeekBar;
import com.android.settingslib.widget.LayoutPreference;
import motorola.core_services.misc.MotoExtHwManager;

public class EdgeSensitivitySettingsFragment extends DashboardFragment {
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.edge_sensitivity_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return EdgeUtils.isEdgeSensitivityAvailable(context);
        }
    };
    /* access modifiers changed from: private */
    public EdgeSensitivityIndicatorView mIndicatorView;
    /* access modifiers changed from: private */
    public MotoExtHwManager mMotoExtHwManager;
    private WindowManager mWindowManager;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "EdgeSenseSettingsFrag";
    }

    public int getMetricsCategory() {
        return 3004;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIndicatorView = new EdgeSensitivityIndicatorView(getActivity());
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        initViews();
    }

    private void initViews() {
        LabeledSeekBar labeledSeekBar = (LabeledSeekBar) ((LayoutPreference) findPreference("edge_sensitivity_settings_layout")).findViewById(C1985R$id.edge_sensitivity_seek_bar);
        Resources resources = getActivity().getResources();
        final int[] sensitivityScales = EdgeUtils.getSensitivityScales(getActivity());
        this.mMotoExtHwManager = MotoExtHwManager.getInstance(getActivity().getApplicationContext());
        this.mWindowManager = (WindowManager) getActivity().getSystemService("window");
        String[] stringArray = resources.getStringArray(C1978R$array.config_edgeSensitivityScaleDisplayTexts);
        labeledSeekBar.setLabels(stringArray);
        int max = Math.max(1, stringArray.length - 1);
        int displayGripSuppressionDistance = EdgeUtils.getDisplayGripSuppressionDistance(getActivity());
        int i = 0;
        for (int i2 = 0; i2 < sensitivityScales.length; i2++) {
            if (sensitivityScales[i2] == displayGripSuppressionDistance) {
                i = i2;
            }
        }
        labeledSeekBar.setMax(max);
        labeledSeekBar.setProgress(i);
        labeledSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                int i2 = sensitivityScales[i];
                EdgeUtils.setDisplayGripSuppressionDistance(EdgeSensitivitySettingsFragment.this.getActivity(), i2);
                if (EdgeSensitivitySettingsFragment.DEBUG) {
                    Log.d("EdgeSenseSettingsFrag", "setWaterfallDisplayGripSuppressionDistance" + i2);
                }
                EdgeSensitivitySettingsFragment.this.mMotoExtHwManager.setWaterfallDisplayGripSuppressionDistance(i2);
            }
        });
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Window window = requireActivity().getWindow();
        window.addFlags(262176);
        window.getDecorView().setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != 1) {
                    return false;
                }
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (EdgeSensitivitySettingsFragment.DEBUG) {
                    Log.d("EdgeSenseSettingsFrag", x + "onTouch" + y);
                }
                EdgeSensitivitySettingsFragment.this.mIndicatorView.showIndicator(x, y);
                view.performClick();
                return true;
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (DEBUG) {
            Log.d("EdgeSenseSettingsFrag", "onResume");
        }
        getActivity().getWindow().getAttributes().layoutInDisplayCutoutMode = 3;
        WindowManager windowManager = this.mWindowManager;
        EdgeSensitivityIndicatorView edgeSensitivityIndicatorView = this.mIndicatorView;
        windowManager.addView(edgeSensitivityIndicatorView, edgeSensitivityIndicatorView.getLayoutParams(getActivity().getWindow().getAttributes()));
        this.mIndicatorView.hideIndicators();
    }

    public void onPause() {
        super.onPause();
        if (DEBUG) {
            Log.d("EdgeSenseSettingsFrag", "onPause");
        }
        this.mWindowManager.removeView(this.mIndicatorView);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.edge_sensitivity_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_edge_sensitivity;
    }
}
