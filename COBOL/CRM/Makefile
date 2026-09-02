# ============================================================
# Makefile — CRM migration Java 8
# Auteur : Sebastien Cotillard
# Date   : 2026-09-02
#
# Cibles :
#   make test        — lance les 174+ tests JUnit
#   make build       — compile + package les 4 JARs
#   make check-cobol — verification syntaxe COBOL locale (GnuCOBOL)
#   make ci          — pipeline complet (check + test + build)
#   make ci-mvs      — pipeline complet + upload/compile TK5
#   make clean       — supprime target/
# ============================================================

JAVA_DIR  := crm-java
SCRIPTS   := scripts

.PHONY: test build check-cobol ci ci-mvs clean

test:
	@echo "==> Tests Java (mvn test)"
	cd $(JAVA_DIR) && mvn test

build:
	@echo "==> Build JARs (mvn package)"
	cd $(JAVA_DIR) && mvn package -DskipTests

check-cobol:
	@echo "==> Verification syntaxe COBOL"
	bash $(SCRIPTS)/01_cobc_check.sh

ci: check-cobol test build
	@echo ""
	@echo "=============================="
	@echo " CI CRM : OK"
	@echo "=============================="

ci-mvs: ci
	@echo "==> Upload + compile TK5"
	bash $(SCRIPTS)/mvs/09_ci.sh

clean:
	cd $(JAVA_DIR) && mvn clean
