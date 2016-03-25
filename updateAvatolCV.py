#!/usr/bin/python

import sys, os, os.path, shutil, urllib
import platform
import glob
import tarfile

def main():
    if (len(sys.argv) < 2):
        usage()
        exit()
    install_root = sys.argv[1]
    ensureDirExists(install_root)
    avatolcv_root = os.path.join(install_root, 'avatol_cv')
    print "...avatolcv_root determined as {0}".format(avatolcv_root)
    ensureDirExists(avatolcv_root)
    
    platform_code = getPlatformCode()
    print "...platform code determined as: {0}".format(platform_code)
    if (platform_code == 'unsupported'):
        print "...this platform ({0}) is not currently supported...exiting!".format( platform.system())
        exit()
    
    # pull in the manifest
    download_manifest_rootname = 'downloadManifest_' + platform_code
    manifest_truename = download_manifest_rootname + ".txt"
    new_manifest_filename = download_manifest_rootname + "_new.txt"
    print "...manifest to download is {0} and will be named {1}".format(manifest_truename, new_manifest_filename)
    
    new_manifest_path = downloadFile(avatolcv_root, new_manifest_filename, manifest_truename)
    
    # are we up to date?
    up_to_date = isUpToDate(avatolcv_root, download_manifest_rootname, new_manifest_path)
    if (up_to_date):
        print "...installed code is up to date... exiting!"
        exit()
        
    print "...installed code needs updating."
    uninstallIfNecessary(avatolcv_root, download_manifest_rootname)
    
    # delete old manifest , rename manifest_new to manifest
    old_manifest_pathname = getOldManifestPath(avatolcv_root, download_manifest_rootname)
    if (os.path.isfile(old_manifest_pathname)):
        print "...deleting prior manifest file {0}".format(old_manifest_pathname)
        deleteFile(old_manifest_pathname)
    print "...renaming new manifest {0} to old manifest {1}".format(new_manifest_path, old_manifest_pathname)
    os.rename(new_manifest_path, old_manifest_pathname)
    
    #
    installFilesInManifest(avatolcv_root, old_manifest_pathname)
    
def getOldManifestPath(avatolcv_root, download_manifest_rootname):
    old_manifest_filename = download_manifest_rootname + "_old.txt"
    old_manifest_pathname = os.path.join(avatolcv_root, old_manifest_filename)
    return old_manifest_pathname
    
def isUpToDate(avatolcv_root, download_manifest_rootname, new_manifest_path):
    old_manifest_pathname = getOldManifestPath(avatolcv_root, download_manifest_rootname)
    if (os.path.isfile(old_manifest_pathname)):
        # check if they are equal
        f_old = open(old_manifest_pathname, "r")
        old_lines = sorted(f_old.readlines());
        f_old.close()
        f_new = open(new_manifest_path, "r")
        new_lines = sorted(f_new.readlines())
        f_new.close()
        if (len(new_lines) != len(old_lines)):
            print "manifests are different lengths - update required"
            return false;
        for i in range(0, len(new_lines)):
            if (new_lines[i] != old_lines[i]):
                print "manifest entries do not match - old: {0} - new: {1}".format(old_lines[i], new_lines[i])
                return false
        return true
    else:
        print "existing manifest not found, install needs updating"
        return false
    
def installFilesInManifest(avatolcv_root, manifest_path):
    with open(manifest_path) as manifest:
        for line in manifest:
            line = line.rstrip()
            if (line.startswith("#")):
                print "installer skipping manifest comment: {0}".format(line)
                continue
            elif (line.endswith(".tgz")):
                downloaded_file = downloadFile(avatolcv_root, line, line)     
                unwrapFile(avatolcv_root, downloaded_file)
            else :
                print "skipping manifest line : {0}".format(line)

def unwrapFile(avatolcv_root, tgzFilePath):
    cur_dir = os.getcwd()
    os.chdir(avatolcv_root)
    tar = tarfile.open(tgzFilePath)
    tar.extractall()
    tar.close()
    os.chdir(cur_dir)
                
def downloadFile(avatolcv_root, manifest_filename, truename):
    target_path = os.path.join(avatolcv_root, manifest_filename)
    foo = urllib.URLopener()
    url = "http://web.engr.oregonstate.edu/~irvineje/AvatolCV/" + truename
    print "downloading url {0} to {1}".format(url, target_path)
    foo.retrieve(url, target_path)
    return target_path

def uninstallIfNecessary(avatolcv_root, download_manifest_rootname):
    present_download_manifest_name = getPresentDownloadManifest(avatolcv_root, download_manifest_rootname)
    # look for existing downloadManifest
    if (present_download_manifest_name != None):
        uninstall_files_in_manifest(avatolcv_root, present_download_manifest_name)
        
def uninstall_files_in_manifest(avatolcv_root, present_download_manifest_name):
    with open(present_download_manifest_name) as manifest:
        for line in manifest:
            line = line.rstrip()
            if (line.startswith("#")):
                print "...skipping manifest comment: {0}".format(line)
                continue
            else:
                # get bundlename from entry
                parts = line.split("_")
                if (len(parts) < 4):
                    print "...PROBLEM: entry in manifest malformed, should be downloadBundle_<name>_<platform_code>_<version>.tgz, but is {0}".format(line)
                    exit()
                bundle_name = parts[1]
                print "...looking for allFiles_{0}.txt".format(bundle_name)
                # look for allFiles_bundleName.txt
                all_files_path = os.path.join(avatolcv_root, 'allFiles_' + bundle_name + '.txt')
                if (os.path.isfile(all_files_path)):
                    # open it and delete each file found
                    print "...uninstalling bundle {0}".format(bundle_name)
                    delete_from_all_files(all_files_path)
                else :
                    print "...PROBLEM - could not uninstall bundle {0}".format(bundle_name)
 
def delete_from_all_files(all_files_path):
    with open(all_files_path) as paths:
        for path in paths:
            path = path.rstrip()
            deleteFile(path)
            
def deleteFile(path):
    try:
        if os.path.isfile(path):
            print "... uninstall path {0}".format(path)
            os.unlink(path)
    except Exception, e:
        print e     
        
def getPresentDownloadManifest(avatolcv_root, download_manifest_rootname):
    search_pattern = avatolcv_root + os.pathsep + download_manifest_rootname + '_old.txt'
    download_manifests = glob.glob(search_pattern)
    count = len(download_manifests)
    if (count == 0):
        print "...no existing download manifest named {0}... mut be initial download".format(search_pattern)
        return None
    elif (count == 1):
        print "...existing download manifest {0} will be used for uninstall.".format(download_manifests[0])
        return download_manifests[0]
    else:
        print "...PROBLEM - found more than one download manifest... choosing {0}".format(download_manifests[count - 1])
        return download_manifests[count - 1]
    
def ensureDirExists(dir):
    if (not(os.path.isdir(dir))):
        os.makedirs(dir)    
       
def usage():
    print "usage:  python updateAvatolCV.py  <installRoot>"
    print ""

def getPlatformCode():
    # get platform extension
    platform_name = platform.system()
    if (platform_name.startswith('Win')):
        platform_code = 'win'
    elif (platform_name.startswith('Mac')):
        platform_code = 'mac'
    else:
        platform_code = 'unsupported'
    print "platform is {0}".format(platform_code)
    return platform_code
    
if __name__ == "__main__":
   main()      
