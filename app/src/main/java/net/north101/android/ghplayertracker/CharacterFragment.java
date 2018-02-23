package net.north101.android.ghplayertracker;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.north101.android.ghplayertracker.data.Character;
import net.north101.android.ghplayertracker.data.CharacterClass;
import net.north101.android.ghplayertracker.data.CharacterList;
import net.north101.android.ghplayertracker.data.CharacterPerk;
import net.north101.android.ghplayertracker.data.Level;
import net.north101.android.ghplayertracker.data.Perk;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.character_layout)
@OptionsMenu(R.menu.character)
public class CharacterFragment extends Fragment {
    ActionBar actionBar;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.max_health)
    TextView maxHealthView;
    @ViewById(R.id.xp_text)
    EditText xpTextView;
    @ViewById(R.id.levels_select)
    Spinner levelsView;
    @ViewById(R.id.gold_text)
    EditText goldTextView;
    @ViewById(R.id.perk_list)
    RecyclerView perkListView;
    @ViewById(R.id.perk_note_grid)
    RecyclerView perkNoteGridView;
    @ViewById(R.id.name)
    TextView nameView;
    @ViewById(R.id.minus_1_text)
    TextView minus1TextView;

    @InstanceState
    @FragmentArg("character")
    Character character;

    LevelAdapter levelAdapter;
    PerkAdapter perkAdapter;
    PerkNoteAdapter perkNoteAdapter;

    Unregistrar keyboardEeventListener;

    @AfterViews
    void afterViews() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        CharacterClass characterClass = character.getCharacterClass();

        int iconId = Util.getImageResource(getContext(), "icon_" + characterClass.id);
        actionBar.setLogo(iconId);
        actionBar.setTitle(characterClass.name);
        actionBar.setBackgroundDrawable(new ColorDrawable(characterClass.color));

        List<CharacterPerk> characterPerkList = new ArrayList<>();
        for (int i = 0; i < character.getPerks().length; i++) {
            Perk perk = characterClass.perks.get(i);
            characterPerkList.add(new CharacterPerk(perk, character.getPerks()[i]));
        }

        LinearLayoutManager perkListLayoutManager = new LinearLayoutManager(getContext());
        perkListView.setLayoutManager(perkListLayoutManager);
        perkListView.setItemAnimator(new DefaultItemAnimator());
        perkAdapter = new PerkAdapter(characterPerkList);
        perkListView.setAdapter(perkAdapter);
        ViewCompat.setNestedScrollingEnabled(perkListView, false);

        GridLayoutManager perkNoteListLayoutManager = new GridLayoutManager(getContext(), 3);
        perkNoteGridView.setLayoutManager(perkNoteListLayoutManager);
        perkNoteGridView.setItemAnimator(new DefaultItemAnimator());
        perkNoteAdapter = new PerkNoteAdapter(character.getPerkNotes());
        perkNoteGridView.setAdapter(perkNoteAdapter);
        ViewCompat.setNestedScrollingEnabled(perkListView, false);

        levelAdapter = new LevelAdapter(characterClass.levels);
        levelsView.setAdapter(levelAdapter);

        nameView.setText(character.getName());
        updateXPText();
        updateLevelText();
        updateMinus1Text();
        updateGoldText();
        updateHealthText();
    }

    @Override
    public void onResume() {
        super.onResume();

        //HACK!
        keyboardEeventListener = KeyboardVisibilityEvent.registerEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (!isOpen) {
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    if (activity == null) return;

                    View view = activity.getCurrentFocus();
                    if (view instanceof EditText) {
                        view.clearFocus();
                        view.requestFocus();
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (keyboardEeventListener != null) {
            keyboardEeventListener.unregister();
            keyboardEeventListener = null;
        }

        updateCharacter();
    }

    @OptionsItem(R.id.start)
    void onMenuStartClick() {
        updateCharacter();

        Bundle args = new Bundle();
        args.putParcelable("character", Parcels.wrap(character));

        Fragment fragment = new CharacterTrackerFragment_();
        fragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.content, fragment)
            .addToBackStack(null)
            .commit();
    }

    @ItemSelect(R.id.levels_select)
    void onLevelSelect(boolean selected, Level selectedItem) {
        character.setLevel(((Level) levelsView.getSelectedItem()).level);
        maxHealthView.setText(String.valueOf(selectedItem.health));
    }

    void updateHealthText() {
        Log.d("khhjv", String.valueOf(character.getLevel()));
        maxHealthView.setText(String.valueOf(character.getMaxHealth()));
    }

    void updateCharacter() {
        character.setName(nameView.getText().toString());
        parseXPText();
        parseGoldText();
        parseMinus1Text();

        for (int i = 0; i < perkAdapter.items.size(); i++) {
            CharacterPerk characterPerk = perkAdapter.items.get(i);
            character.getPerks()[i] = characterPerk.ticks;
        }
    }

    @EditorAction(R.id.xp_text)
    boolean onXPTextChange(TextView tv, int actionId) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        parseXPText();
        return false;
    }

    @FocusChange(R.id.xp_text)
    void onXPTextFocus() {
        if (xpTextView == null || xpTextView.hasFocus()) return;

        parseXPText();
    }

    void parseXPText() {
        try {
            character.setXP(Integer.parseInt(xpTextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid XP value", Toast.LENGTH_SHORT).show();
        }
        updateXPText();
    }

    void updateXPText() {
        String text = String.valueOf(character.getXP());
        if (!text.equals(xpTextView.getText().toString())) {
            xpTextView.setText(text);
        }
    }

    void updateLevelText() {
        levelsView.setSelection(character.getLevel() - 1);
    }

    @EditorAction(R.id.gold_text)
    boolean onGoldTextChange(TextView tv, int actionId) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        parseGoldText();
        return false;
    }

    @FocusChange(R.id.gold_text)
    void onGoldTextFocus() {
        if (goldTextView == null || goldTextView.hasFocus()) return;

        parseGoldText();
    }

    void parseGoldText() {
        try {
            character.setGold(Integer.parseInt(goldTextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid Gold value", Toast.LENGTH_SHORT).show();
        }
        updateGoldText();
    }

    void updateGoldText() {
        String text = String.valueOf(character.getGold());
        if (!text.equals(goldTextView.getText().toString())) {
            goldTextView.setText(text);
        }
    }

    @EditorAction(R.id.minus_1_text)
    boolean onMinus1TextChange(TextView tv, int actionId) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        parseMinus1Text();
        return false;
    }

    @FocusChange(R.id.minus_1_text)
    void onMinus1TextFocus() {
        if (minus1TextView == null || minus1TextView.hasFocus()) return;

        parseMinus1Text();
    }

    void parseMinus1Text() {
        try {
            character.setMinus1(Integer.parseInt(minus1TextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid -1 Attack modifier value", Toast.LENGTH_SHORT).show();
        }
        updateMinus1Text();
    }

    void updateMinus1Text() {
        String text = String.valueOf(character.getMinus1());
        if (!text.equals(minus1TextView.getText().toString())) {
            minus1TextView.setText(text);
        }
    }

    @OptionsItem(R.id.save)
    void onMenuSaveClick() {
        saveCharacter();
    }

    void saveCharacter() {
        saveCharacterTask();
    }

    @Background
    void saveCharacterTask() {
        JSONObject data;
        try {
            data = CharacterList.loadJSON(getContext());
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();

            data = new JSONObject();
        }

        try {
            data.put(character.getId().toString(), character.toJSON());
            CharacterList.parse(getContext(), data).save(getContext());
        } catch (IOException | JSONException e) {
            e.printStackTrace();

            Snackbar.make(getView(), "Failed to save", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Snackbar.make(getView(), "Saved", Snackbar.LENGTH_SHORT).show();
    }

    public class LevelAdapter extends BaseAdapter {
        public final Level[] items;

        public LevelAdapter(Level[] items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return this.items.length;
        }

        @Override
        public Level getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }
            ((TextView) view.findViewById(android.R.id.text1)).setText(String.valueOf(getItem(position).level));
            return view;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.spinner_text_view, parent, false);
            }
            ((TextView) view).setText(String.valueOf(getItem(position).level));
            return view;
        }
    }
}
