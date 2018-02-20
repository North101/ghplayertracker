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
    public final int count;
    public final String text;

    @ParcelConstructor
    Perk(List<PerkItem> perkItems, int count, String text) {
        this.perkItems = perkItems;
        this.count = count;
        this.text = text;
    }

    public static Perk parse(JSONObject data) throws JSONException {
        List<PerkItem> perkItems = new ArrayList<>();
        JSONArray perksData = data.getJSONArray("cards");
        for (int i = 0; i < perksData.length(); i++) {
            perkItems.add(PerkItem.parse(perksData.getJSONObject(i)));
        }

        int count = data.optInt("count", 1);
        String text = data.getString("text");

        return new Perk(perkItems, count, text);
    }
}
