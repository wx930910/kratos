package downloadDiff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;

import edu.drexel.cs.athena.data.commit;
import edu.drexel.cs.athena.parser.revisionHistory.gitPlainParser;

public class downloadDiff {

	private String project;
	private String dir;
	private LinkedHashSet<commit> history;
	private int highThre;
	private String saveDir;

	public static void main(String[] args) {

		String project = "pdfbox";
		String dir = "C:/Users/Twilight/privateproject/kratos/projectRepository/";
		String saveDir = "C:/Users/Twilight/privateproject/kratos/data/";

		int highThre = 30;

		downloadDiff downloader = new downloadDiff(project, dir, saveDir,
				highThre);
		downloader.genCommand();

	}

	public downloadDiff(String project, String projectRepo, String saveDir,
			int highThre) {
		this.project = project;
		this.dir = projectRepo;
		this.highThre = highThre;
		this.saveDir = saveDir + "/" + project + "/diffs/";
		String gitPath = projectRepo + project + "/" + project + "-git.log";
		gitPlainParser parser = new gitPlainParser();
		try {
			parser.process(gitPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		history = parser.getHistory();
	}

	public void genCommand() {

		String repositoryPath = dir + project;

		new File(saveDir).mkdirs();
		for (commit c : history) {
			if (c.changed_files.size() < highThre) {
				String command = "cd " + repositoryPath + "&&"
						+ "git checkout trunk && ";
				command = command + "git diff --unified=9999999 " + c.commitID
						+ "~1 " + c.commitID + ">" + saveDir + c.commitID
						+ ".log";
				runCmd(command);
			}
		}

	}

	public void runCmd(String command) {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
		Process p;
		try {
			p = builder.start();
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
