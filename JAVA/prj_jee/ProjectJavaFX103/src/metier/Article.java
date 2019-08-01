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

public class Article implements DAOObject {

	protected IntegerProperty id_article;
	protected StringProperty nom_article;
	protected FloatProperty prix;
	protected FloatProperty titrage;
	protected FloatProperty volume;
	protected ObjectProperty<Couleur> id_couleur;
	protected ObjectProperty<TypeBiere> id_type;
	protected ObjectProperty<Marque> id_marque;
	/* ********************************************************* */
public static String sDEFAULTNEWARTICLELIBELLE = "[Nouvel Article]";
	/* ********************************************************* */
	/* ********************************************************* */
	public static final String fieldEntityName = "Article";
	public static final String fieldID = "id_article";
	public static final String fieldLibelle = "nom_article";
	public static final String fieldCouleur = "id_couleur";
	public static final String fieldType = "id_type";
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
			FloatProperty volume) {
		super();
		
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleFloatProperty();

		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();
		
		Objects.requireNonNull(id);
		Objects.requireNonNull(nom);
		Objects.requireNonNull(prix);
		Objects.requireNonNull(titrage);
		Objects.requireNonNull(volume);
		
		this.nom_article.set( ((nom.get().length() == 0)?sDEFAULTNEWARTICLELIBELLE : nom.get() ) );
		
		this.id_article.set(id.get());

		this.prix .set( prix.get() );
		this.titrage.set(titrage.get());
		this.volume.set(volume.get());
		this.id_couleur.set(new Couleur());
		this.id_type.set(new TypeBiere());
		this.id_marque.set(new Marque());
	}

	public Article(int id, String nom, float prix, float titrage) {
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleFloatProperty();

		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();

		this.id_article.set(id);
		Objects.requireNonNull(nom);
		
		this.nom_article.set( ((nom.length() == 0)?sDEFAULTNEWARTICLELIBELLE : nom ) );
		
		this.titrage.set(titrage);
		this.prix.set(prix);
	}

	public Article() {
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleFloatProperty();

		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();

		this.nom_article.set(sDEFAULTNEWARTICLELIBELLE);
		this.id_article.set(0);
		this.titrage.set(0);
		this.prix.set(0);
	}

	public Article(int id, String nom) {
		this.id_article = new SimpleIntegerProperty();
		this.nom_article = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleFloatProperty();

		this.id_marque = new SimpleObjectProperty<>();
		this.id_couleur = new SimpleObjectProperty<>();
		this.id_type = new SimpleObjectProperty<>();

		Objects.requireNonNull(nom);

		this.nom_article.set(((nom.length() == 0) ? sDEFAULTNEWARTICLELIBELLE : nom));
		this.id_article.set(id);
		this.titrage.set(0);
		this.prix.set(0);
	}

	public final IntegerProperty getPropertyId() {
		return this.id_article;
	}

	public final Integer getId() {
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

	public final void setId(final Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);

		this.id_article.set(idKey);
	}

	public final StringProperty getPropertyLibelle() {
		return this.nom_article;
	}

	public final String getLibelle() {
		return this.getPropertyLibelle().get();
	}

	public final Boolean setLibelle(final String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		this.nom_article.set(sLibelle);
		return (this.nom_article.get() == null);
	}
/**
 * 
 * @return
 */
	public final FloatProperty getPropertyVolume() {
		return this.volume;
	}

	public final Float getVolume() {
		return this.getPropertyVolume().get();
	}

	public final void setVolume(final Float argVolume) {
		Objects.requireNonNull(volume, sERRMESSAGEDAOOBJECT_PARAM);
		this.volume.set(argVolume);
	}
/**
 * 
 * @return
 */
	public ObjectProperty<Couleur> getPropertyCouleur() {
		return id_couleur;
	}

	public Couleur getCouleur() {
		return id_couleur.get();
	}

	public void setCouleur(Couleur argCouleur) {
		Objects.requireNonNull(argCouleur, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_couleur.set(argCouleur);
	}
/**
 * 
 * @return
 */
	public TypeBiere getType() {
		return id_type.get();
	}

	public void setType(TypeBiere argType) {
		Objects.requireNonNull(argType, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_type.set(argType);
	}

	public ObjectProperty<TypeBiere> getPropertyTypeBiere() {

		return id_type;
	}
/**
 * 
 * @return
 */
	public final FloatProperty getPropertyPrix() {
		return this.prix;
	}

	public final float getPrix() {
		return this.getPropertyPrix().get();
	}

	public final void setPrix(final Float argPrix) {
		Objects.requireNonNull(argPrix, sERRMESSAGEDAOOBJECT_PARAM);
		this.prix.set(argPrix);
	}
/**
 * 
 * @return
 */
	public final FloatProperty getPropertyTitrage() {
		return this.titrage;
	}

	public final float getTitrage() {
		return this.getPropertyTitrage().get();
	}

	public final void setTitrage(final Float argTitrage) {
		Objects.requireNonNull(argTitrage, sERRMESSAGEDAOOBJECT_PARAM);
		this.getPropertyPrix().set(argTitrage);
	}
/**
 * 
 * @return
 */
	public final ObjectProperty<Marque> getPropertyMarque() {
		return this.id_marque;
	}

	public final Marque getMarque() {
		return this.getPropertyMarque().get();
	}

	public final void setMarque(final Marque argMarque) {
		Objects.requireNonNull(argMarque, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_marque.set(argMarque);
	}

	@Override
	public String toString() {
		return "Article [id=" + id_article + ", nom=" + nom_article + ", prix=" + prix + ", titrage=" + titrage
				+ ", volume=" + volume + ", couleur=" + id_couleur + ", type=" + id_type + ", marque=" + id_marque
				+ "]";
	}

}
