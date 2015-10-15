package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SortableRow implements Comparable {
	private List<String> values = null;
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
    public SortableRow(List<String> values){
    	this.values = values;
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
