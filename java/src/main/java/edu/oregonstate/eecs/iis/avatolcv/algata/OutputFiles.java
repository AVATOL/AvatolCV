package edu.oregonstate.eecs.iis.avatolcv.algata;

import java.io.File;
import java.util.Hashtable;
import java.util.Set;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class OutputFiles {
	private static final String NL = System.getProperty("line.separator");
	private static final String FILESEP = System.getProperty("file.separator");
	private String bundleDir = null;
	private Hashtable<String,OutputFile> outputFilesForCharacter = new Hashtable<String, OutputFile>();
	
	public OutputFiles(String path, String bundleDir) throws AvatolCVException {
		this.bundleDir = bundleDir;
		File f = new File(path);
		if (!f.exists()){
			throw new AvatolCVException("path given for output files does not exist: " + path);
		}
		if (!f.isDirectory()){
			throw new AvatolCVException("dirPath given for output files is not a directory: " + path);
		}
		File[] files = f.listFiles();
		for (File file : files){
			if (file.getName().startsWith("sorted_output")){
				OutputFile of = new OutputFile(file.getAbsolutePath(),this.bundleDir);
				String charId = of.getCharId();
				outputFilesForCharacter.put(charId, of);
			}
		}
	}
	public Hashtable<String,OutputFile> getOutputFilesForCharacter(){
		Hashtable<String,OutputFile> result = new Hashtable<String, OutputFile>();
		Set<String> keys = outputFilesForCharacter.keySet();
		for (String key : keys){
			result.put(key, outputFilesForCharacter.get(key));
		}
		return result;
	}
}

//___add method to get them from bundle
//___find where I matched on 'input' - shouldn't it be 'sorted_input'?
