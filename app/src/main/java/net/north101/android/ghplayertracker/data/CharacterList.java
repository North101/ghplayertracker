package net.north101.android.ghplayertracker.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.ParcelConverter;
import org.parceler.Parcels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import net.north101.android.ghplayertracker.Util;

public class CharacterList extends HashMap<UUID, Character> implements ParcelConverter<CharacterList> {
    public CharacterList() {
    }

    public static CharacterList load(Context context) throws IOException, JSONException, ParseException {
        InputStream inputStream = context.openFileInput("characters.json");

        JSONObject data = new JSONObject(Util.readAssetString(inputStream));
        CharacterList characterList = new CharacterList();

        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Character character = Character.parse(context, data.getJSONObject(key));
            characterList.put(character.getId(), character);
        }

        return characterList;
    }

    public void save(Context context) throws IOException, JSONException {
        try (FileOutputStream outputStream = context.openFileOutput("characters.json", Context.MODE_PRIVATE)) {
            JSONObject data = new JSONObject();
            for (Character character : this.values()) {
                data.put(character.getId().toString(), character.toJSON());
            }

            outputStream.write(data.toString().getBytes());
        }
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
