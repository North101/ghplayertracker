package net.north101.android.ghplayertracker.data;

import android.content.Context;

import net.north101.android.ghplayertracker.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.ParcelConverter;
import org.parceler.Parcels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class CharacterList extends HashMap<UUID, Character> implements ParcelConverter<CharacterList> {
    public CharacterList() {
    }

    public static JSONObject loadJSON(Context context) throws IOException, JSONException, ParseException {
        InputStream inputStream = context.openFileInput("characters.json");
        return new JSONObject(Util.readAssetString(inputStream));
    }

    public static CharacterList parse(Context context, JSONObject data) {
        CharacterList characterList = new CharacterList();

        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Character character = Character.parse(context, data.getJSONObject(key));
                characterList.put(character.getId(), character);
            } catch (JSONException | IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        return characterList;
    }

    public static CharacterList load(Context context) throws IOException, JSONException, ParseException {
        return parse(context, loadJSON(context));
    }

    public static void saveJSON(Context context, JSONObject data) throws IOException, JSONException {
        try (FileOutputStream outputStream = context.openFileOutput("characters.json", Context.MODE_PRIVATE)) {
            outputStream.write(data.toString().getBytes());
        }
    }

    public void save(Context context) throws IOException, JSONException {
        JSONObject data = new JSONObject();
        for (Character character : this.values()) {
            data.put(character.getId().toString(), character.toJSON());
        }

        saveJSON(context, data);
    }

    @Override
    public void toParcel(CharacterList input, android.os.Parcel parcel) {
        if (input == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(input.size());
            for (Character item : input.values()) {
                parcel.writeParcelable(Parcels.wrap(item), 0);
            }
        }
    }

    @Override
    public CharacterList fromParcel(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) return null;

        CharacterList items = new CharacterList();
        for (int i = 0; i < size; ++i) {
            Character character = Parcels.unwrap(parcel.readParcelable(Character.class.getClassLoader()));
            items.put(character.getId(), character);
        }
        return items;
    }
}
