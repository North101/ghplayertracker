package net.north101.android.ghplayertracker;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.north101.android.ghplayertracker.data.BasicCards;
import net.north101.android.ghplayertracker.data.Card;
import net.north101.android.ghplayertracker.data.CardSpecial;
import net.north101.android.ghplayertracker.data.Character;
import net.north101.android.ghplayertracker.data.CharacterTracker;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.north101.android.ghplayertracker.data.PlayedCards;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

@EFragment(R.layout.character_tracker_layout)
public class CharacterTrackerFragment extends Fragment {
    Random randomGenerator = new Random();

    ActionBar actionBar;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.health_text)
    EditText healthTextView;
    @ViewById(R.id.health_plus)
    ImageView healthPlusView;
    @ViewById(R.id.health_minus)
    ImageView healthMinusView;
    @ViewById(R.id.xp_text)
    EditText xpTextView;
    @ViewById(R.id.xp_plus)
    ImageView xpPlusView;
    @ViewById(R.id.xp_minus)
    ImageView xpMinusView;
    @ViewById(R.id.gold_text)
    EditText goldTextView;
    @ViewById(R.id.gold_plus)
    ImageView goldPlusView;
    @ViewById(R.id.gold_minus)
    ImageView goldMinusView;
    @ViewById(R.id.draw_deck)
    ImageView drawDeckView;
    @ViewById(R.id.status_disarm)
    ImageView statusDisarmView;
    @ViewById(R.id.status_stun)
    ImageView statusStunView;
    @ViewById(R.id.status_immobilize)
    ImageView statusImmobilizeView;
    @ViewById(R.id.status_strengthen)
    ImageView statusStrengthenView;
    @ViewById(R.id.status_poison)
    ImageView statusPoisonView;
    @ViewById(R.id.status_wound)
    ImageView statusWoundView;
    @ViewById(R.id.status_muddle)
    ImageView statusMuddleView;
    @ViewById(R.id.status_invisible)
    ImageView statusInvisibleView;
    @ViewById(R.id.bless_text)
    EditText blessTextView;
    @ViewById(R.id.bless_plus)
    ImageView blessPlusView;
    @ViewById(R.id.bless_minus)
    ImageView blessMinusView;
    @ViewById(R.id.curse_text)
    EditText curseTextView;
    @ViewById(R.id.curse_plus)
    ImageView cursePlusView;
    @ViewById(R.id.curse_minus)
    ImageView curseMinusView;
    @ViewById(R.id.split)
    ImageView splitIconView;
    @ViewById(R.id.shuffle)
    ImageView shuffleIconView;
    @ViewById(R.id.active_deck_list)
    RecyclerView playedCardsListView;

    PlayedCardsAdapter playedCardsAdapter;

    @FragmentArg("character")
    Character character;

    @InstanceState
    CharacterTracker characterTracker;
    @InstanceState
    BasicCards basicCards;
    @InstanceState
    boolean split = false;
    @InstanceState
    boolean shuffle = false;
    @InstanceState
    Card blessCard;
    @InstanceState
    Card curseCard;

    Unregistrar keyboardEeventListener;

    @AfterViews
    void afterViews() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (basicCards == null) {
            try {
                basicCards = BasicCards.load(getContext());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return;
            }
            blessCard = Card.get("mod_extra_bless_remove");
            curseCard = Card.get("mod_extra_curse_remove");
        }

        if (characterTracker == null) {
            characterTracker = new CharacterTracker(character, basicCards);
        }

        int iconId = Util.getImageResource(getContext(), "icon_" + character.getCharacterClass().id);
        actionBar.setLogo(iconId);
        actionBar.setTitle(character.getName());
        actionBar.setBackgroundDrawable(new ColorDrawable(character.getCharacterClass().color));

        updateHealthText();
        updateXPText();
        updateGoldText();
        updateStatusView();
        updateBlessText();
        updateCurseText();

        playedCardsAdapter = new PlayedCardsAdapter();
        GridLayoutManager activeDeckListLayoutManager = new GridLayoutManager(getContext(), 2);
        activeDeckListLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Object item = playedCardsAdapter.getItem(position);
                if (item instanceof PlayedCardsAdapter.CardInfo) {
                    return ((PlayedCardsAdapter.CardInfo) item).split ? 1 : 2;
                } else if (item instanceof PlayedCardsAdapter.CardDivider) {
                    return 2;
                } else if (item instanceof PlayedCardsAdapter.CardHeader) {
                    return 2;
                } else {
                    throw new RuntimeException();
                }
            }
        });

        FadeInDownAnimator animator = new FadeInDownAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        playedCardsListView.setItemAnimator(animator);
        playedCardsListView.setLayoutManager(activeDeckListLayoutManager);

        playedCardsListView.setAdapter(playedCardsAdapter);
        for (int i = characterTracker.getPlayedCardsHistory().size() - 1; i >= 0; i--) {
            playedCardsAdapter.addItem(characterTracker.getPlayedCardsHistory().get(i));
        }
        playedCardsAdapter.updateShuffledHeaderPosition();

        setSplit(split);
        setShuffleEnabled(shuffle);
        updateActiveDecks();
        ViewCompat.setNestedScrollingEnabled(playedCardsListView, false);
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
    }

    @Click(R.id.status_disarm)
    void onStatusDisarmClick() {
        characterTracker.getStatusSet().disarm = !characterTracker.getStatusSet().disarm;
        updateStatusDisarmView();
    }

    void updateStatusDisarmView() {
        setImageViewGreyscale(this.statusDisarmView, !characterTracker.getStatusSet().disarm);
    }

    @Click(R.id.status_stun)
    void onStatusStunClick() {
        characterTracker.getStatusSet().stun = !characterTracker.getStatusSet().stun;
        updateStatusStunView();
    }

    void updateStatusStunView() {
        setImageViewGreyscale(this.statusStunView, !characterTracker.getStatusSet().stun);
    }

    @Click(R.id.status_immobilize)
    void onStatusImmobilizeClick() {
        characterTracker.getStatusSet().immobilize = !characterTracker.getStatusSet().immobilize;
        updateStatusImmobilizeView();
    }

    void updateStatusImmobilizeView() {
        setImageViewGreyscale(this.statusImmobilizeView, !characterTracker.getStatusSet().immobilize);
    }

    @Click(R.id.status_poison)
    void onStatusPoisonClick() {
        characterTracker.getStatusSet().poison = !characterTracker.getStatusSet().poison;
        updateStatusPoisonView();
    }

    void updateStatusPoisonView() {
        setImageViewGreyscale(this.statusPoisonView, !characterTracker.getStatusSet().poison);
    }

    @Click(R.id.status_wound)
    void onStatusWoundClick() {
        characterTracker.getStatusSet().wound = !characterTracker.getStatusSet().wound;
        updateStatusWoundView();
    }

    void updateStatusWoundView() {
        setImageViewGreyscale(this.statusWoundView, !characterTracker.getStatusSet().wound);
    }

    @Click(R.id.status_muddle)
    void onStatusMuddleClick() {
        characterTracker.getStatusSet().muddle = !characterTracker.getStatusSet().muddle;
        updateStatusMuddleView();
    }

    void updateStatusMuddleView() {
        setImageViewGreyscale(this.statusMuddleView, !characterTracker.getStatusSet().muddle);
    }

    @Click(R.id.status_strengthen)
    void onStatusStrengthenClick() {
        characterTracker.getStatusSet().strengthen = !characterTracker.getStatusSet().strengthen;
        updateStatusStrengthenView();
    }

    void updateStatusStrengthenView() {
        setImageViewGreyscale(this.statusStrengthenView, !characterTracker.getStatusSet().strengthen);
    }

    @Click(R.id.status_invisible)
    void onStatusInvisibleClick() {
        characterTracker.getStatusSet().invisible = !characterTracker.getStatusSet().invisible;
        updateStatusInvisibleView();
    }

    void updateStatusInvisibleView() {
        setImageViewGreyscale(this.statusInvisibleView, !characterTracker.getStatusSet().invisible);
    }

    void updateStatusView() {
        updateStatusDisarmView();
        updateStatusStunView();
        updateStatusImmobilizeView();
        updateStatusPoisonView();
        updateStatusWoundView();
        updateStatusMuddleView();
        updateStatusStrengthenView();
        updateStatusInvisibleView();
    }

    void setImageViewGreyscale(ImageView imageView, boolean set) {
        if (set) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);  //0 means grayscale
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(cf);
            imageView.setAlpha(0.5f);
        } else {
            imageView.setColorFilter(null);
            imageView.setAlpha(1.0f);
        }
    }

    @Click(R.id.draw_deck)
    void onDrawDeckClicked() {
        if (split) {
            ArrayList<Card> item1 = drawCards();
            ArrayList<Card> item2 = drawCards();
            if (hasShuffle(item1) || hasShuffle(item2)) {
                setShuffleEnabled(true);
            }

            characterTracker.getPlayedCardsHistory().add(0, new PlayedCards(item1, item2, false));
        } else {
            ArrayList<Card> item = drawCards();
            if (hasShuffle(item)) {
                setShuffleEnabled(true);
            }
            characterTracker.getPlayedCardsHistory().add(0, new PlayedCards(item, null, false));
        }
        playedCardsAdapter.addItem(characterTracker.getPlayedCardsHistory().get(0));
        updateActiveDecks();
    }

    @Click(R.id.split)
    void onSplitIconClick() {
        toggleSplit();
    }

    @Click(R.id.shuffle)
    void onShuffleIconClick() {
        shuffle();
    }

    @Click(R.id.health_minus)
    void onHealthMinusClick() {
        characterTracker.setHealth(characterTracker.getHealth() - 1);
        updateHealthText();
    }

    @Click(R.id.health_plus)
    void onHealthPlusClick() {
        characterTracker.setHealth(characterTracker.getHealth() + 1);
        updateHealthText();
    }

    @EditorAction(R.id.health_text)
    boolean onHealthTextChange(TextView tv, int actionId) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        parseHealthText();
        return false;
    }

    @FocusChange(R.id.health_text)
    void onHealthTextFocus() {
        if (healthTextView == null || healthTextView.hasFocus()) return;

        parseHealthText();
    }

    void parseHealthText() {
        try {
            characterTracker.setHealth(Integer.parseInt(healthTextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid Health value", Toast.LENGTH_SHORT).show();
        }
        updateHealthText();
    }

    void updateHealthText() {
        String text = String.valueOf(characterTracker.getHealth());
        if (!text.equals(healthTextView.getText().toString())) {
            healthTextView.setText(text);
        }
    }

    @Click(R.id.xp_minus)
    void onXPMinusClick() {
        characterTracker.setXp(characterTracker.getXp() - 1);
        updateXPText();
    }

    @Click(R.id.xp_plus)
    void onXPPlusClick() {
        characterTracker.setXp(characterTracker.getXp() + 1);
        updateXPText();
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
            characterTracker.setXp(Integer.parseInt(xpTextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid XP value", Toast.LENGTH_SHORT).show();
        }
        updateXPText();
    }

    void updateXPText() {
        String text = String.valueOf(characterTracker.getXp());
        if (!text.equals(xpTextView.getText().toString())) {
            xpTextView.setText(text);
        }
    }

    @Click(R.id.gold_minus)
    void onGoldMinusClick() {
        characterTracker.setGold(characterTracker.getGold() - 1);
        updateGoldText();
    }

    @Click(R.id.gold_plus)
    void onGoldPlusClick() {
        characterTracker.setGold(characterTracker.getGold() + 1);
        updateGoldText();
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
            characterTracker.setGold(Integer.parseInt(goldTextView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid Gold value", Toast.LENGTH_SHORT).show();
        }
        updateGoldText();
    }

    void updateGoldText() {
        String text = String.valueOf(characterTracker.getGold());
        if (!text.equals(goldTextView.getText().toString())) {
            goldTextView.setText(text);
        }
    }

    @Click(R.id.bless_minus)
    void onBlessMinusClick() {
        characterTracker.getDeck().remove(blessCard);
        updateBlessText();
    }

    @Click(R.id.bless_plus)
    void onBlessPlusClick() {
        characterTracker.getDeck().add(blessCard);
        updateBlessText();
    }

    @EditorAction(R.id.bless_text)
    boolean onBlessTextChange(TextView tv, int actionId) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        parseBlessText();
        return false;
    }

    @FocusChange(R.id.bless_text)
    void onBlessTextFocus() {
        if (blessTextView == null || blessTextView.hasFocus()) return;

        parseBlessText();
    }

    void parseBlessText() {
        try {
            int newCount = Math.max(Integer.parseInt(blessTextView.getText().toString()), 0);

            int oldCount = 0;
            for (Card card : characterTracker.getDeck()) {
                if (card.equals(blessCard)) {
                    oldCount++;
                }
            }

            for (; oldCount > newCount; oldCount--) {
                characterTracker.getDeck().remove(blessCard);
            }
            for (; oldCount < newCount; oldCount++) {
                characterTracker.getDeck().add(blessCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid Bless Card count", Toast.LENGTH_SHORT).show();
        }
        updateBlessText();
    }

    void updateBlessText() {
        int count = 0;
        for (Card card : characterTracker.getDeck()) {
            if (card.equals(blessCard)) {
                count++;
            }
        }
        String text = String.valueOf(count);
        if (!text.equals(blessTextView.getText().toString())) {
            blessTextView.setText(String.valueOf(count));
        }
    }

    @Click(R.id.curse_minus)
    void onCurseMinusClick() {
        characterTracker.getDeck().remove(curseCard);
        updateCurseText();
    }

    @Click(R.id.curse_plus)
    void onCursePlusClick() {
        characterTracker.getDeck().add(curseCard);
        updateCurseText();
    }

    @EditorAction(R.id.curse_text)
    boolean onCurseTextChange(TextView tv, int actionId) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        parseCurseText();
        return false;
    }

    @FocusChange(R.id.curse_text)
    void onCurseTextFocus() {
        if (curseTextView == null || curseTextView.hasFocus()) return;

        parseCurseText();
    }

    void parseCurseText() {
        try {
            int newCount = Math.max(Integer.parseInt(curseTextView.getText().toString()), 0);

            int oldCount = 0;
            for (Card card : characterTracker.getDeck()) {
                if (card.equals(curseCard)) {
                    oldCount++;
                }
            }

            for (; oldCount > newCount; oldCount--) {
                characterTracker.getDeck().remove(curseCard);
            }
            for (; oldCount < newCount; oldCount++) {
                characterTracker.getDeck().add(curseCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid Curse Card count", Toast.LENGTH_SHORT).show();
        }
        updateCurseText();
    }

    void updateCurseText() {
        int count = 0;
        for (Card card : characterTracker.getDeck()) {
            if (card.equals(curseCard)) {
                count++;
            }
        }
        String text = String.valueOf(count);
        if (!text.equals(curseTextView.getText().toString())) {
            curseTextView.setText(String.valueOf(count));
        }
    }

    void toggleSplit() {
        setSplit(!this.split);
    }

    void setSplit(boolean split) {
        this.split = split;
        if (split) {
            splitIconView.setImageResource(R.drawable.ic_call_split_black_24dp);
        } else {
            splitIconView.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
        }
    }

    void setShuffleEnabled(boolean shuffle) {
        this.shuffle = shuffle;
        shuffleIconView.setEnabled(shuffle);
        shuffleIconView.setAlpha(shuffle ? 1.0f : 0.5f);
    }

    void shuffle() {
        setShuffleEnabled(false);

        for (PlayedCards playedCards : characterTracker.getPlayedCardsHistory()) {
            if (playedCards.shuffled)
                continue;

            for (Card card : playedCards.pile1) {
                if (card.special != CardSpecial.Remove) {
                    characterTracker.getDeck().add(card);
                }
            }
            if (playedCards.pile2 != null) {
                for (Card card : playedCards.pile2) {
                    if (card.special != CardSpecial.Remove) {
                        characterTracker.getDeck().add(card);
                    }
                }
            }
            playedCards.shuffled = true;
        }
        for (int i = 0; i < playedCardsAdapter.getItemCount(); i++) {
            Object item = playedCardsAdapter.getItem(i);
            if (item instanceof PlayedCardsAdapter.CardInfo) {
                if (!((PlayedCardsAdapter.CardInfo) item).shuffled) {
                    ((PlayedCardsAdapter.CardInfo) item).shuffled = true;
                    playedCardsAdapter.notifyItemChanged(i);
                }
            }
        }
        playedCardsAdapter.updateShuffledHeaderPosition();
        updateActiveDecks();

        Snackbar.make(getView(), "Shuffled", Snackbar.LENGTH_SHORT).show();
    }

    boolean hasShuffle(List<Card> cards) {
        for (Card card : cards) {
            if (card.special == CardSpecial.Shuffle) {
                return true;
            }
        }
        return false;
    }

    ArrayList<Card> drawCards() {
        ArrayList<Card> cards = new ArrayList<>();

        while (true) {
            if (characterTracker.getDeck().size() == 0) {
                shuffle();
                if (characterTracker.getDeck().size() == 0) {
                    break;
                }
            }

            int index = randomGenerator.nextInt(characterTracker.getDeck().size());
            Card card = characterTracker.getDeck().remove(index);
            cards.add(card);
            if (card.special != CardSpecial.Rolling)
                break;
        }

        return cards;
    }

    void updateActiveDecks() {
        updateBlessText();
        updateCurseText();

        if (characterTracker.getDeck().size() == 0) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);  //0 means grayscale
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            drawDeckView.setColorFilter(cf);
        } else {
            drawDeckView.setColorFilter(null);
        }
    }
}
