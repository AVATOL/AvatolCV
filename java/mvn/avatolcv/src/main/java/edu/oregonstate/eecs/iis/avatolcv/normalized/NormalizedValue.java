package edu.oregonstate.eecs.iis.avatolcv.normalized;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class NormalizedValue extends NormalizedTypeIDName {
	public NormalizedValue(String s) throws AvatolCVException {
		super(s);
	}
	public int hashCode(){
		return this.normalizedValue.hashCode();
	}
	public boolean equals(Object obj){
		if (!(obj instanceof NormalizedValue)){
			return false;
		}
		NormalizedValue other = (NormalizedValue) obj;
		return this.normalizedValue.equals(other.getNormalizedValue());
	}
}
