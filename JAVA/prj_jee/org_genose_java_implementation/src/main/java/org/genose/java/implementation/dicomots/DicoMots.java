package org.genose.java.implementation.dicomots;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.genose.java.implementation.streams.LectureFichier;

/**
 * 
 */

/**
 * @author 59013-36-18
 *
 */
public class DicoMots {

	static public final String DEFAULT_DICO_LANG = "Francais";
	private String sLangueDico = DEFAULT_DICO_LANG;
	private Path documentPath = null;
	private ArrayList<String> mots;

	/**
	 * @param sLangueDico
	 */
	public DicoMots(String sLangueDicoToSet) {
		super();
		this.sLangueDico = (((sLangueDicoToSet != null) && (String.valueOf(sLangueDicoToSet).length() > 2))
				? sLangueDico
				: DEFAULT_DICO_LANG);
		/* **************************************************************** */
		try {

			this.documentPath = Paths.get("Dictionnaires", sLangueDico + ".txt");

			if (this.documentPath != null) {
				boolean bFileExists = LectureFichier.pathExists(this.documentPath.toAbsolutePath().toString());
 
				if (!bFileExists) {
					throw new FileNotFoundException(this.documentPath.toAbsolutePath().toString());
				}

			} else {
				/* **************************************************************** */
				String cheminFichier = "Dictionniares.fr.txt";
				/* **************************************************************** */
				this.mots = (new LectureFichier(cheminFichier)).lire();
			}
			/* **************************************************************** */
		} catch (Exception e) {
			e.printStackTrace();

			this.mots = new ArrayList<String>();
			this.mots.add("Exception");
			this.mots.add("LolliDolls");

		}

	}

	/**
	 * 
	 * @return String
	 */
	public String getLangueDico() {
		return sLangueDico;
	}

	public void setLangueDico(String sLangueDicoToSet) {

		this.sLangueDico = (((sLangueDicoToSet != null) && (String.valueOf(sLangueDicoToSet).length() > 2))
				? sLangueDico
				: DEFAULT_DICO_LANG);
	}

	public String genererMotAleatoire() {
		Random alea = new Random();
		return mots.get(alea.nextInt(mots.size()));
	}

	public int count() {
		return ((mots != null) ? mots.size() : 0);
	}

}
