package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drinkteam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import drinkteam.controleur.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Timothée on 04/07/2017.
 *
 * Interface de création de compte pour les utilisateurs utilisant un système Email-Password classique
 */
public class Connexion_RegisterEmail extends Fragment
{
	/**
	 * Création de la vue à partir du layout dédié
	 * @param inflater Objet utilisé pour remplir la vue
	 * @param container Vue parente qui contient la vue
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 * @return Vue créée
	 */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
	{
		// associe le layout au fragment //
		return inflater.inflate(R.layout.connexion_register_email, container, false);
	}
	
	/**
	 * Ajout des éléments près la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull final View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		// champ de texte pour entrer son email //
		final EditText txt_email = view.findViewById(R.id.txt_registerEmail_email);
		
		// champ de texte pour entrer son mot de passe //
		final EditText txt_password = view.findViewById(R.id.txt_registerEmail_password);
		
		// bouton de connexion //
		Button btn_register = view.findViewById(R.id.btn_register_email);
		// listener de click //
		btn_register.setOnClickListener(new View.OnClickListener() {
			/**
			 * Action lors du click sur le bouton
			 * @param view Vue du bouton cliquée
			 */
			@Override
			public void onClick(View view)
			{
				// méthode de firebase pour se créer un compte avec email-password //
				Utils.mAuth.createUserWithEmailAndPassword(txt_email.getText().toString(),
						txt_password.getText().toString())
						.addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
							/**
							 * A la fin de la tentative de création de compte
							 * @param task Opération de connexion
							 */
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								// si création dans firebase réussie //
								if (task.isSuccessful()) {
									// enregistrement dans la base de données et accès à l'application //
									Utils.registerUser(getContext());
								}
								// si erreur //
								else {
									// If sign in fails, display a message to the user.
									Toast.makeText(getApplicationContext(), "Une erreur est survenue. Réessayez plus tard.", Toast.LENGTH_SHORT)
											.show();
								}
							}
						});
			}
		});
	}
}
