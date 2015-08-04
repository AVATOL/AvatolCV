#!/bin/bash

THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
THIRD_PARTY_DIR=${THIS_DIR}/../../3rdParty

trainingImagesFile=""
testingImagesFile=""
rawImagesDir=""
trainingImagesDir=""
segmentationOutputDir=""
trainingFileSuffix=""
outputFileSuffix=""
croppedMaskImageSuffix=""
croppedOrigImageSuffix=""
avatolCVStatusFile=""
groundTruthImagesFile=""

filename="$1"
while read -r line
do
    lineAsArray=(${line//=/ })
    key=${lineAsArray[0]}
    val=${lineAsArray[1]}
    #echo key ${key}
    #echo val ${val}
    if [ ${key:0:1} = "#" ]; then
        foo=1
    elif [ "trainingImagesFile" = "$key" ]; then
	    trainingImagesFile=${val}
    elif [ "testingImagesFile" = "$key" ]; then
	    testingImagesFile=${val}
	elif [ "rawImagesDir" = "$key" ]; then
	    rawImagesDir=${val}
	elif [ "trainingImagesDir" = "$key" ]; then
	    trainingImagesDir=${val}
	elif [ "segmentationOutputDir" = "$key" ]; then
	    segmentationOutputDir=${val}
	elif [ "trainingFileSuffix" = "$key" ]; then
	    trainingFileSuffix=${val}
	elif [ "croppedOrigImageSuffix" = "$key" ]; then
	    croppedOrigImageSuffix=${val}
	elif [ "croppedMaskImageSuffix" = "$key" ]; then
	    croppedMaskImageSuffix=${val}
    elif [ "avatolCVStatusFile" = "$key" ]; then
	    avatolCVStatusFile=${val}
    elif [ "groundTruthImagesFile" = "$key" ]; then
	    groundTruthImagesFile=${val}
    fi
done < "$filename"

missingArg=0
if [ "$trainingImagesFile" = "" ]; then
	missingArg=1
	echo segRunConfig file missing entry for trainingImagesFile
fi
if [ "$testingImagesFile" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for testingImagesFile
fi
if [ "$rawImagesDir" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for rawImagesDir
fi
if [ "$trainingImagesDir" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for trainingImagesDir
fi
if [ "$segmentationOutputDir" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for segmentationOutputDir
fi
if [ "$trainingFileSuffix" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for trainingFileSuffix
fi
if [ "$croppedMaskImageSuffix" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for croppedMaskImageSuffix
fi

if [ "$croppedOrigImageSuffix" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for croppedOrigImageSuffix
fi
if [ "$avatolCVStatusFile" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for avatolCVStatusFile
fi 
if [ "$groundTruthImagesFile" = "" ]; then
    missingArg=1
	echo segRunConfig file missing entry for groundTruthImagesFile
fi 
if [ "$missingArg" = 1 ]; then 
    exit 1
fi
echo trainingImagesFile is ${trainingImagesFile}
echo testingImagesFile is ${testingImagesFile}
echo rawImagesDir is ${rawImagesDir}
echo trainingImagesDir is ${trainingImagesDir}
echo segmentationOutputDir is ${segmentationOutputDir}
echo trainingFileSuffix is ${trainingFileSuffix}
echo croppedOrigImageSuffix is ${croppedOrigImageSuffix}
echo croppedMaskImageSuffix is ${croppedMaskImageSuffix}
echo avatolCVStatusFile is ${avatolCVStatusFile}
echo groundTruthImagesFile is ${groundTruthImagesFile}



#
# copy trainlistShipped.txt into trainlist.txt and add any files coming in from trainingImagesFile
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
done < "$trainingImagesFile"
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
    lineAsArray=(${filename//./ })
    fileroot=${lineAsArray[0]}
    echo fileroot found as ${fileroot}
    echo ${fileroot} >> ${TEST_LIST}
done < "$testingImagesFile"
# by this time we have our testlist.txt file



#
#
# copy the session-provided testing images to the allImages dir
# (we will not be testing the shipped test images so don't copy those)
#
RELATIVE_ALL_IMAGES_DIR=data/allImages
ALL_IMAGES_DIR=${THIS_DIR}/${RELATIVE_ALL_IMAGES_DIR}
mkdir ${ALL_IMAGES_DIR}
rm ${ALL_IMAGES_DIR}/*
while read -r line
do
    filepath=${line}
    echo testing image filepath found for copying : ${filepath}
    cp ${filepath} ${ALL_IMAGES_DIR}
done < "$testingImagesFile"



#
#
# copy the shipped training images to allImages
#
#

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
done < "$trainingImagesFile"
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
done < "$groundTruthImagesFile"


#
# create darwin's config.xml file
#
DARWIN_CONFIG_XML=${THIS_DIR}/darwinConfig.xml
${THIS_DIR}/createDarwinConfigXml.sh ${DARWIN_CONFIG_XML} ${THIS_DIR} ${RELATIVE_ALL_IMAGES_DIR} ${RELATIVE_LABELS_DIR}


#
#
# RUN THE STEPS OF DARWIN
#
#
OPENCV_LIB_DIR=${THIRD_PARTY_DIR}/darwin/drwn-1.8.0/external/opencv/lib
export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH:${OPENCV_LIB_DIR}"
DARWIN_BIN_DIR=${THIRD_PARTY_DIR}/darwin/drwn-1.8.0/bin
# converting pixel labels
echo running darwin step 1 of 6 > ${avatolCVStatusFile}
echo ${DARWIN_BIN_DIR}/convertPixelLabels -config ${DARWIN_CONFIG_XML} -i "_GT.png" ${TRAIN_LIST}
${DARWIN_BIN_DIR}/convertPixelLabels -config ${DARWIN_CONFIG_XML} -i "_GT.png" ${TRAIN_LIST}

# train boosted classifiers
echo running darwin step 2 of 6 > ${avatolCVStatusFile}
echo ${DARWIN_BIN_DIR}/learnPixelSegModel -config ${DARWIN_CONFIG_XML} -component BOOSTED -set drwnDecisionTree split MISCLASS -set drwnBoostedClassifier numRounds 200 -subSample 250 ${TRAIN_LIST}
${DARWIN_BIN_DIR}/learnPixelSegModel -config ${DARWIN_CONFIG_XML} -component BOOSTED -set drwnDecisionTree split MISCLASS -set drwnBoostedClassifier numRounds 200 -subSample 250 ${TRAIN_LIST}


# train unary potentials
echo running darwin step 3 of 6 > ${avatolCVStatusFile}
echo ${DARWIN_BIN_DIR}/learnPixelSegModel -config ${DARWIN_CONFIG_XML} -component UNARY -subSample 25 ${TRAIN_LIST}
${DARWIN_BIN_DIR}/learnPixelSegModel -config ${DARWIN_CONFIG_XML} -component UNARY -subSample 25 ${TRAIN_LIST}

# evaluate with unary terms only
echo running darwin step 4 of 6 > ${avatolCVStatusFile}
echo ${DARWIN_BIN_DIR}/inferPixelLabels -config ${DARWIN_CONFIG_XML} -pairwise 0.0 -outLabels .unary.txt -outImages .unary.png ${TEST_LIST}
${DARWIN_BIN_DIR}/inferPixelLabels -config ${DARWIN_CONFIG_XML} -pairwise 0.0 -outLabels .unary.txt -outImages .unary.png ${TEST_LIST}

# train pairwise potentials
echo running darwin step 5 of 6 > ${avatolCVStatusFile}
echo ${DARWIN_BIN_DIR}/learnPixelSegModel -config ${DARWIN_CONFIG_XML} -component CONTRAST ${TRAIN_LIST}
${DARWIN_BIN_DIR}/learnPixelSegModel -config ${DARWIN_CONFIG_XML} -component CONTRAST ${TRAIN_LIST}

# evaluate with unary and pariwise terms
echo running darwin step 6 of 6 > ${avatolCVStatusFile}
echo ${DARWIN_BIN_DIR}/inferPixelLabels -config ${DARWIN_CONFIG_XML} -outLabels .pairwise.txt -outImages .pairwise.png ${TEST_LIST}
${DARWIN_BIN_DIR}/inferPixelLabels -config ${DARWIN_CONFIG_XML} -outLabels .pairwise.txt -outImages .pairwise.png ${TEST_LIST}

echo cropping images > ${avatolCVStatusFile}

#
#  call matlab to crop the leaf on both raw and mask images
#
echo ./${THIS_DIR}/yaoSegPP_via_runtime.sh ${segmentationOutputDir} ${testingImagesFile} ${croppedOrigImageSuffix} ${croppedMaskImageSuffix}
${THIS_DIR}/yaoSegPP_via_runtime.sh ${segmentationOutputDir} ${testingImagesFile} ${croppedOrigImageSuffix} ${croppedMaskImageSuffix}
echo done with orientation
echo done with orientation > ${avatolCVStatusFile}
