# Extraction de mapping COBOL / Oracle Rdb

`extract_mapping.py` genere un dictionnaire de donnees a partir :

- des clauses `PIC` d'un programme ou copybook COBOL ;
- des commentaires de colonnes contenus dans un extrait DDL Oracle Rdb ;
- des tables indiquees par ordre de priorite.

Le resultat peut etre affiche en CSV ASCII ou ecrit dans un classeur XLSX existant.

## Prerequis

- Python 3 ;
- `openpyxl` uniquement pour la sortie XLSX.

Sur ce poste, utiliser le wrapper qui selectionne l'interpreteur contenant `openpyxl` :

```bash
./extract_mapping.sh --help
```

## Conversion des PIC

| PIC COBOL                          | Format produit   | Taille |
| ---------------------------------- | ---------------- | -----: |
| `PIC X`                            | `CHAR`           |      1 |
| `PIC X(n)`                         | `CHAR`           |      n |
| `PIC X(23)`                        | `DATE VMS`       |   vide |
| `PIC 9(n)` / `PIC S9(n)`           | `BIGINT`         |   vide |
| `PIC 9(a)V9(b)` / `PIC S9(a)V9(b)` | `DECIMAL(a+b,b)` |   vide |

`PIC X(23)` est considere comme une date VMS selon la convention de ce projet.

## Sections COBOL

L'option `--section` choisit la section analysee :

- `working-storage` : `WORKING-STORAGE SECTION` uniquement, valeur par defaut ;
- `linkage` : `LINKAGE SECTION` uniquement ;
- `all` : les deux sections, dans leur ordre d'apparition.

Les champs `FILLER` sont ignores.

## Sortie ASCII

Exemple avec le groupe `ENR-LINCDE-` :

```bash
./extract_mapping.sh \
  --cob D02_EXTCDE_CRMCSP1.COB \
  --ddl TAB_DEPOT.TXT \
  --table CDE --table CDL --table MES --table ART \
  --prefix ENR-LINCDE-
```

Sortie CSV separee par des points-virgules :

```text
N;Nom;Format;Taille;Commentaire;Exemple
1;CODART;CHAR;10;(CDL) code article csp;ABCDEFGHIJ
2;QTCCDE;BIGINT;;(CDL) qte commandee;12345678
3;QTCGRT;BIGINT;;(CDL) qte gratuite;12345678
4;LIBELL;CHAR;35;(CDL) libelle produit;ABCDEFGHIJ0123456789ABCDEFGHIJ01234
5;CODLABLAB;CHAR;4;;ABCD
```

Pour enregistrer cette sortie :

```bash
./extract_mapping.sh [options] > mapping.csv
```

## Exemple LINKAGE SECTION

```bash
./extract_mapping.sh \
  --cob PROGRAMME.COB \
  --ddl TAB_DEPOT.TXT \
  --table CDE --table CDL \
  --section linkage \
  --prefix LK-COMMANDE-
```

Pour analyser `WORKING-STORAGE` et `LINKAGE` :

```bash
./extract_mapping.sh [options] --section all
```

## Sortie XLSX

Le classeur doit deja exister. Le script ecrit six colonnes consecutives :

`N°`, `Nom`, `Format`, `Taille`, `Commentaire`, `Exemple`.

```bash
./extract_mapping.sh \
  --cob D02_EXTCDE_CRMCSP1.COB \
  --ddl TAB_DEPOT.TXT \
  --table CDE --table CDL --table MES --table ART \
  --prefix ENR-REFCDE- \
  --xlsx Mapping_D02_EXTCDE_CRMCSP1.xlsx \
  --sheet-col Q \
  --sheet-header-row 2 \
  --sheet-start-row 3
```

Options de positionnement :

| Option               | Defaut | Description               |
| -------------------- | -----: | ------------------------- |
| `--sheet-col`        |    `Q` | Premiere colonne du bloc  |
| `--sheet-header-row` |    `2` | Ligne des en-tetes        |
| `--sheet-start-row`  |    `3` | Premiere ligne de donnees |

## Options principales

| Option      | Obligatoire | Description                                    |
| ----------- | ----------: | ---------------------------------------------- |
| `--cob`     |         oui | Programme ou copybook COBOL                    |
| `--ddl`     |         oui | Extrait DDL Rdb, option repetable              |
| `--table`   |         oui | Table recherchee, option repetable et ordonnee |
| `--prefix`  |         non | Prefixe COBOL filtre puis retire du nom        |
| `--section` |         non | `working-storage`, `linkage` ou `all`          |
| `--xlsx`    |         non | Classeur existant a mettre a jour              |

## Regles de correspondance

Pour chaque champ COBOL :

1. le prefixe demande est retire ;
2. le nom obtenu est recherche dans les tables DDL ;
3. la premiere table correspondante fournit le commentaire ;
4. le commentaire produit prend la forme `(TABLE) commentaire`.

L'ordre des options `--table` est donc significatif.

## Limites

- Le parseur traite les clauses `PIC` ecrites sur une seule ligne.
- Les clauses `COPY` ne sont pas resolues automatiquement.
- Les champs sans commentaire DDL conservent un commentaire vide.
- La sortie XLSX ecrit dans la feuille active du classeur.
- Le script ne supprime pas les anciennes lignes situees apres les donnees ecrites.
