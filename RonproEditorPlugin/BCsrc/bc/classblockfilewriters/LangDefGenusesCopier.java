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

public class LangDefGenusesCopier implements Copier {

	private BufferedReader br;

	public void print(File file) {
		try {
			FileInputStream ldfReader = new FileInputStream(
					"ext/block/lang_def_genuses.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "UTF-8");
			br = new BufferedReader(ldfISR);

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(turtleByteArray);
			// ���ׂĂ̍s���R�s�[����
			String line;
			while ((line = br.readLine()) != null) {
				// ��s�������� >>lang_def.xml
				ps.println(line);
				// �v���W�F�N�g�̃u���b�N��`�t�@�C���̒ǉ�
				if (line.contains("&lang_def_genuses_turtle")) {
					ps.println("&lang_def_genuses_project;");
				}
			}
			// menu���̃R�s�[
			// ps�ɏ������������̂����ׂĕ�����ɕϊ�����
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def_genuses.xml");

			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "UTF-8");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("�����`�t�@�C���o�͎��ɃG���[���������܂����Flang_def_genuses");
		}

	}

}
