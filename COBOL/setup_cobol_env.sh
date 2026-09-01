#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# COBOL workspace environment — sourced by .vscode/tasks.json
# Compatible macOS (MacPorts / Homebrew), Linux (apt/dnf/pacman)
# Windows : sourcer depuis WSL bash uniquement
# ─────────────────────────────────────────────────────────────────────────────

case "$(uname -s)" in

  Darwin)
    if [[ -x /opt/local/bin/cobc ]]; then
      # MacPorts
      export COBC="/opt/local/bin/cobc"
      _COB_SYS_BASE="/opt/local"
      export COB_CFLAGS="-I/opt/local/include"
      export COB_LDFLAGS="-L/opt/local/lib"
    else
      # Homebrew fallback
      _BREW="$(brew --prefix gnucobol 2>/dev/null || echo /usr/local)"
      export COBC="${_BREW}/bin/cobc"
      _COB_SYS_BASE="$_BREW"
    fi
    export COB_CC="/usr/bin/clang"   # force Apple clang (évite erreurs headers)
    ;;

  Linux)
    export COBC="${COBC:-$(command -v cobc || echo cobc)}"
    _COB_SYS_BASE="/usr"
    export COB_CC="${CC:-gcc}"
    ;;

  MINGW*|MSYS*|CYGWIN*)
    # Windows natif via MSYS2/MinGW — GnuCOBOL doit être dans PATH
    export COBC="${COBC:-cobc}"
    _COB_SYS_BASE="/usr"
    export COB_CC="${CC:-gcc}"
    ;;

esac

# ── GnuCOBOL runtime paths ────────────────────────────────────────────────────
export COB_CONFIG_DIR="${_COB_SYS_BASE}/share/gnucobol/config"
_COB_COPY_SYS="${_COB_SYS_BASE}/share/gnucobol/copy"

# ── Project copybook search path (GSTK + CRM + système) ─────────────────────
WS="${WORKSPACE_FOLDER:-$(pwd)}"
export COB_COPY_DIR="${WS}/GSTK:${WS}/CRM:${_COB_COPY_SYS}"

# ── PostgreSQL (substitut DB2 pour les tests locaux) ─────────────────────────
export PGHOST="${PGHOST:-localhost}"
export PGPORT="${PGPORT:-5432}"
export PGDATABASE="${PGDATABASE:-gstk}"
export PGUSER="${PGUSER:-$(whoami)}"
