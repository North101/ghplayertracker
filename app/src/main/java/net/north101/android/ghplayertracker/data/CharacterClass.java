package net.north101.android.ghplayertracker.data;

import android.content.Context;
import android.graphics.Color;

import net.north101.android.ghplayertracker.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Parcel
public class CharacterClass {
    private static final HashMap<String, CharacterClass> cache = new HashMap<>();
    public static final int LEVEL_MIN = 1;
    public static final int LEVEL_MAX = 9;

    public final String id;
    public final String name;
    public final int color;
    public final Level[] levels = new Level[LEVEL_MAX - LEVEL_MIN];
    public final HashMap<String, Card> cards;
    public final List<Perk> perks;

    @ParcelConstructor
    protected CharacterClass(String id, String name, int color, Level[] levels, HashMap<String, Card> cards, List<Perk> perks) {
        this.id = id;
        this.name = name;
        this.color = color;
        System.arraycopy(levels, 0, this.levels, 0, Math.min(levels.length, this.levels.length));
        for (int i = 0; i < this.levels.length; i++) {
            if (this.levels[i] == null) {
                this.levels[i] = new Level(i + 1, i == 0 ? 1 : this.levels[i - 1].health);
            }
        }
        this.cards = cards;
        this.perks = perks;

        CharacterClass.cache.put(id, this);
    }

    private static CharacterClass parse(String id, JSONObject data) throws JSONException {
        String name = data.getString("name");
        int color = Color.parseColor(data.getString("color"));

        HashMap<String, Card> cards = new HashMap<>();

        JSONArray healthData = data.getJSONArray("health");
        Level[] levels = new Level[healthData.length()];
        for (int i = 0; i < healthData.length(); i++) {
            levels[i] = new Level(i + 1, healthData.getInt(i));
        }

        JSONObject cardsData = data.getJSONObject("cards");
        Iterator<String> cardsIterator = cardsData.keys();
        while (cardsIterator.hasNext()) {
            String cardId = cardsIterator.next();
            Card card = Card.parse(cardId, cardsData.getJSONObject(cardId));
            cards.put(cardId, card);
        }

        List<Perk> perks = new ArrayList<>();
        JSONArray perkGroupsData = data.getJSONArray("perks");
        for (int i = 0; i < perkGroupsData.length(); i++) {
            perks.add(Perk.parse(perkGroupsData.getJSONObject(i)));
        }

        return new CharacterClass(id, name, color, levels, cards, perks);
    }

    public static CharacterClass load(Context context, String id) throws IOException, JSONException {
        CharacterClass characterClass = cache.get(id);
        if (characterClass != null)
            return characterClass;

        InputStream inputStream = context.getAssets().open(id + ".json");

        return CharacterClass.parse(id, new JSONObject(Util.readAssetString(inputStream)));
    }
}
