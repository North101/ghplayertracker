package net.north101.android.ghplayertracker.data;

public class CharacterPerk {
    public final Perk perk;
    public int ticks;

    public CharacterPerk(Perk perk, int ticks) {
        this.perk = perk;
        this.ticks = ticks;
    }
}
