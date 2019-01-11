package drinkteam.vue;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drinkteam.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.Map;

import drinkteam.DownloadImageTask;
import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;

/**
 * Created by Timothée on 20/07/2017.
 *
 * Gestionnaire de la ListView qui affiche les résultats d'une recherche d'utilisateurs (Dialog de
 * la barre de recherche)
 * Il défini le comportement de la ListView et le contenu de chaque item de la liste
 */
public class Amis_SearchListViewAdapter extends BaseAdapter
{
	// objet utilisé pour remplir la vue //
	private LayoutInflater inflater;
	
	// liste des résultats de la recherche d'utilisateurs <SnapShot de firebase, score de recherche>
	private Map.Entry<DataSnapshot, Double>[] userArray;
	
	/**
	 * Constructeur
	 * @param context Contexte de l'application
	 * @param users Liste des résultats de la recherche d'utilisateurs
	 */
	public Amis_SearchListViewAdapter(Context context, Map.Entry<DataSnapshot, Double>[] users){
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.userArray = users;
	}
	
	/**
	 * Compte le nombre d'objets affichés
	 * @return Nombre de résultats de la recherche
	 */
	@Override
	public int getCount()
	{
		return this.userArray.length;
	}
	
	/**
	 * Récupère un objet affiché dans la ListView
	 * @param i Position dans la ListView
	 * @return DataSnapShot firebase de l'utilisateur
	 */
	@Override
	public Object getItem(int i)
	{
		return this.userArray[i];
	}
	
	/**
	 * Récupère l'identifiant d'un objet affiché dans la ListView
	 * @param i Position dans la ListView
	 * @return Position dans la ListView
	 */
	@Override
	public long getItemId(int i)
	{
		return i;
	}
	
	/**
	 * Définit l'affichage de chaque item de la liste
	 */
	private class ItemHolder
	{
		// image du profil //
		ImageView profile;
		// nom de l'utilisateur //
		TextView nom;
		// titre de l'utilisateur //
		TextView titre;
	}
	
	/**
	 * Méthode utilisée par le gestionnaire pour afficher chaque item de la ListView
	 * @param position Position de l'item dans la ListView
	 * @param view Vue de l'item
	 * @param viewGroup Vue de la ListView
	 * @return Vue de l'item
	 */
	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		View itemView;
		// REUSE VIEW IF NOT NULL CODE //
		if(view == null) {
			// redéfinit le layout utilisé pour l'item //
			itemView = this.inflater.inflate(R.layout.amis_search_list_item, viewGroup, false);
		}
		else{
			itemView =  view;
		}
		
		final ItemHolder holder = new ItemHolder();
		holder.profile = itemView.findViewById(R.id.icon_profile);
		holder.nom = itemView.findViewById(R.id.nom_profile);
		holder.titre = itemView.findViewById(R.id.titre_profile);
		
		String userID = this.userArray[position].getKey().child(Config.DB_USER_ID).getValue().toString();
		// récupération de l'url de l'image dans le storage de firebase //
		Utils.storage.child(Config.STR_NODE_COMPTES).child(userID).child(Config.STR_USER_PROFILE)
				.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
		{
			/**
			 * Si récupération réussie
			 * @param uri Url de téléchargement de l'image
			 */
			@Override
			public void onSuccess(Uri uri)
			{
				// téléchargement et affichage de l'image de profil //
				new DownloadImageTask(holder.profile).execute(Config.STR_USER_PROFILE, uri.toString());
			}
		});
		
		holder.nom.setText(userArray[position].getKey().child(Config.DB_USER_NAME).getValue().toString());
		holder.titre.setText(userArray[position].getKey().child(Config.DB_USER_TITRE).getValue().toString());
		
		return itemView;
	}
}
