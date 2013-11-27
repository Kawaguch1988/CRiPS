/*
 * PrintTest.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.apps;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ClassBlockFileModel.Copier;
import ClassBlockFileModel.LangDefFileCopier;
import ClassBlockFileModel.LangDefFileDtdCopier;
import ClassBlockFileModel.LangDefGenusesCopier;
import ClassBlockFileModel.OutputSelDefClassPageModel;
import bc.BlockConverter;
import bc.j2b.analyzer.JavaToBlockAnalyzer;
import bc.j2b.model.CompilationUnitModel;
import bc.utils.ASTParserWrapper;
import bc.utils.ExtensionChanger;
import clib.common.filesystem.CFile;

/**
 * @author macchan
 */
public class JavaToBlockMain {

	public JavaToBlockMain() {

	}

	public static void main(String[] args) throws Exception {
		JavaToBlockMain jtb = new JavaToBlockMain();
		jtb.process(new File("testcase/Test32.java"), "JISAutoDetect",
				new PrintStream(new File("testcase/Test32.xml")),
				new String[] {});
	}

	// public String run(File file) throws Exception {
	// return run(file, "JISAutoDetect");
	// }

	public String run(File file, String enc, String[] classpaths)
			throws Exception {
		String filePath = ExtensionChanger.changeToXmlExtension(file.getPath());

		process(file, enc, new PrintStream(new File(filePath),
				BlockConverter.ENCODING_BLOCK_XML), classpaths);
		return filePath;
	}

	public void process(File file, String enc, PrintStream out,
			String[] classpaths) throws Exception {
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		JavaToBlockAnalyzer visitor = new JavaToBlockAnalyzer(file, enc);
		// unit.accept(new SimplePrintVisitor(System.out));
		unit.accept(visitor);

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
				selfDefModel.setLocalSelDefClass(name.substring(0,
						name.indexOf(".java")));
				selfDefModel.setGlobalSelDefClass(name.substring(0,
						name.indexOf(".java")));
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

		/*
		 * NodeList lists = menuRoot.getChildNodes(); NodeList elementNodes =
		 * document.getElementsByTagName("BlockDrawer"); Element
		 * additionalElement = document.createElement("hoge");
		 * elementNodes.item(0).appendChild(additionalElement);
		 */
		/*
		 * for (int i = 0; i < lists.getLength(); i++) { Node list =
		 * lists.item(i); System.out.println("listname" + list.getNodeName());
		 * 
		 * if (list.getNodeName().equals("BlockDrawerSet")) { NodeList factry =
		 * list.getChildNodes(); for (int j = 0; j < factry.getLength(); j++) {
		 * Node drawer = factry.item(j); if
		 * (drawer.getNodeName().equals("BlockDrawer")) {
		 * 
		 * } } }
		 * 
		 * }
		 */

		CompilationUnitModel root = visitor.getCompilationUnit();
		root.print(out, 0);
		out.close();
	}
}
