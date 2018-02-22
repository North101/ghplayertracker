package net.north101.android.ghplayertracker;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;

@EActivity(R.layout.main_layout)
public class MainActivity extends AppCompatActivity {
    @InstanceState
    boolean init = false;

    @AfterViews
    void afterViews() {
        if (init) return;

        Fragment fragment = new CharacterListFragment_();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, fragment)
            .commit();

        init = true;
    }
}
