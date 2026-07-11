#!/usr/bin/env bash
set -euo pipefail

# Bootstrap helper for COBOL VS Code setup across macOS/Linux/Windows (Git Bash/WSL).
# It validates toolchain availability, prepares bin/, and optionally installs VS Code extensions.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_DIR="$SCRIPT_DIR"
BIN_DIR="$WORKSPACE_DIR/bin"
INSTALL_EXTENSIONS=false

REQUIRED_EXTENSIONS=(
  "bitlang.cobol"
  "ms-vscode.cpptools"
)

usage() {
  cat <<'EOF'
Usage: ./bootstrap_cobol_vscode.sh [options]

Options:
  --install-extensions   Install required VS Code extensions (if 'code' CLI is available)
  -h, --help             Show this help

What this script does:
  1) Detects OS and validates COBOL toolchain presence
  2) Applies macOS compiler safety variables (COB_CC/COBC)
  3) Creates ./bin output directory
  4) Optionally installs required VS Code extensions

Note:
  - On Windows native, run this script from Git Bash or WSL.
  - For PowerShell-native tasks, ensure 'cobc' is in PATH.
EOF
}

log() {
  printf "[bootstrap] %s\n" "$*"
}

warn() {
  printf "[bootstrap][warn] %s\n" "$*" >&2
}

fail() {
  printf "[bootstrap][error] %s\n" "$*" >&2
  exit 1
}

detect_os() {
  local uname_out
  uname_out="$(uname -s || true)"
  case "$uname_out" in
    Darwin*) echo "macos" ;;
    Linux*) echo "linux" ;;
    MINGW*|MSYS*|CYGWIN*) echo "windows" ;;
    *) echo "unknown" ;;
  esac
}

ensure_cobc() {
  if command -v cobc >/dev/null 2>&1; then
    echo "$(command -v cobc)"
    return 0
  fi
  return 1
}

configure_env_for_os() {
  local os="$1"
  case "$os" in
    macos)
      export PATH="/usr/local/bin:$PATH"
      export COB_CC="/usr/bin/clang"
      if [[ -x "/usr/local/bin/cobc" ]]; then
        export COBC="/usr/local/bin/cobc"
      else
        export COBC="$(command -v cobc)"
      fi
      ;;
    linux)
      export COBC="$(command -v cobc)"
      ;;
    windows)
      export COBC="$(command -v cobc)"
      ;;
    *)
      fail "Unsupported OS. Please run setup manually."
      ;;
  esac
}

print_install_hint() {
  local os="$1"
  case "$os" in
    macos)
      warn "Install GnuCOBOL with: brew install gnucobol"
      ;;
    linux)
      warn "Install GnuCOBOL with your distro package manager (apt/dnf/pacman)."
      ;;
    windows)
      warn "Install GnuCOBOL via MSYS2/MinGW or use WSL, then ensure 'cobc' is in PATH."
      ;;
  esac
}

install_extensions() {
  if ! command -v code >/dev/null 2>&1; then
    warn "VS Code CLI 'code' not found; skipping extension install."
    warn "Install manually: ${REQUIRED_EXTENSIONS[*]}"
    return 0
  fi

  local installed
  installed="$(code --list-extensions || true)"

  for ext in "${REQUIRED_EXTENSIONS[@]}"; do
    if grep -qi "^${ext}$" <<<"$installed"; then
      log "Extension already installed: $ext"
    else
      log "Installing extension: $ext"
      code --install-extension "$ext"
    fi
  done
}

main() {
  while (($#)); do
    case "$1" in
      --install-extensions)
        INSTALL_EXTENSIONS=true
        ;;
      -h|--help)
        usage
        exit 0
        ;;
      *)
        fail "Unknown option: $1"
        ;;
    esac
    shift
  done

  local os
  os="$(detect_os)"
  log "Detected OS: $os"

  if ! ensure_cobc >/dev/null; then
    print_install_hint "$os"
    fail "'cobc' not found in PATH."
  fi

  configure_env_for_os "$os"

  mkdir -p "$BIN_DIR"
  log "Output directory ready: $BIN_DIR"

  log "Using COBOL compiler: $COBC"
  "$COBC" -V | head -n 1

  if [[ "$os" == "macos" ]]; then
    log "COB_CC=$COB_CC"
  fi

  if [[ "$INSTALL_EXTENSIONS" == "true" ]]; then
    install_extensions
  else
    log "Skipping extension installation (use --install-extensions to enable)."
  fi

  log "Bootstrap complete. You can now run VS Code task: 'COBOL: Compile current file'."
}

main "$@"
