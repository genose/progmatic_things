package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement REFCDE — en-tête de commande, écrit une fois par changement
 * de groupe laboratoire dans TABLE-CDL (voir comportement B11).
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "REFCDE"
 *      7   : espace
 *   8- 13  : DATCDE       X(6)   date commande YYMMDD — ficDatecde.substring(2,8)
 *     14   : espace
 *  15- 36  : CDELAB       X(22)  D.CDE.CDELAB
 *     37   : espace
 *  38- 72  : REFCDE       X(35)  D.CDE.REFCDE  (hôte :w-refcde)
 *     73   : espace
 *  74- 75  : TYPCDE       X(2)   D.CDE.TYPCDE
 *     76   : espace
 *  77- 82  : CLICSP       9(6)   D.CDE.CLICSP  (zéros à gauche)
 *     83   : espace
 *  84-118  : NOMLIV       X(35)  astuce CLICSP décalé — voir §8
 *    119   : espace  (ENR-REFCDE-FILLER)
 * 120-122  : CODOPE       X(3)   D.CDE.CODOPE
 *    123   : espace
 * 124-133  : NUMDOC       X(10)  P.DOCENT.NUMDOC ou 10 espaces
 *    134   : espace
 *    135   : CDESAISIE    X      D.CDE.CDESAISIE
 *    136   : espace
 * 137-159  : DATREC       X(23)  D.CDE.DATREC ASCII VMS complet
 *    160   : espace
 *    161   : TRAFIC       X      D.CDE.TRAFIC ou 'N' si vide
 *    162   : espace
 * 163-170  : DATEBP       X(8)   D.CDE.DATEBP YYYYMMDD (date du jour si NULL)
 *    171   : espace
 * 172-178  : CODSAI       X(7)   D.CDE.CODSAI
 *    179   : espace
 * 180-183  : CODSTR       X(4)   D.CDE.CODSTR
 *    184   : espace
 * 185-188  : STREXP       X(4)   D.CDE.STREXP
 *    189   : espace
 * 190-197  : CODREP       X(8)   D.CDE.CODREP
 */
public final class RefcdeFormatter implements TransmissionRecord {

    /** Date commande YYMMDD (6 chars), pré-calculé par ficDatecde.substring(2,8). */
    private final String datcde;
    /** D.CDE.CDELAB (22 chars max). */
    private final String cdelab;
    /** D.CDE.REFCDE — hôte :w-refcde (35 chars max). */
    private final String refcde;
    /** D.CDE.TYPCDE (2 chars). */
    private final String typcde;
    /** D.CDE.CLICSP — valeur numérique (formattée sur 6 chiffres + astuce NOMLIV). */
    private final long   clicsp;
    /** D.CDE.CODOPE (3 chars). */
    private final String codope;
    /** P.DOCENT.NUMDOC (10 chars) ou null/vide si absent. */
    private final String numdoc;
    /** D.CDE.CDESAISIE (1 char). */
    private final String cdesaisie;
    /** D.CDE.DATREC en ASCII VMS complet 23 chars (ou 23 espaces si NULL en CURCDE_R). */
    private final String datrec;
    /** D.CDE.TRAFIC (1 char) ou null/vide → sera remplacé par 'N'. */
    private final String trafic;
    /** D.CDE.DATEBP YYYYMMDD (8 chars), date du jour si IDATEBP = -1. */
    private final String datebp;
    /** D.CDE.CODSAI (7 chars). */
    private final String codsai;
    /** D.CDE.CODSTR (4 chars). */
    private final String codstr;
    /** D.CDE.STREXP (4 chars). */
    private final String strexp;
    /** D.CDE.CODREP (8 chars). */
    private final String codrep;

    public RefcdeFormatter(
            String datcde,
            String cdelab,
            String refcde,
            String typcde,
            long   clicsp,
            String codope,
            String numdoc,
            String cdesaisie,
            String datrec,
            String trafic,
            String datebp,
            String codsai,
            String codstr,
            String strexp,
            String codrep) {
        this.datcde    = datcde;
        this.cdelab    = cdelab;
        this.refcde    = refcde;
        this.typcde    = typcde;
        this.clicsp    = clicsp;
        this.codope    = codope;
        this.numdoc    = numdoc;
        this.cdesaisie = cdesaisie;
        this.datrec    = datrec;
        this.trafic    = trafic;
        this.datebp    = datebp;
        this.codsai    = codsai;
        this.codstr    = codstr;
        this.strexp    = strexp;
        this.codrep    = codrep;
    }

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        // ENR-TYPMES pos 1-6 (index 0-5)
        FixedField.writeAlpha(rec, 0, "REFCDE", 6);
        // index 6 : espace

        // Corps (index 7-196)
        FixedField.writeAlpha  (rec,   7, datcde,          6); // pos  8-13
        // index 13 : espace
        FixedField.writeAlpha  (rec,  14, cdelab,         22); // pos 15-36
        // index 36 : espace
        FixedField.writeAlpha  (rec,  37, refcde,         35); // pos 38-72
        // index 72 : espace
        FixedField.writeAlpha  (rec,  73, typcde,          2); // pos 74-75
        // index 75 : espace
        FixedField.writeNumeric(rec,  76, clicsp,          6); // pos 77-82
        // index 82 : espace
        FixedField.writeAlpha  (rec,  83, buildNomliv(),  35); // pos 84-118
        // index 118 : espace (ENR-REFCDE-FILLER)
        FixedField.writeAlpha  (rec, 119, codope,          3); // pos 120-122
        // index 122 : espace
        FixedField.writeAlpha  (rec, 123, numdoc,         10); // pos 124-133
        // index 133 : espace
        FixedField.writeAlpha  (rec, 134, cdesaisie,       1); // pos 135
        // index 135 : espace
        FixedField.writeAlpha  (rec, 136, datrec,         23); // pos 137-159
        // index 159 : espace
        rec[160] = resolveTrafic();                             // pos 161
        // index 161 : espace
        FixedField.writeAlpha  (rec, 162, datebp,          8); // pos 163-170
        // index 170 : espace
        FixedField.writeAlpha  (rec, 171, codsai,          7); // pos 172-178
        // index 178 : espace
        FixedField.writeAlpha  (rec, 179, codstr,          4); // pos 180-183
        // index 183 : espace
        FixedField.writeAlpha  (rec, 184, strexp,          4); // pos 185-188
        // index 188 : espace
        FixedField.writeAlpha  (rec, 189, codrep,          8); // pos 190-197

        assert new String(rec).length() == 197;
        return new String(rec);
    }

    /**
     * Astuce NOMLIV (§8 du guide) :
     *   MOVE CLICSP TO NOMLIV → "000123" + 29 espaces (35 chars)
     *   MOVE NOMLIV(4:6) TO NOMLIV → sous-chaîne COBOL position 4, longueur 6
     *   Java : padded.substring(3, 9) → "123   " puis padder à 35.
     */
    private String buildNomliv() {
        String clicsp6  = String.format("%06d", clicsp);          // "000123"
        String padded35 = String.format("%-35s", clicsp6);         // "000123" + 29 espaces
        String sub6     = padded35.substring(3, 9);                // "123   "
        return String.format("%-35s", sub6);                       // "123   " + 29 espaces
    }

    /**
     * Si TRAFIC est null, vide ou espace → retourner 'N' (règle COBOL ligne ~1645).
     * Sinon retourner le premier caractère de la valeur DB.
     */
    private char resolveTrafic() {
        if (trafic == null || trafic.trim().isEmpty()) return 'N';
        return trafic.charAt(0);
    }
}
