# ============================================================
# Makefile CI/CD TK5 — Pipeline GSTK pour MVS TK5 / KICKS
# Lancer depuis COBOL/CI_CD_TK5/
# ============================================================
# Targets locaux :
#   make check      → syntaxe COBOL + tests SQL PostgreSQL
#   make pg-reset   → recréer la base PostgreSQL de zéro
#
# Targets MVS TK5 :
#   make upload     → uploader les sources modifiés vers MVS
#   make bms        → assembler les 8 mapsets BMS
#   make cobol      → compiler les 8 programmes COBOL
#   make deploy     → CICS newcopy (recharger les modules)
#   make build      → upload + bms + cobol + deploy
#   make test       → tests CICS automatisés via s3270
#
# Pipeline complet :
#   make ci         → check + build + test
#   make watch      → surveillance fichiers → build auto
#
# Utilitaires :
#   make log        → syslog MVS en temps réel
#   make status     → état Hercules + CICS
#   make spool      → activité JES2 récente
# ============================================================

# Scripts locaux GSTK (PostgreSQL, syntaxe COBOL)
GSTK_SCRIPTS := ../GSTK/scripts
# Scripts MVS/TK5 (dans CI_CD_TK5/)
MVS          := mvs
SHELL        := /usr/bin/env bash

.PHONY: check pg-reset pg-setup upload upload-one bms cobol deploy build \
        test test-one ci inc watch log status spool spool-read install-all clean help

# ---- Local ---------------------------------------------------------------
check:
	@echo "=== [1/2] Syntaxe COBOL ==="
	@bash $(GSTK_SCRIPTS)/04_cobc_check.sh
	@echo ""
	@echo "=== [2/2] Tests SQL ==="
	@bash $(GSTK_SCRIPTS)/05_test_sql.sh

pg-reset:
	@bash $(GSTK_SCRIPTS)/03_pg_setup.sh --reset

pg-setup:
	@bash $(GSTK_SCRIPTS)/03_pg_setup.sh

# ---- MVS TK5 -------------------------------------------------------------
upload:
	@bash $(MVS)/01_upload.sh all

upload-one:
	@bash $(MVS)/01_upload.sh $(PROG)

bms:
	@bash $(MVS)/02_submit.sh bms

cobol:
	@bash $(MVS)/02_submit.sh cobol

deploy:
	@bash $(MVS)/03_cics.sh newcopy

build: upload bms cobol deploy
	@echo "=== Build complet terminé ==="

# ---- Tests CICS ----------------------------------------------------------
test:
	@bash $(MVS)/08_test_cics.sh all

test-one:
	@bash $(MVS)/08_test_cics.sh trans $(TRANS)

# ---- Pipeline CI/CD -------------------------------------------------------
ci:
	@bash $(MVS)/09_ci.sh

# ---- Build incrémental (détecte les fichiers modifiés) -------------------
inc:
	@bash $(MVS)/06_build.sh

# ---- Surveillance fichiers ------------------------------------------------
watch:
	@bash $(MVS)/07_watch.sh

# ---- Utilitaires ----------------------------------------------------------
log:
	@bash $(MVS)/herc.sh watch

status:
	@bash $(MVS)/herc.sh status
	@echo ""
	@bash $(MVS)/03_cics.sh status

spool:
	@bash $(MVS)/herc.sh spool

spool-read:
	@bash $(MVS)/10_spool_reader.sh $(JOB)

# ---- Première installation complète --------------------------------------
install-all:
	@echo "=== Installation complète GSTK sur MVS TK5 ==="
	@echo ""
	@echo "Étape 1/6 : Setup PostgreSQL local"
	@bash $(GSTK_SCRIPTS)/03_pg_setup.sh
	@echo ""
	@echo "Étape 2/6 : Vérification syntaxe COBOL"
	@bash $(GSTK_SCRIPTS)/04_cobc_check.sh
	@echo ""
	@echo "Étape 3/6 : Allocation des datasets MVS (1ère fois)"
	@bash $(MVS)/02_submit.sh alloc
	@echo ""
	@echo "Étape 4/6 : Upload vers MVS"
	@bash $(MVS)/01_upload.sh all
	@echo ""
	@echo "Étape 5/6 : Compilation BMS + COBOL"
	@bash $(MVS)/02_submit.sh bms
	@bash $(MVS)/02_submit.sh cobol
	@echo ""
	@echo "Étape 6/6 : Définitions CICS"
	@bash $(MVS)/03_cics.sh install
	@echo ""
	@echo "=== Installation terminée ==="
	@echo "Taper G000 dans x3270 pour démarrer."

clean:
	@rm -f mvs/.checksums mvs/.build.log 2>/dev/null || true
	@echo "Checksums nettoyés (prochain build = upload complet)"

help:
	@grep -E '^[a-zA-Z_-]+:' Makefile | grep -v "^#" | awk -F: '{print "  make " $$1}' | sort
