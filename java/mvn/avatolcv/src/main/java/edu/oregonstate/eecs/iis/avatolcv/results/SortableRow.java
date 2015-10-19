package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SortableRow implements Comparable {
	private List<String> values = null;
	private int index = -1;
    private static List<Integer> sortColumns = new ArrayList<Integer>();
    // widgetHash will store the JavaFX (or whatever) widgets for each item
    private Hashtable<String, Object> widgetHash = new Hashtable<String, Object>();
    public static void addSortColumn(int columnIndex){
    	if (sortColumns.size() == 0){
    		sortColumns.add(new Integer(columnIndex));
    		return;
    	}
    	if (columnIndex > sortColumns.size() - 1){
    		return;
    	}
    	for (Integer integer : sortColumns){
    		if (integer.intValue() == columnIndex){
    			return;
    		}
    	}
    	sortColumns.add(new Integer(columnIndex));
    }
    public SortableRow(List<String> values, int index){
    	this.values = values;
    	this.index = index;
    }
    public int getIndex(){
        return this.index;
    }
    public void setWidget(String colName, Object widget){
        widgetHash.put(colName, widget);
    }
    public Object getWidget(String colName){
        return widgetHash.get(colName);
    }
    
	@Override
	public int compareTo(Object o) {
		SortableRow otherRow = (SortableRow)o;
		return compareColumn(0, 0, otherRow);
	}
	public boolean hasDoubleValueLessThanThisAtIndex(String s, int index){
	    String myValue = getValue(index);
	    //System.out.println("myValue at index " + index + " is " + myValue);
	    Double myDouble = new Double(myValue);
	    Double otherDouble = new Double(s);
	    if (myDouble.doubleValue() < otherDouble.doubleValue()){
	        return true;
	    }
	    return false;
	}
	public int compareColumn(int colIndex, int columnsChecked, SortableRow otherRow){
		if (colIndex > SortableRow.sortColumns.size() - 1 ){
			return 0;
		}
		int trueIndex = SortableRow.sortColumns.get(colIndex).intValue();
		String thisVal = this.getValue(trueIndex);
		String otherVal = otherRow.getValue(trueIndex);
		// keep looking across the columns if up to now, column values have been equal.
		int thisCompareResult = thisVal.compareTo(otherVal);
		if (thisCompareResult > 0){
			return 1;
		}
		else if (thisCompareResult < 0){
			return -1;
		}
		else {
		    return compareColumn(colIndex + 1, columnsChecked + 1, otherRow);
		}
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		int i;
		for (i = 0; i < values.size() - 1; i++){
			sb.append(values.get(i) + ",");
		}
		sb.append(values.get(i));
		return "" + sb;
	}
	public String getValue(int index){
		if (index > this.values.size() - 1){
			return "?";
		}
		return values.get(index);
	}
	
}
