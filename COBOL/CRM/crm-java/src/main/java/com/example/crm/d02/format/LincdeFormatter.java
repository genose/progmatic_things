package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement LINCDE — une ligne de commande (entrée CODENR='3' de TABLE-CDL).
 *
 * Attention — noms COBOL trompeurs :
 *   ENR-LINCDE-QTCCDE reçoit TCDL-QTELIV (quantité livrée), pas la quantité commandée.
 *   ENR-LINCDE-LIBELL est TOUJOURS forcé à 35 espaces — jamais D.ART.LIBELL.
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "LINCDE"
 *      7   : espace
 *   8- 17  : CODART     X(10)  LigneCumulee.codart  ← D.CDL.CODART
 *     18   : espace
 *  19- 25  : QTCCDE     9(7)   TCDL-QTELIV (somme D.CDL.QTLCDE) — nom trompeur !
 *     26   : espace
 *  27- 33  : QTCGRT     9(7)   somme D.CDL.QTCGRT + QTCECH
 *     34   : espace
 *  35- 69  : LIBELL     X(35)  TOUJOURS 35 espaces (jamais ART.LIBELL)
 *     70   : espace
 *  71- 74  : CODLABLAB  X(4)   LigneCumulee.codlablab  ← D.CDL.CODLAB
 *  75-112  : FILLER     X(38)  espaces
 * 113-197  : non adressé — espaces
 */
public final class LincdeFormatter implements TransmissionRecord {

    /** D.CDL.CODART (10 chars max). */
    private final String codart;
    /**
     * Quantité livrée — somme D.CDL.QTLCDE.
     * Malgré le nom COBOL QTCCDE, ce champ contient bien la quantité livrée (B — T26).
     */
    private final long   qteliv;
    /** Somme D.CDL.QTCGRT + D.CDL.QTCECH. */
    private final long   qtcgrt;
    /** D.CDL.CODLAB (4 chars). */
    private final String codlablab;

    public LincdeFormatter(String codart, long qteliv, long qtcgrt, String codlablab) {
        this.codart    = codart;
        this.qteliv    = qteliv;
        this.qtcgrt    = qtcgrt;
        this.codlablab = codlablab;
    }

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        FixedField.writeAlpha  (rec,  0, "LINCDE",   6); // pos  1- 6
        // index 6 : espace

        FixedField.writeAlpha  (rec,  7, codart,    10); // pos  8-17
        // index 17 : espace
        FixedField.writeNumeric(rec, 18, qteliv,     7); // pos 19-25  (QTCCDE reçoit QTELIV)
        // index 25 : espace
        FixedField.writeNumeric(rec, 26, qtcgrt,     7); // pos 27-33
        // index 33 : espace
        // LIBELL pos 35-69 (index 34-68) : 35 espaces — déjà fait par Arrays.fill
        // index 69 : espace
        FixedField.writeAlpha  (rec, 70, codlablab,  4); // pos 71-74
        // FILLER pos 75-112 (index 74-111) : 38 espaces
        // non adressé pos 113-197 : espaces

        assert new String(rec).length() == 197;
        return new String(rec);
    }
}
