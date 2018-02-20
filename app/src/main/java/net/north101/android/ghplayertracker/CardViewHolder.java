package net.north101.android.ghplayertracker;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CardViewHolder extends BaseViewHolder<PlayedCardsAdapter.CardInfo> {
    public static int layout = R.layout.card_item_view;

    public static CardViewHolder inflate(ViewGroup parent) {
        return new CardViewHolder(inflate(parent, layout));
    }

    ImageView cardView;

    public CardViewHolder(View itemView) {
        super(itemView);

        cardView = itemView.findViewById(R.id.card);
    }

    @Override
    public void bind(PlayedCardsAdapter.CardInfo item) {
        super.bind(item);

        if (item.card == null) {
            cardView.setImageResource(0);
        } else {
            cardView.setImageResource(Util.getImageResource(itemView.getContext(), item.card.id));
        }
        if (item.shuffled) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);  //0 means grayscale
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            cardView.setColorFilter(cf);
        } else {
            cardView.setColorFilter(null);
        }
    }
}
