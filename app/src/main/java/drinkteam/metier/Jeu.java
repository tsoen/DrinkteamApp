package drinkteam.metier;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Représente les caractéristiques d'un Jeu
 */
public class Jeu {
	
	//region ////////////////// ATTRIBUTS //////////////////
	
	private long Code;
    private String Nom;
    private int Joueurs;
    private String Regles;
    private String Anecdote;
    private String Image;
	
	//endregion ////////////////////////////////////
	
	//region ////////////////// CONSTRUCTEURS //////////////////
	
	/**
	 * Constructeur neutre
	 * Entre autres utilisé par Firebase pour créé un Jeu à partir des données dans la base
	 * (créé un Cocktail vide et utilise les setters)
	 */
	public Jeu(){}
    
    public Jeu(long code, String nom, int joueurs, String regles, String anecdote, String image) {
        this.Code = code;
        this.Nom = nom;
        this.Joueurs = joueurs;
        this.Regles = regles;
        this.Anecdote = anecdote;
        this.Image = image;
    }
	
	//endregion ////////////////////////////////////
	
	//region ////////////////// GETS & SETS //////////////////
	
	public long getCode() {
        return this.Code;
    }

    public void setCode(long code) {
		this.Code = code;
    }

    public String getNom() {
        return this.Nom;
    }

    public void setNom(String nom) {
		this.Nom = nom;
    }

    public int getJoueurs(){
        return this.Joueurs;
    }

    public void setJoueurs(int joueurs) {
		this.Joueurs = joueurs;
    }

    public String getRegles(){
        return this.Regles;
    }

    public void setRegles(String regles) {
		this.Regles = regles;
    }

    public String getAnecdote(){
        return this.Anecdote;
    }

    public void setAnecdote(String anecdote) {
		this.Anecdote = anecdote;
    }

    public String getImage(){
        return this.Image;
    }

    public void setImage(String image) {
		this.Image = image;
    }
	
	//endregion ////////////////////////////////////
	
}

