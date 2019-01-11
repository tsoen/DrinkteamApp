package drinkteam.controleur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import drinkteam.metier.Succes;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Permet de manipuler la base de données temporaire des Succes
 * Pour notre application seuls le stockage et la récupération devraient être utilisés
 *
 * Elle extend DAOBase qui représente concrètement la base de données
 */
public class SuccesDAO extends DAOBase {
	
	/*
	 * Définit les colonnes de la table des Succes dans la base de données
	 * Modifier ici nécessite aussi de modifier dans la création de la table dans DataBaseHandler
	 */
	//region //////////////////// ATTRIBUTS ////////////////////
	
	private static final String TABLE_NAME = "tableSucces";
	private static final String CODECATEGORIE = "CodeCategorie";
	private static final String CODESUCCES = "CodeSucces";
	private static final String NOM = "Nom";
	private static final String DESCRIPTION = "Description";
	private static final String PALIER = "Palier";
	private static final String FACTEUR = "Facteur";
	private static final String ANECDOTE = "Anecdote";
	private static final String IMAGE = "Image";
	
	//endregion ////////////////////////////////////////
	
	//region //////////////////// CONSTRUCTEUR ////////////////////
	
	/**
	 * Constructeur
	 * @param context Contexte de l'activité
	 */
	public SuccesDAO(Context context)
    {
		// Appel le constructeur de DAOBase//
		super(context);
    }
	
	//endregion ////////////////////////////////////////
	
	/*
	 * Opérations CRUD (Create, Read, Update, Delete) sur la table des Succes
	 */
	//region //////////////////// OPERATIONS ////////////////////
	
	/**
	 * Ajoute un succes à la base
	 * @param succes Succes à ajouter
	 */
	public void addSucces(Succes succes) {
        ContentValues values = new ContentValues();
        values.put(CODECATEGORIE, succes.getCodecategorie());
        values.put(CODESUCCES, succes.getCodesucces());
        values.put(NOM, succes.getNom());
        values.put(DESCRIPTION, succes.getDescription());
        values.put(PALIER, succes.getPalier());
        values.put(FACTEUR, succes.getFacteur());
        values.put(ANECDOTE, succes.getAnecdote());
        values.put(IMAGE, succes.getImage());
		
		this.database.insert(TABLE_NAME, null, values);
	}
	
	/**
	 * Retourne un succes de la base
	 * @param codeCategorie ID de la catégorie du succes
	 * @param codeSucces ID du succes
	 * @return Succes
	 */
	public Succes getSucces(long codeCategorie, long codeSucces) {
		// récupération des valeurs du succes dans la base //
		Cursor cursor = this.database.query(TABLE_NAME, new String[] { CODECATEGORIE, CODESUCCES, NOM, DESCRIPTION, PALIER, FACTEUR, ANECDOTE, IMAGE },
				"(" + CODECATEGORIE + "," + CODESUCCES + ")" + " = ?",
				new String[] {String.valueOf(codeCategorie), String.valueOf(codeSucces)}, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		// création du succes à partir des valeurs récupérées //
		Succes succes = new Succes(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
				cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)),
				Integer.parseInt(cursor.getString(5)), cursor.getString(6), cursor.getString(7));
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return succes;
	}
	
	/**
	 * Retourne la liste des succes de la base
	 * @return Liste de succes
	 */
	public List<Succes> getAllSucces() {
		List<Succes> SuccesList = new ArrayList<>();
		
		// requête de sélection de toutes les lignes de la table //
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		
		// récupération des lignes //
		Cursor cursor = this.database.rawQuery(selectQuery, null);
		
		// boucle sur toutes les lignes récupérées si au moins une existe //
		if (cursor.moveToFirst()) {
			do {
				// création du succès à partir des valeurs de la ligne //
				Succes succes = new Succes();
				succes.setCodecategorie(cursor.getLong(0));
				succes.setCodesucces(cursor.getLong(1));
				succes.setNom(cursor.getString(2));
				succes.setDescription(cursor.getString(3));
				succes.setPalier(cursor.getInt(4));
				succes.setFacteur(cursor.getInt(5));
				succes.setAnecdote(cursor.getString(6));
				succes.setImage(cursor.getString(7));
				
				// ajout du succes à la liste //
				SuccesList.add(succes);
			}
			while (cursor.moveToNext());
		}
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return SuccesList;
	}
	
	/**
	 * Supprime un succes de la base
	 * @param codeCategorie ID de la catégorie du succes
	 * @param codeSucces ID du succes
	 */
    public void deleteSucces(long codeCategorie, long codeSucces) {
		this.database.delete(TABLE_NAME, "(" + CODECATEGORIE + "," + CODESUCCES + ")" + " = ?",
				new String[] {String.valueOf(codeCategorie), String.valueOf(codeSucces)});
    }
	
	/**
	 * Remplace un succes par un nouveau avec le même ID
	 * @param succes Nouveau succes
	 */
    public void updateSucces(Succes succes) {
        ContentValues value = new ContentValues();
        value.put(NOM, succes.getNom());
        value.put(DESCRIPTION, succes.getDescription());
        value.put(PALIER, succes.getPalier());
        value.put(FACTEUR, succes.getFacteur());
        value.put(ANECDOTE, succes.getAnecdote());
        value.put(IMAGE, succes.getImage());
		this.database.update(TABLE_NAME, value, "(" + CODECATEGORIE + "," + CODESUCCES + ")" + " = ?",
				new String[] {String.valueOf(succes.getCodecategorie()), String.valueOf(succes.getCodesucces())});
    }
	
	/**
	 * Compte le nombre de succes de la base
	 * Evite de faire getAllSucces().size(), léger gain de temps
	 * @return (int) Nombre de succes
	 */
    public int getSuccesCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = this.database.rawQuery(countQuery, null);
		int res = cursor.getCount();
	
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();

        return res;
    }
	
	//endregion ////////////////////////////////////////
	
}

