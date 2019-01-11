package drinkteam.vue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drinkteam.R;

import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 17/05/2017.
 *
 * Page (Fragment) de détails d'un Cocktail
 */
public class Cocktails_Fiche extends Fragment
{
	/**
	 * Création de la vue à partir du layout dédié
	 * @param inflater Objet utilisé pour remplir la vue
	 * @param container Vue parente qui contient la vue
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 * @return Vue créée
	 */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// associe le layout au fragment //
		return inflater.inflate(R.layout.cocktails_fiche, container, false);
	}
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		// récupération des informations sur l'utilisateur transmises à la création du fragment //
		Bundle args = getArguments();
		
		// code du cocktail //
		long code = args.getLong("code");
		
		// nom du cocktail //
		TextView nom = view.findViewById(R.id.nom_cocktail_fiche);
		nom.setText(Utils.dbCocktails.getCocktail(code).getNom());
		
		// vue de l'image de l'icone du cocktail //
		ImageView icon = view.findViewById(R.id.icon_cocktail_fiche);
		// TODO sélection de l'image du cocktail dans les ressources de l'appli //
		int iconID = getContext().getResources().getIdentifier("default_icon",
				"drawable", getContext().getPackageName());
		Bitmap ImageBit = BitmapFactory.decodeResource(getContext().getResources(), iconID);
		icon.setImageBitmap(Bitmap.createScaledBitmap(ImageBit, 120, 120, false));
		
		// recette du cocktail //
		TextView recette = view.findViewById(R.id.recette_cocktail_fiche);
		recette.setText("Recette: \n" + Utils.dbCocktails.getCocktail(code).getRecette());
		
		// anecdote du cocktail //
		TextView anecdote = view.findViewById(R.id.anecdote_cocktail_fiche);
		anecdote.setText(Utils.dbCocktails.getCocktail(code).getAnecdote());
	}
}
