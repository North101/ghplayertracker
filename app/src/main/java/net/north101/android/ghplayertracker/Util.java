package net.north101.android.ghplayertracker;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
    public static String readAssetString(InputStream inputStream) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder builder = new StringBuilder(1024);

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            builder.append(inputStr);
        }

        return builder.toString();
    }

    public static int getImageResource(Context context, String name) {
        Resources res = context.getResources();
        int i = res.getIdentifier(name, "drawable", context.getPackageName());
        if (i == 0)
            Log.d("getImageResource", name);
        return i;
    }
}
