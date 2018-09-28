package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class testCMD {

	public static void main(String[] args) {
		try {
			test();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void test() throws IOException {

		String cmd = "java -jar C:/Users/Twilight/privateproject/kratos/genSdsm-8-2-2018.jar -cytoscape -f C:/Users/Twilight/privateproject/kratos/data/pdfbox/xml/000e4b3ecd009b82b9b32f0b449a3fa9c565d728/before/000e4b3ecd009b82b9b32f0b449a3fa9c565d728-before.xml -o C:/Users/Twilight/privateproject/kratos/data/pdfbox/changeDsms/000e4b3ecd009b82b9b32f0b449a3fa9c565d728/000e4b3ecd009b82b9b32f0b449a3fa9c565d728-before.dsm";
		String tempPath = "C:/Users/Twilight/Desktop/temp.bat";
		FileWriter writer = new FileWriter(new File(tempPath));
		writer.write(cmd);
		writer.flush();
		writer.close();
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c start", cmd);
		Process p;
		try {
			p = builder.start();
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
