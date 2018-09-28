package genDsm;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class genBatch_process {

	public static void main(String[] args) throws FileNotFoundException {

		String projectName = "groovy";

		String dir = "C:/Users/Twilight/program/kratos/";
		String inputDir = dir + projectName + "/" + projectName
				+ "_change_files";
		String outDir = dir + projectName + "/" + projectName + "_issue_dsm";
		String genSdsmPath = "C:/Users/Twilight/workspace/kratos/lib/genSdsm-8-2-2018.jar";
		String tempBatch = outDir + "/batch_genSdsm.bat";
		String dsmDir = outDir + "/dsm";
		String tempXmlDir = outDir + "/temp_xml";

		writeBatch(inputDir, outDir, genSdsmPath, tempBatch, dsmDir,
				tempXmlDir, projectName);
		runBatch(tempBatch);

	}

	private static void runBatch(String tempBatch) {

		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec("cmd.exe /c start " + tempBatch);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeBatch(String inputDir, String outDir,
			String genSdsmPath, String tempBatch, String dsmDir,
			String tempXmlDir, String project_name)
			throws FileNotFoundException {

		new File(outDir).mkdirs();
		List<String> issueIds = findFoldersInDirectory(inputDir);
		PrintWriter printwriter = new PrintWriter(tempBatch);
		printwriter.println("mkdir " + dsmDir.replace("/", "\\"));

		for (String issue_name : issueIds) {
			System.out.println(issue_name);
			String issueDir = inputDir + "/" + issue_name;
			List<String> commitIds = findFoldersInDirectory(issueDir);
			for (String id : commitIds) {
				String dir2 = issueDir + "/" + id;
				List<String> before_after = findFoldersInDirectory(dir2);
				for (String ba : before_after) {
					String directory = dir2 + "/" + ba;
					String tempXmlFolder = tempXmlDir + "/" + issue_name + "/"
							+ id + "/" + ba;
					new File(tempXmlFolder).mkdirs();
					String name = issue_name + "-" + id + "-" + ba;
					PrintWriter pw = new PrintWriter(tempXmlFolder + "/" + name
							+ ".txt");
					pw.println("und> und create -db " + tempXmlFolder + "/"
							+ name + ".udb -languages java");
					pw.println("und> und add " + directory + " "
							+ tempXmlFolder + "/" + name + ".udb");
					pw.println("und> und analyze " + tempXmlFolder + "/" + name
							+ ".udb");
					pw.println("und> export -dependencies file cytoscape "
							+ tempXmlFolder + "/" + name + ".xml");
					printwriter.println("und> und process " + tempXmlFolder
							+ "/" + name + ".txt");
					printwriter.println("mkdir " + dsmDir.replace("/", "\\")
							+ "\\" + issue_name + "\\" + id);
					printwriter.println("java -jar "
							+ genSdsmPath.replace("/", "\\")
							+ " -cytoscape -f "
							+ tempXmlFolder.replace("/", "\\") + "\\" + name
							+ ".xml -o " + dsmDir.replace("/", "\\") + "\\"
							+ issue_name + "\\" + id + "\\" + name
							+ ".dsm -xprefix " + inputDir.replace("/", "\\")
							+ "\\" + issue_name + "\\" + id + "\\" + ba + "\\");
					pw.close();
				}
			}
		}
		printwriter.close();

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
