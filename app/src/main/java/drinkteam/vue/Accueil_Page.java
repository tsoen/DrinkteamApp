package drinkteam.vue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drinkteam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageException;

import drinkteam.DownloadImageTask;
import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 18/06/2016.
 *
 * Page (Fragment) "Accueil" accessible depuis le menu et affichée à l'ouverture de l'application
 */
public class Accueil_Page extends Fragment {
	
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
        return inflater.inflate(R.layout.accueil_page, container, false);
    }
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		try
		{
			FirebaseUser user = Utils.mAuth.getCurrentUser();
			if (user != null)
			{
				// titre de l'actionBar //
				Bundle args = getArguments();
				((MainActivity)getActivity()).setTitreActionBar(args.getString("titre_actionbar"));
				
				final Bundle bundle = new Bundle();
				
				// récupération des données de l'utilisateurs //
				Utils.database.child(Config.DB_NODE_COMPTES).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
				{
					/**
					 * Détecte un changement de donnée dans la base à l'emplacement défini
					 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
					 * @param dataSnapshot Contient les données présentes à cet emplacement
					 */
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot)
					{
						// si des données existent pour cet utilisateur //
						if (dataSnapshot.exists()) {
							// titre de l'utilisateur //
							TextView titre = view.findViewById(R.id.accueil_profile_titre);
							titre.setText(dataSnapshot.child(Config.DB_USER_TITRE).getValue().toString());
							
							// création des paramètres à transmettre si on souhaite afficher
							// la page de profil détaillée de l'utilisateur //
							bundle.putString("id", dataSnapshot.child(Config.DB_USER_ID).getValue().toString());
							bundle.putString("name", dataSnapshot.child(Config.DB_USER_NAME).getValue().toString());
							bundle.putString("provider", dataSnapshot.child(Config.DB_USER_PROVIDER).getValue().toString());
							bundle.putString("providerid", dataSnapshot.child(Config.DB_USER_PROVIDERID).getValue().toString());
							bundle.putString("titre", dataSnapshot.child(Config.DB_USER_TITRE).getValue().toString());
						}
					}
					
					/**
					 * Arrêt de la récupération des données
					 * @param databaseError Erreur éventuelle
					 */
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError)
					{
						
					}
				});
				
				// vue contenant la photo de couverture - RelativeLayout dont on va set le background //
				final RelativeLayout background = view.findViewById(R.id.accueil_profile_layout);
				// listener de clicks sur la photo de couverture //
				background.setOnClickListener(new View.OnClickListener()
				{
					/**
					 * Action lors du click sur l'image
					 * @param view Vue de l'image cliquée
					 */
					@Override
					public void onClick(View view)
					{
						// affiche du profil détaillé de l'utilisateur //
						Utils.replaceFragments(Amis_Profil.class, getActivity(), bundle, true);
					}
				});
				
				// nom de l'utilisateur //
				TextView name = view.findViewById(R.id.accueil_profile_name);
				name.setText(user.getDisplayName());
				
				//region ////////// IMAGE DE PROFIL //////////
				
				// photo de profil de l'utilisateur //
				final ImageView profile = view.findViewById(R.id.accueil_profile_pic);
				// télécharge et affiche la photo dans le vue indiquée //
				// récupération de l'url de l'image dans le storage de firebase //
				Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid()).child(Config.STR_USER_PROFILE)
						.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
					/**
					 * Si récupération réussie
					 * @param uri Url de téléchargement de l'image
					 */
					@Override
					public void onSuccess(Uri uri)
					{
						// téléchargement et affichage de l'image de profil //
						new DownloadImageTask(profile).execute(Config.STR_USER_PROFILE, uri.toString());
					}
				}).addOnFailureListener(new OnFailureListener() {
					/**
					 * si une erreur est survenue lors de la récupération de l'url de l'image //
					 * @param exception Description de l'erreur
					 */
					@Override
					public void onFailure(@NonNull Exception exception)
					{
						// si l'erreur provient du storage de firebase //
						if (exception.getClass() == StorageException.class) {
							// si le fichier n'existe pas dans le storage //
							if (((StorageException) exception)
									.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND)
							{
								// récupération de l'image de profil par défaut //
								Utils.storage.child(Config.STR_NODE_COMPTES).child(Config.STR_DEFAULT_PROFILE).getDownloadUrl()
										.addOnSuccessListener(new OnSuccessListener<Uri>()
										{
											/**
											 * Si récupération réussie
											 * @param uri Url de téléchargement de l'image
											 */
											@Override
											public void onSuccess(Uri uri)
											{
												// téléchargement et affichage de l'image de profil par défaut //
												new DownloadImageTask(profile)
														.execute(Config.STR_USER_PROFILE, uri.toString());
											}
										}).addOnFailureListener(new OnFailureListener()
								{
									/**
									 * si une erreur est survenue lors de la récupération de l'url de l'image //
									 * @param exception Description de l'erreur
									 */
									@Override
									public void onFailure(@NonNull Exception exception)
									{
										Log.d("myapp", "Amis_Profil profile onFailure: " + exception.toString());
									}
								});
							}
						}
						else {
							Log.d("myapp", "Amis_Profil profile onFailure: " + exception.toString());
						}
					}
				});
				
				//endregion
				
				//region ////////// IMAGE DE COUVERTURE //////////
				
				// récupération de la photo de couverture dans le storage firebase //
				Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid()).child(Config.STR_USER_COVER)
						.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
					/**
					 * Réussite de récupération des données
					 * @param uri Uri de téléchargement du fichier
					 */
					@Override
					public void onSuccess(Uri uri)
					{
						new DownloadImageTask(background).execute(Config.STR_USER_COVER, uri.toString());
					}
				}).addOnFailureListener(new OnFailureListener() {
					/**
					 * Si les données n'ont pas pû être récupérées
					 * @param exception Décrit l'erreur survenue
					 */
					@Override
					public void onFailure(@NonNull Exception exception) {
						// si l'erreur provient du storage firebase //
						if (exception.getClass() == StorageException.class)
						{
							// si l'erreur est dûe au fait qu'il n'existe pas de données
							// (de fichier) à l'emplacement indiqué //
							if (((StorageException) exception)
									.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND)
							{
								// récupération de la photo de couverture par défaut //
								Utils.storage.child(Config.STR_NODE_COMPTES).child(Config.STR_DEFAULT_COVER).getDownloadUrl()
										.addOnSuccessListener(new OnSuccessListener<Uri>()
										{
											/**
											 * Réussite de récupération des données
											 * @param uri Uri de téléchargement du fichier
											 */
											@Override
											public void onSuccess(Uri uri)
											{
												new DownloadImageTask(background)
														.execute(Config.STR_USER_COVER, uri.toString());
											}
										}).addOnFailureListener(new OnFailureListener()
								{
									/**
									 * Si les données n'ont pas pû être récupérées
									 * A ce stage on a atteint une erreur critique
									 * @param exception Décrit l'erreur survenue
									 */
									@Override
									public void onFailure(@NonNull Exception exception)
									{
										// TODO display error message (restart app ?)
									}
								});
							}
						}
						else {
							// TODO display error message
						}
					}
				});
				
				//endregion
				
				Button button_all_succes = view.findViewById(R.id.accueil_succes_button);
				button_all_succes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
						Utils.replaceFragments(Succes_Page.class, getActivity(), bundle, true);
					}
				});
				
				HorizontalGridView horizontalGridView = view.findViewById(R.id.accueil_succes_list);
				bundle.putString("id", user.getUid());
				Utils.setUserNiveauSucces(bundle, horizontalGridView);
				
				TextView cocktailName = view.findViewById(R.id.weekly_cocktail_name);
				cocktailName.setText(Utils.dbCocktails.getCocktail(Utils.weeklyCocktailID).getNom());
				
				ImageView cocktailLogo = view.findViewById(R.id.accueil_cocktail_logo);
				// TODO sélection de l'image du cocktail dans les ressources de l'appli //
				int iconID = getContext().getResources().getIdentifier("default_icon",
						"drawable", getContext().getPackageName());
				Bitmap ImageBit = BitmapFactory.decodeResource(getContext().getResources(), iconID);
				cocktailLogo.setImageBitmap(Bitmap.createScaledBitmap(ImageBit, 120, 120, false));
				
				Button cocktailFicheButton = view.findViewById(R.id.cocktail_fiche_button);
				cocktailFicheButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
						Bundle bundle = new Bundle();
						bundle.putLong("code", Utils.dbCocktails.getCocktail(Utils.weeklyCocktailID).getCode());
						Utils.replaceFragments(Cocktails_Fiche.class, getActivity(), bundle, true);
					}
				});
			}
			else{
				Log.d("myapp", "Accueil_Page: no user logged in");
			}
		}
		catch (Exception ex) {
			Log.d("myapp", "changeUI " + ex.toString());
		}
	}
}
