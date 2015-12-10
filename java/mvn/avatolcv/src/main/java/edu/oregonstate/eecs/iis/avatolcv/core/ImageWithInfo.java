package edu.oregonstate.eecs.iis.avatolcv.core;

import javafx.scene.image.Image;

public class ImageWithInfo extends Image{
	private ImageInfo ii = null;

	public ImageWithInfo(String arg0, ImageInfo ii) {
		super(arg0);
		this.ii = ii;
	}
	public ImageInfo getImageInfo(){
		return this.ii;
	}
}