package extractFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

import edu.drexel.cs.athena.data.commit;
import edu.drexel.cs.athena.parser.revisionHistory.gitPlainParser;

public class extractor {

	private String project;
	private String repositoryDir;
	private String saveDir;
	private LinkedHashSet<commit> history;
	private LinkedHashMap<String, commit> commitMap = new LinkedHashMap<String, commit>();
	private HashSet<String> filePathList = new HashSet<String>();
	private String perCommit;

	public static void main(String[] args) {

		String project = "pdfbox";
		String repositoryDir = "C:/Users/Twilight/privateproject/kratos/projectRepository/";
		String dataDir = "C:/Users/Twilight/privateproject/kratos/data/";
		// String commitID = "d17b752a0321ebe2e0cbdd0acd6de8698e1a3d1a";

		extractor ex = new extractor(project, dataDir, repositoryDir);
		// ex.saveReferenceFile(commitID);
		ex.saveReferenceFiles();
		// ex.getCorrespondingVersion(commitID);
		// LinkedHashSet<String> referenceFiles = ex.getReferenceFiles(commitID);
		// System.out.println(referenceFiles.size());
		// LinkedHashSet<String> changedFiles = ex.getChangedFiles(commitID);
		// System.out.println(changedFiles.size());

	}

	public extractor(String project, String dataDir, String repositoryDir) {

		this.project = project;
		this.repositoryDir = repositoryDir;
		this.saveDir = dataDir + project + "/diffFiles/";
		String gitPath = repositoryDir + project + "/" + project + "-git.log";
		gitPlainParser parser = new gitPlainParser();
		try {
			parser.process(gitPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		history = parser.getHistory();
		for (commit c : history) {
			commitMap.put(c.commitID, c);
		}

	}

	private void deleteFolder(String path) {

		File index = new File(path);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}

	}

	public void saveReferenceFiles() {

		File file = new File(saveDir);
		String[] commitIDs = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		double len = commitIDs.length;
		double index = 1;
		for (String cmid : commitIDs) {
			// System.out
			// .println(String.format("%.2f", index++ / len * 100) + "%");
			this.perCommit = cmid;
			saveReferenceFile(cmid);
		}
		// System.out.println(commitIDs);

	}

	public void saveReferenceFile(String commitID) {

		getAfterChangeVersion(commitID);
		LinkedHashSet<String> afterReferenceFiles = getReferenceFiles(commitID);
		String beforeDir = saveDir + commitID + "/before/references/";
		String afterDir = saveDir + commitID + "/after/references/";
		if (new File(beforeDir).exists()) {
			deleteFolder(beforeDir);
		}
		if (new File(afterDir).exists()) {
			deleteFolder(afterDir);
		}
		new File(beforeDir).mkdirs();
		new File(afterDir).mkdirs();
		String prefix = repositoryDir + project + "/";
		for (String rf : afterReferenceFiles) {

			// String name = rf.substring(rf.lastIndexOf("/") + 1, rf.length());
			String fullName = rf.replace(prefix, "").replace("/", ".");
			// System.out.println(fullName);
			String afterDest = afterDir + fullName;
			File source = new File(rf);
			File ad = new File(afterDest);
			try {
				Files.copy(source.toPath(), ad.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getBeforeChangeVersion(commitID);
		LinkedHashSet<String> beforeReferenceFiles = getReferenceFiles(commitID);
		for (String rf : beforeReferenceFiles) {
			// String name = rf.substring(rf.lastIndexOf("/") + 1, rf.length());
			String fullName = rf.replace(prefix, "").replace("/", ".");
			String beforeDest = beforeDir + fullName;
			File bd = new File(beforeDest);
			File source = new File(rf);
			try {
				Files.copy(source.toPath(), bd.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println(referenceFiles);

	}

	public LinkedHashSet<String> getReferenceFiles(String commitID) {

		LinkedHashSet<String> changedFiles = getChangedFiles(commitID);
		LinkedHashSet<String> importedFiles = getImportFiles(changedFiles);
		LinkedHashSet<String> packageFiles = getPackageFiles(changedFiles);
		LinkedHashSet<String> referenceFiles = new LinkedHashSet<String>();
		for (String file : importedFiles) {
			if (!changedFiles.contains(file)) {
				referenceFiles.add(file);
			}
		}
		for (String file : packageFiles) {
			if (!changedFiles.contains(file)) {
				referenceFiles.add(file);
			}
		}
		for (String file : changedFiles) {
			String fullPath = repositoryDir + project + "/"
					+ getAbsoluteFilePath(file);
			if (!referenceFiles.remove(fullPath)) {
				System.out.println(this.perCommit);
				System.out
						.println("Cannot pair change file with reference file \n"
								+ fullPath);
			}
		}
		// for (String file : changedFiles) {
		// referenceFiles.remove(file);
		// System.out.println("Remove: " + file);
		// System.out.println(referenceFiles);
		// }
		return referenceFiles;

	}

	public void getBeforeChangeVersion(String commitID) {

		String repositoryPath = repositoryDir + project;
		try {
			String cmd = "cd " + repositoryPath + "&&";
			cmd = cmd + "git checkout " + commitID + "~1&&";
			cmd = cmd + "git ls-files > " + project + "-files.log";
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
			Process p = builder.start();
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(new FileReader(
					repositoryDir + project + "/" + project + "-files.log"));
			String line;
			filePathList = new HashSet<String>();
			while ((line = reader.readLine()) != null) {
				filePathList.add(line);
			}
			reader.close();
			// System.out.println("Successfully changed repository version!");
		} catch (IOException e) {
			System.out.println(this.perCommit);
			System.err.println("Error switching repository");
			e.printStackTrace();
		}

	}

	public void getAfterChangeVersion(String commitID) {

		// Runtime run = Runtime.getRuntime();
		String repositoryPath = repositoryDir + project;
		try {
			String cmd = "cd " + repositoryPath + "&&";
			cmd = cmd + "git checkout " + commitID + "&&";
			cmd = cmd + "git ls-files > " + project + "-files.log";
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
			Process p = builder.start();
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(new FileReader(
					repositoryDir + project + "/" + project + "-files.log"));
			String line;
			filePathList = new HashSet<String>();
			while ((line = reader.readLine()) != null) {
				filePathList.add(line);
			}
			reader.close();
			// System.out.println("Successfully changed repository version!");
		} catch (IOException e) {
			System.out.println(this.perCommit);
			System.err.println("Error switching repository");
			e.printStackTrace();
		}

	}

	public LinkedHashSet<String> getChangedFiles(String commitID) {

		commit c = commitMap.get(commitID);
		LinkedHashSet<String> files = new LinkedHashSet<String>();
		for (String file : c.changedFilePath) {
			String path = file.replace(".", "/").replace("_java", ".java");
			if (getAbsoluteFilePath(path) != null) {
				files.add(path);
			}
		}
		return files;

	}

	private String getAbsolutePathForClassAttributes(String filePath) {

		String aPath = filePath.substring(0, filePath.lastIndexOf("/"));
		for (String abPath : filePathList) {
			if (abPath.contains(aPath)) {
				return abPath;
			}
		}
		System.out.println(this.perCommit);
		System.err.println("Cannot Find Corresponding File or Class: " + aPath);
		return null;

	}

	private String getAbsoluteFilePath(String filePath) {

		// System.out.println(filePath);
		for (String abPath : filePathList) {
			if (abPath.contains(filePath)) {
				return abPath;
			}
		}

		// System.err.println("Treat import as attribute in class");
		return null;

	}

	private String getAbsolutePath(String filePath) {

		// System.out.println(filePath);
		for (String abPath : filePathList) {
			if (abPath.contains(filePath)) {
				return abPath;
			}
		}

		// System.err.println("Treat import as attribute in class");
		return getAbsolutePathForClassAttributes(filePath);

	}

	private LinkedHashSet<String> getPackageFiles(String filePath) {

		String abPath = getAbsolutePath(filePath);
		LinkedHashSet<String> packageFiles = new LinkedHashSet<String>();
		if (abPath == null) {
			System.out.println(this.perCommit);
			System.err.println("Reason: Cannot Find changed File");
			return null;
		}
		String FILE_PATH = repositoryDir + project + "/" + abPath;
		String FILE_DIR = FILE_PATH.substring(0, FILE_PATH.lastIndexOf("/"));
		File folder = new File(FILE_DIR);
		File[] fileList = folder.listFiles();
		for (File f : fileList) {
			// System.out.println("PF: " + f.getAbsolutePath().replace("\\", "/"));
			packageFiles.add(f.getAbsolutePath().replace("\\", "/"));
		}
		return packageFiles;

	}

	private LinkedHashSet<String> getPackageFiles(LinkedHashSet<String> files) {

		LinkedHashSet<String> packageFiles = new LinkedHashSet<String>();
		for (String f : files) {
			LinkedHashSet<String> singlePackageFiles = getPackageFiles(f);
			packageFiles.addAll(singlePackageFiles);
		}
		return packageFiles;

	}

	private LinkedHashSet<String> getImportModules(String filePath) {

		String abPath = getAbsolutePath(filePath);
		LinkedHashSet<String> modules = new LinkedHashSet<String>();
		if (abPath == null) {
			System.out.println(this.perCommit);
			System.err.println("Reason: Cannot Find changed File");
			return null;
		}
		String FILE_PATH = repositoryDir + project + "/" + abPath;
		// System.out.println(abPath);
		try {
			CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
			for (ImportDeclaration id : cu.getImports()) {
				modules.add(id.getNameAsString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(this.perCommit);
			System.err.println("Java parser error");
		}
		return modules;

	}

	private LinkedHashSet<String> getImportModules(LinkedHashSet<String> files) {

		LinkedHashSet<String> modules = new LinkedHashSet<String>();
		for (String f : files) {
			// System.out.println(f);
			LinkedHashSet<String> singleFileModule = getImportModules(f);
			if (singleFileModule.size() != 0) {
				modules.addAll(singleFileModule);
			}
		}
		return modules;

	}

	private LinkedHashSet<String> getImportFiles(
			LinkedHashSet<String> changedFiles) {

		LinkedHashSet<String> importedFiles = new LinkedHashSet<String>();
		LinkedHashSet<String> modules = getImportModules(changedFiles);
		for (String m : modules) {
			/*
			 * Assume that only module name contains project name could be inner-project module
			 */
			if (m.contains(project)) {
				String path = m.replace(".", "/") + ".java";
				String abPath = getAbsolutePath(path);
				if (abPath != null) {
					importedFiles.add(repositoryDir + project + "/" + abPath);
					// System.out.println("IM: " + repositoryDir + project + "/"
					// + abPath);
				} else {
					System.out.println(this.perCommit);
					System.err.println("Reason: Cannot Find Imported File");
				}
			}

		}
		return importedFiles;

	}

}
