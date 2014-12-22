package edu.oregonstate.eecs.iis.avatolcv.mb;

public class PointAsPercent {
	private double x;
	private double y;
    public PointAsPercent(double x, double y){
    	this.x = x;
    	this.y = y;
    }
    public double getX(){
    	return this.x;
    }
    public double getY(){
    	return this.y;
    }
    public int getXPixel(int imageWidthInPixels){
    	int result = (int)(0.01 * this.x * imageWidthInPixels);
    	return result;
    }
    public int getYPixel(int imageHeightInPixels){
    	int result = (int)(0.01 * this.y * imageHeightInPixels);
    	return result;
    }
}
