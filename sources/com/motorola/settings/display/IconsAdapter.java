package com.motorola.settings.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1992R$string;
import java.util.ArrayList;

public class IconsAdapter extends ArrayAdapter<StableIconInfo> {
    private final int mIconResId;
    private final int mTextResId;

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEnabled(int i) {
        return false;
    }

    public IconsAdapter(Context context, int i, int i2, int i3) {
        super(context, i, i2);
        this.mIconResId = i3;
        this.mTextResId = i2;
        ArrayList arrayList = new ArrayList();
        int[] iArr = {C1983R$drawable.ic_display_preview_alarm_24, C1983R$drawable.ic_display_preview_settings_24, C1983R$drawable.ic_display_preview_calculate_24};
        int[] iArr2 = {C1992R$string.volume_alarm_description, C1992R$string.settings_label, C1992R$string.screen_zoom_preview_calculator};
        for (int i4 = 0; i4 < 3; i4++) {
            arrayList.add(new StableIconInfo(iArr2[i4], iArr[i4]));
        }
        addAll(arrayList);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = super.getView(i, view, viewGroup);
        StableIconInfo stableIconInfo = (StableIconInfo) getItem(i);
        ((ImageView) view2.findViewById(this.mIconResId)).setImageResource(stableIconInfo.getmIconRes());
        ((TextView) view2.findViewById(this.mTextResId)).setText(stableIconInfo.getLabelRes());
        return view2;
    }

    public static class StableIconInfo {
        private final int labelRes;
        private final int mIconRes;

        public StableIconInfo(int i, int i2) {
            this.labelRes = i;
            this.mIconRes = i2;
        }

        public int getLabelRes() {
            return this.labelRes;
        }

        public int getmIconRes() {
            return this.mIconRes;
        }
    }
}
