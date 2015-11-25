#!/bin/bash
export PATH="$PATH:/Applications/MATLAB_R2012b.app/bin/maci64"
export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH:/Applications/MATLAB_R2012b.app/bin/maci64"

cd /Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg
/Applications/MATLAB_R2012b.app/bin/maci64/deploytool -build yaoSegPP.prj
