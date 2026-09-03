      *================================================================*
      * COPYBOOK : GSTKCPY.CPY                                         *
      * DESCRIPTION : ZONES COMMUNES A TOUS LES PROGRAMMES GSTK        *
      *================================================================*

      *----------------------------------------------------------------*
      * COMMAREA PARTAGEE ENTRE TOUS LES PROGRAMMES                    *
      *----------------------------------------------------------------*
       01  GSTK-COMMAREA.
           05  CA-TRAN-RETOUR       PIC X(8).
           05  CA-OPERATEUR         PIC X(10).
           05  CA-SESSION-ID        PIC X(25).
           05  CA-DATE-SAISIE       PIC X(10).
           05  CA-HEURE-SAISIE      PIC X(08).
           05  CA-TERMINAL          PIC X(10).
           05  CA-PROFIL            PIC X(15).
           05  CA-MSG-RETOUR        PIC X(79).
           05  CA-ART-CODE-SELEC    PIC X(10).
           05  CA-FILTRE-CAT        PIC X(15).
           05  CA-FILTRE-STATUT     PIC X(10).
           05  CA-PAGE-COURANTE     PIC 9(4).
           05  CA-NB-PAGES          PIC 9(4).
           05  FILLER               PIC X(55).

      *----------------------------------------------------------------*
      * ENREGISTREMENT ARTICLE (COMMUN)                                *
      *----------------------------------------------------------------*
       01  GSTK-ART-RECORD.
           05  ART-CODE             PIC X(10).
           05  ART-DESIGNATION      PIC X(50).
           05  ART-DESCRIPTION      PIC X(80).
           05  ART-CATEGORIE        PIC X(15).
           05  ART-SOUS-CAT         PIC X(15).
           05  ART-UNITE            PIC X(10).
           05  ART-QTE-STOCK        PIC 9(10)V999.
           05  ART-QTE-MIN          PIC 9(10)V999.
           05  ART-QTE-MAX          PIC 9(10)V999.
           05  ART-PRIX-ACHAT       PIC 9(10)V9999.
           05  ART-PRIX-VENTE       PIC 9(10)V9999.
           05  ART-TVA-TAUX         PIC 9(3)V99.
           05  ART-EMPLACEMENT      PIC X(15).
           05  ART-FRN-CODE         PIC X(10).
           05  ART-FRN-NOM          PIC X(40).
           05  ART-STATUT           PIC X(10).
           05  ART-DATE-CREATION    PIC X(10).
           05  ART-DATE-MAJ         PIC X(10).
           05  ART-OPERATEUR        PIC X(10).
           05  ART-CODE-BARRE       PIC X(13).
           05  ART-DELAI-APPRO      PIC 9(3).
           05  FILLER               PIC X(7).

      *----------------------------------------------------------------*
      * ENREGISTREMENT MOUVEMENT (COMMUN)                              *
      *----------------------------------------------------------------*
       01  GSTK-MVT-RECORD.
           05  MVT-ID               PIC 9(12).
           05  MVT-DATE             PIC X(10).
           05  MVT-HEURE            PIC X(8).
           05  MVT-TYPE             PIC X(10).
           05  MVT-SENS             PIC X(1).
           05  MVT-ART-CODE         PIC X(10).
           05  MVT-DESIGNATION      PIC X(50).
           05  MVT-QUANTITE         PIC 9(10)V999.
           05  MVT-PRIX-UNIT        PIC 9(10)V9999.
           05  MVT-MONTANT-HT       PIC 9(12)V99.
           05  MVT-STOCK-AVANT      PIC 9(10)V999.
           05  MVT-STOCK-APRES      PIC 9(10)V999.
           05  MVT-NUM-BON          PIC X(12).
           05  MVT-NUM-LOT          PIC X(30).
           05  MVT-EMPLACEMENT      PIC X(15).
           05  MVT-TIERS            PIC X(60).
           05  MVT-CENTRE-COUT      PIC X(15).
           05  MVT-OPERATEUR        PIC X(10).
           05  MVT-POSTE            PIC X(10).
           05  MVT-COMMENTAIRE      PIC X(80).
           05  FILLER               PIC X(5).

      *----------------------------------------------------------------*
      * ENREGISTREMENT BON ENTREE (COMMUN)                             *
      *----------------------------------------------------------------*
       01  GSTK-BEN-RECORD.
           05  BEN-NUMERO           PIC X(12).
           05  BEN-DATE             PIC X(10).
           05  BEN-HEURE            PIC X(8).
           05  BEN-FRN-CODE         PIC X(10).
           05  BEN-FOURNISSEUR      PIC X(60).
           05  BEN-REF-FRN          PIC X(30).
           05  BEN-NUM-CMD          PIC X(20).
           05  BEN-NB-LIGNES        PIC 9(4).
           05  BEN-MONTANT-HT       PIC 9(12)V99.
           05  BEN-MONTANT-TTC      PIC 9(12)V99.
           05  BEN-STATUT           PIC X(15).
           05  BEN-COMMENTAIRE      PIC X(80).
           05  BEN-OPERATEUR        PIC X(10).
           05  BEN-POSTE            PIC X(10).
           05  FILLER               PIC X(49).

      *----------------------------------------------------------------*
      * ENREGISTREMENT BON SORTIE (COMMUN)                             *
      *----------------------------------------------------------------*
       01  GSTK-BSO-RECORD.
           05  BSO-NUMERO           PIC X(12).
           05  BSO-DATE             PIC X(10).
           05  BSO-HEURE            PIC X(8).
           05  BSO-DEMANDEUR        PIC X(60).
           05  BSO-CENTRE-COUT      PIC X(15).
           05  BSO-NUM-CMD          PIC X(20).
           05  BSO-MOTIF            PIC X(20).
           05  BSO-NB-LIGNES        PIC 9(4).
           05  BSO-MONTANT-HT       PIC 9(12)V99.
           05  BSO-MONTANT-TTC      PIC 9(12)V99.
           05  BSO-STATUT           PIC X(15).
           05  BSO-COMMENTAIRE      PIC X(80).
           05  BSO-OPERATEUR        PIC X(10).
           05  BSO-POSTE            PIC X(10).
           05  FILLER               PIC X(35).

      *================================================================*
      * FIN COPYBOOK GSTKCPY.CPY
      *================================================================*