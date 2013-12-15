package gs.frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginDialog {
	public static void main(String[] args) throws IOException {
		new LoginDialog().openLoginDialog();
	}

	// private static List<Integer> groupNumbers = new ArrayList<Integer>();
	// private int tfnum;
	private int groupNumber;
	private String name;

	public void openLoginDialog() {

		final JDialog dialog = new JDialog();
		final JTextField nameField = new JTextField(10);
		JTextField groupNumber = new JTextField(10);
		JPanel btnPanel = new JPanel();
		JPanel textPanel = new JPanel();
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("�L�����Z��");

		dialog.setTitle("���O�C�����");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setBounds(100, 100, 200, 150);

		textPanel.add(nameField);
		textPanel.add(groupNumber);
		btnPanel.add(okButton);
		btnPanel.add(cancelButton);

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name = nameField.getText();
				dialog.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		dialog.add(textPanel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.PAGE_END);

		dialog.setModal(true);
		dialog.setVisible(true);

	}

	// public int selectRoomNum() throws IOException {
	//
	// makeLoginDialog();
	//
	// return tfnum;
	// }

	public String getName() {
		return name;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	// public boolean checkRoomNum(int roomNum) {
	// for (int number : groupNumbers) {
	// if (roomNum == number) {
	// // ���������݂�����true
	// return true;
	// }
	// }
	// // �����ԍ����Ȃ������烊�X�g�ɉ�����
	// groupNumbers.add(roomNum);
	// // ���݂��Ȃ�������false
	// return false;
	// }
	//
	// // ���������X�g�̉��Ԗڂɂ��邩�𒲂ׂ�
	// public int countRoom(int roomNum) {
	// int i = 0;
	// for (int number : groupNumbers) {
	// if (roomNum == number) {
	// return i;
	// }
	// i++;
	// }
	// return -1;
	// }

	// public String getUserName() {
	// return name;
	// }

}
