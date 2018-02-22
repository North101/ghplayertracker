package net.north101.android.ghplayertracker.data;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Parcel
public class CharacterTracker {
    protected final Character character;
    protected int health;
    protected int xp;
    protected int gold;
    protected final StatusSet statusSet;
    protected final List<Card> deck;
    protected final ArrayList<PlayedCards> playedCardsHistory;

    @ParcelConstructor
    public CharacterTracker(Character character, int health, int xp, int gold, StatusSet statusSet, List<Card> deck, ArrayList<PlayedCards> playedCardsHistory) {
        this.character = character;
        this.health = health;
        this.xp = xp;
        this.gold = gold;
        this.statusSet = statusSet;
        this.deck = deck;
        this.playedCardsHistory = playedCardsHistory;
    }

    public CharacterTracker(Character character, BasicCards basicCards) {
        this(character, character.getCurrentLevel().health, 0, 0, new StatusSet(), new ArrayList<Card>(), new ArrayList<PlayedCards>());

        for (Map.Entry<String, Integer> entry : basicCards.basicDeck.entrySet()) {
            Card card = Card.get(entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
                deck.add(card);
            }
        }

        Card minus1Card = Card.get("mod_extra_minus_1");
        for (int i = 0; i < character.getMinus1(); i++) {
            deck.add(minus1Card);
        }

        for (int perkIndex = 0; perkIndex < character.getPerks().length; perkIndex++) {
            Perk perk = character.getCharacterClass().perks.get(perkIndex);
            int perkTicks = character.getPerks()[perkIndex];
            for (int perkTick = 0; perkTick < perkTicks; perkTick++) {
                for (PerkItem perkItem : perk.perkItems) {
                    Card card = Card.get(perkItem.cardId);
                    if (perkItem.perkAction == PerkAction.add) {
                        for (int perkItemCount = 0; perkItemCount < perkItem.repeat; perkItemCount++) {
                            deck.add(card);
                        }
                    } else if (perkItem.perkAction == PerkAction.remove) {
                        for (int perkItemCount = 0; perkItemCount < perkItem.repeat; perkItemCount++) {
                            deck.remove(card);
                        }
                    }
                }
            }
        }
    }

    public Character getCharacter() {
        return character;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.min(Math.max(health, 0), character.getCurrentLevel().health);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(xp, 0);
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = Math.max(gold, 0);
    }

    public StatusSet getStatusSet() {
        return statusSet;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public ArrayList<PlayedCards> getPlayedCardsHistory() {
        return playedCardsHistory;
    }

}
