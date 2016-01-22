#!/usr/bin/env python
import argparse
import os
import subprocess
import platform
import time

# paths to MaATLAB
MAC_MATLAB_PATH = "/Applications/MATLAB_R2015b.app/bin/matlab"
WIN_MATLAB_PATH = "C:\\Program Files\\MATLAB\\R2015b\\bin\\matlab.exe"

# whether to log MATLAB runs (for debugging)
LOG_MATLAB_RUNS = True

def remove_cache_directory(cache_dir):
    '''Delete the cache directory'''

    print "cache_dir is {0}".format(cache_dir)
    print

    os.remove(cache_dir)

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
    exit_status = subprocess.call(application)
    if exit_status != 0:
        print "MATLAB exited with error. ({0})".format(func_name)
        print
        print "running step Error"
        exit(1)

    print "MATLAB exited successfully. ({0})".format(func_name)

def main():
    '''Main loop'''

    # parse arguments
    parser = argparse.ArgumentParser(description="Launch bat scoring algorithm")
    parser.add_argument("runConfigFileName", help="path to the runConfig_scoring.txt ile that is generated from the avatol_cv program.")
    args = parser.parse_args()
    run_config_file_name = args.runConfigFileName

    # constants: paths
    THIS_DIR = os.path.dirname(os.path.realpath(__file__))
    # THID_PARTY_DIR = os.path.join(THIS_DIR, '..', '..', '3rdParty')

    # constants: keys in run_config_file_name
    TEST_IMAGES_FILE = "testImagesFile"
    TRAINING_DATA_DIR = "trainingDataDir"
    SCORING_OUTPUT_DIR = "scoringOutputDir"

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
    # expecting: testImagesFile, trainingDataDir, scoringOutputDir

    if TEST_IMAGES_FILE not in run_config:
        print "scoringRunConfig file missing entry for {0}".format(TEST_IMAGES_FILE)
        exit(1)
    if TRAINING_DATA_DIR not in run_config:
        print "scoringRunConfig file missing entry for {0}".format(TRAINING_DATA_DIR)
        exit(1)
    if SCORING_OUTPUT_DIR not in run_config:
        print "scoringRunConfig file missing entry for {0}".format(SCORING_OUTPUT_DIR)
        exit(1)

    print "run_config['{1}'] is {0}".format(
        run_config[TEST_IMAGES_FILE], 
        TEST_IMAGES_FILE)
    print "run_config['{1}'] is {0}".format(
        run_config[TRAINING_DATA_DIR], 
        TRAINING_DATA_DIR)
    print "run_config['{1}'] is {0}".format(
        run_config[SCORING_OUTPUT_DIR], 
        SCORING_OUTPUT_DIR)
    print

    # remove cache directory
    cache_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
    cache_dir = os.path.join(cache_dir, 'legacy_format', 'cache')
    # remove_cache_directory(cache_dir)

    # logs directory
    logs_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
    logs_dir = os.path.join(logs_dir, 'logs')
    if not os.path.exists(logs_dir):
        os.makedirs(logs_dir)

    #
    #  call matlab to translate input
    #

    matlab_func1 = "translate_input('{0}', '{1}', '{2}')".format(
        run_config[SCORING_OUTPUT_DIR], 
        run_config[TRAINING_DATA_DIR], 
        run_config[TEST_IMAGES_FILE])

    print 'running step Processing Inputs'
    os.chdir(THIS_DIR)

    run_matlab_function(matlab_func1, "translate_input", logs_dir)

    #
    #  call matlab to score
    #

    summary_file = os.path.dirname(run_config[TEST_IMAGES_FILE])
    summary_file = os.path.join(summary_file, 'legacy_format', 'input', 'summary.txt')
    print "summary_file is {0}".format(summary_file)

    matlab_func2 = "invoke_batskull_system('{0}','{1}')".format(
        summary_file, 
        "regime2")

    print 'running step Training and Scoring'
    os.chdir(os.path.join('bat','chain_rpm'))

    run_matlab_function(matlab_func2, "invoke_batskull_system", logs_dir)

    os.chdir(THIS_DIR)

    #
    #  call matlab to translate output
    #

    matlab_func3 = "translate_output('{0}', '{1}', '{2}')".format(
        run_config[SCORING_OUTPUT_DIR], 
        run_config[TRAINING_DATA_DIR], 
        run_config[TEST_IMAGES_FILE])

    print 'running step Processing Outputs'
    os.chdir(THIS_DIR)

    run_matlab_function(matlab_func3, "translate_output", logs_dir)

    print 'run completed'

# run script (or do nothing on import)
if __name__ == "__main__":
    main()
