package net.north101.android.ghplayertracker.data;

import android.content.Context;

import net.north101.android.ghplayertracker.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class ClassList {
    public final List<String> classList;

    @ParcelConstructor
    public ClassList(List<String> classList) {
        this.classList = classList;
    }

    public static ClassList load(Context context) throws IOException, JSONException {
        InputStream inputStream = context.getAssets().open("classes.json");

        return ClassList.parse(new JSONArray(Util.readAssetString(inputStream)));
    }

    public static ClassList parse(JSONArray data) throws JSONException {
        List<String> classes = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            classes.add(data.getString(i));
        }

        return new ClassList(classes);
    }
}
