package com.android.settings.homepage.contextualcards.legacysuggestion;

import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C1981R$color;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardController;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;
import com.google.android.material.card.MaterialCardView;

public class LegacySuggestionContextualCardRenderer implements ContextualCardRenderer {
    private static final ComponentName CARD_SETUP_COMPONENT = ComponentName.unflattenFromString("com.google.android.setupwizard/.deferred.DeferredSettingsSuggestionActivity");
    public static final int VIEW_TYPE = C1987R$layout.extended_legacy_suggestion_tile;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public LegacySuggestionContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new LegacySuggestionViewHolder(view);
    }

    public void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard) {
        LegacySuggestionViewHolder legacySuggestionViewHolder = (LegacySuggestionViewHolder) viewHolder;
        ContextualCardController controller = this.mControllerRendererPool.getController(this.mContext, contextualCard.getCardType());
        legacySuggestionViewHolder.icon.setImageDrawable(contextualCard.getIconDrawable());
        legacySuggestionViewHolder.title.setText(contextualCard.getTitleText());
        legacySuggestionViewHolder.summary.setText(contextualCard.getSummaryText());
        legacySuggestionViewHolder.itemView.setOnClickListener(new LegacySuggestionContextualCardRenderer$$ExternalSyntheticLambda0(controller, contextualCard));
        legacySuggestionViewHolder.closeButton.setOnClickListener(new LegacySuggestionContextualCardRenderer$$ExternalSyntheticLambda1(controller, contextualCard));
        legacySuggestionViewHolder.cardView.setCardBackgroundColor(getCardBackgroundColor(contextualCard));
    }

    private int getCardBackgroundColor(ContextualCard contextualCard) {
        ComponentName componentName = CARD_SETUP_COMPONENT;
        if (componentName == null || !contextualCard.getName().equals(componentName.flattenToString())) {
            return ContextCompat.getColor(this.mContext, C1981R$color.suggestion_card_default_color);
        }
        return ContextCompat.getColor(this.mContext, C1981R$color.suggestion_card_highlighted_color);
    }

    private static class LegacySuggestionViewHolder extends RecyclerView.ViewHolder {
        public final MaterialCardView cardView;
        public final View closeButton;
        public final ImageView icon;
        public final TextView summary;
        public final TextView title;

        public LegacySuggestionViewHolder(View view) {
            super(view);
            this.cardView = (MaterialCardView) view.findViewById(C1985R$id.suggestion_card);
            this.icon = (ImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
            this.summary = (TextView) view.findViewById(16908304);
            this.closeButton = view.findViewById(C1985R$id.close_button);
        }
    }
}
