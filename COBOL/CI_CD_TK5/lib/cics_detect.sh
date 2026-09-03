#!/usr/bin/env bash
# ============================================================
# lib/cics_detect.sh — Détection du backend CICS actif
# Auteur : Sebastien Cotillard
# Date   : 2026-09-03
#
# Source ce fichier depuis les scripts CI_CD_TK5.
# HERC_URL et DOCKER_CONTAINER doivent être définis avant le source.
#
# Fonctions exportées :
#   cics_detect_installed  — ce qui est installé  (DASD Docker)
#   cics_detect_running    — ce qui tourne actuellement (syslog)
#   cics_resolve_backend   — résout CICS_BACKEND → kicks|cicsvs|both
#   cics_backend_label     — libellé lisible du backend
#   cics_print_status      — résumé détection (affichage terminal)
# ============================================================

HERC_URL="${HERC_URL:-http://localhost:8038}"
DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"

# ---- Helpers Hercules (locaux) ----
_cics_herc_syslog() {
    local n="${1:-100}"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=${n}" 2>/dev/null \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only\|^Refresh\|^$" \
        || true
}

_cics_herc_cmd() {
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
        --data-urlencode "command=$1" \
        --data "norefresh=1" --data "msgcount=5" 2>/dev/null \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only\|^Refresh\|^$" \
        | tail -5 \
        || true
}

# ============================================================
# cics_detect_installed
# Vérifie la présence des fichiers DASD dans le container Docker.
# Retourne : kicks | cicsvs | both | none
# ============================================================
cics_detect_installed() {
    local has_kicks=0 has_cicsvs=0

    # KICKS : volume kicks0.3350 créé par 12_kicks_install.sh phase1
    docker exec "${DOCKER_CONTAINER}" \
        test -f "/opt/tk5/dasd/kicks0.3350" 2>/dev/null \
        && has_kicks=1 || true

    # CICS/VS : volume cics0.3350 créé par 13_cicsvs_install.sh phase2
    docker exec "${DOCKER_CONTAINER}" \
        test -f "/opt/tk5/dasd/cics0.3350" 2>/dev/null \
        && has_cicsvs=1 || true

    if   [[ $has_kicks -eq 1 && $has_cicsvs -eq 1 ]]; then echo "both"
    elif [[ $has_kicks -eq 1 ]];  then echo "kicks"
    elif [[ $has_cicsvs -eq 1 ]]; then echo "cicsvs"
    else echo "none"
    fi
}

# ============================================================
# cics_detect_running
# Inspecte le syslog Hercules pour déterminer ce qui tourne.
# KICKS  : messages KICKS/KIKCOBCL dans le syslog récent
# CICS/VS: DFHSI1500 ("initialized and ready") dans syslog
# Retourne : kicks | cicsvs | both | none
# ============================================================
cics_detect_running() {
    local has_kicks=0 has_cicsvs=0
    local applid="${CICS_APPLID:-CICS01}"

    # Prendre les 300 dernières lignes syslog (sessions récentes)
    local sl; sl=$(_cics_herc_syslog 300)

    # KICKS : messages caractéristiques (KICKS démarre des messages spécifiques)
    echo "$sl" | grep -qiE "KICKS|KICKSSYS|KICSRUN|KIKCOBCL" \
        && has_kicks=1 || true

    # CICS/VS : DFHSI1500 = région initialisée + ready for work
    echo "$sl" | grep -qE "DFHSI1500|INITIALIZED AND READY" \
        && has_cicsvs=1 || true

    # Vérification complémentaire via $D A (STC actif)
    if [[ $has_cicsvs -eq 0 ]]; then
        local stc_list; stc_list=$(_cics_herc_cmd '$D A' 2>/dev/null || true)
        echo "$stc_list" | grep -qi "${applid}" && has_cicsvs=1 || true
    fi

    if   [[ $has_kicks -eq 1 && $has_cicsvs -eq 1 ]]; then echo "both"
    elif [[ $has_kicks -eq 1 ]];  then echo "kicks"
    elif [[ $has_cicsvs -eq 1 ]]; then echo "cicsvs"
    else echo "none"
    fi
}

# ============================================================
# cics_resolve_backend
# Résout la variable CICS_BACKEND en backend effectif.
#
# CICS_BACKEND valeurs :
#   kicks   — forcer KICKS (erreur si non installé)
#   cicsvs  — forcer CICS/VS 1.7 (erreur si non installé)
#   both    — opérer sur les deux backends
#   auto    — détection automatique (DASD), préférence KICKS si les deux
#
# Retourne : kicks | cicsvs | both
# En cas d'erreur, affiche un message >&2 et retourne "none"
# ============================================================
cics_resolve_backend() {
    local setting="${CICS_BACKEND:-auto}"

    if [[ "$setting" == "auto" ]]; then
        local installed; installed=$(cics_detect_installed)
        case "$installed" in
            both)   echo "kicks" ;;   # préférence KICKS si les deux sont installés
            kicks)  echo "kicks" ;;
            cicsvs) echo "cicsvs" ;;
            none)
                echo "none"
                echo "AVERTISSEMENT : aucun backend CICS installé (KICKS ni CICS/VS 1.7)" >&2
                echo "  Installer KICKS : bash mvs/12_kicks_install.sh all" >&2
                return 1
                ;;
        esac
        return 0
    fi

    if [[ "$setting" == "both" ]]; then
        local installed; installed=$(cics_detect_installed)
        if [[ "$installed" != "both" ]]; then
            echo "AVERTISSEMENT : CICS_BACKEND=both mais seul ${installed} est installé" >&2
            echo "$installed"
        else
            echo "both"
        fi
        return 0
    fi

    # Valeur explicite : kicks ou cicsvs
    local installed; installed=$(cics_detect_installed)
    case "$setting" in
        kicks)
            if [[ "$installed" != "kicks" && "$installed" != "both" ]]; then
                echo "ERREUR : CICS_BACKEND=kicks mais KICKS n'est pas installé" >&2
                echo "  Installer : bash mvs/12_kicks_install.sh all" >&2
                echo "none"; return 1
            fi
            echo "kicks"
            ;;
        cicsvs)
            if [[ "$installed" != "cicsvs" && "$installed" != "both" ]]; then
                echo "ERREUR : CICS_BACKEND=cicsvs mais CICS/VS 1.7 n'est pas installé" >&2
                echo "  Installer : bash mvs/13_cicsvs_install.sh all" >&2
                echo "none"; return 1
            fi
            echo "cicsvs"
            ;;
        *)
            echo "ERREUR : CICS_BACKEND='${setting}' invalide (kicks|cicsvs|both|auto)" >&2
            echo "none"; return 1
            ;;
    esac
}

# ============================================================
# cics_backend_label — libellé lisible
# ============================================================
cics_backend_label() {
    case "${1:-}" in
        kicks)  echo "KICKS v1.5.0 (TSO)" ;;
        cicsvs) echo "CICS/VS 1.7 (VTAM STC)" ;;
        both)   echo "KICKS v1.5.0 + CICS/VS 1.7" ;;
        none)   echo "(aucun backend CICS)" ;;
        *)      echo "(inconnu)" ;;
    esac
}

# ============================================================
# cics_print_status — résumé lisible de la détection
# ============================================================
cics_print_status() {
    local installed; installed=$(cics_detect_installed)
    local running;   running=$(cics_detect_running)
    local resolved;  resolved=$(cics_resolve_backend)

    echo "Backend CICS configuré (CICS_BACKEND=${CICS_BACKEND:-auto}) :"
    echo "  Installé  : $(cics_backend_label "$installed")"
    echo "  En cours  : $(cics_backend_label "$running")"
    echo "  Effectif  : $(cics_backend_label "$resolved")"
}
