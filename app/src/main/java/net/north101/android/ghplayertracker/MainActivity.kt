package net.north101.android.ghplayertracker

import android.support.v7.app.AppCompatActivity
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.InstanceState

@EActivity(R.layout.main_layout)
open class MainActivity : AppCompatActivity() {
    @JvmField
    @InstanceState
    protected var init = false

    @AfterViews
    fun afterViews() {
        if (init) return

        val fragment = CharacterListFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, fragment)
            .commit()

        init = true
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        if (fragment !is OnBackPressedListener) {
            super.onBackPressed()
            return
        }

        if ((fragment as OnBackPressedListener).onBackPressed())
            return

        super.onBackPressed()
    }
}
