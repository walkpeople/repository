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

	public void chatServer() {
		init();
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (ip.getText().trim().equals("") || port.getText().trim().equals("")) {
					area2.append("������ַ��˿ں����벻��Ϊ��" + "\n");
				} else {
					// ��������������ʽ����������һ����֤ �Ժ�����
					area2.append("���ӵ�����Ϊ��" + ip.getText() + " ");
					area2.append("�˿ں�Ϊ��" + port.getText() + "\n");
				}
				if (isStart) {
					try {
						server = new ServerSocket(Integer.valueOf(port.getText().trim()));
						area2.append("�������ѿ���" + "\n");
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
					area2.append("�������ѹر�" + "\n");
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
					JOptionPane.showMessageDialog(null, "���벻��Ϊ��");
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
		start = new JButton("����");
		stop = new JButton("ֹͣ");
		northpanel.setBorder(new TitledBorder("������Ϣ"));
		northpanel.add(new JLabel("������ַ"));
		northpanel.add(ip);
		northpanel.add(new JLabel("�˿ں�"));
		northpanel.add(port);
		northpanel.add(start);
		northpanel.add(stop);
		jf.setBounds(300, 300, 640, 500);
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setLayout(new BorderLayout());

		jmb = new JMenuBar();
		record = new JMenu("��¼");
		save = new JMenuItem("����");
		format = new JMenu("��ʽ");
		font = new JMenuItem("����");
		record.add(save);
		format.add(font);
		jmb.add(record);
		jmb.add(format);

		area1 = new JTextArea();
		area1.setEditable(true);
		JScrollPane leftpanel = new JScrollPane(area1);
		leftpanel.setBorder(new TitledBorder("��������"));

		area2 = new JTextArea();
		JScrollPane rightpanel = new JScrollPane(area2);
		area2.setEditable(true);
		rightpanel.setBorder(new TitledBorder("������Ϣ"));

		centerpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerpanel.setLeftComponent(leftpanel);
		centerpanel.setRightComponent(rightpanel);
		centerpanel.setDividerLocation(100);

		southpanel = new JPanel();
		southpanel.setBorder(new TitledBorder("����Ϣ"));
		msg_tf = new JTextField(45);
		send = new JButton("����");
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
				PrintStream out = new PrintStream(client.getOutputStream());
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
