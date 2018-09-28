package processDiff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import diff.data.diff;
import diff.data.file;
import extract.changes.diffParser;

public class processIssueDiff {

	public static void main(String[] args) {

		String projectName = "groovy";

		String dir = "C:/Users/Twilight/program/kratos/";
		String alldiffsDir = dir + projectName + "/" + projectName
				+ "_issue_diff/";
		String generalDir = dir + projectName + "/" + projectName
				+ "_change_files/";
		System.out.println(projectName);

		// new File(filesSaveDir).mkdirs();

		processIssueDiff p = new processIssueDiff();
		/*
		 * if (diffInput.isFile()) { File diffInput = new File(diffDir); diffParser parser = new diffParser(); diff per_diff; try { per_diff =
		 * parser.parser(diffPath); p.writeChangedFiles(per_diff, filesSaveDir, commitID); } catch (Exception e) { e.printStackTrace(); } } else {
		 * p.writeChangedIssue(diffDir, filesSaveDir); }
		 */
		// p.writeChangedIssue(diffDir, filesSaveDir);
		// new File(alldiffsDir).mkdirs();
		p.writeChangedIssues(alldiffsDir, generalDir);

	}

	public void writeChangedIssues(String alldiffsDir, String generalDir) {

		File diffInput = new File(alldiffsDir);
		File[] list = diffInput.listFiles();
		for (File path : list) {
			String issueID = path.getName();
			String diffDir = alldiffsDir + issueID + "/";
			String filesSaveDir = generalDir + issueID;
			writeChangedIssue(diffDir, filesSaveDir);
			// System.out.println(path.getName());
		}

	}

	public void writeChangedIssue(String diffDir, String filesSaveDir) {
		File diffInput = new File(diffDir);
		new File(filesSaveDir).mkdirs();
		diffParser parser = new diffParser();
		processIssueDiff p = new processIssueDiff();

		File[] listOfFiles = diffInput.listFiles();
		for (File path : listOfFiles) {
			try {
				diff per_diff = parser.parser(path.getAbsolutePath());
				p.writeChangedFiles(per_diff, filesSaveDir, path.getName()
						.replace(".log", ""));
			} catch (Exception e) {
				System.out.println(path + " Error");
			}
		}

	}

	public void writeChangedFiles(diff per_diff, String saveDir, String commitID) {

		String beforeDir = saveDir + "/" + commitID + "/before/";
		String afterDir = saveDir + "/" + commitID + "/after/";
		new File(beforeDir).mkdirs();
		new File(afterDir).mkdirs();
		try {
			for (file per_file : per_diff.JFiles) {
				// System.out.println(per_file.fileName.replace("/", "."));
				String temppath = afterDir + "/"
						+ per_file.fileName.replace("/", ".");
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						temppath));
				writer.write(per_file.afterPureCode);
				writer.flush();
				writer.close();

				if (per_file.beforePureCode.length() != 0) {
					temppath = beforeDir + "/"
							+ per_file.fileName.replace("/", ".");
					writer = new BufferedWriter(new FileWriter(temppath));
					writer.write(per_file.beforePureCode);
					writer.flush();
					writer.close();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
