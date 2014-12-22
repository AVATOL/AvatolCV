package edu.oregonstate.eecs.iis.avatolcv;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import edu.oregonstate.eecs.iis.avatolcv.mb.PointAsPercent;

public class TestPointAsPercent {

	@Test
	public void test() {
		PointAsPercent p = new PointAsPercent(50.0,40.0);
		Assert.assertTrue(p.getXPixel(50) == 25);
		Assert.assertTrue(p.getYPixel(50) == 20);
		Assert.assertTrue(p.getXPixel(100) == 50);
		Assert.assertTrue(p.getYPixel(100) == 40);
		Assert.assertTrue(p.getXPixel(200) == 100);
		Assert.assertTrue(p.getYPixel(200) == 80);
		
		p = new PointAsPercent(50.4,40.4);
		Assert.assertTrue(p.getXPixel(50) == 25);
		Assert.assertTrue(p.getYPixel(50) == 20);
		Assert.assertTrue(p.getXPixel(100) == 50);
		Assert.assertTrue(p.getYPixel(100) == 40);
		Assert.assertTrue(p.getXPixel(200) == 100);
		Assert.assertTrue(p.getYPixel(200) == 80);

		p = new PointAsPercent(50.5,40.5);
		Assert.assertTrue(p.getXPixel(50) == 25);
		Assert.assertTrue(p.getYPixel(50) == 20);
		Assert.assertTrue(p.getXPixel(100) == 50);
		Assert.assertTrue(p.getYPixel(100) == 40);
		Assert.assertTrue(p.getXPixel(200) == 101);
		Assert.assertTrue(p.getYPixel(200) == 81);
		
		p = new PointAsPercent(10.0,10);
		Assert.assertTrue(p.getXPixel(50) == 5);
		Assert.assertTrue(p.getYPixel(50) == 5);
		Assert.assertTrue(p.getXPixel(100) == 10);
		Assert.assertTrue(p.getYPixel(100) == 10);
		Assert.assertTrue(p.getXPixel(200) == 20);
		Assert.assertTrue(p.getYPixel(200) == 20);
	}

}
