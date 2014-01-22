package ronproeditor.ext;

import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ronproeditor.REApplication;
import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;

// TODO CompileError.csv���o�͂������PPV���N���ł��Ȃ��Ȃ�s�������
public class RECreateCocoDataManager {
	REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppv�t�H���_�ɓW�J����
	private static String ORIGINAL_DATA_FILE = "CompileError.csv"; // ppv����o�͂����csv�t�@�C��
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext����ErrorKinds
	private static String DATA_FILE = "CompileErrorLog.csv"; // Coco�p�̃R���p�C���G���[�f�[�^
	private PPProjectSet ppProjectSet;

	public RECreateCocoDataManager(REApplication application) {
		this.application = application;
	}

	public void createCocoData() {
		// �m�F�_�C�A���O
		// int res = JOptionPane.showConfirmDialog(null,
		// "�f�[�^�̍쐬�ɂ͎��Ԃ�������܂����C��낵���ł����H", "�f�[�^�̍쐬",
		// JOptionPane.OK_CANCEL_OPTION);
		// if (res != JOptionPane.OK_OPTION) {
		// return;
		// }

		// CompileError.csv�������I�ɃG�N�X�|�[�g����
		autoExportCompileErrorCSV();

		// �����I�ɃG�N�X�|�[�g�����t�@�C����Coco�p�f�[�^�ɕϊ�����
		convertCompileErrorData();

		// ������肻������ppv�̕����X���b�h�����Ȃ��Ƃ����Ȃ�
		// Thread thread = new Thread() {
		// public void run() {
		// try {
		// // CompileError.csv�������I�ɃG�N�X�|�[�g����
		// autoExportCompileErrorCSV();
		//
		// // �����I�ɃG�N�X�|�[�g�����t�@�C����Coco�p�f�[�^�ɕϊ�����
		// convertCompileErrorData();
		// } catch (Exception ex) {
		// throw new RuntimeException("ppv�f�[�^�쐬�Ɏ��s���܂���");
		// }
		// }
		// };
		//
		// thread.run();
	}

	private void autoExportCompileErrorCSV() {
		REPresVisualizerManager ppvManager = new REPresVisualizerManager(
				application);
		ppvManager.exportAndImportAll();
		PPDataManager ppDataManager = ppvManager.getPPDataManager();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		// TODO Hardcoding
		ppProjectSet = ppDataManager.openProjectSet("hoge", true, true, true);
	}

	private void convertCompileErrorData() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString()
				+ "/";

		checkAllFileExist();

		// �G���[�̎�ރf�[�^�����[�h
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

		// CompileError�f�[�^��Coco�p�ɃR���o�[�g
		try {
			CCCompileErrorConverter errorConverter = new CCCompileErrorConverter(
					manager);
			errorConverter.convertData(ppvRootPath + ORIGINAL_DATA_FILE,
					ppvRootPath + DATA_FILE);
		} catch (Exception e) {
			throw new RuntimeException("CocoViewer�̃f�[�^�ϊ����ł��܂���ł���");
		}
	}

	private void checkAllFileExist() {
		checkOneFileExist(DATA_FILE);
		checkOneFileExist(ORIGINAL_DATA_FILE);
	}

	private void checkOneFileExist(String filename) {
		application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).findOrCreateFile(filename);
	}

	public PPProjectSet getppProjectSet() {
		return ppProjectSet;
	}
}
