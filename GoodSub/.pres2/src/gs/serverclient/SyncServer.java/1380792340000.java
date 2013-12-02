package gs.serverclient;

import gs.connection.Connection;
import gs.connection.ConnectionPool;
import gs.testframe.TextFrame;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private TextFrame frame;
	private ConnectionPool connectionPool = new ConnectionPool();

	public void run() {

		frame = new TextFrame();

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
			while (conn.established()) {
				obj = conn.read();
				if (obj instanceof Integer) { // Integer�Ȃ畔���ԍ����X�N���[���o�[��value
					if ((int) obj < 0) { // �}�C�i�X�Ȃ畔���ԍ�
						conn.setRoomNum((int) obj);
						frame.println("room number is [" + conn.getRoomNum()
								* (-1) + "]");
					} else if ((int) obj >= 0) { // 0�ȏ�Ȃ�X�N���[���o�[��value
						int value = (int) obj;
						connectionPool.broadcast(value, conn);
					}
				} else if (obj instanceof String) { // String�Ȃ瑗�肽��������
					String text = (String) obj;
					connectionPool.broadcast(text, conn);
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
