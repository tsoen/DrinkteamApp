package drinkteam.controleur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import drinkteam.metier.Jeu;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Permet de manipuler la base de données temporaire des Jeux
 * Pour notre application seuls le stockage et la récupération devraient être utilisés
 *
 * Elle extend DAOBase qui représente concrètement la base de données
 */
public class JeuDAO extends DAOBase {
	
	/*
	 * Définit les colonnes de la table des Jeux dans la base de données
	 * Modifier ici nécessite aussi de modifier dans la création de la table dans DataBaseHandler
	 */
	//region //////////////////// ATTRIBUTS ////////////////////
	
	private static final String TABLE_NAME = "tableJeux";
	private static final String CODE = "Code";
	private static final String NOM = "Nom";
	private static final String JOUEURS = "Joueurs";
	private static final String REGLES = "Regles";
	private static final String ANECDOTE = "Anecdote";
	private static final String IMAGE = "Image";
	
	//endregion ////////////////////////////////////////
	
	//region //////////////////// CONSTRUCTEUR ////////////////////
	
	/**
	 * Constructeur
	 * @param context Contexte de l'activité
	 */
	public JeuDAO(Context context)
    {
		// Appel le constructeur de DAOBase//
		super(context);
    }
	
	//endregion ////////////////////////////////////////
	
	/*
	 * Opérations CRUD (Create, Read, Update, Delete) sur la table des Jeux
	 */
	//region //////////////////// OPERATIONS ////////////////////
	
	/**
	 * Ajoute un jeu à la base
	 * @param jeu Jeu à ajouter
	 */
	public void addJeu(Jeu jeu) {
        ContentValues values = new ContentValues();
		values.put(CODE, jeu.getCode());
		values.put(NOM, jeu.getNom());
		values.put(JOUEURS, jeu.getJoueurs());
		values.put(REGLES, jeu.getRegles());
		values.put(ANECDOTE, jeu.getAnecdote());
		values.put(IMAGE, jeu.getImage());
		
        this.database.insert(TABLE_NAME, null, values);
    }
	
	/**
	 * Retourne un jeu de la base
	 * @param id ID du jeu
	 * @return Jeu
	 */
	public Jeu getJeu(long id) {
		// récupération des valeurs du jeu dans la base //
		Cursor cursor =  this.database.query(TABLE_NAME, new String[] { CODE, NOM, JOUEURS, REGLES, ANECDOTE, IMAGE },
				CODE + " = ?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		// création du jeu à partir des valeurs récupérées //
		Jeu jeu = new Jeu(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
				Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return jeu;
	}
	
	/**
	 * Retourne la liste des jeux de la base
	 * @return Liste de jeux
	 */
	public List<Jeu> getAllJeux() {
		List<Jeu> JeuList = new ArrayList<>();
		
		// requête de sélection de toutes les lignes de la table //
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		
		// récupération des lignes //
		Cursor cursor =  this.database.rawQuery(selectQuery, null);
		
		// boucle sur toutes les lignes récupérées si au moins une existe //
		if (cursor.moveToFirst()) {
			do {
				// création du jeu à partir des valeurs de la ligne //
				Jeu jeu = new Jeu();
				jeu.setCode(cursor.getLong(0));
				jeu.setNom(cursor.getString(1));
				jeu.setJoueurs(cursor.getInt(2));
				jeu.setRegles(cursor.getString(3));
				jeu.setAnecdote(cursor.getString(4));
				jeu.setImage(cursor.getString(5));
				
				// ajout du jeu à la liste //
				JeuList.add(jeu);
			} while (cursor.moveToNext());
		}
		
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
		
		return JeuList;
	}
	
	/**
	 * Supprime un jeu de la base
	 * @param id ID du jeu
	 */
    public void deleteJeu(long id) {
		this.database.delete(TABLE_NAME, CODE + " = ?", new String[] {String.valueOf(id)});
    }
	
	/**
	 * Remplace un jeu par un nouveau avec le même ID
	 * @param jeu Nouveau jeu
	 */
    public void updateJeu(Jeu jeu) {
        ContentValues value = new ContentValues();
        value.put(NOM, jeu.getNom());
        value.put(JOUEURS, jeu.getJoueurs());
        value.put(REGLES, jeu.getRegles());
        value.put(ANECDOTE, jeu.getAnecdote());
        value.put(IMAGE, jeu.getImage());
		
		this.database.update(TABLE_NAME, value, CODE  + " = ?", new String[] {String.valueOf(jeu.getCode())} );
    }
	
	/**
	 * Compte le nombre de jeux de la base
	 * Evite de faire getAllJeux().size(), léger gain de temps
	 * @return (int) Nombre de jeux
	 */
    public int getJeuxCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor =  this.database.rawQuery(countQuery, null);
		int res =  cursor.getCount();
	
		// libère l'objet de recherche dans la base pour en effectuer d'autres //
		cursor.close();
	
		return res;
    }
	
	//endregion ////////////////////////////////////////
	
}

