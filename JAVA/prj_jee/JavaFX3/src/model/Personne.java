package model;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import outils.OutilsChaine;
import outils.OutilsDate;

public class Personne
{
	private StringProperty nom;
	private StringProperty prenom;
	private StringProperty dateNaissance;

	public Personne(String nom, String prenom, String dateNaissance)
	{
		this.nom = new SimpleStringProperty();
		this.prenom = new SimpleStringProperty();
		this.dateNaissance = new SimpleStringProperty();
		setAll(nom, prenom, OutilsDate.stringToDate(dateNaissance));
	}

	public void setAll(String nom, String prenom, LocalDate dateNaissance)
	{
		this.nom.set(nom.toUpperCase());
		this.prenom.set(OutilsChaine.toNomPropre(prenom));
		this.dateNaissance.set(OutilsDate.dateToString(dateNaissance));
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
	

	public final StringProperty prenomProperty()
	{
		return this.prenom;
	}
	

	public final String getPrenom()
	{
		return this.prenomProperty().get();
	}
	

	public final void setPrenom(final String prenom)
	{
		this.prenomProperty().set(prenom);
	}
	

	public final StringProperty dateNaissanceProperty()
	{
		return this.dateNaissance;
	}
	

	public final String getDateNaissance()
	{
		return this.dateNaissanceProperty().get();
	}
	

	public final void setDateNaissance(final String dateNaissance)
	{
		this.dateNaissanceProperty().set(dateNaissance);
	}
		

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (this.getClass() != other.getClass())
			return false;
		Personne otherP = (Personne) other;
		return nom.equals(otherP.nom) && prenom.equals(otherP.prenom) && dateNaissance.equals(otherP.dateNaissance);
	}

	@Override
	public int hashCode()
	{
		int resultat = 0;
		int i = 1;
		int mod = 10;
		for (char car : nom.get().toCharArray())
		{
			resultat += car * (i++ % mod + 1);
		}
		for (char car : prenom.get().toCharArray())
		{
			resultat += car * (i++ % mod + 1);
		}
		for (char car : dateNaissance.toString().toCharArray())
		{
			resultat += car * (i++ % mod + 1);
		}
		return resultat;
	}

	@Override
	public String toString()
	{
		return getPrenom() + " " + getNom()  ;
	}
}
