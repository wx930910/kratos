package test;

import java.util.TreeMap;

public class testTreeMap {

	public static void main(String[] args) {
		TreeMap<Integer, String> tm = new TreeMap<Integer, String>();
		String value = "";
		tm.put(1, value);
		tm.put(3, value);
		tm.put(2, value);
		System.out.println(tm.keySet());
	}

}
