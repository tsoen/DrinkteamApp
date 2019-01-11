package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.drinkteam.R;

import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 25/05/2017.
 *
 * Page affichée si l'utilisateur n'est pas déjà connecté
 * Propose la connexion à un compte existant ou la création d'un nouveau compte
 */
public class Connexion_NotConnected extends Fragment
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
		return inflater.inflate(R.layout.connexion_not_connected, container, false);
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
		
		// bouton de création de compte //
		Button newAccount = view.findViewById(R.id.btn_new_account);
		// listener de click sur le bouton //
		newAccount.setOnClickListener(new View.OnClickListener()
		{
			/**
			 * Action lors du click sur le bouton
			 * @param view Vue du bouton cliquée
			 */
			@Override
			public void onClick(View view)
			{
				// affiche la page de création de compte //
				Utils.replaceFragments(Connexion_Register.class, getActivity(), null, true);
			}
		});
		
		// bouton de connexion à un compte existant //
		Button connexion = view.findViewById(R.id.btn_connection);
		// listener de click sur le bouton //
		connexion.setOnClickListener(new View.OnClickListener()
		{
			/**
			 * Action lors du click sur le bouton
			 * @param view Vue du bouton cliquée
			 */
			@Override
			public void onClick(View view)
			{
				// affiche la page de connexion à un compte existant //
				Utils.replaceFragments(Connexion_Login.class, getActivity(), null, true);
			}
		});
	}
}
