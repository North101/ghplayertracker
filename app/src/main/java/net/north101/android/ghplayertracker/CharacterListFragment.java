package net.north101.android.ghplayertracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import net.north101.android.ghplayertracker.data.Character;
import net.north101.android.ghplayertracker.data.CharacterList;
import net.north101.android.ghplayertracker.data.SelectableCharacter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

@EFragment(R.layout.character_list_layout)
public class CharacterListFragment extends Fragment implements ActionMode.Callback {
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.fab)
    FloatingActionButton fab;
    @ViewById(R.id.character_list)
    RecyclerView listView;

    CharacterListAdapter listAdapter;

    @AfterViews
    void afterViews() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ClassListFragment_();
                getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });

        CharacterList characterList;
        try {
            characterList = CharacterList.load(getContext());
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();

            characterList = new CharacterList();
        }

        LinearLayoutManager listViewLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(listViewLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), listViewLayoutManager.getOrientation());
        listView.addItemDecoration(dividerItemDecoration);

        FadeInDownAnimator animator = new FadeInDownAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        listView.setItemAnimator(animator);

        listAdapter = new CharacterListAdapter(new ArrayList<SelectableCharacter>());
        listAdapter.setOnClickListener(onClickListener);
        listView.setAdapter(listAdapter);

        setCharacterList(characterList);
    }

    public ActionMode actionMode;

    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    BaseViewHolder.ClickListener<SelectableCharacter> onClickListener = new BaseViewHolder.ClickListener<SelectableCharacter>() {
        @Override
        public void onItemClick(BaseViewHolder<SelectableCharacter> holder) {
            if (actionMode != null) {
                holder.item.selected = !holder.item.selected;
                listAdapter.notifyItemChanged(holder.getAdapterPosition());
                return;
            }

            Fragment fragment = new CharacterFragment_();
            Bundle args = new Bundle();
            args.putParcelable("character", Parcels.wrap(holder.item.character));
            fragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
        }

        @Override
        public boolean onItemLongClick(BaseViewHolder<SelectableCharacter> holder) {
            if (actionMode != null) {
                actionMode.finish();
                return true;
            }

            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(CharacterListFragment.this);
            holder.item.selected = true;
            listAdapter.notifyItemChanged(holder.getAdapterPosition());

            return true;
        }
    };

    void setCharacterList(CharacterList characterList) {
        ArrayList<SelectableCharacter> selectableCharacterList = new ArrayList<>();
        for (Character character : characterList.values()) {
            selectableCharacterList.add(new SelectableCharacter(character, false));
        }
        Collections.sort(selectableCharacterList, new Comparator<SelectableCharacter>() {
            @Override
            public int compare(SelectableCharacter item1, SelectableCharacter item2) {
                return item2.character.getModified().compareTo(item1.character.getModified());
            }
        });

        int oldSize = listAdapter.items.size();
        listAdapter.items.clear();
        listAdapter.notifyItemRangeRemoved(0, oldSize);
        listAdapter.items.addAll(selectableCharacterList);
        listAdapter.notifyItemRangeInserted(0, listAdapter.items.size());
    }

    void deleteSelectedCharacters() {
        CharacterList newCharacterList;
        try {
            newCharacterList = CharacterList.load(getContext());
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
            return;
        }

        int charactersDeleted = 0;
        for (SelectableCharacter character : listAdapter.items) {
            if (character.selected) {
                newCharacterList.remove(character.character.getId());
                charactersDeleted++;
            }
        }

        try {
            newCharacterList.save(getContext());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        setCharacterList(newCharacterList);

        Snackbar.make(getView(), "Deleted " + String.valueOf(charactersDeleted) + " character(s)", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.character_list_action_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete) {
            deleteSelectedCharacters();
            actionMode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        for (int i = 0; i < listAdapter.items.size(); i++) {
            listAdapter.items.get(i).selected = false;
            listAdapter.notifyItemChanged(i);
        }
    }
}
