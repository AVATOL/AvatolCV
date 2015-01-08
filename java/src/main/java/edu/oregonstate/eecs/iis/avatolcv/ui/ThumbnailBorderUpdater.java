package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class ThumbnailBorderUpdater implements Runnable {
	private JLabel label = null;
	private Color color = null;
	public ThumbnailBorderUpdater(Color c, JLabel label){
		this.color = c;
		this.label = label;
	}
	@Override
	public void run() {
		this.label.setBorder(new LineBorder(this.color, ImageNavigator.THUMBNAIL_BORDER_WIDTH));
	}
}
