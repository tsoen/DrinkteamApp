package drinkteam.vue;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.drinkteam.R;

import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 16/05/2017.
 *
 * Page (Fragment) de détails d'un jeu
 * Accessible par click d'un jeu dans la liste de Jeux_Page
 */
public class Jeux_Fiche extends Fragment
{
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
		return inflater.inflate(R.layout.jeux_fiche, container, false);
	}
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull final View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		Bundle args = getArguments();
		
		// code du jeu //
		long code = args.getLong("code");
		
		// nom du jeu //
		TextView nom = view.findViewById(R.id.nom_jeu_fiche);
		nom.setText(Utils.dbJeux.getJeu(code).getNom());
		
		// nombre de joueurs //
		TextView joueurs = view.findViewById(R.id.joueurs_jeu_fiche);
		joueurs.setText("Joueurs: " + Utils.dbJeux.getJeu(code).getJoueurs());
		
		//TextView regles = (TextView) view.findViewById(R.id.regles_jeu_fiche);
		//on utilise du texte formaté en html
		//regles.setText("Règles: \n" + Html.fromHtml(Utils.dbJeux.getJeu(code).getRegles()));
		
		WebView regles = view.findViewById(R.id.regles_jeu_fiche);
		WebSettings settings = regles.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		regles.setBackgroundColor(Color.TRANSPARENT);
		regles.loadData(Utils.dbJeux.getJeu(code).getRegles(), "text/html; charset=utf-8", "utf-8");
		
	}
}
