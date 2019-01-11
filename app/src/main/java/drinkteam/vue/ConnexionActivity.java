package drinkteam.vue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.drinkteam.R;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 24/05/2017.
 *
 * Activité au lancement de l'application dans tous les cas
 * Si un utilisateur est déjà connecté, redirige automatiquement vers MainActivity
 */
public class ConnexionActivity extends AppCompatActivity
{
	/**
	 * Création de l'activité
	 * @param savedInstanceState Etat sauvegardé de l'application si elle a été mise de côté
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// si un utilisateur est déjà connecté, redirection vers la page principale //
		if(Utils.mAuth.getCurrentUser() != null){
			// affichage de la page principale MainActivity //
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			// destruction de cette activité pour ne pas la garder active en background //
			this.finish();
		}
		// sinon afichage des boutons de connexion / création de compte //
		else {
			// associe le layout à l'activité //
			this.setContentView(R.layout.connexion_activity);
			
			// affiche la page avec les boutons de connexion / création de compte //
			Utils.replaceFragments(Connexion_NotConnected.class, this, null, false);
			
			// cache le titre de l'app //
			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.hide();
			}
			
			// permissions d'accès de l'application aux infos du compte google //
			GoogleSignInOptions googleReadPermissions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken(Config.SERVER_CLIENT_ID).requestEmail().build();
			
			// définition du point d'entrée de connexion à google //
			Utils.mGoogleApiClient = new GoogleApiClient.Builder(this)
					.enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener()
					{
						/**
						 * Action si erreur lors de la connexion à google
						 * @param connectionResult Objet représentant le résultat de la connexion
						 */
						@Override
						public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
						{
							
						}
					})
					.addApi(Auth.GOOGLE_SIGN_IN_API, googleReadPermissions)
					.build();
			
			
			// définition du gestionnaire d'appels au bouton de connexion facebook //
			Utils.callbackManager = CallbackManager.Factory.create();
		}
	}
	
	/**
	 * Appelée lorsqu'une activité se termine
	 * Cela peut être cette activité ou les activités de connexion générées par facebook et google
	 * @param requestCode Permet d'identifier quelle activité a généré le résultat - startActivityForResult(requestCode)
	 * @param resultCode Code retourné par l'activité fermée
	 * @param data Données supplémentaires
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// si fenêtre de connexion google //
		if (requestCode == Config.GOOGLE_SIGN_IN || requestCode == Config.GOOGLE_CREATE) {
			
			// résultat de la connexion à google //
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			
			// connexion à google réussie //
			if (result.isSuccess()) {
				
				// connexion à firebase avec le compte google connecté //
				firebaseAuthWithGoogle(result.getSignInAccount(), requestCode);
			}
			// echec connexion à google //
			else {

			}
		}
		// si fenêtre de connexion facebook //
		else{
			// voir la partie callBack de LOGIN FACEBOOK dans Connexion_Login ou Connexion_Register //
			Utils.callbackManager.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	/**
	 * Connexion à firebase avec un compte google
	 * @param account Compte google connecté
	 * @param code Création de compte ou simple connexion
	 */
	private void firebaseAuthWithGoogle(GoogleSignInAccount account, final int code) {
		
		// ConnexionActivity //
		final Context context = this;
		
		// récupération du jeton d'accès à google //
		AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		
		// connexion à Firebase à partir du jeton d'accès à google//
		Utils.mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					/**
					 * A la fin de la tentative de connexion
					 * @param task Opération de connexion
					 */
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						// si connexion réussie //
						if (task.isSuccessful()) {
							// enregistre l'utilisateur dans la base de données ou le connecte à
							// l'application en fonction du code //
							if(code == Config.GOOGLE_CREATE){
								Utils.registerUser(context);
							}
							else if(code == Config.GOOGLE_SIGN_IN){
								Utils.loginUser(context);
							}
						}
						// si erreur //
						else {
							Toast.makeText(getApplicationContext(), "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
}
