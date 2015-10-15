package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class TestResultsRow extends TestCase {
	private static final String NL = System.getProperty("line.separator");
	public void testResultsRowSort(){
		/*
		List<SortableRow> rows = setupRows();
		System.out.println("sorting on 1...");
		SortableRow.addSortColumn(0);
		Collections.sort(rows);
		dumpRows(rows);
		
		System.out.println(NL + "sorting on 1,2...");
		SortableRow.addSortColumn(1);
		Collections.sort(rows);
		dumpRows(rows);

		System.out.println(NL + "sorting on 1,2,3...");
		SortableRow.addSortColumn(2);
		Collections.sort(rows);
		dumpRows(rows);
*/
		
		/*
		List<SortableRow> rows = setupRows();
		System.out.println(NL + NL + "sorting on 3...");
		SortableRow.addSortColumn(2);
		Collections.sort(rows);
		dumpRows(rows);
		
		System.out.println(NL + "sorting on 3,2...");
		SortableRow.addSortColumn(1);
		Collections.sort(rows);
		dumpRows(rows);

		System.out.println(NL + "sorting on 3,2,1...");
		SortableRow.addSortColumn(0);
		Collections.sort(rows);
		dumpRows(rows);
*/
		List<SortableRow> rows = setupRowsTwo();
		System.out.println(NL + NL + "sorting on 2...");
		SortableRow.addSortColumn(1);
		Collections.sort(rows);
		dumpRows(rows);
		
		System.out.println(NL + "sorting on 2,1...");
		SortableRow.addSortColumn(0);
		Collections.sort(rows);
		dumpRows(rows);


	}
	public void dumpRows(List<SortableRow> rows){
		for (SortableRow row : rows){
			System.out.println(row);
		}
	}
	public List<String> getList(String s1, String s2, String s3, String s4, String s5){
		List<String> list = new ArrayList<String>();
		list.add(s1);
		list.add(s2);
		list.add(s3);
		list.add(s4);
		list.add(s5);
		return list;
	}
	public List<String> getList(String s1, String s2, String s3){
		List<String> list = new ArrayList<String>();
		list.add(s1);
		list.add(s2);
		list.add(s3);
		return list;
	}
	public List<String> getList(String s1, String s2){
		List<String> list = new ArrayList<String>();
		list.add(s1);
		list.add(s2);
		return list;
	}
	public List<SortableRow> setupRowsThree(){
		SortableRow rr1 = new SortableRow(getList("a1","b1","c1"));
		SortableRow rr2 = new SortableRow(getList("a1","b1","c2"));
		SortableRow rr3 = new SortableRow(getList("a1","b2","c1"));
		SortableRow rr4 = new SortableRow(getList("a1","b2","c2"));
		SortableRow rr5 = new SortableRow(getList("a2","b1","c1"));
		SortableRow rr6 = new SortableRow(getList("a2","b1","c2"));
		SortableRow rr7 = new SortableRow(getList("a2","b2","c1"));
		SortableRow rr8 =  new SortableRow(getList("a2","b2","c2"));
		
		List<SortableRow> rows = new ArrayList<SortableRow>();
		rows.add(rr8);
		rows.add(rr7);
		rows.add(rr6);
		rows.add(rr5);
		rows.add(rr4);
		rows.add(rr3);
		rows.add(rr2);
		rows.add(rr1);
		return rows;
	}
	public List<SortableRow> setupRowsTwo(){
		SortableRow rr1 = new SortableRow(getList("a1","b1"));
		SortableRow rr2 = new SortableRow(getList("a1","b2"));
		SortableRow rr3 = new SortableRow(getList("a2","b1"));
		SortableRow rr4 = new SortableRow(getList("a2","b2"));
		
		List<SortableRow> rows = new ArrayList<SortableRow>();
		rows.add(rr4);
		rows.add(rr3);
		rows.add(rr2);
		rows.add(rr1);
		return rows;
	}
}
