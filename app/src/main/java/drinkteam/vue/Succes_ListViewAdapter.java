package drinkteam.vue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.drinkteam.R;

import drinkteam.controleur.Config;
import drinkteam.controleur.Utils;
import drinkteam.metier.Succes;

import static drinkteam.controleur.Utils.mAuth;

/**
 * Created by Timothée on 28/06/2016.
 *
 * Gestionnaire de la ListView qui affiche tous les Succès possibles (Succès_Page)
 * Il défini le comportement de la ListView et le contenu de chaque item de la liste
 * Cette liste est "Expandable" : sélectionner un item créer un sous-item pour l'item choisi
 */
public class Succes_ListViewAdapter extends BaseExpandableListAdapter{
	
	//region //////////////////// ATTRIBUTS ////////////////////
	
	// contexte de l'application //
    private Context context;
	
	// liste des Succès à afficher //
	private List<Succes> listSucces = new ArrayList<>();
	
	// objet utilisé pour remplir la vue //
	private LayoutInflater inflater;
	
	private String userID;
	
	//endregion

	//region //////////////////// CONSTRUCTEUR ////////////////////
	
	/**
	 * Constructeur
	 * @param context Contexte de l'application
	 */
    public Succes_ListViewAdapter(Context context, String userID) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// récupère la liste des succès de la base //
        this.listSucces =  Utils.dbSucces.getAllSucces();
		
		this.userID = userID;
    }
    
    //endregion ////////////////////////////////////////

	//region //////////////////// ACCESSEURS ////////////////////
	
	/**
	 * Retourne le nombre d'items (Succes) affichés
	 * @return Nombre de Succès affichés
	 */
    @Override
    public int getGroupCount() {
        return this.listSucces.size();
    }
	
	/**
	 * Retourne le nombre d'enfant de l'item à la position indiquée
	 * Chaque item ne possède qu'un seul "sous-item"
	 * @param i Index de l'item
	 * @return Entier (1)
	 */
	@Override
    public int getChildrenCount(int i) {
        return 1;
    }
	
	/**
	 * Retourne l'item à la position indiquée
	 * @param i Index de l'item
	 * @return Succès
	 */
	@Override
    public Object getGroup(int i) {
        return listSucces.get(i);
    }
	
	/**
	 * Retourne un sous-item de l'item à la position indiquée
	 * @param i Position de l'item dans la ListView
	 * @param i1 Position du sous-item dans la liste des sous-items de l'item
	 * @return -
	 */
    @Override
    public Object getChild(int i, int i1) {
        return null;
    }
	
	/**
	 * Retourne l'ID du Succès à la position indiquée
	 * @param i Index du Succès dans la ListView
	 * @return Long
	 */
	@Override
    public long getGroupId(int i) {
		return Integer.parseInt(this.listSucces.get(i).getCodecategorie() + "" + this.listSucces.get(i).getCodesucces());
    }
	
	/**
	 * Retourne l'ID du sous-item à la position indiquée
	 * @param i Position de l'item dans la ListView
	 * @param i1 Position du sous-item dans la liste des sous-items de l'item
	 * @return -
	 */
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }
	
	/**
	 * Vrai : l'ID des items ne change pas à chaque modification de la ListView (qui ne devrait pas
	 * être modifiée de toute façon)
	 * @return True
	 */
	@Override
    public boolean hasStableIds() {
        return true;
    }
    
    //endregion ////////////////////////////////////////
	
	/**
	 * Définit l'affichage de chaque item de la liste
	 */
	private class GroupHolder
    {
		// icone du Succès //
        ImageView icon;
		// niveau atteint par l'utilisateur pour ce Succès //
        ImageView niveau;
		// nom du Succès //
        TextView nom;
		// description du Succès //
        TextView description;
    }
	
	/**
	 * Méthode utilisée par le gestionnaire pour afficher chaque item de la ListView
	 * @param position Position de l'item dans la ListView
	 * @param isExpanded Indique si le sous-item est affiché ou non
	 * @param view Vue de l'item
	 * @param viewGroup Vue de la ListView
	 * @return Vue de l'item
	 */
    @Override
    public View getGroupView(int position, boolean isExpanded, View view, ViewGroup viewGroup) {
		
		View itemView;
		// REUSE VIEW IF NOT NULL CODE //
		if(view == null) {
			// redéfinit le layout utilisé pour l'item //
			itemView = inflater.inflate(R.layout.succes_list_item, null);
		}
		else{
			itemView =  view;
		}
	
		GroupHolder holder = new GroupHolder();
		holder.nom = itemView.findViewById(R.id.nom_succes);
        holder.icon = itemView.findViewById(R.id.icon_succes);
        holder.niveau = itemView.findViewById(R.id.icon_niveau);
        holder.description = itemView.findViewById(R.id.descrip_succes);

		// nom du Succès //
        holder.nom.setText(listSucces.get(position).getNom());
	
		// nom du fichier contenant l'image du Succès //
		String image;
		if(listSucces.get(position).getImage() != null) {
			image = listSucces.get(position).getImage();
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
		holder.niveau.setImageURI(Uri.parse(Utils.niveauxSuccesAnyUser.get(position)));
		
		// description du Succès //
        holder.description.setText(listSucces.get(position).getDescription());

        return itemView;
    }
	
	/**
	 * Défini l'affichage du sous-item pour chaque item de la liste
	 */
	private class ChildHolder
    {
		// score actuel et prochain palier à atteindre //
		TextView palier;
		// anecdote sur le Succès //
        TextView anecdote;
		// lancer une demande de validation //
        Button demande;
    }
	
	/**
	 * Méthode utilisée par le gestionnaire pour afficher sous-item item d'un item de la ListView
	 * @param groupPosition Position de l'item parent dans la ListView
	 * @param childPosition Position du sous-item si un item pouvait avoir plusieurs sous-item
	 * @param isLastChild Indique si le sous-item est le dernier des sous-item (toujours vrai ici)
	 * @param view Vue du sous-item
	 * @param viewGroup Vue de l'item parent
	 * @return Vue de l'item
	 */
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View view, final ViewGroup viewGroup) {
	
		View itemView;
		// REUSE VIEW IF NOT NULL CODE //
		if(view == null) {
			// redéfinit le layout utilisé pour l'item //
			itemView = inflater.inflate(R.layout.succes_list_subitem, null);
		}
		else{
			itemView =  view;
		}
		
		ChildHolder holder = new ChildHolder();
		
		// anecdote //
        holder.anecdote = itemView.findViewById(R.id.anecdote_succes);
		holder.anecdote.setText(listSucces.get(groupPosition).getAnecdote());
		
		// palier //
        holder.palier = itemView.findViewById(R.id.palier_succes);
		int score = 0;
		boolean exists = false;
		if (Utils.listDataSuccesAnyUser.indexOfKey(groupPosition) >= 0){
			// score calculé dans Utils.setUserNiveauSucces //
			score = Integer.parseInt(Utils.listDataSuccesAnyUser.get(groupPosition).get(0));
			exists = true;
		}
		
		// score à obtenir pour atteindre le premier palier //
		int premierPalier = listSucces.get(groupPosition).getPalier();
		// facteur de multiplication des paliers à atteindre //
		int facteur = listSucces.get(groupPosition).getFacteur();
		
		// calcul du score à atteindre pour le prochain palier //
		int i = 0;
		int prochainPalier = 0;
		while(score >= prochainPalier){
			// suite numérique //
			if(facteur == 1){
				prochainPalier = premierPalier + (premierPalier * i);
			}
			// suite géométrique //
			else{
				prochainPalier = (int)(premierPalier*Math.pow(facteur, i));
			}
			i++;
		}
	
		// score actuel et prochain palier à atteindre //
		String stringPalier = "Prochain palier : " + score + " / " + prochainPalier;
		holder.palier.setText(stringPalier);
	
		// bouton pour demande de validation //
		if(this.userID.equals(Utils.mAuth.getCurrentUser().getUid())) {
			holder.demande = itemView.findViewById(R.id.nouvelle_demande_succes);
			
			// fixe la valeur des variables pour etre accessibles dans le listener //
			final int tempScore = score;
			final boolean tempExist = exists;
			final int tempProchainPalier = prochainPalier;
			
			// listener de click sur le bouton //
			holder.demande.setOnClickListener(new View.OnClickListener()
			{
				/**
				 * Action lors du click sur le bouton
				 *
				 * @param view Vue du bouton cliquée
				 */
				@Override
				public void onClick(View view)
				{
					// id de l'utilisateur connecté //
					String userId = mAuth.getCurrentUser().getUid();
					
					// ID du succes (on suppose = position dans la liste //
					// TODO changer le fonctionnement des ID des Succes //
					String succesId = Integer.toString(groupPosition);
					
					// TODO supprimer partie augmentant les scores //
					// enregistrement du nouveau score //
					Utils.database.child(Config.DB_NODE_SCORES).child(userId).child(succesId).child(Config.DB_SUCCES_SCORE).setValue(tempScore + 1);
					// enregistrement de la date si le score atteint un nouveau palier //
					Date today = new Date();
					DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
					String dateOut = dateFormatter.format(today);
					if (!tempExist || tempScore + 1 == tempProchainPalier) {
						Utils.database.child(Config.DB_NODE_SCORES).child(userId).child(succesId).child(Config.DB_SUCCES_DATE).setValue(dateOut);
					}
					
					Utils.database.child(Config.DB_NODE_DEMANDES).child(userId).child(succesId).child(Config.DB_DEMANDE_SCORE).setValue(0);
					Utils.database.child(Config.DB_NODE_DEMANDES).child(userId).child(succesId).child(Config.DB_DEMANDE_DATE).setValue(dateOut);
				}
			});
		}
		else{
			((ViewManager)itemView).removeView(holder.demande);
		}
		
        return itemView;
    }
	
	/**
	 * Indique si le sous-item peut être cliqué
	 * @param i Position de l'item dans la ListView
	 * @param i1 Position du sous-item dans la liste des sous-items de l'item
	 * @return False
	 */
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
