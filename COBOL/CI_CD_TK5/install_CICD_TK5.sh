#!/usr/bin/env bash
# ============================================================
# install_CICD_TK5.sh — Installation et configuration du pipeline CI_CD_TK5
# Auteur : Sebastien Cotillard
# Date   : 2026-09-03
#
# Compatible : macOS / Linux / Windows WSL2
#
# Usage :
#   bash install_CICD_TK5.sh              # vérifier les prérequis + état CICS
#   bash install_CICD_TK5.sh --install    # installer les dépendances système manquantes
#   bash install_CICD_TK5.sh --env        # créer/éditer le fichier .env
#   bash install_CICD_TK5.sh --cics       # assistant installation backend CICS
#                                         #   (KICKS, CICS/VS 1.7, ou les deux)
#   bash install_CICD_TK5.sh --full       # --install + --env + --cics + make install-all
#   bash install_CICD_TK5.sh --help       # afficher cette aide
# ============================================================
set -euo pipefail

# ---- Couleurs ----
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

ok()   { echo -e "  ${GREEN}✓${NC}  $*"; }
fail() { echo -e "  ${RED}✗${NC}  $*"; }
warn() { echo -e "  ${YELLOW}⚠${NC}   $*"; }
info() { echo -e "  ${CYAN}ℹ${NC}   $*"; }
hdr()  { echo -e "\n${BOLD}$*${NC}"; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/.env"

# ---- Flags ----
DO_INSTALL=0
DO_ENV=0
DO_CICS=0
DO_FULL=0

for arg in "$@"; do
    case "$arg" in
        --install)      DO_INSTALL=1 ;;
        --env)          DO_ENV=1 ;;
        --cics)         DO_CICS=1 ;;
        --full)         DO_INSTALL=1; DO_ENV=1; DO_CICS=1; DO_FULL=1 ;;
        --help|-h)
            sed -n '/#/,/^[^#]/p' "$0" | grep '^#' | sed 's/^# \?//'
            exit 0 ;;
        *)
            echo "Option inconnue : $arg" >&2
            echo "Utiliser --help pour l'aide." >&2
            exit 1 ;;
    esac
done

# ============================================================
# Détection OS
# ============================================================
detect_os() {
    local os
    os="$(uname -s)"
    case "$os" in
        Darwin) echo "macos" ;;
        Linux)
            # Distinguer WSL de Linux natif
            if grep -qi microsoft /proc/version 2>/dev/null; then
                echo "wsl"
            else
                echo "linux"
            fi
            ;;
        MINGW*|MSYS*|CYGWIN*) echo "windows" ;;
        *) echo "unknown" ;;
    esac
}

OS="$(detect_os)"

# Détecter le gestionnaire de paquets Linux
_pkg_manager() {
    if command -v apt-get &>/dev/null; then echo "apt"
    elif command -v dnf &>/dev/null;     then echo "dnf"
    elif command -v pacman &>/dev/null;  then echo "pacman"
    else echo "unknown"
    fi
}

# ============================================================
# Installation d'un paquet selon l'OS
# ============================================================
_install_pkg() {
    local pkg_mac_port="$1"   # nom MacPorts
    local pkg_brew="$2"       # nom Homebrew
    local pkg_apt="$3"        # nom apt
    local pkg_dnf="$4"        # nom dnf
    local pkg_pacman="$5"     # nom pacman

    case "$OS" in
        macos)
            if command -v port &>/dev/null; then
                sudo port install "$pkg_mac_port"
            elif command -v brew &>/dev/null; then
                brew install "$pkg_brew"
            else
                warn "Ni MacPorts ni Homebrew trouvé — installer manuellement : $pkg_brew"
            fi
            ;;
        linux|wsl)
            local pm; pm=$(_pkg_manager)
            case "$pm" in
                apt)    sudo apt-get install -y "$pkg_apt" ;;
                dnf)    sudo dnf install -y "$pkg_dnf" ;;
                pacman) sudo pacman -S --noconfirm "$pkg_pacman" ;;
                *)      warn "Gestionnaire de paquets inconnu — installer manuellement : $pkg_apt" ;;
            esac
            ;;
        windows)
            warn "Windows natif non supporté — utiliser WSL2 (Ubuntu recommandé)."
            ;;
    esac
}

# ============================================================
# Vérification / installation d'un outil
# ============================================================
# $1 = nom affiché
# $2 = commande à tester (command -v)
# $3 = description
# $4-$8 = paquets (port / brew / apt / dnf / pacman)
# Retourne 0 si trouvé, 1 si absent
MISSING=()

check_tool() {
    local name="$1"
    local cmd="$2"
    local desc="$3"
    local pkg_port="${4:-$name}"
    local pkg_brew="${5:-$name}"
    local pkg_apt="${6:-$name}"
    local pkg_dnf="${7:-$name}"
    local pkg_pacman="${8:-$name}"

    if command -v "$cmd" &>/dev/null; then
        local ver
        ver="$("$cmd" --version 2>/dev/null | head -1 || true)"
        ok "${name} — ${ver:-présent}"
        return 0
    else
        fail "${name} — absent  (${desc})"
        MISSING+=("$name")
        if [[ $DO_INSTALL -eq 1 ]]; then
            info "Installation de ${name}..."
            _install_pkg "$pkg_port" "$pkg_brew" "$pkg_apt" "$pkg_dnf" "$pkg_pacman"
            if command -v "$cmd" &>/dev/null; then
                ok "${name} installé"
            else
                warn "${name} : installation manuelle peut être nécessaire"
            fi
        fi
        return 1
    fi
}

# ============================================================
# Vérification Docker + container mvs-tk5
# ============================================================
check_docker() {
    hdr "Docker"

    # Docker daemon
    if ! command -v docker &>/dev/null; then
        fail "docker — absent"
        MISSING+=("docker")
        case "$OS" in
            macos)
                info "Installer Docker Desktop : https://www.docker.com/products/docker-desktop/"
                info "  ou: brew install --cask docker" ;;
            linux|wsl)
                info "  sudo apt install docker.io && sudo usermod -aG docker \$USER" ;;
        esac
        return
    fi

    if ! docker info &>/dev/null 2>&1; then
        warn "docker — installé mais daemon non démarré"
        case "$OS" in
            macos)   info "Lancer Docker Desktop depuis les Applications" ;;
            linux)   info "sudo systemctl start docker" ;;
            wsl)     info "Lancer Docker Desktop sur Windows (option WSL2 backend activée)" ;;
        esac
        return
    fi

    local ver
    ver="$(docker --version)"
    ok "docker — ${ver}"

    # Container mvs-tk5
    local DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
    if docker ps -a --format '{{.Names}}' | grep -q "^${DOCKER_CONTAINER}$"; then
        local state
        state="$(docker inspect --format '{{.State.Status}}' "${DOCKER_CONTAINER}" 2>/dev/null || echo "?")"
        if [[ "$state" == "running" ]]; then
            ok "container ${DOCKER_CONTAINER} — running"
        else
            warn "container ${DOCKER_CONTAINER} — ${state} (arrêté)"
            info "Démarrer : docker start ${DOCKER_CONTAINER}"
        fi
    else
        fail "container ${DOCKER_CONTAINER} — non trouvé"
        info "Le container MVS TK5 n'est pas encore créé."
        info "Référence : http://www.prince-webdesign.nl/index.php/software/16-hercules-mvs-3-8j-turnkey-5"
    fi
}

# ============================================================
# Vérification s3270 (chemin spécifique macOS)
# ============================================================
check_s3270() {
    hdr "s3270 (terminal 3270)"

    local found=""
    # Chercher dans les emplacements courants en plus du PATH
    for _p in \
        "${S3270:-}" \
        /opt/local/bin/s3270 \
        /opt/homebrew/bin/s3270 \
        /usr/local/bin/s3270 \
        /usr/bin/s3270; do
        [[ -x "${_p:-}" ]] && { found="$_p"; break; }
    done
    [[ -z "$found" ]] && found="$(command -v s3270 2>/dev/null || true)"

    if [[ -x "${found:-}" ]]; then
        local ver; ver="$("$found" --version 2>/dev/null | head -1 || true)"
        ok "s3270 — ${ver:-présent} (${found})"
    else
        fail "s3270 — absent"
        MISSING+=("s3270")
        case "$OS" in
            macos)
                info "  brew install x3270" ;;
            linux|wsl)
                info "  sudo apt install x3270    (Debian/Ubuntu)"
                info "  sudo dnf install x3270    (Fedora/RHEL)" ;;
        esac
        if [[ $DO_INSTALL -eq 1 ]]; then
            _install_pkg x3270 x3270 x3270 x3270 x3270
        fi
    fi
}

# ============================================================
# Vérification fswatch / inotifywait
# ============================================================
check_watcher() {
    hdr "Surveillance fichiers (make watch)"

    if [[ "$OS" == "macos" ]]; then
        local fswatch=""
        for _p in /opt/local/bin/fswatch /opt/homebrew/bin/fswatch /usr/local/bin/fswatch; do
            [[ -x "$_p" ]] && { fswatch="$_p"; break; }
        done
        [[ -z "$fswatch" ]] && fswatch="$(command -v fswatch 2>/dev/null || true)"

        if [[ -x "${fswatch:-}" ]]; then
            ok "fswatch — présent (${fswatch})"
        else
            fail "fswatch — absent"
            MISSING+=("fswatch")
            info "  brew install fswatch  ou  sudo port install fswatch"
            if [[ $DO_INSTALL -eq 1 ]]; then
                _install_pkg fswatch fswatch fswatch fswatch fswatch
            fi
        fi
    else
        if command -v inotifywait &>/dev/null; then
            ok "inotifywait — présent"
        else
            fail "inotifywait — absent"
            MISSING+=("inotifywait")
            info "  sudo apt install inotify-tools    (Debian/Ubuntu)"
            info "  sudo dnf install inotify-tools    (Fedora/RHEL)"
            info "  sudo pacman -S inotify-tools      (Arch)"
            if [[ $DO_INSTALL -eq 1 ]]; then
                _install_pkg inotify-tools inotify-tools inotify-tools inotify-tools inotify-tools
            fi
        fi
    fi
}

# ============================================================
# Vérification nc (netcat) — utilisé dans s3270_lib.sh
# ============================================================
check_nc() {
    if command -v nc &>/dev/null; then
        ok "netcat (nc) — présent"
    else
        fail "netcat (nc) — absent"
        MISSING+=("nc")
        case "$OS" in
            linux|wsl) info "  sudo apt install netcat-openbsd" ;;
            macos)     info "  préinstallé sur macOS — vérifier PATH" ;;
        esac
        if [[ $DO_INSTALL -eq 1 ]]; then
            _install_pkg netcat netcat netcat-openbsd nmap-ncat openbsd-netcat
        fi
    fi
}

# ============================================================
# Résumé de l'environnement courant (.env ou variables)
# ============================================================
check_env() {
    hdr "Configuration .env"

    if [[ -f "$ENV_FILE" ]]; then
        ok ".env trouvé : ${ENV_FILE}"
        # Vérifier les variables clés
        local vars=(TK5_HOST TK5_PORT TSO_USER HLQ HERC_URL DOCKER_CONTAINER)
        for v in "${vars[@]}"; do
            local val
            val="$(grep -E "^export ${v}=" "$ENV_FILE" 2>/dev/null \
                    | sed "s/^export ${v}=//" | tr -d '"' || true)"
            if [[ -n "$val" ]]; then
                info "  ${v}=${val}"
            else
                warn "  ${v} non défini (sera utilisé valeur par défaut)"
            fi
        done
    else
        warn ".env absent — les valeurs par défaut seront utilisées."
        info "Générer : bash install_CICD_TK5.sh --env"
    fi
}

# ============================================================
# Créer / régénérer le fichier .env
# ============================================================
create_env() {
    hdr "Création du fichier .env"

    # Valeurs actuelles (depuis .env existant ou env)
    _read_var() {
        local var="$1" default="$2"
        local val
        val="$(grep -E "^export ${var}=" "$ENV_FILE" 2>/dev/null \
                | sed "s/^export ${var}=//" | tr -d '"' || true)"
        echo "${val:-${!var:-$default}}"
    }

    local tk5_host;     tk5_host="$(_read_var TK5_HOST localhost)"
    local tk5_port;     tk5_port="$(_read_var TK5_PORT 3270)"
    local tso_user;     tso_user="$(_read_var TSO_USER HERC02)"
    local tso_pass;     tso_pass="$(_read_var TSO_PASS CUL8TR)"
    local hlq;          hlq="$(_read_var HLQ HERC02)"
    local herc_url;     herc_url="$(_read_var HERC_URL http://localhost:8038)"
    local docker_ctr;   docker_ctr="$(_read_var DOCKER_CONTAINER mvs-tk5)"
    local cobhlq;       cobhlq="$(_read_var COBHLQ IGY)"
    local cics_backend; cics_backend="$(_read_var CICS_BACKEND auto)"
    local cics_applid;  cics_applid="$(_read_var CICS_APPLID CICS01)"

    # Détecter s3270
    local s3270_path=""
    for _p in \
        "${S3270:-}" \
        /opt/local/bin/s3270 \
        /opt/homebrew/bin/s3270 \
        /usr/local/bin/s3270 \
        /usr/bin/s3270; do
        [[ -x "${_p:-}" ]] && { s3270_path="$_p"; break; }
    done
    [[ -z "$s3270_path" ]] && s3270_path="$(command -v s3270 2>/dev/null || echo "")"
    s3270_path="$(_read_var S3270 "${s3270_path}")"

    echo ""
    info "Génération de ${ENV_FILE}"
    info "Appuyer sur Entrée pour conserver la valeur actuelle."
    echo ""

    _prompt() {
        local label="$1" current="$2" varname="$3"
        local input
        printf "  %-22s [%s]: " "$label" "$current"
        read -r input
        echo "${input:-$current}"
    }

    tk5_host="$(_prompt   "TK5_HOST (IP/hostname)" "$tk5_host")"
    tk5_port="$(_prompt   "TK5_PORT (TN3270)"      "$tk5_port")"
    tso_user="$(_prompt   "TSO_USER"               "$tso_user")"
    tso_pass="$(_prompt   "TSO_PASS"               "$tso_pass")"
    hlq="$(_prompt        "HLQ (MVS qualifier)"    "$hlq")"
    herc_url="$(_prompt   "HERC_URL"               "$herc_url")"
    docker_ctr="$(_prompt "DOCKER_CONTAINER"       "$docker_ctr")"
    cobhlq="$(_prompt     "COBHLQ (compilateur)"   "$cobhlq")"
    s3270_path="$(_prompt "S3270 (chemin binaire)" "$s3270_path")"

    echo ""
    echo "  Backend CICS (kicks|cicsvs|both|auto) :"
    info "    kicks  = KICKS v1.5.0 (TSO, CEDA/CEMT)"
    info "    cicsvs = CICS/VS 1.7  (VTAM STC, tables assemblées)"
    info "    both   = opérer sur les deux"
    info "    auto   = détection automatique, préférence KICKS"
    cics_backend="$(_prompt "CICS_BACKEND"           "$cics_backend")"
    cics_applid="$( _prompt "CICS_APPLID (CICS/VS)"  "$cics_applid")"

    cat > "$ENV_FILE" <<ENVEOF
# CI_CD_TK5 — Configuration environnement
# Généré par install_CICD_TK5.sh le $(date '+%Y-%m-%d %H:%M:%S')
# Source ce fichier avant d'utiliser les scripts :
#   source CI_CD_TK5/.env

export TK5_HOST="${tk5_host}"
export TK5_PORT="${tk5_port}"
export TSO_USER="${tso_user}"
export TSO_PASS="${tso_pass}"
export HLQ="${hlq}"
export HERC_URL="${herc_url}"
export DOCKER_CONTAINER="${docker_ctr}"
export COBHLQ="${cobhlq}"
export S3270="${s3270_path}"

# Backend CICS : kicks | cicsvs | both | auto
# auto = détection automatique (DASD Docker), préférence KICKS si les deux installés
export CICS_BACKEND="${cics_backend}"
export CICS_APPLID="${cics_applid}"     # VTAM APPLID région CICS/VS (ignoré si CICS_BACKEND=kicks)
ENVEOF

    ok ".env créé : ${ENV_FILE}"
    info "Sourcer avant utilisation : source CI_CD_TK5/.env"
}

# ============================================================
# Assistant installation backend CICS
# Appelé par --cics ou --full, ou depuis detect_cics_backend
# si rien n'est installé et --install est actif.
# ============================================================
install_cics_backend() {
    local docker_ctr="${DOCKER_CONTAINER:-mvs-tk5}"
    local has_kicks=0 has_cicsvs=0

    if docker info &>/dev/null 2>&1 && \
       docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${docker_ctr}$"; then
        docker exec "$docker_ctr" test -f "/opt/tk5/dasd/kicks0.3350" 2>/dev/null \
            && has_kicks=1  || true
        docker exec "$docker_ctr" test -f "/opt/tk5/dasd/cics0.3350"  2>/dev/null \
            && has_cicsvs=1 || true
    fi

    hdr "Assistant installation backend CICS"
    echo ""
    echo -e "  Backends déjà installés :"
    [[ $has_kicks  -eq 1 ]] && ok "KICKS v1.5.0"    || info "KICKS v1.5.0    — non installé"
    [[ $has_cicsvs -eq 1 ]] && ok "CICS/VS 1.7"     || info "CICS/VS 1.7     — non installé"
    echo ""

    # ---- Choix interactif ----
    echo -e "  ${BOLD}Quel backend CICS installer ?${NC}"
    echo ""
    echo "    1) KICKS v1.5.0  — recommandé pour le développement"
    echo "       Tourne sous TSO, CEDA/CEMT interactif, installation ~30 min"
    echo ""
    echo "    2) CICS/VS 1.7   — comportement IBM authentique (avancé)"
    echo "       Tourne comme région VTAM (STC), tables assemblées, plusieurs jours"
    echo "       Nécessite une image bande depuis bitsavers.org"
    echo ""
    echo "    3) Les deux      — KICKS + CICS/VS 1.7"
    echo ""
    echo "    0) Ignorer / déjà installé"
    echo ""
    local choice
    printf "  Choix [1/2/3/0] : "
    read -r choice

    case "$choice" in
        1)
            _install_kicks "$has_kicks"
            ;;
        2)
            _install_cicsvs "$has_cicsvs"
            ;;
        3)
            _install_kicks "$has_kicks"
            echo ""
            _install_cicsvs "$has_cicsvs"
            ;;
        0|"")
            info "Installation backend ignorée"
            ;;
        *)
            warn "Choix invalide — installation ignorée"
            ;;
    esac
}

# ---- Sous-fonctions d'installation KICKS ----
_install_kicks() {
    local already="${1:-0}"
    local docker_ctr="${DOCKER_CONTAINER:-mvs-tk5}"

    if [[ $already -eq 1 ]]; then
        ok "KICKS déjà installé — relancer une phase spécifique ?"
        printf "  Phase à relancer (all/status/entrée vide pour ignorer) : "
        local phase; read -r phase
        [[ -z "$phase" ]] && return
        cd "$SCRIPT_DIR"
        bash mvs/12_kicks_install.sh "${phase}"
        return
    fi

    hdr "Installation KICKS v1.5.0"
    echo ""
    info "KICKS va être installé sur le container ${docker_ctr}"
    info "Durée estimée : 20-40 min (selon la vitesse réseau pour le XMI)"
    echo ""

    # Vérifier que docker + container sont prêts
    if ! docker info &>/dev/null 2>&1; then
        fail "Docker non disponible — démarrer Docker puis relancer"
        return 1
    fi
    if ! docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${docker_ctr}$"; then
        fail "Container ${docker_ctr} non démarré"
        info "  docker start ${docker_ctr}"
        return 1
    fi

    # Choix des phases
    echo "  Phases disponibles :"
    echo "    all      — installation complète (recommandé)"
    echo "    dasd     — volume DASD KICKS0 seulement"
    echo "    status   — vérifier l'état actuel"
    printf "  Phase(s) [all] : "
    local phases; read -r phases
    phases="${phases:-all}"

    echo ""
    info "Lancement : bash mvs/12_kicks_install.sh ${phases}"
    echo ""
    cd "$SCRIPT_DIR"
    # shellcheck disable=SC2086
    bash mvs/12_kicks_install.sh ${phases} && \
        ok "KICKS installé avec succès" || \
        warn "Installation KICKS terminée avec des avertissements — vérifier le syslog"
}

# ---- Sous-fonctions d'installation CICS/VS 1.7 ----
_install_cicsvs() {
    local already="${1:-0}"
    local docker_ctr="${DOCKER_CONTAINER:-mvs-tk5}"

    if [[ $already -eq 1 ]]; then
        ok "CICS/VS 1.7 déjà installé — relancer une phase spécifique ?"
        printf "  Phase à relancer (all/check/start/entrée vide pour ignorer) : "
        local phase; read -r phase
        [[ -z "$phase" ]] && return
        cd "$SCRIPT_DIR"
        bash mvs/13_cicsvs_install.sh "${phase}"
        return
    fi

    hdr "Installation CICS/VS 1.7 (avancé)"
    echo ""
    warn "PROJET AVANCÉ — durée estimée : plusieurs jours"
    warn "Nécessite une image bande depuis bitsavers.org"
    echo ""
    echo "  Documentation :"
    info "  https://bitsavers.org/bits/IBM/cics/"
    info "  https://www.jaymoseley.com/hercules/"
    echo ""
    printf "  Confirmer l'installation CICS/VS 1.7 ? [o/N] : "
    local confirm; read -r confirm
    [[ "${confirm,,}" != "o" && "${confirm,,}" != "y" ]] && {
        info "Installation CICS/VS annulée"
        return
    }

    # Vérifier docker + container
    if ! docker info &>/dev/null 2>&1; then
        fail "Docker non disponible — démarrer Docker puis relancer"
        return 1
    fi
    if ! docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${docker_ctr}$"; then
        fail "Container ${docker_ctr} non démarré"
        info "  docker start ${docker_ctr}"
        return 1
    fi

    # Vérifier image bande
    local tape_path="${CICS_TAPE_LOCAL:-/tmp/cicsvs17.aws}"
    echo ""
    if [[ -f "$tape_path" ]]; then
        ok "Image bande trouvée : ${tape_path} ($(du -h "${tape_path}" | cut -f1))"
    else
        warn "Image bande absente : ${tape_path}"
        info "Télécharger depuis bitsavers.org et placer ici : ${tape_path}"
        info "Ou surcharger : export CICS_TAPE_LOCAL=/chemin/vers/cicsvs17.aws"
        echo ""
        printf "  Continuer sans l'image bande (phase check seulement) ? [o/N] : "
        local nocont; read -r nocont
        if [[ "${nocont,,}" != "o" && "${nocont,,}" != "y" ]]; then
            info "Installation CICS/VS annulée — obtenir l'image bande d'abord"
            return
        fi
    fi

    # Choix des phases
    echo ""
    echo "  Phases disponibles :"
    echo "    check    — vérifier prérequis (recommandé en premier)"
    echo "    all      — installation complète"
    echo "    dasd     — volume DASD CICS0 seulement"
    printf "  Phase(s) [check] : "
    local phases; read -r phases
    phases="${phases:-check}"

    echo ""
    info "Lancement : bash mvs/13_cicsvs_install.sh ${phases}"
    echo ""
    cd "$SCRIPT_DIR"
    # shellcheck disable=SC2086
    bash mvs/13_cicsvs_install.sh ${phases} && \
        ok "Phase(s) CICS/VS terminée(s)" || \
        warn "Phase(s) terminée(s) avec des avertissements — voir le syslog"
}

# ============================================================
# Détection et sélection du backend CICS (affichage seul)
# ============================================================
detect_cics_backend() {
    local docker_ctr="${DOCKER_CONTAINER:-mvs-tk5}"
    local has_kicks=0 has_cicsvs=0

    # Vérifier seulement si docker tourne
    if ! docker info &>/dev/null 2>&1; then
        warn "Docker non disponible — détection CICS ignorée"
        return
    fi
    if ! docker ps --format '{{.Names}}' 2>/dev/null | grep -q "^${docker_ctr}$"; then
        warn "Container ${docker_ctr} non démarré — détection CICS ignorée"
        info "  docker start ${docker_ctr}  puis relancer"
        return
    fi

    docker exec "$docker_ctr" test -f "/opt/tk5/dasd/kicks0.3350"  2>/dev/null && has_kicks=1  || true
    docker exec "$docker_ctr" test -f "/opt/tk5/dasd/cics0.3350"   2>/dev/null && has_cicsvs=1 || true

    echo ""
    if [[ $has_kicks -eq 1 && $has_cicsvs -eq 1 ]]; then
        ok "KICKS v1.5.0 + CICS/VS 1.7 installés (both)"
        info "CICS_BACKEND=both → opère sur les deux backends"
        info "CICS_BACKEND=kicks → préférer KICKS (recommandé)"
    elif [[ $has_kicks -eq 1 ]]; then
        ok "KICKS v1.5.0 installé"
        info "CICS_BACKEND=kicks (défaut)"
    elif [[ $has_cicsvs -eq 1 ]]; then
        ok "CICS/VS 1.7 installé"
        warn "KICKS non installé — fonctionnalités CEDA non disponibles"
        info "CICS_BACKEND=cicsvs"
    else
        warn "Aucun backend CICS installé"
        echo ""
        info "Options :"
        info "  Assistant interactif : bash install_CICD_TK5.sh --cics"
        info "  KICKS (recommandé)   : bash mvs/12_kicks_install.sh all"
        info "  CICS/VS 1.7          : bash mvs/13_cicsvs_install.sh all  (avancé)"
    fi
}

# ============================================================
# Vérifier conf/ et scripts requis
# ============================================================
check_structure() {
    hdr "Structure CI_CD_TK5"

    local ok_count=0 fail_count=0
    local required_files=(
        "mvs/01_upload.sh"
        "mvs/02_submit.sh"
        "mvs/03_cics.sh"
        "mvs/06_build.sh"
        "mvs/07_watch.sh"
        "mvs/08_test_cics.sh"
        "mvs/09_ci.sh"
        "mvs/herc.sh"
        "mvs/s3270_lib.sh"
        "lib/project.sh"
        "Makefile"
    )

    for f in "${required_files[@]}"; do
        if [[ -f "${SCRIPT_DIR}/${f}" ]]; then
            (( ok_count++ )) || true
        else
            fail "${f} — manquant"
            (( fail_count++ )) || true
        fi
    done
    [[ $fail_count -eq 0 ]] && ok "Tous les scripts présents (${ok_count}/${#required_files[@]})"

    # Lister les projets configurés
    echo ""
    info "Projets disponibles (conf/*.conf) :"
    for f in "${SCRIPT_DIR}/conf/"*.conf; do
        [[ -f "$f" ]] && info "  • $(basename "$f" .conf)"
    done
}

# ============================================================
# Lancement make install-all
# ============================================================
run_install_all() {
    hdr "Installation MVS TK5 (make install-all)"

    local project="${PROJECT_NAME:-gstk}"
    info "Projet : ${project}"
    echo ""

    if ! command -v make &>/dev/null; then
        fail "make introuvable"
        case "$OS" in
            macos)   info "  xcode-select --install  ou  brew install make" ;;
            linux|wsl) info "  sudo apt install make" ;;
        esac
        return 1
    fi

    cd "$SCRIPT_DIR"
    PROJECT_NAME="$project" make install-all
}

# ============================================================
# Main
# ============================================================
echo ""
echo -e "${BOLD}════════════════════════════════════════════════${NC}"
echo -e "${BOLD}  CI_CD_TK5 — Script d'installation            ${NC}"
echo -e "${BOLD}  Auteur : Sebastien Cotillard                  ${NC}"
echo -e "${BOLD}════════════════════════════════════════════════${NC}"

# Afficher l'OS détecté
case "$OS" in
    macos)   echo -e "  OS détecté : ${CYAN}macOS${NC}" ;;
    linux)   echo -e "  OS détecté : ${CYAN}Linux${NC}" ;;
    wsl)     echo -e "  OS détecté : ${CYAN}Windows WSL2${NC}" ;;
    windows) echo -e "  OS détecté : ${YELLOW}Windows natif${NC} (recommander WSL2)" ;;
    *)       echo -e "  OS détecté : ${YELLOW}inconnu${NC}" ;;
esac

if [[ "$OS" == "windows" ]]; then
    echo ""
    warn "Windows natif non supporté."
    warn "Installer WSL2 + Ubuntu et relancer depuis WSL :"
    warn "  wsl --install -d Ubuntu"
    warn "  # puis depuis WSL : bash install_CICD_TK5.sh"
    exit 1
fi

# ---- Vérification structure ----
check_structure

# ---- Prérequis système ----
hdr "Prérequis système"
check_tool "GnuCOBOL"  cobc   "vérification syntaxe COBOL locale" \
    gnu-cobol gnucobol gnucobol gnucobol gnucobol
check_tool "curl"      curl   "API HTTP Hercules" \
    curl curl curl curl curl
check_tool "python3"   python3 "libre port TCP (s3270_lib)" \
    python312 python3 python3 python3 python
check_nc
check_s3270
check_watcher
check_docker

# ---- Backend CICS ----
hdr "Backend CICS (KICKS / CICS/VS 1.7)"
detect_cics_backend

# ---- PostgreSQL (optionnel pour GSTK) ----
hdr "PostgreSQL (optionnel — tests SQL GSTK)"
if command -v psql &>/dev/null; then
    ok "psql — $(psql --version 2>/dev/null | head -1)"
else
    warn "psql absent — requis uniquement pour les tests SQL GSTK"
    case "$OS" in
        macos)   info "  brew install postgresql@16  ou  sudo port install postgresql16" ;;
        linux|wsl) info "  sudo apt install postgresql-client" ;;
    esac
fi

# ---- .env ----
if [[ $DO_ENV -eq 1 ]]; then
    create_env
else
    check_env
fi

# ---- Installation backend CICS (--cics ou --full) ----
if [[ $DO_CICS -eq 1 ]]; then
    install_cics_backend
fi

# ---- Résumé ----
hdr "Résumé"
if [[ ${#MISSING[@]} -eq 0 ]]; then
    ok "Tous les prérequis sont satisfaits."
else
    if [[ $DO_INSTALL -eq 1 ]]; then
        warn "${#MISSING[@]} outil(s) ont nécessité une installation : ${MISSING[*]}"
        warn "Relancer le script pour vérifier si tout est OK."
    else
        warn "${#MISSING[@]} outil(s) manquant(s) : ${MISSING[*]}"
        info "Installer automatiquement : bash install_CICD_TK5.sh --install"
    fi
fi

echo ""
info "Prochaines étapes :"
info "  1. Démarrer le container : docker start \${DOCKER_CONTAINER:-mvs-tk5}"
if [[ ! -f "$ENV_FILE" ]]; then
info "  2. Configurer + installer le backend CICS en une commande :"
info "     bash install_CICD_TK5.sh --env --cics"
info "     (ou --full pour tout : deps + env + cics + make install-all)"
else
info "  2. Installer / vérifier le backend CICS :"
info "     bash install_CICD_TK5.sh --cics"
fi
info "  3. Sourcer : source CI_CD_TK5/.env"
info "  4. Vérifier le backend actif : bash mvs/03_cics.sh detect"
info "  5. Première installation MVS : make install-all"
echo ""

# ---- make install-all ----
if [[ $DO_FULL -eq 1 ]]; then
    if [[ ${#MISSING[@]} -gt 0 ]]; then
        warn "Des outils sont encore manquants — make install-all ignoré."
    else
        run_install_all
    fi
fi
