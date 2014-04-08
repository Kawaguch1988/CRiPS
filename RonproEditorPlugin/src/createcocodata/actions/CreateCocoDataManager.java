package createcocodata.actions;

import javax.swing.JOptionPane;

import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;

public class CreateCocoDataManager {

	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext����ErrorKinds
	private static String ORIGINAL_DATA_FILE = "/CompileError.csv"; // ppv����o�͂����csv�t�@�C��
	private static String DATA_FILE = "/CompileErrorLog.csv"; // Coco�p�̃R���p�C���G���[�f�[�^

	private PPProjectSet ppProjectSet;

	private PresVisualizerManager ppvManager;

	public CreateCocoDataManager(IWorkbenchWindow window) {
		// TODO Auto-generated constructor stub
		// WorkSpace�̃p�X���擾����
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// IWorkspaceRoot root = workspace.getRoot();
		// System.out.println(root.getLocation().toFile().getAbsolutePath()
		// .toString());
		ppvManager = new PresVisualizerManager(window);
		createCocoData();
	}

	public void createCocoData() {
		int res = JOptionPane.showConfirmDialog(null,
				"�f�[�^�̍쐬�ɂ͎��Ԃ�������܂����C��낵���ł����H", "�f�[�^�̍쐬",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// CompileError.csv�������I�ɃG�N�X�|�[�g����
		autoExportCompileErrorCSV();

		// �����I�ɃG�N�X�|�[�g�����t�@�C����Coco�p�f�[�^�ɕϊ�����
		convertCompileErrorData();
	}

	private void autoExportCompileErrorCSV() {
		ppvManager.exportAndImportAll();
		PPDataManager ppDataManager = ppvManager.getPPDataManager();

		// TODO: ���C�u�����̏ꏊ
		ppDataManager.setLibDir(new CDirectory(new CPath(PPV_ROOT_DIR))
				.findOrCreateDirectory("ppv.lib"));
		// TODO Hardcoding
		ppProjectSet = ppDataManager.openProjectSet("hoge", true, true, true);
	}

	private void convertCompileErrorData() {
		CCCompileErrorManager manager = new CCCompileErrorManager();

		checkAllFileExist();

		// �G���[�̎�ރf�[�^�����[�h
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

		// CompileError�f�[�^��Coco�p�ɃR���o�[�g
		try {
			CCCompileErrorConverter errorConverter = new CCCompileErrorConverter(
					manager);
			errorConverter.convertData(PPV_ROOT_DIR + ORIGINAL_DATA_FILE,
					PPV_ROOT_DIR + DATA_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkAllFileExist() {
		checkOneFileExist(DATA_FILE);
		checkOneFileExist(ORIGINAL_DATA_FILE);
	}

	private void checkOneFileExist(String filename) {
		CFileSystem.findDirectory(PPV_ROOT_DIR).findOrCreateFile(filename);
	}

	public PPProjectSet getPPProjectSet() {
		return ppProjectSet;
	}
}
