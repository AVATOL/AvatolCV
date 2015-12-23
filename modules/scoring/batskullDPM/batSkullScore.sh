#!/bin/bash

THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
THIRD_PARTY_DIR=${THIS_DIR}/../../3rdParty

testImagesFile=""
trainingDataDir=""
scoringOutputDir=""
filename="$1"
echo filename is ${filename}
echo 
while read -r line
do
    IFS='=' read -a lineAsArray <<< "$line"
    #lineAsArray=(${line//=/ })
    key=${lineAsArray[0]}
    val=${lineAsArray[1]}
    #echo key ${key}
    #echo val ${val}
    if [ ${key:0:1} = "#" ]; then
        foo=1
    elif [ "testImagesFile" = "$key" ]; then
        testImagesFile=${val}
    elif [ "trainingDataDir" = "$key" ]; then
        trainingDataDir=${val}
    elif [ "scoringOutputDir" = "$key" ]; then
        scoringOutputDir=${val}
fi
done < "$filename"

missingArg=0
if [ "$testImagesFile" = "" ]; then
    missingArg=1
    echo scoringRunConfig file missing entry for testImagesFile
fi
if [ "$trainingDataDir" = "" ]; then
    missingArg=1
    echo scoringRunConfig file missing entry for trainingDataDir
fi
if [ "$scoringOutputDir" = "" ]; then
    missingArg=1
    echo scoringRunConfig file missing entry for scoringOutputDir
fi
if [ "$missingArg" = 1 ]; then
    exit 1
fi
echo "testImagesFile is ${testImagesFile}"
echo "trainingDataDir is ${trainingDataDir}"
echo "scoringOutputDir is ${scoringOutputDir}"
echo

# # remove cache directory
# cacheDir=$(dirname "${testImagesFile}")
# cacheDir+=/legacy_format/cache/
# echo cacheDir is ${cacheDir}
# echo
# rm -rf $cacheDir

#
#  call matlab to translate input
#

#direct matlab call

matlab_func1='try;translate_input('
matlab_func1+="'"
matlab_func1+="${scoringOutputDir}"
matlab_func1+="','"
matlab_func1+="${trainingDataDir}"
matlab_func1+="','"
matlab_func1+="${testImagesFile}"
matlab_func1+="'"
matlab_func1+=');catch exception;disp(getReport(exception));exit(1);end;exit'

echo "running step Processing Inputs"
echo "executing: ${matlab_func1}"
echo
cd $THIS_DIR

#/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "$matlab_func1"
if ! /Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "$matlab_func1"; then
    echo "MATLAB exited with error ${?}. (translate_input)"
    echo
    echo "running step Error"
    exit $?
fi

echo "MATLAB exited successfully. (translate_input)"
echo

#
#  call matlab to score
#

#direct matlab call

summaryFile=$(dirname "${testImagesFile}")
summaryFile+='/legacy_format/input/summary.txt'
echo "summaryFile is ${summaryFile}"
echo

matlab_func2='try;invoke_batskull_system('
matlab_func2+="'"${summaryFile}"'"
matlab_func2+=','
matlab_func2+="'regime2'"
matlab_func2+=');catch exception;disp(getReport(exception));exit(1);end;exit'

echo "running step Training and Scoring"
echo "executing: ${matlab_func2}"
echo
cd bat/chain_rpm

#/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "$matlab_func2"
if ! /Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "$matlab_func2"; then
    echo "MATLAB exited with error ${?}. (invoke_batskull_system)"
    echo
    echo "running step Error"
    exit $?
fi

echo "MATLAB exited successfully. (invoke_batskull_system)"
echo

cd $THIS_DIR

#
#  call matlab to translate output
#

#direct matlab call

matlab_func3='try;translate_output('
matlab_func3+="'"
matlab_func3+="${scoringOutputDir}"
matlab_func3+="','"
matlab_func3+="${trainingDataDir}"
matlab_func3+="','"
matlab_func3+="${testImagesFile}"
matlab_func3+="'"
matlab_func3+=');catch exception;disp(getReport(exception));exit(1);end;exit'

echo "running step Processing Outputs"
echo "executing: ${matlab_func3}"
echo
cd $THIS_DIR

#/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "$matlab_func3"
if ! /Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "$matlab_func3"; then
    echo "MATLAB exited with error ${?}. (translate_output)"
    echo
    echo "running step Error"
    exit $?
fi

echo "MATLAB exited successfully. (translate_output)"
echo

echo "run completed"

#hopefully we will be able to resolve the library issue from Invalid MEX-file '/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64': dlopen(/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64, 1): Library not loaded: @loader_path/libvl.dylib. But for now we have decided to go with the direct matlab call

#echo ./${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${scoringOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
#${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${scoringOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
#echo done with orientation
