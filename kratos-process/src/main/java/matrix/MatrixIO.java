package matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import matrix.Matrix.types;

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

public class MatrixIO {

	String separateChar = "--------------------";
	private Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

	public void writeXlsx(Matrix my_matrix, String output) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		createCellStyle(workbook);
		XSSFSheet spreadsheet = workbook.createSheet("DSM");
		writeExcelIndex(my_matrix, spreadsheet);
		int rowNumber = 1;
		for (String matrix_row : my_matrix.getElements().values()) {
			int cellNumber = 1;
			types t = my_matrix.getElementType(matrix_row);
			Row row = spreadsheet.getRow(rowNumber);
			if (t == types.Add && matrix_row.contains("changes.")) {
				Cell title = row.createCell(cellNumber);
				title.setCellValue(matrix_row);
				title.setCellStyle(styles.get("Green"));
			} else if (t == types.Remove && matrix_row.contains("changes.")) {
				Cell title = row.createCell(cellNumber);
				title.setCellStyle(styles.get("Red"));
				title.setCellValue(matrix_row);
			} else if (matrix_row.contains("references.")) {
				Cell title = row.createCell(cellNumber);
				title.setCellStyle(styles.get("Cyan"));
				title.setCellValue(matrix_row);
			} else {
				Cell title = row.createCell(cellNumber);
				title.setCellValue(matrix_row);
			}
			rowNumber++;
			for (String matrix_col : my_matrix.getElements().values()) {
				cellNumber++;
				if (matrix_row.equals(matrix_col)) {
					Row r = spreadsheet.getRow(rowNumber - 1);
					Cell c = r.createCell(cellNumber);
					c.setCellValue("(" + (rowNumber - 1) + ")");
				} else {
					dependency dp = my_matrix.getCell(matrix_row, matrix_col);
					if (dp != null) {
						LinkedHashSet<String> add = dp.getAdded();
						LinkedHashSet<String> remove = dp.getRemoved();
						LinkedHashSet<String> remain = dp.getRemained();
						XSSFCell cell = (XSSFCell) row.createCell(cellNumber);
						setFontColor(add, remove, remain, cell, workbook);
					}
				}
			}
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(new File(output));
			workbook.write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setFontColor(LinkedHashSet<String> add,
			LinkedHashSet<String> remove, LinkedHashSet<String> remain,
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

		if (remove.size() != 0) {
			int index = 1;
			for (String dp : remove) {
				if (remain.size() == 0 && add.size() == 0
						&& index++ == remove.size()) {
					richString.append(dp, removedFont);
				} else {
					richString.append(dp + ", ", removedFont);
				}
			}
		}
		if (remain.size() != 0) {
			int index = 1;
			for (String dp : remain) {
				if (add.size() == 0 && index++ == add.size()) {
					richString.append(dp, retainedFont);
				} else {
					richString.append(dp + ", ", retainedFont);
				}
			}
		}
		if (add.size() != 0) {
			int index = 1;
			for (String dp : add) {
				if (index++ != add.size()) {
					richString.append(dp + ", ", addedFont);
				} else {
					richString.append(dp, addedFont);
				}
			}
		}
		cell.setCellValue(richString);

	}

	private void writeExcelIndex(Matrix union, XSSFSheet spreadsheet) {
		int rowNumber = 0;
		int colNumber = 2;
		Row row = spreadsheet.createRow(rowNumber);
		for (@SuppressWarnings("unused")
		String node : union.getElements().values()) {
			Cell c = row.createCell(colNumber);
			c.setCellValue(colNumber - 1);
			colNumber++;
		}
		rowNumber++;

		for (@SuppressWarnings("unused")
		String node : union.getElements().values()) {
			Row rowfornow = spreadsheet.createRow(rowNumber);
			Cell c = rowfornow.createCell(0);
			c.setCellValue(rowNumber);
			rowNumber++;
			// System.out.println(c.getNumericCellValue());
		}
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

		CellStyle Cyan = wb.createCellStyle();
		Cyan.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		Cyan.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("Cyan", Cyan);

	}

	// public void write(Matrix<T> my_matrix, String output) {
	//
	// try {
	//
	// File oFile = new File(output);
	// FileWriter fw = new FileWriter(oFile);
	//
	// int size = my_matrix.size();
	//
	// fw.write(size + "\n");
	//
	// for (int row = 1; row <= size; row++) {
	// for (int col = 1; col <= size; col++) {
	// fw.write(my_matrix.getCellValString(row, col) + " ");
	// }
	// fw.write("\n");
	// }
	//
	// for (int row = 0; row < size; row++) {
	// fw.write(my_matrix.getElementName(row + 1) + "\n");
	// }
	//
	// fw.flush();
	// fw.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }

}
