#!/usr/bin/env bash
# ============================================================
# 02_compile.sh — Compilation COBOL CRM sur MVS TK5 (batch IBM)
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Soumet le JCL de compilation via le lecteur de cartes Hercules
# puis surveille la fin du job dans le syslog JES2.
#
# Prérequis :
#   - Container Docker mvs-tk5 démarré
#   - Sources déjà uploadées (bash 01_upload.sh)
#   - Dataset ${HLQ}.CRM.SOURCE alloué (bash 02_compile.sh alloc)
#
# Usage :
#   bash scripts/mvs/02_compile.sh           # compiler les 4 programmes
#   bash scripts/mvs/02_compile.sh alloc     # allouer les datasets (1ère fois)
#   bash scripts/mvs/02_compile.sh spool     # afficher le syslog récent
# ============================================================
set -euo pipefail

CRM_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"
HERC_URL="${HERC_URL:-http://localhost:8038}"
_DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
_CARDREADER_PORT="${CARDREADER_PORT:-3505}"
COBHLQ="${COBHLQ:-IGY}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()   { echo -e "  ${GREEN}OK${NC}   $*"; }
fail() { echo -e "  ${RED}FAIL${NC} $*"; }
warn() { echo -e "  ${YELLOW}WARN${NC} $*"; }
info() { echo -e "  ${CYAN}INFO${NC} $*"; }

# ============================================================
# Envoyer du JCL inline au lecteur de cartes
# ============================================================
submit_cardreader() {
    local jobname="$1"
    local jcl_content="$2"

    if ! docker inspect "${_DOCKER_CONTAINER}" >/dev/null 2>&1; then
        fail "Container ${_DOCKER_CONTAINER} non accessible"
        return 1
    fi

    info "Soumission ${jobname} via lecteur de cartes..."
    echo "${jcl_content}" \
        | docker exec -i "${_DOCKER_CONTAINER}" bash -c \
            "exec 3<>/dev/tcp/127.0.0.1/${_CARDREADER_PORT}; cat >&3; exec 3>&-" \
        2>/dev/null \
        && ok "JCL envoye : ${jobname}" \
        || { fail "Echec envoi JCL (${jobname})"; return 1; }
}

# ============================================================
# Attendre HASP395 dans le syslog JES2
# ============================================================
wait_job() {
    local jobname="$1"
    local maxwait="${2:-300}"
    local elapsed=0

    sleep 3
    printf "  Attente fin de %-10s " "${jobname}"
    while [[ $elapsed -lt $maxwait ]]; do
        sleep 5; elapsed=$(( elapsed + 5 )); printf "."
        local sl
        sl=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=80" \
            | sed 's/<[^>]*>//g' 2>/dev/null || true)
        if echo "$sl" | grep -qi "HASP395.*${jobname}\|HASP396.*${jobname}"; then
            echo " termine (${elapsed}s)"
            if echo "$sl" | grep -qi "ABEND.*${jobname}\|IEF452I.*${jobname}"; then
                fail "${jobname} termine en erreur — verifier le spool"
                return 1
            fi
            ok "${jobname} RC=0"
            return 0
        fi
    done
    echo " timeout (${maxwait}s)"
    warn "Verifier : bash scripts/mvs/02_compile.sh spool"
    return 1
}

show_spool() {
    echo "--- Syslog MVS recent ---"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=40" \
        | sed 's/<[^>]*>//g' | grep -v "^Command:\|^Only\|^Refresh\|^$" | tail -30
}

# ============================================================
# JCL d'allocation des datasets CRM
# ============================================================
jcl_alloc() {
cat <<JCL
//CRMALLOC JOB ,'CRM ALLOC',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//ALLOC   EXEC PGM=IEFBR14
//SOURCE  DD DSN=${HLQ}.CRM.SOURCE,
//           DISP=(NEW,CATLG,DELETE),
//           UNIT=3390,SPACE=(TRK,(50,10,20)),
//           DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
//LOADLIB DD DSN=${HLQ}.CRM.LOADLIB,
//           DISP=(NEW,CATLG,DELETE),
//           UNIT=3390,SPACE=(TRK,(100,20)),
//           DCB=(RECFM=U,BLKSIZE=32760,DSORG=PO)
//JCL     DD DSN=${HLQ}.CRM.JCL,
//           DISP=(NEW,CATLG,DELETE),
//           UNIT=3390,SPACE=(TRK,(10,5,10)),
//           DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
JCL
}

# ============================================================
# JCL de compilation IBM COBOL (IGYCRCTL) — un STEP par source
# ============================================================
jcl_compile() {
cat <<JCL
//CRMCOMP JOB ,'CRM COMPILE',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//*-------------------------------------------------------------
//* Compilation D05_VERIF_CRM (source OpenVMS — NODYNAM)
//*-------------------------------------------------------------
//D05VERIF EXEC PGM=IGYCRCTL,
//         PARM='NODYNAM,NOLIST,RENT,OBJECT,APOST'
//STEPLIB  DD DSN=${COBHLQ}.SIGYCOMP,DISP=SHR
//SYSLIB   DD DSN=${HLQ}.CRM.SOURCE,DISP=SHR
//SYSLIN   DD DSN=&&OBJD05V,DISP=(NEW,PASS),
//            UNIT=SYSDA,SPACE=(TRK,(5,2))
//SYSIN    DD DSN=${HLQ}.CRM.SOURCE(D05VERIF),DISP=SHR
//SYSPRINT DD SYSOUT=A
//SYSTERM  DD SYSOUT=A
//SYSUDUMP DD SYSOUT=A
//*-------------------------------------------------------------
//* Compilation T10_MAJ_DTLIVR_BDCRM
//*-------------------------------------------------------------
//T10MAJDT EXEC PGM=IGYCRCTL,
//         PARM='NODYNAM,NOLIST,RENT,OBJECT,APOST'
//STEPLIB  DD DSN=${COBHLQ}.SIGYCOMP,DISP=SHR
//SYSLIB   DD DSN=${HLQ}.CRM.SOURCE,DISP=SHR
//SYSLIN   DD DSN=&&OBJT10,DISP=(NEW,PASS),
//            UNIT=SYSDA,SPACE=(TRK,(5,2))
//SYSIN    DD DSN=${HLQ}.CRM.SOURCE(T10MAJDT),DISP=SHR
//SYSPRINT DD SYSOUT=A
//SYSTERM  DD SYSOUT=A
//SYSUDUMP DD SYSOUT=A
//*-------------------------------------------------------------
//* Compilation D02_EXTCDE_CRMCSP1
//*-------------------------------------------------------------
//D02EXTCD EXEC PGM=IGYCRCTL,
//         PARM='NODYNAM,NOLIST,RENT,OBJECT,APOST'
//STEPLIB  DD DSN=${COBHLQ}.SIGYCOMP,DISP=SHR
//SYSLIB   DD DSN=${HLQ}.CRM.SOURCE,DISP=SHR
//SYSLIN   DD DSN=&&OBJD02,DISP=(NEW,PASS),
//            UNIT=SYSDA,SPACE=(TRK,(5,2))
//SYSIN    DD DSN=${HLQ}.CRM.SOURCE(D02EXTCD),DISP=SHR
//SYSPRINT DD SYSOUT=A
//SYSTERM  DD SYSOUT=A
//SYSUDUMP DD SYSOUT=A
//*-------------------------------------------------------------
//* Compilation D05_INTCDEFAC_CRM_V2
//*-------------------------------------------------------------
//D05INTCD EXEC PGM=IGYCRCTL,
//         PARM='NODYNAM,NOLIST,RENT,OBJECT,APOST'
//STEPLIB  DD DSN=${COBHLQ}.SIGYCOMP,DISP=SHR
//SYSLIB   DD DSN=${HLQ}.CRM.SOURCE,DISP=SHR
//SYSLIN   DD DSN=&&OBJD05I,DISP=(NEW,PASS),
//            UNIT=SYSDA,SPACE=(TRK,(5,2))
//SYSIN    DD DSN=${HLQ}.CRM.SOURCE(D05INTCD),DISP=SHR
//SYSPRINT DD SYSOUT=A
//SYSTERM  DD SYSOUT=A
//SYSUDUMP DD SYSOUT=A
JCL
}

# ============================================================
# Main
# ============================================================
echo "=== Compilation COBOL CRM — MVS TK5 ==="
echo "HLQ    : ${HLQ}  /  User : ${TSO_USER}"
echo "COBHLQ : ${COBHLQ}"
echo ""

docker inspect "${_DOCKER_CONTAINER}" >/dev/null 2>&1 \
    || { echo -e "${RED}Container ${_DOCKER_CONTAINER} non accessible${NC}"; exit 1; }

ACTION="${1:-compile}"

case "$ACTION" in
    alloc)
        echo "--- Allocation datasets CRM ---"
        submit_cardreader "CRMALLOC" "$(jcl_alloc)"
        wait_job "CRMALLOC" 120
        show_spool
        ok "Datasets alloues. Uploader les sources :"
        echo "   bash scripts/mvs/01_upload.sh"
        ;;

    compile|all)
        echo "--- Compilation des 4 sources COBOL ---"
        submit_cardreader "CRMCOMP" "$(jcl_compile)"
        wait_job "CRMCOMP" 600
        show_spool
        ;;

    spool)
        show_spool
        ;;

    *)
        echo "Usage: $0 [alloc|compile|spool]"
        echo ""
        echo "Ordre recommande (premiere installation) :"
        echo "  1. $0 alloc          # creer les datasets MVS"
        echo "  2. bash 01_upload.sh # uploader les sources"
        echo "  3. $0 compile        # compiler les 4 programmes"
        ;;
esac
