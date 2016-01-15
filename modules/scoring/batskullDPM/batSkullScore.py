#!/usr/bin/env python
import argparse
import os
import subprocess

def remove_cache_directory(cache_dir):
    '''Delete the cache directory'''

    print "cache_dir is {0}".format(cache_dir)
    print

    os.remove(cache_dir)

def run_matlab_function(func_string, func_name):
    '''Wraps function with try-catch to exit MATLAB on errors'''

    wrapped_func_string = "try;{0};catch exception;disp(getReport(exception));exit(1);end;exit".format(func_string)
    
    print "executing: {0}".format(wrapped_func_string)
    print

    # execute MATLAB
    exit_status = subprocess.call([
        "/Applications/MATLAB_R2015b.app/bin/matlab", 
        "-nodisplay", 
        '-r "{0}"'.format(wrapped_func_string)])
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
    cache_dir = os.path.join(cache_dir, 'legacy_format/cache/')
    # remove_cache_directory(cache_dir)

    #
    #  call matlab to translate input
    #

    matlab_func1 = "translate_input('{0}', '{1}', '{2}')".format(
        run_config[SCORING_OUTPUT_DIR], 
        run_config[TRAINING_DATA_DIR], 
        run_config[TEST_IMAGES_FILE])

    print 'running step Processing Inputs'
    os.chdir(THIS_DIR)

    run_matlab_function(matlab_func1, "translate_input")

    #
    #  call matlab to score
    #

    summary_file = os.path.dirname(run_config[TEST_IMAGES_FILE])
    summary_file = os.path.join(summary_file, 'legacy_format/input/summary.txt')
    print "summary_file is {0}".format(summary_file)

    matlab_func2 = "invoke_batskull_system('{0}','{1}')".format(
        summary_file, 
        "regime2")

    print 'running step Training and Scoring'
    os.chdir('bat/chain_rpm')

    run_matlab_function(matlab_func2, "invoke_batskull_system")

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

    run_matlab_function(matlab_func3, "translate_output")

    print 'run completed'

# run script (or do nothing on import)
if __name__ == "__main__":
    main()
