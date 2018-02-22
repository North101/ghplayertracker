package net.north101.android.ghplayertracker.data;

import android.content.Context;

import net.north101.android.ghplayertracker.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

@Parcel
public class BasicCards {
    private static BasicCards instance = null;

    public final HashMap<String, Integer> basicDeck;

    @ParcelConstructor
    protected BasicCards(HashMap<String, Integer> basicDeck) {
        this.basicDeck = basicDeck;

        BasicCards.instance = this;
    }

    public static BasicCards load(Context context) throws IOException, JSONException {
        if (instance != null)
            return instance;

        InputStream inputStream = context.getAssets().open("basic_cards.json");
        return BasicCards.parse(new JSONObject(Util.readAssetString(inputStream)));
    }

    public static BasicCards parse(JSONObject data) throws JSONException {
        JSONObject cardsData = data.getJSONObject("cards");
        Iterator<String> cardsIterator = cardsData.keys();
        while (cardsIterator.hasNext()) {
            String cardId = cardsIterator.next();
            Card.parse(cardId, cardsData.getJSONObject(cardId));
        }

        HashMap<String, Integer> basicDeck = new HashMap<>();
        JSONArray basicData = data.getJSONArray("basic");
        for (int i = 0; i < basicData.length(); i++) {
            JSONObject basicCardData = basicData.getJSONObject(i);

            String cardId = basicCardData.getString("card_id");
            int count = basicCardData.optInt("count", 1);
            basicDeck.put(cardId, count);
        }

        return new BasicCards(basicDeck);
    }
}
