package test;

import java.util.LinkedHashSet;

import matrix.dependency;

public class testDependency {

	public static void main(String[] args) {

		LinkedHashSet<String> before = new LinkedHashSet<String>();
		LinkedHashSet<String> after = new LinkedHashSet<String>();
		int r = 1;
		int c = 1;
		before.add("Call");
		before.add("Use");
		after.add("Call");
		after.add("Extend");
		dependency dp = new dependency(r, c, before, after);

	}

}
