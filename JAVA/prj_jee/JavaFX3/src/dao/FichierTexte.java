package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author François
 *
 */
public class FichierTexte
{
	private File file;

	public FichierTexte(File file)
	{
		this.file = file;
		
		if (!file.exists() )
		{
			try
			{
				 if (file.createNewFile())
					 System.out.println("Fichier créé !!!");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> lire()
	{
		ArrayList<String> lignes = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
		{
			String ligne;
			while ((ligne = bufferedReader.readLine()) != null)
			{
				lignes.add(ligne);
			}
		}
		catch (IOException e)
		{
			// Nothing to do
		}
		return lignes;
	}

	public void ecrire(List<String> lignes)
	{
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file)))
		{
			for (String ligne : lignes)
			{
				bufferedWriter.append(ligne);
				bufferedWriter.newLine();
			}
		}
		catch (IOException exception)
		{
			// Nothing to do
		}
	}
}
