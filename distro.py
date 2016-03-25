#!/usr/bin/python

import sys, os, os.path, shutil

def main():
    if (len(sys.argv) < 2):
        usage()
        exit()
    manifest_path = sys.argv[1]
    if (not(os.path.isfile(manifest_path))):
        print "{0} does not exist".format(manifest_path)
        exit()
    
    # find the root path of avatol_cv
    path_of_this_script = os.path.realpath(__file__)
    print "path of this script : {0}".format(path_of_this_script)
    avatol_cv_root = os.path.dirname(path_of_this_script)
    
    distroDir = getDistroDir(avatol_cv_root)
    ensureDirExists(distroDir)
    cleanDirectory(distroDir)
    
    with open(manifest_path) as manifest:
        for line in manifest:
            if (line.startswith("#")):
                print "skipping comment: {0}".format(line)
                continue
            line = line.replace("/",os.path.sep)    
            relPath = line.rstrip()
            fullPath = os.path.join(avatol_cv_root, relPath)
            print "fullPath {0}".format(fullPath)
            
            targetPath = getTargetPath(avatol_cv_root,relPath) 
            print "targetPath {0}".format(targetPath)
            if (os.path.isfile(fullPath)):
                #print "copyFile {0} -> {1}".format(fullPath, targetPath)
                copyFile(fullPath, targetPath)
            
            elif (os.path.isdir(fullPath)):
                #print "copyFiles {0} -> {1}".format(fullPath, targetPath)
                copyFiles(fullPath, targetPath)
                
            else :
                print "skipping manifest entry: {0}".format(fullPath)

def getDistroDir(root):
    distro_dir =  os.path.join(root, 'distro')
    return distro_dir
    
def ensureDirExists(dir):
    if (not(os.path.isdir(dir))):
        os.makedirs(dir)    
    
def getTargetPath(root, relPath):
    result = os.path.join(root, 'distro')
    result = os.path.join(result, 'avatol_cv')
    result = os.path.join(result, relPath)
    return result
    
def usage():
    print "usage:  python distro.py  <path of manifest>"
    print ""

   
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

def copyFile(src_file_path, dest_file_path):
    try:
        dest_dir = os.path.dirname(dest_file_path)
        ensureDirExists(dest_dir)
        shutil.copyfile(src_file_path, dest_file_path)
    except Exception, e:
        print e
                    
def copyFiles(src_dir,target_dir):
    for the_file in os.listdir(src_dir):
        src_file_path = os.path.join(src_dir, the_file)
        dest_file_path = os.path.join(target_dir, the_file)
        print "copying file {0} to {1}".format(src_file_path, dest_file_path)
        copyFile(src_file_path, dest_file_path)
        
def cleanDirectory(target_dir):
    for the_file in os.listdir(target_dir):
        file_path = os.path.join(target_dir, the_file)
        print "deleting file {0}".format(file_path)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
        except Exception, e:
            print e

if __name__ == "__main__":
   main()      
