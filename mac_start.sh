#!/bin/bash 
export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH:/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/runtime/maci64:/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/sys/os/maci64:/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/runtime/bin/maci64:/System/Library/Frameworks/JavaVM.framework/JavaVM:/System/Library/Frameworks/JavaVM.framework/Libraries"

export XAPPLRESDIR="/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/X11/app-defaults" 
./run_avatol_cv.sh /Applications/MATLAB/MATLAB_Compiler_Runtime/v80
