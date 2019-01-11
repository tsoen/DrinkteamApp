package drinkteam.vue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.DatePicker;

/**
 * Created by Timothée on 25/06/2016.
 *
 * Fragment contenant le Dialog de sélection d'une date
 */
public class Preferences_DateFragment extends PreferenceDialogFragmentCompat {
    private static int lastDate = 0;
    private static int lastMonth = 0;
    private static int lastYear = 0;
    private DatePicker picker = null;
    private static String dateval;
	
	private static Preference preferenceIsDatePreference;
    private Preferences_Date datePref;

    public static Preferences_DateFragment newInstance(Preference preference) {
        Preferences_DateFragment fragment = new Preferences_DateFragment();
		
		preferenceIsDatePreference = preference;
		
        dateval = ((Preferences_Date)preference).getText();
        lastDate = ((Preferences_Date)preference).getDate(dateval);
        lastMonth = ((Preferences_Date)preference).getMonth(dateval)-1;
        lastYear = ((Preferences_Date)preference).getYear(dateval);

        Bundle bundle = new Bundle();
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected View onCreateDialogView(Context context) {
		datePref = (Preferences_Date)preferenceIsDatePreference;
	
		this.picker = new DatePicker(context);
        return this.picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.updateDate(lastYear, lastMonth, lastDate);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
        {
            lastDate = picker.getDayOfMonth();
            lastMonth = picker.getMonth();
            lastYear = picker.getYear();

            dateval = String.valueOf(lastDate) + "-"
                    + String.valueOf((lastMonth)+1) + "-"
                    + String.valueOf(lastYear);

            SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor preferences = prefs.edit();
            preferences.putString("naissance_user", dateval);
            preferences.apply();

            datePref.setText(dateval);
        }
    }
}
