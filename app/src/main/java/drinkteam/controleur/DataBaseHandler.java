package drinkteam.controleur;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Utilitaire qui permet de gérer la connexion, création, évolution de la base de données
 * suivant la version de l'application
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    /**
     * Constructeur
     * @param context Contexte de l'activité
     * @param name Le nom du fichier qui contient la base de données, ou null pour une base de
     *             données en mémoire ce qui permet d'avoir des tables temporaires
     * @param factory Factory à utiliser pour créer les curseurs, ou null par défaut
     * @param version Version de la base de données, appelle onUpgrade() si la version stockée dans
     *                le fichier ne correspond pas à celle indiquée
     */
    public DataBaseHandler(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Initialise les tables de la base de données
     * @param db Base de données SQLLite qui va contenir les tables
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // création table cocktails //
        db.execSQL("CREATE TABLE tableCocktails (" +
                "Code INTEGER PRIMARY KEY, " +
                "Nom TEXT NOT NULL, " +
                "Recette TEXT NOT NULL, " +
                "Alcool TEXT, " +
                "Anecdote TEXT, " +
                "Image TEXT);");

        // création table jeux //
        db.execSQL("CREATE TABLE tableJeux (" +
                "Code INTEGER PRIMARY KEY, " +
                "Nom TEXT NOT NULL, " +
                "Joueurs INTEGER NOT NULL CHECK (Joueurs > 0), " +
                "Regles TEXT NOT NULL, " +
                "Anecdote TEXT, " +
                "Image TEXT);");

        // création table succès //
        db.execSQL("CREATE TABLE tableSucces (" +
                "CodeCategorie INTEGER NOT NULL, " +
                "CodeSucces INTEGER NOT NULL, " +
                "Nom TEXT NOT NULL, " +
                "Description TEXT NOT NULL, " +
                "Palier INTEGER NOT NULL, " +
                "Facteur INTEGER NOT NULL, " +
                "Anecdote TEXT, " +
                "Image TEXT, " +
                "PRIMARY KEY (CodeCategorie,CodeSucces));");
    }

    /**
     * Comportement lors de l'upgrade de la base
	 * Appelée si le constructeur détecte une nouvelle version de la base
     * @param db Base de données SQLLite
     * @param oldVersion Numéro de l'ancienne version
     * @param newVersion Numéro de la nouvelle version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// supprime les tables existantes //
        db.execSQL("DROP TABLE IF EXISTS tableCocktails;");
        db.execSQL("DROP TABLE IF EXISTS tableJeux;");
        db.execSQL("DROP TABLE IF EXISTS tableSucces;");
		
		// recrée les tables //
        onCreate(db);
    }
}

