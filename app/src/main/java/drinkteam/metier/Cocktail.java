package drinkteam.metier;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Représente les caractéristiques d'un Cocktail
 */
public class Cocktail {
    
	//region ////////////////// ATTRIBUTS //////////////////
	
    private long Code;
    private String Nom;
    private String Recette;
    private String Alcool;
    private String Anecdote;
    private String Image;
	
	//endregion ////////////////////////////////////

	//region ////////////////// CONSTRUCTEURS //////////////////
	
	/**
	 * Constructeur neutre
	 * Entre autres utilisé par Firebase pour créé un Cocktail à partir des données dans la base
	 * (créé un Cocktail vide et utilise les setters)
	 */
    public Cocktail(){}
	
    public Cocktail(long code, String nom, String recette, String alcool, String anecdote, String image) {
        this.Code = code;
        this.Nom = nom;
        this.Recette = recette;
        this.Alcool = alcool;
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

    public String getRecette(){
        return this.Recette;
    }

    public void setRecette(String recette) {
		this.Recette = recette;
    }

    public String getAlcool(){
        return this.Alcool;
    }

    public void setAlcool(String alcool) {
		this.Alcool = alcool;
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

