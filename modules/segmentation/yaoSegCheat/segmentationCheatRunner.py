import sys, os, os.path, imp

def main():
    if (len(sys.argv) < 2):
        usage()
        exit()
    run_config_path = sys.argv[1]
    print "path is {0}".format(run_config_path)
    path_of_this_script = os.path.realpath(__file__)
    print "path of this script : {0}".format(path_of_this_script)
    path_of_this_dir = os.path.dirname(path_of_this_script)
    path_of_pregen_data = os.path.join(path_of_this_dir, 'preGenData')
    dir_for_alg_type = os.path.dirname(path_of_this_dir)
    modules_dir = os.path.dirname(dir_for_alg_type)
    path_to_chreatcopy = os.path.join(modules_dir, 'python/cheatcopy.py')
    cheatcopy = imp.load_source('cheatcopy', path_to_chreatcopy)
    cheatcopy.cheatcopy(run_config_path, path_of_pregen_data, 'segmentationOutputDir')
    
def usage():
    print "usage:  python segmentationCheatRunner.py  <path of runConfigFile>"
    print ""
    print "   where some_command can be..."

if __name__ == "__main__":
   main()
