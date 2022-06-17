package com.android.settings.wifi.calling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import java.util.List;

public class DisclaimerItemListAdapter extends RecyclerView.Adapter<DisclaimerItemViewHolder> {
    private List<DisclaimerItem> mDisclaimerItemList;

    public DisclaimerItemListAdapter(List<DisclaimerItem> list) {
        this.mDisclaimerItemList = list;
    }

    public DisclaimerItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new DisclaimerItemViewHolder(((LayoutInflater) viewGroup.getContext().getSystemService("layout_inflater")).inflate(C1987R$layout.wfc_simple_disclaimer_item, (ViewGroup) null, false));
    }

    public void onBindViewHolder(DisclaimerItemViewHolder disclaimerItemViewHolder, int i) {
        disclaimerItemViewHolder.titleView.setText(this.mDisclaimerItemList.get(i).getTitleId());
        disclaimerItemViewHolder.descriptionView.setText(this.mDisclaimerItemList.get(i).getMessageId());
    }

    public int getItemCount() {
        return this.mDisclaimerItemList.size();
    }

    public static class DisclaimerItemViewHolder extends RecyclerView.ViewHolder {
        @VisibleForTesting
        static final int ID_DISCLAIMER_ITEM_DESCRIPTION = C1985R$id.disclaimer_desc;
        @VisibleForTesting
        static final int ID_DISCLAIMER_ITEM_TITLE = C1985R$id.disclaimer_title;
        public final TextView descriptionView;
        public final TextView titleView;

        public DisclaimerItemViewHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(ID_DISCLAIMER_ITEM_TITLE);
            this.descriptionView = (TextView) view.findViewById(ID_DISCLAIMER_ITEM_DESCRIPTION);
        }
    }
}
