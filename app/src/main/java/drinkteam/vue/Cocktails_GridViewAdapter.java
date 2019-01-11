package drinkteam.vue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.drinkteam.R;
import drinkteam.metier.Cocktail;

/**
 * Created by Timothée on 17/05/2017.
 *
 * Gestionnaire de la GridView qui affiche la liste des Cocktails (Cocktails_Page)
 * Il défini le comportement de la GridView et le contenu de chaque item de la liste
 */
public class Cocktails_GridViewAdapter extends BaseAdapter
{
	// Contexte de l'application //
	private Context context;
	
	// objet utilisé pour remplir la vue //
	private LayoutInflater inflater;
	
	// liste des cocktails à afficher //
	private List<Cocktail> listCocktails;
	
	/**
	 * Constructeur
	 * @param context Contexte de l'application
	 * @param listCocktails Liste des cocktails à afficher
	 */
	public Cocktails_GridViewAdapter(Context context, List<Cocktail> listCocktails)
	{
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.listCocktails = listCocktails;
	}
	
	/**
	 * Compte le nombre d'items affichés
	 * @return Nombre de cocktails affichés
	 */
	@Override
	public int getCount()
	{
		return this.listCocktails.size();
	}
	
	/**
	 * Récupère un Cocktail affiché dans la GridView
	 * @param i Position dans la GridView
	 * @return Cocktail
	 */
	@Override
	public Object getItem(int i)
	{
		return this.listCocktails.get(i);
	}
	
	/**
	 * Récupère l'identifiant d'un Cocktail affiché dans la GridView
	 * @param i Position dans la GridView
	 * @return Code du Cocktail
	 */
	@Override
	public long getItemId(int i)
	{
		return this.listCocktails.get(i).getCode();
	}
	
	/**
	 * Définit l'affichage de chaque item de la liste
	 */
	private class ItemHolder
	{
		// nom du Cocktail //
		TextView nom;
		// alcool principale du Cocktail //
		TextView alcool;
		// image du Cocktail //
		ImageView icon;
	}
	
	/**
	 * Méthode utilisée par le gestionnaire pour afficher chaque item de la GridView
	 * @param position Position de l'item dans la GridView
	 * @param view Vue de l'item
	 * @param viewGroup Vue de la GridView
	 * @return Vue de l'item
	 */
	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		View gridView;
		// REUSE VIEW IF NOT NULL CODE //
		if (view == null) {
			// redéfinit le layout utilisé pour l'item //
			gridView = inflater.inflate(R.layout.cocktails_list_item, viewGroup, false);
		}
		else {
			gridView = view;
		}
		
		ItemHolder holder = new ItemHolder();
		
		// nom du Cocktail //
		holder.nom = gridView.findViewById(R.id.nom_cocktail);
		holder.nom.setText(listCocktails.get(position).getNom());
		
		// alcool principale du Cocktail //
		holder.alcool = gridView.findViewById(R.id.alcool_cocktail);
		holder.alcool.setText(listCocktails.get(position).getAlcool());
		
		// image du Cocktail //
		holder.icon = gridView.findViewById(R.id.icon_cocktail);
		String icon = listCocktails.get(position).getImage();
		if(listCocktails.get(position).getImage() == null) {
			icon = "default_icon";
		}

		// rend l'image du Cocktail "arrondie" //
		int iconID = this.context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
		Bitmap ImageBit = BitmapFactory.decodeResource(this.context.getResources(), iconID);
		holder.icon.setImageBitmap(ImageBit);
		RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(context.getResources(), ImageBit);
		RBD.setCornerRadius(200);
		RBD.setAntiAlias(true);
		holder.icon.setImageDrawable(RBD);
		
		return gridView;
	}
}
