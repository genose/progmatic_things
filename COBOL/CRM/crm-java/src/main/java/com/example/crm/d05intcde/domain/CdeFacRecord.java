package com.example.crm.d05intcde.domain;

import java.math.BigDecimal;

/**
 * Enregistrement lu depuis FIC-CDEFAC (RMS_CDEFAC).
 * Tous les champs correspondent directement aux champs COBOL ENR-CDEFAC.
 * Les dates sont conservees en format VMS ASCII "DD-MON-YYYY HH:MM:SS.CC".
 */
public final class CdeFacRecord {
    /* --- cle --- */
    public final String codlab;     // ENR-CODLAB  PIC X(4)
    public final String coddep;     // ENR-CODDEP  PIC X(2)
    public final String sslabo;     // ENR-SSLABO  PIC X(4)
    public final long   numcde;     // ENR-NUMCDE  PIC 9(8)
    public final int    numral;     // ENR-NUMRAL  PIC 9

    /* --- reglement --- */
    public final String moyrgl;     // ENR-MOYRGL  PIC X(2)
    public final String delrgl;     // ENR-DELRGL  PIC X(3)
    public final String codrep;     // ENR-CODREP  PIC X(8)

    /* --- dates (format VMS ASCII) --- */
    public final String datcde;     // ENR-DATCDE  PIC X(23)
    public final String datebl;     // ENR-DATEBL  PIC X(23)
    public final String datsai;     // ENR-DATSAI  PIC X(23)
    public final String datfac;     // ENR-DATFAC  PIC X(23)
    public final String datech;     // ENR-DATECH  PIC X(23)
    public final String dtlivs;     // ENR-DTLIVS  PIC X(23)
    public final String dtlivr;     // ENR-DTLIVR  PIC X(23)

    /* --- montants --- */
    public final BigDecimal escomp;   // ENR-ESCOMP  PIC 9(2)V9(4)
    public final BigDecimal frgest1;  // ENR-FRGEST1 PIC S9(6)V9(2)
    public final BigDecimal remisee;  // ENR-REMISEE PIC 9(6)V9(4)
    public final BigDecimal mtht;     // ENR-MTHT    PIC S9(8)V9(2)
    public final BigDecimal poids;    // ENR-POIDS   PIC 9(7)V9(4)

    /* --- flags --- */
    public final String flagcde;    // ENR-FLAGCDE    PIC X
    public final String flagliv;    // ENR-FLAGLIV    PIC X
    public final String flagfac;    // ENR-FLAGFAC    PIC X
    public final String flagblo;    // ENR-FLAGBLO    PIC X
    public final String flagsup;    // ENR-FLAGSUP    PIC X
    public final String motifsup;   // ENR-MOTIFSUP   PIC XXX
    public final String flagdif;    // ENR-FLAGDIF    PIC X
    public final String flagral;    // ENR-FLAGRAL    PIC X
    public final String flagstd;    // ENR-FLAGSTD    PIC X
    public final String flagdet;    // ENR-FLAGDET    PIC X
    public final String flagocc;    // ENR-FLAGOCC    PIC X
    public final String flagencours;// ENR-FLAGENCOURS PIC X
    public final String flagexp;    // ENR-FLAGEXP    PIC X

    /* --- client livraison --- */
    public final String gencli;     // ENR-GENCLI     PIC X
    public final String libgencli;  // ENR-LIBGENCLI  PIC X(40)
    public final String clilab;     // ENR-CLILAB     PIC X(10)
    public final long   clicsp;     // ENR-CLICSP     PIC 9(8)
    public final String nomliv;     // ENR-NOMLIV     PIC X(40)
    public final String raisocl;    // ENR-RAISOCL    PIC X(40)
    public final String adr1l;      // ENR-ADR1L      PIC X(40)
    public final String adr2l;      // ENR-ADR2L      PIC X(40)
    public final String villel;     // ENR-VILLEL     PIC X(32)
    public final String deparl;     // ENR-DEPARL     PIC XX
    public final String cpostl;     // ENR-CPOSTL     PIC X(5)
    public final String uga;        // ENR-UGA        PIC X(3)
    public final String uga746;     // ENR-UGA746     PIC X(5)

    /* --- client facturation --- */
    public final long   clifac;     // ENR-CLIFAC     PIC 9(8)
    public final String nomfac;     // ENR-NOMFAC     PIC X(40)
    public final String raisocf;    // ENR-RAISOCF    PIC X(40)
    public final String adr1f;      // ENR-ADR1F      PIC X(40)
    public final String adr2f;      // ENR-ADR2F      PIC X(40)
    public final String villef;     // ENR-VILLEF     PIC X(32)
    public final String deparf;     // ENR-DEPARF     PIC X(2)
    public final String cpostf;     // ENR-CPOSTF     PIC X(5)

    /* --- client payeur --- */
    public final long   clipay;     // ENR-CLIPAY     PIC 9(8)
    public final String nompay;     // ENR-NOMPAY     PIC X(40)
    public final String raispay;    // ENR-RAISPAY    PIC X(40)

    /* --- client groupe --- */
    public final long   cligrp;     // ENR-CLIGRP     PIC 9(8)
    public final String nomgrp;     // ENR-NOMGRP     PIC X(40)
    public final String raisgrp;    // ENR-RAISGRP    PIC X(40)

    /* --- facturation --- */
    public final String typnfa;     // ENR-TYPNFA     PIC X(2)
    public final String quanta;     // ENR-QUANTA     PIC X
    public final String moisfa;     // ENR-MOISFA     PIC XX
    public final long   cptfac;     // ENR-CPTFAC     PIC 9(8)
    public final String codrepn;    // ENR-CODREPN    PIC X(8)
    public final String codrepr;    // ENR-CODREPR    PIC X(8)
    public final String regfac;     // ENR-REGFAC     PIC X
    public final String nomrep;     // ENR-NOMREP     PIC X(40)
    public final String nomrepn;    // ENR-NOMREPN    PIC X(40)
    public final String nomrepr;    // ENR-NOMREPR    PIC X(40)

    /* --- statut en cours --- */
    public final String flagencoursSrc; // alias pour lisibilite
    public final long   qtencours;  // ENR-QTENCOURS  PIC S9(8)
    public final String statencours;// ENR-STATENCOURS PIC X(3)

    /* --- autres --- */
    public final long   cippdv;     // ENR-CIPPDV     PIC 9(9)
    public final String genclifac;  // ENR-GENCLIFAC  PIC X
    public final String libgenclifac; // ENR-LIBGENCLIFAC PIC X(40)
    public final String codrepc;    // ENR-CODREPC    PIC X(5)
    public final String nomrepc;    // ENR-NOMREPC    PIC X(80)
    public final String cdelab;     // ENR-CDELAB     PIC X(22)
    public final String refcde;     // ENR-REFCDE     PIC X(35)
    public final String dosexp;     // ENR-DOSEXP     PIC X(10)
    public final long   volstd;     // ENR-VOLSTD     PIC 9(8)
    public final long   voldtl;     // ENR-VOLDTL     PIC 9(8)
    public final String libpays;    // ENR-LIBPAYS    PIC X(80)
    public final String promos;     // ENR-PROMOS     PIC X(4)
    public final long   nbcolis;    // ENR-NBCOLIS    PIC 9(6)
    public final long   nbpal;      // ENR-NBPAL      PIC 9(6)

    public CdeFacRecord(Builder b) {
        this.codlab       = b.codlab;
        this.coddep       = b.coddep;
        this.sslabo       = b.sslabo;
        this.numcde       = b.numcde;
        this.numral       = b.numral;
        this.moyrgl       = b.moyrgl;
        this.delrgl       = b.delrgl;
        this.codrep       = b.codrep;
        this.datcde       = b.datcde;
        this.datebl       = b.datebl;
        this.datsai       = b.datsai;
        this.datfac       = b.datfac;
        this.datech       = b.datech;
        this.dtlivs       = b.dtlivs;
        this.dtlivr       = b.dtlivr;
        this.escomp       = b.escomp;
        this.frgest1      = b.frgest1;
        this.remisee      = b.remisee;
        this.mtht         = b.mtht;
        this.poids        = b.poids;
        this.flagcde      = b.flagcde;
        this.flagliv      = b.flagliv;
        this.flagfac      = b.flagfac;
        this.flagblo      = b.flagblo;
        this.flagsup      = b.flagsup;
        this.motifsup     = b.motifsup;
        this.flagdif      = b.flagdif;
        this.flagral      = b.flagral;
        this.flagstd      = b.flagstd;
        this.flagdet      = b.flagdet;
        this.flagocc      = b.flagocc;
        this.flagencours  = b.flagencours;
        this.flagencoursSrc = b.flagencours;
        this.flagexp      = b.flagexp;
        this.gencli       = b.gencli;
        this.libgencli    = b.libgencli;
        this.clilab       = b.clilab;
        this.clicsp       = b.clicsp;
        this.nomliv       = b.nomliv;
        this.raisocl      = b.raisocl;
        this.adr1l        = b.adr1l;
        this.adr2l        = b.adr2l;
        this.villel       = b.villel;
        this.deparl       = b.deparl;
        this.cpostl       = b.cpostl;
        this.uga          = b.uga;
        this.uga746       = b.uga746;
        this.clifac       = b.clifac;
        this.nomfac       = b.nomfac;
        this.raisocf      = b.raisocf;
        this.adr1f        = b.adr1f;
        this.adr2f        = b.adr2f;
        this.villef       = b.villef;
        this.deparf       = b.deparf;
        this.cpostf       = b.cpostf;
        this.clipay       = b.clipay;
        this.nompay       = b.nompay;
        this.raispay      = b.raispay;
        this.cligrp       = b.cligrp;
        this.nomgrp       = b.nomgrp;
        this.raisgrp      = b.raisgrp;
        this.typnfa       = b.typnfa;
        this.quanta       = b.quanta;
        this.moisfa       = b.moisfa;
        this.cptfac       = b.cptfac;
        this.codrepn      = b.codrepn;
        this.codrepr      = b.codrepr;
        this.regfac       = b.regfac;
        this.nomrep       = b.nomrep;
        this.nomrepn      = b.nomrepn;
        this.nomrepr      = b.nomrepr;
        this.qtencours    = b.qtencours;
        this.statencours  = b.statencours;
        this.cippdv       = b.cippdv;
        this.genclifac    = b.genclifac;
        this.libgenclifac = b.libgenclifac;
        this.codrepc      = b.codrepc;
        this.nomrepc      = b.nomrepc;
        this.cdelab       = b.cdelab;
        this.refcde       = b.refcde;
        this.dosexp       = b.dosexp;
        this.volstd       = b.volstd;
        this.voldtl       = b.voldtl;
        this.libpays      = b.libpays;
        this.promos       = b.promos;
        this.nbcolis      = b.nbcolis;
        this.nbpal        = b.nbpal;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        public String codlab = ""; public String coddep = ""; public String sslabo = "";
        public long   numcde = 0;  public int    numral = 0;
        public String moyrgl = ""; public String delrgl = ""; public String codrep = "";
        public String datcde = ""; public String datebl = ""; public String datsai = "";
        public String datfac = ""; public String datech = ""; public String dtlivs = "";
        public String dtlivr = "";
        public BigDecimal escomp = BigDecimal.ZERO; public BigDecimal frgest1 = BigDecimal.ZERO;
        public BigDecimal remisee = BigDecimal.ZERO; public BigDecimal mtht = BigDecimal.ZERO;
        public BigDecimal poids = BigDecimal.ZERO;
        public String flagcde = ""; public String flagliv = ""; public String flagfac = "";
        public String flagblo = ""; public String flagsup = ""; public String motifsup = "";
        public String flagdif = ""; public String flagral = ""; public String flagstd = "";
        public String flagdet = ""; public String flagocc = ""; public String flagencours = "";
        public String flagexp = "";
        public String gencli = ""; public String libgencli = ""; public String clilab = "";
        public long   clicsp = 0;  public String nomliv = ""; public String raisocl = "";
        public String adr1l = ""; public String adr2l = ""; public String villel = "";
        public String deparl = ""; public String cpostl = ""; public String uga = "";
        public String uga746 = "";
        public long   clifac = 0; public String nomfac = ""; public String raisocf = "";
        public String adr1f = ""; public String adr2f = ""; public String villef = "";
        public String deparf = ""; public String cpostf = "";
        public long   clipay = 0; public String nompay = ""; public String raispay = "";
        public long   cligrp = 0; public String nomgrp = ""; public String raisgrp = "";
        public String typnfa = ""; public String quanta = ""; public String moisfa = "";
        public long   cptfac = 0; public String codrepn = ""; public String codrepr = "";
        public String regfac = ""; public String nomrep = ""; public String nomrepn = "";
        public String nomrepr = "";
        public long   qtencours = 0; public String statencours = "";
        public long   cippdv = 0; public String genclifac = ""; public String libgenclifac = "";
        public String codrepc = ""; public String nomrepc = ""; public String cdelab = "";
        public String refcde = ""; public String dosexp = ""; public long volstd = 0;
        public long   voldtl = 0; public String libpays = ""; public String promos = "";
        public long   nbcolis = 0; public long nbpal = 0;

        public CdeFacRecord build() { return new CdeFacRecord(this); }
    }
}
