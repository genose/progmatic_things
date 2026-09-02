package com.example.crm.d02.domain;

/**
 * Remplace une entrée du tableau COBOL TABLE-CDL (OCCURS 1000).
 *
 * Chaque groupe (NUMLIG, CODART) du curseur CURCDL produit exactement
 * 2 entrées LigneCumulee (comportement B01) :
 *   — codenr = '1' : récapitulatif, lotfab toujours vide, jamais écrite dans les fichiers.
 *   — codenr = '3' : détail lot, lotfab conservé si GESLOT ∈ {'4','5'}.
 *                    Seule cette entrée génère un LINCDE.
 *
 * Les quantités sont cumulées sur toutes les lignes du même groupe.
 */
public final class LigneCumulee {

    /** TCDL-NUMLIG PIC 9(4) — numéro de ligne commande. */
    private final String numlig;
    /** TCDL-CODART PIC X(10) — code article. */
    private final String codart;
    /** TCDL-ARTLAB PIC X(15) — libellé laboratoire article. */
    private final String artlab;
    /** TCDL-ARTSAI PIC X(07) — code saisie article. */
    private final String artsai;
    /** TCDL-LIBELL PIC X(35) — libellé (non utilisé dans LINCDE). */
    private final String libell;
    /** TCDL-ARTCIP PIC X(7) — code CIP. */
    private final String artcip;
    /**
     * TCDL-LOTFAB PIC X(12).
     * Vide (espaces) pour codenr='1' et pour codenr='3' si GESLOT ∉ {'4','5'}.
     * Conservé sinon.
     */
    private final String lotfab;
    /** TCDL-QTELIV — somme D.CDL.QTLCDE. Alimente ENR-LINCDE-QTCCDE malgré le nom. */
    private final long   qteliv;
    /** TCDL-QTCGRT — somme D.CDL.QTCGRT + D.CDL.QTCECH. Alimente ENR-LINCDE-QTCGRT. */
    private final long   qtcgrt;
    /** TCDL-QTECDE — somme D.CDL.QTCCDE (non écrite dans le fichier de transmission). */
    private final long   qtecde;
    /**
     * '1' = entrée récapitulative (jamais écrite dans les fichiers).
     * '3' = entrée lot (produit un LINCDE).
     */
    private final char   codenr;
    /** TCDL-CODLAB PIC X(4) — code laboratoire. */
    private final String codlab;
    /** TCDL-CODDEP "CO" ou "MO" (éventuellement remplacé pour lab 2951). */
    private final String coddep;
    /** TCDL-CODLABLAB PIC X(4) — alimente ENR-LINCDE-CODLABLAB. */
    private final String codlablab;

    public LigneCumulee(String numlig, String codart, String artlab, String artsai,
                        String libell, String artcip, String lotfab,
                        long qteliv, long qtcgrt, long qtecde,
                        char codenr, String codlab, String coddep, String codlablab) {
        this.numlig    = numlig;
        this.codart    = codart;
        this.artlab    = artlab;
        this.artsai    = artsai;
        this.libell    = libell;
        this.artcip    = artcip;
        this.lotfab    = lotfab;
        this.qteliv    = qteliv;
        this.qtcgrt    = qtcgrt;
        this.qtecde    = qtecde;
        this.codenr    = codenr;
        this.codlab    = codlab;
        this.coddep    = coddep;
        this.codlablab = codlablab;
    }

    public String getNumlig()    { return numlig;    }
    public String getCodart()    { return codart;    }
    public String getArtlab()    { return artlab;    }
    public String getArtsai()    { return artsai;    }
    public String getLibell()    { return libell;    }
    public String getArtcip()    { return artcip;    }
    public String getLotfab()    { return lotfab;    }
    public long   getQteliv()    { return qteliv;    }
    public long   getQtcgrt()    { return qtcgrt;    }
    public long   getQtecde()    { return qtecde;    }
    public char   getCodenr()    { return codenr;    }
    public String getCodlab()    { return codlab;    }
    public String getCoddep()    { return coddep;    }
    public String getCodlablab() { return codlablab; }

    /** Retourne true si cette entrée est de type CODENR='3' (génère un LINCDE). */
    public boolean isDetail() { return codenr == '3'; }

    /** Retourne le dépôt correspondant (CO ou MO), ou null si valeur inconnue. */
    public Depot getDepot() { return Depot.fromCobol(coddep); }
}
