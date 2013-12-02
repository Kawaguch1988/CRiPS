package gs.connection;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DivideRoom implements ActionListener {
	public static void main(String[] args) {
		new DivideRoom().makeFrame();
	}

	private static List<Integer> rooms = new ArrayList<Integer>();

	public int selectRoomNum() throws IOException {
		System.out.print("�����ԍ� : ");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String str = input.readLine();
		int roomNum = Integer.parseInt(str);
		return roomNum;
	}

	public boolean checkRoomNum(int roomNum) {
		for (int number : rooms) {
			if (roomNum == number) {
				// ���������݂�����true
				return true;
			}
		}
		// �����ԍ����Ȃ������烊�X�g�ɉ�����
		rooms.add(roomNum);
		// ���݂��Ȃ�������false
		return false;
	}

	// ���������X�g�̉��Ԗڂɂ��邩�𒲂ׂ�
	public int countRoom(int roomNum) {
		int i = 0;
		for (int number : rooms) {
			if (roomNum == number) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public void makeFrame() {
		JFrame frame = new JFrame("CheCoPro -�W�-");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 300, 500);

		JButton plus = new JButton("+");
		plus.addActionListener(this);
		plus.setActionCommand("plus");
		frame.add(plus, BorderLayout.NORTH);

		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String pushed = e.getActionCommand();
		if (pushed.equals("plus")) {
			System.out.println("plus button pushed");
			makeDialog();
		}
	}

	public void makeDialog() {
		JDialog dialog = new JDialog();
		JLabel label = new JLabel("�O���[�v��");
		JButton okbtn = new JButton("OK");
		JButton canbtn = new JButton("�L�����Z��");
		JPanel gnPanel = new JPanel();
		JPanel btnPanel = new JPanel();
		JTextField tf = new JTextField();

		dialog.setTitle("�V�K�W��̍쐬");
		dialog.setBounds(150, 150, 200, 200);
		gnPanel.add(label);
		gnPanel.add(tf);
		btnPanel.add(okbtn);
		btnPanel.add(canbtn);
		dialog.add(gnPanel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.PAGE_END);
		dialog.setModal(true);
		dialog.setVisible(true);

	}
}
