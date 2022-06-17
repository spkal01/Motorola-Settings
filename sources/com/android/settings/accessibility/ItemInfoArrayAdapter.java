package com.android.settings.accessibility;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.accessibility.ItemInfoArrayAdapter.ItemInfo;
import java.util.List;

public class ItemInfoArrayAdapter<T extends ItemInfo> extends ArrayAdapter<T> {
    public ItemInfoArrayAdapter(Context context, List<T> list) {
        super(context, C1987R$layout.dialog_single_radio_choice_list_item, C1985R$id.title, list);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = super.getView(i, view, viewGroup);
        ItemInfo itemInfo = (ItemInfo) getItem(i);
        ((TextView) view2.findViewById(C1985R$id.title)).setText(itemInfo.mTitle);
        TextView textView = (TextView) view2.findViewById(C1985R$id.summary);
        if (!TextUtils.isEmpty(itemInfo.mSummary)) {
            textView.setVisibility(0);
            textView.setText(itemInfo.mSummary);
        } else {
            textView.setVisibility(8);
        }
        ImageView imageView = (ImageView) view2.findViewById(C1985R$id.image);
        imageView.setImageResource(itemInfo.mDrawableId);
        if (getContext().getResources().getConfiguration().getLayoutDirection() == 0) {
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
        }
        return view2;
    }

    public static class ItemInfo {
        public final int mDrawableId;
        public final CharSequence mSummary;
        public final CharSequence mTitle;

        public ItemInfo(CharSequence charSequence, CharSequence charSequence2, int i) {
            this.mTitle = charSequence;
            this.mSummary = charSequence2;
            this.mDrawableId = i;
        }
    }
}
