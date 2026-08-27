package com.example.crm.t10.domain;

/**
 * Représente un enregistrement du fichier MAJBDSTAT*.DAT
 * (COPY DIRCOB:T10_DESC_FICMAJBDSTAT.LIB).
 *
 * datliv : date de livraison en format VMS ASCII "DD-MON-YYYY HH:MM:SS.CC"
 *          passée telle quelle à SYS$BINTIM (B-T10-01).
 * numcde : PIC 9(7) dans T10 (7 chiffres, différent de D05 qui est PIC 9(9)).
 */
public final class LivraisonRecord {

    private final String coddep;
    private final String codlab;
    private final int    numcde;
    private final int    numral;
    private final String datliv;

    public LivraisonRecord(String coddep, String codlab, int numcde, int numral, String datliv) {
        this.coddep = coddep;
        this.codlab = codlab;
        this.numcde = numcde;
        this.numral = numral;
        this.datliv = datliv;
    }

    public String getCoddep() { return coddep; }
    public String getCodlab() { return codlab; }
    public int    getNumcde() { return numcde; }
    public int    getNumral() { return numral; }
    public String getDatliv() { return datliv; }
}
