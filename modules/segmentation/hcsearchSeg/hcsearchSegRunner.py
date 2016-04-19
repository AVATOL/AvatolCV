#!/usr/bin/env python
import argparse
import os
import subprocess
import platform
import time
import traceback
import shutil

# paths to MATLAB
MAC_MATLAB_PATH = "/Applications/MATLAB_R2015b.app/bin/matlab"
WIN_MATLAB_PATH = "C:\\Program Files\\MATLAB\\R2015b\\bin\\matlab.exe"

# training mode
# 0 = use shipped models, no training
# 1 = use shipped training images for training (images not yet uploaded)
# 2 = use provided training images for training (not yet implemented)
TRAIN_OPTION = 0

# time bound for HC-Search
TIME_BOUND = 10

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

def run_hc_search(input_dir, output_dir, time_bound, logs_dir, base_dir, infer_only):
    '''Calls HC-Search command line.'''

    # check OS and use appropriate command/arguments
    stdout_logfile = os.path.join(logs_dir,
        'hcsearch_run_stdout_{0}.txt'.format(int(time.time())))
    stderr_logfile = os.path.join(logs_dir,
        'hcsearch_run_stderr_{0}.txt'.format(int(time.time())))
    application = []
    HC_SEARCH_PATH = os.path.join('nematocyst/', 'HCSearch')
    if platform.system() == 'Darwin': # Mac
        application = [
        HC_SEARCH_PATH,
        '{}'.format(input_dir),
        '{}'.format(output_dir),
        str(time_bound)]

        if not infer_only:
            application.append("--learn")

        application.extend(["--infer",
        "--prune",
        "none",
        "--ranker",
        "vw",
        "--successor",
        "flipbit-neighbors",
        "--base-path",
        '{}'.format(base_dir)])
        # for Mac, have to separate arguments like this
    elif platform.system() == 'Windows': # Windows
        application = [
        HC_SEARCH_PATH,
        '{}'.format(input_dir),
        '{}'.format(output_dir),
        str(time_bound)]

        if not infer_only:
            application.append("--learn")

        application.extend(["--infer",
        "--prune",
        "none",
        "--ranker",
        "vw",
        "--successor",
        "flipbit-neighbors",
        "--base-path",
        '{}'.format(base_dir)])
        # for Windows, have to separate arguments like this
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
    # application = [HC_SEARCH_PATH, '--help'] # DEBUG TODO

    print "Calling HC-Search command line: {}".format(application)
    print

    # execute HC-Search
    try:
        with open(stdout_logfile, 'w') as stdout:
            with open(stderr_logfile, 'w') as stderr:
                exit_status = subprocess.call(application, stdout=stdout, stderr=stderr)
    except:
        print "Could not successfully call HC-Search command."
        traceback.print_exc()
        exit(1)

    if exit_status != 0:
        print "HC-Search exited with error."
        print
        print "running step Error"
        exit(1)

    print "HC-Search ran without error."

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
    else:
        run_config[TRAIN_IMAGES_FILE] = ""
    if GT_IMAGES_FILE in run_config:
        print "run_config['{1}'] is {0}".format(
            run_config[GT_IMAGES_FILE],
            GT_IMAGES_FILE)
    else:
        run_config[GT_IMAGES_FILE] = ""
    print

    # make logs directory
    logs_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
    logs_dir = os.path.join(logs_dir, 'logs')
    logs_dir = os.path.join(logs_dir, 'segmentation')
    if not os.path.exists(logs_dir):
        os.makedirs(logs_dir)

    # make temp directory
    print "creating temp directory"
    temp_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
    temp_dir = os.path.join(temp_dir, 'segmentationTemp')
    images_processed_dir = os.path.join(temp_dir, 'imagesPreprocessed')
    hcsearch_output_dir = os.path.join(temp_dir, 'hcSearchOutput')
    if not os.path.exists(images_processed_dir):
        os.makedirs(images_processed_dir)
    if not os.path.exists(hcsearch_output_dir):
        os.makedirs(hcsearch_output_dir)

    # copy test images to temp folder
    print "copying test images to temp"
    test_temp_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
    test_temp_dir = os.path.join(test_temp_dir, 'segmentationTemp')
    test_temp_dir = os.path.join(test_temp_dir, 'images')
    if not os.path.exists(test_temp_dir):
        os.makedirs(test_temp_dir)
    with open(run_config[TEST_IMAGES_FILE], 'r') as f:
        for line in f:
            file_name_base = os.path.basename(line.strip())
            shutil.copyfile(line.strip(), os.path.join(test_temp_dir, file_name_base))

    if TRAIN_OPTION == 0:
        # copy shipped model files
        print "copying shipped models to temp"
        shipped_models_dir = os.path.join(THIS_DIR, 'models')
        shipped_models = ["codebook.txt", "edgeclassifier_model.txt",
            "edgeclassifier_training.txt", "initfunc_model.txt",
            "initfunc_training.txt"]
        for m in shipped_models:
            shutil.copyfile(os.path.join(shipped_models_dir, m),
                os.path.join(images_processed_dir, m))

        hcsearch_models_dir = os.path.join(hcsearch_output_dir, 'models')
        if not os.path.exists(hcsearch_models_dir):
            os.makedirs(hcsearch_models_dir)
        shipped_models = ["model_cost.txt", "model_cost.txt.model",
            "model_heuristic.txt", "model_heuristic.txt.model"]
        for m in shipped_models:
            shutil.copyfile(os.path.join(shipped_models_dir, m),
                os.path.join(hcsearch_models_dir, m))

        # set to not train images
        run_config[TRAIN_IMAGES_FILE] = ""
        run_config[GT_IMAGES_FILE] = ""
        shipped_training_images_file = ""

    elif TRAIN_OPTION == 1:
        # copy shipped training images
        print "copying shipped training images to temp"
        train_temp_dir = os.path.dirname(run_config[TEST_IMAGES_FILE])
        train_temp_dir = os.path.join(train_temp_dir, 'segmentationTemp')
        train_temp_dir = os.path.join(train_temp_dir, 'images')
        if not os.path.exists(train_temp_dir):
            os.makedirs(train_temp_dir)
        shipped_training_images_file = os.path.join(THIS_DIR, 'models/', 'training/', 'training_images_list.txt')
        training_images_dir = os.path.join(THIS_DIR, 'models/', 'training/', 'images/')
        gt_images_dir = os.path.join(THIS_DIR, 'models/', 'training/', 'groundtruth/')
        with open(shipped_training_images_file, 'r') as f:
            for line in f:
                file_name_base = line.strip()
                shutil.copyfile(os.path.join(training_images_dir, '{0}.jpg'.format(file_name_base)),
                    os.path.join(train_temp_dir, '{0}.jpg'.format(file_name_base)))
                shutil.copyfile(os.path.join(gt_images_dir, '{0}.jpg'.format(file_name_base)),
                    os.path.join(train_temp_dir, '{0}.jpg'.format(file_name_base)))

    else:
        print "Unknown/unimplemented TRAIN_OPTION: {0}".format(TRAIN_OPTION)
        print
        print "running step Error"
        exit(1)

    #
    #  call matlab...
    #

    matlab_func1 = "preprocess_for_hcsearch('{0}', '{1}', '{2}', '{3}', '{4}', '{5}')".format(
        run_config[SEGMENTATION_OUTPUT_DIR],
        run_config[TEST_IMAGES_FILE],
        run_config[TRAIN_IMAGES_FILE],
        run_config[GT_IMAGES_FILE],
        shipped_training_images_file,
        THIRD_PARTY_DIR)

    print 'running step Preprocessing'
    os.chdir(THIS_DIR)

    run_matlab_function(matlab_func1, "preprocess_for_hcsearch", logs_dir)

    #
    #  call HC-Search...
    #
    base_dir = os.path.join(THIS_DIR, 'nematocyst/')
    infer_only = TRAIN_OPTION == 0

    print 'running step Segmenting'
    os.chdir(THIS_DIR)

    run_hc_search(images_processed_dir, hcsearch_output_dir, TIME_BOUND, logs_dir, base_dir, infer_only)

    #
    #  call matlab...
    #

    matlab_func2 = "postprocess_for_hcsearch('{0}', '{1}', '{2}', '{3}', '{4}')".format(
        run_config[SEGMENTATION_OUTPUT_DIR],
        run_config[TEST_IMAGES_FILE],
        run_config[TRAIN_IMAGES_FILE],
        run_config[GT_IMAGES_FILE],
        TIME_BOUND)

    print 'running step Postprocessing'
    os.chdir(THIS_DIR)

    run_matlab_function(matlab_func2, "postprocess_for_hcsearch", logs_dir)

    #
    #  copy original images
    #

    # copy original images to segmentedData/*_orig.jpg
    print "copying original images"
    with open(run_config[TEST_IMAGES_FILE], 'r') as f:
        for line in f:
            orig_image_path = line.strip()
            old_file_name_base = os.path.splitext(os.path.basename(orig_image_path))[0]
            new_file_name = '{}_orig.jpg'.format(old_file_name_base)
            dest = os.path.join(run_config[SEGMENTATION_OUTPUT_DIR], new_file_name)
            shutil.copyfile(orig_image_path, dest)

    print 'run completed'

# run script (or do nothing on import)
if __name__ == "__main__":
    main()
