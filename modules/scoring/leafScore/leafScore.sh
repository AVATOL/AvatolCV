#!/bin/bash

THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
THIRD_PARTY_DIR=${THIS_DIR}/../../3rdParty

testImagesMaskFile=""
testImagesFile=""
scoringOutputDir=""
pathLibSsvmMatlab=""
pathVlfeat=""
trainingDataDir=""
filename="$1"
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
    elif [ "testImagesMaskFile" = "$key" ]; then
	    testImagesMaskFile=${val}
    elif [ "testImagesFile" = "$key" ]; then
	    testImagesFile=${val}
	elif [ "scoringOutputDir" = "$key" ]; then
	    scoringOutputDir=${val}
    elif [ "pathLibSsvmMatlab" = "$key" ]; then
        pathLibSsvmMatlab=${val}
    elif [ "pathVlfeat" = "$key" ]; then
        pathVlfeat=${val}
    elif [ "trainingDataDir" = "$key" ]; then
        trainingDataDir=${val}
    fi
done < "$filename"

missingArg=0
if [ "$testImagesMaskFile" = "" ]; then
	missingArg=1
	echo segRunConfig file missing entry for testImagesMaskFile
fi
if [ "$testImagesFile" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for testImagesFile
fi
if [ "$scoringOutputDir" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for scoringOutputDir
fi
if [ "$pathLibSsvmMatlab" = "" ]; then
missingArg=1
echo segRunConfig file missing entry for pathLibSsvmMatlab
fi
if [ "$trainingDataDir" = "" ]; then
missingArg=1
echo segRunConfig file missing entry for trainingDataDir
fi
if [ "$pathVlfeat" = "" ]; then
missingArg=1
echo segRunConfig file missing entry for pathVlfeat
fi
if [ "$missingArg" = 1 ]; then
    exit 1
fi
echo testImagesMaskFile is ${testImagesMaskFile}
echo testImagesFile is ${testImagesFile}
echo scoringOutputDir is ${scoringOutputDir}
echo pathLibSsvmMatlab is ${pathLibSsvmMatlab}
echo pathVlfeat is ${pathVlfeat}
echo trainingDataDir is ${trainingDataDir}

#
#  call matlab to crop the leaf on both raw and mask images
#

#direct matlabe call
#pathVlfeat='/Users/jedirvine/av/avatol_cv/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/vl_setup'
#echo pathVlfeat is ${pathVlfeat}

matlab_func='score('
matlab_func+="'"${testImagesFile}"'"
matlab_func+=','
matlab_func+="'"${testImagesMaskFile}"'"
matlab_func+=','
matlab_func+="'"${scoringOutputDir}"'"
matlab_func+=','
matlab_func+="'"${pathLibSsvmMatlab}"'"
matlab_func+=','
matlab_func+="'"${pathVlfeat}"'"
matlab_func+=','
matlab_func+="'"${trainingDataDir}"'"
matlab_func+=')'

echo $matlab_func
cd $THIS_DIR

/Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r $matlab_func

echo "matlab exited!!!"

#hopefully we will be able to resolve the library issue from Invalid MEX-file '/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64': dlopen(/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64, 1): Library not loaded: @loader_path/libvl.dylib. But for now we have decided to go with the direct matlab call

#echo ./${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${orientationOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
#${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${orientationOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
#echo done with orientation
