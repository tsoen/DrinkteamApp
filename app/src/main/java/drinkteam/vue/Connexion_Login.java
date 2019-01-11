package drinkteam.vue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.drinkteam.R;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Timothée on 24/05/2017.
 *
 * Interface de connexion pour les utilisateurs disposant déjà d'un compte
 */
public class Connexion_Login extends Fragment
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
		return inflater.inflate(R.layout.connexion_login, container, false);
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
		
		//region ////////// LOGIN FACEBOOK //////////
		
		// association du layout du bouton //
		LoginButton btn_facebook = view.findViewById(R.id.btn_login_facebook);
		
		// permissions d'accès pour l'application //
		btn_facebook.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
		
		// action lors de la connexion à facebook //
		btn_facebook.registerCallback(Utils.callbackManager, new FacebookCallback<LoginResult>()
		{
			/**
			 * Si succès de la connexion à facebook
			 * @param result Objet représentant le résultat de la connexion
			 */
			@Override
			public void onSuccess(LoginResult result)
			{
				// objet représentant le jeton d'accès à facebook //
				AccessToken facebookToken = result.getAccessToken();
				
				// définition du listener de connexion à Firebase //
				Utils.authListener = new FirebaseAuth.AuthStateListener() {
					/**
					 * Action après un changment de statut de connexion à firebase :
					 * - quand le listener est enregistré
					 * - quand un utilisateur se connecte
					 * - quand un utilisateur de déconnecte
					 * @param firebaseAuth Point d'entrée à l'outil d'authentification firebase
					 */
					@Override
					public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
					{
						// connexion et accès à l'application //
						Utils.loginUser(getContext());
					}
				};
				
				// ajout du listener au point d'entrée à firebase //
				Utils.mAuth.addAuthStateListener(Utils.authListener);
				
				// récupération du jeton d'accès à facebook //
				AuthCredential credential = FacebookAuthProvider.getCredential(facebookToken.getToken());
				
				// connexion à Firebase à partir du jeton d'accès à facebook, déclenche authListener //
				Utils.mAuth.signInWithCredential(credential);
			}
			
			/**
			 * Annulation de la connexion à facebook
			 */
			@Override
			public void onCancel(){ }
			
			/**
			 * Erreur lors de la connexion à facebook
			 * @param error Object décrivant l'erreur de connexion
			 */
			@Override
			public void onError(FacebookException error)
			{
				Toast.makeText(getApplicationContext(), "Error occurred while logging in. Please try again.", Toast.LENGTH_SHORT)
						.show();
			}
		});
		
		//endregion
		
		//region ////////// LOGIN GOOGLE //////////
		
		// association du layout du bouton de connexion Google //
		SignInButton btn_google = view.findViewById(R.id.btn_login_google);
		
		// listener de click sur le bouton //
		btn_google.setOnClickListener(new View.OnClickListener()
		{
			/**
			 * Action lors du click sur le bouton
			 * @param view Vue du bouton cliquée
			 */
			@Override
			public void onClick(View view)
			{
				// définition de la fenêtre de sélection du compte google //
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(Utils.mGoogleApiClient);
				
				// démarrage de la fenêtre de sélection du compte //
				// la gestion de la connexion se trouve dans ConnexionActivity.onActivityResult //
				getActivity().startActivityForResult(signInIntent, Config.GOOGLE_SIGN_IN);
			}
		});
		
		//endregion
		
		//region ////////// LOGIN EMAIL/PASSWORD //////////
		
		// association du layout du bouton de connexion email-password //
		Button btn_email = view.findViewById(R.id.btn_login_email);
		// listener de click sur le bouton //
		btn_email.setOnClickListener(new View.OnClickListener()
		{
			/**
			 * Action lors du click sur le bouton
			 * @param view Vue du bouton cliquée
			 */
			@Override
			public void onClick(View view)
			{
				// affichage d'une page personnalisée pour entrer les identifiants //
				Utils.replaceFragments(Connexion_LoginEmail.class, getActivity(), null, true);
			}
		});
		
		//endregion
	}
}
