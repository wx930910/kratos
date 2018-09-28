package genDsm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

public class genCommitDsm {

	private String project;
	private String changesFileDir;
	private String saveDir;
	private String xmlDir;
	private String jarPath;
	private String tempBatch;

	public static void main(String[] args) {

		String project = "pdfbox";
		String dir = "C:/Users/Twilight/privateproject/kratos/data/";
		String commitID = "b364d4b2bc7aa37e40e58701e5baba2391241a02";
		String jarPath = "C:/Users/Twilight/privateproject/kratos/genSdsm-8-2-2018.jar";

		genCommitDsm generator = new genCommitDsm(project, dir, jarPath);
		// generator.writetxt(commitID);
		// generator.writeTempBatch(commitID);
		// generator.runBatch();
		generator.genDsm();

	}

	public genCommitDsm(String project, String dir, String jarPath) {

		this.project = project;
		this.changesFileDir = dir + this.project + "/diffFiles/";
		this.saveDir = dir + this.project + "/changeDsms/";
		this.xmlDir = dir + this.project + "/xml/";
		this.jarPath = jarPath;
		this.tempBatch = dir + "temp.bat";

	}

	public void genDsm() {
		generateXML();
		generateTempBat();
		// runBatch();
	}

	public void runBatch() {

		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec("cmd.exe /c start  " + tempBatch);
			// p.waitFor();
			File processCheck = new File(tempBatch);
			// Boolean canBeDeleted = processCheck.setWritable(true);
			// System.out.println(processCheck.canWrite());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// catch (InterruptedException e) {
		// e.printStackTrace();
		// }

	}

	public void generateXML() {

		File file = new File(changesFileDir);
		String[] commitIDs = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		for (String cmid : commitIDs) {
			writetxt(cmid);
		}

	}

	public void generateTempBat() {
		File file = new File(xmlDir);
		String[] commitIDs = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		try {
			FileWriter fw = new FileWriter(new File(tempBatch));
			for (String cmid : commitIDs) {
				String cmd = generateSingleCMD(cmid);
				fw.write(cmd);
			}
			fw.flush();
			fw.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

	}

	public String generateSingleCMD(String commitID) {

		new File(saveDir + commitID + "/").mkdirs();
		String xmlFolder = xmlDir + commitID + "/";
		String beforeFolder = xmlFolder + "before/";
		String beforetxtPath = beforeFolder + commitID + "-before.txt";
		String beforexmlPath = beforetxtPath.replace(".txt", ".xml");
		String beforeDSMSavePath = saveDir + commitID + "/" + commitID
				+ "-before.dsm";
		String beforePrefix = (changesFileDir + commitID + "/before/").replace(
				"/", "\\");
		String afterFolder = xmlFolder + "after/";
		String aftertxtPath = afterFolder + commitID + "-after.txt";
		String afterxmlPath = aftertxtPath.replace(".txt", ".xml");
		String afterDSMSavePath = saveDir + commitID + "/" + commitID
				+ "-after.dsm";
		String afterPrefix = (changesFileDir + commitID + "/after/").replace(
				"/", "\\");
		String dsmDir = saveDir + commitID;
		String cmd = "und> und process " + beforetxtPath + "\n";
		cmd = cmd + "mkdir " + dsmDir.replace("/", "\\") + "\n";
		cmd = cmd + "java -jar " + jarPath + " -cytoscape -f " + beforexmlPath
				+ " -o " + beforeDSMSavePath + " -xprefix " + beforePrefix
				+ "\n";
		cmd = cmd + "und> und process " + aftertxtPath + "\n";
		cmd = cmd + "java -jar " + jarPath + " -cytoscape -f " + afterxmlPath
				+ " -o " + afterDSMSavePath + " -xprefix " + afterPrefix + "\n";
		return cmd;

	}

	public void writeTempBatch(String commitID) {

		new File(saveDir + commitID + "/").mkdirs();
		String xmlFolder = xmlDir + commitID + "/";
		String beforeFolder = xmlFolder + "before/";
		String beforetxtPath = beforeFolder + commitID + "-before.txt";
		String beforexmlPath = beforetxtPath.replace(".txt", ".xml");
		String beforeDSMSavePath = saveDir + commitID + "/" + commitID
				+ "-before.dsm";
		String beforePrefix = (changesFileDir + commitID + "/before/").replace(
				"/", "\\");
		String afterFolder = xmlFolder + "after/";
		String aftertxtPath = afterFolder + commitID + "-after.txt";
		String afterxmlPath = aftertxtPath.replace(".txt", ".xml");
		String afterDSMSavePath = saveDir + commitID + "/" + commitID
				+ "-after.dsm";
		String afterPrefix = (changesFileDir + commitID + "/after/").replace(
				"/", "\\");
		String dsmDir = saveDir + commitID;
		String cmd = "und> und process " + beforetxtPath + "\n";
		cmd = cmd + "mkdir " + dsmDir.replace("/", "\\") + "\n";
		cmd = cmd + "java -jar " + jarPath + " -cytoscape -f " + beforexmlPath
				+ " -o " + beforeDSMSavePath + " -xprefix " + beforePrefix
				+ "\n";
		cmd = cmd + "und> und process " + aftertxtPath + "\n";
		cmd = cmd + "java -jar " + jarPath + " -cytoscape -f " + afterxmlPath
				+ " -o " + afterDSMSavePath + " -xprefix " + afterPrefix + "\n";
		cmd = cmd + "exit";
		try {
			FileWriter fw = new FileWriter(new File(tempBatch));
			fw.write(cmd);
			fw.flush();
			fw.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

	}

	public void writetxt(String commitID) {

		String xmlFolder = xmlDir + commitID + "/";
		String beforeFolder = xmlFolder + "before/";
		String beforeFileDir = changesFileDir + commitID + "/before/";
		String afterFolder = xmlFolder + "after/";
		new File(beforeFolder).mkdirs();
		new File(afterFolder).mkdirs();
		String afterFileDir = changesFileDir + commitID + "/after/";
		try {
			PrintWriter pw = new PrintWriter(beforeFolder + commitID
					+ "-before.txt");
			pw.println("und> und create -db " + beforeFolder + commitID
					+ "-before.udb -languages java");
			pw.println("und> und add " + beforeFileDir + " " + beforeFolder
					+ commitID + "-before.udb");
			pw.println("und> und analyze " + beforeFolder + commitID
					+ "-before.udb");
			pw.println("und> export -dependencies file cytoscape "
					+ beforeFolder + commitID + "-before.xml");
			pw.flush();
			pw.close();
			pw = new PrintWriter(afterFolder + commitID + "-after.txt");
			pw.println("und> und create -db " + afterFolder + commitID
					+ "-after.udb -languages java");
			pw.println("und> und add " + afterFileDir + " " + afterFolder
					+ commitID + "-after.udb");
			pw.println("und> und analyze " + afterFolder + commitID
					+ "-after.udb");
			pw.println("und> export -dependencies file cytoscape "
					+ afterFolder + commitID + "-after.xml");
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
