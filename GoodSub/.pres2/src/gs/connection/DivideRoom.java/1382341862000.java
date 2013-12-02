package gs.connection;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

public class DivideRoom {
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
		plus.setMargin(new Insets(null, 0, null, 0));
		frame.add(plus, BorderLayout.NORTH);

		frame.setVisible(true);
	}
}
