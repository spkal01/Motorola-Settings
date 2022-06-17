package com.android.settings.display;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.IconDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.android.settings.C1987R$layout;
import com.motorola.settings.display.IconsAdapter;

public class AppGridView extends GridView {
    public AppGridView(Context context) {
        super(context);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    private void init(Context context) {
        setAdapter(new IconsAdapter(context, C1987R$layout.screen_zoom_preview_app_icon, 16908308, 16908295));
    }

    public static class AppsAdapter extends ArrayAdapter<ActivityEntry> {
        private final int mIconResId;

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEnabled(int i) {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            ((ImageView) view2.findViewById(this.mIconResId)).setImageDrawable(((ActivityEntry) getItem(i)).getIcon());
            return view2;
        }
    }

    public static class ActivityEntry implements Comparable<ActivityEntry> {
        public final ResolveInfo info;
        public final String label;
        private final IconDrawableFactory mIconFactory;
        private final int mUserId;

        public int compareTo(ActivityEntry activityEntry) {
            return this.label.compareToIgnoreCase(activityEntry.label);
        }

        public String toString() {
            return this.label;
        }

        public Drawable getIcon() {
            IconDrawableFactory iconDrawableFactory = this.mIconFactory;
            ActivityInfo activityInfo = this.info.activityInfo;
            return iconDrawableFactory.getBadgedIcon(activityInfo, activityInfo.applicationInfo, this.mUserId);
        }
    }
}
