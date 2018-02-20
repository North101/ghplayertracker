package net.north101.android.ghplayertracker;

import android.view.View;
import android.view.ViewGroup;

public class CardDividerViewHolder extends BaseViewHolder<Object> {
    public static int layout = R.layout.card_divider_view;

    public static CardDividerViewHolder inflate(ViewGroup parent) {
        return new CardDividerViewHolder(inflate(parent, layout));
    }

    public CardDividerViewHolder(View itemView) {
        super(itemView);
    }
}
