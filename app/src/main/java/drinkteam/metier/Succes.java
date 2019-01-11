package drinkteam.metier;

/**
 * Created by Timothée on 26/06/2016.
 *
 * Représente les caractéristiques d'un Succes
 */
public class Succes {
	
	//region ////////////////// ATTRIBUTS //////////////////
	
	private long CodeCategorie;
    private long CodeSucces;
    private String Nom;
    private String Description;
    private Integer Palier;
    private Integer Facteur;
    private String Anecdote;
    private String Image;
	
	//endregion ////////////////////////////////////
	
	//region ////////////////// CONSTRUCTEURS //////////////////
	
	/**
	 * Constructeur neutre
	 * Entre autres utilisé par Firebase pour créé un Succes à partir des données dans la base
	 * (créé un Cocktail vide et utilise les setters)
	 */
	public Succes(){}

    public Succes(long codeCategorie, long codeSucces, String nom, String description) {
        this.CodeCategorie = codeCategorie;
        this.CodeSucces = codeSucces;
        this.Nom = nom;
        this.Description = description;
		this.Palier = 1;
		this.Facteur = 1;
		this.Anecdote = "";
		this.Image = null;
    }

    public Succes(long codeCategorie, long codeSucces, String nom, String description, Integer palier, Integer facteur, String anecdote, String image) {
        this.CodeCategorie = codeCategorie;
        this.CodeSucces = codeSucces;
        this.Nom = nom;
        this.Description = description;
        this.Palier = palier;
        this.Facteur = facteur;
        this.Anecdote = anecdote;
        this.Image = image;
    }
	
	//endregion ////////////////////////////////////
	
	//region ////////////////// GETS & SETS //////////////////
	
    // ! Firebase ne comprend pas getCodeCategorie ! //
	public long getCodecategorie() {
        return this.CodeCategorie;
    }
	
	// ! Firebase ne comprend pas setCodeCategorie ! //
	public void setCodecategorie(long codecategorie) {
		this.CodeCategorie = codecategorie;
    }
	
	// ! Firebase ne comprend pas getCodeSucces ! //
    public long getCodesucces() {
        return this.CodeSucces;
    }
	
	// ! Firebase ne comprend pas setCodeSucces ! //
    public void setCodesucces(long codesucces) {
		this.CodeSucces = codesucces;
    }

    public String getNom() {
        return this.Nom;
    }

    public void setNom(String nom) {
		this.Nom = nom;
    }

    public String getDescription(){
        return this.Description;
    }

    public void setDescription(String description) {
		this.Description = description;
    }

    public Integer getPalier() {
		return this.Palier;
    }

    public void setPalier(Integer palier) {
		this.Palier = palier;
    }

    public Integer getFacteur() {
		return this.Facteur;
    }

    public void setFacteur(Integer facteur) {
		this.Facteur = facteur;
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

