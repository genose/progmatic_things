#!/usr/bin/env bash
# Wrapper : force l'interpreteur Homebrew (/usr/local/bin/python3) qui a openpyxl installe.
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec /usr/local/bin/python3 "$SCRIPT_DIR/extract_mapping.py" "$@"
