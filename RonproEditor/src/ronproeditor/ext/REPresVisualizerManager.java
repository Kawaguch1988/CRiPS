package ronproeditor.ext;

import java.util.List;

import javax.swing.JOptionPane;

import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPRonproPPVLoader;
import ronproeditor.REApplication;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFilename;
import clib.common.io.CIOUtils;

// TODO ppDataManager��null�̎�������̂�����ł��ˁD��ŏC��
// TODO ����S���R���p�C���������̂�����ł��ˁD���ƂŏC��
// TODO project���ɒ���PPV�Q�Ƃ������ł��ˁD���ƂŏC��
public class REPresVisualizerManager {

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppv�t�H���_�ɓW�J����
	private static String PPV_TMP_DIR = "tmp";// zip�t�@�C����W�J���邽�߂̈ꎞ�t�H���_ /.ppv��
	private static String PPV_PROJECTSET_NAME = "hoge";// projectset��
	private static IPPVLoader RONPRO_PPV_ROADER = new PPRonproPPVLoader();

	private REApplication application;

	private PPDataManager ppDataManager;

	public REPresVisualizerManager(REApplication application) {
		this.application = application;
	}

	public void openPresVisualizer() {
		// �m�F�_�C�A���O
		int res = JOptionPane.showConfirmDialog(null,
				"�f�[�^�̍쐬�ɂ͎��Ԃ�������܂����C��낵���ł����H", "�f�[�^�̍쐬",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		exportAndImportAll();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		ppDataManager.openProjectSet(PPV_PROJECTSET_NAME, true, true, false);

		// ���Project�𒼐�PPProjectViewerFrame�ŊJ��
		// CDirectory projectSetDir = ppDataManager.getDataDir().findDirectory(
		// PPV_PROJECTSET_NAME);
		// PPProjectSet projectSet = new PPProjectSet(projectSetDir);
		// ppDataManager.loadProjectSet(projectSet, true, false);
		// IPLUnit model = null;
		// model = projectSet.getProjects().get(0).getRootPackage();
		// final PPProjectViewerFrame frame = new PPProjectViewerFrame(model);
		// frame.setBounds(50, 50, 1000, 700);
		// frame.setVisible(true);
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		// frame.fitScale();
		// frame.getTimelinePane().getTimeModel()
		// .setTime(new CTime(2013, 10, 15, 3, 33, 50));
		// }
		// });
	}

	public PPDataManager getPPDataManager() {
		return ppDataManager;
	}

	public void exportAndImportAll() {
		CDirectory ppvRoot = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR);

		// .ppv/ppv.data/cash�ȊO��.ppv�ȉ��̃t�@�C�����폜(cash�͏����̍������̂��߂ɏ����Ȃ�)
		List<CFileElement> elements = ppvRoot.getChildren(CFileFilter
				.IGNORE_BY_NAME_FILTER("ppv.data"));
		elements.add(ppvRoot.findOrCreateDirectory("ppv.data")
				.findOrCreateDirectory("data"));
		for (CFileElement element : elements) {
			boolean deleted = element.delete();
			if (!deleted) {
				throw new RuntimeException(elements.toString() + "���폜�ł��܂���ł����D");
			}
		}

		// CDirectory ppvdataDir = ppvRoot.findOrCreateDirectory("ppv.data");
		// CDirectory dataDir = ppvdataDir.findOrCreateDirectory("data");
		// boolean deleted = dataDir.delete();
		// if (!deleted) {
		// throw new RuntimeException("ppvdata���폜�ł��܂���ł����D");
		// }

		// cash �������Ƃ��������Ԃ�������
		// boolean deleted = ppvRoot.delete();
		// if (!deleted) {
		// throw new RuntimeException("ppvRoot���폜�ł��܂���ł����D");
		// }

		this.ppDataManager = new PPDataManager(ppvRoot);
		CDirectory ppvRootDir = ppDataManager.getBaseDir();
		CDirectory tmpDir = ppvRootDir.findOrCreateDirectory(PPV_TMP_DIR);
		exportAllProjects(tmpDir);
		importAllProjects(PPV_PROJECTSET_NAME, tmpDir);
	}

	private void exportAllProjects(CDirectory tmpDir) {
		List<CDirectory> projects = application.getSourceManager()
				.getAllProjects();

		for (CDirectory project : projects) {
			CDirectory pres = project.findOrCreateDirectory(".pres2");
			if (pres.findFile("pres2.log") != null) {
				exportOneProject(project, tmpDir);
			} else {
				throw new RuntimeException(project.getNameByString()
						+ "�ɂ�����pres2.log��������܂���");
			}
		}
	}

	private void exportOneProject(CDirectory project, CDirectory tmpDir) {
		CFilename projectName = project.getName();
		projectName.setExtension("zip");
		CFile zipfile = tmpDir.findOrCreateFile(projectName);
		CIOUtils.zip(project, zipfile);
	}

	private void importAllProjects(String projectSetName, CDirectory tmpDir) {
		CDirectory projectSetDir = ppDataManager.getDataDir()
				.findOrCreateDirectory(projectSetName);
		List<CFile> zipfiles = tmpDir.getFileChildren();
		for (CFile zipfile : zipfiles) {
			importOneProject(projectSetDir, zipfile);
		}
	}

	private void importOneProject(CDirectory projectSetDir, CFile zipfile) {
		ppDataManager.loadOneFile(zipfile, projectSetDir, RONPRO_PPV_ROADER);
	}

	public void clearCash() {
		// �m�F�_�C�A���O
		int res = JOptionPane.showConfirmDialog(null,
				"Cash�̍폜�ɂ͎��Ԃ�������܂����C��낵���ł����H", "cash�̍폜",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// cash���폜���Ă���i���_�C�����O�𗘗p�������̂ŁCPPDataManager�̊֐����Ă�
		CDirectory ppvRoot = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR);

		this.ppDataManager = new PPDataManager(ppvRoot);
		try {
			ppDataManager.clearCompileCash();
		} catch (Exception ex) {
			throw new RuntimeException("cash���폜�ł��܂���ł����D");
		}

		// boolean deleted = ppvRoot.findOrCreateDirectory("ppv.data")
		// .findOrCreateDirectory("cash").delete();
		// if (!deleted) {
		// throw new RuntimeException("cash���폜�ł��܂���ł����D");
		// }
	}
}
