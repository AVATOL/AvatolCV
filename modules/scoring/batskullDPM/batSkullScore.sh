#!/bin/bash

THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
THIRD_PARTY_DIR=${THIS_DIR}/../../3rdParty

testImagesFile=""
trainingDataDir=""
scoringOutputDir=""
filename="$1"
echo "FILENAME: "
echo $filename
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
echo testImagesFile is ${testImagesFile}
echo trainingDataDir is ${trainingDataDir}
echo scoringOutputDir is ${scoringOutputDir}

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

echo $matlab_func1
cd $THIS_DIR

#/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "$matlab_func1"
/Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "$matlab_func1"

echo "matlab exited!!! (translate_input)"

#
#  call matlab to score
#

#direct matlab call

summaryFile=$(dirname "${testImagesFile}")
summaryFile+='/legacy_format/input/summary.txt'
echo "summaryFile: "
echo $summaryFile

matlab_func2='try;invoke_batskull_system('
matlab_func2+="'"${summaryFile}"'"
matlab_func2+=','
matlab_func2+="'regime2'"
matlab_func2+=');catch exception;disp(getReport(exception));exit(1);end;exit'

echo $matlab_func2
cd bat/chain_rpm

#/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "$matlab_func2"
/Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "$matlab_func2"

echo "matlab exited!!! (invoke_batskull_system)"

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

echo $matlab_func3
cd $THIS_DIR

#/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "$matlab_func3"
/Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "$matlab_func3"

echo "matlab exited!!! (translate_output)"

#hopefully we will be able to resolve the library issue from Invalid MEX-file '/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64': dlopen(/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64, 1): Library not loaded: @loader_path/libvl.dylib. But for now we have decided to go with the direct matlab call

#echo ./${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${scoringOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
#${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${scoringOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
#echo done with orientation
