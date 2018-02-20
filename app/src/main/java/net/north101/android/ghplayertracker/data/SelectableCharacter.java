package net.north101.android.ghplayertracker.data;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class SelectableCharacter {
    public final Character character;
    public boolean selected;

    @ParcelConstructor
    public SelectableCharacter(Character character, boolean selected) {
        this.character = character;
        this.selected = selected;
    }
}
