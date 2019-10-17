package main.java.fr.jk.outils;



public class Chaine {

	private Chaine() {
	}

	public static String setMaj(String chaine) {
		
		if (chaine.isEmpty()) return chaine;
		StringBuilder resultat = new StringBuilder();
		resultat.append(chaine.substring(0, 1).toUpperCase());
		
			
			for (int i = 1; i < chaine.length(); i++) {
				if (chaine.substring(i - 1, i).equals(" ") || chaine.substring(i - 1, i).equals("-")) {
					resultat.append(chaine.substring(i, i + 1).toUpperCase());
				} else {
					resultat.append(chaine.substring(i, i + 1).toLowerCase());
				}
			}
			return resultat.toString();
		

	}

	public static String epureAccents(String chaine) {
		StringBuilder resultat = new StringBuilder();
		chaine = chaine.toLowerCase();
		int pos;
		String carAccentue = "àâäçéêëèìîïòôöùûüÿ";
		String carSubstit = "aaaceeeeiiiooouuuy";
		for (int i = 0; i < chaine.length(); i++) {
			pos = carAccentue.indexOf(chaine.substring(i, i + 1));
			if (pos == -1) {
				resultat.append(chaine.substring(i, i + 1));
			} else {
				resultat.append(carSubstit.substring(pos, pos + 1));
			}
		}
		return resultat.toString();
	}

	public static String repeter(String chaine, int nombre) {
		StringBuilder resultat = new StringBuilder();
		for (int i = 1; i <= nombre; i++) {
			resultat.append(chaine);
		}
		return resultat.toString();

	}

}
