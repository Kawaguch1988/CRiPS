package gs.serverclient;

import gs.connection.Connection;
import gs.connection.ConnectionPool;
import gs.connection.DivideRoom;
import gs.testframe.TextFrame;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private TextFrame frame = new TextFrame();
	private ConnectionPool connectionPool = new ConnectionPool();
	private List<ConnectionPool> pools = new ArrayList<ConnectionPool>();

	public void run() {

		// テキストエリア初期か
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
			int roomNum;
			while (conn.established()) {
				obj = conn.read();
				if (obj instanceof Integer) { // Integerなら部屋番号かスクロールバーのvalue
					if ((int) obj < 0) { // マイナスなら部屋番号
						roomNum = (int) obj;
						if (!DivideRoom.checkRoomNum(roomNum)) {
							pools.add(roomNum * (-1), new ConnectionPool());
						}
						conn.setRoomNum((int) obj);
						frame.println("room number is [" + conn.getRoomNum()
								* (-1) + "]");
					} else if ((int) obj >= 0) { // 0以上ならスクロールバーのvalue
						int value = (int) obj;
						connectionPool.broadcast(value, conn);
					}
				} else if (obj instanceof String) { // Stringなら送りたい文字列
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
