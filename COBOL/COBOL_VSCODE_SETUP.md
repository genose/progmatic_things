# COBOL Setup for VS Code (macOS)

This document summarizes the complete configuration used in this repository to compile and run COBOL programs with the VS Code Run button.

## Project context

- All COBOL files are independent console programs.
- All executables are built into a shared output directory: `bin/`.
- The active file in VS Code is compiled and then executed.

## 1) Prerequisites

Install GnuCOBOL with Homebrew:

```bash
brew install gnucobol
```

Check installation:

```bash
/usr/local/bin/cobc -V
```

## 2) Environment bootstrap script

The project uses `setup_cobol_env.sh` to force a stable compiler toolchain on macOS.

```bash
source ./setup_cobol_env.sh
```

What it sets:

- `PATH` preferring Homebrew binaries
- `COBC=/usr/local/bin/cobc`
- `COB_CC=/usr/bin/clang`

Why this matters:

On this machine, `cobc` could pick a non-standard `clang` from PATH and fail with missing C headers. Forcing `COB_CC=/usr/bin/clang` fixes it.

## 3) VS Code configuration

The repository includes:

- `.vscode/tasks.json`
- `.vscode/launch.json`

### Build task (current file)

Task label: `COBOL: Compile current file`

Behavior:

1. Loads `setup_cobol_env.sh`
2. Ensures `bin/` exists
3. Compiles the active file to `bin/${fileBasenameNoExtension}`

### Build task (all files)

Task label: `COBOL: Compile all files`

Behavior:

1. Loads `setup_cobol_env.sh`
2. Ensures `bin/` exists
3. Compiles all `*.cbl` and `*.CBL` files into `bin/`

### Run configuration

Launch name: `Run Current COBOL File`

Behavior:

1. Runs pre-launch task `COBOL: Compile current file`
2. Starts `bin/${fileBasenameNoExtension}` as a console program

## 4) Manual compile/run commands

Compile one file:

```bash
source ./setup_cobol_env.sh
mkdir -p ./bin
/usr/local/bin/cobc -x -o ./bin/COBOL_22 ./COBOL_22.cbl
```

Run:

```bash
./bin/COBOL_22
```

## 5) Input files for programs

Some programs require runtime input files.

Example: `COBOL_22.cbl` expects a file named `VENTES-LOGIQUE` in the working directory.

Minimal test sample:

```text
2026071000125
2026071000075
2026071100200
```

## 6) Validation performed

- `COBOL_22.cbl`: compile + run success
- `cobol_8.cbl`: compile + run success (interactive console program)

## 7) Troubleshooting

### Error: missing headers like `string.h` or `machine/setjmp.h`

Use the environment script and ensure Apple clang is selected:

```bash
source ./setup_cobol_env.sh
echo "$COB_CC"
```

Expected:

```text
/usr/bin/clang
```

### Program compiles but fails at runtime with file not found

Check required input files for that COBOL program and place them in the current working directory.

## 8) Recommended GitHub section

You can link this file from your main README:

```md
## COBOL Development Setup

See [COBOL_VSCODE_SETUP.md](./COBOL_VSCODE_SETUP.md) for full VS Code + GnuCOBOL setup and run instructions.
```
