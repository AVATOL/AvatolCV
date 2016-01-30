package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

/*
 * 
dependency:pathLibSsvmMatlab=<modules>/3rdParty/libsvm/libsvm-318/matlab
dependency:pathVlfeat=<modules>/3rdParty/vlfeat/vlfeat-0.9.20/toolbox/v1_setup

 */
public class AlgorithmDependency {
    private static final String usage = "dependency:someKey=<modules>/a/b/c  or  dependency:someKey=/a/b/c";
    private String key = null;
    private String path = null;
    public AlgorithmDependency(String line) throws AvatolCVException {
        // remove the declarationKey
        String dependencyInfo = line.replace(Algorithm.DECLARATION_DEPENDENCY, "");
        if ("".equals(dependencyInfo)){
            expressUsageError(line);
        }
        if (!dependencyInfo.contains("=")){
            expressUsageError(line);
        }
        String[] depInfoParts = ClassicSplitter.splitt(dependencyInfo,'=');
        if (depInfoParts.length < 2){
            expressUsageError(line);
        }
        this.key = depInfoParts[0];
        this.path = depInfoParts[1];
        if ("".equals(key)){
            expressUsageError(line);
        }
        if ("".equals(this.path)){
            expressUsageError(line);
        }
        // check the path to see if <modules> needs to be resolved
        if (this.path.contains("<modules>")){
            String modulesDir = AvatolCVFileSystem.getModulesDir();
            this.path = this.path.replace("<modules>", modulesDir);
        }
    }
    private void expressUsageError(String line) throws AvatolCVException {
        throw new AvatolCVException("dependency: declaration malformed : " + line + " should be " + usage);
    }
    public String getKey(){
        return this.key;
    }
    public String getPath(){
        return this.path;
    }
}
