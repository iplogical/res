#!/usr/bin/env bash
xinput_calibrator  --output-type xorg.conf.d | grep -A6 'Section "InputClass"' > /usr/share/X11/xorg.conf.d/99-calibration.conf

