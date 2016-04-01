#!/usr/bin/python

import sys, os, os.path, shutil, urllib
import platform
import glob
import tarfile
import pdb

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
    
    new_manifest_pathname = downloadFile(avatolcv_root, new_manifest_filename, manifest_truename)
    
    
    old_manifest_pathname = getOldManifestPath(avatolcv_root, download_manifest_rootname)
    show_manifest(old_manifest_pathname, "old manifest")
    show_manifest(new_manifest_pathname, "new manifest")
    # are we up to date?
    bundles_to_download = getBundlesToDownload(old_manifest_pathname, new_manifest_pathname)
    if (len(bundles_to_download) == 0):
        print "...installed code is up to date... exiting!"
        deleteFile(new_manifest_pathname)
        exit()
        
    print "...the following bundles need updating {0}".format(bundles_to_download)
    uninstall_bundles(avatolcv_root, bundles_to_download)
    
    # delete old manifest , rename manifest_new to manifest
    old_manifest_pathname = getOldManifestPath(avatolcv_root, download_manifest_rootname)
    if (os.path.isfile(old_manifest_pathname)):
        print "...deleting prior manifest file {0}".format(old_manifest_pathname)
        deleteFile(old_manifest_pathname)
    print "...renaming new manifest {0} to old manifest {1}".format(new_manifest_pathname, old_manifest_pathname)
    os.rename(new_manifest_pathname, old_manifest_pathname)
    
    #
    installBundles(avatolcv_root, old_manifest_pathname, bundles_to_download)

def show_manifest(path, description):
    print "========================================================="
    if (not(os.path.isfile(path))):
        print "{0} - no manifest present".format(description)
        print "========================================================="
        return
    f = open(path, "r")
    lines = sorted(f.readlines())
    f.close()
    print "{0}".format(description)
    for line in lines:
        line = line.rstrip()
        print "{0}".format(line)
    print "========================================================="
    
def installBundles(avatolcv_root, manifest_path, bundles_to_download):
    bundles_dict = getBundlesDict(manifest_path)
    for bundle in bundles_to_download:
        print "...installing bundle {0}".format(bundle)
        filename_to_download = bundles_dict[bundle]
        downloaded_file = downloadFile(avatolcv_root, filename_to_download, filename_to_download)     
        unwrapFile(avatolcv_root, downloaded_file)
                
def getOldManifestPath(avatolcv_root, download_manifest_rootname):
    old_manifest_filename = download_manifest_rootname + "_old.txt"
    old_manifest_pathname = os.path.join(avatolcv_root, old_manifest_filename)
    return old_manifest_pathname
    
def getVersionsDict(path):
    version_dict = {}
    if (not(os.path.isfile(path))):
        return version_dict
    f = open(path, "r")
    lines = sorted(f.readlines())
    f.close()
    for line in lines:
        line = line.rstrip()
        if (len(line) == 0):
            continue
        if (line.startswith("#")):
            continue
        name_parts = line.split('.')
        file_root = name_parts[0]
        parts = file_root.split('_')
        if (len(parts) < 4):
            print "...ERROR - manifest line is malformed : {0}".format(line)
        else:
            key = parts[1]
            version = parts[3]
            print "adding key and version to version_dict : {0} {1}".format(key, version)
            version_dict[key] = version
    return version_dict

def getBundlesDict(path):
    bundles_dict = {}
    if (not(os.path.isfile(path))):
        return bundles_dict
    f = open(path, "r")
    lines = sorted(f.readlines())
    f.close()
    for line in lines:
        line = line.rstrip()
        if (len(line) == 0):
            continue
        if (line.startswith("#")):
            continue
        name_parts = line.split('.')
        file_root = name_parts[0]
        parts = file_root.split('_')
        if (len(parts) < 4):
            print "...ERROR - manifest line is malformed : {0}".format(line)
        else:
            key = parts[1]
            print "adding key and line to modules dict : {0} {1}".format(key, line)
            bundles_dict[key] = line
    return bundles_dict    
    
def getBundlesToDownload(old_manifest_pathname, new_manifest_pathname):
    #pdb.set_trace()
    old_versions_dict = getVersionsDict(old_manifest_pathname)
    new_versions_dict = getVersionsDict(new_manifest_pathname)
    bundles_to_download = []
    new_keys = new_versions_dict.keys()
    for new_key in new_keys:
        if (not(old_versions_dict.has_key(new_key))):
            bundles_to_download.append(new_key)
        else:
            new_version_for_key = new_versions_dict[new_key]
            old_version_for_key = old_versions_dict[new_key]
            if (not(new_version_for_key == old_version_for_key)):
                bundles_to_download.append(new_key)
    return bundles_to_download;
  
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

def uninstall_bundles(avatolcv_root, bundles_to_uninstall):
    for bundle in bundles_to_uninstall:
        print "...looking for allFiles_{0}.txt".format(bundle)
        # look for allFiles_bundleName.txt
        all_files_path = os.path.join(avatolcv_root, 'allFiles_' + bundle + '.txt')
        if (os.path.isfile(all_files_path)):
            # open it and delete each file found
            print "...uninstalling bundle {0}".format(bundle)
            delete_from_all_files(avatolcv_root, all_files_path)
            deleteFile(all_files_path)
        else :
            print "...WARNING - could not find bundle {0} to uninstall".format(bundle)
                
def delete_from_all_files(avatolcv_root, all_files_path):
    with open(all_files_path) as rel_paths:
        for rel_path in rel_paths:
            rel_path = rel_path.rstrip()
            path = os.path.join(avatolcv_root, rel_path)
            deleteFile(path)
            
def deleteFile(path):
    try:
        if os.path.isfile(path):
            print "... deleting {0}".format(path)
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
    elif (platform_name.startswith('Darwin')):
        platform_code = 'mac'
    else:
        platform_code = 'unsupported'
    return platform_code
    
if __name__ == "__main__":
   main()      
