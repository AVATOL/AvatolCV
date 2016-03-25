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
    ensureDirExists(avatolcv_root)
    
    platform_code = getPlatformCode()
    if (platform_code == 'unsupported'):
        print "this platform ({0}) is not cuirrently supported".format( platform.system())
        exit()
    
    download_manifest_rootname = 'downloadManifest_' + platform_code
    uninstallIfNecessary(avatolcv_root, download_manifest_rootname)
    manifest_filename = download_manifest_rootname + ".txt"
    manifest_path = downloadFile(avatolcv_root, manifest_filename)
    installFilesInManifest(avatolcv_root, manifest_path)
    

    
def installFilesInManifest(avatolcv_root, manifest_path):
    with open(manifest_path) as manifest:
        for line in manifest:
            if (line.startswith("#")):
                print "installer skipping manifest comment: {0}".format(line)
                continue
            elif (line.endswith(".tgz")):
                downloaded_file = downloadFile(avatolcv_root, line)     
                unwrapFile(avatolcv_root, downloaded_file)
            else :
                print "skipping manifest line : {0}".format(line)

def unwrapFile(avatolcv_root, tgzFilePath):
    with cd(avatolcv_root):
        tar = tarfile.open(tgzFilePath)
        tar.extractall()
        tar.close()
                
def downloadFile(avatolcv_root, manifest_filename):
    target_path = os.path.join(avatolcv_root, manifest_filename)
    u = urllib.URLopener()
    url = "http://web.engr.oregonstate.edu/~irvineje/AvatolCV/" + manifest_filename
    print "downloading url {0} to {1}".format(url, target_path)
    u.retrieve(url, target_path)
    return target_path

def uninstallIfNecessary(avatolcv_root, download_manifest_rootname):
    present_download_manifest_name = getPresentDownloadManifest(avatolcv_root, download_manifest_rootname)
    # look for existing downloadManifest
    if (present_download_manifest_name != None):
        uninstall_files_in_manifest(avatolcv_root, present_download_manifest_name)
        
def uninstall_files_in_manifest(avatolcv_root, present_download_manifest_name):
    with open(present_download_manifest_name) as manifest:
        for line in manifest:
            if (line.startswith("#")):
                print "skipping manifest comment: {0}".format(line)
                continue
            else:
                # get bundlename from entry
                parts = line.split("_")
                if (len(parts) < 4):
                    print "Problem: entry in manifest malformed, should be downloadBundle_<name>_<platform_code>_<version>.tgz, but is {0}".format(line)
                    exit()
                bundle_name = parts[1]
                # look for allFiles_bundleName.txt
                all_files_path = os.path.join(avatolcv_root, 'allFiles_' + bundle_name + '.txt')
                if (os.path.isfile(all_files_path)):
                    # open it and delete each file found
                    delete_from_all_files(all_files_path)
                else :
                    print "ERROR - could not uninstall bundle {0}".format(bundle_name)
 
def delete_from_all_files(all_files_path):
    with open(all_files_path) as paths:
        for path in paths:
            path = path.rstrip()
            try:
                if os.path.isfile(path):
                    print "uninstall... deleting path {0}".format(path)
                    os.unlink(path)
            except Exception, e:
                print e
                
def getPresentDownloadManifest(avatolcv_root, download_manifest_rootname):
    search_pattern = avatolcv_root + os.pathsep + download_manifest_rootname + '*.txt'
    download_manifests = glob.glob(search_pattern)
    count = len(download_manifests)
    if (count == 0):
        print "No existing download manifest... mut be initial download"
        return None
    elif (count == 1):
        print "existing download manifest {0} will be used for uninstall.".format(download_manifests[0])
        return download_manifests[0]
    else:
        print "Problem - found more than one download manifest... choosing {0}".format(download_manifests[count - 1])
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
