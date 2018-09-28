package matrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Matrix {

	public enum types {
		Remain, Remove, Add
	}

	private String changeWord = "changes.";
	private LinkedHashMap<Integer, String> elements;
	private HashMap<String, Integer> elementIDs;
	private HashMap<String, types> elementTypes;

	private Map<Integer, Map<Integer, dependency>> dependencies_index_by_row;
	private Map<Integer, Map<Integer, dependency>> dependencies_index_by_col;

	public Matrix() {

		elements = new LinkedHashMap<Integer, String>();
		elementIDs = new HashMap<String, Integer>();
		elementTypes = new HashMap<String, types>();

		dependencies_index_by_row = new HashMap<Integer, Map<Integer, dependency>>();
		dependencies_index_by_col = new HashMap<Integer, Map<Integer, dependency>>();
	}

	public types getElementType(String eName) {
		return elementTypes.get(eName);
	}

	public Set<dependency> getAllCells() {

		Set<dependency> rt = new HashSet<dependency>();

		for (Map<Integer, dependency> cells_on_row : dependencies_index_by_row
				.values()) {

			rt.addAll(cells_on_row.values());
		}
		return rt;
	}

	public void setElementType(String element, String tp) {

		if (tp.equals("Add")) {
			elementTypes.put(element, types.Add);
		} else if (tp.equals("Remain")) {
			elementTypes.put(element, types.Remain);
		} else if (tp.equals("Remove")) {
			elementTypes.put(element, types.Remove);
		}

	}

	public void removeElement(String eName) {

		String name = eName;
		int e = elementIDs.remove(name);
		elements.remove(e);
		elementTypes.remove(name);
		for (Map<Integer, dependency> rowMap : dependencies_index_by_row
				.values()) {
			rowMap.remove(e);
		}
		dependencies_index_by_row.remove(e);
		for (Map<Integer, dependency> colMap : dependencies_index_by_col
				.values()) {
			colMap.remove(e);
		}
		dependencies_index_by_col.remove(e);

	}

	public void removeElement(int e) {

		String name = elements.remove(e);
		elementIDs.remove(name);
		elementTypes.remove(name);
		for (Map<Integer, dependency> rowMap : dependencies_index_by_row
				.values()) {
			rowMap.remove(e);
		}
		dependencies_index_by_row.remove(e);
		for (Map<Integer, dependency> colMap : dependencies_index_by_col
				.values()) {
			colMap.remove(e);
		}
		dependencies_index_by_col.remove(e);

	}

	public void removeCell(String rS, String cS) {

		int r = elementIDs.get(rS);
		int c = elementIDs.get(cS);

		removeCell(r, c);
	}

	public void removeCell(int r, int c) {

		if (dependencies_index_by_row.containsKey(r)) {

			Map<Integer, dependency> row = dependencies_index_by_row.get(r);
			row.remove(c);

			if (row.size() == 0) {
				dependencies_index_by_row.remove(r);
			}
		}

		if (dependencies_index_by_col.containsKey(c)) {
			Map<Integer, dependency> col = dependencies_index_by_col.get(c);
			col.remove(r);

			if (col.size() == 0) {
				dependencies_index_by_col.remove(c);
			}
		}

	}

	public HashMap<String, Integer> getElementsIDs() {
		return elementIDs;
	}

	public LinkedHashMap<Integer, String> getElements() {

		return elements;
	}

	public Vector<String> getExistColFileNames(String row) {
		Vector<String> existColFileNames = new Vector<String>();
		int rowId = elementIDs.get(row);
		for (int colId = 1; colId <= elements.size(); colId++) {
			if (getCell(rowId, colId) != null)
				existColFileNames.add(elements.get(colId));
		}
		return existColFileNames;
	}

	public Map<Integer, dependency> getRow(int r) {
		return dependencies_index_by_row.get(r);
	}

	public Map<Integer, dependency> getRow(String rS) {

		int r = elementIDs.get(rS);
		return dependencies_index_by_row.get(r);
	}

	public Map<Integer, dependency> getCol(int c) {
		return dependencies_index_by_col.get(c);
	}

	public Map<Integer, dependency> getCol(String cS) {

		int c = elementIDs.get(cS);

		return dependencies_index_by_col.get(c);
	}

	public void addElement(String name) {
		int id = 1;
		if (!(elements.size() == 0))
			id = (int) elements.keySet().toArray()[elements.size() - 1] + 1;
		elements.put(id, name);
		elementIDs.put(name, id);
	}

	public void addElement(int id, String name) {
		elements.put(id, name);
		elementIDs.put(name, id);
	}

	public dependency getCell(int r, int c) {
		dependency rt = null;

		if (dependencies_index_by_row.size() < dependencies_index_by_col.size()) {
			Map<Integer, dependency> row = dependencies_index_by_row.get(r);
			if (row != null)
				rt = row.get(c);
			else
				rt = null;
		} else {
			Map<Integer, dependency> col = dependencies_index_by_col.get(c);
			if (col != null)
				rt = col.get(r);
			else
				rt = null;
		}

		return rt;
	}

	public dependency getCell(String rS, String cS) {

		int r = elementIDs.get(rS);
		int c = elementIDs.get(cS);

		return getCell(r, c);

	}

	public String getDependencyValString(int r, int c) {
		dependency rt = getCell(r, c);

		if (rt == null)
			return "0";
		return rt.getVal().toString();
	}

	public String getCellValString(String first, String second) {

		int fid = this.getElementId(first);
		int sid = this.getElementId(second);

		return getDependencyValString(fid, sid);

	}

	public void addCell(String rName, String cName,
			LinkedHashSet<String> before_dependencies,
			LinkedHashSet<String> after_dependencies) {

		int r = this.getElementId(rName);
		int c = this.getElementId(cName);
		addCell(r, c, before_dependencies, after_dependencies);
	}

	public void addCell(int r, int c,
			LinkedHashSet<String> before_dependencies,
			LinkedHashSet<String> after_dependencies) {

		dependency new_cell = new dependency(r, c, before_dependencies,
				after_dependencies);

		// check if this row exists
		if (dependencies_index_by_row.containsKey(r)) {
			dependencies_index_by_row.get(r).put(c, new_cell);
		} else {
			Map<Integer, dependency> row = new HashMap<Integer, dependency>();
			row.put(c, new_cell);
			dependencies_index_by_row.put(r, row);
		}

		// check if this col exists
		if (dependencies_index_by_col.containsKey(c)) {
			dependencies_index_by_col.get(c).put(r, new_cell);
		} else {
			Map<Integer, dependency> col = new HashMap<Integer, dependency>();
			col.put(r, new_cell);
			dependencies_index_by_col.put(c, col);
		}
	}

	public void updateCell(int r, int c,
			LinkedHashSet<String> before_dependencies,
			LinkedHashSet<String> after_dependencie) {
		dependency cur_cell = this.getCell(r, c);

		// This cell doesn't exist
		if (cur_cell == null) {
			addCell(r, c, before_dependencies, after_dependencie);
		}
		// This cell already exist
		else {
			cur_cell.setVal(before_dependencies, after_dependencie);
		}
	}

	public void updateCell(String rS, String cS,
			LinkedHashSet<String> before_dependencies,
			LinkedHashSet<String> after_dependencie) {

		int r = elementIDs.get(rS);
		int c = elementIDs.get(cS);
		dependency cur_cell = this.getCell(r, c);

		// This cell doesn't exist
		if (cur_cell == null) {
			addCell(r, c, before_dependencies, after_dependencie);
		}
		// This cell already exist
		else {
			cur_cell.setVal(before_dependencies, after_dependencie);
		}
	}

	public int size() {

		return elements.size();
	}

	public String getElementName(int id) {

		return elements.get(id);
	}

	public int getElementId(String name) {

		return elementIDs.get(name);
	}

	public boolean hasRow(int id) {
		if (this.getRow(id) != null)
			return true;
		return false;
	}

	public boolean hasCol(int id) {
		if (this.getCol(id) != null)
			return true;
		return false;
	}

	public double pc() {
		int size = this.size();
		int sum = 0;
		for (int row : dependencies_index_by_row.keySet()) {
			sum = sum + dependencies_index_by_row.get(row).size();
		}
		return (double) sum / (double) (size * size);
	}

	public boolean ifChangedDependency(String eName) {

		Map<Integer, dependency> row = getRow(eName);
		Map<Integer, dependency> col = getCol(eName);
		if (ifChangedDependency(row) || ifChangedDependency(col)) {
			return true;
		}
		return false;

	}

	private boolean ifChangedDependency(Map<Integer, dependency> ds) {

		if (ds != null) {
			for (dependency dp : ds.values()) {
				if (dp.getAdded().size() != 0 || dp.getRemoved().size() != 0) {
					return true;
				}
			}
		}

		return false;

	}

	public void sort() {

		LinkedHashMap<Integer, String> sortedElements = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> addChange = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> removeChange = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> remainChange = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> addReference = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> removeReference = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> remainReference = new LinkedHashMap<Integer, String>();
		for (int i : elements.keySet()) {
			String element = elements.get(i);
			if (element.contains("changes.")
					&& elementTypes.get(element).equals(types.Add)) {
				addChange.put(i, element);
				// System.out.println(element);
			} else if (element.contains("changes.")
					&& elementTypes.get(element).equals(types.Remove)) {
				removeChange.put(i, element);
				// System.out.println(element);
			} else if (element.contains("changes.")
					&& elementTypes.get(element).equals(types.Remain)) {
				remainChange.put(i, element);
			} else if (element.contains("references.")
					&& elementTypes.get(element).equals(types.Add)) {
				addReference.put(i, element);
			} else if (element.contains("references.")
					&& elementTypes.get(element).equals(types.Remove)) {
				removeReference.put(i, element);
			} else if (element.contains("references.")
					&& elementTypes.get(element).equals(types.Remain)) {
				remainReference.put(i, element);
			}
		}
		sortedElements.putAll(addChange);
		sortedElements.putAll(removeChange);
		sortedElements.putAll(remainChange);
		sortedElements.putAll(addReference);
		sortedElements.putAll(removeReference);
		sortedElements.putAll(remainReference);
		elements = sortedElements;

	}

	public void trim() {

		Vector<String> trimList = new Vector<String>();
		for (String eName : elements.values()) {
			if (!ifChangedDependency(eName)) {
				trimList.add(eName);
			}
		}
		// System.out.println("Trim: " + trimList.size());
		for (String eName : trimList) {
			if (!eName.contains(changeWord)) {
				removeElement(eName);
			}
		}

	}
}
