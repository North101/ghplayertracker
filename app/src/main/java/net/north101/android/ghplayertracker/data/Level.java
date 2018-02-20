package net.north101.android.ghplayertracker.data;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Level {
    public static int[] LEVEL_XP_LIST = {
        0,
        45,
        95,
        150,
        210,
        275,
        345,
        420,
        500,
    };

    public final int level;
    public final int health;

    @ParcelConstructor
    public Level(int level, int health) {
        this.level = level;
        this.health = health;
    }

    public int getXP() {
        return LEVEL_XP_LIST[level - 1];
    }
}
