package net.north101.android.ghplayertracker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.north101.android.ghplayertracker.data.Card;
import net.north101.android.ghplayertracker.data.PlayedCards;

import java.util.ArrayList;
import java.util.List;

public class PlayedCardsAdapter extends RecyclerView.Adapter<BaseViewHolder<?>> {
    public static class CardDivider {
    }

    public static class CardHeader {
        public String text;

        public CardHeader(String text) {
            this.text = text;
        }
    }

    public static CardHeader shuffledHeader = new CardHeader("Shuffled");
    public static CardDivider cardDivider = new CardDivider();

    private List<Object> items = new ArrayList<>();

    public class CardInfo {
        public final Card card;
        public final boolean split;
        public boolean shuffled;

        public CardInfo(Card card, boolean split, boolean shuffled) {
            this.card = card;
            this.split = split;
            this.shuffled = shuffled;
        }
    }

    @Override
    public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return CardViewHolder.inflate(parent);
            case 2:
                return CardDividerViewHolder.inflate(parent);
            case 3:
                return CardHeaderViewHolder.inflate(parent);
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<?> holder, int position) {
        Object item = getItem(position);
        if (item instanceof CardInfo) {
            ((CardViewHolder) holder).bind((CardInfo) item);
        } else if (item instanceof CardDivider) {
            ((CardDividerViewHolder) holder).bind(item);
        } else if (item instanceof CardHeader) {
            ((CardHeaderViewHolder) holder).bind((CardHeader) item);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof CardInfo) {
            return 1;
        } else if (item instanceof CardDivider) {
            return 2;
        } else if ((item instanceof CardHeader)) {
            return 3;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public void clearItems() {
        int oldSize = this.items.size();
        this.items.clear();
        this.notifyItemRangeRemoved(0, oldSize);
    }

    public void addItem(Object item) {
        this.items.add(item);
        this.notifyItemInserted(this.items.size() - 1);
    }

    public void addItem(int i, Object item) {
        this.items.add(i, item);
        this.notifyItemInserted(i);
    }

    public void setItem(int i, Object item) {
        this.items.set(i, item);
        this.notifyItemChanged(i);
    }

    public void removeItem(int i) {
        this.items.remove(i);
        this.notifyItemRemoved(i);
    }

    public void addItems(int i, List<Object> items) {
        this.items.addAll(i, items);
        this.notifyItemRangeInserted(i, items.size());
    }

    public void addItem(PlayedCards item) {
        ArrayList<Object> newItems = new ArrayList<>();
        if (item.pile2 == null) {
            for (Card card : item.pile1) {
                newItems.add(new CardInfo(card, false, item.shuffled));
            }
        } else {
            int count = Math.max(item.pile1.size(), item.pile2.size());
            for (int i = 0; i < count; i++) {
                if (i < item.pile1.size()) {
                    newItems.add(new CardInfo(item.pile1.get(i), true, item.shuffled));
                } else {
                    newItems.add(new CardInfo(null, true, item.shuffled));
                }
                if (i < item.pile2.size()) {
                    newItems.add(new CardInfo(item.pile2.get(i), true, item.shuffled));
                } else {
                    newItems.add(new CardInfo(null, true, item.shuffled));
                }
            }
        }
        if (items.size() > 0 && !(items.get(0) instanceof CardHeader)) {
            newItems.add(cardDivider);
        }
        addItems(0, newItems);
    }

    public void updateShuffledHeaderPosition() {
        int position = -1;
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (item == shuffledHeader) {
                this.setItem(i, cardDivider);
            } else if (position == -1 && item instanceof CardInfo && ((CardInfo) item).shuffled) {
                position = i;
            }
        }

        if (position == 0) {
            this.addItem(position, shuffledHeader);
        } else if (position != -1) {
            int insertPosition = position - 1;
            if (!(items.get(insertPosition) instanceof CardDivider))
                throw new RuntimeException(String.valueOf(items.get(insertPosition)));

            this.setItem(insertPosition, shuffledHeader);
        }

        if (items.size() > 0 && this.getItem(0) instanceof CardDivider) {
            this.removeItem(0);
        }
    }
}
