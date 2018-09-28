package spreadsheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.Sets;
import utils.dsmUtils;
import edu.drexel.cs.rise.minos.MinosException;
import edu.drexel.cs.rise.minos.cluster.FileParser;
import edu.drexel.cs.rise.tellus.cluster.Clustering;
import edu.drexel.cs.rise.util.WeightedDigraph;

public class genExcel {

	private Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
	private HashSet<String> changeType = new HashSet<String>();
	String type1 = "Type 1: add/delete files";
	String type2 = "Type 2: change file dependency";
	String type3 = "Type 3: change architecturally connected files";
	String type4 = "Type 4: change architecturally independent files";

	public static void main(String[] args) throws IOException {

		String dir = "C:/Users/Twilight/privateproject/kratos/data/pdfbox/changeDsms/";
		String saveDir = "C:/Users/Twilight/privateproject/kratos/data/pdfbox/diffDsms/";
		String commitID = "000e4b3ecd009b82b9b32f0b449a3fa9c565d728";
		String beforePath = dir + commitID + "/" + commitID + "-before.dsm";
		String afterPath = dir + commitID + "/" + commitID + "-after.dsm";
		String xlsxPath = saveDir + commitID + "/" + commitID + ".xlsx";
		new File(saveDir + commitID).mkdirs();

		genExcel test = new genExcel();

		try {
			WeightedDigraph<String> before;
			before = dsmUtils.loadDSM(beforePath);
			WeightedDigraph<String> after = dsmUtils.loadDSM(afterPath);
			WeightedDigraph<String> union = dsmUtils.getUnionDigraph(
					beforePath, afterPath);

			// test.writeExcel(union, before, after, clsxPath, path);
			test.writeExcel(union, before, after, xlsxPath);
			System.out.println(test.getChangeType());
		} catch (MinosException e) {
			e.printStackTrace();
		}

	}

	private void writeExcelIndex(WeightedDigraph<String> union,
			XSSFSheet spreadsheet) {
		int rowNumber = 0;
		int colNumber = 2;
		Row row = spreadsheet.createRow(rowNumber);
		for (@SuppressWarnings("unused")
		String node : union.vertices()) {
			Cell c = row.createCell(colNumber);
			c.setCellValue(colNumber - 1);
			colNumber++;
		}
		rowNumber++;

		for (@SuppressWarnings("unused")
		String node : union.vertices()) {
			Row rowfornow = spreadsheet.createRow(rowNumber);
			Cell c = rowfornow.createCell(0);
			c.setCellValue(rowNumber);
			rowNumber++;
			// System.out.println(c.getNumericCellValue());
		}
	}

	public void writeExcel(WeightedDigraph<String> union,
			WeightedDigraph<String> before, WeightedDigraph<String> after,
			String clsxPath, String path) throws IOException {

		LinkedHashSet<String> sorted_nodes = new LinkedHashSet<String>();

		try {
			Clustering root = FileParser.load(new File(clsxPath));
			for (String node : root.items()) {
				// if (node.equals("lang.java.tools.src.main.java.org.apache.avro.tool.JsonToBinaryFragmentTool")) {
				// String newNode = node + ".java";
				// sorted_nodes.add(newNode);
				// } else {
				// String newNode = node.replace("lang.", "lang.java.");
				// newNode = newNode.replace("main.", "main.java.");
				// newNode += ".java";
				// sorted_nodes.add(newNode);
				// }
				String newNode = node.replace("lang.", "lang.java.");
				newNode = newNode.replace("main.", "main.java.");
				newNode += ".java";
				sorted_nodes.add(newNode);
				// sorted_nodes.add(node);

			}
		} catch (MinosException e) {
			e.printStackTrace();
		}

		XSSFWorkbook workbook = new XSSFWorkbook();
		createCellStyle(workbook);
		XSSFSheet spreadsheet = workbook.createSheet("DSM");

		writeExcelIndex(union, spreadsheet);
		int rowNumber = 1;
		for (String node : sorted_nodes) {
			System.out.println(rowNumber + ":" + sorted_nodes.size());
			Row row = spreadsheet.getRow(rowNumber);
			int cellNumber = 1;
			if (!before.containsVertex(node)) {
				Cell title = row.createCell(cellNumber);
				title.setCellValue(node);
				title.setCellStyle(styles.get("Green"));
				// System.out.println(node);
			} else if (!after.containsVertex(node)) {
				Cell title = row.createCell(cellNumber);
				title.setCellStyle(styles.get("Red"));
				title.setCellValue(node);
				// System.out.println(node);
			} else {
				Cell title = row.createCell(cellNumber);
				title.setCellValue(node);
			}
			rowNumber++;
			for (String vertex : sorted_nodes) {
				cellNumber++;
				if (vertex.equals(node)) {
					Row r = spreadsheet.getRow(rowNumber - 1);
					Cell c = r.createCell(cellNumber);
					c.setCellValue("(" + (rowNumber - 1) + ")");
				} else {
					LinkedHashSet<String> b = new LinkedHashSet<String>();
					LinkedHashSet<String> a = new LinkedHashSet<String>();
					LinkedHashSet<String> u = new LinkedHashSet<String>();

					if (before.getEdge(vertex, node) != null) {
						b = dsmUtils.typesToSet(before.getEdge(vertex, node)
								.weight());
					}
					if (after.getEdge(vertex, node) != null) {
						a = dsmUtils.typesToSet(after.getEdge(vertex, node)
								.weight());
					}
					if (union.getEdge(vertex, node) != null) {
						u = dsmUtils.typesToSet(union.getEdge(vertex, node)
								.weight());
					}

					Set<String> removed = Sets.difference(u, a);
					Set<String> added = Sets.difference(u, b);
					Set<String> retained = Sets.intersection(a, b);

					XSSFCell cell = (XSSFCell) row.createCell(cellNumber);
					setFontColor(removed.toString(), retained.toString(),
							added.toString(), cell, workbook);
				}
			}

			// System.out.println("Cells added.");
		}

		FileOutputStream out = new FileOutputStream(new File(path));
		workbook.write(out);
		out.close();
		System.out.println(path + " written successfully" + ", " + rowNumber
				+ " files.");

	}

	public void writeExcel(WeightedDigraph<String> union,
			WeightedDigraph<String> before, WeightedDigraph<String> after,
			String path) throws IOException {

		// System.out.println("Writing: " + path);
		if (union.edges().size() != 0) {
			changeType.add(type3);
		} else {
			changeType.add(type4);
		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		createCellStyle(workbook);
		XSSFSheet spreadsheet = workbook.createSheet("DSM");

		LinkedHashSet<String> sorted_nodes = new LinkedHashSet<String>();
		sorted_nodes
				.addAll(Sets.difference(union.vertices(), after.vertices()));
		sorted_nodes.addAll(Sets.intersection(before.vertices(),
				after.vertices()));
		sorted_nodes
				.addAll(Sets.difference(union.vertices(), before.vertices()));

		writeExcelIndex(union, spreadsheet);
		int rowNumber = 1;
		long t1 = System.currentTimeMillis();
		System.out.print(System.currentTimeMillis());
		for (String node : sorted_nodes) {
			System.out.println(rowNumber + ":" + sorted_nodes.size());
			Row row = spreadsheet.getRow(rowNumber);
			int cellNumber = 1;
			if (!before.containsVertex(node)) {
				Cell title = row.createCell(cellNumber);
				title.setCellValue(node);
				title.setCellStyle(styles.get("Green"));
				changeType.add(type1);
			} else if (!after.containsVertex(node)) {
				Cell title = row.createCell(cellNumber);
				title.setCellStyle(styles.get("Red"));
				title.setCellValue(node);
				changeType.add(type1);
			} else {
				Cell title = row.createCell(cellNumber);
				title.setCellValue(node);
			}
			rowNumber++;
			for (String vertex : sorted_nodes) {
				cellNumber++;
				if (vertex.equals(node)) {
					Row r = spreadsheet.getRow(rowNumber - 1);
					Cell c = r.createCell(cellNumber);
					c.setCellValue("(" + (rowNumber - 1) + ")");
				} else {
					LinkedHashSet<String> b = new LinkedHashSet<String>();
					LinkedHashSet<String> a = new LinkedHashSet<String>();
					LinkedHashSet<String> u = new LinkedHashSet<String>();

					if (before.getEdge(vertex, node) != null) {
						b = dsmUtils.typesToSet(before.getEdge(vertex, node)
								.weight());
					}
					if (after.getEdge(vertex, node) != null) {
						a = dsmUtils.typesToSet(after.getEdge(vertex, node)
								.weight());
					}
					if (union.getEdge(vertex, node) != null) {
						u = dsmUtils.typesToSet(union.getEdge(vertex, node)
								.weight());
					}

					Set<String> removed = Sets.difference(u, a);
					Set<String> added = Sets.difference(u, b);
					Set<String> retained = Sets.intersection(a, b);
					// System.out.println(removed);
					if (removed.size() != 0 || added.size() != 0) {
						changeType.add(type2);
					}

					XSSFCell cell = (XSSFCell) row.createCell(cellNumber);
					setFontColor(removed.toString(), retained.toString(),
							added.toString(), cell, workbook);
				}
			}

			// System.out.println("Cells added.");
		}

		FileOutputStream out = new FileOutputStream(new File(path));
		workbook.write(out);
		out.close();
		System.out.println(path + " written successfully" + ", " + rowNumber
				+ " files.");

	}

	private void createCellStyle(XSSFWorkbook wb) {

		CellStyle Red = wb.createCellStyle();
		Red.setFillForegroundColor(IndexedColors.RED.getIndex());
		Red.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("Red", Red);

		CellStyle Green = wb.createCellStyle();
		Green.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		Green.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("Green", Green);

	}

	private void setFontColor(String removed, String retained, String added,
			XSSFCell cell, XSSFWorkbook workbook) {
		// Set up fonts
		XSSFFont removedFont = workbook.createFont();
		removedFont.setColor(HSSFColor.RED.index);

		XSSFFont retainedFont = workbook.createFont();
		retainedFont.setColor(HSSFColor.BLACK.index);

		XSSFFont addedFont = workbook.createFont();
		addedFont.setColor(HSSFColor.GREEN.index);

		// XSSFRichTextString richString = new XSSFRichTextString(retained);
		XSSFRichTextString richString = new XSSFRichTextString();

		if (!removed.equals("[]")) {
			richString.append(removed.replace("[", "").replace("]", "") + ", ",
					removedFont);
		}
		if (!retained.equals("[]")) {
			richString.append(
					retained.replace("[", "").replace("]", "") + ", ",
					retainedFont);
		}
		if (!added.equals("[]")) {
			richString.append(added.replace("[", "").replace("]", ""),
					addedFont);
		}
		cell.setCellValue(richString);

	}

	public String getChangeType() {
		if (changeType.contains(type1)) {
			return type1;
		} else if (changeType.contains(type2)) {
			return type2;
		} else if (changeType.contains(type3)) {
			return type3;
		}
		return type4;
	}
}
