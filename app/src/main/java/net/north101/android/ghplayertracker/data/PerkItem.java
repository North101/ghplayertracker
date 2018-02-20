package net.north101.android.ghplayertracker.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class PerkItem {
    public final PerkAction perkAction;
    public final String cardId;
    public final int count;

    @ParcelConstructor
    public PerkItem(PerkAction perkAction, String cardId, int count) {
        this.perkAction = perkAction;
        this.cardId = cardId;
        this.count = count;
    }

    public static PerkItem parse(JSONObject jsonObject) throws JSONException {
        PerkAction perkAction = PerkAction.parse(jsonObject.getString("action"));
        int count = jsonObject.optInt("count", 1);
        String cardId = jsonObject.getString("card");

        return new PerkItem(perkAction, cardId, count);
    }
}
