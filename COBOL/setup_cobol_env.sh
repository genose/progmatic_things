#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# COBOL workspace environment — sourced by .vscode/tasks.json
# ─────────────────────────────────────────────────────────────────────────────

# ── Compiler (MacPorts GnuCOBOL 3.2) ─────────────────────────────────────────
export COBC="/opt/local/bin/cobc"
export COB_CC="/usr/bin/clang"

# ── GnuCOBOL runtime paths ────────────────────────────────────────────────────
export COB_CONFIG_DIR="/opt/local/share/gnucobol/config"
export COB_COPY_DIR="/opt/local/share/gnucobol/copy"

# ── Project copybook search path (colon-separated) ───────────────────────────
WS="${WORKSPACE_FOLDER:-$(pwd)}"
export COB_COPY_DIR="${WS}/GSTK:${WS}/CRM:${COB_COPY_DIR}"

# ── Standard library paths (MacPorts) ────────────────────────────────────────
export COB_CFLAGS="-I/opt/local/include"
export COB_LDFLAGS="-L/opt/local/lib"

# ── PostgreSQL (DB2 substitute for local SQL dev) ─────────────────────────────
export PGHOST="localhost"
export PGPORT="5432"
export PGDATABASE="gstk"
export PGUSER="$(whoami)"

# ── Debugger ──────────────────────────────────────────────────────────────────
export GDB="/usr/local/bin/gdb"
