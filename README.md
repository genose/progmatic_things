# progmatic_things

```text
╔══════════════════════════════════════════════════════════════╗
║                        genose.org                            ║
║                    Sebastien Cotillard                       ║
║          ── compilation of works and R&D ──                  ║
╚══════════════════════════════════════════════════════════════╝
```

[![CI](https://github.com/genose/progmatic_things/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/genose/progmatic_things/actions/workflows/ci.yml)
[![GnuCOBOL](https://img.shields.io/badge/GnuCOBOL-3.2-success?style=flat-square)](https://gnucobol.sourceforge.io/)
[![Java](https://img.shields.io/badge/Java-8%20%2F%20JEE%20%2F%20Spring-red?style=flat-square)](JAVA/)
[![TypeScript](https://img.shields.io/badge/TypeScript-Angular-blue?style=flat-square)](TYPESCRIPT/)
[![Delphi](https://img.shields.io/badge/Delphi-Pascal-orange?style=flat-square)](Delphi/)
[![.NET](https://img.shields.io/badge/.NET-8.x%20C%23-purple?style=flat-square)](C.Sharp.Net/)
[![MVS TK5](https://img.shields.io/badge/MVS-TK5%20%2F%20Hercules-blue?style=flat-square)](COBOL/)

Multi-language R&D repository — mainframe to web, batch to real-time.

---

## Projects

| Domain | Directory | Stack |
| ------ | --------- | ----- |
| Mainframe COBOL + CICS CI/CD | [COBOL/](COBOL/README.md) | GnuCOBOL · MVS TK5 · KICKS · CICS/VS · Java 8 |
| Java — Spring, JEE, Android, JavaFX | [JAVA/](JAVA/) | Java 8/11 · Spring Boot · Android · JavaFX |
| TypeScript — Angular | [TYPESCRIPT/](TYPESCRIPT/) | Angular · TypeScript |
| JavaScript — Games & Graphics | [JAVACRIPT/](JAVACRIPT/) | Canvas · Web Audio · Game loop |
| C# .NET — Hotel Management | [C.Sharp.Net/](C.Sharp.Net/) | .NET 8 · C# |
| Delphi / Pascal — Utilities & Tools | [Delphi/](Delphi/) | Delphi · Pascal · SQL · REST |
| Web Bluetooth POC | [POCWEBBluetooth/](POCWEBBluetooth/) | Web Bluetooth API · JS |

---

## Highlights

### COBOL — Mainframe & Migration

> GnuCOBOL exercises · CICS/COBOL stock management on MVS TK5 · COBOL → Java 8 TDD migration

- **GSTK** — 8 CICS programs, 8 BMS mapsets (3270), DB2 SQL, COMMAREA 263 bytes
- **CRM migration** — 4 OpenVMS COBOL programs → Java 8 ports & adapters · 174 tests · 0 failure
- **CI_CD_TK5** — generic CI/CD pipeline (Bash · Docker · s3270) for MVS 3.8j, macOS/Linux/WSL

```bash
make build              # upload + compile + CICS newcopy
make ci                 # full 5-stage pipeline
make watch              # hot-reload on save
```

→ [COBOL/README.md](COBOL/README.md)

---

### Java

| Sub-project | Description |
| ----------- | ----------- |
| `JavaProjects/` | Intro exercises, Spring Boot 101, boot-serving-static-pages |
| `prj_jee/` | JEE 101–106, Android 101, JavaFX 3/12, OnyxFx |
| `Helisius.io` | Server-side Java · Servers |

---

### TypeScript — Angular

| Sub-project | Description |
| ----------- | ----------- |
| `Angular_Projects/Angular_101–104` | Progressive Angular learning |
| `Angular_Projects/Angular_OVS` | Angular production project |
| `projet101` | TypeScript POC |

---

### Delphi / Pascal

| Sub-project | Description |
| ----------- | ----------- |
| `CSVMappingUtils` | CSV ↔ database mapping utility |
| `EEG_BSI_Project` | EEG / biometric signal integration |
| `UGns_SQLConnection` | Generic SQL connection layer |
| `U_Integration_CSV_SGBD_INSEE` | INSEE data import pipeline |
| `WSRESTClient` | REST client |
| `uHashList.pas` | Hash list data structure |
| `scetlinkdb` | Database link tooling |

---

## Tooling baseline

- Node.js 22.23.1 — see [.nvmrc](.nvmrc)
- npm 10.9.8
- .NET SDK 8.x — builds [C.Sharp.Net/progmatic_things_CSharp.sln](C.Sharp.Net/progmatic_things_CSharp.sln)
- GnuCOBOL 3.2.x — see [COBOL/](COBOL/)
- Java 8+ / Maven — see [COBOL/CRM/](COBOL/CRM/README.md) and [JAVA/](JAVA/)

---

genose.org · Sebastien Cotillard · progmatic_things
