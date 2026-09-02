package com.example.crm.d02.domain;

/**
 * Dépôt de destination — remplace le test CDL.CODDEP = "CO" / "MO" du COBOL.
 *
 * Chaque enregistrement REFCDE / LINTXT / TXTCDE / LINCDE est routé vers
 * le fichier du dépôt correspondant à TCDL-CODDEP(IND-CDL).
 * FINCDE est routé vers le dépôt de PREV-CODDEP (dernier vu — comportement B03).
 */
public enum Depot {
    CO, MO;

    /**
     * Construit un Depot depuis la valeur COBOL CODDEP (2 caractères, ex : "CO").
     * Retourne null si la valeur n'est ni "CO" ni "MO" (anomalie à loguer).
     */
    public static Depot fromCobol(String coddep) {
        if (coddep == null) return null;
        switch (coddep.trim().toUpperCase()) {
            case "CO": return CO;
            case "MO": return MO;
            default:   return null;
        }
    }
}
