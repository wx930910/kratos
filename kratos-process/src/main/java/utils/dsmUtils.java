package utils;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import matrix.Matrix;
import edu.drexel.cs.rise.minos.MinosException;
import edu.drexel.cs.rise.minos.dsm.BiWeightedFileParser;
import edu.drexel.cs.rise.util.Graph.Edge;
import edu.drexel.cs.rise.util.WeightedDigraph;

public class dsmUtils {

	public static WeightedDigraph<String> loadDSM(String path)
			throws MinosException {
		WeightedDigraph<String> bigraph = new WeightedDigraph<String>();
		WeightedDigraph<String> dsm = BiWeightedFileParser.load(new File(path));

		// add vertices to bigraph
		for (String node : dsm.vertices()) {
			if (!node.contains(".test.") && !node.contains(".tests.")) {
				bigraph.addVertex(node);
			}
		}
		// add edges to bigraph
		String[] dpTypes = BiWeightedFileParser.dpTypes;
		for (Edge<String> edge : dsm.edges()) {
			LinkedHashSet<String> types = new LinkedHashSet<String>();
			char[] flags = edge.weight().toCharArray();
			for (int i = 0; i < flags.length; i++) {
				if (flags[i] == '1') {
					types.add(dpTypes[i]);
					// System.out.println(dpTypes[i]);
				}
			}
			if (bigraph.containsVertex(edge.first())
					&& bigraph.containsVertex(edge.second())) {
				bigraph.addEdge(edge.first(), edge.second(), types.toString());
			}

		}
		// System.out.println(dsm.size());
		return bigraph;
	}

	public static Matrix getUnion(String beforePath, String afterPath)
			throws MinosException {
		WeightedDigraph<String> before = loadDSM(beforePath);
		WeightedDigraph<String> after = loadDSM(afterPath);
		Matrix m = new Matrix();
		Set<String> nodes = Sets.union(before.vertices(), after.vertices());

		for (String vertex : nodes) {
			if (before.vertices().contains(vertex)
					&& after.vertices().contains(vertex)) {
				m.setElementType(vertex, "Remain");
				m.addElement(vertex);
			} else if (!before.vertices().contains(vertex)
					&& after.vertices().contains(vertex)) {
				m.setElementType(vertex, "Add");
				m.addElement(vertex);
			} else {
				m.setElementType(vertex, "Remove");
				m.addElement(vertex);
			}
		}
		int added = 0, removed = 0, modified = 0;
		for (String row : nodes) {
			for (String col : nodes) {
				// System.out.println(col);
				if (before.containsEdge(col, row)
						&& !after.containsEdge(col, row)) {
					// It's a removed dependency
					LinkedHashSet<String> before_dependencies = typesToSet(before
							.getEdge(col, row).weight());
					LinkedHashSet<String> after_dependencies = new LinkedHashSet<String>();
					m.addCell(row, col, before_dependencies, after_dependencies);
					removed++;
				} else if (!before.containsEdge(col, row)
						&& after.containsEdge(col, row)) {
					// It's an added dependency
					LinkedHashSet<String> before_dependencies = new LinkedHashSet<String>();
					LinkedHashSet<String> after_dependencies = typesToSet(after
							.getEdge(col, row).weight());
					// System.out.println(after.getEdge(col, row).first() + " -> "
					// + after.getEdge(col, row).second());
					// System.out.println(after.getEdge(col, row).weight());
					m.addCell(row, col, before_dependencies, after_dependencies);
					added++;
				} else if (before.containsEdge(col, row)
						&& after.containsEdge(col, row)) {
					// It's an remained edge
					LinkedHashSet<String> before_dependencies = typesToSet(before
							.getEdge(col, row).weight());
					LinkedHashSet<String> after_dependencies = typesToSet(after
							.getEdge(col, row).weight());
					// System.out.println(after_dependencies);
					m.addCell(row, col, before_dependencies, after_dependencies);
					modified++;
				}
			}
		}
		System.out.println("Removed: " + removed + "\tAdded: " + added
				+ "\tModified: " + modified);
		return m;

	}

	public static WeightedDigraph<String> getUnionDigraph(String beforePath,
			String afterPath) throws MinosException {
		WeightedDigraph<String> before = loadDSM(beforePath);
		WeightedDigraph<String> after = loadDSM(afterPath);
		// System.out.println(before.size());
		WeightedDigraph<String> res = new WeightedDigraph<String>();

		// add vertices to the union graph
		Set<String> nodes = Sets.union(before.vertices(), after.vertices());
		for (String vertex : nodes) {
			res.addVertex(vertex);
		}

		// add edges to the union graph
		int added = 0, removed = 0, modified = 0;
		for (String row : nodes) {
			for (String col : nodes) {
				// System.out.println(col);
				if (before.containsEdge(row, col)
						&& !after.containsEdge(row, col)) {
					// It's a removed dependency
					res.addEdge(row, col, before.getEdge(row, col).weight());
					removed++;
				} else if (!before.containsEdge(row, col)
						&& after.containsEdge(row, col)) {
					// It's an added dependency
					res.addEdge(row, col, after.getEdge(row, col).weight());
					added++;
				} else if (before.containsEdge(row, col)
						&& after.containsEdge(row, col)) {
					// It's an modified edge
					LinkedHashSet<String> before_dependencies = typesToSet(before
							.getEdge(row, col).weight());
					LinkedHashSet<String> after_dependencies = typesToSet(after
							.getEdge(row, col).weight());
					System.out.println(after_dependencies);
					Set<String> weight = Sets.union(before_dependencies,
							after_dependencies);
					res.addEdge(row, col, weight.toString());
					modified++;
				}
			}
		}
		// System.out.println(nodes.size() + "\t" + res.vertices().size());
		System.out.println("Removed: " + removed + "\tAdded: " + added
				+ "\tModified: " + modified);
		return res;

	}

	public static LinkedHashSet<String> typesToSet(String types) {
		LinkedHashSet<String> res = new LinkedHashSet<String>();

		types = types.substring(1, types.length() - 1);
		String[] temp = types.split(", ");
		for (String type : temp)
			res.add(type);

		return res;
	}

}
