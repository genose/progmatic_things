#!/usr/bin/env bash
# ============================================================
# conf/gstk_cics_tests.sh — Suites de tests CICS GSTK
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Sourcé par 08_test_cics.sh quand PROJECT_NAME=gstk.
# Définit run_project_smoke() et run_project_tests().
#
# Prérequis : les fonctions assert_screen, assert_no_error,
# section, pass, fail doivent déjà être définies.
# ============================================================

# ============================================================
# SUITE : Tests smoke (rapides — G000 + G001 uniquement)
# ============================================================
run_project_smoke() {
    section "SMOKE TESTS (G000 + G001)"

    assert_screen "G000 - Menu principal s'affiche" "G000" \
        "GSTK000" "MENU"

    assert_no_error "G000 - Pas d'erreur CICS" "G000"

    assert_screen "G001 - Consultation stock s'affiche" "G001" \
        "GSTK001" "CODE ART" "DATE"

    assert_no_error "G001 - Pas d'erreur CICS" "G001"
}

# ============================================================
# SUITE : Tests complets de chaque transaction
# ============================================================
run_project_tests() {
    section "G000 — Menu principal"
    assert_screen "G000 - titre affiché"       "G000" "GSTK000" "MENU"
    assert_screen "G000 - options du menu"      "G000" "G001" "G002" "G003"
    assert_no_error "G000 - pas d'erreur CICS" "G000"

    section "G001 — Consultation stock"
    assert_screen "G001 - en-tête affiché"     "G001" "GSTK001" "CODE ART"
    assert_screen "G001 - colonnes présentes"  "G001" "STOCK" "DESIGNATION" "CATEGORIE"
    assert_screen "G001 - pagination"          "G001" "PAGE"
    assert_no_error "G001 - pas d'erreur"      "G001"

    section "G002 — Entrée marchandise"
    assert_screen "G002 - en-tête affiché"     "G002" "GSTK002" "ENTREE"
    assert_screen "G002 - champs de saisie"    "G002" "CODE ART" "QUANTITE"
    assert_screen "G002 - instructions PF"     "G002" "PF5" "PF6"
    assert_no_error "G002 - pas d'erreur"      "G002"

    section "G003 — Sortie marchandise"
    assert_screen "G003 - en-tête affiché"     "G003" "GSTK003" "SORTIE"
    assert_screen "G003 - champs de saisie"    "G003" "CODE ART" "QUANTITE"
    assert_no_error "G003 - pas d'erreur"      "G003"

    section "G004 — Gestion articles"
    assert_screen "G004 - en-tête affiché"     "G004" "GSTK004"
    assert_screen "G004 - champs article"      "G004" "CODE" "DESIGNATION"
    assert_no_error "G004 - pas d'erreur"      "G004"

    section "G005 — Rapports stock"
    assert_screen "G005 - en-tête affiché"     "G005" "GSTK005" "RAPPORT"
    assert_screen "G005 - stats globales"      "G005" "CATEGORIE"
    assert_no_error "G005 - pas d'erreur"      "G005"

    section "G006 — Alertes stock critique"
    assert_screen "G006 - en-tête affiché"     "G006" "GSTK006" "ALERTE"
    assert_screen "G006 - colonnes alertes"    "G006" "CODE ART" "STOCK" "MINIMUM"
    assert_no_error "G006 - pas d'erreur"      "G006"

    section "G007 — Historique mouvements"
    assert_screen "G007 - en-tête affiché"     "G007" "GSTK007" "HISTORIQUE"
    assert_screen "G007 - colonnes historique" "G007" "DATE" "TYPE" "QUANTITE"
    assert_screen "G007 - filtres disponibles" "G007" "PF5" "PF7" "PF8"
    assert_no_error "G007 - pas d'erreur"      "G007"
}
