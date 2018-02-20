package net.north101.android.ghplayertracker.data;

import org.json.JSONException;

public enum Elements {
    fire,
    ice,
    wind,
    earth,
    sun,
    moon;

    public static Elements parse(String elementData) throws JSONException {
        switch (elementData) {
            case "fire":
                return Elements.fire;

            case "ice":
                return Elements.ice;

            case "earth":
                return Elements.earth;

            case "wind":
                return Elements.wind;

            case "sun":
                return Elements.sun;

            case "moon":
                return Elements.moon;

            default:
                throw new JSONException("");
        }
    }
}
