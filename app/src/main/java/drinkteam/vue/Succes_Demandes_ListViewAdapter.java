package drinkteam.vue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.drinkteam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;
import drinkteam.metier.Succes;

/**
 * Created by Timothée on 14/10/2017.
 */

public class Succes_Demandes_ListViewAdapter extends BaseAdapter
{
	//region //////////////////// ATTRIBUTS ////////////////////
	
	// contexte de l'application //
	private Context context;
	
	// liste des Succès à afficher //
	private List<Succes> listSucces = new ArrayList<>();
	
	// liste des Succès à afficher //
	private List<Integer> positions = new ArrayList<>();
	
	// objet utilisé pour remplir la vue //
	private LayoutInflater inflater;
	
	private String userID;
	
	//endregion
	
	//region //////////////////// CONSTRUCTEUR ////////////////////
	
	/**
	 * Constructeur
	 * @param context Contexte de l'application
	 * @param userID ID de l'utilisateur
	 *
	 */
	public Succes_Demandes_ListViewAdapter(Context context, String userID, List<Integer> positions) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.listSucces = Utils.dbSucces.getAllSucces();
		
		this.positions =  positions;
		
		this.userID = userID;
	}
	
	//region //////////////////// ACCESSEURS ////////////////////
	
	/**
	 * Retourne le nombre d'items (Succes) affichés
	 * @return Nombre de Succès affichés
	 */
	@Override
	public int getCount()
	{
		return this.positions.size();
	}
	
	/**
	 * Retourne l'item à la position indiquée
	 * @param i Index de l'item
	 * @return Succès
	 */
	@Override
	public Object getItem(int i)
	{
		return listSucces.get(positions.get(i));
	}
	
	/**
	 * Retourne l'ID du Succès à la position indiquée
	 * @param i Index du Succès dans la ListView
	 * @return Long
	 */
	@Override
	public long getItemId(int i)
	{
		return Integer.parseInt(this.listSucces.get(i).getCodecategorie() + "" + this.listSucces.get(i).getCodesucces());
	}
	
	//endregion ////////////////////////////////////////
	
	/**
	 * Définit l'affichage de chaque item de la liste
	 */
	private class Holder
	{
		// icone du Succès //
		ImageView icon;
		// niveau atteint par l'utilisateur pour ce Succès //
		ImageView niveau;
		// nom du Succès //
		TextView nom;
		// description du Succès //
		TextView description;
		// bouton de confirmation de la demande //
		Button buttonYes;
		// bouton de refus de la demande //
		Button buttonNo;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		View itemView;
		// REUSE VIEW IF NOT NULL CODE //
		if(view == null) {
			// redéfinit le layout utilisé pour l'item //
			itemView = inflater.inflate(R.layout.succes_demandes_list_item, null);
		}
		else{
			itemView =  view;
		}
		
		Holder holder = new Holder();
		holder.nom = itemView.findViewById(R.id.nom_succes);
		holder.icon = itemView.findViewById(R.id.icon_succes);
		holder.niveau = itemView.findViewById(R.id.icon_niveau);
		holder.description = itemView.findViewById(R.id.descrip_succes);
		holder.buttonYes = itemView.findViewById(R.id.succes_demande_accept_button);
		holder.buttonNo = itemView.findViewById(R.id.succes_demande_refuse_button);
		
		// nom du Succès //
		holder.nom.setText(listSucces.get(positions.get(position)).getNom());
		
		// nom du fichier contenant l'image du Succès //
		String image;
		if(listSucces.get(positions.get(position)).getImage() != null) {
			image = listSucces.get(positions.get(position)).getImage();
		}
		else{
			image = "default_icon";
		}
		
		int iconID = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
		// arrondi l'image de base //
		Bitmap ImageBit = BitmapFactory.decodeResource(context.getResources(), iconID);
		holder.icon.setImageBitmap(ImageBit);
		RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(context.getResources(),ImageBit);
		RBD.setCornerRadius(200);
		RBD.setAntiAlias(true);
		holder.icon.setImageDrawable(RBD);
		
		// image du niveau atteint par l'utilisateur pour ce Succès //
		holder.niveau.setImageURI(Uri.parse(Utils.niveauxSuccesAnyUser.get(positions.get(position))));
		
		// description du Succès //
		holder.description.setText(listSucces.get(positions.get(position)).getDescription());
		
		return itemView;
	}
}
