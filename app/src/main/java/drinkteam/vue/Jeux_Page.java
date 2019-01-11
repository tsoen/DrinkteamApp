package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import com.drinkteam.R;
import drinkteam.controleur.Utils;
import drinkteam.metier.Jeu;

/**
 * Created by Timothée on 18/06/2016.
 *
 * Page (Fragment) "Nos idées jeux" accessible depuis le menu
 * Liste les Jeux proposés par l'application
 */
public class Jeux_Page extends Fragment {
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
        return inflater.inflate(R.layout.jeux_page, container, false);
    }
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull final View view, Bundle savedInstanceState){
		
		// titre de l'actionBar //
		Bundle args = getArguments();
		((MainActivity)getActivity()).setTitreActionBar(args.getString("titre_actionbar"));
		
		// récupère la liste des succès de la base//
		List<Jeu> listJeux =  Utils.dbJeux.getAllJeux();
		
		// récupère la vue de la liste des cocktails //
		GridView gridView = view.findViewById(R.id.gridviewJeux);
		// donne la liste des Jeux à afficher au gestionnaire de la liste //
		Jeux_GridViewAdapter adapter = new Jeux_GridViewAdapter(getContext(), listJeux);
		// auto-scroll au dernier Jeu sélectionné quand on revient d'une Jeux_Fiche //
		int index = gridView.getFirstVisiblePosition();
		gridView.smoothScrollToPosition(index);
		gridView.setAdapter(adapter);
		
		// listener de click sur un Jeu de la liste //
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/**
			 * Action lors du click sur le bouton
			 * @param view Vue du bouton cliquée
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Bundle bundle = new Bundle();
				bundle.putLong("code", id);
				
				// affiche la fiche du Jeu //
				Utils.replaceFragments(Jeux_Fiche.class, getActivity(), bundle, true);
			}
		});
		
		/* The fuck was that, me ?
		gridView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					getFragmentManager().popBackStackImmediate();
					return true;
				}
				return false;
			}
		});
		*/
	}
}
