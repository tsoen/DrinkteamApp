package drinkteam.vue;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import drinkteam.controleur.CocktailDAO;
import drinkteam.controleur.Config;
import drinkteam.controleur.JeuDAO;
import drinkteam.controleur.SuccesDAO;
import drinkteam.controleur.Utils;
import drinkteam.metier.Cocktail;
import drinkteam.metier.Jeu;
import drinkteam.metier.Succes;
import com.drinkteam.R;

/**
 * Conteneur principal de l'application
 * Cette activité gère :
 * - l'ActionBar qui contient le titre de l'application et les boutons associés
 * - le menu de navigation entre les différentes pages. Ces pages sont représentées sous forme de
 * 	Fragment qui occupent tous la même zone de l'activité
 * - la récupération et l'initialisation des bases de données de Jeux, Cocktails et Succès
 */
public class MainActivity extends AppCompatActivity
    implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback{
	
	//region //////////////////// ATTRIBUTS ////////////////////
	
    // menu de navigation //
    private DrawerLayout drawerLayout;

    // bouton sur l'ActionBar qui permet d'ouvrir le menu //
    private ActionBarDrawerToggle drawerButton;

    // liste qui va contenir les différentes lignes du menu //
    private ListView drawerItems;

    // zone de texte du titre de l'actionBar (nécessaire pour centrer le texte) //
    private TextView titreActionBar;
	
	//endregion

    /**
     * Ouverture de l'activitié / application
     * @param savedInstanceState Etat sauvegardé de l'application si elle a été mise de côté
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		// on défini la Vue qui constitue l'activité //
		setContentView(R.layout.general_mainactivity);
		
		//region //////////////////// BASES DE DONNEES ////////////////////
	
		// base de données des Cocktails //
		Utils.dbCocktails = new CocktailDAO(this);
		// base de données des Jeux //
		Utils.dbJeux = new JeuDAO(this);
		// base de données des Succès //
		Utils.dbSucces = new SuccesDAO(this);
		
		// récupération des Cocktails de Firebase //
		Utils.database.child(Config.DB_NODE_COCKTAILS).addListenerForSingleValueEvent(new ValueEventListener() {
			/**
			 * Détecte un changement de donnée dans la base à l'emplacement défini
			 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
			 * @param dataSnapshot Contient les données présentes à cet emplacement
			 */
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot cocktailSnapshot: dataSnapshot.getChildren()) {
					Utils.dbCocktails.addCocktail(cocktailSnapshot.getValue(Cocktail.class));
				}
				
				Utils.generateWeeklyCocktail(MainActivity.this);
			}
			
			/**
			 * Arrêt de la récupération des données
			 * @param firebaseError Erreur éventuelle
			 */
			@Override
			public void onCancelled(@NonNull DatabaseError firebaseError) {
				Log.d("myapp", "Init. Cocktails: " + firebaseError.toString());
			}
		});
		
		// récupération des Jeux de Firebase //
		Utils.database.child(Config.DB_NODE_JEUX).addListenerForSingleValueEvent(new ValueEventListener() {
			/**
			 * Détecte un changement de donnée dans la base à l'emplacement défini
			 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
			 * @param dataSnapshot Contient les données présentes à cet emplacement
			 */
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot jeuSnapshot : dataSnapshot.getChildren()) {
					Utils.dbJeux.addJeu(jeuSnapshot.getValue(Jeu.class));
				}
			}
			
			/**
			 * Arrêt de la récupération des données
			 * @param firebaseError Erreur éventuelle
			 */
			@Override
			public void onCancelled(@NonNull DatabaseError firebaseError) {
				Log.d("myapp", "Init. Cocktails: " + firebaseError.toString());
			}
		});
		
		// récupération des Succès de Firebase puis chargement de la page d'accueil //
		Utils.database.child(Config.DB_NODE_SUCCES).addListenerForSingleValueEvent(new ValueEventListener() {
			/**
			 * Détecte un changement de donnée dans la base à l'emplacement défini
			 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
			 * @param dataSnapshot Contient les données présentes à cet emplacement
			 */
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot succesSnapshot : dataSnapshot.getChildren()) {
					Utils.dbSucces.addSucces(succesSnapshot.getValue(Succes.class));
				}
				
				// on attend que tous les succès aient été initialisés pour charger la page d'accueil //
				chargementAccueil(savedInstanceState);
			}
			
			/**
			 * Arrêt de la récupération des données
			 * @param firebaseError Erreur éventuelle
			 */
			@Override
			public void onCancelled(@NonNull DatabaseError firebaseError) {
				Log.d("myapp", "Init. Cocktails: " + firebaseError.toString());
			}
		});
		
        //endregion

        //region //////////////////// ACTIONBAR ////////////////////

        // sélectionne la Vue de l'actionBar, qui contient la zone d'affichage du titre //
        View viewActionBar = getLayoutInflater().inflate(R.layout.general_actionbar, null);

        // défini le texte de la zone d'affichage du titre //
		this.titreActionBar = viewActionBar.findViewById(R.id.actionbar_textview);

        // permet de centrer la zone de texte qui contient le titre de l'actionBar //
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER
        );

        ActionBar actionBar = getSupportActionBar();

        // créer une vue personnalisée avec les paramètres définis précedemment //
        actionBar.setCustomView(viewActionBar, params);

        // applique la vue personnalisée à notre actionBar
        actionBar.setDisplayShowCustomEnabled(true);
		
        // empêche l'affichage d'un titre automatique //
        actionBar.setDisplayShowTitleEnabled(false);

		// TODO: check if necessary
        // clique sur le titre de l'actionBar :
        // false -> remonter en haut de la page; true -> remonter d'un niveau
        actionBar.setDisplayHomeAsUpEnabled(true);
        // boutton "home" de l'actionBar ?
        actionBar.setHomeButtonEnabled(true);

        //endregion

        //region //////////////////// MENU DE NAVIGATION ////////////////////

        // définition de la vue du Menu principal //
        this.drawerLayout = findViewById(R.id.drawer_layout);

        // récupèration de la liste des titres du menu //
        String[] drawerItemsList = getResources().getStringArray(R.array.items);

        // définition de la Vue qui contient la liste des pages et on lui ajoute les titres //
		this.drawerItems = findViewById(R.id.my_drawer);
		this.drawerItems.setAdapter(new ArrayAdapter<>(this, R.layout.general_menu_item, drawerItemsList));

        // listener de click sur les onglets //
		this.drawerItems.setOnItemClickListener(new MyDrawerItemClickListener());

        // création du bouton sur l'actionBar qui va ouvrir le menu drawerLayout //
		this.drawerButton = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.ouverture, R.string.fermeture);

        // on précise au Menu qu'il doit s'ouvrir quand on appuie sur le bouton en haut à gauche //
		this.drawerLayout.addDrawerListener(this.drawerButton);
		this.drawerButton.syncState();
		
		//endregion ////////////////////////////////////////
		
	}
	
	/**
	 * Chargement du fragment de la page d'accueil à l'ouverture de l'application
	 * @param savedInstanceState Etat sauvegardé de l'application si elle a été mise de côté
	 */
	private void chargementAccueil(Bundle savedInstanceState){
		// vérification que l'activité utilise bien le layout qui doit contenir les fragments //
		if (findViewById(R.id.fragment_container) != null) {
			
			// si un état est déjà en sauvegarde, ne pas recréer le fragment
			// pour éviter une superposition
			if (savedInstanceState == null) {
				
				Bundle bundle = new Bundle();
				bundle.putString("titre_actionbar", getResources().getString(R.string.app_name));
				
				// ajout de fragment dans la zone qui doit le contenir //
				Utils.replaceFragments(Accueil_Page.class, this, bundle, false);
			}
		}
	}
	
	//region //////////////////// MENU ////////////////////
	
	/**
	 * Listener de cliques sur le menu
	 */
	private class MyDrawerItemClickListener implements ListView.OnItemClickListener {
		
		/**
		 * Comportement à la détection d'un clique
		 * @param parent AdapterView où le clique a eu lieu
		 * @param view Vue de l'AdapterView
		 * @param pos Index de l'onglet sélectionné
		 * @param id ID de l'index
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			
			// créer un nouveau fragment qui correspond à l'onglet sélectionné //
			Class newFragmentClass = null;
			
			Bundle bundle = new Bundle();
			
			switch (pos){
				case 0:
					newFragmentClass = Accueil_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.app_name));
					break;
				case 1:
					newFragmentClass = Succes_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Mes_succes));
					bundle.putString("id", Utils.mAuth.getCurrentUser().getUid());
					break;
				case 2:
					newFragmentClass = Amis_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Mes_amis));
					break;
				case 3:
					newFragmentClass = Cocktails_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Les_recettes));
					break;
				case 4:
					newFragmentClass = Jeux_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Idées_jeux));
					break;
				case 5:
					newFragmentClass = TrouverBar_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Trouver_bar));
					break;
				case 6:
					newFragmentClass = News_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.News));
					break;
				case 7:
					newFragmentClass = Evenements_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Evènements));
					break;
				case 8:
					newFragmentClass = Preferences_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.Paramètres));
					break;
				case 9:
					newFragmentClass = About_Page.class;
					bundle.putString("titre_actionbar", getResources().getString(R.string.AboutUs));
					break;
				default:
					break;
			}
			
			// si un fragment a bien été créé //
			if(newFragmentClass != null)
			{
				// remplace le fragment actuel par le nouveau fragment //
				Utils.replaceFragments(newFragmentClass, MainActivity.this, bundle, true);
			}
			
			// ferme le Menu, ce qui va redessiner l'actionBar et mettre à jour le titre //
			drawerLayout.closeDrawer(drawerItems);
		}
	}
	
	/**
	 * Appelé uniquement à la première ouverture de l'application ( onCreate )
	 * @param menu Actionbar
	 * @return Validation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//créer le menu avec les boutons en haut à droite
		getMenuInflater().inflate(R.menu.menu, menu);
		
		return true;
	}
	
	//endregion ////////////////////////////////////////
	
	/**
     * La page des préférences (Paramètres) est composée de plusieurs écrans au sein
	 * d'un même fragment. Cette fonction est appelée à chaque ouverture d'un des écrans (sauf
	 * l'écran de démarrage du fragment) ex: écran "Notifications" ou "Modifier le profil"
	 * On redessine le fragment on lui indiquant dans quel écran on va se trouver
     * @param fragmentCompat Fragment de la page de préférences
     * @param preferenceScreen L'écran de préférences ouvert
     * @return Validation de l'ouverture
     */
    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat fragmentCompat, PreferenceScreen preferenceScreen) {
		
		// indique au fragment dans quel écran de préférence on va se trouver //
        Bundle bundle = new Bundle();
        bundle.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());

        // remplacement du fragment actuel par le nouveau fragment //
		Utils.replaceFragments(Preferences_Page.class, this, bundle, true);
		
        return true;
    }
	
    /**
     * Détecte les clicks sur les boutons de l'actionBar :
	 * - le drawerButton du menu de navigation
	 * - le bouton de recherche
	 * - le bouton "écrire"
     * @param item Bouton sélectionné
     * @return Validation du clique
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// si l'item sélectionné est le bouton d'ouverture du menu, ouvre le menu //
        if (this.drawerButton.onOptionsItemSelected(item)) {
            return true;
        }
        else {
			switch (item.getItemId()) {
				// si bouton de recherche sélectionné //
				case R.id.action_search:
					// initialisation du dialog de recherche d'utilisateurs //
					final Dialog dialog = new Dialog(MainActivity.this, R.style.full_screen_dialog);
					dialog.setContentView(R.layout.amis_search_dialog);
					// background semi-transparent //
					Drawable background = new ColorDrawable(Color.BLACK);
					background.setAlpha(130);
					dialog.getWindow().setBackgroundDrawable(background);
					dialog.show();
					
					// bouton de fermeture du dialog //
					Button cancel_button = dialog.findViewById(R.id.action_bar_cancel_search);
					// listener de click //
					cancel_button.setOnClickListener(new View.OnClickListener()
					{
						/**
						 * Action lors du click sur le bouton
						 * @param view Vue du bouton cliquée
						 */
						@Override
						public void onClick(View view)
						{
							// fermeture du dialog //
							dialog.cancel();
						}
					});
					
					// champs de recherche //
					final EditText text_search = dialog.findViewById(R.id.actionbar_edittext);
					// gestionnaire d'événements quand l'utilisateur entre du texte //
					text_search.addTextChangedListener(new TextWatcher()
					{
						// timer pour ne pas déclencher les événements à chaque caractère //
						private Timer timer = new Timer();
						// durée du timer en millisecondes //
						private final long DELAY = 500;
						
						@Override
						public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
						
						@Override
						public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
						
						/**
						 * Evenement déclenché après avoir entré du texte
						 * @param editable Contenu du champ
						 */
						@Override
						public void afterTextChanged(final Editable editable)
						{
							// réinitialisation du timer à chaque fois que cette méthode est appelée
							// (à chaque caractère entré dans le champ) //
							timer.cancel();
							timer = new Timer();
							// si le timer atteint le DELAY (il n'a pas été réinitialisé à 0 avant)
							// déclenche la recherche //
							timer.schedule(new TimerTask()
							{
								/**
								 * Opération réalisée dans un autre thread
								 */
								@Override
								public void run()
								{
									// recherche et affiche les utilisateurs //
									searchAndDisplay(dialog, editable.toString());
								}
							}, DELAY);
						}
					});
					
					text_search.setOnTouchListener(new View.OnTouchListener() {
						@Override
						public boolean onTouch(View view, MotionEvent motionEvent)
						{
							if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
								if(motionEvent.getRawX() >= (text_search.getRight() - text_search.getCompoundDrawables()[2].getBounds().width())) {
									text_search.getText().clear();
								}
							}
							return false;
						}
					});
					return true;
				
				case R.id.action_talk:
					return true;
				
				default:
					return super.onOptionsItemSelected(item);
			}
		}
    }
	
	/**
	 * Détection du click sur le bouton "retour" du téléphone
	 */
	@Override
	public void onBackPressed() {
		// TODO dialog de confirmation pour fermer l'application
		// détection de la page actuellement affichée //
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		
		// si on se trouve sur la page d'acueil, on peut demander confirmation
		// avant de fermer l'application //
		if (fragment instanceof Accueil_Page) {
			finishAndRemoveTask();
		}
		// sinon on garde le comportement basique de revenir à la dernière page affichée //
		else{
			super.onBackPressed();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && data != null && data.getData() != null) {
			
			final FirebaseUser user = Utils.mAuth.getCurrentUser();
			final Uri url = data.getData();
			
			if (requestCode == Config.PICK_IMAGE_PROFILE) {
				
				new Thread(new Runnable()
				{
					public void run()
					{
						try {
							Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid()).child(Config.STR_USER_PROFILE).putFile(url);
						}
						catch (Exception e) {
							Log.d("myapp", "onActivityResult setCoverImage: " + e.toString());
						}
					}
				}).start();
			}
			else if (requestCode == Config.PICK_IMAGE_COVER) {
				
				new Thread(new Runnable()
				{
					public void run()
					{
						try {
							Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid()).child(Config.STR_USER_COVER).putFile(url);
						} catch (Exception e) {
							Log.d("myapp", "onActivityResult setCoverImage: " + e.toString());
						}
					}
				}).start();
			}
			else {
				try
				{
					Bundle extras = data.getExtras();
					Bitmap imageBitmap = (Bitmap) extras.get("data");
					
					File imageFile = new File(getApplicationContext().getFilesDir(), "tempPicture.jpg");
					
					OutputStream fileOutputStream = new FileOutputStream(imageFile);
					
					imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
					fileOutputStream.flush();
					fileOutputStream.close();
					
					if (requestCode == Config.CAPTURE_IMAGE_PROFILE) {
						
						Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid()).child(Config.STR_USER_PROFILE)
								.putFile(Uri.parse(imageFile.toURI().toString()));
					}
					else if (requestCode == Config.CAPTURE_IMAGE_COVER) {
						
						Utils.storage.child(Config.STR_NODE_COMPTES).child(user.getUid()).child(Config.STR_USER_COVER)
								.putFile(Uri.parse(imageFile.toURI().toString()));
					}
				}
				catch (Exception e) {
					Log.d("myapp", "Error writing bitmap");
				}
			}
		}
	}
	
	/**
	 * Recherche les utilisateurs correspondants au texte entré et les affiche dans le dialog
	 * @param dialog Dialog de recherche  des utilisateurs
	 * @param query La chaîne entrée par l'utilisateur
	 */
    private void searchAndDisplay(final Dialog dialog, final String query)
	{
		// récupère tous les comptes de la base de données //
		Utils.database.child(Config.DB_NODE_COMPTES).addListenerForSingleValueEvent(new ValueEventListener()
		{
			/**
			 * Détecte un changement de donnée dans la base à l'emplacement défini
			 * Appelée 1 seule fois immédiatement pour un ListenerForSingleValueEvent
			 * @param dataSnapshot Contient les données présentes à cet emplacement
			 */
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				// ListView qui va contenir les résultats de la recherche //
				final ListView listView = dialog.findViewById(R.id.search_friends_list);
				
				// si du texte a bien été entré pour effectuer la recherche //
				if(query != null && !query.equals("")) {
					// liste qui va contenir les résultats et leur "score" (pertinence) de recherche //
					HashMap<DataSnapshot, Double> userList = new HashMap<>();
					
					// parcours de tous les comptes //
					for (DataSnapshot compte : dataSnapshot.getChildren()) {
						// comparaison de leur nom au texte de recherche //
						// le "score" de recherche est compris entre 0 et 1 //
						String name = compte.child(Config.DB_USER_NAME).getValue().toString();
						double similarity = Utils.compareStrings(name, query);
						// critère de sélection pour l'affichage //
						if (similarity > 0.5) {
							// ajout du résultat dans la liste qui sera à afficher //
							userList.put(compte, similarity);
						}
					}
					
					// manipulation pour transformer la liste en tableau, ce qui va permettre
					// de trier les résultats par score de recherche //
					Map.Entry<DataSnapshot, Double>[] arrayType = (Map.Entry<DataSnapshot, Double>[]) Array
							.newInstance(Map.Entry.class, userList.size());
					Map.Entry<DataSnapshot, Double>[] userArray = userList.entrySet().toArray(arrayType);
					
					// tri du tableau des résultats en fonction de leur score de recherche //
					Arrays.sort(userArray, new Comparator()
					{
						// précise à l'algorithme de tri que le critère de tri est le "score" de recherche //
						public int compare(Object o1, Object o2)
						{
							return ((Map.Entry<DataSnapshot, Double>) o2).getValue()
									.compareTo(((Map.Entry<DataSnapshot, Double>) o1).getValue());
						}
					});
					
					// donne le tableau trié des résultats au gestionnaire de la ListView //
					Amis_SearchListViewAdapter adapter = new Amis_SearchListViewAdapter(getApplicationContext(), userArray);
					listView.setAdapter(adapter);
					// détecte les clicks sur les résultats et affiche le profil de l'utilisateur sélectionné //
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{
						/**
						 * Action lors du clique sur un cocktail
						 * @param adapterView L'adapter qui gère la liste
						 * @param view La vue qui contient l'adapter
						 * @param position La position dans la liste de l'item cliqué
						 * @param id L'ID de l'item cliqué (Amis_SearchListViewAdapter.getItemId(position))
						 */
						@Override
						public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
						{
							// récupère l'utilisateur sélectionné dans la liste
							// (Amis_SearchListViewAdapter.getItemId(position)) //
							Map.Entry<DataSnapshot, Double> userEntry = (Map.Entry<DataSnapshot, Double>) adapterView
									.getItemAtPosition(position);
							
							// récupère les données de l'utilisateur //
							DataSnapshot userData = userEntry.getKey();
							
							// défini les paramètres pour l'affichage du profil de l'utilisateur //
							Bundle bundle = new Bundle();
							bundle.putString("titre_actionbar", userData.child(Config.DB_USER_NAME).getValue().toString());
							bundle.putString("id", userData.child(Config.DB_USER_ID).getValue().toString());
							bundle.putString("name", userData.child(Config.DB_USER_NAME).getValue().toString());
							bundle.putString("provider", userData.child(Config.DB_USER_PROVIDER).getValue().toString());
							bundle.putString("providerid", userData.child(Config.DB_USER_PROVIDERID).getValue().toString());
							bundle.putString("titre", userData.child(Config.DB_USER_TITRE).getValue().toString());
							
							// affiche le profil de l'utilisateur //
							Utils.replaceFragments(Amis_Profil.class, MainActivity.this, bundle, true);
							
							// ferme le dialog de recherche //
							dialog.cancel();
						}
					});
				}
				// si aucun texte n'a été entré pour la recherche, supprime les résultats d'une recherche précédente //
				else{
					listView.setAdapter(null);
				}
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
	
	public void setTitreActionBar(final String titre){
		
		this.titreActionBar.setText(titre);
		
		titreActionBar.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// empeche le déclenchement plusieurs fois //
				titreActionBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				
				if(titreActionBar.getLineCount() > 1){
					
					String[] words = titre.split(" ");
					
					StringBuilder builder = new StringBuilder();
					int i = 1;
					for(String word : words) {
						if(i == words.length / 2){
							builder.append(word);
							builder.append("\n");
						}
						else{
							builder.append(word);
						}
						
						i++;
					}
					
					titreActionBar.setText(builder.toString());
				}
			}
		});
	}
}

