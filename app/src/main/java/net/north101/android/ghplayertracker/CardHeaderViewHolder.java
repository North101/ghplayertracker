package net.north101.android.ghplayertracker;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class CardHeaderViewHolder extends BaseViewHolder<PlayedCardsAdapter.CardHeader> {
    public static int layout = R.layout.card_header_view;

    public static CardHeaderViewHolder inflate(ViewGroup parent) {
        return new CardHeaderViewHolder(inflate(parent, layout));
    }

    public TextView textView;

    public CardHeaderViewHolder(View itemView) {
        super(itemView);
        this.textView = itemView.findViewById(R.id.text);
    }

    @Override
    public void bind(PlayedCardsAdapter.CardHeader item) {
        super.bind(item);

        this.textView.setText(item.text);
    }
}
