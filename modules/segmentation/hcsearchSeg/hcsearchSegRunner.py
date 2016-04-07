#!/usr/bin/env python
import argparse
import os
import subprocess
import platform
import time
import traceback
import shutil

# paths to MaATLAB
MAC_MATLAB_PATH = "/Applications/MATLAB_R2015b.app/bin/matlab"
WIN_MATLAB_PATH = "C:\\Program Files\\MATLAB\\R2015b\\bin\\matlab.exe"

# whether to log MATLAB runs (for debugging)
LOG_MATLAB_RUNS = True

def run_matlab_function(func_string, func_name, logs_dir):
    '''Wraps function with try-catch to exit MATLAB on errors'''

    wrapped_func_string = "try;{0};catch exception;disp(getReport(exception));exit(1);end;exit".format(func_string)

    print "executing: {0}".format(wrapped_func_string)
    print

    # check OS and use appropriate command/arguments
    logfile = os.path.join(logs_dir,
        'matlab_run_{0}_{1}.txt'.format(
            int(time.time()),
            func_name))
    application = []
    if platform.system() == 'Darwin': # Mac
        application = [
        MAC_MATLAB_PATH,
        "-nodisplay",
        '-r "{0}"'.format(wrapped_func_string)]
        # for Mac, have to separate arguments like this

        if LOG_MATLAB_RUNS:
            application.append('-logfile "{0}"'.format(logfile))
    elif platform.system() == 'Windows': # Windows
        application = [
        WIN_MATLAB_PATH,
        "-nosplash",
        "-wait",
        "-nodesktop",
        "-minimize",
        "-r",
        '"{0}"'.format(wrapped_func_string)]
        # for Windows, have to separate arguments like this

        if LOG_MATLAB_RUNS:
            application.append("-logfile")
            application.append('"{0}"'.format(logfile))
    elif platform.system() == 'Linux': # Linux
        print "Linux unsupported at this time."
        print
        print "running step Error"
        exit(1)
    else:
        print "Unrecognized OS/Platform: {0}".format(platform.system())
        print
        print "running step Error"
        exit(1)

    # execute MATLAB
    try:
        exit_status = subprocess.call(application)
    except:
        print "Could not successfully open MATLAB. Is this the correct path: "
        if platform.system() == 'Windows':
            print WIN_MATLAB_PATH
        elif platform.system() == 'Darwin':
            print MAC_MATLAB_PATH
        else:
            print "(unknown os)"
        traceback.print_exc()
        exit(1)

    if exit_status != 0:
        print "MATLAB exited with error. ({0})".format(func_name)
        print
        print "running step Error"
        exit(1)

    print "MATLAB exited successfully. ({0})".format(func_name)

def main():
    '''Main loop'''

    # parse arguments
    parser = argparse.ArgumentParser(description="Launch HC-Search segmentation algorithm")
    parser.add_argument("runConfigFileName", help="path to the runConfig_segmentation.txt file that is generated from the avatol_cv program.")
    args = parser.parse_args()
    run_config_file_name = args.runConfigFileName

    # constants: paths
    THIS_DIR = os.path.dirname(os.path.realpath(__file__))
    THIRD_PARTY_DIR = os.path.join(THIS_DIR, '..', '..', '3rdParty')

    # constants: keys in run_config_file_name
    TEST_IMAGES_FILE = "testImagesFile"
    SEGMENTATION_OUTPUT_DIR = "segmentationOutputDir"
    TRAIN_IMAGES_FILE = "userProvidedGroundTruthImagesFile"
    GT_IMAGES_FILE = "userProvidedTrainImagesFile"

    #
    # parse run config file
    #

    print "run_config_file_name is {0}".format(run_config_file_name)
    print

    # loop to parse run config file for key-value pairs
    run_config = {}
    with open(run_config_file_name, "r") as f:
        for line in f:
            key, value = line.partition("=")[::2]
            run_config[key.strip()] = value.strip()

    # check that run config file has all the expected key-value pairs
    # expecting: testImagesFile, segmentationOutputDir

    if TEST_IMAGES_FILE not in run_config:
        print "segmentationRunConfig file missing entry for {0}".format(TEST_IMAGES_FILE)
        exit(1)
    if SEGMENTATION_OUTPUT_DIR not in run_config:
        print "segmentationRunConfig file missing entry for {0}".format(SEGMENTATION_OUTPUT_DIR)
        exit(1)
    if TRAIN_IMAGES_FILE not in run_config:
        print "segmentationRunConfig file missing optional entry for {0}".format(TRAIN_IMAGES_FILE)
    if GT_IMAGES_FILE not in run_config:
        print "segmentationRunConfig file missing optional entry for {0}".format(GT_IMAGES_FILE)

    print "run_config['{1}'] is {0}".format(
        run_config[TEST_IMAGES_FILE],
        TEST_IMAGES_FILE)
    print "run_config['{1}'] is {0}".format(
        run_config[SEGMENTATION_OUTPUT_DIR],
        SEGMENTATION_OUTPUT_DIR)
    if TRAIN_IMAGES_FILE in run_config:
        print "run_config['{1}'] is {0}".format(
            run_config[TRAIN_IMAGES_FILE],
            TRAIN_IMAGES_FILE)
    if GT_IMAGES_FILE in run_config:
        print "run_config['{1}'] is {0}".format(
            run_config[GT_IMAGES_FILE],
            GT_IMAGES_FILE)
    print

    # logs directory
    logs_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
    logs_dir = os.path.join(logs_dir, 'logs')
    logs_dir = os.path.join(logs_dir, 'segmentation')
    if not os.path.exists(logs_dir):
        os.makedirs(logs_dir)

    # copy original images to segmentedData/*_orig.jpg
    print "copying original images"
    with open(run_config[TEST_IMAGES_FILE], 'r') as f:
        for line in f:
            orig_image_path = line.strip()
            old_file_name_base = os.path.splitext(os.path.basename(orig_image_path))[0]
            new_file_name = '{}_orig.jpg'.format(old_file_name_base)
            dest = os.path.join(run_config[SEGMENTATION_OUTPUT_DIR], new_file_name)
            shutil.copyfile(orig_image_path, dest)

    #
    #  call matlab...
    #

    matlab_func1 = "invoke_hcsearchseg_system('{0}', '{1}')".format(
        run_config[SEGMENTATION_OUTPUT_DIR],
        run_config[TEST_IMAGES_FILE])

    print 'running step Segmenting'
    os.chdir(THIS_DIR)

    run_matlab_function(matlab_func1, "translate_input", logs_dir)

    print 'run completed'

# run script (or do nothing on import)
if __name__ == "__main__":
    main()
