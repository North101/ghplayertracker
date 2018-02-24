package net.north101.android.ghplayertracker;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface SharedPrefs {
    @DefaultBoolean(keyRes = R.string.prefs_house_rule_vantage, value = false)
    boolean houseRuleVantage();
}
