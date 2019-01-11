package drinkteam.metier;

/**
 * Created by Timothée on 07/06/2017.
 *
 * Représente les caractéristiques d'un Utilisateur
 */
public class User
{
	
	//region ////////////////// ATTRIBUTS //////////////////
	
	private String Id;
	private String Provider;
	private String ProviderId;
	private String Email;
	private String Name;
	private String Titre;
	private String Ambiance;
	
	//endregion ////////////////////////////////////
	
	//region ////////////////// CONSTRUCTEURS //////////////////
	
	/**
	 * Constructeur neutre
	 * Entre autres utilisé par Firebase pour créé un Utilisateur à partir des données dans la base
	 * (créé un Cocktail vide et utilise les setters)
	 */
	public User(){}
	
	//endregion ////////////////////////////////////
	
	//region ////////////////// GETS & SETS //////////////////
	
	public String getId() {
		return this.Id;
	}
	
	public void setId(String id) {
		this.Id = id;
	}
	
	public String getProvider() {
		return this.Provider;
	}
	
	public void setProvider(String provider) {
		this.Provider = provider;
	}
	
	// ! Firebase ne comprend pas getProviderId ! //
	public String getProviderid(){
		return this.ProviderId;
	}
	
	// ! Firebase ne comprend pas setProviderId ! //
	public void setProviderid(String providerid) {
		this.ProviderId = providerid;
	}
	
	public String getEmail(){
		return this.Email;
	}
	
	public void setEmail(String email) {
		this.Email = email;
	}
	
	public String getName(){
		return this.Name;
	}
	
	public void setName(String name) {
		this.Name = name;
	}
	
	public String getTitre(){
		return this.Titre;
	}
	
	public void setTitre(String titre) {
		this.Titre = titre;
	}
	
	public String getAmbiance(){
		return this.Ambiance;
	}
	
	public void setAmbiance(String ambiance) {
		this.Ambiance = ambiance;
	}
	
	//endregion ////////////////////////////////////
}
