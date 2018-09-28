package spreadsheet;

import java.io.File;
import java.io.FilenameFilter;

import matrix.Matrix;
import matrix.MatrixIO;
import utils.dsmUtils;
import edu.drexel.cs.rise.minos.MinosException;

public class gen_excel {

	private String project;
	private String dsmDir;
	private String saveDir;

	public static void main(String[] args) {
		String project = "pdfbox";
		String dir = "C:/Users/Twilight/privateproject/kratos/data/";

		String commitID = "b364d4b2bc7aa37e40e58701e5baba2391241a02";

		gen_excel gen = new gen_excel(project, dir);
		gen.write(commitID);

	}

	public gen_excel(String project, String dir) {

		this.project = project;
		this.dsmDir = dir + this.project + "/changeDsms/";
		this.saveDir = dir + this.project + "/diffDsms/";

	}

	public void write() {

		File file = new File(dsmDir);
		String[] commitIDs = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		for (String cmid : commitIDs) {
			write(cmid);
		}

	}

	private void write(String cmid) {

		String beforePath = this.dsmDir + cmid + "/" + cmid + "-before.dsm";
		String afterPath = this.dsmDir + cmid + "/" + cmid + "-after.dsm";
		try {
			Matrix m = dsmUtils.getUnion(beforePath, afterPath);
			m.trim();
			m.sort();
			MatrixIO io = new MatrixIO();
			new File(saveDir + cmid).mkdirs();
			String xlsxPath = this.saveDir + cmid + ".xlsx";
			io.writeXlsx(m, xlsxPath);
		} catch (MinosException e) {
			e.printStackTrace();
		}

	}

}
