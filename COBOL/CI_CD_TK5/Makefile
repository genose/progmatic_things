# ============================================================
# Makefile CI/CD TK5 — Pipeline générique pour MVS TK5 / KICKS
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Lancer depuis COBOL/CI_CD_TK5/
# Choisir le projet via : make <cible> PROJECT=gstk|crm|...
# ============================================================

# Projet par défaut
PROJECT ?= gstk
export PROJECT_NAME=$(PROJECT)

# Scripts MVS/TK5 (dans CI_CD_TK5/mvs/)
MVS          := mvs
SHELL        := /usr/bin/env bash

# Scripts locaux GSTK (PostgreSQL, syntaxe) — cibles spécifiques GSTK
# Ces cibles (pg-reset, pg-setup) pointent directement sur GSTK/scripts
# car elles sont intrinsèquement liées à PostgreSQL/GSTK.
GSTK_SCRIPTS := ../GSTK/scripts

.PHONY: check pg-reset pg-setup upload upload-one bms cobol deploy build \
        test test-one ci inc watch log status spool spool-read install-all \
        clean help projects

# ---- Local ---------------------------------------------------------------
check:
	@bash $(MVS)/09_ci.sh --no-mvs

# Cibles PostgreSQL spécifiques GSTK (ne dépendent pas de PROJECT)
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
	@echo "=== Build complet terminé (PROJECT=$(PROJECT)) ==="

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
	@echo "=== Installation complète $(PROJECT) sur MVS TK5 ==="
	@echo ""
	@echo "Étape 1/4 : Allocation des datasets MVS"
	@bash $(MVS)/02_submit.sh alloc
	@echo ""
	@echo "Étape 2/4 : Upload vers MVS"
	@bash $(MVS)/01_upload.sh all
	@echo ""
	@echo "Étape 3/4 : Compilation BMS + COBOL"
	@bash $(MVS)/02_submit.sh bms || true
	@bash $(MVS)/02_submit.sh cobol
	@echo ""
	@echo "Étape 4/4 : Définitions CICS"
	@bash $(MVS)/03_cics.sh install
	@echo ""
	@echo "=== Installation terminée (PROJECT=$(PROJECT)) ==="

clean:
	@rm -f mvs/.checksums_* mvs/.build.log 2>/dev/null || true
	@echo "Checksums nettoyés (prochain build = upload complet)"

# ---- Aide ----------------------------------------------------------------
projects:
	@echo "Projets disponibles :"
	@for f in conf/*.conf; do \
	    echo "  $$(basename $$f .conf)"; \
	done

help:
	@echo "Usage : make <cible> [PROJECT=gstk|crm|...]"
	@echo ""
	@echo "Cibles disponibles :"
	@echo "  check        — vérification syntaxe + tests unitaires (local)"
	@echo "  upload       — uploader les sources vers MVS"
	@echo "  bms          — assembler les mapsets BMS"
	@echo "  cobol        — compiler les programmes COBOL"
	@echo "  deploy       — CICS newcopy (recharger modules)"
	@echo "  build        — upload + bms + cobol + deploy"
	@echo "  test         — tests CICS automatisés"
	@echo "  test-one     — tester une transaction (TRANS=G000)"
	@echo "  ci           — pipeline CI/CD complet"
	@echo "  inc          — build incrémental (détection MD5)"
	@echo "  watch        — surveillance fichiers → build auto"
	@echo "  install-all  — première installation complète"
	@echo "  log          — syslog MVS en temps réel"
	@echo "  status       — état Hercules + CICS"
	@echo "  spool        — activité JES2 récente"
	@echo "  clean        — nettoyer les checksums"
	@echo "  projects     — lister les projets disponibles"
	@echo ""
	@echo "Projets disponibles (PROJECT=...) :"
	@for f in conf/*.conf; do \
	    echo "  $$(basename $$f .conf)"; \
	done
