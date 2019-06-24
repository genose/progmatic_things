package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Personne;
import javafx.collections.ObservableList;

public class ContactDAO
{
	private FichierTexte fichierTexte;

	public ContactDAO(File nom)
	{
		fichierTexte = new FichierTexte(nom);
	}

	public List<Personne> lire()
	{
		List<Personne> contacts = new ArrayList<>();
		List<String> lignes = fichierTexte.lire();
		for (String ligne : lignes)
		{
			contacts.add(stringToPersonne(ligne));
		}
		return contacts;
	}

	private Personne stringToPersonne(String chaine)
	{
		List<String> parse;
		parse = Arrays.asList(chaine.split("\\|"));
		String nom = parse.get(0);
		String prenom = (parse.size() > 1) ? parse.get(1): "";
		String dateNaissance = (parse.size() > 2) ? parse.get(2) : "";
		return new Personne(nom, prenom, dateNaissance);
	}

	public void sauver(ObservableList<Personne> contact)
	{
		List<String> lignes = new ArrayList<>();
		for (Personne personne : contact)
		{
			lignes.add(toCSV(personne));
		}
		fichierTexte.ecrire(lignes);
	}

	private String toCSV(Personne personne)
	{
		return personne.getNom() + "|" + personne.getPrenom() + "|" + personne.getDateNaissance();
	}

}
