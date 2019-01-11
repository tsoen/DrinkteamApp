package drinkteam.vue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.widget.EditText;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import drinkteam.PhotoDialog;
import com.drinkteam.R;


/**
 * Created by Timothée on 24/06/2016.
 *
 * Les préférences sont des données qui sont sauvegardées dans l'application :
 * par exemple, la langue de l'utilisateur
 */
public class Preferences_Page extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    /**
     * A la création d'un fragment de préférences
     * @param savedInstanceState Etat sauvegardé de l'application si elle a été mise de côté
     * @param rootKey .
     */
    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey)
    {
		// titre de l'actionBar //
		Bundle args = getArguments();
		((MainActivity)getActivity()).setTitreActionBar(args.getString("titre_actionbar"));
		
        // charge les éléments présent danss le PreferenceScreen qui a rootKey comme attribut 'key' //
        setPreferencesFromResource(R.xml.preferences_page, rootKey);
		
        // détecte et enregistre les changements de préférences sur ce PreferenceScreen //
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Preferences_Page est forcément un groupe (ou conteneur) d'autres préférences //
        PreferenceGroup pGroup = (PreferenceGroup) findPreference(rootKey);
		
        // parcourt les préférences contenues dans ce groupe //
        int pCount = pGroup.getPreferenceCount();
        for(int i = 0; i < pCount; i++)
        {
            // si la préférence d'index i est une ListPreference //
            if (pGroup.getPreference(i) instanceof ListPreference)
            {
                // mise à jour du summary (indication dans le menu) de la préférence //
                ListPreference listPreference = (ListPreference) pGroup.getPreference(i);
                listPreference.setSummary(listPreference.getEntry());
            }
            // si la préférence d'index i est un editText //
            else if(pGroup.getPreference(i) instanceof EditTextPreference)
            {
                EditTextPreference editTextPreference = (EditTextPreference) pGroup.getPreference(i);
                EditText editText = editTextPreference.getEditText();
				
                // par défaut, le summary correspond au 'hint' //
                CharSequence summary = editText.getHint();
                editTextPreference.setSummary(summary);
            }
        }
    }
	
	/**
	 * Ouverture de l'écran des préférences, on s'assure que les summary des préférences sont à jour
	 */
    @Override
    public void onResume()
    {
        super.onResume();
		
        // vérifie toutes les préférences affichées //
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i)
        {
            Preference preference = getPreferenceScreen().getPreference(i);
			
            // si on rencontre un groupe de Préférence //
            if (preference instanceof PreferenceGroup)
            {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
				
                // met à jour les summary des préférences qu'il contient //
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j)
                {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey());
                }
            }
            // si on rencontre une préférence simple, on met à jour son summary //
            else
            {
                updatePreference(preference, preference.getKey());
            }
        }
    }
	
	/**
	 * Modification du summary d'une préférence à chaque modification
	 * @param sharedPreferences Liste des préférences de l'application
	 * @param key Clé de la préférence modifiée dans la liste des préférences
	 */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key), key);
    }
	
	/**
	 * Met à jour le summary d'une préférence
	 * @param preference L''affichage de la préférence à modifier
	 * @param key Clé de la préférence dans la liste des préférence
	 */
    private void updatePreference(Preference preference, String key) {

        if (preference == null) {
			return;
        }

        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) findPreference(key);
            listPreference.setSummary(listPreference.getEntry());
        }
        if(preference instanceof EditTextPreference)
        {
            EditTextPreference editTextPreference = (EditTextPreference) findPreference(key);
            EditText editText = editTextPreference.getEditText();
            if(editText.getText().length() > 0)
            {
                // si on a rentré du texte dans un editText, le summary prend la valeur du texte //
                editTextPreference.setSummary(editTextPreference.getText());
            }
            // si la zone de texte est vide, le summary reprend la valeur de l'hint //
            else
            {
                editTextPreference.setSummary(editText.getHint());
            }
        }
        if(preference instanceof Preferences_Date){

            Preferences_Date datePreference = (Preferences_Date) preference;

            if(!datePreference.getText().equals(datePreference.getDefaultDate())) {
                datePreference.setSummary(datePreference.getText());
            }
        }
    }
	
	/**
	 * Comportement lors du clique sur une préférence
	 * @param preference Préférence sélectionné
	 * @return Validation
	 */
    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();

        // ces préférences sont personnalisées, il faut donc gérer manuellement leur déclenchement //
		switch (key){
			
			case "profilPic":
				PhotoDialog profileDialog = PhotoDialog.newInstance("profile");
				profileDialog.show(getActivity().getSupportFragmentManager(), "fragment_photoDialog");
				break;
			
			case "backPic":
				PhotoDialog coverDialog = PhotoDialog.newInstance("cover");
				coverDialog.show(getActivity().getSupportFragmentManager(), "fragment_photoDialog");
				break;
			
			case "deconnexion":
				LoginManager.getInstance().logOut();
				FirebaseAuth.getInstance().signOut();
				Activity mainActivity = getActivity();
				Intent intent = new Intent(mainActivity, ConnexionActivity.class);
				startActivity(intent);
				mainActivity.finish();
				break;
			
			default:
				break;
		}

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment fragment;
        if (preference instanceof Preferences_Date) {
            fragment = Preferences_DateFragment.newInstance(preference);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragmentCompat.DIALOG");
        }
        else {
			super.onDisplayPreferenceDialog(preference);
		}
    }
}
