package net.north101.android.ghplayertracker.data;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class PerkNote {
    public static final int MAX_TICKS = 3;

    protected int ticks;

    @ParcelConstructor
    public PerkNote(int ticks) {
        this.setTicks(ticks);
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = Math.min(Math.max(ticks, 0), 3);
    }
}
