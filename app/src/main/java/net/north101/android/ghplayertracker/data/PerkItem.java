package net.north101.android.ghplayertracker.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class PerkItem {
    public final PerkAction perkAction;
    public final String cardId;
    public final int repeat;

    @ParcelConstructor
    public PerkItem(PerkAction perkAction, String cardId, int repeat) {
        this.perkAction = perkAction;
        this.cardId = cardId;
        this.repeat = repeat;
    }

    public static PerkItem parse(JSONObject jsonObject) throws JSONException {
        PerkAction perkAction = PerkAction.parse(jsonObject.getString("action"));
        int repeat = jsonObject.optInt("repeat", 1);
        String cardId = jsonObject.getString("card_id");

        return new PerkItem(perkAction, cardId, repeat);
    }
}
