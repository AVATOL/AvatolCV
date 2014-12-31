package edu.oregonstate.eecs.iis.avatolcv.ui;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ScoredSetMetadatas;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;

public class JavaUI {
     private MorphobankData mbData = null;
     private ScoredSetMetadatas ssms = null;
     private ResultMatrixColumn currentMatrixColumn = null;
     private RunSelector currentRunSelector = null;
     private MorphobankBundle currentMorphobankBundle = null;
     private String currentMatrixName = null;
     public JavaUI(MorphobankData mbData){
    	 this.mbData = mbData;
     }
     public MorphobankData getMorphobankData(){
    	 return this.mbData;
     }
     public String getCurrentMatrixName(){
    	 return this.currentMatrixName;
     }
     public void setScoredSetMetadatas(ScoredSetMetadatas ssms){
    	 this.ssms = ssms;
    	 this.currentMatrixName = this.ssms.getMatrixNameFromKey(ssms.getCurrentKey());
     }
     public ResultMatrixColumn createResultMatrixColumn(SessionDataForTaxa sdft) throws AvatolCVException {
    	 ResultMatrixColumn matrixColumn = new ResultMatrixColumn(sdft, this);
    	 currentMatrixColumn = matrixColumn;
    	 return matrixColumn;
     }
     public RunSelector createRunSelector(){
    	 RunSelector runSelector = new RunSelector(this.ssms, this);
    	 this.currentRunSelector = runSelector;
    	 return runSelector;
     }
     public void setCurrentBundle(MorphobankBundle mb){
    	 this.currentMorphobankBundle = mb;
     }
     public MorphobankBundle getCurrentBundle(){
    	 return this.currentMorphobankBundle;
     }
}
