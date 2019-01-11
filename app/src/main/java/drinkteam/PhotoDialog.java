package drinkteam;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.drinkteam.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 24/06/2016.
 *
 * Fragment de dialogue pour sélectionner une photo ou ouvrir l'appareil photo
 */
public class PhotoDialog extends DialogFragment
{
	/**
	 * Instancie un nouveau Dialog
	 * @param mode Fonction du dialog
	 * @return Dialog
	 */
    public static PhotoDialog newInstance(String mode) {
        PhotoDialog dialog = new PhotoDialog();
        Bundle args = new Bundle();
        args.putString("mode", mode);
        dialog.setArguments(args);
        return dialog;
    }
	
	/**
	 * Affichage du dialog
	 * @param inflater .
	 * @param container Conteneur parent
	 * @param savedInstanceState Etat sauvegardé de l'application
	 * @return Vue du dialog
	 */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
        // récupère le layout qui va contenir le fragmentDialog //
        View view = inflater.inflate(R.layout.dialog_photo, container, false);
		
		final String mode = getArguments().getString("mode");

		//region ////////// PRENDRE UNE PHOTO //////////
		
        // création du bouton pour prendre une photo //
        Button btn_picture = view.findViewById(R.id.takePic);
        btn_picture.setOnClickListener(new View.OnClickListener() {
			
            // évènement déclenché au clique sur le bouton //
            public void onClick(View view) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
					if(mode.equals("profile")) {
						getActivity().startActivityForResult(takePictureIntent, Config.CAPTURE_IMAGE_PROFILE);
					}
					else if(mode.equals("cover")) {
						getActivity().startActivityForResult(takePictureIntent, Config.CAPTURE_IMAGE_COVER);
					}
					
					getDialog().cancel();
				}
			}
        });
		
		//endregion

		//region ////////// CHOISIR UNE PHOTO //////////
		
        Button btn_album = view.findViewById(R.id.fromAlbum);
        btn_album.setOnClickListener(new View.OnClickListener() {
            //évènement déclenché au clique sur le bouton
            public void onClick(View view) {
				Intent intent = new Intent();
				// Show only images, no videos or anything else
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_PICK);
				// Always show the chooser (if there are multiple options available)
				if(mode.equals("profile")) {
					getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), Config.PICK_IMAGE_PROFILE);
				}
				else if(mode.equals("cover")) {
					getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), Config.PICK_IMAGE_COVER);
				}
				
				getDialog().cancel();
            }
        });
		
		//endregion
	
		final FirebaseUser user = Utils.mAuth.getCurrentUser();
		// récupération du provider autre que Firebase //
		String provider = user.getProviders().get(0);
		Button btn_update = view.findViewById(R.id.updatePicture);
	
		//region ////////// MISE A JOUR VIA FACEBOOK //////////
		
		if(provider.equals("facebook.com")) {
			btn_update.setText("Mettre à jour via facebook");
			btn_update.setOnClickListener(new View.OnClickListener()
			{
				//évènement déclenché au clique sur le bouton
				public void onClick(View view)
				{
					if (mode.equals("profile"))
					{
						try
						{
							// enregistrement de la photo de profile sous forme de flux de données //
							String url = user.getProviderData().get(1).getPhotoUrl().toString();
							StorageReference node = Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid())
									.child(Config.STR_USER_PROFILE);
							
							Utils.storeFileByStream(url, node);
						}
						catch (Exception e)
						{
							Log.d("myapp", "updateUser FacebookProfile: " + e.toString());
						}
					}
					else if(mode.equals("cover")){
						// requête pour obtenir la photo de couverture facebook //
						GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
							new GraphRequest.GraphJSONObjectCallback() {
								/**
								 * Une fois la récupération terminée
								 * @param object Données récupérées par la requête encodées en JSON
								 * @param response Contient l'erreur éventuelle
								 */
								@Override
								public void onCompleted(JSONObject object, GraphResponse response) {
									try
									{
										final String url = object.getJSONObject("cover").getString("source");
										
										StorageReference node = Utils.storage.child(Config.STR_NODE_COMPTES)
												.child(user.getUid()).child(Config.STR_USER_COVER);
											
										Utils.storeFileByStream(url, node);
									}
									catch(Exception ex){
										Log.d("myapp", "updateUser couverture request: " + ex.toString());
									}
								}
							});
						Bundle parameters = new Bundle();
						// indique qu'on souhaite récupérer la photo de couverture //
						parameters.putString("fields", "cover");
						request.setParameters(parameters);
						request.executeAsync();
					}
					
					getDialog().cancel();
				}
			});
		}
		
		//endregion
		
		//region ////////// MISE A JOUR VIA GOOGLE //////////
		
		else if(provider.equals("google.com")) {
			if(mode.equals("profile")) {
				btn_update.setText("Mettre à jour via google");
				btn_update.setOnClickListener(new View.OnClickListener()
				{
					//évènement déclenché au clique sur le bouton
					public void onClick(View v)
					{
						try
						{
							// enregistrement de la photo de profile sous forme de flux de données //
							String url = user.getProviderData().get(1).getPhotoUrl().toString();
							
							StorageReference node = Utils.storage.child(Config.STR_NODE_COMPTES)
									.child(user.getUid()).child(Config.STR_USER_PROFILE);
							
							Utils.storeFileByStream(url, node);
						}
						catch (Exception e) {
							Log.d("myapp", "updateUser FacebookProfile: " + e.toString());
						}
						
						getDialog().cancel();
					}
				});
			}
			else{
				ViewGroup layout = (ViewGroup) btn_update.getParent();
				layout.removeView(btn_update);
			}
		}
		
		//endregion

        // création du Dialog à partie de l'instanciation et de la création de la Vue (boutons) //
        getDialog().setTitle("Choisir une action");

        return view;
    }
}
