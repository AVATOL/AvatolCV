package edu.oregonstate.eecs.iis.avatolcv.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algata.ResultImage;

public class ThumbnailClickListener implements MouseListener {
	private ImageNavigator im = null;
	private ResultImage ri = null;
	private JLabel label = null;
	public ThumbnailClickListener(JLabel label, ResultImage ri, ImageNavigator im){
		this.im = im;
		this.ri = ri;
		this.label = label;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		try {
			im.unloadMainImage();
			im.getImageSet().setCurrentResultImage(ri);
			im.loadMainImage(ri);
			im.highlightThumbnail(this.label);
			im.revalidate();
		}
		
		catch(AvatolCVException e){
			e.printStackTrace();
			System.out.println("problem trying to render image for thumbnail " + e.getMessage());
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		try {
			if (!this.im.isResultImageSelected(this.ri)){
				this.label.setBorder(new LineBorder(Color.gray, ImageNavigator.THUMBNAIL_BORDER_WIDTH));
			}
		}
		catch(AvatolCVException ex){
			ex.printStackTrace();
			System.out.println("problem trying to render enteredd image for thumbnail " + ex.getMessage());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		try {
			if (!this.im.isResultImageSelected(this.ri)){
				this.label.setBorder(new LineBorder(Color.white, ImageNavigator.THUMBNAIL_BORDER_WIDTH));
			}
		}
		catch(AvatolCVException ex){
			ex.printStackTrace();
			System.out.println("problem trying to render enteredd image for thumbnail " + ex.getMessage());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
