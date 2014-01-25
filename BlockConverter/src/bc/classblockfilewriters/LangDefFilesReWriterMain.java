package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.utils.ASTParserWrapper;

public class LangDefFilesReWriterMain {

	private File file;
	private String enc;
	private String[] classpaths;
	private BufferedReader br;
	private Map<String, String> addedMethods = new HashMap<String, String>();
	private Map<String, String> addedMethodsJavaType = new HashMap<String, String>();
	private List<String> addedClasses = new LinkedList<String>();

	private Map<String, Family> familyList = new HashMap<String, Family>();

	private List<String> classes;

	public LangDefFilesReWriterMain(File file, String enc, String[] classpaths) {
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
		LangDefFilesRewriter selfDefModel = new LangDefFilesRewriter(
				classDefFile, this.file.getName());
		for (String name : file.getParentFile().list()) {
			if (name.endsWith(".java")) {
				// java�t�@�C�����
				File javaFile = new File(file.getParentFile().getPath() + "/"
						+ name);
				name = name.substring(0, name.indexOf(".java"));

				Family family = new Family();
				family.addFamilyMember(name);
				familyList.put(name, family);

				Map<String, List<PublicMethodInfo>> methods = analyzeJavaFile(
						name, javaFile, name);
				// ���[�J���ϐ��u���b�N�̃��f����ǉ�
				selfDefModel.setLocalVariableBlockModel(name, methods);// ���\�b�h���X�g�������ɒǉ�
				// �C���X�^���X�ϐ��u���b�N�̃��f����ǉ�
				selfDefModel.setInstanceVariableBlockMode(name, methods);
				addedClasses.add(name);
				for (String className : classes) {
					if (!className.equals(name)) {
						// ���[�J���ϐ��u���b�N�̃��f����ǉ�
						selfDefModel.setLocalVariableBlockModel(className,
								methods);// ���\�b�h���X�g�������ɒǉ�
						// �C���X�^���X�ϐ��u���b�N�̃��f����ǉ�
						selfDefModel.setInstanceVariableBlockMode(className,
								methods);
						addedClasses.add(className);
					}

				}

				// �^�ϊ��u���b�N���f���̒ǉ�
				selfDefModel.setConvertBlockModel(name);
				// �����u���b�N���f���̒ǉ�

			}
		}
		// �p���֌W�ɂ���u���b�N�B���t�@�~���[�ɏo��
		printLangDefFamilies();

		// langDef�t�@�C�����쐬����
		Copier langDefXml = new LangDefFileCopier();
		Copier langDefDtd = new LangDefFileDtdCopier();
		langDefXml.print(file);
		langDefDtd.print(file);

		// genuse�t�@�C�����쐬����@���̍ۂ�project�t�@�C���̏ꏊ��ǋL����
		Copier genusCopier = new LangDefGenusesCopier();
		genusCopier.print(file);

		FileReader reader = new FileReader(file);

		br = new BufferedReader(reader);
		String str;

		while ((str = br.readLine()) != null) {
			if (str.contains(" extends Turtle")) {
				File turtleMenu = new File("ext/block/lang_def_menu_turtle.xml");
				selfDefModel.printMenu(projectMenuFile, turtleMenu);
				selfDefModel.printGenus();
				this.addedMethods = selfDefModel.getAddedMethods();
				this.addedMethodsJavaType = selfDefModel
						.getAddedMethodsJavaType();
				return;
			}
		}
		// ���j���[�̏o��
		File cuiMenu = new File("ext/block/lang_def_menu_cui.xml");
		selfDefModel.printMenu(projectMenuFile, cuiMenu);

		// �v���W�F�N�g�̃I�u�W�F�N�g�u���b�N�����o�͂���
		selfDefModel.printGenus();
		this.addedMethods = selfDefModel.getAddedMethods();
		this.addedMethodsJavaType = selfDefModel
				.getAddedMethodsJavaType();
	}

	public List<String> getAddedClasses() {
		return this.addedClasses;
	}

	private void printLangDefFamilies() {
		// �o�^���Ă�����familyList�𐮗�����
		List<String> deleteList = new LinkedList<String>();
		// ���ׂẴN���X�̈����u���b�N���Aobject�^�����u���b�N�̃t�@�~���[�Ƃ��ďo�͂���

		for (String key : familyList.keySet()) {
			if (existAsOtherFamilyMember(key)
					|| familyList.get(key).getFamilyMember().size() == 1) {
				deleteList.add(key);
			}
		}

		for (String key : deleteList) {
			familyList.remove(key);
		}

		LangDefFamiliesCopier langDefFamilies = new LangDefFamiliesCopier();
		langDefFamilies.setProjectFamilies(familyList);
		langDefFamilies.print(file);
		// �c�����t�@�~���[��ǉ�����lang_def_families�����݂̃f�B���N�g���ɏo�͂���
	}

	private boolean existAsOtherFamilyMember(String name) {
		for (String key : familyList.keySet()) {
			if (!key.equals(name)
					&& familyList.get(key).getFamilyMember().contains(name)) {
				return true;
			}
		}

		return false;
	}

	private Map<String, List<PublicMethodInfo>> analyzeJavaFile(String name,
			File file, String childName) throws IOException {
		// java�t�@�C�����
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		MethodAnalyzer visitor = new MethodAnalyzer();

		// addFamily

		// �p���`�F�b�N
		Map<String, List<PublicMethodInfo>> methods = new HashMap<String, List<PublicMethodInfo>>();
		unit.accept(visitor);

		String superClassName = visitor.getSuperClassName();

		if (superClassName != null
				&& existCurrentDirectry(superClassName + ".java")) {
			familyList.get(childName).addFamilyMember(superClassName);
			methods = analyzeJavaFile(superClassName,
					new File(file.getParentFile().getPath() + "/"
							+ superClassName + ".java"), childName);
		}

		classes = visitor.getClasses();

		methods.put(name, visitor.getMethods());
		return methods;
	}

	public Map<String, String> getAddedMethods() {
		return this.addedMethods;
	}

	public Map<String, String> getAddedMethodsJavaType() {
		return this.addedMethodsJavaType;
	}

	private Boolean existCurrentDirectry(String fileName) {
		for (String name : file.getParentFile().list()) {
			if (name.equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	class Family {
		List<String> familyList = new ArrayList<String>();

		protected void addFamilyMember(String member) {
			familyList.add(member);
		}

		protected List<String> getFamilyMember() {
			return this.familyList;
		}

	}
}
