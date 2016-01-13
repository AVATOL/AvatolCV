#!/usr/bin/python

import sys, os, os.path, shutil

def cheatcopy(run_config_path, path_of_pregen_data, output_dir_key):
    
    if (not(os.path.isdir(path_of_pregen_data))):
        print "{0} is not a valid directory to copy from".format(path_of_pregen_data)
        exit()
        
    files_to_copy = os.listdir(path_of_pregen_data)
    if (len(files_to_copy) == 0):
        print "{0} is an empty source directory - cannot copy pregenerated data".format(path_of_pregen_data)
        exit()
        
    #print "path of parentDir : {0}".format(path_of_parent)
    #print "path of preGenDir : {0}".format(path_of_pregen_data)
    print "run_config_path is {0}".format(run_config_path)
    with open(run_config_path) as runConfig:
        for line in runConfig:
            line = line.rstrip()
            key, val = line.split("=")
            if (key == output_dir_key):
                target_dir = val
                if (os.path.isdir(target_dir)):
                    print "copying files from {0} to {1}".format(path_of_pregen_data, target_dir)
                    cleanDirectory(target_dir)
                    copyFiles(path_of_pregen_data,target_dir)
                else: 
                    print "target dir {0} is not a valid directory".format(target_dir)
                    
def copyFiles(src_dir,target_dir):
    for the_file in os.listdir(src_dir):
        src_file_path = os.path.join(src_dir, the_file)
        dest_file_path = os.path.join(target_dir, the_file)
        print "copying file {0} to {1}".format(src_file_path, dest_file_path)
        try:
            shutil.copyfile(src_file_path, dest_file_path)
        except Exception, e:
            print e
def cleanDirectory(target_dir):
    for the_file in os.listdir(target_dir):
        file_path = os.path.join(target_dir, the_file)
        print "deleting file {0}".format(file_path)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
        except Exception, e:
            print e
      
