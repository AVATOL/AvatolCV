#!/bin/bash
FILEPATH="$1"
BASE_DIR="$2"
RELATIVE_IMAGE_DIR="$3"
RELATIVE_LABEL_DIR="$4"

echo "<drwn>" > ${FILEPATH}
echo "  <drwnMultiSegConfig>" >> ${FILEPATH}
echo "    <!-- data options -->"  >> ${FILEPATH}
#echo "    <option name=\"baseDir\" value=\"/Users/jedirvine/av/segmentationTest/\" />"  >> ${FILEPATH}
echo "    <option name=\"baseDir\" value=\"${BASE_DIR}/\" />"  >> ${FILEPATH}
echo "    <option name=\"imgDir\" value=\"${RELATIVE_IMAGE_DIR}/\" />"  >> ${FILEPATH}    
echo "    <option name=\"lblDir\" value=\"${RELATIVE_LABEL_DIR}/\" />"  >> ${FILEPATH}
echo "     <option name=\"segDir\" value=\"data/regions/\" />"  >> ${FILEPATH}
echo "     <option name=\"cacheDir\" value=\"cached/\" />"  >> ${FILEPATH}
echo "     <option name=\"modelsDir\" value=\"models/\" />"  >> ${FILEPATH}
echo "     <option name=\"outputDir\" value=\"output/\" />"  >> ${FILEPATH}
echo "     <option name=\"imgExt\" value=\".jpg\" />"  >> ${FILEPATH}
echo "     <option name=\"lblExt\" value=\".txt\" />"  >> ${FILEPATH}
echo "     <option name=\"segExt\" value=\".sp\" />"  >> ${FILEPATH}
echo "     <option name=\"useCache\" value=\"false\" />"  >> ${FILEPATH}
echo "     <!-- region definitions -->"  >> ${FILEPATH}
echo "     <regionDefinitions>"  >> ${FILEPATH}
echo "       <region id=\"-1\" name=\"void\" color=\"0 0 0\"/>"  >> ${FILEPATH}
echo "       <region id=\"0\" name=\"leaves\" color=\"0 255 0\"/>"  >> ${FILEPATH}
echo "       <region id=\"1\" name=\"tags\" color=\"255 0 0\"/>"  >> ${FILEPATH}
echo "       <region id=\"2\" name=\"background\" color=\"0 0 255\"/>"  >> ${FILEPATH}
echo "     </regionDefinitions>"  >> ${FILEPATH}
echo "  </drwnMultiSegConfig>"  >> ${FILEPATH}
echo "   <drwnSegImagePixelFeatures>"  >> ${FILEPATH}
echo "       <!-- feature options -->  >>" >> ${FILEPATH}
echo "       <option name=\"filterBandwidth\" value=\"1\" />" >> ${FILEPATH}
echo "       <option name=\"featureGridSpacing\" value=\"5\" />" >> ${FILEPATH}
echo "       <option name=\"includeRGB\" value=\"true\" />" >> ${FILEPATH}
echo "       <option name=\"includeHOG\" value=\"true\" />" >> ${FILEPATH}
echo "       <option name=\"includeLBP\" value=\"true\" />" >> ${FILEPATH}
echo "       <option name=\"includeRowCol\" value=\"true\" />" >> ${FILEPATH}
echo "       <option name=\"includeLocation\" value=\"true\" />" >> ${FILEPATH}
echo "     </drwnSegImagePixelFeatures>" >> ${FILEPATH}
echo "     <drwnCodeProfiler enabled=\"true\" />" >> ${FILEPATH}
echo "     <drwnLogger logLevel=\"VERBOSE\" logFile=\"msrc.log\" />" >> ${FILEPATH}
echo "     <drwnThreadPool threads=\"4\" />" >> ${FILEPATH}
echo "     <drwnConfusionMatrix colSep=\" || \" rowBegin=\"    || \" rowEnd=\" \" />" >> ${FILEPATH}
echo "     <drwnHOGFeatures blockSize=\"1\" normClippingLB=\"0.1\" normClippingUB=\"0.5\" />" >> ${FILEPATH}
echo "</drwn>"  >> ${FILEPATH}
