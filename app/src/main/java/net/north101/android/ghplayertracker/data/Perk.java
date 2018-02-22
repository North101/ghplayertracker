package net.north101.android.ghplayertracker.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Perk {
    public final List<PerkItem> perkItems;
    public final int ticks;
    public final String text;

    @ParcelConstructor
    Perk(List<PerkItem> perkItems, int ticks, String text) {
        this.perkItems = perkItems;
        this.ticks = ticks;
        this.text = text;
    }

    public static Perk parse(JSONObject data) throws JSONException {
        List<PerkItem> perkItems = new ArrayList<>();
        JSONArray perksData = data.getJSONArray("cards");
        for (int i = 0; i < perksData.length(); i++) {
            perkItems.add(PerkItem.parse(perksData.getJSONObject(i)));
        }

        int ticks = data.optInt("ticks", 1);
        String text = data.getString("text");

        return new Perk(perkItems, ticks, text);
    }
}
