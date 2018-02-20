package net.north101.android.ghplayertracker.data;

import org.json.JSONException;

public enum CardSpecial {
    Shuffle,
    Rolling,
    Remove;

    public static CardSpecial parse(String data) throws JSONException {
        if (data == null) return null;

        switch (data) {
            case "shuffle":
                return CardSpecial.Shuffle;

            case "rolling":
                return CardSpecial.Rolling;

            case "remove":
                return CardSpecial.Remove;

            default:
                throw new JSONException("");
        }
    }
}
