#!/usr/bin/env bash
# ============================================================
# lib/project.sh — Loader de configuration projet
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Source ce fichier depuis les scripts CI_CD_TK5.
# Attend que CI_DIR soit défini avant le source.
# Lit PROJECT_NAME depuis l'environnement (défaut: gstk).
#
# Usage dans un script fils :
#   CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
#   source "${CI_DIR}/lib/project.sh"
# ============================================================

CI_DIR="${CI_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"
PROJECT_NAME="${PROJECT_NAME:-gstk}"

CONF_FILE="${CI_DIR}/conf/${PROJECT_NAME}.conf"
if [[ ! -f "${CONF_FILE}" ]]; then
    echo "ERREUR : projet inconnu '${PROJECT_NAME}'" >&2
    echo "Projets disponibles :" >&2
    for _f in "${CI_DIR}/conf/"*.conf; do
        [[ -f "$_f" ]] && echo "  $(basename "$_f" .conf)" >&2
    done
    unset _f
    exit 1
fi

source "${CONF_FILE}"

# Résoudre PROJECT_DIR en absolu si relatif
if [[ -n "${PROJECT_DIR:-}" && ! "${PROJECT_DIR}" = /* ]]; then
    PROJECT_DIR="$(cd "${CI_DIR}/${PROJECT_DIR}" 2>/dev/null && pwd || echo "${PROJECT_DIR}")"
fi

# Valeurs par défaut pour variables optionnelles
HAS_CICS="${HAS_CICS:-0}"
MVS_UPLOAD_CMD="${MVS_UPLOAD_CMD:-}"
MVS_COMPILE_CMD="${MVS_COMPILE_CMD:-}"
CHECK_UNIT_CMD="${CHECK_UNIT_CMD:-}"
BMS_PATTERN="${BMS_PATTERN:-}"
CBL_PATTERN="${CBL_PATTERN:-}"
CICS_TESTS_FILE="${CICS_TESTS_FILE:-}"
CBL_FILES=("${CBL_FILES[@]+"${CBL_FILES[@]}"}")
COPYBOOK_FILES=("${COPYBOOK_FILES[@]+"${COPYBOOK_FILES[@]}"}")
BMS_FILES=("${BMS_FILES[@]+"${BMS_FILES[@]}"}")
CICS_MAPSETS=("${CICS_MAPSETS[@]+"${CICS_MAPSETS[@]}"}")
CICS_PROGRAMS=("${CICS_PROGRAMS[@]+"${CICS_PROGRAMS[@]}"}")
CICS_TRANSACTIONS=("${CICS_TRANSACTIONS[@]+"${CICS_TRANSACTIONS[@]}"}")
WATCH_EXTENSIONS=("${WATCH_EXTENSIONS[@]+"${WATCH_EXTENSIONS[@]}"}")
