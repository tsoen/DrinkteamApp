package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import com.drinkteam.R;
import drinkteam.controleur.Utils;
import drinkteam.metier.Cocktail;

/**
 * Created by Timothée on 18/06/2016.
 *
 * Page (Fragment) "Les Recettes !" accessible depuis le menu
 * Liste les Cocktails proposés par l'application
 */
public class Cocktails_Page extends Fragment {
	/**
	 * Création de la vue à partir du layout dédié
	 * @param inflater Objet utilisé pour remplir la vue
	 * @param container Vue parente qui contient la vue
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 * @return Vue créée
	 */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		// associe le layout au fragment //
        return inflater.inflate(R.layout.cocktails_page, container, false);
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
		
		// récupère la liste des cocktails de la base //
		final List<Cocktail> listAllCocktails = Utils.dbCocktails.getAllCocktails();
		
        // récupère la vue de la liste des cocktails //
        final GridView gridView = view.findViewById(R.id.gridviewCocktails);
	
		// récupère la liste (unique) des alcools //
		final ArrayList<String> listAlcools = new ArrayList<>();
		// sélecteur global //
		listAlcools.add("Tous");
		for(Cocktail cocktail : listAllCocktails){
			if(!listAlcools.contains(cocktail.getAlcool())){
				listAlcools.add(cocktail.getAlcool());
			}
		}
		
		// créer la liste déroulante avec les options de tri //
		Spinner dropDownList = view.findViewById(R.id.cocktails_spinner);
		// envoi la liste des alcools à afficher dans le spinner //
		ArrayAdapter dropDownListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listAlcools);
		dropDownList.setAdapter(dropDownListAdapter);
	
		// affiche la liste des cocktails en fonction du tri choisi //
		// appelé aussi une fois à la création du fragment //
		dropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			/**
			 * Met à jour la liste des cocktails affichés quand on sélectionne un paramètre de tri
			 * @param parentView Vue du spinner
			 * @param selectedItemView Vue de l'item sélectionné dans le spinner
			 * @param position Position de l'item sélectionné dans le spinner
			 * @param id ID de l'item sélectionné dans le spinner (== position)
			 */
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				
				// liste des cocktails qui vont être affichés //
				ArrayList<Cocktail> cocktailsToDisplay = new ArrayList<>();
				
				// si option de tri "Tous", on garde tous les Cocktails
				if(position == 0){
					cocktailsToDisplay = new ArrayList<>(listAllCocktails);
				}
				// sinon on compare l'alcool de chaque cocktail à l'acool sélectionnée pour le tri //
				else{
					for(Cocktail cocktail : listAllCocktails){
						if(cocktail.getAlcool().equals(listAlcools.get(position))){
							cocktailsToDisplay.add(cocktail);
						}
					}
				}
				
				// envoi la liste des cocktails à afficher dans la GrdiView du fragment //
				Cocktails_GridViewAdapter adapter = new Cocktails_GridViewAdapter(getActivity(), cocktailsToDisplay);
				gridView.setAdapter(adapter);
			}
		
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {}
		});
		
		// affiche automatiquement la dernière position affichée lorsque l'on revient d'une
		// Cocktails_Fiche //
		gridView.smoothScrollToPosition(gridView.getFirstVisiblePosition());
        
		// détecte les cliques et affiche la fiche d'un cocktail //
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/**
			 * Action lors du clique sur un cocktail
			 * @param parent L'adapter qui gère la liste
			 * @param view La vue qui contient l'adapter
			 * @param position La position dans la liste de l'item cliqué
			 * @param id L'ID de l'item cliqué (Cocktails_GridViewAdapter.getItemId(position))
			 */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// passage de l'ID du cocktail qui a été cliqué //
                Bundle bundle = new Bundle();
                bundle.putLong("code", id);
	
				// remplace le fragment actuel par le nouveau fragment //
				Utils.replaceFragments(Cocktails_Fiche.class, getActivity(), bundle, true);
            }
        });
        
		// revient à la liste complète quand on appuie sur retour depuis une fiche de cocktail //
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
    }
}
