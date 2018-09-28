package utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.csvreader.CsvReader;

public class CSVUtils {

	public static Set<String> getIssueKeys(String path) throws Exception {
		Set<String> keys = new HashSet<String>();

		List<String[]> list = CSVUtils.readCsv(path);

		for (int r = 0; r < list.size(); r++) {
			if (list.get(r)[0] != null && !list.get(r)[0].equals(""))
				keys.add(list.get(r)[0].replaceAll("\\s", ""));
		}

		return keys;
	}

	private static List<String[]> readCsv(String path) throws Exception {
		List<String[]> csvList = new ArrayList<String[]>();
		if (isCsv(path)) {
			CsvReader reader = new CsvReader(path, ',', Charset.forName("GBK"));
			reader.readHeaders();
			while (reader.readRecord()) {
				csvList.add(reader.getValues());
			}
			System.out.println("CSV read successfully!");
			reader.close();
		} else {
			System.out.println("Error: It is not a csv file!");
		}
		return csvList;
	}

	private static boolean isCsv(String fileName) {
		return fileName.matches("^.+\\.(?i)(csv)$");
	}
}
