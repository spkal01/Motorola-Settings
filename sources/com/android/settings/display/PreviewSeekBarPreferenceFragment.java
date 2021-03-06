package com.android.settings.display;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C1985R$id;
import com.android.settings.C1992R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.DotsPageIndicator;
import com.android.settings.widget.LabeledSeekBar;

public abstract class PreviewSeekBarPreferenceFragment extends SettingsPreferenceFragment {
    protected int mCurrentIndex;
    protected String[] mEntries;
    protected int mInitialIndex;
    private TextView mLabel;
    private View mLarger;
    /* access modifiers changed from: private */
    public long mLastCommitTime;
    private DotsPageIndicator mPageIndicator;
    private ViewPager.OnPageChangeListener mPageIndicatorPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
            PreviewSeekBarPreferenceFragment.this.setPagerIndicatorContentDescription(i);
        }
    };
    private ViewPager.OnPageChangeListener mPreviewPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
            PreviewSeekBarPreferenceFragment.this.mPreviewPager.sendAccessibilityEvent(16384);
        }
    };
    /* access modifiers changed from: private */
    public ViewPager mPreviewPager;
    /* access modifiers changed from: private */
    public PreviewPagerAdapter mPreviewPagerAdapter;
    private LabeledSeekBar mSeekBar;
    private View mSmaller;

    /* access modifiers changed from: protected */
    public abstract void commit();

    /* access modifiers changed from: protected */
    public abstract Configuration createConfig(Configuration configuration, int i);

    /* access modifiers changed from: protected */
    public abstract int getActivityLayoutResId();

    /* access modifiers changed from: protected */
    public abstract int[] getPreviewSampleResIds();

    private class onPreviewSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final Choreographer.FrameCallback mCommit;
        private long mCommitDelayMs;
        private boolean mIsChanged;
        private boolean mSeekByTouch;

        private onPreviewSeekBarChangeListener() {
            this.mCommit = new C0911x84d1f24b(this);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(long j) {
            PreviewSeekBarPreferenceFragment.this.commit();
            long unused = PreviewSeekBarPreferenceFragment.this.mLastCommitTime = SystemClock.elapsedRealtime();
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            PreviewSeekBarPreferenceFragment previewSeekBarPreferenceFragment = PreviewSeekBarPreferenceFragment.this;
            if (previewSeekBarPreferenceFragment.mCurrentIndex == i) {
                this.mIsChanged = false;
                return;
            }
            this.mIsChanged = true;
            previewSeekBarPreferenceFragment.setPreviewLayer(i, false);
            if (this.mSeekByTouch) {
                this.mCommitDelayMs = 100;
                return;
            }
            this.mCommitDelayMs = 300;
            commitOnNextFrame();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            this.mSeekByTouch = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            this.mSeekByTouch = false;
            if (this.mIsChanged) {
                if (PreviewSeekBarPreferenceFragment.this.mPreviewPagerAdapter.isAnimating()) {
                    PreviewSeekBarPreferenceFragment.this.mPreviewPagerAdapter.setAnimationEndAction(new C0912x84d1f24c(this));
                } else {
                    commitOnNextFrame();
                }
            }
        }

        /* access modifiers changed from: private */
        public void commitOnNextFrame() {
            if (SystemClock.elapsedRealtime() - PreviewSeekBarPreferenceFragment.this.mLastCommitTime < 800) {
                this.mCommitDelayMs += 800;
            }
            Choreographer instance = Choreographer.getInstance();
            instance.removeFrameCallback(this.mCommit);
            instance.postFrameCallbackDelayed(this.mCommit, this.mCommitDelayMs);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong("mLastCommitTime", this.mLastCommitTime);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (bundle != null) {
            this.mLastCommitTime = bundle.getLong("mLastCommitTime");
        }
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ViewGroup viewGroup2 = (ViewGroup) onCreateView.findViewById(16908351);
        viewGroup2.removeAllViews();
        View inflate = layoutInflater.inflate(getActivityLayoutResId(), viewGroup2, false);
        viewGroup2.addView(inflate);
        this.mLabel = (TextView) inflate.findViewById(C1985R$id.current_label);
        int max = Math.max(1, this.mEntries.length - 1);
        LabeledSeekBar labeledSeekBar = (LabeledSeekBar) inflate.findViewById(C1985R$id.seek_bar);
        this.mSeekBar = labeledSeekBar;
        labeledSeekBar.setLabels(this.mEntries);
        this.mSeekBar.setMax(max);
        View findViewById = inflate.findViewById(C1985R$id.smaller);
        this.mSmaller = findViewById;
        findViewById.setOnClickListener(new PreviewSeekBarPreferenceFragment$$ExternalSyntheticLambda1(this));
        View findViewById2 = inflate.findViewById(C1985R$id.larger);
        this.mLarger = findViewById2;
        findViewById2.setOnClickListener(new PreviewSeekBarPreferenceFragment$$ExternalSyntheticLambda0(this));
        if (this.mEntries.length == 1) {
            this.mSeekBar.setEnabled(false);
        }
        Context context = getContext();
        Configuration configuration = context.getResources().getConfiguration();
        boolean z = configuration.getLayoutDirection() == 1;
        Configuration[] configurationArr = new Configuration[this.mEntries.length];
        for (int i = 0; i < this.mEntries.length; i++) {
            configurationArr[i] = createConfig(configuration, i);
        }
        int[] previewSampleResIds = getPreviewSampleResIds();
        this.mPreviewPager = (ViewPager) inflate.findViewById(C1985R$id.preview_pager);
        PreviewPagerAdapter previewPagerAdapter = new PreviewPagerAdapter(context, z, previewSampleResIds, configurationArr);
        this.mPreviewPagerAdapter = previewPagerAdapter;
        this.mPreviewPager.setAdapter(previewPagerAdapter);
        this.mPreviewPager.setCurrentItem(z ? previewSampleResIds.length - 1 : 0);
        this.mPreviewPager.addOnPageChangeListener(this.mPreviewPageChangeListener);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) inflate.findViewById(C1985R$id.page_indicator);
        this.mPageIndicator = dotsPageIndicator;
        if (previewSampleResIds.length > 1) {
            dotsPageIndicator.setViewPager(this.mPreviewPager);
            this.mPageIndicator.setVisibility(0);
            this.mPageIndicator.setOnPageChangeListener(this.mPageIndicatorPageChangeListener);
        } else {
            dotsPageIndicator.setVisibility(8);
        }
        setPreviewLayer(this.mInitialIndex, false);
        return onCreateView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(View view) {
        int progress = this.mSeekBar.getProgress();
        if (progress > 0) {
            this.mSeekBar.setProgress(progress - 1, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(View view) {
        int progress = this.mSeekBar.getProgress();
        if (progress < this.mSeekBar.getMax()) {
            this.mSeekBar.setProgress(progress + 1, true);
        }
    }

    public void onStart() {
        super.onStart();
        this.mSeekBar.setProgress(this.mCurrentIndex);
        this.mSeekBar.setOnSeekBarChangeListener(new onPreviewSeekBarChangeListener());
    }

    public void onStop() {
        super.onStop();
        this.mSeekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) null);
    }

    /* access modifiers changed from: private */
    public void setPreviewLayer(int i, boolean z) {
        this.mLabel.setText(this.mEntries[i]);
        boolean z2 = false;
        this.mSmaller.setEnabled(i > 0);
        View view = this.mLarger;
        if (i < this.mEntries.length - 1) {
            z2 = true;
        }
        view.setEnabled(z2);
        setPagerIndicatorContentDescription(this.mPreviewPager.getCurrentItem());
        this.mPreviewPagerAdapter.setPreviewLayer(i, this.mCurrentIndex, this.mPreviewPager.getCurrentItem(), z);
        this.mCurrentIndex = i;
    }

    /* access modifiers changed from: private */
    public void setPagerIndicatorContentDescription(int i) {
        this.mPageIndicator.setContentDescription(getString(C1992R$string.preview_page_indicator_content_description, Integer.valueOf(i + 1), Integer.valueOf(getPreviewSampleResIds().length)));
    }
}
