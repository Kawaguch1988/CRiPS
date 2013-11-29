package ClassBlockFileModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.j2b.analyzer.MethodAnalyzer;
import bc.utils.ASTParserWrapper;
import clib.common.filesystem.CFile;

public class LangDefFileReWriter {

	private File file;
	private String enc;
	private String[] classpaths;
	private BufferedReader br;

	public LangDefFileReWriter(File file, String enc, String[] classpaths) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;
	}

	public void rewrite() throws Exception {
		// ohata �u�W�F�N�g�u���b�N�̏����o��
		// �I�u�W�F�N�g�ϐ��u���b�N��xml�t�@�C�����쐬����
		File classDefFile = new File(file.getParentFile().getPath()
				+ "/lang_def_genuses_project.xml");
		// menu����xml���쐬�A�ior�ǉ�)

		File projectMenuFile = new File(file.getParentFile().getPath()
				+ "/lang_def_menu_project.xml");

		// �����f�B���N�g�����̂��ׂĂ�java�t�@�C�����p�[�X���A���f���ɒǉ�����
		OutputSelDefClassPageModel selfDefModel = new OutputSelDefClassPageModel(
				classDefFile, projectMenuFile);
		for (String name : file.getParentFile().list()) {
			if (name.endsWith(".java")) {
				// java�t�@�C�����
				CompilationUnit unit = ASTParserWrapper.parse(file, enc,
						classpaths);
				MethodAnalyzer visitor = new MethodAnalyzer();

				// �p�����Ă���A�e�N���X�̃��\�b�h��ǂ�
				// �p�����Ă邩�H
				// �����܂��ȗ���
				// java�t�@�C�����-----
				// | |
				// �p���`�F�b�N |
				// | ---------�p����̉��
				// java�t�@�C�����

				// �p���`�F�b�N
				File classFile = new File(file.getParentFile().getPath() + "/"
						+ name);
				FileReader reader = new FileReader(classFile);
				br = new BufferedReader(reader);
				String str;

				Pattern p = Pattern.compile("[^ ]+");
				while ((str = br.readLine()) != null) {

					if (str.contains("extends")) {
						str = str.substring(
								str.indexOf("extends") + "extends".length(),
								str.length());
						Matcher m = p.matcher(str);
						if (m.find()) {

						}
						break;
					}
				}

				unit.accept(visitor);

				selfDefModel.setLocalSelDefClass(
						name.substring(0, name.indexOf(".java")),
						visitor.getMethods());// ���\�b�h���X�g�������ɒǉ�
				selfDefModel.setGlobalSelDefClass(
						name.substring(0, name.indexOf(".java")),
						visitor.getMethods());
			}
		}

		// langDef�t�@�C�������݂��Ȃ��ꍇ�́A�쐬����
		if (!new File(file.getParentFile().getPath() + "/lang_def_project.xml")
				.exists()) {
			Copier langDefXml = new LangDefFileCopier();
			Copier langDefDtd = new LangDefFileDtdCopier();
			langDefXml.print(file);
			langDefDtd.print(file);
		}
		// genuses�t�@�C�����Ȃ��ꍇ�͍쐬����@���̍ۂ�project�t�@�C���̏ꏊ��ǋL����
		if (!new File(file.getParentFile().getPath() + "/lang_def_genuses.xml")
				.exists()) {
			Copier genusCopier = new LangDefGenusesCopier();
			genusCopier.print(file);
		}

		CFile jFile = new CFile(file);
		if (jFile.loadText().indexOf(" extends Turtle") != -1) {
			File turtleMenu = new File("ext/block/lang_def_menu_turtle.xml");
			selfDefModel.printMenu(projectMenuFile, turtleMenu);
		} else {
			File cuiMenu = new File("ext/block/lang_def_menu_cui.xml");
			selfDefModel.printMenu(projectMenuFile, cuiMenu);
		}

		// �N���X�̃u���b�N�����o�͂���
		selfDefModel.printGenus();

	}

	public void analyzeJavaFile(String name) throws IOException {
		// java�t�@�C�����
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		MethodAnalyzer visitor = new MethodAnalyzer();

		// �p���`�F�b�N
		File classFile = new File(file.getParentFile().getPath() + "/" + name);
		FileReader reader = new FileReader(classFile);
		br = new BufferedReader(reader);
		String str;

		Pattern p = Pattern.compile("[^ ]+");
		while ((str = br.readLine()) != null) {
			if (str.contains("extends")) {
				str = str.substring(
						str.indexOf("extends") + "extends".length(),
						str.length());
				Matcher m = p.matcher(str);
				if (m.find()) {

				}
				break;
			}
		}

		unit.accept(visitor);

	}

}
