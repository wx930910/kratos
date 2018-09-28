package spreadsheet;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.dsmUtils;

import com.opencsv.CSVWriter;

import edu.drexel.cs.rise.minos.MinosException;
import edu.drexel.cs.rise.util.WeightedDigraph;

public class auto_gen {

	public static void main(String[] args) {

		String projectName = "pdfbox";
		String dir = "C:/Users/Twilight/program/kratos/";
		String dsmDir = dir + projectName + "/" + projectName + "_issue_dsm"
				+ "/dsm";
		String changeDsmDir = dir + projectName + "/" + projectName
				+ "_change_dsm";
		auto_gen gen = new auto_gen();
		gen.process(dsmDir, changeDsmDir, projectName);

	}

	public void process(String dsmDir, String changeDsmDir, String projectName) {

		HashMap<String, String> commitChangeTypeMap = new HashMap<String, String>();
		new File(changeDsmDir).mkdirs();
		String commitChangeTypeSavePath = changeDsmDir + "/" + projectName
				+ "_commitChangeType.csv";
		List<String> issues = findFoldersInDirectory(dsmDir);
		// System.out.println(dsmDir);
		for (String issue : issues) {
			String issueDir = dsmDir + "/" + issue;
			String issueSaveDir = changeDsmDir + "/" + issue;
			new File(issueSaveDir).mkdirs();
			// System.out.println(issues);
			List<String> commits = findFoldersInDirectory(issueDir);
			for (String commit : commits) {

				String beforePath = issueDir + "/" + commit + "/" + issue + "-"
						+ commit + "-before.dsm";
				String afterPath = issueDir + "/" + commit + "/" + issue + "-"
						+ commit + "-after.dsm";
				String savePath = issueSaveDir + "/" + commit + ".xlsx";
				// System.out.println(afterPath);
				genExcel gen = new genExcel();
				try {
					WeightedDigraph<String> before;
					before = dsmUtils.loadDSM(beforePath);
					WeightedDigraph<String> after = dsmUtils.loadDSM(afterPath);
					WeightedDigraph<String> union = dsmUtils.getUnionDigraph(
							beforePath, afterPath);

					gen.writeExcel(union, before, after, savePath);
					commitChangeTypeMap.put(commit, gen.getChangeType());

				} catch (MinosException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(
					commitChangeTypeSavePath));
			String[] title = { "Commit", "Type" };
			writer.writeNext(title);
			for (String commit : commitChangeTypeMap.keySet()) {
				String type = commitChangeTypeMap.get(commit);
				String[] line = { commit, type };
				writer.writeNext(line);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static List<String> findFoldersInDirectory(String directoryPath) {
		File directory = new File(directoryPath);

		FileFilter directoryFileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		File[] directoryListAsFile = directory.listFiles(directoryFileFilter);
		List<String> foldersInDirectory = new ArrayList<String>(
				directoryListAsFile.length);
		for (File directoryAsFile : directoryListAsFile) {
			foldersInDirectory.add(directoryAsFile.getName());
		}

		return foldersInDirectory;
	}

}
