package gs.serverclient;

import gs.connection.Connection;
import gs.connection.ConnectionPool;
import gs.connection.DivideRoom;
import gs.connection.SendObjectList;
import gs.frame.GSFrame;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private GSFrame frame = new GSFrame();
	private ConnectionPool connectionPool = new ConnectionPool();
	private List<ConnectionPool> pools = new ArrayList<ConnectionPool>();
	private DivideRoom divideRoom = new DivideRoom();

	public void run() {

		// �e�L�X�g�G���A������
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 300, 500);
		frame.setTitle("SyncServer");
		frame.open();

		try (ServerSocket serverSock = new ServerSocket(10000)) {
			while (true) {
				frame.println("waiting new client..");
				final Socket sock = serverSock.accept();
				frame.println("accepted..");
				Thread th = new Thread() {
					public void run() {
						Connection conn = connectionPool.newConnection(sock);
						if (conn != null) {
							frame.println("one connection established.");
							loopForOneClient(conn);
						}
					}
				};
				th.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void loopForOneClient(Connection conn) {
		try {
			Object obj;
			SendObjectList sendList;
			int roomNum = 0;
			while (conn.established()) {
				obj = conn.read();
				sendList = (SendObjectList) obj;
				if (obj instanceof Integer) { // Integer�Ȃ畔���ԍ����X�N���[���o�[��value
					if ((int) obj < 0) { // �}�C�i�X�Ȃ畔���ԍ�
						roomNum = (int) obj;
						// �V���������Ȃ�ConnectionPool��V�K�쐬
						if (!divideRoom.checkRoomNum(roomNum * (-1))) {
							pools.add(new ConnectionPool());
						}
						pools.get(divideRoom.countRoom(roomNum * (-1)))
								.addConnection(conn);
						frame.println("room number is [" + roomNum * (-1) + "]");
					} else if ((int) obj >= 0) { // 0�ȏ�Ȃ�X�N���[���o�[��value
						int value = (int) obj;
						pools.get(divideRoom.countRoom(roomNum * (-1)))
								.broadcast(value, conn);
					}
				} else if (obj instanceof String) { // String�Ȃ瑗�肽��������
					String text = (String) obj;
					pools.get(divideRoom.countRoom(roomNum * (-1))).broadcast(
							text, conn);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connectionPool.close(conn);
			frame.println("one connection killed");
		}
	}
}
