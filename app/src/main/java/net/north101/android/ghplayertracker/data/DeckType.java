package net.north101.android.ghplayertracker.data;

import org.json.JSONException;

public enum DeckType {
    Basic,
    Class,
    Extra;

    public static DeckType parse(String cardDeckData) throws JSONException {
        switch (cardDeckData) {
            case "basic":
                return DeckType.Basic;

            case "class":
                return DeckType.Class;

            case "extra":
                return DeckType.Extra;

            default:
                throw new JSONException(cardDeckData);
        }
    }
}
