import sys, os, os.path, imp
cheatcopy = imp.load_source('cheatcopy', '../../python/cheatcopy.py')

def main():
    if (len(sys.argv) < 2):
        usage()
        exit()
    run_config_path = sys.argv[1]
    #print "path is {0}".format(run_config_path)
    path_of_this_script = os.path.realpath(__file__)
    #print "path of this script : {0}".format(path_of_this_script)
    path_of_parent = os.path.dirname(path_of_this_script)
    path_of_pregen_data = os.path.join(path_of_parent, 'preGenData')
    cheatcopy.cheatcopy(run_config_path, path_of_pregen_data)
    
def usage():
    print "usage:  python segmentationCheatRunner.py  <path of runConfigFile>"
    print ""
    print "   where some_command can be..."

if __name__ == "__main__":
   main()
