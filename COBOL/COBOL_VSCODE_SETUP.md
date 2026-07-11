# COBOL Setup for VS Code (macOS / Linux / Windows)

This document describes how to build and run COBOL programs from VS Code in this repository on all major platforms.

## Project context

- COBOL files are independent console programs.
- Executables are generated in `bin/`.
- The default flow is: compile active file, then run it.

## 1) VS Code extensions requirements

### Required

- `bitlang.cobol`
  - COBOL language support (syntax, snippets, navigation).

### Required for Run/Debug button with current `launch.json`

- `ms-vscode.cpptools`
  - Provides `cppdbg`, used by `.vscode/launch.json`.

### Optional but useful

- `timonwong.shellcheck`
  - Helps maintain shell scripts such as `setup_cobol_env.sh`.

## 2) Install GnuCOBOL per OS

### macOS

```bash
brew install gnucobol
```

### Linux

Debian/Ubuntu:

```bash
sudo apt update
sudo apt install -y gnucobol
```

Fedora:

```bash
sudo dnf install -y gnucobol
```

Arch:

```bash
sudo pacman -S gnucobol
```

### Windows

Use one of these approaches:

1. WSL (recommended for Unix-like parity)
   - Install WSL + a Linux distro.
   - Open this project in VS Code using Remote - WSL.
   - Follow Linux steps above.

2. Native Windows toolchain
   - Install a native `cobc` (for example via MSYS2/MinGW distribution that provides GnuCOBOL).
   - Ensure `cobc` is available in `PATH`.
   - This repository now includes Windows task overrides using PowerShell.

## 3) Verify compiler installation

On macOS/Linux/WSL:

```bash
cobc -V
```

If `cobc` is not found, fix your PATH or reinstall GnuCOBOL.

## 4) Environment bootstrap used by this repository

The script `setup_cobol_env.sh` is currently optimized for macOS toolchain stability:

- Prefers Homebrew binary location in PATH.
- Sets `COBC=/usr/local/bin/cobc`.
- Forces `COB_CC=/usr/bin/clang`.

Use it before manual builds:

```bash
source ./setup_cobol_env.sh
```

Why this exists:

On some macOS setups, `cobc` may pick a non-system `clang` and fail on standard headers. Forcing `/usr/bin/clang` avoids that.

## 5) Bootstrap script for quick setup

This repository also includes:

- `bootstrap_cobol_vscode.sh`

Purpose:

1. Detect OS (macOS/Linux/Windows shell environments).
2. Check `cobc` availability.
3. Apply safe macOS environment defaults (`COB_CC`, `COBC`).
4. Ensure `bin/` exists.
5. Optionally install required VS Code extensions.

Usage:

```bash
./bootstrap_cobol_vscode.sh
```

Install required extensions automatically:

```bash
./bootstrap_cobol_vscode.sh --install-extensions
```

Show help:

```bash
./bootstrap_cobol_vscode.sh --help
```

## 6) VS Code configuration included in this repo

### Files

- `.vscode/tasks.json`
- `.vscode/launch.json`

### Build task: current file

Task label: `COBOL: Compile current file`

Behavior:

1. Sources `setup_cobol_env.sh`.
2. Creates `bin/` if missing.
3. Compiles active file to `bin/${fileBasenameNoExtension}`.

### Build task: all files

Task label: `COBOL: Compile all files`

Behavior:

1. Sources `setup_cobol_env.sh`.
2. Creates `bin/`.
3. Compiles all `*.cbl` and `*.CBL` to `bin/`.

### Run configuration

Launch name: `Run Current COBOL File`

Behavior:

1. Runs pre-launch task `COBOL: Compile current file`.
2. Runs `bin/${fileBasenameNoExtension}`.

## 7) Platform compatibility note for tasks and launch

Current tasks/launch are directly usable on:

- macOS
- Linux
- Windows via WSL
- Windows native (PowerShell tasks + dedicated launch config)

Notes for native Windows:

- Build outputs are generated as `bin/<program>.exe`.
- Use launch config `Run Current COBOL File (Windows)`.
- `setup_cobol_env.sh` is not used by Windows tasks.

## 8) Manual compile/run (portable pattern)

On macOS/Linux/WSL:

```bash
source ./setup_cobol_env.sh
mkdir -p ./bin
"$COBC" -x -o ./bin/COBOL_22 ./COBOL_22.cbl
./bin/COBOL_22
```

If `COBC` is not exported in your environment, use:

```bash
cobc -x -o ./bin/COBOL_22 ./COBOL_22.cbl
```

## 9) Runtime input files

Some programs require input files in the working directory.

Example:

- `COBOL_22.cbl` expects `VENTES-LOGIQUE`.

Minimal sample:

```text
2026071000125
2026071000075
2026071100200
```

## 10) Troubleshooting

### Missing header errors on macOS

Symptoms may include errors around `string.h` or `machine/setjmp.h`.

Check:

```bash
source ./setup_cobol_env.sh
echo "$COB_CC"
```

Expected:

```text
/usr/bin/clang
```

### Build succeeds but runtime says file not found

Ensure required input files for the target program are present in the current working directory.

### VS Code run button fails with debugger errors

Confirm `ms-vscode.cpptools` is installed and enabled in the environment where VS Code is running (local host or WSL remote).

## 11) README snippet

```md
## COBOL Development Setup

See [COBOL_VSCODE_SETUP.md](./COBOL_VSCODE_SETUP.md) for macOS/Linux/Windows VS Code + GnuCOBOL setup instructions.
```
