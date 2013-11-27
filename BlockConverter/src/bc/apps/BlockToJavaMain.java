package bc.apps;

import java.io.File;

import org.w3c.dom.Document;

import ClassBlockFileModel.OutputSelDefClassPageModel;
import bc.b2j.analyzer.BlockToJavaAnalyzer;
import bc.b2j.model.JavaSourceWriter;
import bc.b2j.model.ProgramModel;
import bc.b2j.model.ResolveSyntaxError;
import bc.utils.DomParserWrapper;
import bc.utils.ExtensionChanger;

public class BlockToJavaMain {

	public static void convert(File openBlockXmlFile, String enc,
			String[] classpaths) throws Exception {

		Document document = DomParserWrapper.parse(openBlockXmlFile.getPath());
		System.out.println("document:" + openBlockXmlFile.getPath());

		BlockToJavaAnalyzer visitor = new BlockToJavaAnalyzer(
				openBlockXmlFile.getName());
		visitor.visit(document);

		ProgramModel root = visitor.getProgramModel();

		JavaSourceWriter writer = new JavaSourceWriter();

		File javaFile = new File(
				ExtensionChanger.changeToJavaExtension(openBlockXmlFile
						.getPath()));

		OutputSourceModel sourceModel = new OutputSourceModel(javaFile, enc,
				classpaths);

		// project��xml�t�@�C�����쐬����
		File classDefFile = new File(openBlockXmlFile.getParentFile().getPath()
				+ "/lang_def_guneses_" + openBlockXmlFile.getName());
		// menu����xml���쐬�A�ior�ǉ�)
		File projectMenuFile = new File(openBlockXmlFile.getParentFile()
				.getPath() + "/lang_def_menu_project.xml");

		OutputSelDefClassPageModel selfDefModel = new OutputSelDefClassPageModel(
				classDefFile, projectMenuFile);
		// ���f���ɒǉ�����N���X���Z�b�g����
		selfDefModel.setSelDefClassModel(visitor.getSelDefClassModels());

		// �N���X�̃u���b�N�����o�͂���
		selfDefModel.print();

		ResolveSyntaxError resolveError = new ResolveSyntaxError();

		resolveError.resolveError(root);

		writer.print(root, sourceModel);

		sourceModel.save();
	}
}
