package drinkteam.vue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Dialog de Préférence contenant le DatePicker de sélection d'une date
 * En tant que préférence, la prochaine ouverture du dialog aura en mémoire la dernière date sélectionnée
 */
public class Preferences_Date extends DialogPreference {

	//region //////////////////// ATTRIBUTS ////////////////////
	
	// valeur de la date sélectionnée sous forme de chaîne //
    private String dateval;

	// indique la valeur actuelle de la préférence dans le menu des préférences //
    private CharSequence mSummary;
	
	//endregion ////////////////////////////////////////
	
	//region //////////////////// CONSTRUCTEUR ////////////////////
	
	/**
	 * Constructeur
	 * @param ctxt Contexte de l'application
	 * @param attrs Paramètres de la préférences
	 */
    public Preferences_Date(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

		this.dateval = this.getText();
	
		// bouton de validation du dialog //
		this.setPositiveButtonText("Set");
		
		// bouton d'annulation du dialog //
		this.setNegativeButtonText("Cancel");
    }
    
    //endregion ////////////////////////////////////////
	
	//region //////////////////// GETS & SETS ////////////////////
	
	/**
	 * Renvoi l'année sélectionnée
	 * @param dateval Date complète
	 * @return Année sous forme d'entier
	 */
	public int getYear(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[2]));
    }
	
	/**
	 * Renvoi le mois sélectionné
	 * @param dateval Date complète
	 * @return Mois sous forme d'entier
	 */
	public int getMonth(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[1]));
    }
	
	/**
	 * Renvoi le jour sélectionné
	 * @param dateval Date complète
	 * @return Jour sous forme d'entier
	 */
	public int getDate(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[0]));
    }
	
	/**
	 * La date par défaut est "date du jour -18 ans"
	 * @return Date par défaut
	 */
	public String getDefaultDate(){

		// récupère le jour et le mois actuels //
        DateFormat formatJourMois = new SimpleDateFormat("dd-MM-");
        Date dateJourMois = new Date();
        String moisJour = formatJourMois.format(dateJourMois);

		// calcule année actuelle - 18 //
        DateFormat formatAnnee = new SimpleDateFormat("yyyy");
        Date dateAnnee = new Date();
        String annee = Integer.toString((Integer.parseInt(formatAnnee.format(dateAnnee))) - 18) ;

        return (moisJour + annee);
    }
	
	/**
	 * Enregistre la date sélectionnée sous forme de chaîne
	 * La valeur enregistrée sera utilisée pour appeler setSummary
	 * @param text Valeur de la date
	 */
	public void setText(String text) {
		boolean wasBlocking = this.shouldDisableDependents();
		
		// enregistre la date sélectionnée sous forme de chaîne //
		this.dateval = text;
        this.persistString(text);

		boolean isBlocking = this.shouldDisableDependents();

        if (isBlocking != wasBlocking) {
            this.notifyDependencyChange(isBlocking);
        }
    }
	
	/**
	 * Récupère la valeur de la date enregistrée
	 * @return Valeur de la date
	 */
	public String getText() {
        try
        {
            SharedPreferences prefs = getSharedPreferences();
            return prefs.getString("naissance_user", this.dateval);
        }
        catch(NullPointerException ex) {
            return this.getDefaultDate();
        }
    }
	
	/**
	 * Récupère la valeur affichée sur l'écran des préférences
	 * Ne pas supprimer même si No Usage Found
	 * @return Date affichée
	 */
	@Override
	public CharSequence getSummary() {
        return this.mSummary;
    }
	
	/**
	 * Défini la valeur affichée sur l'écran des préférences
	 * @param summary Date à afficher
	 */
	@Override
    public void setSummary(CharSequence summary) {
        if (summary == null && this.mSummary != null || summary != null && !summary.equals(this.mSummary))
        {
            this.mSummary = summary;
            notifyChanged();
        }
    }
    
    //endregion ////////////////////////////////////////
}