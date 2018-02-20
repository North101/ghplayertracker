package net.north101.android.ghplayertracker.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.HashMap;

@Parcel
public class Card {
    private static HashMap<String, Card> cache = new HashMap<>();

    public final String id;
    public final DeckType deckType;
    public final CardSpecial special;

    @ParcelConstructor
    protected Card(String id, DeckType deckType, CardSpecial special) {
        this.id = id;
        this.deckType = deckType;
        this.special = special;

        Card.cache.put(id, this);
    }

    public static Card parse(String cardId, JSONObject data) throws JSONException {
        Card card = cache.get(cardId);
        if (card != null)
            return card;

        DeckType deckType = DeckType.parse(data.getString("deck"));
        CardSpecial special = CardSpecial.parse(data.optString("special", null));

        return new Card(cardId, deckType, special);
    }

    public static Card get(String cardId) {
        Card card = cache.get(cardId);
        if (card == null)
            throw new RuntimeException(cardId);

        return card;
    }
}
