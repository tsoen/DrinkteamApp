package drinkteam.controleur;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Représente le coeur de la base de données SQLite
 * Contient la base de données SQLiteDatabase et son gestionnaire DataBaseHandler
 *
 * La base de données est stockée sous forme de fichier dans un dossier de l'application
 * et n'est modifiée que si la VERSION dans le fichier est différente de la VERSION de cette classe
 */
public class DAOBase {

    //region ////////// ATTRIBUTS //////////
	
    // Version de la base //
	// Cette version est utilisée par le DataBaseHandler pour détecter une évolution //
    private final static int VERSION = 1;

    // Le nom du fichier qui représente la base //
    private final static String NOM = "database";

    // base de données SQLite //
    public SQLiteDatabase database;

    // gestionnaire de la base de données //
    private DataBaseHandler handler;

	//endregion
	
    /**
     * Constructeur
     * Initialise le gestionnaire et la base qui lui est associée
     * @param context Contexte de l'activité
     */
    public DAOBase(Context context) {
        this.handler = new DataBaseHandler(context, NOM, null, VERSION);
        this.open();

        // force le reset des tables à chaque ouverture TODO remove //
        this.handler.onUpgrade(database, VERSION, 100);
    }

    /**
     * Créer et/ou ouvre une connexion à la base de données
     */
    public void open() {

        // Pas besoin de fermer la dernière base puisque getWritableDatabase() s'en charge //
        this.database = handler.getWritableDatabase();
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void close() {
        this.database.close();
    }

    /**
     * Accesseur
     * @return La base de données actuelle
     */
    public SQLiteDatabase getDatabase() {
        return this.database;
    }
}

