package net.north101.android.ghplayertracker;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.north101.android.ghplayertracker.data.Character;
import net.north101.android.ghplayertracker.data.CharacterClass;
import net.north101.android.ghplayertracker.data.ClassList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;

@EFragment(R.layout.class_list_layout)
public class ClassListFragment extends Fragment {
    @ViewById(R.id.class_list)
    RecyclerView listView;

    @InstanceState
    ClassList classList;

    ClassListAdapter listAdapter;

    @AfterViews
    void afterViews() {
        if (classList == null) {
            try {
                classList = ClassList.load(getContext());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        listAdapter = new ClassListAdapter(classList);
        listAdapter.setOnClickListener(onClickListener);

        boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), landscape ? 4 : 2);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(listAdapter);
    }

    BaseViewHolder.ClickListener<String> onClickListener = new BaseViewHolder.ClickListener<String>() {
        @Override
        public void onItemClick(BaseViewHolder<String> holder) {
            CharacterClass characterClass;
            try {
                characterClass = CharacterClass.load(getContext(), holder.item);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return;
            }

            Character character = new Character(characterClass);

            Fragment fragment = new CharacterFragment_();
            Bundle args = new Bundle();
            args.putParcelable("character", Parcels.wrap(character));
            fragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
        }
    };
}
