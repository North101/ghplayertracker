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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerkNote)) return false;

        PerkNote perkNote = (PerkNote) o;

        return getTicks() == perkNote.getTicks();
    }

    @Override
    public int hashCode() {
        return getTicks();
    }
}
