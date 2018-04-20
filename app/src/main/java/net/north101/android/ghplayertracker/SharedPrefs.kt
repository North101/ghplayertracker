package net.north101.android.ghplayertracker

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean
import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref
interface SharedPrefs {
    @DefaultBoolean(keyRes = R.string.prefs_house_rule_vantage, value = false)
    fun houseRuleVantage(): Boolean
}
