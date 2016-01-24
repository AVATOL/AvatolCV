#!/bin/bash
# This script launches the bat skull algorithm.

readonly SCRIPT_NAME="$0"

usage() {
    echo usage: $SCRIPT_NAME /path/to/runConfig_scoring.txt
    echo
    echo This script launches the bat skull algorithm. 
    echo
    echo It accepts one argument, the path to the runConfig_scoring.txt 
    echo file that is generated from the avatol_cv program.
}
if [ "$#" -ne 1 ]; then
    usage
    exit 1
fi

readonly RUN_CONFIG_FILE_NAME="$1"

readonly THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
readonly THIRD_PARTY_DIR=${THIS_DIR}/../../3rdParty

remove_cache_directory() {
    local cache_dir=$1

    echo "cache_dir is ${cache_dir}"
    echo

    rm -rf $cache_dir
}

run_matlab_function() {
    local func_string=$1
    local func_name=$2

    # wraps function with try-catch to exit MATLAB on errors
    local wrapped_func_string='try;'
    wrapped_func_string+=$func_string
    wrapped_func_string+=';catch exception;disp(getReport(exception));exit(1);end;exit'
    echo "executing: ${wrapped_func_string}"
    echo

    #if ! /Applications/MATLAB_R2012b.app/bin/matlab -nodisplay -r "${wrapped_func_string}"; then
    if ! /Applications/MATLAB_R2015b.app/bin/matlab -nodisplay -r "${wrapped_func_string}"; then
        echo "MATLAB exited with error. (${func_name})"
        echo
        echo "running step Error"
        exit 1
    fi

    echo "MATLAB exited successfully. (${func_name})"
    echo
}

main() {
    local testImagesFile=""
    local trainingDataDir=""
    local scoringOutputDir=""

    #
    #  parse run config file
    #

    echo "RUN_CONFIG_FILE_NAME is ${RUN_CONFIG_FILE_NAME}"
    echo

    # loop to parse run config file for key-value pairs
    while read -r line
    do
        IFS='=' read -a lineAsArray <<< "$line"
        #lineAsArray=(${line//=/ })
        local key=${lineAsArray[0]}
        local val=${lineAsArray[1]}

        if [ ${key:0:1} = "#" ]; then
            local foo=1
        elif [ "testImagesFile" = "$key" ]; then
            testImagesFile=${val}
        elif [ "trainingDataDir" = "$key" ]; then
            trainingDataDir=${val}
        elif [ "scoringOutputDir" = "$key" ]; then
            scoringOutputDir=${val}
    fi
    done < "$RUN_CONFIG_FILE_NAME"

    # check that run config file has all the expected key-value pairs
    local missingArg=0
    if [ "${testImagesFile}" = "" ]; then
        missingArg=1
        echo 'scoringRunConfig file missing entry for testImagesFile'
    fi
    if [ "${trainingDataDir}" = "" ]; then
        missingArg=1
        echo 'scoringRunConfig file missing entry for trainingDataDir'
    fi
    if [ "${scoringOutputDir}" = "" ]; then
        missingArg=1
        echo 'scoringRunConfig file missing entry for scoringOutputDir'
    fi
    if [ "${missingArg}" = 1 ]; then
        exit 1
    fi
    echo "testImagesFile is ${testImagesFile}"
    echo "trainingDataDir is ${trainingDataDir}"
    echo "scoringOutputDir is ${scoringOutputDir}"
    echo

    # remove cache directory
    local cache_dir=$(dirname "${testImagesFile}")
    cache_dir+='/legacy_format/cache/'
    # remove_cache_directory "${cache_dir}"

    #
    #  call matlab to translate input
    #

    local matlab_func1='translate_input('
    matlab_func1+="'"
    matlab_func1+="${scoringOutputDir}"
    matlab_func1+="','"
    matlab_func1+="${trainingDataDir}"
    matlab_func1+="','"
    matlab_func1+="${testImagesFile}"
    matlab_func1+="'"
    matlab_func1+=')'

    echo 'running step Processing Inputs'
    cd $THIS_DIR

    run_matlab_function "${matlab_func1}" "translate_input"

    #
    #  call matlab to score
    #

    local summary_file=$(dirname "${testImagesFile}")
    summary_file+='/legacy_format/input/summary.txt'
    echo "summary_file is ${summary_file}"
    echo

    local matlab_func2='invoke_batskull_system('
    matlab_func2+="'"${summary_file}"'"
    matlab_func2+=','
    matlab_func2+="'regime2'"
    matlab_func2+=')'

    echo 'running step Training and Scoring'
    cd bat/chain_rpm

    run_matlab_function "${matlab_func2}" "invoke_batskull_system"

    cd $THIS_DIR

    #
    #  call matlab to translate output
    #

    local matlab_func3='translate_output('
    matlab_func3+="'"
    matlab_func3+="${scoringOutputDir}"
    matlab_func3+="','"
    matlab_func3+="${trainingDataDir}"
    matlab_func3+="','"
    matlab_func3+="${testImagesFile}"
    matlab_func3+="'"
    matlab_func3+=')'

    echo 'running step Processing Outputs'
    cd $THIS_DIR

    run_matlab_function "${matlab_func3}" "translate_output"

    echo 'run completed'

    #hopefully we will be able to resolve the library issue from Invalid MEX-file '/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64': dlopen(/Users/jedirvine/.mcrCache8.0/yaoOri0/modules/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/mex/mexmaci64/vl_hog.mexmaci64, 1): Library not loaded: @loader_path/libvl.dylib. But for now we have decided to go with the direct matlab call

    #echo ./${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${scoringOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
    #${THIS_DIR}/yaoOrient_via_runtime.sh ${testImagesFile} ${testImagesMaskFile} ${scoringOutputDir} ${pathLibSsvmMatlab} ${pathVlfeat}
    #echo done with orientation
}
main
