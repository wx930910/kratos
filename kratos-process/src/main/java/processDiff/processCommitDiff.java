package processDiff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import diff.data.diff;
import diff.data.file;
import extract.changes.diffParser;

public class processCommitDiff {

	private String project;
	private String diffDir;
	private String saveDir;

	public static void main(String[] args) {

		String project = "pdfbox";
		String dir = "C:/Users/Twilight/privateproject/kratos/data/";
		processCommitDiff processor = new processCommitDiff(project, dir);
		processor.writeDiffs();

	}

	public processCommitDiff(String project, String dir) {

		this.project = project;
		this.diffDir = dir + this.project + "/diff/";
		this.saveDir = dir + this.project + "/diffFiles/";

	}

	public void writeDiffs() {

		File diffFolder = new File(diffDir);
		File[] list = diffFolder.listFiles();
		diffParser parser = new diffParser();
		for (File path : list) {
			try {
				diff per_diff = parser.parser(path.getAbsolutePath());
				writePerDiff(per_diff);
			} catch (Exception e) {
				System.out.println(path + " Error");
			}
		}

	}

	private void writePerDiff(diff d) {

		String beforeDir = saveDir + d.CommitID + "/before/changes/";
		String afterDir = saveDir + d.CommitID + "/after/changes/";
		new File(beforeDir).mkdirs();
		new File(afterDir).mkdirs();
		try {
			for (file per_file : d.JFiles) {
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
