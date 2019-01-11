package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.drinkteam.R;

import java.util.List;

import drinkteam.controleur.Utils;
import drinkteam.metier.Succes;

/**
 * Created by Timothée on 18/06/2016.
 *
 * Page (Fragment) "Mes succès" accessible depuis le menu
 * Liste les succès disponibles et les scores obtenus
 */
public class Succes_Page extends Fragment {
	
	/**
	 * Création de la vue à partir du layout dédié
	 * @param inflater Objet utilisé pour remplir la vue
	 * @param container Vue parente qui contient la vue
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 * @return Vue créée
	 */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// associe le layout au fragment //
        return inflater.inflate(R.layout.succes_page, container, false);
    }
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState){
		
		Bundle bundle = getArguments();
	
		// titre de l'actionBar //
		((MainActivity)getActivity()).setTitreActionBar(bundle.getString("titre_actionbar"));
		
		// vue de la liste des Succès //
        ExpandableListView listView = view.findViewById(R.id.expandListViewSucces);
		
		// calcule et affiche les niveaux obtenus pour chaque succès dans la listView //
		Utils.setUserNiveauSucces(bundle, listView);
    }
}
