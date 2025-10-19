# EEG BSI Project
**EEG / BSI Analysis System — Julia DLL + Delphi GUI**

This repository implements a biomedical signal analysis tool for EEG data, with:
- Signal processing & feature extraction in **Julia** (exported as DLL)
- Medical metrics: left/right asymmetry, suppression ratio, amplitude balance
- Delphi IHM for real-time visualization and control
- Full compatibility with OpenBCI / EDF+ datasets

## Features
- Band Power (Δ, Θ, α, β, γ)
- Asymmetry Index (Right–Left hemispheres)
- Suppression Ratio (SR)
- Spectral Entropy & Coherence
- Artifact rejection (EOG / EMG)
- Real-time streaming (UDP or DLL buffer)

## Build
1. **Julia part**
   ```bash
   julia build_dll.jl
   ```
2. **Delphi part**
   - Open `delphi_ui/EEGViewer.dpr` in RAD Studio
   - Compile and run

## License
MIT License © 2025 Sébastien Cotillard
