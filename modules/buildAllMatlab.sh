#!/bin/bash
export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/
export PATH="/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Commands:$PATH:/Applications/MATLAB_R2012b.app/bin/maci64"
export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH:/Applications/MATLAB_R2012b.app/bin/maci64"

java -version

cd /Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg
/Applications/MATLAB_R2012b.app/bin/maci64/deploytool -build yaoSegPP.prj


cd /Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient
/Applications/MATLAB_R2012b.app/bin/maci64/deploytool -build yaoOrient.prj
