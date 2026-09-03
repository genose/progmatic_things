```text
╔══════════════════════════════════════════════════════════════╗
║                        genose.org                            ║
║              Sebastien Cotillard · progmatic_things          ║
║          ── compilation of works and R&D ──                  ║
╚══════════════════════════════════════════════════════════════╝
```

# JAVA

Projets Java — J2EE, JavaFX, Spring, REST API, Android, Kotlin, TDD.

---

## Lien COBOL → Java

Le dossier [COBOL/CRM/](../COBOL/CRM/) contient la migration complète de 4 programmes COBOL OpenVMS (Oracle Rdb) vers Java 8 en architecture **ports & adapters** avec TDD.

| Programme COBOL | Rôle | Projet Java |
| --------------- | ---- | ----------- |
| [`D05_VERIF_CRM.SCO`](../COBOL/CRM/D05_VERIF_CRM.SCO) | Synchro commandes DEPOT ↔ CRM | [`COBOL/CRM/crm-java/`](../COBOL/CRM/crm-java/) |
| [`T10_MAJ_DTLIVR_BDCRM.COB`](../COBOL/CRM/T10_MAJ_DTLIVR_BDCRM.COB) | Mise à jour dates livraison DTLIVR | [`COBOL/CRM/crm-java/`](../COBOL/CRM/crm-java/) |
| [`D02_EXTCDE_CRMCSP1.COB`](../COBOL/CRM/D02_EXTCDE_CRMCSP1.COB) | Génération fichiers confirmation CSP | [`COBOL/CRM/crm-java/`](../COBOL/CRM/crm-java/) |
| [`D05_INTCDEFAC_CRM_V2.SCO`](../COBOL/CRM/D05_INTCDEFAC_CRM_V2.SCO) | UPSERT CDEFAC → BD_CRM.S.CDE_FAC | [`COBOL/CRM/crm-java/`](../COBOL/CRM/crm-java/) |

**174 tests · 0 échec · Java 8 · Maven · architecture ports & adapters**

```bash
cd ../COBOL/CRM/crm-java
mvn test
```

→ [COBOL/CRM/README.md](../COBOL/CRM/README.md) — documentation complète  
→ [COBOL/CRM/COBOL_VERS_JAVA8_TDD.md](../COBOL/CRM/COBOL_VERS_JAVA8_TDD.md) — guide de migration

---

## prj_jee/

Projets Java EE / Web — Servlet, JSF, REST API, Spring Boot, JavaFX.

| Projet | Description | Stack |
| ------- | ----------- | ----- |
| `ProjectJ2EE_101` → `106` | Exercices Java EE progressifs | Servlet · JSP · JDBC |
| `ProjectJ2EE_JSF101` / `102` | Interfaces JSF | JSF 2.2 · PrimeFaces |
| `TestJSF` | Application JSF complète | JSF 2.2 · PrimeFaces 8 · MySQL |
| `ProjectJavaFX_101` / `102` | Intro JavaFX | JavaFX 11 |
| `ProjectJavaFX103` → `105` | JavaFX avancé — charts, JSON, API | JavaFX 11 · JSON |
| `JAVAFX12LOCALPRJ` | Projet local JavaFX 12 | JavaFX 12 |
| `RestApi` | API REST complète — JWT + bcrypt + Swagger | Jersey 2 · MySQL |
| `RestApiTEST` | Tests API REST | Jersey 2 · Swagger |
| `SDBMJEE` | Système de gestion base documentaire | Java EE |
| `demo` / `demo2` | Spring Boot démo | Spring Boot 3 |
| `gs-rest-service` | REST service Spring | Spring Boot |
| `org_genose_java_implementation` | Implémentation multi-modules genose.org | Java 11 · Maven |
| `ProjectSGBD_101` | Connexions SGBD | JDBC · MySQL · MSSQL |
| `securitytokeded` | Authentification JWT | Java · JWT |
| `Android101` | Application Android native | Kotlin · Android |
| `ProjectKotlin_101` | Intro Kotlin | Kotlin |

## JavaProjects/

Projets Java standalone — introduction et exercices fondamentaux.

| Projet | Description |
| ------- | ----------- |
| `JavaIntro` | Fondamentaux Java |
| `Animaux` | POO — héritage, polymorphisme |
| `CandyCount` | Algorithmes de comptage |
| `NoticeMe` | Patterns observateur |
| `java_spring_101` | Introduction Spring |
| `boot-serving-static-pages` | Spring Boot — pages statiques |

---

genose.org · Sebastien Cotillard · progmatic_things
