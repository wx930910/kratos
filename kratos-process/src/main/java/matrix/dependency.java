package matrix;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import utils.Sets;

public class dependency {

	private LinkedHashSet<String> before_dependencies;
	private LinkedHashSet<String> after_dependencies;
	private LinkedHashSet<String> combined_dependencies;
	private Vector<operation> operationList;
	private int row;
	private int col;

	private enum operation {
		Remain, Remove, Add
	}

	public dependency(int r, int c, LinkedHashSet<String> before_dependencies,
			LinkedHashSet<String> after_dependencies) {

		this.row = r;
		this.col = c;
		this.before_dependencies = before_dependencies;
		this.after_dependencies = after_dependencies;
		this.combined_dependencies = new LinkedHashSet<String>();
		this.operationList = new Vector<operation>();
		Set<String> union = Sets.union(before_dependencies, after_dependencies);
		for (String dp : union) {
			if (before_dependencies.contains(dp)
					&& after_dependencies.contains(dp)) {
				combined_dependencies.add(dp);
				operationList.add(operation.Remain);
				// System.out.println("Remain Dependency: " + dp);
			} else if (before_dependencies.contains(dp)
					&& !after_dependencies.contains(dp)) {
				combined_dependencies.add(dp);
				operationList.add(operation.Remove);
				// System.out.println("Remove Dependency: " + dp);
			} else if (!before_dependencies.contains(dp)
					&& after_dependencies.contains(dp)) {
				combined_dependencies.add(dp);
				operationList.add(operation.Add);
				// System.out.println("Add Dependency: " + dp);
			}
		}
		// System.out.println(combined_dependencies);
		// System.out.println(operationList);
	}

	public void setVal(LinkedHashSet<String> before_dependencies,
			LinkedHashSet<String> after_dependencies) {

		this.before_dependencies = before_dependencies;
		this.after_dependencies = after_dependencies;
		this.combined_dependencies = new LinkedHashSet<String>();
		this.operationList = new Vector<operation>();
		Set<String> union = Sets.union(before_dependencies, after_dependencies);
		for (String dp : union) {
			if (before_dependencies.contains(dp)
					&& after_dependencies.contains(dp)) {
				combined_dependencies.add(dp);
				operationList.add(operation.Remain);
				// System.out.println("Remain Dependency: " + dp);
			} else if (before_dependencies.contains(dp)
					&& !after_dependencies.contains(dp)) {
				combined_dependencies.add(dp);
				operationList.add(operation.Remove);
				// System.out.println("Remove Dependency: " + dp);
			} else if (!before_dependencies.contains(dp)
					&& after_dependencies.contains(dp)) {
				combined_dependencies.add(dp);
				operationList.add(operation.Add);
				// System.out.println("Add Dependency: " + dp);
			}
		}

	}

	public LinkedHashSet<String> getAdded() {

		LinkedHashSet<String> addedDp = new LinkedHashSet<String>();
		int index = 0;
		for (operation op : operationList) {
			if (op.equals(operation.Add)) {
				addedDp.add((String) combined_dependencies.toArray()[index]);
			}
			index++;
		}
		return addedDp;

	}

	public LinkedHashSet<String> getRemoved() {

		LinkedHashSet<String> removedDp = new LinkedHashSet<String>();
		int index = 0;
		for (operation op : operationList) {
			if (op.equals(operation.Remove)) {
				removedDp.add((String) combined_dependencies.toArray()[index]);
			}
			index++;
		}
		return removedDp;

	}

	public LinkedHashSet<String> getRemained() {

		LinkedHashSet<String> remainedDp = new LinkedHashSet<String>();
		int index = 0;
		for (operation op : operationList) {
			if (op.equals(operation.Remain)) {
				remainedDp.add((String) combined_dependencies.toArray()[index]);
			}
			index++;
		}
		return remainedDp;

	}

	public String getVal() {

		return combined_dependencies.toString();

	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
