/**
 * 
 */
package org.genose.java.implementation.streams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * @author 59013-36-18
 *
 */
public class LectureFichier {
	
	String nomFichier;
	public LectureFichier (String nomFichier)
	{
		this.nomFichier = nomFichier;
	}
	
	public ArrayList <String> lire()
	{
		ArrayList<String> lignes = new ArrayList<>();
		try (BufferedReader fichier = new BufferedReader(new FileReader(nomFichier)))
		{	
			String ligne;
			while((ligne = fichier.readLine()) != null)
			{
				lignes.add(ligne);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return lignes;
	}
	
	public static Boolean pathExists(String aPath) {
		boolean bRessourceExists = false;
		try {
			
			Path aRessourcePath = Paths.get(aPath);
			
			if( aRessourcePath != null) {
				File tmpPath = new File(aRessourcePath.toAbsolutePath().toString()); 
				bRessourceExists = tmpPath.exists();
			}else {
				File tmpPath = new File(aPath); 
				bRessourceExists = tmpPath.exists();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return bRessourceExists;
	}
}
