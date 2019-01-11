package drinkteam.controleur;

/**
 * Created by Timothée on 05/07/2017.
 *
 * Définit des constantes utilisées à plusieurs endroits dans l'application
 * Cela permet d'éviter les fautes de frappes, de faciliter leur modification, et centralise
 * leur déclaration
 */
public class Config
{
	// mode de requête google pour la connexion à un compte existant //
	public static final int GOOGLE_SIGN_IN = 1000;
	
	// mode de requête google pour la création d'un compte //
	public static final int GOOGLE_CREATE = 1001;
	
	// id web client google //
	public static final String SERVER_CLIENT_ID = "124880078835-74l55gp163776circadj6incbnptbjtj.apps.googleusercontent.com";
	
	// chemin de stockage des ressources image //
	public static final String path = "android.resource://com.drinkteam/drawable/";
	
	public static final int PICK_IMAGE_PROFILE = 2000;
	
	public static final int PICK_IMAGE_COVER = 2001;
	
	public static final int CAPTURE_IMAGE_PROFILE = 2002;
	
	public static final int CAPTURE_IMAGE_COVER = 2003;
	
	//region ////////// DATABASE NODE NAMES //////////
	
	public static final String DB_NODE_COCKTAILS = "Cocktails";
	
	public static final String DB_NODE_COMPTES = "Comptes";
	
	public static final String DB_USER_ID = "id";
	
	public static final String DB_USER_NAME = "name";
	
	public static final String DB_USER_PROVIDER = "provider";
	
	public static final String DB_USER_PROVIDERID = "providerid";
	
	public static final String DB_USER_TITRE = "titre";
	
	public static final String DB_NODE_SCORES = "ComptesSucces";
	
	public static final String DB_NODE_JEUX = "Jeux";
	
	public static final String DB_NODE_SUCCES = "Succes";
	
	public static final String DB_SUCCES_SCORE = "Score";
	
	public static final String DB_SUCCES_DATE = "Date";
	
	public static final String DB_NODE_DEMANDES = "SuccesDemandes";
	
	public static final String DB_DEMANDE_SCORE = "Score";
	
	public static final String DB_DEMANDE_DATE = "Date";
	
	//endregion
	
	//region ////////// STORAGE NODE NAMES //////////
	
	public static final String STR_NODE_COMPTES = "Comptes";
	
	public static final String STR_USER_PROFILE = "profile";
	
	public static final String STR_USER_COVER = "cover";
	
	// nom du fichier dans le storage firebase pour la photo de couverture par défaut //
	public static final String STR_DEFAULT_COVER = "defaultCover.png";
	
	// nom du fichier dans le storage firebase pour la photo de profile par défaut //
	public static final String STR_DEFAULT_PROFILE = "defaultProfile.png";
	
	//endregion
	
}
