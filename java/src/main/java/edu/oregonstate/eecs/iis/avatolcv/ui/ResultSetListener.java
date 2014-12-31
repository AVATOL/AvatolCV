package edu.oregonstate.eecs.iis.avatolcv.ui;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.SessionDataForTaxa;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class ResultSetListener {
    public void expressResultSet(RunSelector runSelector, JavaUI javaUI){
    	try {
			runSelector.expressDataForCurrentMetadata();
			String currentMatrixName = javaUI.getCurrentMatrixName();
			String newMatrixName = runSelector.getActiveMatrixName();
			if (!currentMatrixName.equals(newMatrixName)){
				MorphobankBundle bundle = javaUI.getMorphobankData().getBundle(newMatrixName);
				if (null == bundle){
					bundle = javaUI.getMorphobankData().loadMatrix(newMatrixName);
				}
				javaUI.setCurrentBundle(bundle);
			}
			SessionDataForTaxa sdft = runSelector.getSessionDataForTaxaForCurrentSession();
			// this will automatically render itself
			ResultMatrixColumn rmc = new ResultMatrixColumn(sdft, javaUI);
		}
		catch(AvatolCVException ace){
			ace.printStackTrace();
			System.out.println(ace.getMessage());
		}
		catch(MorphobankDataException mde){
			mde.printStackTrace();
			System.out.println(mde.getMessage());
		}
    }
}
