# Architecture Overview

1. **Julia DLL**
   - Processes EEG data (left/right)
   - Extracts medical-standard features (SR, asymmetry, band power)
   - Exposed as shared DLL

2. **Delphi GUI**
   - Calls Julia DLL
   - Displays results (real-time plots, feature logs)
   - Future: Threaded version with continuous acquisition
