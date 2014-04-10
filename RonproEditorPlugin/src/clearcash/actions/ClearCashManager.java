package clearcash.actions;

import javax.swing.JOptionPane;

import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPDataManager;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

// TODO lib�̃t�H���_�ݒ�
// TODO ZIP�̃t�H���_�\��
// TODO �o�b�N�O���E���h����
public class ClearCashManager {

	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();

	private PPDataManager ppDataManager;

	public ClearCashManager(IWorkbenchWindow window) {
		clearCash();
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
		CDirectory ppvRoot = CFileSystem.findDirectory(PPV_ROOT_DIR);

		this.ppDataManager = new PPDataManager(ppvRoot);
		try {
			ppDataManager.clearCompileCash();
		} catch (Exception ex) {
			throw new RuntimeException("cash���폜�ł��܂���ł����D");
		}
	}
}
