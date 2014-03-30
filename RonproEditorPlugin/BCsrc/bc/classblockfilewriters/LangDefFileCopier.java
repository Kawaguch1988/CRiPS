package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class LangDefFileCopier implements Copier {

	private BufferedReader br;

	public void print(File file) {
		// TODO Auto-generated method stub
		try {
			FileInputStream ldfReader = new FileInputStream(
					"ext/block/lang_def_turtle.xml");

			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "SJIS");
			br = new BufferedReader(ldfISR);

			// File ldf = new File("/ext/block/lang_def.dtd");

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream turtlePs = new PrintStream(turtleByteArray);
			// ���ׂĂ̍s���R�s�[����
			String line;
			while ((line = br.readLine()) != null) {
				// ��s�������� >>lang_def.xml
				if (line.contains("lang_def_menu")) {
					// ���j���[�̏�������
					turtlePs.println("\t\t&lang_def_menu_project;");
				} else {
					turtlePs.println(line);
				}
			}
			// menu���̃R�s�[
			// ps�ɏ������������̂����ׂĕ�����ɕϊ�����
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def_project.xml");
			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");
			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "SJIS");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("�����`�t�@�C���o�͎��ɃG���[���������܂����Flang_def_file");
		}

	}

}
