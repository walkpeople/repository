package chat_server;

import java.awt.SecondaryLoop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class shell_server {
	private static PrintStream out;
	private static ArrayList<Socket> client_list = new ArrayList<Socket>();

	public static void main(String[] args) {
		boolean flag = true;
		ServerSocket server;
		try {
			server = new ServerSocket(9999);
			while (flag) {
				Socket client = server.accept();
				out = new PrintStream(client.getOutputStream());
				client_list.add(client);
				out.println("在线人数为：" + client_list.size());
				new Thread(new listenThread(client, client_list)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class listenThread implements Runnable {

	private Socket client;
	private ArrayList<Socket> client_list;
	private PrintStream out;

	public listenThread(Socket client, ArrayList<Socket> client_list) {
		super();
		this.client = client;
		this.client_list = client_list;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean flag = true;
		Scanner scan;
		try {
			scan = new Scanner(client.getInputStream(), "UTF-8");
			while (flag) {
				if (scan.hasNext()) {
					String msg = scan.nextLine().trim();
						if (! msg.contains("903ff46c-dcad-4671-b186-a716f357ec78")) {
							for (Socket cl : client_list) {
								PrintStream out = new PrintStream(cl.getOutputStream());
								if (msg.contains("上线")) {
									out.println("在线人数为"+client_list.size());
									out.println(msg);
								}else {
									out.println(msg);
								}
								// out.close();
							}
							System.out.println(msg);
						} else {
							client_list.remove(client);
							String msg_split[] = msg.split("9");
							System.out.println("用户"+msg_split[0] + "退出");
							for(Socket cle : client_list) {
								PrintStream out = new PrintStream(cle.getOutputStream());
									out.println("在线人数为"+client_list.size());
									out.println("用户"+msg_split[0] + "退出");
							}
							scan.close();
							out.close();
							flag = false;

						}
					}
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
