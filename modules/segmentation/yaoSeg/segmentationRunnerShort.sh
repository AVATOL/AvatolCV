#!/bin/bash

THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
THIRD_PARTY_DIR=${THIS_DIR}/../../3rdParty

userProvidedTrainImagesFile=""
userProvidedGroundTruthImagesFile=""
testImagesFile=""
segmentationOutputDir=""

croppedMaskImageSuffix="_croppedMask"
croppedOrigImageSuffix="_croppedOrig"

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
    elif [ "userProvidedTrainImagesFile" = "$key" ]; then
	    userProvidedTrainImagesFile=${val}
    elif [ "testImagesFile" = "$key" ]; then
	    testImagesFile=${val}
	elif [ "segmentationOutputDir" = "$key" ]; then
	    segmentationOutputDir=${val}
    elif [ "userProvidedGroundTruthImagesFile" = "$key" ]; then
	    userProvidedGroundTruthImagesFile=${val}
    fi
done < "$filename"

missingArg=0
if [ "$userProvidedTrainImagesFile" = "" ]; then
	missingArg=1
	echo segRunConfig file missing entry for userProvidedTrainImagesFile
fi
if [ "$testImagesFile" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for testImagesFile
fi
if [ "$segmentationOutputDir" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for segmentationOutputDir
fi
if [ "$userProvidedGroundTruthImagesFile" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for userProvidedGroundTruthImagesFile
fi 
if [ "$missingArg" = 1 ]; then 
    exit 1
fi
echo userProvidedTrainImagesFile is ${userProvidedTrainImagesFile}
echo testImagesFile is ${testImagesFile}
echo segmentationOutputDir is ${segmentationOutputDir}
echo userProvidedGroundTruthImagesFile is ${userProvidedGroundTruthImagesFile}



#
# copy trainlistShipped.txt into trainlist.txt and add any files coming in from userProvidedTrainImagesFile
#
TRAIN_LIST=${THIS_DIR}/trainlist.txt

cp ${THIS_DIR}/trainlistShipped.txt ${TRAIN_LIST}

while read -r line
do
    filename=$(basename $line)
    echo filename detected was ${filename}
    lineAsArray=(${filename//./ })
    fileroot=${lineAsArray[0]}
    echo fileroot found as ${fileroot}
    echo ${fileroot} >> ${TRAIN_LIST}
done < "$userProvidedTrainImagesFile"
# by this time we have our combined trainlist.txt file


#
#
# reformat testing image file to correct form
#
#
TEST_LIST=${THIS_DIR}/testlist.txt
rm ${TEST_LIST}
while read -r line
do
    filename=$(basename $line)
    echo filename detected was ${filename}
#lineAsArray=(${filename//./ })
#fileroot=${lineAsArray[0]}
    fileroot=${filename%.*}
    echo fileroot found as ${fileroot}
    echo ${fileroot} >> ${TEST_LIST}
done < "$testImagesFile"
# by this time we have our testlist.txt file



#
#
# copy the session-provided testing images to the allImages dir
# (we will not be testing the shipped test images so don't copy those)
#
DATA_DIR=${THIS_DIR}/data
mkdir ${DATA_DIR}
RELATIVE_ALL_IMAGES_DIR=data/allImages
ALL_IMAGES_DIR=${THIS_DIR}/${RELATIVE_ALL_IMAGES_DIR}
mkdir ${ALL_IMAGES_DIR}
rm ${ALL_IMAGES_DIR}/*
while read -r line
do
    filepath=${line}
    echo testing image filepath found for copying : ${filepath}
    cp ${filepath} ${ALL_IMAGES_DIR}
done < "$testImagesFile"



#
#
# copy the shipped training images to allImages
#
#
#create dir in case its not there yet
SHIPPED_TRAINING_IMAGES_DIR=data/training_imgs
mkdir ${SHIPPED_TRAINING_IMAGES_DIR}
cp ${THIS_DIR}/data/training_imgs/* ${ALL_IMAGES_DIR}


#
#
#  Copy the session-provided training images to ALL_IMAGES_DIR
#
#

while read -r line
do
    filepath=${line}
    echo training image filepath found for copying : ${filepath}
    cp ${filepath} ${ALL_IMAGES_DIR}
done < "$userProvidedTrainImagesFile"
#by this point, training images are in place


#
#
# copy the shipped ground truth images to allImages
#
#
RELATIVE_LABELS_DIR=data/allLabels
ALL_LABELS_DIR=${THIS_DIR}/${RELATIVE_LABELS_DIR}
mkdir ${ALL_LABELS_DIR}
cp ${THIS_DIR}/data/labels/* ${ALL_LABELS_DIR}


#
#
#  Copy the session-provided ground truth images to ALL_LABELS_DIR
#
#

while read -r line
do
    filepath=${line}
    echo ground truth image filepath found for copying : ${filepath}
    cp ${filepath} ${ALL_LABELS_DIR}
done < "$userProvidedGroundTruthImagesFile"


#
# create darwin's config.xml file
#
DARWIN_CONFIG_XML=${THIS_DIR}/darwinConfig.xml

${THIS_DIR}/createDarwinConfigXml.sh ${DARWIN_CONFIG_XML} ${THIS_DIR} ${RELATIVE_ALL_IMAGES_DIR} ${RELATIVE_LABELS_DIR}

#
#  call matlab to crop the leaf on both raw and mask images
#
echo running step 7 of 7 - cropping images
#direct matlabe call
matlab_func='Yao_postprocessing('
matlab_func+="'"${segmentationOutputDir}"'"
matlab_func+=','
matlab_func+="'"${testImagesFile}"'"
matlab_func+=','
matlab_func+="'"${croppedOrigImageSuffix}"'"
matlab_func+=','
matlab_func+="'"${croppedMaskImageSuffix}"'"
matlab_func+=')'

echo $matlab_func
cd $THIS_DIR

/Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r $matlab_func quit

echo "matlab exited!!!"
# this is the compiled version
#cd ${THIS_DIR}
#echo ${THIS_DIR}/yaoSegPP_via_runtime.sh ${segmentationOutputDir} ${testImagesFile} ${croppedOrigImageSuffix} ${croppedMaskImageSuffix}
#${THIS_DIR}/yaoSegPP_via_runtime.sh ${segmentationOutputDir} ${testImagesFile} ${croppedOrigImageSuffix} ${croppedMaskImageSuffix}
#echo done with segmentation
#echo done with segmentation > ${avatolCVStatusFile}
