#!/usr/bin/env bash
# ============================================================
# s3270_lib.sh — Helper library : s3270 en mode -scriptport
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Pourquoi scriptport ?  s3270 4.5 exécute Wait(inputfield)
# implicitement avant tout script stdin.  Le banner Hercules
# n'ayant aucun champ de saisie, le pipe stdin bloque ad vitam.
# Le mode -scriptport envoie chaque action via TCP et reçoit
# la réponse synchrone (ok / error:) sans ce problème.
#
# Pourquoi "skip dead device" ?
#   TK5/VTAM peut avoir relâché certains terminaux TN3270
#   (IST800I TERM CUUxxxx HAS BEEN RELEASED BY NETSOL).
#   Hercules réattribue ces slots en premier.  s3270_start()
#   sonde chaque dispositif : il envoie Reset+Clear et attend
#   une réponse VTAM.  Les slots morts (pas de réponse en 10 s)
#   restent connectés pour bloquer le slot ; la recherche
#   continue jusqu'à trouver un terminal actif (max 6 essais).
#
# Usage (depuis un script fils) :
#   source "$(dirname "${BASH_SOURCE[0]}")/s3270_lib.sh"
#
# Fonctions exportées :
#   s3270_start   — démarrer s3270, trouver un terminal VTAM actif
#   s3270_stop    — déconnecter + tuer s3270 (et les dummies)
#   s3270_cmd     — envoyer une action, retourner la sortie
#   s3270_screen  — texte de l'écran courant (lignes data:)
#   s3270_login   — séquence TSO TK5 (Reset→Clear→user→pass)
# ============================================================

# ---- Localiser s3270 ----
if [[ -z "${S3270:-}" ]]; then
    for _s3270_c in \
        /usr/local/bin/s3270 \
        /opt/homebrew/bin/s3270 \
        /opt/local/bin/s3270; do
        [[ -x "$_s3270_c" ]] && { S3270="$_s3270_c"; break; }
    done
    if [[ -z "${S3270:-}" ]]; then
        S3270=$(command -v s3270 2>/dev/null || true)
    fi
fi

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"

_S3270_PID=""
_S3270_PORT=""
_S3270_DUMMY_PIDS=()   # connexions zombies maintenues pour bloquer les slots morts

# ---- Trouver un port TCP libre ----
_s3270_free_port() {
    python3 -c "
import socket
s = socket.socket()
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind(('127.0.0.1', 0))
p = s.getsockname()[1]
s.close()
print(p)
" 2>/dev/null || echo "13270"
}

# ---- Tuer les connexions dummy ----
_s3270_kill_dummies() {
    if [[ ${#_S3270_DUMMY_PIDS[@]} -gt 0 ]]; then
        local p
        for p in "${_S3270_DUMMY_PIDS[@]}"; do
            kill "$p" 2>/dev/null || true
            wait "$p" 2>/dev/null || true
        done
        _S3270_DUMMY_PIDS=()
    fi
}

# ---- Démarrer s3270 en mode scriptport ----
#
# Stratégie "skip dead device" :
#   1. Lancer s3270 (-model 2 -scriptport) → Hercules alloue un terminal.
#   2. Sonder VTAM : Reset() + Clear() puis Ascii() pendant 10 s.
#      - Si l'écran reste vide  → VTAM ne répond pas (slot mort).
#        Garder s3270 connecté (pour bloquer le slot) et recommencer.
#      - Si l'écran reçoit du texte → VTAM a répondu (panel TSO LOGON).
#        Ce slot est actif : l'utiliser comme connexion principale.
#   3. Au retour, _S3270_PID/_S3270_PORT pointent vers le slot actif.
#      Les dummies sont libérés par s3270_stop().
#
# Paramètres importants :
#   -model 2  : IBM-3279-2-E (24x80).  TK5 VTAM définit ses LU en model 2 ;
#               le modèle par défaut (4) ne reçoit pas de réponse VTAM.
s3270_start() {
    [[ -x "${S3270:-}" ]] || {
        echo "ERROR: s3270 introuvable (S3270=${S3270:-<vide>})" >&2
        return 1
    }

    local max_dev=6
    local attempt=0
    local port pid i j ok vtam_ok scr

    while [[ $attempt -lt $max_dev ]]; do
        attempt=$(( attempt + 1 ))
        port=$(_s3270_free_port)

        # Lancer s3270 en arrière-plan
        "$S3270" -noverifycert -model 2 -scriptport "$port" \
            "${TK5_HOST}:${TK5_PORT}" >/dev/null 2>&1 &
        pid=$!

        # Attendre que le scriptport soit ouvert (max 5 s)
        ok=0
        i=0
        while [[ $i -lt 10 ]]; do
            sleep 0.5
            if nc -z 127.0.0.1 "$port" 2>/dev/null; then ok=1; break; fi
            i=$(( i + 1 ))
        done

        if [[ $ok -eq 0 ]]; then
            echo "ERROR: scriptport $port ne répond pas (tentative $attempt)" >&2
            kill "$pid" 2>/dev/null || true
            return 1
        fi

        # Sonder VTAM : Reset + Clear, puis Ascii pendant 10 s
        printf "Reset()\n" | nc -w3 127.0.0.1 "$port" >/dev/null 2>&1 || true
        sleep 0.3
        # Clear() envoie un AID au host ; nc -w3 peut expirer avant la réponse
        # VTAM (normale), mais l'AID est bien transmis par s3270.
        printf "Clear()\n" | nc -w3 127.0.0.1 "$port" >/dev/null 2>&1 || true

        vtam_ok=0
        j=0
        while [[ $j -lt 20 ]]; do   # 20 × 0.5 s = 10 s max
            sleep 0.5
            scr=$(printf "Ascii()\n" | nc -w3 127.0.0.1 "$port" 2>/dev/null \
                  | grep "^data:" | sed 's/^data: //')
            # Le panel TSO LOGON contient "Terminal", "Date", "Time" etc.
            # Un slot mort garde l'écran vide (Clear sans réponse VTAM).
            if echo "$scr" | grep -qE "[A-Za-z]{5,}"; then
                vtam_ok=1
                break
            fi
            j=$(( j + 1 ))
        done

        if [[ $vtam_ok -eq 1 ]]; then
            _S3270_PID=$pid
            _S3270_PORT=$port
            return 0
        fi

        # Slot mort : garder connecté pour le bloquer, passer au suivant
        _S3270_DUMMY_PIDS+=("$pid")
    done

    echo "ERROR: aucun terminal VTAM actif trouvé (${max_dev} tentatives)" >&2
    _s3270_kill_dummies
    return 1
}

# ---- Envoyer une action s3270 via scriptport ----
# $1 = action  (ex: 'String("HERC02")', 'Enter()', 'Ascii()')
# $2 = timeout en secondes (défaut 30)
#
# Retourne 0 si réponse « ok »,  1 si « error: » ou timeout.
# Imprime sur stdout les lignes data: (contenu écran pour Ascii()).
s3270_cmd() {
    local action="$1"
    local timeout="${2:-30}"
    local resp

    resp=$(printf "%s\n" "$action" \
        | nc -w"${timeout}" 127.0.0.1 "${_S3270_PORT}" 2>/dev/null) || true

    if [[ -z "$resp" ]]; then
        echo "W: s3270_cmd [${action}] — pas de réponse (timeout ${timeout}s ?)" >&2
        return 1
    fi

    if echo "$resp" | grep -q "^error:"; then
        local errmsg
        errmsg=$(echo "$resp" | grep "^error:" | head -1 | sed 's/^error: //')
        echo "W: [${action}] → ${errmsg}" >&2
        return 1
    fi

    # Afficher les lignes data: sans le préfixe (contenu écran)
    echo "$resp" | grep "^data:" | sed 's/^data: //'
    return 0
}

# ---- Lire l'écran courant (texte brut) ----
s3270_screen() {
    s3270_cmd "Ascii()" 10
}

# ---- Connexion TSO selon la séquence officielle TK5 ----
#
# Particularités TK5 observées :
#   1. Enter() après userid complète en ~1 s, MAIS l'écran suivant
#      (panel mot de passe) arrive dans un second write host distinct.
#      → Wait(30,Output) est obligatoire pour "voir" le panel password.
#   2. Enter() après mot de passe bloque jusqu'à ce que TSO ait loggué
#      l'utilisateur ET déverrouillé le clavier (prompt READY).
#      Cette étape peut prendre 30-90 s sur TK5 → nc -w120.
#
# Séquence : Reset → Clear → panel TSO LOGON → user → Enter →
#            Wait(Output) [panel password] → pass → Enter(120s) → READY
s3270_login() {
    local user="${TSO_USER:-HERC02}"
    local pass="${TSO_PASS:-CUL8TR}"

    # Reset pour déverrouiller le clavier (peut être L après la sonde)
    s3270_cmd "Reset()" 5 >/dev/null 2>&1 || true
    sleep 0.5
    # Renvoyer Clear pour obtenir un panel TSO LOGON propre
    s3270_cmd "Clear()" 60 >/dev/null 2>&1 || true
    sleep 2   # laisser le panel TSO LOGON se stabiliser

    # Saisir le nom d'utilisateur
    s3270_cmd "String(\"${user}\")" 5 >/dev/null
    # Enter() complète vite (~1 s) mais laisse l'écran vide (1ᵉʳ write = clear)
    s3270_cmd "Enter()" 60 >/dev/null 2>&1 || true
    # Wait(Output) capture le 2ᵉ write = panel mot de passe
    s3270_cmd "Wait(30,Output)" 35 >/dev/null 2>&1 || true
    sleep 0.5

    # Saisir le mot de passe
    s3270_cmd "String(\"${pass}\")" 5 >/dev/null
    #
    # Sur TK5/HERC02, Enter(password) bloque ~173 s (TSO + ISPF chargent en fond).
    # Le keyboard se déverrouille quand TSO affiche le 1er écran de messages
    # (banner "***" welcome screen) — PAS après ISPF primary menu.
    # nc -w300 attend jusqu'à 300 s pour la réponse "ok".
    echo "  [s3270_login] attente init TSO/ISPF (3-4 min)..." >&2
    s3270_cmd "Enter()" 300 >/dev/null 2>&1 || true

    # Boucle post-logon : avancer à travers les écrans de messages ("***")
    # jusqu'à READY ou ISPF Primary Option Menu.
    #   - écran "***" (welcome, fortune cookie...) → keyboard unlock → Enter
    #   - "OPTION ==>" (ISPF panel) → sortir avec "=X" + Enter
    #   - "READY" → TSO READY, sortir
    local deadline=$(( SECONDS + 120 ))
    local scr
    while [[ $SECONDS -lt $deadline ]]; do
        scr=$(s3270_cmd "Ascii()" 10 2>/dev/null)

        # TSO READY
        if echo "$scr" | grep -qE "^\s*READY\s*$"; then
            break
        fi

        # ISPF : tout écran avec "OPTION ==>" → sortir avec =X
        if echo "$scr" | grep -qE "OPTION ==>"; then
            s3270_cmd "String(\"=X\")" 5 >/dev/null 2>&1 || true
            s3270_cmd "Enter()" 60 >/dev/null 2>&1 || true
            sleep 2
            continue
        fi

        # Écran intermédiaire (messages, welcome, ***) → Enter pour avancer
        s3270_cmd "Enter()" 10 >/dev/null 2>&1 || true

        # Attendre la prochaine mise à jour écran
        s3270_cmd "Wait(8,Output)" 12 >/dev/null 2>&1 || true
    done
    sleep 0.5
}

# URL de la console HTTP Hercules (pour CANCEL TSO)
HERC_URL="${HERC_URL:-http://localhost:8038}"

# ---- Annuler la session TSO via la console Hercules ----
# Utilisé par s3270_stop() pour éviter les sessions zombies :
#   - LOGOFF interactif est peu fiable (cursor, keyboard lock, ISPF)
#   - CANCEL U=user via l'API Hercules est toujours disponible et fiable
_s3270_cancel_tso() {
    local user="${TSO_USER:-HERC02}"
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
        --data-urlencode "command=/CANCEL U=${user}" \
        --data "norefresh=1" --data "msgcount=3" >/dev/null 2>&1 || true
    sleep 1   # laisser MVS traiter le cancel avant de tuer s3270
}

# ---- Arrêter la session s3270 ----
# Déconnecte s3270 ET annule la session TSO via la console Hercules.
# Raison : LOGOFF interactif est non-fiable depuis ISPF / écrans "***".
# CANCEL U=user via l'API HTTP est toujours fiable.
s3270_stop() {
    # Tenter un Disconnect propre
    s3270_cmd "Disconnect()" 5 >/dev/null 2>&1 || true
    if [[ -n "${_S3270_PID:-}" ]]; then
        kill "${_S3270_PID}" 2>/dev/null || true
        wait "${_S3270_PID}" 2>/dev/null || true
        _S3270_PID=""
    fi
    _s3270_kill_dummies
    _S3270_PORT=""
    # Annuler la session TSO MVS (évite les sessions zombies "IN USE")
    _s3270_cancel_tso
}
