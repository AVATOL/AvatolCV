package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class NormalizedKey extends NormalizedTypeIDName implements Comparable  {
	public NormalizedKey(String s) throws AvatolCVException {
		super(s);
	}
	public int hashCode(){
		return this.normalizedValue.hashCode();
	}
	public boolean equals(Object obj){
		if (!(obj instanceof NormalizedKey)){
			return false;
		}
		NormalizedKey other = (NormalizedKey) obj;
		return this.normalizedValue.equals(other.getNormalizedValue());
	}
	@Override
	public int compareTo(Object obj) {
		NormalizedKey other = (NormalizedKey) obj;
		return this.normalizedValue.compareTo(other.getNormalizedValue());
	}
	
}
