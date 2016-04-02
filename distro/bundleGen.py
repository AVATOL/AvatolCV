#!/usr/bin/python
# python distro.py win docs,java C:\jed\avatol\git\avatol_cv\distro\cred.txt

import sys, os, os.path, shutil
import pysftp, datetime, tarfile

def main():
    # find the root path of avatol_cv
    path_of_this_script = os.path.realpath(__file__)
    distro_root_dir = os.path.dirname(path_of_this_script)
    avatol_cv_root = os.path.dirname(distro_root_dir)
    print "...avatol_cv_root : {0}".format(avatol_cv_root)
    
    # verify argument count
    if (len(sys.argv) != 4):
        usage()
        exit()
    platform_code = sys.argv[1]
    print "...platform code  : {0}".format(platform_code)
    
    bundle_list = sys.argv[2]
    bundles = bundle_list.split(",")
    manifest_dict = {}
    
    manifests_dir = os.path.join(distro_root_dir,"manifests")
    for bundle in bundles:
        print "...finding manifest file for bundle {0}".format(bundle)
        manifest_filename = 'distroManifest_' + bundle + '.txt'
        manifest_path = os.path.join(manifests_dir, manifest_filename)
        if (not(os.path.isfile(manifest_path))):
            print "...manifest {0} does not exist".format(manifest_path)
            exit()
        manifest_dict[bundle] = manifest_path
        
    credentials_path = sys.argv[3]
    if (not(os.path.isfile(credentials_path))):
        print "...credentials file {0} does not exist".format(credentials_path)
        exit()
    # get this connection first as its the weak link - if this is going to fail, lets not burn time generating distros that we can't determine versions for 
    sftp_connection = getSftpConnectionToPullSite(credentials_path)
    #print "skipping connection until ready"
    
    # list the filenames in the public_html/AvatolCV area on flip
    existing_filenames = sftp_connection.listdir()
    print "existing files : {0}".format(existing_filenames)
    #existing_filenames = [ ]
    #existing_filenames = [ 'downloadBundle_docs_win_20160330a.tgz','downloadBundle_java_win_20160330x.tgz' ]
    #existing_filenames = [ 'downloadBundle_docs_win_20160331a.tgz','downloadBundle_java_win_20160331x.tgz' ]
    #existing_filenames = [ 'downloadBundle_docs_win_20160331a.tgz', 'downloadBundle_docs_win_20160331b.tgz' ]
    #existing_filenames = [ 'downloadBundle_docs_win_20160331a.tgz', 'downloadBundle_docs_win_20160331b.tgz', 'downloadBundle_docs_win_20160331c.tgz' ]
    for bundle_name in bundles:
        bundle_full_name_root = getBundleManifestRoot(bundle_name,platform_code)
        distro_dir = prepareDistroDir(avatol_cv_root, bundle_name)
        manifest_path = manifest_dict[bundle_name]
        with open(manifest_path) as manifest:
            copyAsPerManifest(avatol_cv_root, bundle_name, manifest, distro_dir)
            
        # get version for this bundle    
        version = getVersionForBundle(bundle_full_name_root, existing_filenames)
        print "version for bundle {0} is {1}".format(bundle_name, version)
        bundle_zip_path = createGzippedTarFile(bundle_name, platform_code, version, distro_dir)
        sftp_connection.put(bundle_zip_path, preserve_mtime=True)
        
    sftp_connection.close()
    
def createGzippedTarFile(bundle_name, platform_code, version, distro_dir):
    cur_dir = os.getcwd()
    print "distro_dir is {0}".format(distro_dir)
    
    os.chdir(distro_dir)
    all_files_filename = 'allFiles_' + bundle_name + '.txt'
    all_files_pathname = os.path.join(distro_dir,all_files_filename)
    f = open(all_files_pathname, 'r')
    files = f.readlines()
    f.close()
    #downloadBundle_docs_win_20160304a.tgz
    tarfile_name = 'downloadBundle_' + bundle_name + '_' + platform_code + '_' + version + '.tgz'
    parent = os.path.dirname(distro_dir)
    print "parent is {0}".format(parent)
    #tarfile_path = os.path.join(parent, tarfile_name)
    tarfile_path = tarfile_name
    print "...tarfile path is {0}".format(tarfile_path)
    # NOTE using the |gz compression on the stream resulted in wierdness where a second shell of a .tgz file was created around the first one (?!)
    # so, doing gzip as separate step.
    tar = tarfile.open(tarfile_path, "w|gz")
    
    for file_rel_path in files:
        file_rel_path = file_rel_path.rstrip()
        print "adding to {0} : {1}".format(tarfile_name, file_rel_path)
        tar.add(file_rel_path)
    tar.add(all_files_filename)
    tar.close()
    
    bundle_root = os.path.dirname(distro_dir)
    distro_root = os.path.dirname(bundle_root)
    final_tgz_path = os.path.join(distro_root, tarfile_name)
    if (os.path.isfile(final_tgz_path)):
        deleteFile(final_tgz_path)
    print "moving file {0} to {1}".format(tarfile_path, distro_root)
    shutil.move(tarfile_path, final_tgz_path)
    os.chdir(cur_dir)
    return final_tgz_path
    
def getVersionForBundle(bundle_name_root, existing_filenames):
    prior_versions_of_bundle = []
    todays_datestamp = getTodaysDatestamp()
    for bundle_filename in existing_filenames:
        if bundle_filename.startswith(bundle_name_root):
            prior_versions_of_bundle.append(bundle_filename)

    count = len(prior_versions_of_bundle)
    if count == 0:
        return todays_datestamp + 'a'
    else:
        prior_versions_of_bundle.sort();
        print "......{0}".format(prior_versions_of_bundle)
        most_recent_bundle = prior_versions_of_bundle[len(prior_versions_of_bundle)-1]
        print "......most recent bundle {0}".format(most_recent_bundle)
        date_of_most_recent_version = getDateFromBundleName(most_recent_bundle)
        print "......date_of_most_recent_version {0}".format(date_of_most_recent_version)
        if date_of_most_recent_version == todays_datestamp:
            suffix = getVersionSuffixFromBundleName(most_recent_bundle)
            new_suffix = getNextSuffix(suffix)
            print "......new suffix : {0}".format(new_suffix)
            return todays_datestamp + new_suffix
        else:
            return todays_datestamp + 'a'

def getNextSuffix(suffix_letter):
    dict = {}
    dict['a'] = 'b'
    dict['b'] = 'c'
    dict['c'] = 'd'
    dict['d'] = 'e'
    dict['e'] = 'f'
    dict['f'] = 'g'
    dict['g'] = 'h'
    dict['h'] = 'i'
    dict['i'] = 'j'
    dict['j'] = 'k'
    dict['k'] = 'l'
    dict['l'] = 'm'
    dict['m'] = 'n'
    dict['n'] = 'o'
    dict['o'] = 'p'
    dict['p'] = 'q'
    dict['q'] = 'r'
    dict['r'] = 's'
    dict['s'] = 't'
    dict['t'] = 'u'
    dict['u'] = 'v'
    dict['v'] = 'w'
    dict['w'] = 'x'
    dict['x'] = 'y'
    dict['y'] = 'z'
    dict['z'] = '?'
    return dict[suffix_letter]
    
def getVersionSuffixFromBundleName(name):
    #downloadBundle_docs_win_20160304a.tgz
    major_parts = name.split('.')
    parts = major_parts[0].split('_')
    version = parts[3]
    suffix = version[8:]
    return suffix
	            
def getDateFromBundleName(name):
    #downloadBundle_dummy1_win_20160304a.tgz
    major_parts = name.split('.')
    parts = major_parts[0].split('_')
    version = parts[3]
    datestamp = version[:8]
    return datestamp
	

def getTodaysDatestamp():
    now = datetime.datetime.now()
    datestamp = now.strftime("%Y%m%d")
    return datestamp
	
def prepareDistroDir(avatol_cv_root, bundle_name):
    distro_dir = getBundleDistroDir(avatol_cv_root, bundle_name)
    print "...distro_dir     : {0}".format(distro_dir)
    print ""
    ensureDirExists(distro_dir)
    cleanDirectory(distro_dir)
    print ""
    return distro_dir

def getBundleManifestRoot(bundle_name, platform_code):
    print "working bundle {0}".format(bundle_name)
    #downloadBundle_dummy1_win_20160304a.tgz
    bundle_full_name_root = 'downloadBundle_' + bundle_name + '_' + platform_code
    print "...bundle root    : {0}".format(bundle_full_name_root)
    return bundle_full_name_root
   
def copyAsPerManifest(avatol_cv_root, bundle_name, manifest, distro_dir):
    all_files = []
    for line in manifest:
        if (line.startswith("#")):
            #print "...skipping comment: {0}".format(line)
            continue
        line = line.replace("/",os.path.sep)    
        rel_path = line.rstrip()
        full_path = os.path.join(avatol_cv_root, rel_path)
        #print "...full_path   {0}".format(full_path)
                
        target_path = os.path.join(distro_dir, rel_path)
        #print "...target_path {0}".format(target_path)
        if (os.path.isfile(full_path)):
            print "......copyFile {0} -> {1}".format(full_path, target_path)
            copyFile(full_path, target_path)
            rel_path = rel_path.replace('\\', '/')
            all_files.append(rel_path)
        elif (os.path.isdir(full_path)):
            #print "copyFiles {0} -> {1}".format(full_path, target_path)
            files_copied = copyFiles(avatol_cv_root, full_path, target_path)
            all_files.extend(files_copied)
        else :
            print "...skipping manifest entry: {0}".format(full_path)
        all_files_path = os.path.join(distro_dir, 'allFiles_'+bundle_name+'.txt')
        f = open(all_files_path, "w")
        for copied_file in all_files:
            f.write("%s\n" % copied_file)
        f.close()

def getSftpConnectionToPullSite(credentials_path):
    dict = getCredentialsDict(credentials_path)
    hostname  = dict['host']
    user      = dict['username']
    pw        = dict['password']
    releasedir= dict['releasedir']
    print "credentials read as:"
    print "    host      : {0}".format(hostname)
    print "    username  : {0}".format(user)
    print "    password  : {0}".format(pw)
    print "    releasedir : {0}".format(releasedir)
    conn = pysftp.Connection(host=hostname, username=user, password=pw)
    conn.chdir(releasedir)
    return conn

def getBundleDistroDir(root, bundle_name):
    distro_dir =  os.path.join(root, 'distro')
    distro_dir = os.path.join(distro_dir, bundle_name)
    distro_dir = os.path.join(distro_dir, 'avatol_cv')
    return distro_dir

def getCredentialsDict(path):
    dict = {}
    if (not(os.path.isfile(path))):
        return dict
    f = open(path, "r")
    lines = sorted(f.readlines())
    f.close()
    for line in lines:
        line = line.rstrip()
        if (len(line) == 0):
            continue
        if (line.startswith("#")):
            continue
        key, val = line.split('=')
        dict[key] = val
    return dict 
    
def ensureDirExists(dir):
    if (not(os.path.isdir(dir))):
        os.makedirs(dir)    
    
def usage():
    print "usage:  python distro.py  <platform_code> <bundle list> <path of credentials file>"
    print "...where platform_code == win | mac"
    print "...credentials file has host, username and password for site to push to" 
    print "...bundle list is a comma separated list of bundle names from this set:   docs,modules_3rdparty, modules_osu,java"
    print ""
 
def copyFile(src_file_path, dest_file_path):
    try:
        dest_dir = os.path.dirname(dest_file_path)
        ensureDirExists(dest_dir)
        shutil.copyfile(src_file_path, dest_file_path)
    except Exception, e:
        print e
                    
def copyFiles(avatol_cv_root, src_dir, target_dir):
    files_copied = []
    for the_file in os.listdir(src_dir):
        src_file_path = os.path.join(src_dir, the_file)
        dest_file_path = os.path.join(target_dir, the_file)
        if (os.path.isdir(src_file_path)):
            
        elif (os.path.isfile(src_file_path)) :
            print "......copy {0} to {1}".format(src_file_path, dest_file_path)
            copyFile(src_file_path, dest_file_path)
            root_len = len(avatol_cv_root)
            root_len_plus_1 = root_len + 1
            rel_path = src_file_path[root_len_plus_1:]
            rel_path = rel_path.replace('\\', '/')
            #print "...rel_path determined as {0}".format(rel_path)
            files_copied.append(rel_path)
        else :
        
        
    return files_copied
    
def deleteFile(path):
    try:
        if os.path.isfile(path):
            os.unlink(path)
    except Exception, e:
        print e
def cleanDirectory(target_dir):
    for the_file in os.listdir(target_dir):
        file_path = os.path.join(target_dir, the_file)
        print "......deleting file {0}".format(file_path)
        deleteFile(file_path)

if __name__ == "__main__":
   main()      
