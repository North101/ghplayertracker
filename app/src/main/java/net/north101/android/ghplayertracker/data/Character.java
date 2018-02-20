package net.north101.android.ghplayertracker.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Parcel
public class Character {
    public static TimeZone TIMEZONE = TimeZone.getTimeZone("UTC");
    public static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static int PERK_NOTES_COUNT = 6;

    static {
        DATE_FORMATTER.setTimeZone(TIMEZONE);
    }

    protected final UUID id;
    protected final CharacterClass characterClass;
    protected String name;
    protected int xp;
    protected int level;
    protected int gold;
    protected final int[] perks;
    protected final PerkNote[] perkNotes = new PerkNote[PERK_NOTES_COUNT];
    protected int minus1;
    protected final Date created;
    protected Date modified;

    @ParcelConstructor
    public Character(UUID id, CharacterClass characterClass, String name, int xp, int level, int gold, int minus1, int[] perks, PerkNote[] perkNotes, Date created, Date modified) {
        this.id = id;
        this.characterClass = characterClass;
        this.setName(name);
        this.setXP(xp);
        this.setLevel(level);
        this.setGold(gold);
        this.setMinus1(minus1);
        this.created = created;
        this.setModified(modified);

        this.perks = new int[characterClass.perks.size()];
        System.arraycopy(perks, 0, this.perks, 0, Math.min(perks.length, this.perks.length));

        System.arraycopy(perkNotes, 0, this.perkNotes, 0, Math.min(perkNotes.length, PERK_NOTES_COUNT));
        for (int i = 0; i < this.perkNotes.length; i++) {
            if (this.perkNotes[i] == null) {
                this.perkNotes[i] = new PerkNote(0);
            }
        }
    }

    public Character(CharacterClass characterClass) {
        this(UUID.randomUUID(), characterClass, characterClass.name, 0, 1, 0, 0, new int[0], new PerkNote[0], new Date(), new Date());
    }

    public UUID getId() {
        return id;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXP() {
        return xp;
    }

    public void setXP(int xp) {
        this.xp = Math.max(xp, 0);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(Math.max(level, CharacterClass.LEVEL_MIN), CharacterClass.LEVEL_MAX);
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = Math.max(gold, 0);
    }

    public int[] getPerks() {
        return perks;
    }

    public PerkNote[] getPerkNotes() {
        return perkNotes;
    }

    public int getMinus1() {
        return minus1;
    }

    public void setMinus1(int minus1) {
        this.minus1 = Math.max(minus1, 0);
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public static Character parse(Context context, JSONObject data) throws JSONException, IOException, ParseException {
        UUID id = UUID.fromString(data.getString("id"));

        String className = data.getString("class");
        CharacterClass characterClass = CharacterClass.load(context, className);

        String name = data.getString("name");
        int xp = data.getInt("xp");
        int level = data.getInt("level");
        int gold = data.getInt("gold");
        int minus1 = data.getInt("minus1");

        JSONArray perksData = data.getJSONArray("perks");
        int[] perks = new int[perksData.length()];
        for (int i = 0; i < perksData.length(); i++) {
            perks[i] = perksData.getInt(i);
        }

        JSONArray perkNotesData = data.getJSONArray("perk_notes");
        PerkNote[] perkNotes = new PerkNote[perkNotesData.length()];
        for (int i = 0; i < perkNotesData.length(); i++) {
            perkNotes[i] = new PerkNote(perkNotesData.getInt(i));
        }

        Date created = DATE_FORMATTER.parse(data.getString("created"));
        Date modified = DATE_FORMATTER.parse(data.getString("modified"));

        return new Character(id, characterClass, name, xp, level, gold, minus1, perks, perkNotes, created, modified);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("id", id.toString());
        data.put("class", characterClass.id);
        data.put("name", name);
        data.put("xp", xp);
        data.put("level", level);
        data.put("gold", gold);
        data.put("minus1", minus1);
        data.put("perks", new JSONArray(perks));
        JSONArray perkNotes = new JSONArray();
        for (PerkNote perkNote : this.perkNotes) {
            perkNotes.put(perkNote.getTicks());
        }
        data.put("perk_notes", perkNotes);
        data.put("created", DATE_FORMATTER.format(created));
        data.put("modified", DATE_FORMATTER.format(modified));

        return data;
    }

    public Level getCurrentLevel() {
        return characterClass.levels[level - 1];
    }

    public int getMaxHealth() {
        return getCurrentLevel().health;
    }
}
