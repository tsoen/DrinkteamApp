package drinkteam.vue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.drinkteam.R;

import drinkteam.controleur.Utils;
import drinkteam.metier.Succes;

/**
 * Created by Timothée on 17/06/2017.
 *
 * Gestionnaire de la GridView qui affiche la liste des Succès de la page de profil d'un utilisateur
 * (Amis_Profil) ou sur la page d'accueil de l'application (Accueil_Page)
 * Il défini le comportement de la GridView et le contenu de chaque item de la liste
 */
public class Succes_HorizontalGridViewAdapter extends RecyclerView.Adapter<Succes_HorizontalGridViewAdapter.ItemHolder>
{
	// contexte de l'application //
	private Context context;
	
	// liste de tous les Succès de l'application //
	private List<Succes> listSucces;
	
	private String userID;
	
	/**
	 * Constructeur
	 * @param context Contexte de l'application
	 */
	public Succes_HorizontalGridViewAdapter(Context context, String userID){
		this.context = context;
		this.listSucces = Utils.dbSucces.getAllSucces();
		this.userID = userID;
	}
	
	/**
	 * Compte le nombre d'items affichés
	 * @return Nombre de Succès affichés
	 */
	@Override
	public int getItemCount() {
		return this.listSucces.size();
	}
	
	/**
	 * Renvoi l'ID du Succès sélectionné
	 * @param i Index du Succès dans la liste
	 * @return ID
	 */
	@Override
	public long getItemId(int i) {
		return Integer.parseInt(this.listSucces.get(i).getCodecategorie() + "" + this.listSucces.get(i).getCodesucces());
	}
	
	/**
	 * Définit l'affichage de chaque item de la liste
	 */
	public static class ItemHolder extends RecyclerView.ViewHolder {
		// icon du succès //
		ImageView icon;
		// niveau atteint par l'utilisateur pour ce succès //
		ImageView niveau;
		// nom du succès //
		TextView nom;
		
		/**
		 * Constructeur
		 * @param view Vue de l'item
		 */
		private ItemHolder(View view) {
			super(view);
			nom = view.findViewById(R.id.nom_succes);
			icon = view.findViewById(R.id.icon_succes);
			niveau = view.findViewById(R.id.icon_niveau);
		}
	}
	
	/**
	 * Méthode utilisée par le gestionnaire pour créer chaque item de la GridView
	 * @param parent Vue de la GridView
	 * @param viewType -
	 * @return Vue de l'item
	 */
	@Override
	public @NonNull ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(this.context).inflate(R.layout.succes_recents_item, parent, false);
		return new ItemHolder(view);
	}
	
	/**
	 * Méthode utilisée par le gestionnaire pour afficher chaque item de la GridView
	 * @param holder Vue de l'item
	 * @param position Position de l'item dans la GridView
	 */
	@Override
	public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
		try
		{
			// nom du succès //
			holder.nom.setText(listSucces.get(position).getNom());
			
			// nom du fichier contenant l'image du Succès //
			String image;
			if (listSucces.get(position).getImage() != null) {
				image = listSucces.get(position).getImage();
			}
			else {
				image = "default_icon";
			}
			int iconID = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
			// arrondi l'image de base //
			Bitmap ImageBit = BitmapFactory.decodeResource(context.getResources(), iconID);
			holder.icon.setImageBitmap(ImageBit);
			RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory
					.create(context.getResources(), ImageBit);
			RBD.setCornerRadius(200);
			RBD.setAntiAlias(true);
			holder.icon.setImageDrawable(RBD);
			
			// image du niveau atteint par l'utilisateur pour ce succès //
			holder.niveau.setImageURI(Uri.parse(Utils.niveauxSuccesAnyUser.get(position)));
			
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					MainActivity activity = (MainActivity)Succes_HorizontalGridViewAdapter.this.context;
					
					Bundle bundle = new Bundle();
					if(userID.equals(Utils.mAuth.getCurrentUser().getUid())){
						bundle.putString("titre_actionbar", activity.getResources().getString(R.string.Mes_succes));
					}
					else{
						ActionBar actionBar = ((MainActivity)context).getSupportActionBar();
						TextView currentUserName = actionBar.getCustomView().findViewById(R.id.actionbar_textview);
						bundle.putString("titre_actionbar", currentUserName.getText().toString());
					}
					bundle.putString("id", userID);
					int succesID = Integer.parseInt(listSucces.get(position).getCodecategorie() + ""
							+ listSucces.get(position).getCodesucces());
					bundle.putInt("succesID", succesID);
					Utils.replaceFragments(Succes_Page.class, activity, bundle, true);
				}
			});
		}
		catch(Exception ex)
		{
			Log.d("myapp", ex.toString());
		}
	}
}
