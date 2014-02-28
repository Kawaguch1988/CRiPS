package src.coco.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import src.coco.model.CCCompileErrorList;
import src.coco.model.CCCompileErrorManager;

public class CCMainFrame2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String APP_NAME = "CoCo Viewer";
	public static final String VERSION = "0.0.1";

	// Button Size
	private int buttonWidth = 100;
	private int buttonHeight = 100;

	// Dialog size
	private int width = 1120;
	private int height = 740;

	// Compile Error Date
	private CCCompileErrorManager manager;

	// For GUI
	private JPanel rootPanel = new JPanel();
	private ArrayList<CCErrorElementButton2> buttons = new ArrayList<CCErrorElementButton2>();

	public CCMainFrame2(CCCompileErrorManager manager) {
		this.manager = manager;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = d.width * 3 / 4;
		this.height = d.height * 3 / 4;
		this.buttonWidth = this.width / 8;
		this.buttonHeight = this.height / 8;
		initialize();
	}

	private void initialize() {
		// rootPanel �̃��C�A�E�g�����Z�b�g����
		// rootPanel.setLayout(null);
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		rootPanel.setSize(new Dimension(width, height));

		// title�Ȃǂ̐ݒ�
		frameSetting();

		// �S�̂̃R���p�C�����\��
		setCompileErrorNumber();

		// �{�^����z�u����
		setButtonsPanel();

		// ���C�A�E�g�����z�u�ŃR���e���c��ǉ�
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		// TODO: Window�T�C�Y�ύX�ɑΉ��ł���悤�ɂ��邱��
		// this.addWindowListener(new WindowAdapter() {
		// public void windowStateChanged(WindowEvent e) {
		//
		// }
		// });
	}

	private void frameSetting() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(APP_NAME + " " + VERSION);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				for (CCErrorElementButton2 button : buttons) {
					button.closeGraphFrame();
				}
			}
		});
	}

	private void setCompileErrorNumber() {
		JLabel label = new JLabel();
		String string = "���Ȃ��̂���܂ł̑��R���p�C���G���[�� �F " + manager.getTotalErrorCount();
		label.setText(string);
		// CCAchivementButton achivementButton = new CCAchivementButton(manager,
		// label);
		// achivementButton.setBounds(10, 5, 350, 25);
		label.setBounds(10, 5, 350, 25);

		// label �̔w�i��ݒ肷��ꍇ�͔w�i��s�����ɂ��鏈���������邱��
		// label.setBackground(Color.yellow);
		// label.setOpaque(true);
		rootPanel.add(BorderLayout.NORTH, label);
		// rootPanel.add(achivementButton);
	}

	private void setButtonsPanel() {

		// �G���[ID���Ƃ̐��l���������݁A�{�^������������
		for (CCCompileErrorList list : manager.getAllLists()) {
			CCErrorElementButton2 button = new CCErrorElementButton2(list,
					buttonWidth, buttonHeight, manager.getBaseDir(),
					manager.getLibDir(), manager.getPPProjectSet());
			buttons.add(button);
		}

		JPanel buttonsEreaPanel = new JPanel();
		buttonsEreaPanel.setLayout(new GridLayout((height * 15 / 16)
				/ buttonHeight, width / buttonWidth));
		// �{�^����z�u����
		int i = 1;
		int errorkindsCount = 48;
		for (int x = 0; x < Math.sqrt(errorkindsCount); x++) {
			for (int y = 0; y < Math.sqrt(errorkindsCount); y++) {
				if (manager.getAllLists().size() >= i) {
					if (manager.getList(i).getErrors().size() > 0) {
						buttonsEreaPanel.add(buttons.get(i - 1));
					} else {
						buttonsEreaPanel
								.add(setEmptyButton(manager.getList(i)));
					}
					i++;
				} else {
					buttonsEreaPanel.add(setEmptyButton(null));
				}
			}
		}

		JScrollPane scrollPanel = new JScrollPane(buttonsEreaPanel);
		rootPanel.add(scrollPanel, BorderLayout.SOUTH);
	}

	// �N���b�N�ł��Ȃ��{�^�����쐬
	private JButton setEmptyButton(CCCompileErrorList list) {
		JButton emptyButton = new JButton("������");
		emptyButton.setEnabled(false);
		emptyButton.setToolTipText("�������ł�");
		emptyButton.setBackground(Color.GRAY);
		return emptyButton;
	}
}