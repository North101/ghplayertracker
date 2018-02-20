package net.north101.android.ghplayertracker.data;

import org.json.JSONException;

public enum PerkAction {
    add,
    remove;

    public static PerkAction parse(String perkActionData) throws JSONException {
        switch (perkActionData) {
            case "add":
                return PerkAction.add;

            case "remove":
                return PerkAction.remove;

            default:
                throw new JSONException("");
        }
    }
}
