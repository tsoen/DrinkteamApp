package drinkteam.controleur;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.drinkteam.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import drinkteam.metier.Succes;
import drinkteam.metier.User;
import drinkteam.vue.ConnexionActivity;
import drinkteam.vue.MainActivity;
import drinkteam.vue.Succes_Demandes_ListViewAdapter;
import drinkteam.vue.Succes_HorizontalGridViewAdapter;
import drinkteam.vue.Succes_ListViewAdapter;
import drinkteam.vue.Succes_Page;

/**
 * Created by Timothée on 05/07/2017.
 *
 * Définit des attributs et méthodes utilisés à plusieurs endroits
 * et qui ne sont pas spécifiques à une classe
 *
 * - Référence aux bases de données
 * - Données sur les succès
 * - Références Firebase
 * - Connexion et création de compte
 * - Navigation dans les fragments
 * - Algorithme de recherche
 */
public class Utils
{
	//region ////////// DONNES LOCALES //////////
	
	// base de données des Succès //
	public static SuccesDAO dbSucces;
	
	// base de données des Cocktails //
	public static CocktailDAO dbCocktails;
	
	// base de données des Jeux //
	public static JeuDAO dbJeux;
	
	// données sur les succes obtenus par un utilisateur //
	public static SparseArray<List<String>> listDataSuccesAnyUser;
	
	// liste des grades obtenus pour chaque succès d'un utilisateur //
	public static List<String> niveauxSuccesAnyUser;
	
	public static long weeklyCocktailID;
	
	//endregion
	
	//region ////////// FIREBASE //////////
	
	// reference à la base de données Firebase //
	public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
	
	// reference au cloud storage de Firebase //
	public static StorageReference storage = FirebaseStorage.getInstance().getReference();
	
	public static void storeFileByStream(final String url, final StorageReference storageNode){
		new Thread(new Runnable() {
			public void run() {
				try
				{
					InputStream stream = new URL(url).openStream();
					storageNode.putStream(stream);
				}
				catch (Exception e)
				{
					Log.d("myapp", "storeFileByStream : " + e.toString());
				}
			}
		}).start();
	}
	
	//endregion
	
	/*
	 * Utilitaires de connexion à l'application et de création de compte
	 */
	//region ////////// CONNEXION //////////
	
	// détecte la connexion à Firebase via facebook //
	public static AuthStateListener authListener;
	
	// point d'entrée principal pour la connexion à Google//
	public static GoogleApiClient mGoogleApiClient;
	
	// gère les appels du bouton de connexion à Facebook //
	public static CallbackManager callbackManager;
	
	// point d'entrée à l'outil d'authentification Firebase //
	public static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
	
	/**
	 * Enregistre l'utilisateur dans la base de données firebase et le redirige vers l'application
	 * @param context Informations globales sur l'environnement de l'applicaiotn
	 */
	public static void registerUser(final Context context){
		
		// récupération de l'utilisateur firebase connecté //
		final FirebaseUser user = mAuth.getCurrentUser();
		
		// si il y a bien un utilisateur connecté //
		if(user != null) {
			
			// objet représentant l'utilisateur créé //
			final User newUser = new User();
			
			// récupération des informations de l'utilisateur firebase //
			// toujours à la fois sur firebase et un autre provider //
			for (UserInfo profile : user.getProviderData()) {
				
				// provider d'authentification //
				String providerId = profile.getProviderId();
				
				// les informations générales sont centralisées dans le profil firebase //
				if (providerId.equals("firebase")) {
					newUser.setId(profile.getUid());
					newUser.setName(profile.getDisplayName());
					newUser.setEmail(profile.getEmail());
					
					// titre de base à la création du compte //
					newUser.setTitre("Débutant");
					
					// enregistrement asynchrone de la photo de profile dans le storage firebase //
					new Thread(new Runnable() {
						/**
						 * Méthode exécutée par ce thread
						 */
						public void run() {
							try
							{
								// enregistrement de la photo de profile sous forme de flux de données //
								InputStream stream = new URL(user.getPhotoUrl().toString()).openStream();
								storage.child(Config.STR_NODE_COMPTES).child(newUser.getId()).child(Config.STR_USER_PROFILE).putStream(stream);
							}
							catch (Exception e)
							{
								Log.d("myapp", "registerUser Profile: " + e.toString());
							}
						}
					}).start();
				}
				// données propres à l'autre provider //
				else {
					// enregistre l'autre provider d'identification (facebook.com) //
					newUser.setProvider(providerId);
					
					// identifiant de l'utilisateur propre à ce provider //
					newUser.setProviderid(profile.getUid());
					
					if(providerId.equals("facebook.com")){
						
						// requête pour obtenir la photo de couverture facebook //
						GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {
									/**
									 * Une fois la récupération terminée
									 * @param object Données récupérées par la requête encodées en JSON
									 * @param response Contient l'erreur éventuelle
									 */
									@Override
									public void onCompleted(JSONObject object, GraphResponse response) {
										try {
											
											final String url = object.getJSONObject("cover").getString("source");
											
											// enregistrement asynchrone de la photo de couverture dans le storage firebase //
											new Thread(new Runnable() {
												public void run() {
													try
													{
														InputStream stream = new URL(url).openStream();
														storage.child(Config.STR_NODE_COMPTES).child(newUser.getId()).child(Config.STR_USER_COVER).putStream(stream);
													}
													catch (Exception e)
													{
														Log.d("myapp", "registerUser Couverture: " + e.toString());
													}
												}
											}).start();
										}
										catch(Exception ex){
											Log.d("myapp", "registerUser couverture request: " + ex.toString());
										}
									}
								});
						Bundle parameters = new Bundle();
						// indique qu'on souhaite récupérer la photo de couverture //
						parameters.putString("fields", "cover");
						request.setParameters(parameters);
						request.executeAsync();
					}
				}
			}
			
			// tentative de création du compte dans la base de données //
			// si un conflit existe (un autre utilisateur s'enregistre en même temps),
			// une transaction se relance jusqu'à réussite //
			database.child(Config.DB_NODE_COMPTES).child(newUser.getId()).runTransaction(new Transaction.Handler()
			{
				/**
				 * Tentative de création du compte
				 * @param currentData Données existantes à l'endroit spécifié
				 * @return Résultat de la transaction
				 */
				@Override
				public @NonNull Transaction.Result doTransaction(@NonNull MutableData currentData)
				{
					// création du compte si il n'existe pas de données avec cette ID //
					if (currentData.getValue() == null) {
						// enregistrement automatique des données à partir d'un object User //
						currentData.setValue(newUser);
						
						return Transaction.success(currentData);
					}
					
					return Transaction.success(currentData);
				}
				
				/**
				 * Une fois la transaction terminée
				 * @param databaseError Null ou contient l'erreur survenue
				 * @param committed True si succès, false si erreur survenue
				 * @param dataSnapshot Nouvelles données présentes à l'endroit spécifié
				 */
				@Override
				public void onComplete(@Nullable DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot)
				{
					// si l'enregistrement dans la base a fonctionné //
					if(databaseError == null){
						// ouverture de l'activité principale de l'application //
						Intent intent = new Intent(context, MainActivity.class);
						context.startActivity(intent);
					}
				}
			});
			
			// supprime le listener pour ne pas le déclencher à la déconnexion //
			mAuth.removeAuthStateListener(authListener);
		}
	}
	
	/**
	 * Redirige l'utilisateur vers l'application si son compte existe dans la base
	 * @param context Contexte de l'application
	 */
	public static void loginUser(final Context context){
		
		// récupération de l'utilisateur connecté //
		FirebaseUser user = mAuth.getCurrentUser();
		
		// si il y a bien un utilisateur connecté //
		if(user != null) {
			
			// vérification de l'existence du compte dans la base de données //
			database.child(Config.DB_NODE_COMPTES).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
			{
				/**
				 * Essaye de récupérer les données à l'endroit précisé
				 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
				 * @param snapshot Contient les données présentes à cet emplacement
				 */
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot)
				{
					// si données (compte) existent //
					if (snapshot.exists()) {
						// ouverture de l'activité principale de l'application //
						Intent intent = new Intent(context, MainActivity.class);
						context.startActivity(intent);
					}
					// si le compte n'est pas enregistré dans la base de données,
					// il faut déconnecter l'utilisateur //
					else {
						// déconnexion de facebook //
						LoginManager.getInstance().logOut();
						
						// déconnexion de google //
						Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
								new ResultCallback<Status>() {
									@Override
									public void onResult(@NonNull Status status) { }
								});
						
						// message d'erreur //
						Toast.makeText(context, "Compte inexistant", Toast.LENGTH_LONG)
								.show();
					}
				}
				
				/**
				 * Arrêt de la récupération des données
				 * @param databaseError Erreur éventuelle
				 */
				@Override
				public void onCancelled(@NonNull DatabaseError databaseError)
				{
					Log.d("myapp", "loginUser " + databaseError.toString());
				}
			});
			
			// supprime le listener pour ne pas le déclencher à la déconnexion à firebase //
			mAuth.removeAuthStateListener(authListener);
		}
	}
	
	//endregion
	
	/**
	 * Changement de fragment dans l'activité principale
	 * Permet de réaliser ce changement depuis un fragment
	 * @param fragmentClass Classe du fragment à faire apparaître
	 * @param activity Référence vers l'activité qui contient les fragments
	 * @param bundle Variables à passer au fragment qui y seront accessibles
	 */
	public static void replaceFragments(Class fragmentClass, FragmentActivity activity,
										@Nullable Bundle bundle, Boolean addToBackStack)
	{
		try {
			// initialisation du nouveau fragment //
			Fragment fragment = (Fragment) fragmentClass.newInstance();
			
			if(bundle != null){
				fragment.setArguments(bundle);
			}
			
			FragmentTransaction transaction;
			// Remplace le fragment actuel par le nouveau fragment //
			if(activity.getClass() == ConnexionActivity.class){
				transaction = activity.getSupportFragmentManager()
						.beginTransaction().replace(R.id.connection_fragment_container, fragment);
			}
			// activity.getClass() == MainActivity.class //
			else {
				transaction = activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment);
			}
			
			if(addToBackStack){
				transaction.addToBackStack(null);
			}
			transaction.commit();
		}
		catch (Exception e) {
			Log.d("myapp", "replaceFragments: "  + e.toString());
		}
	}
	
	/**
	 * Calcule et affiche les images des niveaux obtenus pour les succès d'un joueur
	 * TODO: les algos de détection et stockage des succès puent bien la merde
	 * @param bundle Paramètre supplémentaires
	 * @param view Vue de la liste contenant les images de niveaux
	 */
	public static void setUserNiveauSucces(final Bundle bundle, final View view){

		Utils.database.child(Config.DB_NODE_SCORES).child(bundle.getString("id")).addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				// liste de tous les succès existant //
				List<Succes> listSucces = Utils.dbSucces.getAllSucces();
				
				// liste des données des données succès-score de l'utilisateur //
				SparseArray<List<String>> dataSucces = new SparseArray<>();
				
				// liste des grades obtenu pour chaque succès //
				List<String> niveauSucces = new ArrayList<>();
				
				if (dataSnapshot.exists()) {
					
					// récupération des données pour chaque succès obtenu //
					for (DataSnapshot succesSnapshot : dataSnapshot.getChildren()) {
						List<String> data = new ArrayList<>();
						data.add(0, succesSnapshot.child(Config.DB_SUCCES_SCORE).getValue().toString());
						data.add(1, succesSnapshot.child(Config.DB_SUCCES_DATE).getValue().toString());
						dataSucces.append(Integer.parseInt(succesSnapshot.getKey()), data);
					}
				}
				
				// définition de l'image en fonction du palier atteint //
				String imagePath;
				
				// parcours de tous les succès possibles //
				for (int pos = 0; pos < listSucces.size(); ++pos) {
					int score = 0;
					
					// si l'utilisateur possède ce succès dans sa liste //
					if (dataSucces.indexOfKey(pos) >= 0) {
						// récupération du score //
						score = Integer.parseInt(dataSucces.get(pos).get(0));
					}
					
					// calcul du palier atteint par l'utilisateur //
					int palier = listSucces.get(pos).getPalier();
					int facteur = listSucces.get(pos).getFacteur();
					int i = 0;
					int prochainPalier = 0;
					while (score >= prochainPalier) {
						if (facteur == 1) {
							prochainPalier = palier + palier * i;
						}
						else {
							prochainPalier = (int) (palier * Math.pow(facteur, i));
						}
						i++;
					}
					
					// définit l'image en fonction du palier atteint //
					switch (i - 1) {
						case 0:
							imagePath = Config.path + "niveau_inconnu";
							break;
						case 1:
							imagePath = Config.path + "niveau_bronze";
							break;
						case 2:
							imagePath = Config.path + "niveau_argent";
							break;
						case 3:
							imagePath = Config.path + "niveau_or";
							break;
						case 4:
							imagePath = Config.path + "niveau_platine";
							break;
						case 5:
							imagePath = Config.path + "niveau_diamant";
							break;
						case 6:
							imagePath = Config.path + "niveau_master";
							break;
						case 7:
							imagePath = Config.path + "niveau_challenger";
							break;
						default:
							imagePath = Config.path + "niveau_challenger";
							break;
					}
					
					niveauSucces.add(imagePath);
				}
				
				niveauxSuccesAnyUser = niveauSucces;
				listDataSuccesAnyUser = dataSucces;
				
				// HorizontalGridView de la page de profil //
				if (view.getClass() == HorizontalGridView.class) {
					Succes_HorizontalGridViewAdapter adapter = new Succes_HorizontalGridViewAdapter(view
							.getContext(), bundle.getString("id"));
					((HorizontalGridView) view).setAdapter(adapter);
				}
				
				// ExpandableListView de la page des succès //
				else if (view.getClass() == ExpandableListView.class) {
					Succes_ListViewAdapter adapter = new Succes_ListViewAdapter(view.getContext(), bundle.getString("id"));
					((ExpandableListView) view).setAdapter(adapter);
					
					int pos = -1;
					for (int i = 0; i < listSucces.size(); i++) {
						int code = Integer
								.parseInt(listSucces.get(i).getCodecategorie() + "" + listSucces.get(i).getCodesucces());
						
						if (code == bundle.getInt("succesID")) {
							pos = i;
							break;
						}
					}
					
					if (pos != -1) {
						((ExpandableListView) view).expandGroup(pos);
					}
				}
				else if (view.getClass() == ListView.class) {
					
					Utils.database.child(Config.DB_NODE_DEMANDES).child(bundle.getString("id")).addListenerForSingleValueEvent(new ValueEventListener()
					{
						/**
						 * Détecte un changement de donnée dans la base à l'emplacement défini
						 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
						 * @param dataSnapshot Contient les données présentes à cet emplacement
						 */
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot)
						{
							List<Integer> positions = new ArrayList<>();
							for(DataSnapshot succes : dataSnapshot.getChildren()){
								positions.add(Integer.parseInt(succes.getKey()));
							}
							
							Succes_Demandes_ListViewAdapter adapter = new Succes_Demandes_ListViewAdapter(view
									.getContext(), bundle.getString("id"), positions);
							((ListView) view).setAdapter(adapter);
							setListViewHeightBasedOnChildren((ListView)view);
							
						}
						
						/**
						 * Arrêt de la récupération des données
						 * @param firebaseError Erreur éventuelle
						 */
						@Override
						public void onCancelled(@NonNull DatabaseError firebaseError)
						{
							Log.d("myapp", "searchAndDisplay: " + firebaseError.toString());
						}
					});
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError firebaseError)
			{
				Log.d("myapp", "setUserNiveauSucces:" + firebaseError.toString());
			}
		});
	}
	
	/**** Method for Setting the Height of the ListView dynamically.
	 **** Hack to fix the issue of not showing all the items of the ListView
	 **** when placed inside a ScrollView  ****/
	private static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {
			int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
			int totalHeight = 0;
			View view = null;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				view = listAdapter.getView(i, view, listView);
				if (i == 0) {
					view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
				}
				
				view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
				totalHeight += view.getMeasuredHeight();
			}
			
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}
	}
	
	/*
	 * Fonctions utilisées pour la recherche d'utilisateurs
	 */
	//region ////////// ALGORITHME DE RECHERCHE //////////
	
	/**
	 * Calcule le nombre de paires de caractères en commun entre 2 chaînes
	 * L'ordre des paramètres n'a pas d'importance
	 * @param str1 Première chaîne pour la comparaison
	 * @param str2 Deuxième chaîne pour la comparaison
	 * @return Similarité lexicale entre 0 et 1
	 */
	public static double compareStrings(String str1, String str2) {
		
		// pairs de 2 caractères qui composent la 1ère chaîne //
		ArrayList<String> pairs1 = wordLetterPairs(str1.toUpperCase());
		// pairs de 2 caractères qui composent la 2ème chaîne //
		ArrayList<String> pairs2 = wordLetterPairs(str2.toUpperCase());
		
		int similarity = 0;
		int totalSize = pairs1.size() + pairs2.size();
		
		for (String pair1 : pairs1) {
			
			for(String pair2 : pairs2) {
				if (pair1.equals(pair2)) {
					similarity++;
					pairs2.remove(pair2);
					break;
				}
			}
		}
		
		return (2.0 * similarity) / totalSize;
	}
	
	/**
	 * Découpe une chaîne de mots en liste de chaînes de 2 caractères
	 * @param str Chaîne à découper
	 * @return Liste de chaînes de 2 caractères
	 */
	private static ArrayList<String> wordLetterPairs(String str) {
		ArrayList<String> allPairs = new ArrayList<>();
		
		// sépare les mots de la chaîne et les place dans un tableau //
		String[] words = str.split("\\s");
		
		// parcours des mots de la chaîne //
		for (String word : words) {
			// découpe le mot en chaînes de 2 caractères //
			String[] pairsInWord = letterPairs(word);
			
			allPairs.addAll(Arrays.asList(pairsInWord));
		}
		
		return allPairs;
	}
	
	/**
	 * Découpe un mot en tableau de chaînes de 2 caractères
	 * @param str Mot à découper
	 * @return Tableau de chaînes de 2 caractères
	 */
	private static String[] letterPairs(String str) {
		int numPairs = str.length() - 1;
		
		String[] pairs = new String[numPairs];
		for (int i = 0; i < numPairs; i++) {
			pairs[i] = str.substring(i, i+2);
		}
		
		return pairs;
	}
	
	//endregion
	
	public static void generateWeeklyCocktail(AppCompatActivity activity){
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		int currentWeek = Calendar.WEEK_OF_YEAR;
		
		int lastWeek = sharedPref.getInt("weekNumber", -1);
		
		if(lastWeek != currentWeek){
			
			weeklyCocktailID = ThreadLocalRandom.current().nextLong(0, dbCocktails.getAllCocktails().size() + 1);
			
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt("weekNumber", currentWeek);
			editor.putLong("weeklyCocktailID", weeklyCocktailID);
			editor.apply();
		}
		else{
			weeklyCocktailID = sharedPref.getLong("weeklyCocktailID", -1);
		}
		
		Log.d("myapp", weeklyCocktailID+"");
	}
}
