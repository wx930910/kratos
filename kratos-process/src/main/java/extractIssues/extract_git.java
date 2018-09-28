package extractIssues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.CSVUtils;
import edu.drexel.cs.athena.data.commit;
import edu.drexel.cs.athena.parser.revisionHistory.gitPlainParser;

public class extract_git {

	public static void main(String[] args) {

		String dir = "C:/Users/Twilight/program/kratos/";
		String projectName = "groovy";
		String pkey = "GROOVY-[0-9]*";

		String projectRepoDir = "C:/Users/Twilight/privateproject/projectrepository/"
				+ projectName;
		String gitPath = projectRepoDir + "/git-" + projectName + ".log";
		String issuePath = dir + projectName + "/" + projectName + ".csv";
		String issueSaveDir = dir + projectName + "/" + projectName
				+ "_issue_commits/";

		new File(issueSaveDir).mkdirs();

		try {
			Set<String> keys = CSVUtils.getIssueKeys(issuePath);
			// System.out.println(keys.toString());
			for (String key : keys) {
				writeToText(key, issueSaveDir, pkey, gitPath);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeToText(String key, String outDir, String pkey,
			String gitPath) throws FileNotFoundException {
		Set<String> commitIds = find(key, pkey, gitPath);
		new File(outDir).mkdirs();
		PrintWriter pw = new PrintWriter(outDir + "/" + key + ".txt");
		pw.println(key);
		for (String id : commitIds) {
			pw.println(id);
		}
		pw.close();
		System.out.println(key + " write sucessfully.");
	}

	private static Set<String> find(String key, String pkey, String gitPath)
			throws FileNotFoundException {
		Set<String> res = new HashSet<String>();

		gitPlainParser parser = new gitPlainParser();
		parser.process(gitPath);
		LinkedHashSet<commit> history = parser.getHistory();

		for (commit c : history) {
			Set<String> jira_issues = new HashSet<String>();
			// System.out.println(c.commitID);
			if (c.commitMessage != null) {
				jira_issues.addAll(fullmatch(c.commitMessage, pkey));
			}
			if (c.msg != null) {
				jira_issues.addAll(fullmatch(c.msg, pkey));
			}

			if (jira_issues.contains(key)) {
				res.add(c.commitID);
			}
		}

		return res;
	}

	// return all jira issue keys in one commit
	private static Set<String> fullmatch(String msg, String pkey) {
		Set<String> res = new HashSet<String>();
		String temp = msg;
		Pattern p = Pattern.compile(pkey);
		Matcher m = p.matcher(temp);
		while (m.find()) {
			String fkey = m.group(0);
			res.add(fkey);
		}
		return res;
	}

}
