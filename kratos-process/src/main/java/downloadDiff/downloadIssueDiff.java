package downloadDiff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class downloadIssueDiff {

	String tempBat = "C:/Users/Twilight/workspace/kratos/temp.bat";
	String tempDir = "C:/Users/Twilight/workspace/kratos/temp/";

	public static void main(String[] args) {

		String dir = "C:/Users/Twilight/program/kratos/";
		String projectName = "groovy";
		// String issueID = "AVRO-839";

		String saveDir = dir + projectName + "/" + projectName + "_issue_diff";
		String projectRepoDir = "C:/Users/Twilight/privateproject/projectrepository/"
				+ projectName;
		String issueDir = "C:/Users/Twilight/program/kratos/" + projectName
				+ "/" + projectName + "_issue_commits";

		downloadIssueDiff t = new downloadIssueDiff();
		new File(saveDir).mkdirs();

		// t.createTemBat(saveDir, issueID, projectRepoDir, projectName, issueDir);
		// t.runTemBat();
		t.runTems(saveDir, projectRepoDir, projectName, issueDir);

	}

	public void runTems(String saveDir, String projectRepoDir,
			String projectName, String issueDir) {

		File index = new File(tempDir);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}
		index.delete();

		new File(tempDir).mkdirs();

		File issueInput = new File(issueDir);
		File[] listOfFiles = issueInput.listFiles();
		for (File path : listOfFiles) {
			try {
				String issueID = path.getName().replace(".txt", "");
				createTemBat(saveDir, issueID, projectRepoDir, projectName,
						issueDir);
				runTemBat(issueID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void runTemBat(String issueID) {
		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec("cmd.exe /c start " + tempDir + issueID
					+ ".bat");
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createTemBat(String saveDir, String issueID,
			String projectRepoDir, String projectName, String issueDir) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempDir
					+ issueID + ".bat"));
			String echoStatement = "@echo off" + "\n";
			String setStatement = "setlocal EnableDelayedExpansion" + "\n";
			String variableStatement = "SET commitIDs="
					+ issueDir.replace("/", "\\") + "\\" + issueID + ".txt"
					+ "\n" + "SET repository="
					+ projectRepoDir.replace("/", "\\") + "\n" + "SET dir="
					+ saveDir.replace("/", "\\") + "\n" + "SET index=1" + "\n"
					+ "SET issue=x" + "\n";
			String cdStatement = "cd %repository%\\" + "\n"; // potential bug
			String forStatement = "FOR /f \"tokens=*\" %%i IN (%commitIDs%) Do (call :subroutine %%i)"
					+ "\n";
			// String pauseStatement = "PAUSE" + "\n";
			String gotoStatement = "GOTO :eof";
			String subroutine = ":subroutine"
					+ "\n"
					+ "ECHO %1"
					+ "\n"
					+ "if %index%==1 ("
					+ "\n"
					+ "mkdir %dir%\\%1"
					+ "\n"
					+ "set issue=%1) ELSE ("
					+ "\n"
					+ "git diff --unified=9999999 %1~1 %~1>%dir%\\%issue%\\%~1.log"
					+ "\n" + ")" + "\n" + "set /a index=index + 1" + "\n"
					+ "GOTO :eof" + "\n";
			writer.write(echoStatement);
			writer.write(setStatement);
			writer.write(variableStatement);
			writer.write(cdStatement);
			writer.write(forStatement);
			// writer.write(pauseStatement); // can be commented to unpause
			writer.write(gotoStatement);
			writer.write("\n");
			writer.write(subroutine);
			writer.write("exit");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
