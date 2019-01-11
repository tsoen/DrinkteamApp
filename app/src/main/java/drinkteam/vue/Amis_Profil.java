package drinkteam.vue;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.drinkteam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageException;

import drinkteam.DownloadImageTask;
import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;


/**
 * Created by Timothée on 05/07/2017.
 *
 * Page (Fragment) de détails du profil d'un utilisateur
 * Accessible par la recherche d'un utilisateur ou clique sur la bannière de la page d'accueil
 * pour l'utilisateur connecté
 */
public class Amis_Profil extends Fragment
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
		return inflater.inflate(R.layout.amis_profil, container, false);
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
		
		try
		{
			// récupération des informations sur l'utilisateur transmises à la création du fragment //
			final Bundle bundle = getArguments();
			
			// titre de l'actionBar //
			((MainActivity)getActivity()).setTitreActionBar(bundle.getString("name"));
			
			// nom de l'utilisateur //
			TextView name = view.findViewById(R.id.profile_name);
			name.setText(bundle.getString("name"));
			
			// titre de l'utilisateur //
			TextView titre = view.findViewById(R.id.profile_titre);
			titre.setText(bundle.getString("titre"));
			
			//region ////////// IMAGE DE PROFIL //////////
			
			// vue de l'image du profil de l'utilisateur //
			final ImageView profile = view.findViewById(R.id.profile_pic);
			// récupération de l'url de l'image dans le storage de firebase //
			Utils.storage.child(Config.STR_NODE_COMPTES).child(bundle.getString("id")).child(Config.STR_USER_PROFILE)
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
			
			// vue de l'image du couverture de l'utilisateur //
			final View background = view.findViewById(R.id.profile_header);
			// récupération de l'url de l'image dans le storage de firebase //
			Utils.storage.child(Config.STR_NODE_COMPTES).child(bundle.getString("id")).child(Config.STR_USER_COVER)
					.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
				/**
				 * Si récupération réussie
				 * @param uri Url de téléchargement de l'image
				 */
				@Override
				public void onSuccess(Uri uri)
				{
					// téléchargement et affichage de l'image de couverture //
					new DownloadImageTask(background).execute(Config.STR_USER_COVER, uri.toString());
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
							Utils.storage.child(Config.STR_NODE_COMPTES).child(Config.STR_DEFAULT_COVER).getDownloadUrl()
									.addOnSuccessListener(new OnSuccessListener<Uri>()
									{
										/**
										 * Si récupération réussie
										 * @param uri Url de téléchargement de l'image
										 */
										@Override
										public void onSuccess(Uri uri)
										{
											// téléchargement et affichage de l'image de couverture par défaut //
											new DownloadImageTask(background)
													.execute(Config.STR_USER_COVER, uri.toString());
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
									Log.d("myapp", "Amis_Profil couverture onFailure: " + exception.toString());
								}
							});
						}
					}
					else {
						Log.d("myapp", "Amis_Profil couverture onFailure: " + exception.toString());
					}
				}
			});
			
			//endregion
			
			Button button_all_succes = view.findViewById(R.id.profile_succes_button);
			button_all_succes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Utils.replaceFragments(Succes_Page.class, getActivity(), bundle, true);
				}
			});
			
			// vue qui contient la liste des derniers succès //
			HorizontalGridView horizontalGridViewSucces = view.findViewById(R.id.profile_succes_list);
			// calcul des niveaux et affichage des succès //
			Utils.setUserNiveauSucces(bundle, horizontalGridViewSucces);
			
			ListView demandesListView = view.findViewById(R.id.profile_demandes_list);
			Utils.setUserNiveauSucces(bundle, demandesListView);
		}
		catch (Exception e)
		{
			Log.d("myapp", "Amis_Profil onViewCreated: " + e.toString());
		}
	}
}
