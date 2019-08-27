package metier;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Article
{
	private IntegerProperty id;
	private StringProperty nom;
	private FloatProperty prix;
	private FloatProperty titrage;
	private IntegerProperty volume;
	private Couleur couleur;
	private Type type;
	private ObjectProperty<Marque> marque;

	public Article(int id, String nom, float titrage, float prix)
	{
		this.id = new SimpleIntegerProperty();
		this.nom = new SimpleStringProperty();
		this.titrage = new SimpleFloatProperty();
		this.prix = new SimpleFloatProperty();
		this.volume = new SimpleIntegerProperty();
		this.marque = new SimpleObjectProperty<>();
		this.nom.set(nom);
		this.id.set(id);
		this.titrage.set(titrage);
		this.prix.set(prix);
	}

	public Article()
	{
	}

	public Article(int id, String nom)
	{
		this.nom.set(nom);
		this.id.set(id);
	}

	public final IntegerProperty idProperty()
	{
		return this.id;
	}

	public final int getId()
	{
		return this.idProperty().get();
	}

	public final void setId(final int id)
	{
		this.idProperty().set(id);
	}

	public final StringProperty nomProperty()
	{
		return this.nom;
	}

	public final String getNom()
	{
		return this.nomProperty().get();
	}

	public final void setNom(final String nom)
	{
		this.nomProperty().set(nom);
	}

	public final IntegerProperty volumeProperty()
	{
		return this.volume;
	}

	public final int getVolume()
	{
		return this.volumeProperty().get();
	}

	public final void setVolume(final int volume)
	{
		this.volumeProperty().set(volume);
	}

	public Couleur getCouleur()
	{
		return couleur;
	}

	public void setCouleur(Couleur couleur)
	{
		this.couleur = couleur;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public final FloatProperty prixProperty()
	{
		return this.prix;
	}

	public final float getPrix()
	{
		return this.prixProperty().get();
	}

	public final void setPrix(final float prix)
	{
		this.prixProperty().set(prix);
	}

	public final FloatProperty titrageProperty()
	{
		return this.titrage;
	}

	public final float getTitrage()
	{
		return this.titrageProperty().get();
	}

	public final void setTitrage(final float titrage)
	{
		this.titrageProperty().set(titrage);
	}

	public final ObjectProperty<Marque> marqueProperty()
	{
		return this.marque;
	}

	public final Marque getMarque()
	{
		return this.marqueProperty().get();
	}

	public final void setMarque(final Marque marque)
	{
		this.marqueProperty().set(marque);
	}

	@Override
	public String toString()
	{
		return "Article [id=" + id + ", nom=" + nom + ", prix=" + prix + ", titrage=" + titrage + ", volume=" + volume
				+ ", couleur=" + couleur + ", type=" + type + ", marque=" + marque + "]";
	}

}
