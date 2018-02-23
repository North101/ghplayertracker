package net.north101.android.ghplayertracker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import net.north101.android.ghplayertracker.data.Character;
import net.north101.android.ghplayertracker.data.CharacterClass;
import net.north101.android.ghplayertracker.data.ClassList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

@EFragment(R.layout.class_list_layout)
public class ClassListFragment extends Fragment {
    @ViewById(R.id.class_list)
    RecyclerView listView;
    @ViewById(R.id.loading)
    View loadingView;

    @InstanceState
    ClassList classList;

    ClassListAdapter listAdapter;

    @AfterViews
    void afterViews() {
        listAdapter = new ClassListAdapter(new ClassList(new ArrayList<String>()));
        listAdapter.setOnClickListener(onClickListener);

        boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), landscape ? 4 : 2);
        listView.setLayoutManager(gridLayoutManager);

        FadeInDownAnimator animator = new FadeInDownAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        listView.setItemAnimator(animator);

        listView.setAdapter(listAdapter);

        if (classList == null) {
            loadClasses();
        } else {
            setClassList(classList);
        }

    }

    void loadClasses() {
        listView.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);

        loadClassesTask(getContext());
    }

    @Background()
    void loadClassesTask(Context context) {
        ClassList classList;
        try {
            classList = ClassList.load(context);
        } catch (IOException | JSONException e) {
            e.printStackTrace();

            classList = null;
        }
        setClassList(classList);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void setClassList(ClassList classList) {
        if (this.isRemoving()) {
            return;
        }

        this.classList = classList;

        loadingView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        int oldSize = listAdapter.items.size();
        listAdapter.items.clear();
        listAdapter.notifyItemRangeRemoved(0, oldSize);
        if (classList != null) {
            listAdapter.items.addAll(this.classList.classList);
            listAdapter.notifyItemRangeInserted(0, listAdapter.items.size());
            listView.scheduleLayoutAnimation();
        }
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
