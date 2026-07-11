#!/usr/bin/env bash
set -euo pipefail

# Prefer Homebrew GnuCOBOL over other installations.
export PATH="/usr/local/bin:$PATH"

# Force Apple clang to avoid header issues with non-standard clang binaries.
export COB_CC="/usr/bin/clang"
export COBC="/usr/local/bin/cobc"

if ! command -v "$COBC" >/dev/null 2>&1; then
  echo "Error: cobc not found at $COBC"
  echo "Install with: brew install gnucobol"
  exit 1
fi

echo "COBOL environment ready"
echo "cobc: $($COBC -V | head -n 1)"
echo "COB_CC: $COB_CC"
