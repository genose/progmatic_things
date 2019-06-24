package dao;

import java.io.File;
import java.util.ArrayList;

import service.ParametreBean;

public class ParametresDAO
{
	private String repertoire;
	private ArrayList<String> fichiersRecents;

	public ParametresDAO()
	{
		fichiersRecents = new ArrayList<>();
		String home = System.getProperty("user.home");
		FichierTexte parametreFile = new FichierTexte(new File(home + "\\monApplication.set"));
		ArrayList<String> lignes = parametreFile.lire();

		if (!lignes.isEmpty() && lignes.get(0).startsWith("Répertoire="))
			repertoire = lignes.get(0).substring(11);
		else
			repertoire = "\\";
		for (int i = 1 ; i < lignes.size();i++)
		{
			fichiersRecents.add(lignes.get(i).substring(8));
		}	
	}

	public String getRepertoire()
	{
		return repertoire;
	}

	public ArrayList<String> getFichiersRecents()
	{
		return fichiersRecents;
	}
	
	public void sauver(ParametreBean parametres)
	{
		String home = System.getProperty("user.home");
		FichierTexte parametreFile = new FichierTexte(new File(home + "\\monApplication.set"));
		ArrayList<String> lignes = new ArrayList<>();
		lignes.add("Répertoire=" + parametres.getDernierRepertoire());	
		fichiersRecents = parametres.getFichiersRecents();
		for (String fichier : fichiersRecents)
		{
			lignes.add("Fichier=" + fichier);
		}
		parametreFile.ecrire(lignes);
	}
}
