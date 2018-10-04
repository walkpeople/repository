package demo_awt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;

class two_window {

	private JFrame jf;
	private JPanel jp1;
	private JPanel panel;
	private JPanel northpanel;
	private JTextField ip;
	private JTextField port;
	private JButton start;
	private JButton stop;
	private JMenuBar jmb;
	private JMenu record;
	private JMenuItem save;
	private JMenu format;
	private JMenuItem font;
	private JSplitPane centerpanel;
	private JTextArea area1;
	private JTextArea area2;
	private JPanel southpanel;
	private JButton send;
	private JTextField msg_tf;

	private ServerSocket server;
	private boolean isStart = true;
	private ArrayList<Socket> client_list = new ArrayList<Socket>();
	private PrintStream out;
	private Scanner scan;

	public void chatServer() {
		init();
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (ip.getText().trim().equals("") || port.getText().trim().equals("")) {
					area2.append("主机地址或端口输入不能为空" + "\n");
				} else {
					// 可用正则表达式做进一步验证
					area2.append("连接的主机为：" + ip.getText() + " ");
					area2.append("端口号为" + port.getText() + "\n");
				}
				if (isStart) {
					try {
						server = new ServerSocket(Integer.valueOf(port.getText().trim()));
						area2.append("服务已开启" + "\n");
						ServerThread st = new ServerThread(server);
						new Thread(st).start();
						port.setEditable(false);
						ip.setEditable(false);
						isStart = false;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					server.close();
					area2.append("服务已关闭" + "\n");
					port.setEditable(true);
					ip.setEditable(true);
					isStart = true;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (msg_tf.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "输入不能为空");
				}
				String msg_send = msg_tf.getText();
				System.out.println(client_list.size());
				sendMessage(msg_send);
				System.out.println(client_list.size());
				msg_tf.setText("");
			}
		});
	}

	public void init() {
		jf = new JFrame();
		jp1 = new JPanel();
		panel = new JPanel();

		northpanel = new JPanel();
		ip = new JTextField(15);
		port = new JTextField(15);
		start = new JButton("启动");
		stop = new JButton("停止");
		northpanel.setBorder(new TitledBorder("配置信息"));
		northpanel.add(new JLabel("主机地址ַ"));
		northpanel.add(ip);
		northpanel.add(new JLabel("端口号"));
		northpanel.add(port);
		northpanel.add(start);
		northpanel.add(stop);
		jf.setBounds(300, 300, 640, 500);
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setLayout(new BorderLayout());

		jmb = new JMenuBar();
		record = new JMenu("记录");
		save = new JMenuItem("保存");
		format = new JMenu("格式");
		font = new JMenuItem("字体");
		record.add(save);
		format.add(font);
		jmb.add(record);
		jmb.add(format);

		area1 = new JTextArea();
		area1.setEditable(true);
		JScrollPane leftpanel = new JScrollPane(area1);
		leftpanel.setBorder(new TitledBorder("在线人数"));

		area2 = new JTextArea();
		JScrollPane rightpanel = new JScrollPane(area2);
		area2.setEditable(true);
		rightpanel.setBorder(new TitledBorder("聊天信息"));

		centerpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerpanel.setLeftComponent(leftpanel);
		centerpanel.setRightComponent(rightpanel);
		centerpanel.setDividerLocation(100);

		southpanel = new JPanel();
		southpanel.setBorder(new TitledBorder("发消息"));
		msg_tf = new JTextField(45);
		send = new JButton("发送");
		southpanel.add(msg_tf);
		southpanel.add(send);

		jp1.setLayout(new BorderLayout());
		jp1.add(jmb, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout());
		panel.add(northpanel, BorderLayout.NORTH);
		panel.add(centerpanel, BorderLayout.CENTER);
		panel.add(southpanel, BorderLayout.SOUTH);
		jp1.add(panel, BorderLayout.CENTER);
		jf.add(jp1);
		jf.setVisible(true);
	}

	public void sendMessage(String msg) {
		try {
			for (Socket client : client_list) {
				out = new PrintStream(client.getOutputStream());
				out.println(msg);
				System.out.println(client_list.size());
				// out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class ServerThread implements Runnable {
		private ServerSocket server;

		public ServerThread(ServerSocket server) {
			super();
			this.server = server;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					Socket client = server.accept();
					client_list.add(client);
					new Thread(new Listenthread(client)).start();
					System.out.println(client_list.size());
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class Listenthread implements Runnable {

		private Socket client;
		private Scanner scan;

		public Listenthread(Socket client) {
			super();
			this.client = client;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean flag = true;
			while (flag) {
				try {
					scan = new Scanner(client.getInputStream());
					if (scan.hasNext()) {
						String msg = scan.nextLine().trim();
						if (msg.equals("bye")) {
							client_list.remove(client);
							client.close();
							flag = false;
						} else {
							sendMessage(msg);
							area2.append(msg + "\n");
						}
					} else {
						flag = false;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}

public class Swing_one {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		two_window ne = new two_window();
		ne.chatServer();

	}

}
