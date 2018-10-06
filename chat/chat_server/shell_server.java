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
				out.println("在线人数为："+client_list.size());
				new Thread(new listenThread(client,client_list)).start();
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
			scan = new Scanner(client.getInputStream());
			while (flag) {
				if (scan.hasNext()) {
					String msg = scan.nextLine().trim();
					if ("bye".equals(msg)) {
						client_list.remove(client);
						scan.close();
						flag =false;
					} else {
						if(msg.contains("说") || msg.contains("上线")) {
						for(Socket cl:client_list) {
							PrintStream out = new PrintStream(cl.getOutputStream());
							out.println(msg);
							//out.close();
						}
						System.out.println(msg);
						}else {
							System.out.println(msg+"退出");
							client_list.remove(client);
							
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


	

