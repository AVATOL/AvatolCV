package edu.oregonstate.eecs.iis.avatolcv.ui;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class ImageBrowserSwitcher implements Runnable{
	private ImageBrowser ib = null;
	public ImageBrowserSwitcher(ImageBrowser ib){
		this.ib = ib;
	}
	@Override
	public void run() {
		try {
			System.out.println("ImageBrowserSwitcher calling ImageBrowser.hostImageBrowser");
			ImageBrowser.hostImageBrowser(this.ib);
		}
		catch(AvatolCVException ace){
			System.out.println(ace.getMessage());
			ace.printStackTrace();
		}
		catch(MorphobankDataException mde){
			System.out.println(mde.getMessage());
			mde.printStackTrace();
		}
	}
}
