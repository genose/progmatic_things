package metier;


import java.util.Objects;

import dao.objectInterface.DAOObject; 
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Article implements DAOObject
{
	
	protected IntegerProperty id_article;
	protected StringProperty nom_article;
	protected FloatProperty prix;
	protected FloatProperty titrage;
	protected IntegerProperty volume;
	protected ObjectProperty<Couleur> id_couleur;
	protected ObjectProperty<TypeBiere> id_type;
	protected ObjectProperty<Marque> id_marque;

	public static final String fieldEntityName = "Article";
	public static final String fieldID = "id_article";
	public static final String fieldLibelle = "nom_article";
	public static final String fieldCouleur = "id_couleur";
	public static final  String fieldType = "id_type";
	public static final String fieldTitrage = "titrage";
	public static final String fieldVolume = "volume";
	public static final String fieldPrix = "prix_achat";
	public static final String fieldMarque = "id_marque";
	
	/**
	 * @param id
	 * @param nom
	 * @param prix
	 * @param titrage
	 * @param volume
	 * @param id_couleur
	 * @param id_type
	 * @param id_marque
	 */
	public Article(IntegerProperty id, StringProperty nom, FloatProperty prix, FloatProperty titrage,
			IntegerProperty volume) {
		super();
		this.id_article = id;
		this.nom_article = nom;
		this.prix = prix;
		this.titrage = titrage;
		this.volume = volume;
		this.id_couleur = null;
		this.id_type = null;
		this.id_marque = null;
	}

	public Article(int id, String nom, float prix, float titrage)
	{
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleIntegerProperty();
		
		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();
		
		this.id_article.set(id);
		this.nom_article.set(nom);
		this.titrage.set(titrage);
		this.prix.set(prix);
	}

	public Article()
	{
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleIntegerProperty();
		
		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();
		
		this.nom_article.set("");
		this.id_article.set(0);
		this.titrage.set(0);
		this.prix.set(0);
	}

	public Article(int id, String nom)
	{
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleIntegerProperty();
		
		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();
		
		
		this.nom_article.set(nom);
		this.id_article.set(id);
		this.titrage.set(0);
		this.prix.set(0);
	}

	public final IntegerProperty getPropertyId()
	{
		return this.id_article;
	}

	public final Integer getId()
	{
		return this.getPropertyId().get();
	}

	@Override
	public void setPropertyId(final IntegerProperty idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(idKey.get(), sERRMESSAGEDAOOBJECT_PARAM);
		this.id_article = idKey;
		
	}

	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(sLibelle.get(), sERRMESSAGEDAOOBJECT_PARAM);
		this.nom_article = sLibelle;
		return this.getLibelle() != null;
	}

	
	public final void setId(final Integer idKey)
	{
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		
		this.id_article.set(idKey);
	}

	public final StringProperty getPropertyLibelle()
	{
		return this.nom_article;
	}

	public final String getLibelle()
	{
		return this.getPropertyLibelle().get();
	}

	public final Boolean setLibelle(final String sLibelle)
	{
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		this.nom_article.set(sLibelle); 
		return (this.nom_article.get() == null);
	}

	public final IntegerProperty getPropertyVolume()
	{
		return this.volume;
	}

	public final int getVolume()
	{
		return this.getPropertyVolume().get();
	}

	public final void setVolume(final Integer volume)
	{
		Objects.requireNonNull(volume, sERRMESSAGEDAOOBJECT_PARAM);
		this.volume.set(volume);
	}

	public Couleur getCouleur()
	{
		return id_couleur.get();
	}

	public void setCouleur(Couleur aCouleur)
	{
		Objects.requireNonNull(aCouleur, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_couleur.set(aCouleur);
	}

	public TypeBiere getType()
	{
		return id_type.get();
	}

	public void setType(TypeBiere aType)
	{
		Objects.requireNonNull(aType, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_type.set(aType);
	}

	public final FloatProperty getPropertyPrix()
	{
		return this.prix;
	}

	public final float getPrix()
	{
		return this.getPropertyPrix().get();
	}

	public final void setPrix(final Float aPrix)
	{
		Objects.requireNonNull(aPrix, sERRMESSAGEDAOOBJECT_PARAM);
		this.prix.set(aPrix);
	}

	public final FloatProperty getPropertyTitrage()
	{
		return this.titrage;
	}

	public final float getTitrage()
	{
		return this.getPropertyTitrage().get();
	}

	public final void setTitrage(final Float aTitrage)
	{
		Objects.requireNonNull(aTitrage, sERRMESSAGEDAOOBJECT_PARAM);
		this.getPropertyPrix().set(aTitrage);
	}

	public final ObjectProperty<Marque> getPropertyMarque()
	{
		return this.id_marque;
	}

	public final Marque getMarque()
	{
		return this.getPropertyMarque().get();
	}

	public final void setMarque(final Marque aMarque)
	{
		Objects.requireNonNull(aMarque, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_marque.set(aMarque);
	}

	@Override
	public String toString()
	{
		return "Article [id=" + id_article + ", nom=" + nom_article + ", prix=" + prix + ", titrage=" + titrage + ", volume=" + volume
				+ ", couleur=" + id_couleur + ", type=" + id_type + ", marque=" + id_marque + "]";
	}

}

