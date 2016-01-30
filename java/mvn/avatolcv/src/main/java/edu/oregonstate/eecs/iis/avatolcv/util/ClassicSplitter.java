package edu.oregonstate.eecs.iis.avatolcv.util;

import java.util.ArrayList;
import java.util.List;

public class ClassicSplitter {
	public static String[] splitt(String s, char c){
		int len = s.length();
		if (len == 0){
			String[] result = new String[1];
			result[0] = "";
			return result;
		}
		if (s.indexOf(c) == -1){
			String[] result = new String[1];
			result[0] = s;
			return result;
		}
		List<Integer> matchIndices = new ArrayList<Integer>();
		//int curMatchIndex = 0;
		//ArrayList<String> strings = new ArrayList<String>();
		//String curString = "";
		for (int i = 0; i < len; i++){
			char curChar = s.charAt(i);
			if (curChar == c){
				matchIndices.add(new Integer(i));
			}
		}
		List<Integer> matchIndicesLeftShifted = new ArrayList<Integer>();
		// make another set of indices based on first, but left shifted
		for (int i = 1; i < matchIndices.size(); i++){
			matchIndicesLeftShifted.add(matchIndices.get(i));
		}
		// "1,"
		//String s45 = ",,1,22,333,,";
		// 0,1,3,6,10,11
		// 1,3,6,10,11
		List<String> result = new ArrayList<String>();
		// if delim starts the string, make an empty string representing "to the left" of that delim
		//int resultIndex = 0;
		if (s.charAt(0) == c){
			result.add("");
		}
		else {
			result.add(s.substring(0,matchIndices.get(0)));
		}
		int i = 0;
		for (; i < matchIndicesLeftShifted.size(); i++){
			result.add(s.substring(matchIndices.get(i).intValue() + 1, matchIndicesLeftShifted.get(i).intValue()));
		}
		result.add(s.substring(matchIndices.get(i).intValue() + 1, s.length()));
		/*String s0 = s.substring(1,1);
		String s1 = s.substring(2,3);
		String s2 = s.substring(4,6);
		String s3 = s.substring(7,10);
		String s4 = s.substring(11,11);
		int foo = 4;
		s.substring
		String[] result = new String[10];
*/
		String[] resultFinal = new String[result.size()];
		resultFinal = result.toArray(resultFinal);
		return resultFinal;
	}
}
