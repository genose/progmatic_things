# COBOL 2026

![Platform macOS](https://img.shields.io/badge/platform-macOS-black)
![GnuCOBOL Ready](https://img.shields.io/badge/GnuCOBOL-ready-success)

Collection de programmes COBOL indépendants.

## COBOL Development Setup

Voir le guide complet : [COBOL_VSCODE_SETUP.md](./COBOL_VSCODE_SETUP.md)

Ce guide couvre :

- configuration GnuCOBOL sur macOS
- intégration VS Code Run/Build
- compilation dans `bin/`
- exécution et dépannage

## Exemples de commandes

### Compiler et exécuter COBOL_22

```bash
source ./setup_cobol_env.sh
mkdir -p ./bin
/usr/local/bin/cobc -x -o ./bin/COBOL_22 ./COBOL_22.cbl
./bin/COBOL_22
```

### Compiler et exécuter cobol_8 (programme interactif)

```bash
source ./setup_cobol_env.sh
mkdir -p ./bin
/usr/local/bin/cobc -x -o ./bin/cobol_8 ./cobol_8.cbl
./bin/cobol_8
```

## Known issues

- `cobol_8.cbl` compile avec des warnings non bloquants sur le typage alphanumérique (observé autour de la ligne 94).
- La compilation globale de tous les fichiers (`COBOL: Compile all files`) échoue actuellement sur certains sources avec erreurs de code COBOL existantes, notamment `COBOL_12.cbl` et `COBOL_13.cbl`.
- Certains programmes nécessitent des fichiers d'entrée au runtime. Exemple : `COBOL_22.cbl` attend un fichier `VENTES-LOGIQUE` dans le dossier de travail.
