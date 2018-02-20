package net.north101.android.ghplayertracker.data;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;

@Parcel
public class PlayedCards {
    public final ArrayList<Card> pile1;
    public final ArrayList<Card> pile2;
    public boolean shuffled;

    @ParcelConstructor
    public PlayedCards(ArrayList<Card> pile1, ArrayList<Card> pile2, boolean shuffled) {
        this.pile1 = pile1;
        this.pile2 = pile2;
        this.shuffled = shuffled;
    }
}
