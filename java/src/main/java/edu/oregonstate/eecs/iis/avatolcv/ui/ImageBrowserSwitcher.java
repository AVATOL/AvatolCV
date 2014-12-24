package edu.oregonstate.eecs.iis.avatolcv.ui;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class ImageBrowserSwitcher implements Runnable{
	private ImageBrowser ib = null;
	public ImageBrowserSwitcher(ImageBrowser ib){
		this.ib = ib;
	}
	@Override
	public void run() {
		try {
			ImageBrowser.hostImageBrowser(this.ib);
		}
		catch(AvatolCVException ace){
			System.out.println(ace.getMessage());
			ace.printStackTrace();
		}
	}
}
