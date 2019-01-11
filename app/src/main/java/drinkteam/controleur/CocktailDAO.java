package drinkteam.controleur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import drinkteam.metier.Cocktail;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Permet de manipuler la base de données temporaire des Cocktails
 * Pour notre application seuls le stockage et la récupération devraient être utilisés
 *
 * Elle extend DAOBase qui représente concrètement la base de données
 */
public class CocktailDAO extends DAOBase {
	
	/*
	 * Définit les colonnes de la table des Cocktails dans la base de données
	 * Modifier ici nécessite aussi de modifier dans la création de la table dans DataBaseHandler
	 */
	//region //////////////////// ATTRIBUTS ////////////////////
			
	private static final String TABLE_NAME = "tableCocktails";
    private static final String CODE = "Code";
    private static final String NOM = "Nom";
    private static final String RECETTE = "Recette";
    private static final String ALCOOL = "Alcool";
    private static final String ANECDOTE = "Anecdote";
    private static final String IMAGE = "Image";
	
	//endregion ////////////////////////////////////////
	
	//region //////////////////// CONSTRUCTEUR ////////////////////
	
	/**
	 * Constructeur
	 * @param context Contexte de l'activité
	 */
    public CocktailDAO(Context context)
    {
		// Appel le constructeur de DAOBase//
        super(context);
    }
    
    //endregion ////////////////////////////////////////
	
	/*
	 * Opérations CRUD (Create, Read, Update, Delete) sur la table des Cocktails
	 */
	//region //////////////////// OPERATIONS ////////////////////
	
	/**
	 * Ajoute un cocktail à la base
	 * @param cocktail Cocktail à ajouter
	 */
	public void addCocktail(Cocktail cocktail) {
        ContentValues values = new ContentValues();
		values.put(CODE, cocktail.getCode());
		values.put(NOM, cocktail.getNom());
        values.put(RECETTE, cocktail.getRecette());
        values.put(ALCOOL, cocktail.getAlcool());
        values.put(ANECDOTE, cocktail.getAnecdote());
        values.put(IMAGE, cocktail.getImage());
		
        this.database.insert(TABLE_NAME, null, values);
    }
	
	/**
	 * Retourne un cocktail de la base
	 * @param id ID du cocktail à récupérer
	 * @return Cocktail
	 */
	public Cocktail getCocktail(long id) {
		// récupération des valeurs du cocktail dans la base //
		Cursor cursor =  this.database.query(TABLE_NAME, new String[] { CODE, NOM, RECETTE, ALCOOL, ANECDOTE, IMAGE },
				CODE + " = ?", new String[] { String.valueOf(id) }, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		// création du cocktail à partir des valeurs récupérées //
		Cocktail cocktail = new Cocktail(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
				cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return cocktail;
	}
	
	/**
	 * Retourne la liste des cocktails de la base
	 * @return Liste de cocktails
	 */
	public List<Cocktail> getAllCocktails() {
		List<Cocktail> CocktailList = new ArrayList<>();
		
		// requête de sélection de toutes les lignes de la table //
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		
		// récupération des lignes //
		Cursor cursor =  this.database.rawQuery(selectQuery, null);
		
		// boucle sur toutes les lignes récupérées si au moins une existe //
		if (cursor.moveToFirst()) {
			do {
				// création du cocktail à partir des valeurs de la ligne //
				Cocktail cocktail = new Cocktail();
				cocktail.setCode(cursor.getLong(0));
				cocktail.setNom(cursor.getString(1));
				cocktail.setRecette(cursor.getString(2));
				cocktail.setAlcool(cursor.getString(3));
				cocktail.setAnecdote(cursor.getString(4));
				cocktail.setImage(cursor.getString(5));
				
				// ajout du cocktail à la liste //
				CocktailList.add(cocktail);
			} while (cursor.moveToNext());
		}
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return CocktailList;
	}
	
	/**
	 * Supprime un cocktail de la base
	 * @param id ID du cocktail
	 */
	public void deleteCocktail(long id) {
		this.database.delete(TABLE_NAME, CODE + " = ?", new String[] {String.valueOf(id)});
    }
	
	/**
	 * Remplace un cocktail par un nouveau avec le même ID
	 * @param cocktail Nouveau cocktail
	 */
	public void updateCocktail(Cocktail cocktail) {
        ContentValues value = new ContentValues();
        value.put(NOM, cocktail.getNom());
        value.put(RECETTE, cocktail.getRecette());
        value.put(ALCOOL, cocktail.getAlcool());
        value.put(ANECDOTE, cocktail.getAnecdote());
        value.put(IMAGE, cocktail.getImage());
		
		this.database.update(TABLE_NAME, value, CODE  + " = ?", new String[] {String.valueOf(cocktail.getCode())} );
    }
	
	/**
	 * Compte le nombre de cocktails de la base
	 * Evite de faire getAllCocktails().size(), léger gain de temps
	 * @return (int) Nombre de cocktails
	 */
	public int getCocktailCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor =  this.database.rawQuery(countQuery, null);
		int res =  cursor.getCount();
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return res;
    }
    
    //endregion ////////////////////////////////////////

}

