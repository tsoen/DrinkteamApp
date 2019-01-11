package drinkteam.vue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.drinkteam.R;
import drinkteam.metier.Jeu;

/**
 * Created by Timothée on 14/05/2017.
 *
 * Gestionnaire de la GridView qui affiche la liste des Jeux (Jeux_Page)
 * Il défini le comportement de la GridView et le contenu de chaque item de la liste
 */
public class Jeux_GridViewAdapter extends BaseAdapter
{
	// objet utilisé pour remplir la vue //
	private LayoutInflater inflater;
	
	// liste des jeux à afficher //
	private List<Jeu> listJeux;
	
	/**
	 * Constructeur
	 * @param context Contexte de l'application
	 * @param listJeux Liste des Jeux à afficher
	 */
	public Jeux_GridViewAdapter(Context context, List<Jeu> listJeux){
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.listJeux = listJeux;
	}
	
	/**
	 * Compte le nombre d'items affichés
	 * @return Nombre de Jeux affichés
	 */
	@Override
	public int getCount()
	{
		return this.listJeux.size();
	}
	
	/**
	 * Récupère un Jeu affiché dans la GridView
	 * @param i Position dans la GridView
	 * @return Jeu
	 */
	@Override
	public Object getItem(int i)
	{
		return this.listJeux.get(i);
	}
	
	/**
	 * Renvoi l'ID du jeu sélectionné
	 * @param i Index du jeu dans la liste
	 * @return Code
	 */
	@Override
	public long getItemId(int i)
	{
		return this.listJeux.get(i).getCode();
	}
	
	/**
	 * Définit l'affichage de chaque item de la liste
	 */
	private class ItemHolder
	{
		// nom du Jeu //
		TextView nom;
		// nombre de joueurs pour ce Jeu //
		TextView joueurs;
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
		// REUSE VIEW IF NOT NULL CODE
		if(view == null) {
			// redéfinit le layout utilisé pour l'item //
			gridView = this.inflater.inflate(R.layout.jeux_list_item, null);
		}
		else{
			gridView =  view;
		}
		
		ItemHolder holder = new ItemHolder();
		
		// nom du Jeu //
		holder.nom = gridView.findViewById(R.id.nom_jeu);
		holder.nom.setText(listJeux.get(position).getNom());
		
		// nombre de joueurs pour ce Jeu //
		holder.joueurs = gridView.findViewById(R.id.joueurs_jeu);
		holder.joueurs.setText(Integer.toString(listJeux.get(position).getJoueurs()) + " joueurs");
		
		return gridView;
	}
}
